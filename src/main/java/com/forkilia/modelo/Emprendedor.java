package com.forkilia.modelo;

/**
 * Clase HIJA de Usuario (herencia).
 * Representa al rol Emprendedor: el dueño de un negocio de cocina que
 * opera Forkilia día a día (inventario, recetas, pedidos, mermas).
 */
public class Emprendedor extends Usuario {

    // Atributo propio de esta clase hija (no existe en la clase padre Usuario)
    private String tipoNegocio = "Cocina independiente";

    public Emprendedor() {
        super();
        setRol("Emprendedor");
    }

    // Constructor que copia los datos de un Usuario ya existente
    // (se usa justo después del login, cuando ya sabemos que su rol es Emprendedor)
    public Emprendedor(Usuario base) {
        super();
        setIdUsuario(base.getIdUsuario());
        setUsername(base.getUsername());
        setPassword(base.getPassword());
        setEmail(base.getEmail());
        setNombre(base.getNombre());
        setRol(base.getRol());
        setFechaCreacion(base.getFechaCreacion());
        setActivo(base.isActivo());
        setNombreNegocio(base.getNombreNegocio());
        setDireccion(base.getDireccion());
        setTelefonoNegocio(base.getTelefonoNegocio());
        setCorreoContacto(base.getCorreoContacto());
    }

    public String getTipoNegocio() {
        return tipoNegocio;
    }

    public void setTipoNegocio(String tipoNegocio) {
        this.tipoNegocio = tipoNegocio;
    }

    // Método propio de esta clase hija: SOBREESCRIBE (@Override) el de la clase padre.
    // Esto es polimorfismo: mismo nombre de método, comportamiento distinto según la clase.
    @Override
    public String panelPrincipal() {
        return "Dashboard operativo: gestión de mi negocio (inventario, recetas, pedidos, mermas)";
    }
}