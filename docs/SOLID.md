# AE4 – Revisión del diseño con principios SOLID

**Base común:** AE2 de Patiño Florencia (repositorio `AE2_PLP2`, commit `36db1cf`).
La pareja decidió usar una sola solución como base, y por eso no se integró el proyecto del compañero.
Los cambios están en la rama `solid`, con commits separados por problema.

## Cómo se verificó cada cambio
Después de cada refactorización se compiló el proyecto (Java 17, con todas las advertencias del compilador activadas y sin ninguna)
y se ejecutó `Main`. La salida del programa tiene las mismas 68 líneas que la versión original; solo cambiaron las tildes de los textos.
Además se probaron aparte los casos nuevos (estructuras llenas, errores de archivo y pagos inválidos).
La versión final, con capas y patrones, se probó ejecutando el menú completo con entradas simuladas: casos válidos y de error, guardar, cerrar el programa y recuperar los datos en una ejecución nueva.

---

## Los cinco principios y dónde aparecen en este proyecto

| Principio | Idea | En este proyecto |
|---|---|---|
| **S** – Responsabilidad única | Una clase debe tener una sola razón para cambiar. | **Problemas 1 y 2** (violaciones corregidas). |
| **O** – Abierto/cerrado | Se extiende sin modificar el código que ya funciona. | Hoy no hay ningún `if/else` ni `switch` que elija comportamiento según un tipo, así que no hay violación. `GestorArchivos.guardar` ya lo cumple: guarda cualquier `Exportable`. Con los descuentos por categoría de cliente sí apareció la necesidad, y se resolvió con el patrón Strategy (`DescuentoStrategy`, ver `docs/ARQUITECTURA_Y_PATRONES.md`). |
| **L** – Sustitución de Liskov | Una subclase se puede usar donde se espera su base sin romper nada. | Sin violaciones: `Producto` y `Servicio` cumplen el contrato de `ItemFacturable`, y `Cliente`, `Empleado` y `Proveedor` el de `Persona`. |
| **I** – Segregación de interfaces | Interfaces chicas y específicas. | Sin violaciones: `Imprimible` y `Exportable` tienen un solo método y todas sus clases lo usan de verdad. |
| **D** – Inversión de dependencias | Depender de abstracciones, no de clases concretas. | **Problema 3** (violación corregida). |

---

## Problema 1 – SRP en `Cliente`

**Síntoma.** `Cliente.agregarFactura()` tenía tres razones para cambiar: la regla de crédito (negocio),
el manejo del arreglo `Factura[]` (almacenamiento) y un `System.out.println` cuando el historial estaba lleno
(salida por consola). Además, con el historial lleno **la factura se perdía sin avisar**.

**Antes**
```java
public void agregarFactura(Factura factura) throws LimiteCreditoExcedidoException {
    // Total acumulado de las facturas que ya tiene el cliente
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
public void agregarFactura(Factura factura) throws LimiteCreditoExcedidoException {
    double totalAcumulado = historial.calcularTotalAcumulado();
    double totalNuevo = factura.calcularTotal();

    if (totalAcumulado + totalNuevo > limiteCredito) {
        throw new LimiteCreditoExcedidoException(...);
    }

    historial.agregar(factura); // guardar es responsabilidad del historial
}

// HistorialFacturas: arreglo, contador y total acumulado
public void agregar(Factura factura) {
    if (cantidad >= facturas.length) {
        throw new CapacidadExcedidaException("No se puede agregar la factura: historial completo (20 facturas).");
    }
    facturas[cantidad] = factura;
    cantidad++;
}
```
Clases nuevas: `HistorialFacturas` y `CapacidadExcedidaException`.

**Qué se ganó.** Si cambia la estructura de almacenamiento (por ejemplo, pasar el arreglo a un `ArrayList`) se modifica
`HistorialFacturas` y no `Cliente`. Si cambia la política de crédito se modifica `Cliente` y no el almacenamiento.
Y la factura ya no se pierde sin avisar.
**Qué se pagó.** Una clase más.

**Para explicarlo en el coloquio.** Pregunta de control: ¿quién pediría un cambio en este método? El negocio (política de crédito),
un cambio técnico (estructura de datos) y un cambio de comunicación (cómo se avisa). Tres respuestas distintas = tres responsabilidades.

### Mismo patrón en otras tres clases
El mismo defecto estaba en `Factura.agregarItem()` (perdía el ítem 11 sin avisar) y en
`Departamento.agregarEmpleado()` y `Proveedor.agregarProducto()` (escribían por consola). Las cuatro estructuras
ahora avisan con la misma `CapacidadExcedidaException`. Es una excepción **no verificada** (extiende `IllegalStateException`),
igual que las colecciones con límite de Java: expresa un límite técnico de la estructura, no una regla de negocio como el
límite de crédito, que sí es verificada.

---

## Problema 2 – SRP en `Factura`

**Síntoma.** `Factura` mezclaba tres motivos de cambio: las reglas de negocio (ítems, total, validación del pago),
el texto que se muestra en pantalla y el formato de la línea que se guarda en archivo.

**Antes**
```java
public void mostrarDetalle() {
    System.out.println("===== Factura N° " + numero + " =====");
    System.out.println("Fecha de emision: " + fechaEmision);
    ... // el resto del texto armado dentro de la factura
}

@Override
public String aLineaTexto() {
    return numero + ";" + fechaEmision + ";" + cliente.getNombre() + ";"
            + empleado.getNombre() + ";" + calcularTotal();
}
```

