package com.example.dominio.fabrica;

import com.example.dominio.ItemFacturable;
import com.example.dominio.Producto;
import com.example.dominio.Proveedor;

/** Creador concreto de Producto. */
public class CreadorProducto extends CreadorItem {

    @Override
    protected ItemFacturable crear(String codigo, String nombre, double precio,
                                   String tipo, Proveedor proveedor) {
        return new Producto(codigo, nombre, precio, tipo, proveedor);
    }

    @Override
    public String getClave() {
        return "P";
    }
}
