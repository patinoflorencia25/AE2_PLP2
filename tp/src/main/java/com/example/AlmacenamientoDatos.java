package com.example;

import java.util.List;

/**
 * Contrato de persistencia: guardar una lista de objetos Exportable y leer lo guardado.
 * La lógica del sistema depende de esta interfaz y no de CÓMO se guardan los datos
 * (archivo de texto hoy; base de datos u otro formato mañana) - DIP.
 */
public interface AlmacenamientoDatos {

    void guardar(String ruta, List<Exportable> lista);

    List<String> leer(String ruta);
}
