package com.example.multas.infrastructure.pago;

import com.example.multas.domain.ResultadoPago;
import com.example.multas.domain.port.PasarelaPagoPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/** Ambos adaptadores cumplen el mismo contrato del puerto: son intercambiables. */
class PasarelasPagoTest {

    static Stream<Arguments> pasarelas() {
        return Stream.of(
                Arguments.of(new PagosUdesAdapter(), PagosUdesAdapter.PROVEEDOR),
                Arguments.of(new WompiAdapter(), WompiAdapter.PROVEEDOR));
    }

    @ParameterizedTest(name = "{1} aprueba monto dentro del limite")
    @MethodSource("pasarelas")
    void apruebaMontoDentroDelLimite(PasarelaPagoPort pasarela, String proveedor) {
        ResultadoPago r = pasarela.cobrar("EST-001", new BigDecimal("8500.00"), "MULTA-1");

        assertThat(r.exitoso()).isTrue();
        assertThat(r.proveedor()).isEqualTo(proveedor);
        assertThat(r.referenciaExterna()).isNotBlank();
    }

    @ParameterizedTest(name = "{1} rechaza monto sobre el limite")
    @MethodSource("pasarelas")
    void rechazaMontoSobreElLimite(PasarelaPagoPort pasarela, String proveedor) {
        ResultadoPago r = pasarela.cobrar("EST-001", new BigDecimal("63500.00"), "MULTA-2");

        assertThat(r.exitoso()).isFalse();
        assertThat(r.proveedor()).isEqualTo(proveedor);
        assertThat(r.referenciaExterna()).isNull();
        assertThat(r.mensaje()).isNotBlank();
    }

    @Test
    void wompiTraduceMontoACentavos() {
        assertThat(WompiAdapter.aCentavos(new BigDecimal("8500.00"))).isEqualTo(850_000L);

        ResultadoPago r = new WompiAdapter().cobrar("EST-001", new BigDecimal("8500"), "MULTA-3");
        assertThat(r.mensaje()).contains("amount_in_cents 850000");
    }
}
