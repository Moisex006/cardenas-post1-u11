package com.tienda.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

// DTO de salida — nunca incluye notasInternas (uso exclusivo de logística)
@Schema(description = "Pedido devuelto por la API")
public class PedidoResponseDTO {

    @Schema(description = "Identificador del pedido", example = "1")
    private Long id;

    @Schema(description = "Correo del cliente", example = "ana@correo.com")
    private String clienteEmail;

    @Schema(description = "Tipo de cliente", example = "VIP")
    private String tipoCliente;

    @Schema(description = "Subtotal antes de descuento e IVA", example = "100000.0")
    private Double subtotal;

    @Schema(description = "Descuento aplicado segun el tipo de cliente", example = "15000.0")
    private Double descuento;

    @Schema(description = "IVA del 19% sobre el subtotal con descuento", example = "16150.0")
    private Double iva;

    @Schema(description = "Total a pagar", example = "101150.0")
    private Double total;

    @Schema(description = "Estado del pedido", example = "CREADO")
    private String estado;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getClienteEmail() { return clienteEmail; }
    public void setClienteEmail(String clienteEmail) { this.clienteEmail = clienteEmail; }
    public String getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
    public Double getDescuento() { return descuento; }
    public void setDescuento(Double descuento) { this.descuento = descuento; }
    public Double getIva() { return iva; }
    public void setIva(Double iva) { this.iva = iva; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
