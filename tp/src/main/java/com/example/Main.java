package com.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {

        // Departamento y Empleado
        Departamento depSistemas = new Departamento("Sistemas", 500000.0);
        Empleado empleado1 = new Empleado("Juan Pérez", "Calle 123", "12345678", "3764111111",
                350000.0, "Técnico", LocalDate.of(2023, 3, 1), depSistemas);
        depSistemas.asignarResponsable(empleado1);
        depSistemas.agregarEmpleado(empleado1);

        // Proveedor
        Proveedor proveedor1 = new Proveedor("Distribuidora SRL", "Ruta 12 km 5", "20111222",
                "3764222222", "Distribuidora SRL", "30-12345678-9");

        // Productos y Servicios (polimorfismo: ambos son ItemFacturable)
        Producto producto1 = new Producto("P001", "Mouse Inalámbrico", 15000.0, "Periférico", proveedor1);
        Servicio servicio1 = new Servicio("S001", "Instalación de Software", 8000.0, "Soporte", proveedor1);
        proveedor1.agregarProducto(producto1);

        // Cliente
        Cliente cliente1 = new Cliente("María Gómez", "Av. Siempreviva 742", "87654321",
                "3764333333", 100000.0, "Premium");

        // Factura
        Factura factura1 = new Factura(1, LocalDate.now(), cliente1, empleado1);

        // Demostración polimórfica: mismo arreglo para Producto y Servicio
        ItemFacturable[] itemsFactura = { producto1, servicio1 };
        for (ItemFacturable item : itemsFactura) {
            factura1.agregarItem(item);
            System.out.println(item.getDescripcion() + " -> $" + item.getSubtotal());
        }

        try {
            cliente1.agregarFactura(factura1);
        } catch (LimiteCreditoExcedidoException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // Pago / Recibo
        Pago pago1 = new Pago(factura1.calcularTotal(), LocalDate.now(), "Transferencia", "Cancelado");
        try {
            factura1.asignarPago(pago1);
        } catch (FacturaSinItemsException | PagoInvalidoException | PagoExcedidoException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // Demostración de abstracción: mostrarInfo() de Persona, cada rol aporta lo suyo
        cliente1.mostrarInfo();
        empleado1.mostrarInfo();
        proveedor1.mostrarInfo();
        // Mostrar detalle completo de la factura
        factura1.mostrarDetalle();

        // Demostración de la interfaz Imprimible: Pago y Factura no son parientes,
        // pero ambos pueden tratarse como Imprimible
        System.out.println();
        System.out.println("--- Demostración de la interfaz Imprimible ---");
        Imprimible[] imprimibles = { pago1, factura1 };
        for (Imprimible imprimible : imprimibles) {
            imprimible.mostrarDetalle();
        }

        // Demostración de la interfaz Exportable: Cliente y Factura no son parientes,
        // pero ambos pueden convertirse en una línea de texto
        System.out.println();
        System.out.println("--- Demostración de la interfaz Exportable ---");
        Exportable[] exportables = { cliente1, factura1 };
        for (Exportable exportable : exportables) {
            System.out.println(exportable.aLineaTexto());
        }

        // Demostración de Comparable y Comparator con Collections.sort()
        System.out.println();
        System.out.println("--- Demostración de Comparable y Comparator ---");
        Producto producto2 = new Producto("P002", "Teclado Mecánico", 25000.0, "Periférico", proveedor1);
        Servicio servicio2 = new Servicio("S002", "Limpieza de Equipo", 5000.0, "Soporte", proveedor1);

        ArrayList<ItemFacturable> catalogo = new ArrayList<>();
        catalogo.add(servicio1);
        catalogo.add(producto2);
        catalogo.add(producto1);
        catalogo.add(servicio2);

        System.out.println("Lista original (sin ordenar):");
        for (ItemFacturable item : catalogo) {
            System.out.println("  " + item.getCodigo() + " - " + item.getDescripcion() + " -> $" + item.getPrecio());
        }

        Collections.sort(catalogo); // orden natural: Comparable (por código)
        System.out.println("Orden natural (Comparable, por código):");
        for (ItemFacturable item : catalogo) {
            System.out.println("  " + item.getCodigo() + " - " + item.getDescripcion() + " -> $" + item.getPrecio());
        }

        Collections.sort(catalogo, new ComparadorPorPrecio()); // orden alternativo: Comparator (por precio)
        System.out.println("Orden alternativo (Comparator, por precio):");
        for (ItemFacturable item : catalogo) {
            System.out.println("  " + item.getCodigo() + " - " + item.getDescripcion() + " -> $" + item.getPrecio());
        }

        // Demostración de excepciones propias del dominio
        System.out.println();
        System.out.println("--- Demostración de excepciones propias ---");

        // 1) Factura sin ítems
        Factura facturaVacia = new Factura(2, LocalDate.now(), cliente1, empleado1);
        try {
            facturaVacia.asignarPago(new Pago(1000.0, LocalDate.now(), "Efectivo", "Parcial"));
        } catch (FacturaSinItemsException | PagoInvalidoException | PagoExcedidoException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // 2) Pago mayor al total adeudado
        Factura facturaChica = new Factura(3, LocalDate.now(), cliente1, empleado1);
        facturaChica.agregarItem(producto1);
        try {
            facturaChica.asignarPago(new Pago(20000.0, LocalDate.now(), "Tarjeta", "Cancelado"));
        } catch (FacturaSinItemsException | PagoInvalidoException | PagoExcedidoException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // 3) Cliente que supera su límite de crédito
        Cliente clienteLimitado = new Cliente("Carlos López", "Calle 456", "11223344",
                "3764444444", 10000.0, "Regular");
        Factura facturaGrande = new Factura(4, LocalDate.now(), clienteLimitado, empleado1);
        facturaGrande.agregarItem(producto2);
        try {
            clienteLimitado.agregarFactura(facturaGrande);
        } catch (LimiteCreditoExcedidoException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // Demostración de persistencia: guardar y volver a leer archivos de texto
        System.out.println();
        System.out.println("--- Demostración de persistencia ---");
        // Main es el único lugar que elige la implementación concreta; el resto trabaja con la interfaz
        AlmacenamientoDatos gestor = new GestorArchivos();

        try {
            ArrayList<Exportable> facturasAGuardar = new ArrayList<>();
            facturasAGuardar.add(factura1);
            facturasAGuardar.add(facturaChica);
            gestor.guardar("facturas.txt", facturasAGuardar);
            System.out.println("Se guardaron " + facturasAGuardar.size() + " registros en facturas.txt");

            ArrayList<Exportable> clientesAGuardar = new ArrayList<>();
            clientesAGuardar.add(cliente1);
            clientesAGuardar.add(clienteLimitado);
            gestor.guardar("clientes.txt", clientesAGuardar);
            System.out.println("Se guardaron " + clientesAGuardar.size() + " registros en clientes.txt");

            System.out.println("Contenido leído de facturas.txt:");
            for (String linea : gestor.leer("facturas.txt")) {
                System.out.println("  " + linea);
            }
            System.out.println("Contenido leído de clientes.txt:");
            for (String linea : gestor.leer("clientes.txt")) {
                System.out.println("  " + linea);
            }
        } catch (PersistenciaException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

}
