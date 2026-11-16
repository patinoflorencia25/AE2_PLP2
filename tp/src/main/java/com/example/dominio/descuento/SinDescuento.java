package com.example.dominio.descuento;

/** Estrategia para clientes Regular: no hay descuento. */
public class SinDescuento implements DescuentoStrategy {

    @Override
    public double calcularDescuento(double subtotal) {
        return 0.0;
    }

    @Override
    public String getNombre() {
        return "Sin descuento";
    }
}
