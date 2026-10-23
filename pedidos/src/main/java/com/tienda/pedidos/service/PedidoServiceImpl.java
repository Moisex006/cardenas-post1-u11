package com.tienda.pedidos.service;

import com.tienda.pedidos.dto.PedidoRequestDTO;
import com.tienda.pedidos.dto.PedidoResponseDTO;
import com.tienda.pedidos.entity.Pedido;
import com.tienda.pedidos.exception.RecursoNoEncontradoException;
import com.tienda.pedidos.repository.PedidoRepository;
import com.tienda.pedidos.strategy.EstrategiaDescuento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// SRP: orquesta el caso de uso (cálculo, persistencia y notificación); la
// validación de entrada queda en el DTO y el acceso a datos en el repositorio.
// DIP: todas sus dependencias llegan por constructor como abstracciones.
@Service
public class PedidoServiceImpl implements PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoServiceImpl.class);
    private static final double IVA = 0.19;

    private final PedidoRepository repo;
    private final NotificacionService notificador;
    private final Map<String, EstrategiaDescuento> estrategias;

    // Spring puebla el mapa con todos los beans EstrategiaDescuento, usando el
    // nombre de cada bean (REGULAR, VIP, MAYORISTA) como llave
    public PedidoServiceImpl(PedidoRepository repo,
                             NotificacionService notificador,
                             Map<String, EstrategiaDescuento> estrategias) {
        this.repo = repo;
        this.notificador = notificador;
        this.estrategias = estrategias;
    }

    // Los mensajes de log solo llevan identificadores y montos: nunca notasInternas
    @Override
    public PedidoResponseDTO crear(PedidoRequestDTO dto) {
        log.info("Creando pedido: clienteEmail={}, tipoCliente={}",
                 dto.getClienteEmail(), dto.getTipoCliente());
        String tipo = dto.getTipoCliente().toUpperCase();
        if (!estrategias.containsKey(tipo)) {
            log.warn("Tipo de cliente desconocido tipoCliente={}, se aplica el descuento REGULAR", tipo);
        }
        EstrategiaDescuento estrategia = estrategias.getOrDefault(tipo, estrategias.get("REGULAR"));
        double descuento = redondear(estrategia.calcularDescuento(dto.getSubtotal()));
        // Único punto de auditoría del descuento (no un log en cada Strategy)
        log.info("Descuento aplicado: pedido para clienteEmail={}, tipoCliente={}, descuento={}",
                 dto.getClienteEmail(), tipo, descuento);
        double baseConDescuento = dto.getSubtotal() - descuento;
        double iva = redondear(baseConDescuento * IVA);
        double total = redondear(baseConDescuento + iva);

        Pedido pedido = new Pedido();
        pedido.setClienteEmail(dto.getClienteEmail());
        pedido.setTipoCliente(tipo);
        pedido.setSubtotal(dto.getSubtotal());
        pedido.setDescuento(descuento);
        pedido.setIva(iva);
        pedido.setTotal(total);
        pedido.setEstado("CREADO");
        pedido.setNotasInternas("Pedido creado automaticamente, revisar stock antes de despachar");
        pedido.setFechaCreacion(LocalDateTime.now());

        Pedido guardado = repo.save(pedido);
        log.info("Pedido creado exitosamente con id={}, total={}", guardado.getId(), guardado.getTotal());
        try {
            notificador.enviarConfirmacion(guardado.getClienteEmail(), guardado.getTotal());
        } catch (RuntimeException ex) {
            // El pedido ya quedó guardado: un fallo del correo no debe anularlo,
            // pero se registra como ERROR para que soporte lo reenvíe
            log.error("No se pudo enviar la confirmacion del pedido id={}", guardado.getId(), ex);
        }
        return toResponseDTO(guardado);
    }

    @Override
    public PedidoResponseDTO buscarPorId(Long id) {
        log.debug("Buscando pedido con id={}", id);
        Pedido p = repo.findById(id).orElseThrow(() -> {
            log.warn("Pedido con id={} no encontrado", id);
            return new RecursoNoEncontradoException("Pedido", id);
        });
        return toResponseDTO(p);
    }

    @Override
    public List<PedidoResponseDTO> listarPorCliente(String clienteEmail) {
        List<PedidoResponseDTO> pedidos = repo.findByClienteEmail(clienteEmail).stream()
            .map(this::toResponseDTO).toList();
        log.debug("Consulta de pedidos: clienteEmail={}, encontrados={}", clienteEmail, pedidos.size());
        return pedidos;
    }

    // Montos en pesos con 2 decimales: evita arrastrar errores de punto flotante
    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    // Mapeo 1:1 entidad -> DTO; notasInternas y fechaCreacion no se copian
    private PedidoResponseDTO toResponseDTO(Pedido p) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(p.getId());
        dto.setClienteEmail(p.getClienteEmail());
        dto.setTipoCliente(p.getTipoCliente());
        dto.setSubtotal(p.getSubtotal());
        dto.setDescuento(p.getDescuento());
        dto.setIva(p.getIva());
        dto.setTotal(p.getTotal());
        dto.setEstado(p.getEstado());
        return dto;
    }
}
