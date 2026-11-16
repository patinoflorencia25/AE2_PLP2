package com.example.servicio;

import java.util.LinkedHashMap;
import java.util.Map;

import com.example.dominio.descuento.DescuentoCorporativo;
import com.example.dominio.descuento.DescuentoPremium;
import com.example.dominio.descuento.DescuentoStrategy;
import com.example.dominio.descuento.SinDescuento;

/**
 * Elige la estrategia de descuento que corresponde a cada categoría de cliente.
 * Agregar una categoría nueva es crear su estrategia y registrarla acá:
 * Factura y Cliente no se tocan (OCP).
 */
public final class DescuentosPorCategoria {

    private static final Map<String, DescuentoStrategy> ESTRATEGIAS = new LinkedHashMap<>();
    private static final Map<String, String> NOMBRES = new LinkedHashMap<>();

    static {
        registrar("Regular", new SinDescuento());
        registrar("Premium", new DescuentoPremium());
        registrar("Corporativo", new DescuentoCorporativo());
    }

    private DescuentosPorCategoria() {
    }

    private static void registrar(String categoria, DescuentoStrategy estrategia) {
        ESTRATEGIAS.put(categoria.toLowerCase(), estrategia);
        NOMBRES.put(categoria.toLowerCase(), categoria);
    }

    public static boolean esCategoriaValida(String categoria) {
        return categoria != null && ESTRATEGIAS.containsKey(categoria.trim().toLowerCase());
    }

    /** Devuelve el nombre canónico (con mayúscula inicial) de la categoría. */
    public static String normalizar(String categoria) {
        if (!esCategoriaValida(categoria)) {
            throw new IllegalArgumentException("Categoría inválida: " + categoria
                    + ". Opciones: " + categoriasDisponibles());
        }
        return NOMBRES.get(categoria.trim().toLowerCase());
    }

    public static DescuentoStrategy para(String categoria) {
        normalizar(categoria);
        return ESTRATEGIAS.get(categoria.trim().toLowerCase());
    }

    public static java.util.List<String> categoriasDisponibles() {
        return new java.util.ArrayList<>(NOMBRES.values());
    }
}
