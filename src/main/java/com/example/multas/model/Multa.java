package com.example.multas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Entidad de dominio "rica": conoce sus reglas de calculo y de transicion de estado.
 */
@Entity
@Table(name = "multas")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Multa {

    /** Cargo fijo por cualquier retraso. */
    public static final BigDecimal MONTO_BASE = new BigDecimal("2000");
    /** Tarifa diaria dias 1..7. */
    public static final BigDecimal TARIFA_TRAMO_1 = new BigDecimal("500");
    /** Tarifa diaria dias 8..15. */
    public static final BigDecimal TARIFA_TRAMO_2 = new BigDecimal("1000");
    /** Tarifa diaria desde el dia 16. */
    public static final BigDecimal TARIFA_TRAMO_3 = new BigDecimal("2000");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String estudianteId;

    @Column(nullable = false)
    private int diasRetraso;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMulta estado;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    public Multa(String estudianteId, int diasRetraso) {
        if (estudianteId == null || estudianteId.isBlank()) {
            throw new IllegalArgumentException("El estudianteId es obligatorio");
        }
        if (diasRetraso < 1) {
            throw new IllegalArgumentException("Los dias de retraso deben ser mayores a 0");
        }
        this.estudianteId = estudianteId;
        this.diasRetraso = diasRetraso;
        this.estado = EstadoMulta.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
        this.monto = calcularMonto().setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Monto base + tarifa progresiva por dia:
     * dias 1-7 a 500, dias 8-15 a 1.000 y desde el dia 16 a 2.000.
     */
    public BigDecimal calcularMonto() {
        int tramo1 = Math.min(diasRetraso, 7);
        int tramo2 = Math.max(0, Math.min(diasRetraso, 15) - 7);
        int tramo3 = Math.max(0, diasRetraso - 15);
        return MONTO_BASE
                .add(TARIFA_TRAMO_1.multiply(BigDecimal.valueOf(tramo1)))
                .add(TARIFA_TRAMO_2.multiply(BigDecimal.valueOf(tramo2)))
                .add(TARIFA_TRAMO_3.multiply(BigDecimal.valueOf(tramo3)));
    }

    public boolean estaPendiente() {
        return estado == EstadoMulta.PENDIENTE;
    }

    public void marcarPagada() {
        if (!estaPendiente()) {
            throw new IllegalStateException("La multa " + id + " ya se encuentra pagada");
        }
        this.estado = EstadoMulta.PAGADA;
    }
}
