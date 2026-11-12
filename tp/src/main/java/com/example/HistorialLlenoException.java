package com.example;

/**
 * Se lanza cuando se intenta agregar una factura a un historial que ya no tiene lugar.
 * Reemplaza al mensaje por consola que antes imprimia Cliente (y que hacia que la
 * factura se perdiera sin que nadie se enterara).
 */

public class HistorialLlenoException extends Exception {

    public HistorialLlenoException(String mensaje) {
        super(mensaje);
    }
}
