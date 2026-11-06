package com.forkilia.modelo;

public class RecetaIngrediente {

    private String idReceta;
    private String idIngrediente;
    private String nombreIngrediente; // solo para mostrar, viene de un JOIN con Ingredients
    private double cantidad;
    private String unidad;
    private double costoProporcional; // lo que costó usar esta cantidad exacta en la receta

    public RecetaIngrediente() {
    }

    public String getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(String idReceta) {
        this.idReceta = idReceta;
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

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public double getCostoProporcional() {
        return costoProporcional;
    }

    public void setCostoProporcional(double costoProporcional) {
        this.costoProporcional = costoProporcional;
    }
}