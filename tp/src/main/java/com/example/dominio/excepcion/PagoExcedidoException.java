package com.example.dominio.excepcion;

/**
 * Se lanza cuando un pago supera el total adeudado de la factura.
 */
public class PagoExcedidoException extends Exception {

    private static final long serialVersionUID = 1L;

    public PagoExcedidoException(String mensaje) {
        super(mensaje);
    }
}
