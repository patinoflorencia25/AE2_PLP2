package com.example;

import java.time.LocalDate;
import java.util.Arrays;

public class Factura implements Imprimible, Exportable {

    private static final int CAPACIDAD_MAXIMA = 10;

    private int numero;
    private LocalDate fechaEmision;
    private Cliente cliente;
    private Empleado empleado;
    private ItemFacturable[] items;
    private int cantidadItems;
    private Pago pago;
    private FormateadorFactura formateador = new FormateadorFactura();

    public Factura(int numero, LocalDate fechaEmision, Cliente cliente, Empleado empleado) {
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.cliente = cliente;
        this.empleado = empleado;
        this.items = new ItemFacturable[CAPACIDAD_MAXIMA];
        this.cantidadItems = 0;
    }

    public void agregarItem(ItemFacturable item) {
        if (cantidadItems < items.length) {
            items[cantidadItems] = item;
            cantidadItems++;
        }
    }

    public double calcularTotal() {
        double total = 0.0;
        for (int i = 0; i < cantidadItems; i++) {
            total += items[i].getSubtotal();
        }
        return total;
    }

        public void asignarPago(Pago pago) throws FacturaSinItemsException, PagoExcedidoException {
        if (cantidadItems == 0) {
            throw new FacturaSinItemsException("No se puede registrar el pago: la factura N° "
                    + numero + " no tiene items.");
        }
        double total = calcularTotal();
        if (pago.getMonto() > total) {
            throw new PagoExcedidoException("El pago de $" + pago.getMonto()
                    + " supera el total adeudado de $" + total + " de la factura N° " + numero + ".");
        }
        this.pago = pago;
    }

    // Getters que necesita el formateador para armar los textos
    public int getNumero() {
        return numero;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public Pago getPago() {
        return pago;
    }

    // Devuelve solo los items cargados (una copia, el arreglo interno no se expone)
    public ItemFacturable[] getItems() {
        return Arrays.copyOf(items, cantidadItems);
    }

    // Factura sigue cumpliendo los contratos Imprimible y Exportable,
    // pero delega en FormateadorFactura el armado de los textos
    @Override
    public void mostrarDetalle() {
        System.out.print(formateador.detalle(this));
    }

    @Override
    public String aLineaTexto() {
        return formateador.lineaArchivo(this);
    }
}
