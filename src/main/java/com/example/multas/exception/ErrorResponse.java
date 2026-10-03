package com.example.multas.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(LocalDateTime timestamp, int status, String error, String mensaje,
                            Map<String, String> detalles) {

    public static ErrorResponse of(int status, String error, String mensaje) {
        return new ErrorResponse(LocalDateTime.now(), status, error, mensaje, Map.of());
    }
}