**Después**
```java
@Override
public void mostrarDetalle() { System.out.print(formateador.detalle(this)); }

@Override
public String aLineaTexto() { return formateador.lineaArchivo(this); }
```
Clase nueva: `FormateadorFactura`, que arma los dos textos. `Pago` pasó a tener `describir()`, que devuelve su texto, y
`mostrarDetalle()` lo imprime. `Factura` sigue cumpliendo `Imprimible` y `Exportable` (requisito de la AE2).

**Qué se ganó.** Un cambio en el formato del archivo o del texto impreso se hace en `FormateadorFactura`; las reglas de negocio no se tocan.
**Qué se pagó.** `Factura` ahora expone getters (los ítems se entregan como una copia, el arreglo interno no se expone) y crea su propio `FormateadorFactura`.
No se inyectó ni se le definió una interfaz: es una ayuda interna sin detalles técnicos que cambiar (no es un archivo ni una base de datos)
y tiene una sola implementación, así que agregar la abstracción sería sobrediseño. Si hubiera más de un formato, ahí sí convendría.

---

## Problema 3 – DIP en la persistencia

**Síntoma.** `Main` creaba y usaba directamente la clase concreta `GestorArchivos` (`new GestorArchivos()`), así que el resto del
sistema quedaba atado a que los datos se guarden en archivos de texto. Además, `GestorArchivos` capturaba la `IOException` y
escribía el error por consola: quien lo usaba nunca se enteraba de que el guardado había fallado, y la lógica no podía reaccionar.

**Antes**
```java
GestorArchivos gestor = new GestorArchivos();
gestor.guardar("facturas.txt", facturasAGuardar);

// dentro de GestorArchivos
} catch (IOException e) {
    System.out.println("ERROR al guardar el archivo " + ruta + ": " + e.getMessage());
}
```

**Después**
```java
public interface AlmacenamientoDatos {
    void guardar(String ruta, List<Exportable> lista) throws PersistenciaException;
    List<String> leer(String ruta) throws PersistenciaException;
}

public class GestorArchivos implements AlmacenamientoDatos {
    ...
    } catch (IOException e) {
        throw new PersistenciaException("No se pudo guardar el archivo " + ruta + ". ...", e);
    }
}

// En Main (único lugar que elige la implementación concreta)
AlmacenamientoDatos gestor = new GestorArchivos();
```
Clases nuevas: `AlmacenamientoDatos` (interfaz) y `PersistenciaException`. `GestorArchivos` ya no escribe en consola y usa UTF-8
(antes dependía de la configuración de cada computadora).

**Por qué `PersistenciaException` y no `IOException`.** Si la interfaz declarara `IOException`, quedaría atada a los archivos, que es
justo lo que el principio quiere evitar. La excepción propia permite cambiar la tecnología sin tocar a quien usa la interfaz.

**Qué se ganó.** Quien guarda datos conoce un contrato y no la tecnología: pasar a otro formato o a una base de datos es crear otra
implementación. Y el error llega a quien puede decidir qué hacer con él.
**Qué se pagó.** Una interfaz y una excepción más. Con la arquitectura en capas, `SistemaFacturacion` (capa de servicio) recibe `AlmacenamientoDatos` por constructor y `Main` solo elige la implementación concreta (`new SistemaFacturacion(new GestorArchivos())`). Así la lógica del negocio ya no depende de cómo se guardan los datos.

---

## Principios revisados sin cambios
- **OCP.** No hay `if/else` ni `switch` por tipo (`categoria`, `puesto`, `metodoPago` y `tipo` son datos, no comportamiento).
  Introducir una abstracción sin comportamiento que varíe sería artificial.
- **LSP.** `Producto.getSubtotal()` y `Servicio.getSubtotal()` cumplen el contrato de `ItemFacturable`; ninguna subclase restringe ni cambia reglas de su base.
- **ISP.** `Imprimible` y `Exportable` son contratos de un solo método.

## Integración con las capas y los patrones
Con la versión final del proyecto, lo que había quedado para coordinar se resolvió así:
- **Persistencia:** `SistemaFacturacion` recibe `AlmacenamientoDatos` por constructor y el menú captura `PersistenciaException`.
- **Presentación:** el menú de consola es la capa de presentación. `Factura.mostrarDetalle()`, `Pago.mostrarDetalle()` y `Persona.mostrarInfo()` siguen imprimiendo porque así lo exigen `Imprimible` y la AE2; el menú usa `FormateadorFactura` para mostrar las facturas.
- **Errores:** el menú captura todos los errores esperados, incluida `CapacidadExcedidaException` (no verificada), y muestra el mensaje sin cortar el programa.
- **Paquetes:** el proyecto quedó dividido en `presentacion`, `servicio`, `dominio` (con `descuento`, `fabrica` y `excepcion`) y `persistencia`.
- **Strategy:** resuelve los descuentos por categoría, que era el caso de OCP que había quedado abierto; no se cuenta como un problema de SOLID aparte.
- **Clases nuevas de esta parte para el diagrama UML:** `HistorialFacturas`, `FormateadorFactura`, `AlmacenamientoDatos`, `CapacidadExcedidaException`, `PagoInvalidoException` y `PersistenciaException`.
