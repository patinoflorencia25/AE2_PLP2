package com.example;

/**
 * Se lanza cuando se intenta operar con una factura que no tiene ítems.
 */
public class FacturaSinItemsException extends Exception {

    private static final long serialVersionUID = 1L;

    public FacturaSinItemsException(String mensaje) {
        super(mensaje);
    }
}
