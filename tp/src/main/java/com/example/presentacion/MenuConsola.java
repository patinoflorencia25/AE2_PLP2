package com.example.presentacion;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.example.dominio.Cliente;
import com.example.dominio.Factura;
import com.example.dominio.FormateadorFactura;
import com.example.dominio.ItemFacturable;
import com.example.dominio.Pago;
import com.example.servicio.DescuentosPorCategoria;
import com.example.servicio.SistemaFacturacion;

/**
 * Capa de presentación: menú de consola. Solo lee lo que escribe la persona, llama al
 * servicio y muestra el resultado o el error. No tiene reglas de negocio ni toca archivos.
 */
public class MenuConsola {

    /** Una acción del menú; puede fallar con cualquier error del sistema. */
    private interface Accion {
        void ejecutar() throws Exception;
    }

    private final SistemaFacturacion sistema;
    private final Scanner entrada;
    private final PrintStream salida;
    private final FormateadorFactura formateador = new FormateadorFactura();

    public MenuConsola(SistemaFacturacion sistema, InputStream in, PrintStream out) {
        this.sistema = sistema;
        this.entrada = new Scanner(in, "UTF-8");
        this.salida = out;
    }

    public void iniciar() {
        try {
            boolean seguir = true;
            while (seguir) {
                mostrarMenu();
                String opcion = leer("Opción");
                switch (opcion) {
                    case "1": ejecutar(this::registrarCliente); break;
                    case "2": ejecutar(this::registrarItem); break;
                    case "3": ejecutar(this::emitirFactura); break;
                    case "4": ejecutar(this::registrarPago); break;
                    case "5": ejecutar(this::menuListados); break;
                    case "6": ejecutar(this::verFactura); break;
                    case "7": ejecutar(this::guardar); break;
                    case "8": ejecutar(this::recuperar); break;
                    case "0": seguir = false; break;
                    default: salida.println("Opción inválida."); break;
                }
            }
            salida.println("Hasta luego.");
        } catch (NoSuchElementException e) {
            salida.println();
            salida.println("Fin de la entrada. Hasta luego.");
        }
    }

    private void mostrarMenu() {
        salida.println();
        salida.println("===== SISTEMA DE FACTURACIÓN =====");
        salida.println("1. Registrar cliente");
        salida.println("2. Registrar producto o servicio");
        salida.println("3. Emitir factura");
        salida.println("4. Registrar pago");
        salida.println("5. Listar información (ordenada)");
        salida.println("6. Ver detalle de una factura");
        salida.println("7. Guardar datos");
        salida.println("8. Recuperar datos");
        salida.println("0. Salir");
    }

