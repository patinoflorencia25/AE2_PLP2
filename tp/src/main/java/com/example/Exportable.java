package com.example;

/**
 * Capacidad de convertirse en una línea de texto, lista para guardar en un archivo.
 * La implementan clases que no son parientes entre sí (Factura y Cliente).
 */

public interface Exportable {

    String aLineaTexto();
}
