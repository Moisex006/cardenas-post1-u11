package com.tienda.pedidos.strategy;

// Strategy: cada tipo de cliente aporta su propia regla de descuento. Un tipo
// nuevo (ej. CORPORATIVO) se agrega con una clase @Component("CORPORATIVO")
// sin modificar PedidoServiceImpl (OCP).
public interface EstrategiaDescuento {
    double calcularDescuento(double subtotal);
}
