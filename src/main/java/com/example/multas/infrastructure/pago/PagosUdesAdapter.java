package com.example.multas.infrastructure.pago;

import com.example.multas.domain.ResultadoPago;
import com.example.multas.domain.port.PasarelaPagoPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Adaptador simulado de la pasarela institucional PagosUDES.
 * Opera en pesos y rechaza cobros que superan el cupo institucional por transaccion.
 */
@Component
@ConditionalOnProperty(prefix = "app.pagos", name = "proveedor", havingValue = "pagosudes", matchIfMissing = true)
public class PagosUdesAdapter implements PasarelaPagoPort {

    public static final String PROVEEDOR = "PAGOSUDES";
    public static final BigDecimal CUPO_MAXIMO = new BigDecimal("50000");

    @Override
    public ResultadoPago cobrar(String estudianteId, BigDecimal monto, String referencia) {
        if (monto.compareTo(CUPO_MAXIMO) > 0) {
            return ResultadoPago.rechazado(PROVEEDOR,
                    "El monto $" + monto.toPlainString() + " excede el cupo institucional de $" + CUPO_MAXIMO.toPlainString());
        }
        String ref = "UDES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return ResultadoPago.aprobado(PROVEEDOR, ref,
                "Cobro institucional aplicado al estudiante " + estudianteId + " (" + referencia + ")");
    }
}
