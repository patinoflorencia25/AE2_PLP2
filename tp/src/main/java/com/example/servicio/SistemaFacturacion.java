package com.example.servicio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.dominio.Cliente;
import com.example.dominio.ComparadorPorPrecio;
import com.example.dominio.Departamento;
import com.example.dominio.Empleado;
import com.example.dominio.Exportable;
import com.example.dominio.Factura;
import com.example.dominio.ItemFacturable;
import com.example.dominio.Pago;
import com.example.dominio.Proveedor;
import com.example.dominio.excepcion.FacturaSinItemsException;
import com.example.dominio.excepcion.LimiteCreditoExcedidoException;
import com.example.dominio.excepcion.PagoExcedidoException;
import com.example.dominio.excepcion.PagoInvalidoException;
import com.example.dominio.fabrica.CreadorItem;
import com.example.dominio.fabrica.CreadorProducto;
import com.example.dominio.fabrica.CreadorServicio;
import com.example.persistencia.AlmacenamientoDatos;
import com.example.persistencia.PersistenciaException;

/**
 * Capa de servicio: casos de uso del sistema (registrar clientes, facturar, cobrar,
 * listar, guardar y recuperar). No imprime ni lee de consola (eso es presentación) y no
 * sabe cómo se guardan los datos: recibe un AlmacenamientoDatos por constructor (DIP).
 */
public final class SistemaFacturacion {

    public static final String ARCHIVO_CLIENTES = "clientes.txt";
    public static final String ARCHIVO_ITEMS = "items.txt";
    public static final String ARCHIVO_FACTURAS = "facturas.txt";

    public static final List<String> METODOS_PAGO = List.of("Efectivo", "Transferencia", "Tarjeta");

    private final AlmacenamientoDatos almacenamiento;
    private final GeneradorNumeroFactura numerador = GeneradorNumeroFactura.getInstancia(); // Singleton
    private final ConversorLineas conversor = new ConversorLineas();
    private final Map<String, CreadorItem> creadores = new LinkedHashMap<>(); // clave -> Factory Method

    private final Proveedor proveedor;
    private final Empleado empleado;

    private List<Cliente> clientes = new ArrayList<>();
    private List<ItemFacturable> catalogo = new ArrayList<>();
    private List<Factura> facturas = new ArrayList<>();

    public SistemaFacturacion(AlmacenamientoDatos almacenamiento) {
        this.almacenamiento = almacenamiento;
        registrarCreador(new CreadorProducto());
        registrarCreador(new CreadorServicio());

        // Datos base del negocio (los mismos del Main de la AE2)
        Departamento sistemas = new Departamento("Sistemas", 500000.0);
        this.empleado = new Empleado("Juan Pérez", "Calle 123", "12345678", "3764111111",
                350000.0, "Técnico", LocalDate.of(2023, 3, 1), sistemas);
        sistemas.asignarResponsable(empleado);
        sistemas.agregarEmpleado(empleado);
        this.proveedor = new Proveedor("Distribuidora SRL", "Ruta 12 km 5", "20111222",
                "3764222222", "Distribuidora SRL", "30-12345678-9");
        cargarCatalogoInicial();
        numerador.reiniciar(0);
    }

    private void registrarCreador(CreadorItem creador) {
        creadores.put(creador.getClave(), creador);
    }

    private void cargarCatalogoInicial() {
        registrarItem("P", "P001", "Mouse Inalámbrico", 15000.0, "Periférico");
        registrarItem("P", "P002", "Teclado Mecánico", 25000.0, "Periférico");
        registrarItem("S", "S001", "Instalación de Software", 8000.0, "Soporte");
        registrarItem("S", "S002", "Limpieza de Equipo", 5000.0, "Soporte");
    }

    // ---------------------------------------------------------------- casos de uso

    public Cliente registrarCliente(String nombre, String domicilio, String dni, String telefono,
                                    String categoria, double limiteCredito) {
        exigirTexto("nombre", nombre);
        exigirTexto("DNI", dni);
        exigirTexto("domicilio", domicilio);
        exigirTexto("teléfono", telefono);
        if (limiteCredito <= 0) {
            throw new IllegalArgumentException("El límite de crédito debe ser mayor a cero.");
        }
        if (buscarCliente(dni) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con DNI " + dni.trim() + ".");
        }
        Cliente cliente = new Cliente(nombre.trim(), domicilio.trim(), dni.trim(), telefono.trim(),
                limiteCredito, DescuentosPorCategoria.normalizar(categoria));
        clientes.add(cliente);
        return cliente;
    }

