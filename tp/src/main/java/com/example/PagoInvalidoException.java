package com.example;

/**
 * Se lanza cuando se intenta registrar un pago con un monto que no tiene sentido
 * (cero o negativo). Es distinta de PagoExcedidoException, que cubre el pago
 * mayor al total adeudado.
 */
public class PagoInvalidoException extends Exception {

    private static final long serialVersionUID = 1L;

    public PagoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
