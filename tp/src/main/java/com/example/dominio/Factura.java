package com.example.dominio;

import com.example.dominio.excepcion.*;

import java.time.LocalDate;
import java.util.Arrays;

import com.example.dominio.descuento.DescuentoStrategy;
import com.example.dominio.descuento.SinDescuento;

/**
 * Factura emitida a un cliente por un empleado. Agrupa ítems facturables
 * (productos y servicios) y puede tener un pago asociado.
 * Es Imprimible y Exportable, pero delega en FormateadorFactura el armado de los textos.
 */
public class Factura implements Imprimible, Exportable {

    private static final int CAPACIDAD_MAXIMA = 10;

    private int numero;
    private LocalDate fechaEmision;
    private Cliente cliente;
    private Empleado empleado;
    private ItemFacturable[] items;
    private int cantidadItems;
    private Pago pago;
    private final DescuentoStrategy descuento; // Strategy: cómo se descuenta según el cliente
    private final FormateadorFactura formateador = new FormateadorFactura();

    public Factura(int numero, LocalDate fechaEmision, Cliente cliente, Empleado empleado) {
        this(numero, fechaEmision, cliente, empleado, new SinDescuento());
    }

    public Factura(int numero, LocalDate fechaEmision, Cliente cliente, Empleado empleado,
                   DescuentoStrategy descuento) {
        this.descuento = descuento;
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.cliente = cliente;
        this.empleado = empleado;
        this.items = new ItemFacturable[CAPACIDAD_MAXIMA];
        this.cantidadItems = 0;
    }

    public void agregarItem(ItemFacturable item) {
        if (cantidadItems >= items.length) {
            throw new CapacidadExcedidaException("No se puede agregar el ítem: la factura N° "
                    + numero + " ya tiene el máximo de " + CAPACIDAD_MAXIMA + " ítems.");
        }
        items[cantidadItems] = item;
        cantidadItems++;
    }

    // Suma de los subtotales de los ítems, sin descuento
    public double calcularSubtotal() {
        double total = 0.0;
        for (int i = 0; i < cantidadItems; i++) {
            total += items[i].getSubtotal();
        }
        return total;
    }

    // El cálculo concreto lo decide la estrategia (Strategy)
    public double calcularDescuento() {
        return descuento.calcularDescuento(calcularSubtotal());
    }

    public double calcularTotal() {
        return calcularSubtotal() - calcularDescuento();
    }

    public DescuentoStrategy getDescuento() {
        return descuento;
    }

    public void asignarPago(Pago pago)
            throws FacturaSinItemsException, PagoInvalidoException, PagoExcedidoException {
        if (cantidadItems == 0) {
            throw new FacturaSinItemsException("No se puede registrar el pago: la factura N° "
                    + numero + " no tiene ítems.");
        }
        if (pago.getMonto() <= 0) {
            throw new PagoInvalidoException("El pago de $" + pago.getMonto()
                    + " no es válido: el monto debe ser mayor a cero.");
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

    // Devuelve solo los ítems cargados (una copia: el arreglo interno no se expone)
    public ItemFacturable[] getItems() {
        return Arrays.copyOf(items, cantidadItems);
    }

    @Override
    public void mostrarDetalle() {
        System.out.print(formateador.detalle(this));
    }

    @Override
    public String aLineaTexto() {
        return formateador.lineaArchivo(this);
    }
}
