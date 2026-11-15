package com.example;

/**
 * Se lanza cuando se intenta agregar una factura a un historial que ya no tiene lugar.
 * Reemplaza al mensaje por consola que antes imprimía Cliente (y que hacía que la
 * factura se perdiera sin que nadie se enterara).
 */
public class HistorialLlenoException extends Exception {

    private static final long serialVersionUID = 1L;

    public HistorialLlenoException(String mensaje) {
        super(mensaje);
    }
}
