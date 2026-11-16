package com.example.dominio.descuento;

/** Estrategia para clientes Premium: 10% sobre el subtotal. */
public class DescuentoPremium implements DescuentoStrategy {

    private static final double PORCENTAJE = 0.10;

    @Override
    public double calcularDescuento(double subtotal) {
        return subtotal * PORCENTAJE;
    }

    @Override
    public String getNombre() {
        return "Descuento Premium (10%)";
    }
}
