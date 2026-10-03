package com.example.multas.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Mismo endpoint, otra pasarela: solo cambia la propiedad app.pagos.proveedor. */
@SpringBootTest(properties = "app.pagos.proveedor=wompi")
@AutoConfigureMockMvc
class MultaControllerWompiTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void pagarEnLineaUsaWompi() throws Exception {
        long id = crear("EST-WOMPI-1", 10);

        mvc.perform(post("/api/multas/{id}/pagar-en-linea", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.proveedor").value("WOMPI"))
                .andExpect(jsonPath("$.mensaje", containsString("amount_in_cents 850000")))
                .andExpect(jsonPath("$.multa.estado").value("PAGADA"));
    }

    @Test
    void cobroRechazadoPorWompiDevuelve402() throws Exception {
        long id = crear("EST-WOMPI-2", 40);

        mvc.perform(post("/api/multas/{id}/pagar-en-linea", id))
                .andExpect(status().isPaymentRequired())
                .andExpect(jsonPath("$.mensaje", containsString("WOMPI")));
    }

    private long crear(String estudianteId, int dias) throws Exception {
        String json = mvc.perform(post("/api/multas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estudianteId\":\"" + estudianteId + "\",\"diasRetraso\":" + dias + "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return Long.parseLong(json.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }
}
