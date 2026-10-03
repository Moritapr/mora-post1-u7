package com.example.multas.domain.port;

import com.example.multas.domain.ResultadoPago;

import java.math.BigDecimal;

/**
 * Puerto de salida del dominio hacia cualquier pasarela de pago.
 * Java puro: el dominio no depende de Spring ni de ningun proveedor concreto.
 */
public interface PasarelaPagoPort {

    /**
     * @param estudianteId estudiante que paga
     * @param monto        monto en pesos (COP)
     * @param referencia   referencia interna del cobro (ej. "MULTA-15")
     */
    ResultadoPago cobrar(String estudianteId, BigDecimal monto, String referencia);
}
