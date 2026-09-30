package com.example;

/**
 * Se lanza cuando se intenta operar con una factura que no tiene items.
 */

public class FacturaSinItemsException extends Exception {

    public FacturaSinItemsException(String mensaje) {
        super(mensaje);
    }
}