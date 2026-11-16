package com.example.dominio.descuento;

/** Estrategia para clientes Corporativo: 15% sobre el subtotal. */
public class DescuentoCorporativo implements DescuentoStrategy {

    private static final double PORCENTAJE = 0.15;

    @Override
    public double calcularDescuento(double subtotal) {
        return subtotal * PORCENTAJE;
    }

    @Override
    public String getNombre() {
        return "Descuento Corporativo (15%)";
    }
}
