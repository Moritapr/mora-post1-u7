package com.example.multas.dto;

import com.example.multas.model.EstadoMulta;
import com.example.multas.model.Multa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MultaResponse(Long id, String estudianteId, int diasRetraso, BigDecimal monto,
                            EstadoMulta estado, LocalDateTime fechaCreacion) {

    public static MultaResponse de(Multa multa) {
        return new MultaResponse(multa.getId(), multa.getEstudianteId(), multa.getDiasRetraso(),
                multa.getMonto(), multa.getEstado(), multa.getFechaCreacion());
    }
}
