package com.example.persistencia;

import com.example.dominio.Exportable;

import java.util.List;

/**
 * Contrato de persistencia: guardar una lista de objetos Exportable y leer lo guardado.
 * La lógica del sistema depende de esta interfaz y no de CÓMO se guardan los datos
 * (archivo de texto hoy; base de datos u otro formato mañana) - DIP.
 * Los errores se informan con PersistenciaException, sin exponer detalles técnicos.
 */
public interface AlmacenamientoDatos {

    void guardar(String ruta, List<Exportable> lista) throws PersistenciaException;

    List<String> leer(String ruta) throws PersistenciaException;
}
