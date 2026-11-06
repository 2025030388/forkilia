package com.forkilia.dao;

import com.forkilia.conexion.ConexionBD;
import com.forkilia.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Usuario buscarPorUsername(String username) throws SQLException {
        String sql = "SELECT * FROM Usuarios WHERE Username = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                } else {
                    return null;
                }
            }
        }
    }

    @Override
    public Usuario buscarPorId(String idUsuario) throws SQLException {
        String sql = "SELECT * FROM Usuarios WHERE ID_Usuario = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                } else {
                    return null;
                }
            }
        }
    }

    @Override
    public void crear(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO Usuarios (ID_Usuario, Username, Password, Email, Nombre, Rol, Fecha_Creacion) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getIdUsuario());
            ps.setString(2, usuario.getUsername());
            ps.setString(3, usuario.getPassword());
            ps.setString(4, usuario.getEmail());
            ps.setString(5, usuario.getNombre());
            ps.setString(6, usuario.getRol());
            ps.setDate(7, java.sql.Date.valueOf(usuario.getFechaCreacion()));

            ps.executeUpdate();
        }
    }

    @Override
    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT * FROM Usuarios";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        }
        return usuarios;
    }

    @Override
    public void actualizar(Usuario usuario) throws SQLException {
        String sql;
        boolean actualizaPassword = usuario.getPassword() != null && !usuario.getPassword().isBlank();

        if (actualizaPassword) {
            sql = "UPDATE Usuarios SET Username = ?, Email = ?, Nombre = ?, Rol = ?, Password = ? WHERE ID_Usuario = ?";
        } else {
            sql = "UPDATE Usuarios SET Username = ?, Email = ?, Nombre = ?, Rol = ? WHERE ID_Usuario = ?";
        }

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getNombre());
            ps.setString(4, usuario.getRol());

            if (actualizaPassword) {
                ps.setString(5, usuario.getPassword());
                ps.setString(6, usuario.getIdUsuario());
            } else {
                ps.setString(5, usuario.getIdUsuario());
            }

            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(String idUsuario) throws SQLException {
        String sql = "DELETE FROM Usuarios WHERE ID_Usuario = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idUsuario);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizarActivo(String idUsuario, boolean activo) throws SQLException {
        String sql = "UPDATE Usuarios SET Activo = ? WHERE ID_Usuario = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setString(2, idUsuario);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizarDatosNegocio(String idUsuario, String nombreNegocio, String direccion,
                                        String telefonoNegocio, String correoContacto) throws SQLException {
        String sql = "UPDATE Usuarios SET NombreNegocio = ?, Direccion = ?, TelefonoNegocio = ?, CorreoContacto = ? "
                    + "WHERE ID_Usuario = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombreNegocio);
            ps.setString(2, direccion);
            ps.setString(3, telefonoNegocio);
            ps.setString(4, correoContacto);
            ps.setString(5, idUsuario);

            ps.executeUpdate();
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getString("ID_Usuario"));
        u.setUsername(rs.getString("Username"));
        u.setPassword(rs.getString("Password"));
        u.setEmail(rs.getString("Email"));
        u.setNombre(rs.getString("Nombre"));
        u.setRol(rs.getString("Rol"));
        java.sql.Date fechaCreacionDB = rs.getDate("Fecha_Creacion");
        u.setFechaCreacion(fechaCreacionDB != null ? fechaCreacionDB.toLocalDate() : null);
        u.setActivo(rs.getBoolean("Activo"));
        u.setNombreNegocio(rs.getString("NombreNegocio"));
        u.setDireccion(rs.getString("Direccion"));
        u.setTelefonoNegocio(rs.getString("TelefonoNegocio"));
        u.setCorreoContacto(rs.getString("CorreoContacto"));
        return u;
    }
}