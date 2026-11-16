package com.example.dominio;

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
        if (factura.calcularDescuento() > 0) {
            sb.append("Subtotal: $").append(factura.calcularSubtotal()).append("\n");
            sb.append(factura.getDescuento().getNombre()).append(": -$")
              .append(factura.calcularDescuento()).append("\n");
        }
        if (factura.getPago() != null) {
            sb.append(factura.getPago().describir()).append("\n");
        }
        sb.append("Total: $").append(factura.calcularTotal()).append("\n");
        return sb.toString();
    }

    // Formato de archivo (alcanza para reconstruir la factura al recuperarla):
    // número;fecha;dniCliente;empleado;códigosÍtems(separados por coma);
    //   método;monto;estado;fechaPago   (los 4 últimos vacíos si no tiene pago)
    public String lineaArchivo(Factura factura) {
        StringBuilder codigos = new StringBuilder();
        for (ItemFacturable item : factura.getItems()) {
            if (codigos.length() > 0) {
                codigos.append(",");
            }
            codigos.append(item.getCodigo());
        }
        Pago pago = factura.getPago();
        String datosPago = pago == null ? ";;;"
                : pago.getMetodoPago() + ";" + pago.getMonto() + ";" + pago.getEstado() + ";" + pago.getFecha();
        return factura.getNumero() + ";" + factura.getFechaEmision() + ";"
                + factura.getCliente().getDNI() + ";" + factura.getEmpleado().getNombre() + ";"
                + codigos + ";" + datosPago;
    }
}
