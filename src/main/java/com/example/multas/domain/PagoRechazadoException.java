package com.example.multas.domain;

public class PagoRechazadoException extends RuntimeException {

    private final ResultadoPago resultado;

    public PagoRechazadoException(ResultadoPago resultado) {
        super("Pago rechazado por " + resultado.proveedor() + ": " + resultado.mensaje());
        this.resultado = resultado;
    }

    public ResultadoPago getResultado() {
        return resultado;
    }
}
