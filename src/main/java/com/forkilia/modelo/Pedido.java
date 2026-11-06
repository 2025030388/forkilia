package com.forkilia.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {

    private String idPedido;
    private String folio;
    private String idCliente;
    private String nombreCliente;   // solo para mostrar, viene de un JOIN con Clientes
    private String telefonoCliente; // solo para mostrar
    private String idUsuario;
    private String tipo;            // "local", "recoleccion" o "domicilio"
    private String mesa;
    private double total;
    private double anticipo;
    private String status;          // "pendiente", "preparando", "listo" o "entregado"
    private String notas;
    private LocalDateTime fecha;

    private List<PedidoDetalleItem> detalle = new ArrayList<>();

    public Pedido() {
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMesa() {
        return mesa;
    }

    public void setMesa(String mesa) {
        this.mesa = mesa;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getAnticipo() {
        return anticipo;
    }

    public void setAnticipo(double anticipo) {
        this.anticipo = anticipo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public List<PedidoDetalleItem> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<PedidoDetalleItem> detalle) {
        this.detalle = detalle;
    }

    public double calcularRestante() {
        return this.total - this.anticipo;
    }
    private boolean descuentoAplicado; // true si se aplicó el 10% de cliente empresa

public boolean isDescuentoAplicado() {
    return descuentoAplicado;
}

public void setDescuentoAplicado(boolean descuentoAplicado) {
    this.descuentoAplicado = descuentoAplicado;
}
}