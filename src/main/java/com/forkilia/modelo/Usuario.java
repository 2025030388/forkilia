package com.forkilia.modelo;
import java.time.LocalDate;
public class Usuario 
{
    private String idUsuario;
    private String username;
    private String password;
    private String email;
    private String nombre;
    private String rol;
    private LocalDate fechaCreacion;
    private boolean activo = true;

    // Datos del negocio (para la pantalla de Configuración)
    private String nombreNegocio;
    private String direccion;
    private String telefonoNegocio;
    private String correoContacto;

    // Constructor vacío (necesario para crear un Usuario e ir llenándolo con los setters)
    public Usuario() 
    {
    }
    // Constructor completo (útil para crear un Usuario con todos sus datos de una vez)
    public Usuario(String idUsuario, String username, String password, String email,
                   String nombre, String rol, LocalDate fechaCreacion) 
    {
        this.idUsuario = idUsuario;
        this.username = username;
        this.password = password;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
        this.fechaCreacion = fechaCreacion;
    }
    // Getters y Setters
    public String getIdUsuario()
    {
        return idUsuario;
    }
    public void setIdUsuario(String idUsuario) 
    {
        this.idUsuario = idUsuario;
    }
    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password) 
    {
        this.password = password;
    }
    public String getEmail()
    {
        return email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }
    public String getNombre() 
    {
        return nombre;
    }
    public void setNombre(String nombre) 
    {
        this.nombre = nombre;
    }
    public String getRol() 
    {
        return rol;
    }
    public void setRol(String rol) 
    {
        this.rol = rol;
    }
    public LocalDate getFechaCreacion() 
    {
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDate fechaCreacion) 
    {
        this.fechaCreacion = fechaCreacion;
    }
    public boolean isActivo() 
    {
        return activo;
    }
    public void setActivo(boolean activo) 
    {
        this.activo = activo;
    }
    public String getNombreNegocio() 
    {
        return nombreNegocio;
    }
    public void setNombreNegocio(String nombreNegocio) 
    {
        this.nombreNegocio = nombreNegocio;
    }
    public String getDireccion() 
    {
        return direccion;
    }
    public void setDireccion(String direccion) 
    {
        this.direccion = direccion;
    }
    public String getTelefonoNegocio() 
    {
        return telefonoNegocio;
    }
    public void setTelefonoNegocio(String telefonoNegocio) 
    {
        this.telefonoNegocio = telefonoNegocio;
    }
    public String getCorreoContacto() 
    {
        return correoContacto;
    }
    public void setCorreoContacto(String correoContacto) 
    {
        this.correoContacto = correoContacto;
    }
    // Método útil: saber si este usuario es Admin, sin repetir la comparación de texto en todos lados
    public boolean esAdmin() 
    {
        return "Admin".equalsIgnoreCase(this.rol);
    }

    // Método que las clases hijas (Administrador, Emprendedor) sobreescriben
    // cada una con su propia versión (polimorfismo).
    public String panelPrincipal()
    {
        return "Panel general de Forkilia";
    }
}