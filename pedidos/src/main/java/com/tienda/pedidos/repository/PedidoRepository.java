package com.tienda.pedidos.repository;

import com.tienda.pedidos.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// Reemplaza el SQL concatenado del código de partida: CRUD heredado de
// JpaRepository y consultas derivadas del nombre del método, siempre con parámetros
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteEmail(String clienteEmail);
}
