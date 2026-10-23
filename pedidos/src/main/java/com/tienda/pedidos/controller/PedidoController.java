package com.tienda.pedidos.controller;

import com.tienda.pedidos.dto.PedidoRequestDTO;
import com.tienda.pedidos.dto.PedidoResponseDTO;
import com.tienda.pedidos.exception.ApiError;
import com.tienda.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// Solo traduce HTTP <-> servicio: sin lógica de negocio, sin SQL y sin try/catch
@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Creacion y consulta de pedidos")
public class PedidoController {

    // Ejemplos reales de ApiError mostrados en Swagger UI para cada respuesta de error
    private static final String EJEMPLO_400 = "{\"status\":400,\"error\":\"Bad Request\","
        + "\"mensaje\":\"subtotal: El subtotal debe ser mayor a cero\","
        + "\"timestamp\":\"2026-10-23T11:47:09\",\"path\":\"/api/pedidos\"}";
    private static final String EJEMPLO_404 = "{\"status\":404,\"error\":\"Not Found\","
        + "\"mensaje\":\"Pedido con id 999 no encontrado.\","
        + "\"timestamp\":\"2026-10-23T11:47:09\",\"path\":\"/api/pedidos/999\"}";
    private static final String EJEMPLO_400_PARAMETRO = "{\"status\":400,\"error\":\"Bad Request\","
        + "\"mensaje\":\"Required parameter 'clienteEmail' is not present.\","
        + "\"timestamp\":\"2026-10-23T11:47:10\",\"path\":\"/api/pedidos\"}";

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @Operation(summary = "Crear un nuevo pedido",
               description = "Calcula el descuento segun tipoCliente (REGULAR 0%, VIP 15%, MAYORISTA 25%), "
                           + "aplica IVA del 19% sobre la base con descuento y guarda el pedido")
    @ApiResponse(responseCode = "201", description = "Pedido creado, con descuento aplicado segun tipoCliente",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PedidoResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = EJEMPLO_400)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponseDTO crear(@Valid @RequestBody PedidoRequestDTO dto) {
        return service.crear(dto);
    }

    @Operation(summary = "Obtener pedido por ID")
    @ApiResponse(responseCode = "200", description = "Pedido encontrado",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PedidoResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = EJEMPLO_404)))
    @GetMapping("/{id}")
    public PedidoResponseDTO obtener(
            @Parameter(description = "ID del pedido", example = "1")
            @PathVariable Long id) {
        return service.buscarPorId(id); // sin try/catch: la excepcion la atiende GlobalExceptionHandler
    }

    @Operation(summary = "Listar los pedidos de un cliente")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos del cliente (vacia si no tiene pedidos)",
                 content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PedidoResponseDTO.class))))
    @ApiResponse(responseCode = "400", description = "Falta el parametro clienteEmail",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = EJEMPLO_400_PARAMETRO)))
    @GetMapping
    public List<PedidoResponseDTO> listarPorCliente(
            @Parameter(description = "Correo del cliente", example = "ana@correo.com")
            @RequestParam String clienteEmail) {
        return service.listarPorCliente(clienteEmail);
    }
}