    /** tipo: "P" (producto) o "S" (servicio). El creador correspondiente decide la clase. */
    public ItemFacturable registrarItem(String tipo, String codigo, String nombre, double precio,
                                        String tipoItem) {
        CreadorItem creador = creadores.get(tipo == null ? "" : tipo.trim().toUpperCase());
        if (creador == null) {
            throw new IllegalArgumentException("Tipo de ítem inválido (use P o S).");
        }
        exigirTexto("código", codigo);
        exigirTexto("nombre", nombre);
        exigirTexto("tipo", tipoItem);
        if (buscarItem(codigo) != null) {
            throw new IllegalArgumentException("Ya existe un ítem con código " + codigo.trim() + ".");
        }
        ItemFacturable item = creador.crearItem(codigo, nombre, precio, tipoItem, proveedor);
        catalogo.add(item);
        return item;
    }

    /**
     * Emite una factura al cliente con los ítems indicados (por código; se puede repetir uno).
     * El número lo entrega el Singleton y el descuento sale de la categoría del cliente (Strategy).
     * Si el cliente supera su límite de crédito, la factura no se guarda y el número no se gasta.
     */
    public Factura emitirFactura(String dniCliente, List<String> codigosItems)
            throws LimiteCreditoExcedidoException {
        Cliente cliente = buscarCliente(dniCliente);
        if (cliente == null) {
            throw new IllegalArgumentException("No existe un cliente con DNI " + dniCliente + ".");
        }
        if (codigosItems == null || codigosItems.isEmpty()) {
            throw new IllegalArgumentException("La factura necesita al menos un ítem.");
        }
        Factura factura = new Factura(numerador.proximoNumero(), LocalDate.now(), cliente, empleado,
                DescuentosPorCategoria.para(cliente.getCategoria()));
        for (String codigo : codigosItems) {
            ItemFacturable item = buscarItem(codigo);
            if (item == null) {
                throw new IllegalArgumentException("No existe un ítem con código " + codigo + ".");
            }
            factura.agregarItem(item); // CapacidadExcedidaException si pasa de 10 ítems
        }
        cliente.agregarFactura(factura); // puede lanzar LimiteCreditoExcedidoException
        facturas.add(factura);
        numerador.registrarUsado(factura.getNumero());
        return factura;
    }

    /** El estado lo calcula el sistema: Cancelado si cubre el total, Parcial si no. */
    public Pago registrarPago(int numeroFactura, double monto, String metodoPago)
            throws FacturaSinItemsException, PagoInvalidoException, PagoExcedidoException {
        Factura factura = buscarFactura(numeroFactura);
        if (factura == null) {
            throw new IllegalArgumentException("No existe la factura N° " + numeroFactura + ".");
        }
        if (factura.getPago() != null) {
            throw new IllegalArgumentException("La factura N° " + numeroFactura + " ya tiene un pago registrado.");
        }
        String metodo = metodoValido(metodoPago);
        String estado = Math.abs(monto - factura.calcularTotal()) < 0.005
                ? Pago.ESTADO_CANCELADO : Pago.ESTADO_PARCIAL;
        Pago pago = new Pago(monto, LocalDate.now(), metodo, estado);
        factura.asignarPago(pago);
        return pago;
    }

    // ---------------------------------------------------------------- consultas ordenadas

    public Cliente buscarCliente(String dni) {
        if (dni == null) {
            return null;
        }
        for (Cliente c : clientes) {
            if (c.getDNI().equals(dni.trim())) {
                return c;
            }
        }
        return null;
    }

    public ItemFacturable buscarItem(String codigo) {
        if (codigo == null) {
            return null;
        }
        for (ItemFacturable i : catalogo) {
            if (i.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return i;
            }
        }
        return null;
    }

    public Factura buscarFactura(int numero) {
        for (Factura f : facturas) {
            if (f.getNumero() == numero) {
                return f;
            }
        }
        return null;
    }

    public int proximoNumeroFactura() {
        return numerador.proximoNumero();
    }

    public List<Cliente> clientesPorNombre() {
        List<Cliente> lista = new ArrayList<>(clientes);
        lista.sort(Comparator.comparing(Cliente::getNombre, String.CASE_INSENSITIVE_ORDER));
        return lista;
    }

    public List<Cliente> clientesPorLimiteDescendente() {
        List<Cliente> lista = new ArrayList<>(clientes);
        lista.sort(Comparator.comparingDouble(Cliente::getLimiteCredito).reversed());
        return lista;
    }

    public List<Factura> facturasPorNumero() {
        List<Factura> lista = new ArrayList<>(facturas);
        lista.sort(Comparator.comparingInt(Factura::getNumero));
        return lista;
    }

