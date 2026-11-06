package com.forkilia.modelo;

public class PedidoDetalleItem {

    private String idReceta;
    private String nombreReceta; // solo para mostrar, viene de un JOIN con Recetas
    private int cantidad;
    private double precioUnitario;

    public PedidoDetalleItem() {
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

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double calcularSubtotal() {
        return this.cantidad * this.precioUnitario;
    }
}