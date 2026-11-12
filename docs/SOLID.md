# AE4 – Revisión del diseño con principios SOLID

Base común: **AE2 de Patiño Florencia** (repositorio `AE2_PLP2`, commit `36db1cf`).
Los cambios están en la rama `solid`, con un commit por problema.

Verificación: antes y después de cada refactorización se compiló el proyecto y se
comparó la salida de `Main` con la de la versión original. La salida por pantalla
(68 líneas) y los archivos `facturas.txt` y `clientes.txt` quedaron **idénticos**.

---

## Los cinco principios en una frase (con el código de este proyecto)

| Principio | Idea | En este proyecto |
|---|---|---|
| **S** – Responsabilidad única | Una clase, una sola razón para cambiar. | Problemas 1 y 3 (violaciones). |
| **O** – Abierto/cerrado | Extender sin modificar código que ya funciona. | Sin `if/else` por tipo hoy, no hay violación. `GestorArchivos.guardar` ya lo cumple: acepta cualquier `Exportable`. |
| **L** – Sustitución de Liskov | Una subclase puede usarse donde se espera la base sin romper nada. | Sin violaciones: `Producto`/`Servicio` y `Cliente`/`Empleado`/`Proveedor` respetan a su clase base. |
| **I** – Segregación de interfaces | Interfaces chicas y específicas. | Sin violaciones: `Imprimible` y `Exportable` tienen un método y todas sus clases lo usan. |
| **D** – Inversión de dependencias | Depender de abstracciones, no de clases concretas. | Problema 2 (violación). |

---

## Problema 1 – SRP en `Cliente`

**Síntoma.** `Cliente.agregarFactura()` tenía tres razones para cambiar:
la regla de crédito (negocio), el manejo del arreglo `Factura[]` (almacenamiento)
y un `System.out.println` cuando el historial estaba lleno (salida por consola).
Además, con el historial lleno **la factura se perdía en silencio**.

**Antes**
```java
public void agregarFactura(Factura factura) throws LimiteCreditoExcedidoException {
    double totalAcumulado = 0.0;
    for (int i = 0; i < cantidadFacturas; i++) {
        totalAcumulado += historial[i].calcularTotal();
    }
    double totalNuevo = factura.calcularTotal();
    if (totalAcumulado + totalNuevo > limiteCredito) {
        throw new LimiteCreditoExcedidoException(...);
    }
    if (cantidadFacturas < historial.length) {
        historial[cantidadFacturas] = factura;
        cantidadFacturas++;
    } else {
        System.out.println("No se puede agregar la factura: historial completo.");
    }
}
```

**Después**
```java
// Cliente: solo la regla de negocio
public void agregarFactura(Factura factura)
        throws LimiteCreditoExcedidoException, HistorialLlenoException {
    double totalAcumulado = historial.calcularTotalAcumulado();
    double totalNuevo = factura.calcularTotal();
    if (totalAcumulado + totalNuevo > limiteCredito) {
        throw new LimiteCreditoExcedidoException(...);
    }
    historial.agregar(factura);   // guardar es responsabilidad del historial
}
```
Clases nuevas: `HistorialFacturas` (arreglo, contador y total acumulado) y
`HistorialLlenoException` (reemplaza al `println` y evita perder la factura).

**Qué se ganó.** Si cambia la estructura de almacenamiento (por ejemplo, pasar el arreglo
a un `ArrayList`) se toca `HistorialFacturas` y no `Cliente`. Si cambia la política de
crédito se toca `Cliente` y no el almacenamiento.
**Qué se pagó.** Una clase más y una excepción más que `Main` debe capturar.

---

## Problema 2 – DIP en la persistencia

**Síntoma.** `Main` creaba y usaba directamente la clase concreta `GestorArchivos`
(`new GestorArchivos()`). El resto del sistema quedaba atado a que los datos se guarden
en archivos de texto, y no había forma de cambiar eso sin tocar a quien lo usa.

**Antes**
```java
GestorArchivos gestor = new GestorArchivos();
gestor.guardar("facturas.txt", facturasAGuardar);
```

**Después**
```java
public interface AlmacenamientoDatos {
    void guardar(String ruta, List<Exportable> lista);
    List<String> leer(String ruta);
}

public class GestorArchivos implements AlmacenamientoDatos { ... }

// En Main (único lugar que elige la implementación concreta)
AlmacenamientoDatos gestor = new GestorArchivos();
```

