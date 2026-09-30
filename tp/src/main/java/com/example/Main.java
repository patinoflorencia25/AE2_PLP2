package com.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        
        // Departamento y Empleado
        Departamento depSistemas = new Departamento("Sistemas", 500000.0);
        Empleado empleado1 = new Empleado("Juan Perez", "Calle 123", "12345678", "3764111111",
                350000.0, "Tecnico", LocalDate.of(2023, 3, 1), depSistemas);
        depSistemas.asignarResponsable(empleado1);
        depSistemas.agregarEmpleado(empleado1);
 
        // Proveedor
        Proveedor proveedor1 = new Proveedor("Distribuidora SRL", "Ruta 12 km 5", "20111222",
                "3764222222", "Distribuidora SRL", "30-12345678-9");
 
        // Productos y Servicios (polimorfismo: ambos son ItemFacturable)
        Producto producto1 = new Producto("P001", "Mouse Inalambrico", 15000.0, "Periferico", proveedor1);
        Servicio servicio1 = new Servicio("S001", "Instalacion de Software", 8000.0, "Soporte", proveedor1);
        proveedor1.agregarProducto(producto1);
 
        // Cliente
        Cliente cliente1 = new Cliente("Maria Gomez", "Av. Siempreviva 742", "87654321",
                "3764333333", 100000.0, "Premium");
 
        // Factura
        Factura factura1 = new Factura(1, LocalDate.now(), cliente1, empleado1);
 
        // Demostracion polimorfica: mismo arreglo para Producto y Servicio
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
        } catch (FacturaSinItemsException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (PagoExcedidoException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
 
        // Demostracion de abstraccion: mostrarInfo() de Persona, cada rol aporta lo suyo
        cliente1.mostrarInfo();
        empleado1.mostrarInfo();
        proveedor1.mostrarInfo();
        // Mostrar detalle completo de la factura
        factura1.mostrarDetalle();
        
        // Demostracion de la interfaz Imprimible: Pago y Factura no son parientes,
        // pero ambos pueden tratarse como Imprimible
        System.out.println();
        System.out.println("--- Demostracion de la interfaz Imprimible ---");
        Imprimible[] imprimibles = { pago1, factura1 };
        for (Imprimible imprimible : imprimibles) {
            imprimible.mostrarDetalle();
        }
        
        // Demostracion de la interfaz Exportable: Cliente y Factura no son parientes,
        // pero ambos pueden convertirse en una linea de texto
        System.out.println();
        System.out.println("--- Demostracion de la interfaz Exportable ---");
        Exportable[] exportables = { cliente1, factura1 };
        for (Exportable exportable : exportables) {
            System.out.println(exportable.aLineaTexto());
        }
        
        // Demostracion de Comparable y Comparator con Collections.sort()
        System.out.println();
        System.out.println("--- Demostracion de Comparable y Comparator ---");
        Producto producto2 = new Producto("P002", "Teclado Mecanico", 25000.0, "Periferico", proveedor1);
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

        Collections.sort(catalogo); // orden natural: Comparable (por codigo)
        System.out.println("Orden natural (Comparable, por codigo):");
        for (ItemFacturable item : catalogo) {
            System.out.println("  " + item.getCodigo() + " - " + item.getDescripcion() + " -> $" + item.getPrecio());
        }

        Collections.sort(catalogo, new ComparadorPorPrecio()); // orden alternativo: Comparator (por precio)
        System.out.println("Orden alternativo (Comparator, por precio):");
        for (ItemFacturable item : catalogo) {
            System.out.println("  " + item.getCodigo() + " - " + item.getDescripcion() + " -> $" + item.getPrecio());
        }
        
        // Demostracion de excepciones propias del dominio
        System.out.println();
        System.out.println("--- Demostracion de excepciones propias ---");

        // 1) Factura sin items
        Factura facturaVacia = new Factura(2, LocalDate.now(), cliente1, empleado1);
        try {
            facturaVacia.asignarPago(new Pago(1000.0, LocalDate.now(), "Efectivo", "Parcial"));
        } catch (FacturaSinItemsException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (PagoExcedidoException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // 2) Pago mayor al total adeudado
        Factura facturaChica = new Factura(3, LocalDate.now(), cliente1, empleado1);
        facturaChica.agregarItem(producto1);
        try {
            facturaChica.asignarPago(new Pago(20000.0, LocalDate.now(), "Tarjeta", "Cancelado"));
        } catch (FacturaSinItemsException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (PagoExcedidoException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // 3) Cliente que supera su limite de credito
        Cliente clienteLimitado = new Cliente("Carlos Lopez", "Calle 456", "11223344",
                "3764444444", 10000.0, "Regular");
        Factura facturaGrande = new Factura(4, LocalDate.now(), clienteLimitado, empleado1);
        facturaGrande.agregarItem(producto2);
        try {
            clienteLimitado.agregarFactura(facturaGrande);
        } catch (LimiteCreditoExcedidoException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

}
