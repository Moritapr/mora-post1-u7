package com.example.multas.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MultaTest {

    @ParameterizedTest(name = "{0} dias -> ${1}")
    @CsvSource({
            "1, 2500",    // 2000 + 1*500
            "7, 5500",    // 2000 + 7*500
            "8, 6500",    // 5500 + 1*1000
            "15, 13500",  // 5500 + 8*1000
            "16, 15500",  // 13500 + 1*2000
            "20, 23500",  // 13500 + 5*2000
            "40, 63500"   // 13500 + 25*2000
    })
    void calcularMontoAplicaBaseMasTarifaProgresiva(int dias, String esperado) {
        Multa multa = new Multa("EST-001", dias);

        assertThat(multa.calcularMonto()).isEqualByComparingTo(new BigDecimal(esperado));
        assertThat(multa.getMonto()).isEqualByComparingTo(new BigDecimal(esperado));
    }

    @Test
    void nuevaMultaNacePendienteConFecha() {
        Multa multa = new Multa("EST-001", 3);

        assertThat(multa.getEstado()).isEqualTo(EstadoMulta.PENDIENTE);
        assertThat(multa.getFechaCreacion()).isNotNull();
        assertThat(multa.estaPendiente()).isTrue();
    }

    @Test
    void marcarPagadaCambiaEstadoYNoPermiteDoblePago() {
        Multa multa = new Multa("EST-001", 3);

        multa.marcarPagada();

        assertThat(multa.getEstado()).isEqualTo(EstadoMulta.PAGADA);
        assertThatThrownBy(multa::marcarPagada).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void rechazaDatosInvalidos() {
        assertThatThrownBy(() -> new Multa(" ", 3)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Multa("EST-001", 0)).isInstanceOf(IllegalArgumentException.class);
    }
}
