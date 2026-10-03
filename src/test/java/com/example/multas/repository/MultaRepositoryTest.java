package com.example.multas.repository;

import com.example.multas.model.EstadoMulta;
import com.example.multas.model.Multa;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MultaRepositoryTest {

    @Autowired
    private MultaRepository repository;

    @Test
    void cuentaSoloPendientesDelEstudiante() {
        repository.save(new Multa("EST-A", 2));
        repository.save(new Multa("EST-A", 4));
        Multa pagada = new Multa("EST-A", 1);
        pagada.marcarPagada();
        repository.save(pagada);
        repository.save(new Multa("EST-B", 5));

        assertThat(repository.countByEstudianteIdAndEstado("EST-A", EstadoMulta.PENDIENTE)).isEqualTo(2);
        assertThat(repository.countByEstudianteIdAndEstado("EST-A", EstadoMulta.PAGADA)).isEqualTo(1);
        assertThat(repository.findByEstudianteId("EST-A")).hasSize(3);
        assertThat(repository.findByEstudianteId("EST-B")).hasSize(1);
    }
}
