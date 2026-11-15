# Errores encontrados en la base (AE1 y AE2) y qué se hizo con cada uno

Revisión del código del commit `36db1cf` antes de empezar el AE4. Todo lo marcado como corregido está en la rama `solid`.

## Corregidos
| Qué estaba mal | Dónde | Cómo se corrigió |
|---|---|---|
| `CAPACIDAD_EMPLEADOS` era `static` pero no `final`, o sea que no era una constante | `Departamento` | Pasó a `static final`. |
| `Factura.agregarItem()` perdía el ítem número 11 sin avisar | `Factura` | Lanza `CapacidadExcedidaException`. |
| El dominio escribía mensajes por consola cuando una estructura se llenaba | `Departamento`, `Proveedor`, `Cliente` | Todos lanzan `CapacidadExcedidaException`. |
| `GestorArchivos` ocultaba los errores de archivo con un `println` y dependía de la codificación de cada computadora | `GestorArchivos` | Lanza `PersistenciaException` y usa UTF-8. El `pom.xml` declara UTF-8 para compilar. |
| `Factura.asignarPago()` aceptaba pagos de monto cero o negativo | `Factura` | Lanza `PagoInvalidoException`. |
| Textos y comentarios sin tildes (`Teléfono`, `Límite de crédito`, `Método`, `Ítems`, etc.) y errores de tipeo (`reular`, `fja`) | Todo el proyecto | Corregidos. |
| Llaves y `@Override` mal indentados, espacios al final de línea | `Cliente`, `Empleado`, `Proveedor`, `Factura`, `ItemFacturable`, `Main` | Corregido el formato. |
| Las excepciones no definían `serialVersionUID` (el compilador avisa) | Excepciones | Agregado. |

## Pendientes: necesitan una decisión de la pareja
Se verificaron con una prueba, pero no se tocaron porque son reglas de negocio que conviene acordar:
1. **Facturas de otro cliente y repetidas.** `Cliente.agregarFactura()` acepta una factura emitida a otro cliente y acepta la misma factura dos veces.
2. **Estado del pago.** `Pago.estado` y `Pago.metodoPago` son texto libre: se puede registrar "Cancelado" con $1 sobre una factura de $1000.
   Candidato para Strategy (recargo por forma de pago) o para que el estado se calcule solo.
3. **Límite de crédito.** Suma todas las facturas del cliente, incluso las ya pagadas (ya figuraba como mejora posible en el informe del AE2).
4. **Subtotal.** `Producto.getSubtotal()` y `Servicio.getSubtotal()` calculan lo mismo; el método abstracto cobra sentido si, por ejemplo, el servicio se cobra por horas.
5. **Nombre de método.** `Persona.getDNI()` no sigue la convención de Java (`getDni()`). No se cambió para no romper el código del compañero.
6. **`Main` extenso.** Tiene todas las demostraciones del AE2; la consigna del AE4 pide que solo inicie el sistema (capa de presentación).
