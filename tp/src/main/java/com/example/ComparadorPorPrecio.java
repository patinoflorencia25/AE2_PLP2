package com.example;

import java.util.Comparator;

/**
 * Criterio alternativo de orden para los ítems: por precio, de menor a mayor.
 * Se usa con Collections.sort(lista, new ComparadorPorPrecio()).
 */

public class ComparadorPorPrecio implements Comparator<ItemFacturable> {

    @Override
    public int compare(ItemFacturable a, ItemFacturable b) {
        return Double.compare(a.getPrecio(), b.getPrecio());
    }
}