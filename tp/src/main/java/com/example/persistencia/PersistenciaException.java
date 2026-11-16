package com.example.persistencia;

/**
 * Se lanza cuando no se puede guardar o leer información (por ejemplo, un archivo
 * inexistente o sin permisos). Es propia del sistema y no es IOException a propósito:
 * así la interfaz AlmacenamientoDatos no queda atada a una tecnología concreta
 * (archivos) y quien la usa decide qué hacer con el error (DIP).
 */
public class PersistenciaException extends Exception {

    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
