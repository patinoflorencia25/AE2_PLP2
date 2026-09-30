package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Se encarga unicamente de guardar y leer informacion en archivos de texto.
 * Las clases del dominio no manejan archivos: solo saben convertirse en una
 * linea de texto (interfaz Exportable).
 */

public class GestorArchivos {

    // Guarda una linea por cada objeto Exportable de la lista
    public void guardar(String ruta, List<Exportable> lista) {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(ruta))) {
            for (Exportable elemento : lista) {
                escritor.write(elemento.aLineaTexto());
                escritor.newLine();
            }
            System.out.println("Se guardaron " + lista.size() + " registros en " + ruta);
        } catch (IOException e) {
            System.out.println("ERROR al guardar el archivo " + ruta + ": " + e.getMessage());
        }
    }

    // Lee el archivo y devuelve sus lineas
    public List<String> leer(String ruta) {
        List<String> lineas = new ArrayList<>();
        try (BufferedReader lector = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                lineas.add(linea);
            }
        } catch (IOException e) {
            System.out.println("ERROR al leer el archivo " + ruta + ": " + e.getMessage());
        }
        return lineas;
    }
}