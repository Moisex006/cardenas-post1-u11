package com.tienda.pedidos.service;

import com.tienda.pedidos.dto.PedidoRequestDTO;
import com.tienda.pedidos.dto.PedidoResponseDTO;
import com.tienda.pedidos.entity.Pedido;
import com.tienda.pedidos.exception.RecursoNoEncontradoException;
import com.tienda.pedidos.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

// SRP: orquesta el caso de uso (cálculo, persistencia y notificación); la
// validación de entrada queda en el DTO y el acceso a datos en el repositorio.
// DIP: todas sus dependencias llegan por constructor como abstracciones.
@Service
public class PedidoServiceImpl implements PedidoService {

    private static final double IVA = 0.19;

    private final PedidoRepository repo;
    private final NotificacionService notificador;

    public PedidoServiceImpl(PedidoRepository repo, NotificacionService notificador) {
        this.repo = repo;
        this.notificador = notificador;
    }

    @Override
    public PedidoResponseDTO crear(PedidoRequestDTO dto) {
        double descuento = redondear(calcularDescuento(dto.getTipoCliente(), dto.getSubtotal()));
        double baseConDescuento = dto.getSubtotal() - descuento;
        double iva = redondear(baseConDescuento * IVA);
        double total = redondear(baseConDescuento + iva);

        Pedido pedido = new Pedido();
        pedido.setClienteEmail(dto.getClienteEmail());
        pedido.setTipoCliente(dto.getTipoCliente().toUpperCase());
        pedido.setSubtotal(dto.getSubtotal());
        pedido.setDescuento(descuento);
        pedido.setIva(iva);
        pedido.setTotal(total);
        pedido.setEstado("CREADO");
        pedido.setNotasInternas("Pedido creado automaticamente, revisar stock antes de despachar");
        pedido.setFechaCreacion(LocalDateTime.now());

        Pedido guardado = repo.save(pedido);
        notificador.enviarConfirmacion(guardado.getClienteEmail(), guardado.getTotal());
        return toResponseDTO(guardado);
    }

    @Override
    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido p = repo.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Pedido", id));
        return toResponseDTO(p);
    }

    @Override
    public List<PedidoResponseDTO> listarPorCliente(String clienteEmail) {
        return repo.findByClienteEmail(clienteEmail).stream().map(this::toResponseDTO).toList();
    }

    // Lógica trasladada desde el controller; en el siguiente paso se reemplaza por Strategy
    private double calcularDescuento(String tipoCliente, double subtotal) {
        if ("VIP".equalsIgnoreCase(tipoCliente)) {
            return subtotal * 0.15;
        } else if ("MAYORISTA".equalsIgnoreCase(tipoCliente)) {
            return subtotal * 0.25;
        }
        return 0.0;
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
