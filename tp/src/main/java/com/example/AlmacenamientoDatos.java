package com.example;

import java.util.List;

/**
 * Contrato de persistencia: guardar una lista de objetos Exportable y leer lo guardado.
 * La logica del sistema depende de esta interfaz y no de COMO se guardan los datos
 * (archivo de texto hoy; base de datos u otro formato manana) - DIP.
 */

public interface AlmacenamientoDatos {

    void guardar(String ruta, List<Exportable> lista);

    List<String> leer(String ruta);
}
