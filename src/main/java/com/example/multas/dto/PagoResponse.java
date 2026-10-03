package com.example.multas.dto;

public record PagoResponse(MultaResponse multa, String canal, String proveedor,
                           String referenciaExterna, String mensaje) {
}
