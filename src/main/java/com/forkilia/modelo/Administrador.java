package com.forkilia.modelo;

public class Administrador extends Usuario 
{

    // Atributo propio de esta clase hija (no existe en la clase padre Usuario)
    private String nivelAcceso = "Total";

    public Administrador() {
        super();
        setRol("Admin");
    }

    // Constructor que copia los datos de un Usuario ya existente
    // (se usa justo después del login, cuando ya sabemos que su rol es Admin)
    public Administrador(Usuario base) {
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

    public String getNivelAcceso() {
        return nivelAcceso;
    }

    public void setNivelAcceso(String nivelAcceso) {
        this.nivelAcceso = nivelAcceso;
    }

    // Método propio de esta clase hija: SOBREESCRIBE (@Override) el de la clase padre.
    // Esto es polimorfismo: mismo nombre de método, comportamiento distinto según la clase.
    @Override
    public String panelPrincipal() {
        return "Panel de Administración: gestión de cuentas de Emprendedores";
    }
}