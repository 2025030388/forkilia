package com.forkilia.modelo;

import java.util.ArrayList;
import java.util.List;

public class Receta {

    private String idReceta;
    private String idUsuario;
    private String idCategoria;
    private String nombreCategoria; // solo para mostrar, no se guarda en Recetas
    private String nombre;
    private int rendimiento;
    private int tiempoPrepMin;
    private double costo;
    private double precio;
    private double margen;
    private String dificultad;
    private String tip;
    private String imagen;
    private String procedimiento;

    // Lista de ingredientes vinculados (se llena solo en la vista de detalle,
    // no viene de una columna de esta tabla sino de Receta_Ingrediente)
    private List<RecetaIngrediente> ingredientes = new ArrayList<>();

    public Receta() {
    }

    public String getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(String idReceta) {
        this.idReceta = idReceta;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getRendimiento() {
        return rendimiento;
    }

    public void setRendimiento(int rendimiento) {
        this.rendimiento = rendimiento;
    }

    public int getTiempoPrepMin() {
        return tiempoPrepMin;
    }

    public void setTiempoPrepMin(int tiempoPrepMin) {
        this.tiempoPrepMin = tiempoPrepMin;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getMargen() {
        return margen;
    }

    public void setMargen(double margen) {
        this.margen = margen;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getProcedimiento() {
        return procedimiento;
    }

    public void setProcedimiento(String procedimiento) {
        this.procedimiento = procedimiento;
    }

    public List<RecetaIngrediente> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<RecetaIngrediente> ingredientes) {
        this.ingredientes = ingredientes;
    }

    // Ganancia neta en dinero: precio de venta menos el costo de producción
    public double calcularGananciaNeta() {
        return this.precio - this.costo;
    }
}