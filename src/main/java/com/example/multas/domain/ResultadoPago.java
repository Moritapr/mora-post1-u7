package com.example.multas.domain;

public record ResultadoPago(String proveedor, boolean exitoso, String referenciaExterna, String mensaje) {

    public static ResultadoPago aprobado(String proveedor, String referenciaExterna, String mensaje) {
        return new ResultadoPago(proveedor, true, referenciaExterna, mensaje);
    }

    public static ResultadoPago rechazado(String proveedor, String mensaje) {
        return new ResultadoPago(proveedor, false, null, mensaje);
    }
}
