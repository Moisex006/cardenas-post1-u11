package com.tienda.pedidos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// DTO de entrada — datos del cliente hacia la API. El cliente solo envía lo
// que le corresponde: descuento, IVA, total, estado y notas los calcula el servidor.
public class PedidoRequestDTO {

    @NotBlank(message = "El email del cliente es obligatorio")
    private String clienteEmail;

    @NotBlank(message = "El tipo de cliente es obligatorio")
    private String tipoCliente;      // REGULAR, VIP o MAYORISTA

    // @NotNull además de @Positive: @Positive considera válido un valor null
    @NotNull(message = "El subtotal es obligatorio")
    @Positive(message = "El subtotal debe ser mayor a cero")
    private Double subtotal;

    public String getClienteEmail() { return clienteEmail; }
    public void setClienteEmail(String clienteEmail) { this.clienteEmail = clienteEmail; }
    public String getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
}
