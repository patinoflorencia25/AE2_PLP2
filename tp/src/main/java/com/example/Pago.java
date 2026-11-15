package com.example;

import java.time.LocalDate;

/**
 * Pago realizado sobre una factura. Es Imprimible: puede mostrarse en pantalla.
 */
public class Pago implements Imprimible {

    private double monto;
    private LocalDate fecha;
    private String metodoPago;
    private String estado;

    public Pago(double monto, LocalDate fecha, String metodoPago, String estado) {
        this.monto = monto;
        this.fecha = fecha;
        this.metodoPago = metodoPago;
        this.estado = estado;
    }

    public double getMonto() {
        return monto;
    }

    // Texto del pago (lo usa también FormateadorFactura)
    public String describir() {
        return "Pago -> Monto: $" + monto + " | Método: " + metodoPago
                + " | Estado: " + estado + " | Fecha: " + fecha;
    }

    @Override
    public void mostrarDetalle() {
        System.out.println(describir());
    }
}