**Qué se ganó.** Quien guarda datos conoce un contrato (`AlmacenamientoDatos`) y no la
tecnología. Cambiar a otro formato o a una base de datos es crear otra implementación.
**Qué se pagó.** Una interfaz más. Hoy la interfaz tiene una sola implementación, por
eso el beneficio completo aparece cuando la capa de servicio la reciba por constructor
(ver "Coordinación").

---

## Problema 3 – SRP en `Factura`

**Síntoma.** `Factura` mezclaba tres motivos de cambio: las reglas de negocio (items,
total, validación del pago), el texto que se muestra en pantalla y el formato de la línea
que se guarda en archivo.

**Antes**
```java
@Override
public void mostrarDetalle() {
    System.out.println("===== Factura N° " + numero + " =====");
    ... // 10 líneas más armando el texto
}

@Override
public String aLineaTexto() {
    return numero + ";" + fechaEmision + ";" + cliente.getNombre() + ";" + ...;
}
```

**Después**
```java
@Override
public void mostrarDetalle() { System.out.print(formateador.detalle(this)); }

@Override
public String aLineaTexto() { return formateador.lineaArchivo(this); }
```
Clase nueva: `FormateadorFactura`, que arma los dos textos. `Pago` pasó a tener
`describir()`, que devuelve su texto, y `mostrarDetalle()` lo imprime.
`Factura` sigue cumpliendo `Imprimible` y `Exportable` (requisito de la AE2).

**Qué se ganó.** Un cambio en el formato del archivo o del texto impreso se hace en
`FormateadorFactura`; las reglas de negocio de `Factura` no se tocan.
**Qué se pagó.** `Factura` ahora expone getters (el arreglo de items se entrega como copia)
y `Factura` crea su propio `FormateadorFactura`. Se decidió no inyectarlo ni definirle una
interfaz: es una ayuda interna sin detalles técnicos que cambiar (no es un archivo ni una base de
datos) y hoy tiene una sola implementación; agregar la abstracción sería sobrediseño. Si en el
futuro hubiera más de un formato, ahí sí convendría una interfaz y recibirlo por constructor.

---

## Principios revisados sin cambios

- **OCP.** No hay `if/else` ni `switch` por tipo (`categoria`, `puesto`, `metodoPago` y
  `tipo` son datos, no comportamiento). Introducir una abstracción sin comportamiento que
  varíe sería artificial. Si el grupo agrega descuentos por categoría o recargos por forma
  de pago, ese es el lugar donde aplica (y donde encaja Strategy).
- **LSP.** `Producto.getSubtotal()` y `Servicio.getSubtotal()` cumplen el contrato de
  `ItemFacturable`; ninguna subclase restringe ni cambia reglas de su base.
- **ISP.** `Imprimible` y `Exportable` son contratos de un solo método.

## Coordinación con el compañero (capas y patrones)

- **Presentación:** `Factura.mostrarDetalle()` y `Persona.mostrarInfo()` todavía imprimen
  por consola, y la capa de presentación debería encargarse de eso. `FormateadorFactura`
  ya arma el texto, así que puede reutilizarse.
- **Persistencia:** la interfaz `AlmacenamientoDatos` es el contrato; la capa de servicio
  debería recibirla por constructor y `Main` pasarle `new GestorArchivos()`.
- **Paquetes:** conviene que mueva las clases a paquetes sobre esta rama, para no pisar
  estos cambios.
- **Strategy:** si se usa para descuentos o recargos, evitar contarlo dos veces como OCP.

## Pendiente (no toca SOLID)

- `GestorArchivos` captura la `IOException` y escribe en consola (candidato a mejorar al
  separar capas).
- `Factura.agregarItem()` ignora en silencio el item 11 (mismo problema que tenía el historial).
- `Departamento.agregarEmpleado()` y `Proveedor.agregarProducto()` avisan por consola cuando su
  arreglo está lleno, con el mismo patrón que tenía `Cliente`. No se cambió para no ampliar el
  alcance; es el mismo arreglo (excepción en lugar de `println`) si la cátedra lo pide.
- `Persona.mostrarInfo()`, `Pago.mostrarDetalle()` y `Factura.mostrarDetalle()` siguen
  imprimiendo porque así lo exigen `Imprimible` y la AE2; moverlos es tema de la capa de presentación.
- Actualizar el diagrama UML: nuevas clases `HistorialFacturas`, `HistorialLlenoException`,
  `AlmacenamientoDatos` y `FormateadorFactura`.
