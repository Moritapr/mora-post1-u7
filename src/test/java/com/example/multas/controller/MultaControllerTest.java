package com.example.multas.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MultaControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void generarMultaDevuelve201ConMontoCalculado() throws Exception {
        String est = nuevoEstudiante();
        crear(est, 10)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.estudianteId").value(est))
                .andExpect(jsonPath("$.monto").value(8500.00))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.fechaCreacion").exists());
    }

    @Test
    void generarMultaInvalidaDevuelve400() throws Exception {
        mvc.perform(post("/api/multas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estudianteId\":\"\",\"diasRetraso\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.estudianteId").exists())
                .andExpect(jsonPath("$.detalles.diasRetraso").exists());
    }

    @Test
    void cuartaMultaPendienteDevuelve409() throws Exception {
        String est = nuevoEstudiante();
        for (int i = 0; i < 3; i++) {
            crear(est, 2).andExpect(status().isCreated());
        }
        crear(est, 2)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje", containsString("tope 3")));
    }

    @Test
    void pagarLiberaCupoParaNuevaMulta() throws Exception {
        String est = nuevoEstudiante();
        long id = idDe(crear(est, 2));
        crear(est, 2);
        crear(est, 2);

        mvc.perform(post("/api/multas/{id}/pagar-en-ventanilla", id)).andExpect(status().isOk());

        crear(est, 2).andExpect(status().isCreated());
    }

    @Test
    void listarYConsultarPorEstudiante() throws Exception {
        String est = nuevoEstudiante();
        crear(est, 1);
        crear(est, 3);

        mvc.perform(get("/api/multas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
        mvc.perform(get("/api/multas/estudiante/{est}", est))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].estudianteId").value(est));
    }

    @Test
    void pagarEnVentanillaYDoblePagoDevuelve409() throws Exception {
        long id = idDe(crear(nuevoEstudiante(), 5));

        mvc.perform(post("/api/multas/{id}/pagar-en-ventanilla", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canal").value("VENTANILLA"))
                .andExpect(jsonPath("$.multa.estado").value("PAGADA"));
        mvc.perform(post("/api/multas/{id}/pagar-en-ventanilla", id))
                .andExpect(status().isConflict());
        mvc.perform(post("/api/multas/{id}/pagar-en-linea", id))
                .andExpect(status().isConflict());
    }

    @Test
    void pagarEnLineaConPagosUdes() throws Exception {
        long id = idDe(crear(nuevoEstudiante(), 10));

        mvc.perform(post("/api/multas/{id}/pagar-en-linea", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canal").value("EN_LINEA"))
                .andExpect(jsonPath("$.proveedor").value("PAGOSUDES"))
                .andExpect(jsonPath("$.referenciaExterna", containsString("UDES-")))
                .andExpect(jsonPath("$.multa.estado").value("PAGADA"));
    }

    @Test
    void cobroRechazadoDevuelve402YMultaSiguePendiente() throws Exception {
        String est = nuevoEstudiante();
        long id = idDe(crear(est, 40)); // $63.500 > cupo de $50.000

        mvc.perform(post("/api/multas/{id}/pagar-en-linea", id))
                .andExpect(status().isPaymentRequired())
                .andExpect(jsonPath("$.mensaje", containsString("PAGOSUDES")));
        mvc.perform(get("/api/multas/estudiante/{est}", est))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    void multaInexistenteDevuelve404() throws Exception {
        mvc.perform(post("/api/multas/{id}/pagar-en-linea", 999_999))
                .andExpect(status().isNotFound());
        mvc.perform(post("/api/multas/{id}/pagar-en-ventanilla", 999_999))
                .andExpect(status().isNotFound());
    }

    private ResultActions crear(String estudianteId, int dias) throws Exception {
        return mvc.perform(post("/api/multas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"estudianteId\":\"" + estudianteId + "\",\"diasRetraso\":" + dias + "}"));
    }

    private long idDe(ResultActions resultado) throws Exception {
        String json = resultado.andReturn().getResponse().getContentAsString();
        return Long.parseLong(json.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    private static String nuevoEstudiante() {
        return "EST-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
