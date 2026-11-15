package com.example;

/**
 * Guarda las facturas de un cliente y sabe calcular cuánto suman.
 * Su única responsabilidad es el almacenamiento del historial (SRP):
 * la decisión de si un cliente puede o no comprar más sigue siendo de Cliente.
 */
public class HistorialFacturas {

    private static final int CAPACIDAD = 20; // Capacidad máxima del historial

    private Factura[] facturas;
    private int cantidad; // Contador de facturas guardadas

    public HistorialFacturas() {
        this.facturas = new Factura[CAPACIDAD];
        this.cantidad = 0;
    }

    public void agregar(Factura factura) throws HistorialLlenoException {
        if (cantidad >= facturas.length) {
            throw new HistorialLlenoException("No se puede agregar la factura: historial completo ("
                    + CAPACIDAD + " facturas).");
        }
        facturas[cantidad] = factura;
        cantidad++;
    }

    // Suma de los totales de todas las facturas guardadas
    public double calcularTotalAcumulado() {
        double total = 0.0;
        for (int i = 0; i < cantidad; i++) {
            total += facturas[i].calcularTotal();
        }
        return total;
    }

    public Factura[] getFacturas() {
        return facturas;
    }

    public int getCantidad() {
        return cantidad;
    }
}
