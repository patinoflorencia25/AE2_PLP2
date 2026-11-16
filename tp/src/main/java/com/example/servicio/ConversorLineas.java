package com.example.servicio;

import java.time.LocalDate;
import java.util.Map;

import com.example.dominio.Cliente;
import com.example.dominio.Empleado;
import com.example.dominio.Factura;
import com.example.dominio.ItemFacturable;
import com.example.dominio.Pago;
import com.example.dominio.Proveedor;
import com.example.dominio.fabrica.CreadorItem;

/**
 * Reconstruye objetos del dominio a partir de las líneas de texto que entrega la
 * persistencia (la inversa de Exportable.aLineaTexto). La capa de persistencia solo
 * mueve texto; qué significa ese texto es decisión del servicio.
 * Cualquier línea mal formada lanza IllegalArgumentException (o una subclase).
 */
class ConversorLineas {

    private static final String SEPARADOR = ";";

    private String[] partir(String linea, int cantidad) {
        String[] campos = linea.split(SEPARADOR, -1);
        if (campos.length != cantidad) {
            throw new IllegalArgumentException("se esperaban " + cantidad + " campos y hay " + campos.length);
        }
        return campos;
    }

    // nombre;dni;domicilio;teléfono;categoría;límite
    Cliente aCliente(String linea) {
        String[] c = partir(linea, 6);
        return new Cliente(c[0], c[2], c[1], c[3], Double.parseDouble(c[5]),
                DescuentosPorCategoria.normalizar(c[4]));
    }

    // clave;código;nombre;precio;tipo  -> la clave decide qué creador (Factory Method) se usa
    ItemFacturable aItem(String linea, Map<String, CreadorItem> creadores, Proveedor proveedor) {
        String[] c = partir(linea, 5);
        CreadorItem creador = creadores.get(c[0]);
        if (creador == null) {
            throw new IllegalArgumentException("tipo de ítem desconocido: " + c[0]);
        }
        return creador.crearItem(c[1], c[2], Double.parseDouble(c[3]), c[4], proveedor);
    }

    // número;fecha;dni;empleado;códigos;método;monto;estado;fechaPago
    // No agrega la factura al historial del cliente: eso lo hace el servicio (aplica el límite de crédito).
    Factura aFactura(String linea, Map<String, Cliente> clientes, Map<String, ItemFacturable> catalogo,
                     Empleado empleado) throws Exception {
        String[] c = partir(linea, 9);
        Cliente cliente = clientes.get(c[2]);
        if (cliente == null) {
            throw new IllegalArgumentException("cliente inexistente (DNI " + c[2] + ")");
        }
        Factura factura = new Factura(Integer.parseInt(c[0]), LocalDate.parse(c[1]), cliente, empleado,
                DescuentosPorCategoria.para(cliente.getCategoria()));
        if (!c[4].isBlank()) {
            for (String codigo : c[4].split(",")) {
                ItemFacturable item = catalogo.get(codigo);
                if (item == null) {
                    throw new IllegalArgumentException("ítem inexistente (código " + codigo + ")");
                }
                factura.agregarItem(item);
            }
        }
        if (!c[5].isBlank()) {
            factura.asignarPago(new Pago(Double.parseDouble(c[6]), LocalDate.parse(c[8]), c[5], c[7]));
        }
        return factura;
    }
}
