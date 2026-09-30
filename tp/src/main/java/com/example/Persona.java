package com.example;

/**
 * Clase abstracta base para toda persona vinculada a la empresa.
 * No se instancia directamente: siempre a través de Cliente, Empleado o Proveedor.
 * Declara dos métodos abstractos que cada subclase implementa a su manera
 * y un método concreto (mostrarInfo) compartido por todas.
 */

public abstract class Persona {
    
    private String nombre;
    private String domicilio;
    private String dni;
    private String telefono;

    public Persona(String nombre, String domicilio, String dni, String telefono){
        this.nombre = nombre;
        this.domicilio = domicilio;
        this.dni = dni;
        this.telefono = telefono;
    }

    // Métodos abstractos: cada subclase los implementa de forma distinta
    public abstract String getRol();
    public abstract String getDatosEspecificos();

    // Método concreto compartido: usa los datos comunes y los abstractos
    public void mostrarInfo() {
        System.out.println("[" + getRol() + "] " + nombre);
        System.out.println("  DNI: " + dni + " | Domicilio: " + domicilio + " | Telefono: " + telefono);
        System.out.println("  " + getDatosEspecificos());
    }

    public String getNombre() {
        return nombre;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public String getDNI() {
        return dni;
    }

    public String getTelefono() {
        return telefono;
    }
}
