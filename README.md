# AE2_PLP2 – Sistema de facturación (arquitectura en capas + patrones de diseño)

Base: AE2 con las correcciones SOLID de `docs/SOLID.md`. Esta versión agrega los tres puntos pedidos:
arquitectura en capas, patrones de diseño e interacción por consola.
El detalle y la justificación están en [`docs/ARQUITECTURA_Y_PATRONES.md`](docs/ARQUITECTURA_Y_PATRONES.md).

## Cómo ejecutarlo
Requiere Java 17 o superior. Desde la carpeta `tp`:

```
mvn -q compile exec:java -Dexec.mainClass=com.example.presentacion.Main
```
o, sin Maven:
```
javac -encoding UTF-8 -d out $(find src -name "*.java")      # Linux / macOS
java -cp out com.example.presentacion.Main
```
En Windows (PowerShell): `javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName`.
Los archivos de datos (`clientes.txt`, `items.txt`, `facturas.txt`) se crean en la carpeta desde donde se ejecuta, al elegir "Guardar datos".

## Capas (paquetes de `com.example`)
| Capa | Paquete | Contenido |
|---|---|---|
| Presentación | `presentacion` | `Main` (solo inicia el sistema), `MenuConsola` |
| Servicio | `servicio` | `SistemaFacturacion` (casos de uso), `GeneradorNumeroFactura`, `DescuentosPorCategoria`, `ConversorLineas` |
| Dominio | `dominio`, `dominio.descuento`, `dominio.fabrica`, `dominio.excepcion` | Entidades, reglas de negocio, patrones Strategy y Factory Method, excepciones |
| Persistencia | `persistencia` | `AlmacenamientoDatos` (interfaz), `GestorArchivos` (archivos de texto), `PersistenciaException` |

Dependencias: presentación → servicio → dominio, y servicio → `AlmacenamientoDatos` (interfaz). Solo `Main` conoce `GestorArchivos`.

## Patrones aplicados
| Patrón | Tipo | Dónde | Problema que resuelve |
|---|---|---|---|
| Strategy | Comportamiento | `dominio.descuento` | Descuento según categoría del cliente sin `if/else` en `Factura` |
| Factory Method | Creación | `dominio.fabrica` | Crear `Producto` o `Servicio` según el tipo (menú y lectura de archivos) sin repetir un `if` por tipo |
| Singleton | Creación | `servicio.GeneradorNumeroFactura` | Una sola fuente de numeración correlativa; evita facturas con número repetido |

## Menú
1. Registrar cliente · 2. Registrar producto o servicio · 3. Emitir factura · 4. Registrar pago ·
5. Listar información ordenada · 6. Ver detalle de una factura · 7. Guardar datos · 8. Recuperar datos · 0. Salir

Datos iniciales: un empleado (Juan Pérez), un proveedor y un catálogo de 4 ítems (P001, P002, S001, S002).
