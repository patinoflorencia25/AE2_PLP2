package com.example;

/**
 * Arma los textos de una factura: el detalle para mostrar en pantalla y la línea
 * para guardar en archivo. Así Factura se queda solo con las reglas del negocio
 * (ítems, total y pago) y un cambio de formato no la obliga a modificarse (SRP).
 */
public class FormateadorFactura {

    // Mismo texto que antes imprimía Factura.mostrarDetalle()
    public String detalle(Factura factura) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== Factura N° ").append(factura.getNumero()).append(" =====\n");
        sb.append("Fecha de emisión: ").append(factura.getFechaEmision()).append("\n");
        sb.append("Cliente: ").append(factura.getCliente().getNombre()).append("\n");
        sb.append("Empleado que gestionó la operación: ").append(factura.getEmpleado().getNombre()).append("\n");
        sb.append("Ítems:\n");
        for (ItemFacturable item : factura.getItems()) {
            sb.append("  - ").append(item.getDescripcion()).append(" -> $").append(item.getSubtotal()).append("\n");
        }
        if (factura.getPago() != null) {
            sb.append(factura.getPago().describir()).append("\n");
        }
        sb.append("Total: $").append(factura.calcularTotal()).append("\n");
        return sb.toString();
    }

    // Formato de archivo: número;fecha;cliente;empleado;total
    public String lineaArchivo(Factura factura) {
        return factura.getNumero() + ";" + factura.getFechaEmision() + ";"
                + factura.getCliente().getNombre() + ";" + factura.getEmpleado().getNombre() + ";"
                + factura.calcularTotal();
    }
}
