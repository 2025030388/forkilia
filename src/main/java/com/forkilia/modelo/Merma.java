package com.forkilia.modelo;

import java.time.LocalDateTime;

public class Merma {

    private String idMerma;
    private String idUsuario;
    private String idIngrediente;
    private String nombreIngrediente; // solo para mostrar, viene de un JOIN con Ingredients
    private String categoria;          // se calcula en el servidor, no viene de una columna de la BD
    private String idReceta;           // puede ser null (merma sin venir de una receta cocinada)
    private String nombreReceta;       // solo para mostrar
    private double cantidadPerdida;
    private double costoUnitario;
    private double costoTotal;
    private LocalDateTime fecha;

    public Merma() {
    }

    public String getIdMerma() {
        return idMerma;
    }

    public void setIdMerma(String idMerma) {
        this.idMerma = idMerma;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdIngrediente() {
        return idIngrediente;
    }

    public void setIdIngrediente(String idIngrediente) {
        this.idIngrediente = idIngrediente;
    }

    public String getNombreIngrediente() {
        return nombreIngrediente;
    }

    public void setNombreIngrediente(String nombreIngrediente) {
        this.nombreIngrediente = nombreIngrediente;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(String idReceta) {
        this.idReceta = idReceta;
    }

    public String getNombreReceta() {
        return nombreReceta;
    }

    public void setNombreReceta(String nombreReceta) {
        this.nombreReceta = nombreReceta;
    }

    public double getCantidadPerdida() {
        return cantidadPerdida;
    }

    public void setCantidadPerdida(double cantidadPerdida) {
        this.cantidadPerdida = cantidadPerdida;
    }

    public double getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(double costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    public double getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(double costoTotal) {
        this.costoTotal = costoTotal;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}