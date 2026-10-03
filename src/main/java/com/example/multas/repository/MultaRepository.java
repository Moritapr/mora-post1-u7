package com.example.multas.repository;

import com.example.multas.model.EstadoMulta;
import com.example.multas.model.Multa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MultaRepository extends JpaRepository<Multa, Long> {

    List<Multa> findByEstudianteId(String estudianteId);

    /** Se resuelve con un SELECT COUNT(...) en la base de datos, sin cargar entidades. */
    long countByEstudianteIdAndEstado(String estudianteId, EstadoMulta estado);
}
