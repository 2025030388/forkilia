package com.forkilia.dao;
import com.forkilia.modelo.Usuario;
import java.sql.SQLException;
import java.util.List;
public interface UsuarioDAO 
{
    // Busca un usuario por su username (lo usaremos en el Login)
    Usuario buscarPorUsername(String username) throws SQLException;
    // Crea un nuevo usuario (lo usará el Admin al dar de alta a un Emprendedor)
    void crear(Usuario usuario) throws SQLException;
    // Devuelve todos los usuarios (para que el Admin los liste en su pantalla de "Usuarios")
    List<Usuario> listarTodos() throws SQLException;
    // Actualiza los datos de un usuario existente
    void actualizar(Usuario usuario) throws SQLException;
    // Elimina un usuario por su ID (ya no se usa desde el Servlet, pero se deja disponible)
    void eliminar(String idUsuario) throws SQLException;
    // Activa o desactiva un usuario (no lo borra, preserva su historial)
    void actualizarActivo(String idUsuario, boolean activo) throws SQLException;
    // Actualiza solo los datos del negocio (pantalla de Configuración)
    void actualizarDatosNegocio(String idUsuario, String nombreNegocio, String direccion,
                                 String telefonoNegocio, String correoContacto) throws SQLException;
    // Busca un usuario por su ID (para Mi Perfil / Configuración)
    Usuario buscarPorId(String idUsuario) throws SQLException;
}