package com.example.dominio.fabrica;

import com.example.dominio.ItemFacturable;
import com.example.dominio.Proveedor;
import com.example.dominio.Servicio;

/** Creador concreto de Servicio. */
public class CreadorServicio extends CreadorItem {

    @Override
    protected ItemFacturable crear(String codigo, String nombre, double precio,
                                   String tipo, Proveedor proveedor) {
        return new Servicio(codigo, nombre, precio, tipo, proveedor);
    }

    @Override
    public String getClave() {
        return "S";
    }
}
