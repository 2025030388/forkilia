package com.forkilia.modelo;

public class Ingrediente {

    // Datos de la tabla Ingredients
    private String idIngrediente;
    private String idUsuario;
    private String nombre;
    private String unidadBase;

    // Datos de la tabla Inventario
    private String idInventario;
    private double stockActual;
    private double stockMinimo;
    private double costoUnitario; // costo por 1 unidad de la unidadBase (ej: costo por gramo)

    public Ingrediente() {
    }

    // Getters y Setters
    public String getIdIngrediente() {
        return idIngrediente;
    }

    public void setIdIngrediente(String idIngrediente) {
        this.idIngrediente = idIngrediente;
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

    public String getUnidadBase() {
        return unidadBase;
    }

    public void setUnidadBase(String unidadBase) {
        this.unidadBase = unidadBase;
    }

    public String getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(String idInventario) {
        this.idInventario = idInventario;
    }

    public double getStockActual() {
        return stockActual;
    }

    public void setStockActual(double stockActual) {
        this.stockActual = stockActual;
    }

    public double getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(double stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public double getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(double costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    // Método útil: valor total de este ingrediente en inventario (stock * costo unitario)
    public double calcularValorTotal() {
        return this.stockActual * this.costoUnitario;
    }

    // Método útil: saber si está en estado crítico o bajo
    public String calcularEstado() {
        if (stockActual <= 0) {
            return "Crítico";
        } else if (stockActual <= stockMinimo) {
            return "Bajo";
        } else {
            return "OK";
        }
    }
}