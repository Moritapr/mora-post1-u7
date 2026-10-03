package com.example.multas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GenerarMultaRequest(
        @NotBlank(message = "El estudianteId es obligatorio")
        String estudianteId,

        @NotNull(message = "Los dias de retraso son obligatorios")
        @Min(value = 1, message = "Los dias de retraso deben ser al menos 1")
        @Max(value = 365, message = "Los dias de retraso no pueden superar 365")
        Integer diasRetraso) {
}
