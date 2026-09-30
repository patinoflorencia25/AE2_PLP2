package com.example;

/**
 * Se lanza cuando un cliente intenta una compra que supera su limite de credito.
 */

public class LimiteCreditoExcedidoException extends Exception {

    public LimiteCreditoExcedidoException(String mensaje) {
        super(mensaje);
    }
}