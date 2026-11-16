package com.example.dominio.excepcion;

/**
 * Se lanza cuando un cliente intenta una compra que supera su límite de crédito.
 */
public class LimiteCreditoExcedidoException extends Exception {

    private static final long serialVersionUID = 1L;

    public LimiteCreditoExcedidoException(String mensaje) {
        super(mensaje);
    }
}
