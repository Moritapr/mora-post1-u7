package com.example.multas.infrastructure.pago;

import com.example.multas.domain.ResultadoPago;
import com.example.multas.domain.port.PasarelaPagoPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Adaptador simulado de Wompi. Su API trabaja en centavos (amount_in_cents),
 * por lo que el adaptador traduce el monto del dominio antes de "cobrar".
 */
@Component
@ConditionalOnProperty(prefix = "app.pagos", name = "proveedor", havingValue = "wompi")
public class WompiAdapter implements PasarelaPagoPort {

    public static final String PROVEEDOR = "WOMPI";
    public static final long LIMITE_CENTAVOS = 5_000_000L;

    @Override
    public ResultadoPago cobrar(String estudianteId, BigDecimal monto, String referencia) {
        long centavos = aCentavos(monto);
        if (centavos > LIMITE_CENTAVOS) {
            return ResultadoPago.rechazado(PROVEEDOR,
                    "DECLINED: amount_in_cents " + centavos + " supera el limite de " + LIMITE_CENTAVOS);
        }
        String ref = "WMP-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        return ResultadoPago.aprobado(PROVEEDOR, ref,
                "APPROVED: amount_in_cents " + centavos + " (" + referencia + ")");
    }

    static long aCentavos(BigDecimal monto) {
        return monto.movePointRight(2).longValueExact();
    }
}
