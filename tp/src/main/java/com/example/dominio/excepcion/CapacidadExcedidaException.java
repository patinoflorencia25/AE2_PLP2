package com.example.dominio.excepcion;

/**
 * Se lanza cuando se intenta agregar un elemento a una estructura de capacidad fija
 * que ya está llena (ítems de una factura, historial de un cliente, empleados de un
 * departamento o productos de un proveedor).
 * Antes cada clase resolvía este caso a su manera: una perdía el elemento sin avisar
 * y otras escribían un mensaje por consola desde el dominio.
 * Es una excepción no verificada (como IllegalStateException, que usa Java en sus
 * colecciones con límite): expresa un límite técnico de la estructura, no una regla
 * del negocio como el límite de crédito.
 */
public class CapacidadExcedidaException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    public CapacidadExcedidaException(String mensaje) {
        super(mensaje);
    }
}
