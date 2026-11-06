package com.forkilia.modelo;

public class Categoria {

    private String idCategoria;
    private String idUsuario;
    private String nombre;

    public Categoria() {
    }

    public Categoria(String idCategoria, String idUsuario, String nombre) {
        this.idCategoria = idCategoria;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
    }

    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String idCategoria) {
        this.idCategoria = idCategoria;
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
}