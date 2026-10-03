package com.example.multas.controller;

import com.example.multas.dto.GenerarMultaRequest;
import com.example.multas.dto.MultaResponse;
import com.example.multas.dto.PagoResponse;
import com.example.multas.service.MultaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/multas")
@RequiredArgsConstructor
public class MultaController {

    private final MultaService multaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MultaResponse generar(@Valid @RequestBody GenerarMultaRequest request) {
        return multaService.generarMulta(request);
    }

    @GetMapping
    public List<MultaResponse> listar() {
        return multaService.listarTodas();
    }

    @GetMapping("/estudiante/{estudianteId}")
    public List<MultaResponse> porEstudiante(@PathVariable String estudianteId) {
        return multaService.consultarPorEstudiante(estudianteId);
    }

    @PostMapping("/{id}/pagar-en-ventanilla")
    public PagoResponse pagarEnVentanilla(@PathVariable Long id) {
        return multaService.pagarEnVentanilla(id);
    }

    @PostMapping("/{id}/pagar-en-linea")
    public PagoResponse pagarEnLinea(@PathVariable Long id) {
        return multaService.pagarEnLinea(id);
    }
}
