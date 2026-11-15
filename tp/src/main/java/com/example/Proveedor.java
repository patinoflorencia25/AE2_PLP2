package com.example;

/**
 * Proveedor de productos/servicios. Mantiene su catálogo mediante
 * un arreglo de capacidad fija (no colecciones de Java).
 */
public class Proveedor extends Persona {

    private static final int CAPACIDAD_PRODUCTOS = 20;

    private String razonSocial;
    private String cuit;
    private Producto[] productos;
    private int cantidadProductos;

    public Proveedor(String nombre, String domicilio, String dni, String telefono,
                     String razonSocial, String cuit) {
        super(nombre, domicilio, dni, telefono);
        this.razonSocial = razonSocial;
        this.cuit = cuit;
        this.productos = new Producto[CAPACIDAD_PRODUCTOS];
        this.cantidadProductos = 0;
    }

    public void agregarProducto(Producto producto) {
        if (cantidadProductos >= productos.length) {
            throw new CapacidadExcedidaException("No se puede agregar el producto: el catálogo de "
                    + razonSocial + " ya tiene el máximo de " + CAPACIDAD_PRODUCTOS + " productos.");
        }
        productos[cantidadProductos] = producto;
        cantidadProductos++;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public String getCuit() {
        return cuit;
    }

    public Producto[] getProductos() {
        return productos;
    }

    public int getCantidadProductos() {
        return cantidadProductos;
    }

    @Override
    public String getRol() {
        return "Proveedor";
    }

    @Override
    public String getDatosEspecificos() {
        return "Razón social: " + razonSocial + " | CUIT: " + cuit + " | Productos: " + cantidadProductos;
    }
}