    public List<Factura> facturasPorTotalDescendente() {
        List<Factura> lista = new ArrayList<>(facturas);
        lista.sort(Comparator.comparingDouble(Factura::calcularTotal).reversed());
        return lista;
    }

    /** Orden natural de los ítems (Comparable): por código. */
    public List<ItemFacturable> catalogoPorCodigo() {
        List<ItemFacturable> lista = new ArrayList<>(catalogo);
        java.util.Collections.sort(lista);
        return lista;
    }

    /** Orden alternativo (Comparator): por precio, de menor a mayor. */
    public List<ItemFacturable> catalogoPorPrecio() {
        List<ItemFacturable> lista = new ArrayList<>(catalogo);
        lista.sort(new ComparadorPorPrecio());
        return lista;
    }

    // ---------------------------------------------------------------- persistencia

    public void guardarDatos() throws PersistenciaException {
        almacenamiento.guardar(ARCHIVO_ITEMS, new ArrayList<Exportable>(catalogo));
        almacenamiento.guardar(ARCHIVO_CLIENTES, new ArrayList<Exportable>(clientes));
        almacenamiento.guardar(ARCHIVO_FACTURAS, new ArrayList<Exportable>(facturas));
    }

    /**
     * Reemplaza los datos en memoria por los guardados. Es todo o nada: si algún archivo
     * falta o tiene una línea inválida, se informa con PersistenciaException y no se cambia nada.
     */
    public void recuperarDatos() throws PersistenciaException {
        List<ItemFacturable> nuevosItems = new ArrayList<>();
        Map<String, ItemFacturable> itemsPorCodigo = new LinkedHashMap<>();
        List<String> lineasItems = almacenamiento.leer(ARCHIVO_ITEMS);
        for (int n = 0; n < lineasItems.size(); n++) {
            if (lineasItems.get(n).isBlank()) {
                continue;
            }
            try {
                ItemFacturable item = conversor.aItem(lineasItems.get(n), creadores, proveedor);
                nuevosItems.add(item);
                itemsPorCodigo.put(item.getCodigo(), item);
            } catch (RuntimeException e) {
                throw lineaInvalida(ARCHIVO_ITEMS, n + 1, e);
            }
        }

        List<Cliente> nuevosClientes = new ArrayList<>();
        Map<String, Cliente> clientesPorDni = new LinkedHashMap<>();
        List<String> lineasClientes = almacenamiento.leer(ARCHIVO_CLIENTES);
        for (int n = 0; n < lineasClientes.size(); n++) {
            if (lineasClientes.get(n).isBlank()) {
                continue;
            }
            try {
                Cliente cliente = conversor.aCliente(lineasClientes.get(n));
                nuevosClientes.add(cliente);
                clientesPorDni.put(cliente.getDNI(), cliente);
            } catch (RuntimeException e) {
                throw lineaInvalida(ARCHIVO_CLIENTES, n + 1, e);
            }
        }

        List<Factura> nuevasFacturas = new ArrayList<>();
        int mayorNumero = 0;
        List<String> lineasFacturas = almacenamiento.leer(ARCHIVO_FACTURAS);
        for (int n = 0; n < lineasFacturas.size(); n++) {
            if (lineasFacturas.get(n).isBlank()) {
                continue;
            }
            try {
                Factura factura = conversor.aFactura(lineasFacturas.get(n), clientesPorDni, itemsPorCodigo, empleado);
                factura.getCliente().agregarFactura(factura);
                nuevasFacturas.add(factura);
                mayorNumero = Math.max(mayorNumero, factura.getNumero());
            } catch (Exception e) {
                throw lineaInvalida(ARCHIVO_FACTURAS, n + 1, e);
            }
        }

        this.catalogo = nuevosItems;
        this.clientes = nuevosClientes;
        this.facturas = nuevasFacturas;
        numerador.reiniciar(mayorNumero);
    }

    private PersistenciaException lineaInvalida(String archivo, int linea, Exception causa) {
        return new PersistenciaException("Datos inválidos en " + archivo + ", línea " + linea + ": "
                + causa.getMessage() + ". No se modificó nada.", causa);
    }

    // ---------------------------------------------------------------- validaciones

    private void exigirTexto(String campo, String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + campo + " no puede estar vacío.");
        }
        if (valor.contains(";") || valor.contains(",")) {
            throw new IllegalArgumentException("El campo " + campo + " no puede contener ';' ni ','.");
        }
    }

    private String metodoValido(String metodo) {
        for (String m : METODOS_PAGO) {
            if (m.equalsIgnoreCase(metodo == null ? "" : metodo.trim())) {
                return m;
            }
        }
        throw new IllegalArgumentException("Método de pago inválido. Opciones: " + METODOS_PAGO);
    }
}