    // Todo error esperado (validación, regla de negocio, archivos) se informa sin cortar el programa
    private void ejecutar(Accion accion) {
        try {
            accion.ejecutar();
        } catch (NoSuchElementException e) {
            throw e; // fin de la entrada: lo maneja iniciar()
        } catch (Exception e) {
            salida.println("ERROR: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------- acciones

    private void registrarCliente() {
        String nombre = leer("Nombre");
        String dni = leer("DNI");
        String domicilio = leer("Domicilio");
        String telefono = leer("Teléfono");
        String categoria = leer("Categoría " + DescuentosPorCategoria.categoriasDisponibles());
        double limite = leerNumero("Límite de crédito");
        Cliente c = sistema.registrarCliente(nombre, domicilio, dni, telefono, categoria, limite);
        salida.println("Cliente registrado: " + c.getNombre() + " (" + c.getCategoria() + ")");
    }

    private void registrarItem() {
        String tipo = leer("Tipo (P = producto, S = servicio)");
        String codigo = leer("Código");
        String nombre = leer("Nombre");
        double precio = leerNumero("Precio");
        String tipoItem = leer("Tipo/rubro (ej. Periférico, Soporte)");
        ItemFacturable item = sistema.registrarItem(tipo, codigo, nombre, precio, tipoItem);
        salida.println("Registrado: " + item.getDescripcion() + " -> $" + item.getPrecio());
    }

    private void emitirFactura() throws Exception {
        String dni = leer("DNI del cliente");
        salida.println("Catálogo:");
        for (ItemFacturable i : sistema.catalogoPorCodigo()) {
            salida.println("  " + i.getCodigo() + " - " + i.getDescripcion() + " -> $" + i.getPrecio());
        }
        List<String> codigos = new ArrayList<>();
        salida.println("Ingresá los códigos de los ítems (vacío para terminar):");
        while (true) {
            String codigo = leer("Código");
            if (codigo.isEmpty()) {
                break;
            }
            codigos.add(codigo);
        }
        Factura f = sistema.emitirFactura(dni, codigos);
        salida.println("Factura emitida:");
        salida.print(formateador.detalle(f));
    }

    private void registrarPago() throws Exception {
        int numero = (int) leerNumero("N° de factura");
        Factura f = sistema.buscarFactura(numero);
        if (f != null) {
            salida.println("Total a pagar: $" + f.calcularTotal());
        }
        double monto = leerNumero("Monto");
        String metodo = leer("Método de pago " + SistemaFacturacion.METODOS_PAGO);
        Pago p = sistema.registrarPago(numero, monto, metodo);
        salida.println("Pago registrado.");
        p.mostrarDetalle();
    }

    private void menuListados() {
        salida.println("1. Clientes por nombre");
        salida.println("2. Clientes por límite de crédito (mayor a menor)");
        salida.println("3. Facturas por número");
        salida.println("4. Facturas por total (mayor a menor)");
        salida.println("5. Catálogo por código");
        salida.println("6. Catálogo por precio (menor a mayor)");
        switch (leer("Opción")) {
            case "1": listarClientes(sistema.clientesPorNombre()); break;
            case "2": listarClientes(sistema.clientesPorLimiteDescendente()); break;
            case "3": listarFacturas(sistema.facturasPorNumero()); break;
            case "4": listarFacturas(sistema.facturasPorTotalDescendente()); break;
            case "5": listarItems(sistema.catalogoPorCodigo()); break;
            case "6": listarItems(sistema.catalogoPorPrecio()); break;
            default: salida.println("Opción inválida."); break;
        }
    }

    private void listarClientes(List<Cliente> lista) {
        if (lista.isEmpty()) {
            salida.println("No hay clientes registrados.");
        }
        for (Cliente c : lista) {
            c.mostrarInfo();
        }
    }

    private void listarFacturas(List<Factura> lista) {
        if (lista.isEmpty()) {
            salida.println("No hay facturas emitidas.");
        }
        for (Factura f : lista) {
            String estado = f.getPago() == null ? "Sin pago" : f.getPago().getEstado();
            salida.println("N° " + f.getNumero() + " | " + f.getFechaEmision() + " | "
                    + f.getCliente().getNombre() + " | Total: $" + f.calcularTotal() + " | " + estado);
        }
    }

    private void listarItems(List<ItemFacturable> lista) {
        for (ItemFacturable i : lista) {
            salida.println(i.getCodigo() + " - " + i.getDescripcion() + " -> $" + i.getPrecio());
        }
    }

    private void verFactura() {
        int numero = (int) leerNumero("N° de factura");
        Factura f = sistema.buscarFactura(numero);
        if (f == null) {
            salida.println("No existe la factura N° " + numero + ".");
            return;
        }
        salida.print(formateador.detalle(f));
    }

    private void guardar() throws Exception {
        sistema.guardarDatos();
        salida.println("Datos guardados en " + SistemaFacturacion.ARCHIVO_CLIENTES + ", "
                + SistemaFacturacion.ARCHIVO_ITEMS + " y " + SistemaFacturacion.ARCHIVO_FACTURAS + ".");
    }

    private void recuperar() throws Exception {
        sistema.recuperarDatos();
        salida.println("Datos recuperados. Próxima factura: N° " + sistema.proximoNumeroFactura() + ".");
    }

    // ---------------------------------------------------------------- lectura

    private String leer(String etiqueta) {
        salida.print(etiqueta + ": ");
        return entrada.nextLine().trim();
    }

    private double leerNumero(String etiqueta) {
        String texto = leer(etiqueta).replace(',', '.');
        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("\"" + texto + "\" no es un número válido.");
        }
    }
}
