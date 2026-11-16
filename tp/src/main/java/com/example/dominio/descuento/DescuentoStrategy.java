package com.example.dominio.descuento;

/**
 * PATRÓN STRATEGY. Algoritmo intercambiable que calcula el descuento de una factura.
 * Síntoma que lo originó: la categoría del cliente (Regular / Premium / Corporativo) era
 * solo un texto; descontar según ella habría exigido un if/else en Factura que crece con
 * cada categoría nueva (viola OCP). Ahora cada categoría es una estrategia y Factura
 * solo conoce esta interfaz.
 */
public interface DescuentoStrategy {

    /** Monto a descontar sobre el subtotal (nunca negativo). */
    double calcularDescuento(double subtotal);

    /** Nombre legible, para mostrar en la factura. */
    String getNombre();
}
