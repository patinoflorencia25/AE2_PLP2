package com.example.persistencia;

import com.example.dominio.Exportable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Se encarga únicamente de guardar y leer información en archivos de texto.
 * Las clases del dominio no manejan archivos: solo saben convertirse en una
 * línea de texto (interfaz Exportable).
 * Es la implementación en archivos de texto de AlmacenamientoDatos: no escribe
 * en consola ni oculta los errores, los informa con PersistenciaException.
 * Usa siempre UTF-8, para que las tildes se guarden igual en cualquier computadora.
 */
public class GestorArchivos implements AlmacenamientoDatos {

    // Guarda una línea por cada objeto Exportable de la lista
    @Override
    public void guardar(String ruta, List<Exportable> lista) throws PersistenciaException {
        try (BufferedWriter escritor = Files.newBufferedWriter(Path.of(ruta), StandardCharsets.UTF_8)) {
            for (Exportable elemento : lista) {
                escritor.write(elemento.aLineaTexto());
                escritor.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo guardar el archivo " + ruta
                    + ". Verificá que la carpeta exista y que haya permisos de escritura.", e);
        }
    }

    // Lee el archivo y devuelve sus líneas
    @Override
    public List<String> leer(String ruta) throws PersistenciaException {
        try {
            return Files.readAllLines(Path.of(ruta), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo leer el archivo " + ruta
                    + ". Verificá que exista y que esté guardado en UTF-8.", e);
        }
    }
}
