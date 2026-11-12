package com.example;

/**
 * Arma los textos de una factura: el detalle para mostrar en pantalla y la linea
 * para guardar en archivo. Asi Factura se queda solo con las reglas del negocio
 * (items, total y pago) y un cambio de formato no la obliga a tocar - SRP.
 */

public class FormateadorFactura {

    // Mismo texto que antes imprimia Factura.mostrarDetalle()
    public String detalle(Factura factura) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== Factura N° ").append(factura.getNumero()).append(" =====\n");
        sb.append("Fecha de emision: ").append(factura.getFechaEmision()).append("\n");
        sb.append("Cliente: ").append(factura.getCliente().getNombre()).append("\n");
        sb.append("Empleado que gestiono la operacion: ").append(factura.getEmpleado().getNombre()).append("\n");
        sb.append("Items:\n");
        for (ItemFacturable item : factura.getItems()) {
            sb.append("  - ").append(item.getDescripcion()).append(" -> $").append(item.getSubtotal()).append("\n");
        }
        if (factura.getPago() != null) {
            sb.append(factura.getPago().describir()).append("\n");
        }
        sb.append("Total: $").append(factura.calcularTotal()).append("\n");
        return sb.toString();
    }

    // Mismo formato de archivo que antes: numero;fecha;cliente;empleado;total
    public String lineaArchivo(Factura factura) {
        return factura.getNumero() + ";" + factura.getFechaEmision() + ";"
                + factura.getCliente().getNombre() + ";" + factura.getEmpleado().getNombre() + ";"
                + factura.calcularTotal();
    }
}
