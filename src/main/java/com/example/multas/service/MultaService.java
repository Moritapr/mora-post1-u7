package com.example.multas.service;

import com.example.multas.domain.PagoRechazadoException;
import com.example.multas.domain.ResultadoPago;
import com.example.multas.domain.port.PasarelaPagoPort;
import com.example.multas.dto.GenerarMultaRequest;
import com.example.multas.dto.MultaResponse;
import com.example.multas.dto.PagoResponse;
import com.example.multas.exception.RecursoNoEncontradoException;
import com.example.multas.model.EstadoMulta;
import com.example.multas.model.Multa;
import com.example.multas.repository.MultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MultaService {

    public static final int TOPE_MULTAS_PENDIENTES = 3;

    private final MultaRepository multaRepository;
    private final PasarelaPagoPort pasarelaPago;

    @Transactional
    public MultaResponse generarMulta(GenerarMultaRequest request) {
        long pendientes = multaRepository.countByEstudianteIdAndEstado(request.estudianteId(), EstadoMulta.PENDIENTE);
        if (pendientes >= TOPE_MULTAS_PENDIENTES) {
            throw new IllegalStateException("El estudiante " + request.estudianteId()
                    + " ya tiene " + pendientes + " multas pendientes (tope " + TOPE_MULTAS_PENDIENTES + ")");
        }
        Multa multa = new Multa(request.estudianteId(), request.diasRetraso());
        return MultaResponse.de(multaRepository.save(multa));
    }

    @Transactional(readOnly = true)
    public List<MultaResponse> listarTodas() {
        return multaRepository.findAll().stream().map(MultaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<MultaResponse> consultarPorEstudiante(String estudianteId) {
        return multaRepository.findByEstudianteId(estudianteId).stream().map(MultaResponse::de).toList();
    }

    /** Pago presencial (efectivo en ventanilla): no pasa por pasarela externa. */
    @Transactional
    public PagoResponse pagarEnVentanilla(Long id) {
        Multa multa = buscar(id);
        multa.marcarPagada();
        return new PagoResponse(MultaResponse.de(multa), "VENTANILLA", null, null,
                "Pago registrado en ventanilla");
    }

    /** Pago en linea: delega el cobro en el puerto, sin conocer el proveedor concreto. */
    @Transactional(noRollbackFor = PagoRechazadoException.class)
    public PagoResponse pagarEnLinea(Long id) {
        Multa multa = buscar(id);
        if (!multa.estaPendiente()) {
            throw new IllegalStateException("La multa " + id + " ya se encuentra pagada");
        }
        ResultadoPago resultado = pasarelaPago.cobrar(multa.getEstudianteId(), multa.getMonto(), "MULTA-" + id);
        if (!resultado.exitoso()) {
            throw new PagoRechazadoException(resultado);
        }
        multa.marcarPagada();
        return new PagoResponse(MultaResponse.de(multa), "EN_LINEA", resultado.proveedor(),
                resultado.referenciaExterna(), resultado.mensaje());
    }

    private Multa buscar(Long id) {
        return multaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la multa con id " + id));
    }
}
