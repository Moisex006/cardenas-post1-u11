package com.tienda.pedidos.strategy;

import org.springframework.stereotype.Component;

// El nombre del bean ("REGULAR") es la llave con la que Spring lo registra
// en el Map<String, EstrategiaDescuento> que recibe PedidoServiceImpl
@Component("REGULAR")
public class DescuentoRegular implements EstrategiaDescuento {

    @Override
    public double calcularDescuento(double subtotal) {
        return 0.0;
    }
}
