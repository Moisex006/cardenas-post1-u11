package com.tienda.pedidos.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

// Cuerpo uniforme de todas las respuestas de error de la API
@Schema(description = "Respuesta de error de la API")
public class ApiError {

    @Schema(description = "Código de estado HTTP", example = "404")
    private final int status;

    @Schema(description = "Nombre del estado HTTP", example = "Not Found")
    private final String error;

    @Schema(description = "Detalle del error", example = "Pedido con id 999 no encontrado.")
    private final String mensaje;

    @Schema(description = "Fecha y hora del error", example = "2026-10-23T11:45:00")
    private final String timestamp;

    @Schema(description = "Ruta solicitada", example = "/api/pedidos/999")
    private final String path;

    public ApiError(int status, String error, String mensaje, String path) {
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now().withNano(0).toString();
        this.path = path;
    }

    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMensaje() { return mensaje; }
    public String getTimestamp() { return timestamp; }
    public String getPath() { return path; }
}
