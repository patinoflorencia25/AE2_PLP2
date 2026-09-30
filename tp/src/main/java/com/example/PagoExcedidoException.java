package com.example;

/**
 * Se lanza cuando un pago supera el total adeudado de la factura.
 */

public class PagoExcedidoException extends Exception {

    public PagoExcedidoException(String mensaje) {
        super(mensaje);
    }
}
