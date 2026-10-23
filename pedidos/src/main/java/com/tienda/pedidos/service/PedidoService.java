package com.tienda.pedidos.service;

import com.tienda.pedidos.dto.PedidoRequestDTO;
import com.tienda.pedidos.dto.PedidoResponseDTO;
import java.util.List;

// Abstracción de la que depende el controller (DIP): el controller no conoce
// la implementación concreta ni cómo se calcula o persiste un pedido
public interface PedidoService {
    PedidoResponseDTO crear(PedidoRequestDTO dto);
    PedidoResponseDTO buscarPorId(Long id);
    List<PedidoResponseDTO> listarPorCliente(String clienteEmail);
}
