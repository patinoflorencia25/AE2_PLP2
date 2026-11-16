# Arquitectura en capas y patrones de diseño

## 1. Arquitectura en capas
```
presentacion  ──►  servicio  ──►  dominio
   (Main,             │             (entidades, reglas, Strategy, Factory Method)
  MenuConsola)        ▼
               persistencia.AlmacenamientoDatos  (interfaz)
                      ▲
               persistencia.GestorArchivos  (implementación: archivos de texto)
```
- **Presentación.** `MenuConsola` lee la consola, llama al servicio y muestra resultados o errores. No tiene reglas de negocio ni toca archivos.
  `Main` solo crea `SistemaFacturacion(new GestorArchivos())` y arranca el menú; es el único lugar que elige la implementación de persistencia.
- **Servicio.** `SistemaFacturacion` implementa los casos de uso (registrar cliente/ítem, emitir factura, registrar pago, listar ordenado, guardar y recuperar).
  Recibe `AlmacenamientoDatos` por constructor: no sabe si los datos van a archivos, a una base de datos o a memoria.
- **Dominio.** `Cliente`, `Factura`, `Pago`, `ItemFacturable`, etc., con sus reglas (límite de crédito, pago no mayor al total, capacidad de las estructuras).
  No escribe en consola ni accede a archivos (solo `mostrarDetalle()` imprime, porque `Imprimible` de la AE2 lo exige).
- **Persistencia.** Todo `java.io`/`java.nio` está en `GestorArchivos`. El servicio la usa únicamente a través de `AlmacenamientoDatos`.
  La persistencia mueve texto; convertir el texto en objetos es trabajo del servicio (`ConversorLineas`), así que cambiar el formato de almacenamiento no toca el negocio.

Nota de dependencias: `AlmacenamientoDatos` trabaja con `Exportable` (dominio), de modo que persistencia depende del dominio y no al revés.

## 2. Patrones de diseño (síntoma → patrón)

### Strategy – descuento por categoría del cliente
- **Síntoma.** `Cliente.categoria` (Regular / Premium / Corporativo) era un texto que no cambiaba nada. En `docs/SOLID.md` figuraba como caso de OCP "pendiente":
  aplicar descuentos habría obligado a poner un `if/else` por categoría dentro de `Factura`, a modificarlo con cada categoría nueva.
- **Solución.** `DescuentoStrategy` (`calcularDescuento`, `getNombre`) con `SinDescuento` (0 %), `DescuentoPremium` (10 %) y `DescuentoCorporativo` (15 %).
  `Factura` recibe la estrategia y calcula `total = subtotal − descuento` sin conocer las categorías. `DescuentosPorCategoria` elige la estrategia; sumar una categoría es crear una clase y registrarla.

### Factory Method (creación) – ítems facturables
- **Síntoma.** Para crear el catálogo hace falta decidir entre `Producto` y `Servicio`, tanto al registrar desde el menú como al recuperar `items.txt`
  (donde cada línea trae una clave `P` / `S`). Eso significa un `if (tipo)` repetido en cada lugar.
- **Solución.** `CreadorItem` define el proceso (validar y crear) y deja el método fábrica `crear()` a `CreadorProducto` y `CreadorServicio`.
  El servicio guarda un mapa `clave → creador`, así que un tipo nuevo de ítem no obliga a tocar el código que crea.

### Singleton (creación) – numeración de facturas
- **Síntoma.** En la AE2 el número de factura lo tecleaba quien la creaba (`new Factura(1, …)`, `2`, `3`…) y nada impedía repetirlo.
- **Solución.** `GeneradorNumeroFactura.getInstancia()` es la única fuente del número correlativo. Al recuperar datos se reinicia en el mayor número guardado.
  Si una factura se rechaza (por ejemplo, límite de crédito), el número no se consume.

## 3. Decisiones de negocio incluidas
- **Estado del pago calculado.** El sistema decide `Cancelado` (el monto cubre el total) o `Parcial`; ya no es texto libre. Los métodos válidos son Efectivo, Transferencia y Tarjeta. Cada factura admite un solo pago.
- **Recuperar es todo o nada.** Si falta un archivo o una línea es inválida, se informa con `PersistenciaException` y no se modifica el estado en memoria.
- **Campos de texto.** No admiten `;` ni `,` porque son los separadores del formato de archivo.
- **Formatos de archivo (cambiaron respecto de la AE2, para poder recuperar):**
  - `clientes.txt`: `nombre;dni;domicilio;teléfono;categoría;límite`
  - `items.txt`: `clave;código;nombre;precio;tipo`
  - `facturas.txt`: `número;fecha;dniCliente;empleado;códigos(coma);método;monto;estado;fechaPago`

## 4. Qué cambió del Main original
Las demostraciones de la AE2 (polimorfismo, `Imprimible`, `Exportable`, `Comparable`/`Comparator`, excepciones, persistencia) pasaron a ser opciones reales del menú:
los ordenamientos son los listados (`Comparable` por código, `ComparadorPorPrecio` por precio), las excepciones se muestran como mensajes de error y la persistencia es Guardar/Recuperar.
El diagrama `docs/UML_AE2_PATIÑO.png` corresponde a la versión anterior y habría que actualizarlo con los paquetes y las clases nuevas.
