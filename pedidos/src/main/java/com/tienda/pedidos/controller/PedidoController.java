package com.tienda.pedidos.controller;

import com.tienda.pedidos.dto.PedidoRequestDTO;
import com.tienda.pedidos.dto.PedidoResponseDTO;
import com.tienda.pedidos.entity.Pedido;
import com.tienda.pedidos.repository.PedidoRepository;
import com.tienda.pedidos.service.NotificacionEmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

// Paso intermedio de la refactorización: el SQL embebido ya no existe (se usa
// PedidoRepository) y la API recibe/devuelve DTOs. La lógica de negocio todavía
// vive aquí; se mueve a PedidoService en el siguiente paso.
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoRepository repo;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponseDTO crearPedido(@Valid @RequestBody PedidoRequestDTO dto) {
        double descuento;
        if ("VIP".equalsIgnoreCase(dto.getTipoCliente())) {
            descuento = dto.getSubtotal() * 0.15;
        } else if ("MAYORISTA".equalsIgnoreCase(dto.getTipoCliente())) {
            descuento = dto.getSubtotal() * 0.25;
        } else {
            descuento = 0.0;
        }
        double baseConDescuento = dto.getSubtotal() - descuento;
        double iva = baseConDescuento * 0.19;
        double total = baseConDescuento + iva;

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

        NotificacionEmailService notificador = new NotificacionEmailService();
        notificador.enviar(guardado.getClienteEmail(), "Tu pedido fue creado. Total: " + total);

        return toResponseDTO(guardado);
    }

    @GetMapping("/{id}")
    public PedidoResponseDTO obtenerPedido(@PathVariable Long id) {
        return repo.findById(id).map(this::toResponseDTO).orElse(null);
    }

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
