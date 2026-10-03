package com.example.multas.service;

import com.example.multas.domain.port.PasarelaPagoPort;
import com.example.multas.infrastructure.pago.PagosUdesAdapter;
import com.example.multas.infrastructure.pago.WompiAdapter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/** La pasarela se cambia solo por configuracion (app.pagos.proveedor), sin tocar el servicio. */
class SeleccionPasarelaTest {

    @Nested
    @SpringBootTest
    class PorDefecto {
        @Autowired
        PasarelaPagoPort pasarela;

        @Test
        void usaPagosUdes() {
            assertThat(pasarela).isInstanceOf(PagosUdesAdapter.class);
        }
    }

    @Nested
    @SpringBootTest(properties = "app.pagos.proveedor=wompi")
    class ConWompi {
        @Autowired
        PasarelaPagoPort pasarela;

        @Test
        void usaWompi() {
            assertThat(pasarela).isInstanceOf(WompiAdapter.class);
        }
    }
}
