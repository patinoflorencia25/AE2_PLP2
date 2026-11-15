package com.example;

/**
 * Departamento de la empresa, gestionado por un responsable y con un conjunto de empleados.
 */
public class Departamento {

    private static final int CAPACIDAD_EMPLEADOS = 20;

    private String nombre;
    private double presupuesto; 
    private Empleado responsable; // Empleado responsable del departamento
    private Empleado[] empleados;
    private int cantidadEmpleados;

    public Departamento(String nombre, double presupuesto) {
        this.nombre = nombre;
        this.presupuesto = presupuesto;
        this.empleados = new Empleado[CAPACIDAD_EMPLEADOS];
        this.cantidadEmpleados = 0;
    }

    public void asignarResponsable(Empleado empleado) {
        this.responsable = empleado;
    }

    public void agregarEmpleado(Empleado empleado) {
        if (cantidadEmpleados >= empleados.length) {
            throw new CapacidadExcedidaException("No se puede agregar el empleado: el departamento "
                    + nombre + " ya tiene el máximo de " + CAPACIDAD_EMPLEADOS + " empleados.");
        }
        empleados[cantidadEmpleados] = empleado;
        cantidadEmpleados++;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPresupuesto() {
        return presupuesto;
    }

    public Empleado getResponsable() {
        return responsable;
    }

    public Empleado[] getEmpleados() {
        return empleados;
    }

    public int getCantidadEmpleados() {
        return cantidadEmpleados;
    }
}
