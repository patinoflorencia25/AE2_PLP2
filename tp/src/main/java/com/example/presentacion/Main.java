package com.example.presentacion;

import com.example.persistencia.GestorArchivos;
import com.example.servicio.SistemaFacturacion;

/**
 * Punto de entrada: solo arma el sistema y lo inicia.
 * Es el único lugar que elige la implementación concreta de persistencia (archivos de texto).
 */
public class Main {

    public static void main(String[] args) {
        SistemaFacturacion sistema = new SistemaFacturacion(new GestorArchivos());
        new MenuConsola(sistema, System.in, System.out).iniciar();
    }
}
