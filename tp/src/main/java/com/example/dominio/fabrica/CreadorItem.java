package com.example.dominio.fabrica;

import com.example.dominio.ItemFacturable;
import com.example.dominio.Proveedor;

/**
 * PATRÓN FACTORY METHOD (creación). CreadorItem es el creador abstracto: define el
 * proceso de crear un ítem (validar los datos) y delega en el método fábrica
 * crear() la decisión de QUÉ clase concreta instanciar.
 * Síntoma que lo originó: al recuperar el catálogo desde archivo y al registrar ítems
 * desde el menú hacía falta un "if (tipo es P) new Producto(...) else new Servicio(...)",
 * repetido en cada lugar y que obliga a modificar código cada vez que aparezca un tipo nuevo.
 */
public abstract class CreadorItem {

    /** Crea el ítem validando los datos comunes; las subclases eligen la clase concreta. */
    public final ItemFacturable crearItem(String codigo, String nombre, double precio,
                                          String tipo, Proveedor proveedor) {
        if (codigo == null || codigo.isBlank() || nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El código y el nombre del ítem son obligatorios.");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero.");
        }
        return crear(codigo.trim(), nombre.trim(), precio, tipo == null ? "" : tipo.trim(), proveedor);
    }

    /** Método fábrica: cada subclase devuelve su tipo de ítem. */
    protected abstract ItemFacturable crear(String codigo, String nombre, double precio,
                                            String tipo, Proveedor proveedor);

    /** Clave con la que se identifica este tipo en los archivos (P / S). */
    public abstract String getClave();
}
