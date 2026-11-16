package com.example.dominio;

/**
 * Clase abstracta base para todo lo que se puede facturar (Producto o Servicio).
 * No se instancia directamente. Declara dos métodos abstractos que cada
 * subclase implementa a su manera, y deja métodos concretos compartidos.
 */
public abstract class ItemFacturable implements Comparable<ItemFacturable>, Exportable {

    private String codigo;
    private String nombre;
    private double precio;
    private String tipo;
    private Proveedor proveedor;

    public ItemFacturable(String codigo, String nombre, double precio, String tipo, Proveedor proveedor) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.tipo = tipo;
        this.proveedor = proveedor;
    }

    // Métodos abstractos: cada subclase los implementa a su manera
    public abstract String getDescripcion();
    public abstract double getSubtotal();
    public abstract String getClaveTipo(); // "P" producto / "S" servicio (para guardar en archivo)

    // Métodos concretos compartidos por Producto y Servicio
    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public String getTipo() {
        return tipo;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    // Formato de archivo: clave;código;nombre;precio;tipo
    @Override
    public String aLineaTexto() {
        return getClaveTipo() + ";" + codigo + ";" + nombre + ";" + precio + ";" + tipo;
    }

    // Orden natural: por código (Comparable)
    @Override
    public int compareTo(ItemFacturable otro) {
        return this.codigo.compareTo(otro.getCodigo());
    }
}
