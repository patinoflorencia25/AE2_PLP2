package com.example;

/**
 * Cliente de la empresa, asociado a un historial de facturas.
 */
public class Cliente extends Persona implements Exportable {

    private double limiteCredito;
    private String categoria; // regular / premium / corporativo
    private HistorialFacturas historial; // el almacenamiento de las facturas vive en su propia clase

    public Cliente(String nombre, String domicilio, String DNI, String telefono,
                   double limiteCredito, String categoria) {
        super(nombre, domicilio, DNI, telefono);
        this.limiteCredito = limiteCredito;
        this.categoria = categoria;
        this.historial = new HistorialFacturas();
    }

    public void agregarFactura(Factura factura) throws LimiteCreditoExcedidoException {
        // Cliente conserva solo la regla de negocio: puede comprar si no supera su límite
        double totalAcumulado = historial.calcularTotalAcumulado();
        double totalNuevo = factura.calcularTotal();

        if (totalAcumulado + totalNuevo > limiteCredito) {
            throw new LimiteCreditoExcedidoException("El cliente " + getNombre()
                    + " supera su límite de crédito de $" + limiteCredito
                    + " (acumulado: $" + totalAcumulado + " + factura nueva: $" + totalNuevo + ")");
        }

        // Guardar la factura es responsabilidad del historial
        historial.agregar(factura);
    }

    public double getLimiteCredito() {
        return limiteCredito;
    }

    public String getCategoria() {
        return categoria;
    }

    public Factura[] getHistorial() {
        return historial.getFacturas();
    }

    public int getCantidadFacturas() {
        return historial.getCantidad();
    }

    @Override
    public String getRol() {
        return "Cliente";
    }

    @Override
    public String getDatosEspecificos() {
        return "Categoría: " + categoria + " | Límite de crédito: $" + limiteCredito;
    }

    @Override
    public String aLineaTexto() {
        return getNombre() + ";" + getDNI() + ";" + categoria + ";"
                + limiteCredito + ";" + historial.getCantidad();
    }
}
