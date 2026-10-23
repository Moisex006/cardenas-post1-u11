package com.tienda.pedidos.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

// Manejo centralizado de errores: reemplaza el try/catch disperso del código de
// partida, que registraba con println y devolvía null (200 OK con cuerpo vacío)
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(RecursoNoEncontradoException ex, HttpServletRequest req) {
        return new ApiError(404, "Not Found", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String errores = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .sorted() // orden estable de los mensajes
            .collect(Collectors.joining("; "));
        log.warn("Peticion rechazada por validacion en {}: {}", req.getRequestURI(), errores);
        return new ApiError(400, "Bad Request", errores, req.getRequestURI());
    }

    // JSON mal formado o con tipos incorrectos (ej. "subtotal":"abc")
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleCuerpoIlegible(HttpMessageNotReadableException ex, HttpServletRequest req) {
        log.warn("Cuerpo de peticion ilegible en {}", req.getRequestURI());
        return new ApiError(400, "Bad Request",
            "El cuerpo de la peticion no es un JSON valido.", req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception ex, HttpServletRequest req) {
        // Las excepciones propias de Spring MVC (ruta inexistente, método no
        // permitido, parámetro faltante) ya traen su código HTTP: se respeta en
        // vez de convertirlas todas en 500
        if (ex instanceof ErrorResponse errorResponse) {
            HttpStatusCode codigo = errorResponse.getStatusCode();
            HttpStatus estado = HttpStatus.valueOf(codigo.value());
            return ResponseEntity.status(codigo).body(new ApiError(codigo.value(),
                estado.getReasonPhrase(), errorResponse.getBody().getDetail(), req.getRequestURI()));
        }
        log.error("Error inesperado en {}", req.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(500,
            "Internal Server Error", "Error inesperado. Contactar soporte.", req.getRequestURI()));
    }
}
