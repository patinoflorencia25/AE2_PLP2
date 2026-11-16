package com.example.servicio;


public final class GeneradorNumeroFactura {

    private static GeneradorNumeroFactura instancia;

    private int ultimoNumero = 0;

    private GeneradorNumeroFactura() {
    }

    public static synchronized GeneradorNumeroFactura getInstancia() {
        if (instancia == null) {
            instancia = new GeneradorNumeroFactura();
        }
        return instancia;
    }

    /** Número que le corresponde a la próxima factura (no lo consume). */
    public synchronized int proximoNumero() {
        return ultimoNumero + 1;
    }

    /** Marca un número como usado; nunca retrocede. */
    public synchronized void registrarUsado(int numero) {
        if (numero > ultimoNumero) {
            ultimoNumero = numero;
        }
    }

    /** Se usa al recuperar datos: el contador pasa a ser el mayor número recuperado. */
    public synchronized void reiniciar(int ultimo) {
        ultimoNumero = Math.max(0, ultimo);
    }
}
