package com.forkilia.modelo;

public class Cliente {

    private String idCliente;
    private String idUsuario;
    private String nombre;
    private String telefono;
    private String direccion;
    private String empresa;       // opcional: solo si el cliente es una empresa/evento
    private Integer numPersonas;  // opcional: Integer (no int) para poder ser null

    public Cliente() {
    }

    public Cliente(String idCliente, String idUsuario,
            String nombre, String telefono, String direccion) {
        this.idCliente = idCliente;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public Cliente(String idCliente, String idUsuario,
            String nombre, String telefono, String direccion,
            String empresa, Integer numPersonas) {
        this.idCliente = idCliente;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.empresa = empresa;
        this.numPersonas = numPersonas;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public Integer getNumPersonas() {
        return numPersonas;
    }

    public void setNumPersonas(Integer numPersonas) {
        this.numPersonas = numPersonas;
    }
    
    // Método útil: saber si este cliente es una empresa, sin repetir la comparación en todos lados
    public boolean esEmpresa() {
        return empresa != null && !empresa.isBlank();
    }

}