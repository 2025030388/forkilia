package com.forkilia.dao;

import com.forkilia.conexion.ConexionBD;
import com.forkilia.modelo.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAOImpl implements CategoriaDAO {

    @Override
    public void crear(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO Categorias (ID_Categoria, ID_Usuario, Nombre) VALUES (?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, categoria.getIdCategoria());
            ps.setString(2, categoria.getIdUsuario());
            ps.setString(3, categoria.getNombre());

            ps.executeUpdate();
        }
    }

    @Override
    public List<Categoria> listarPorUsuario(String idUsuario) throws SQLException {
        String sql = "SELECT ID_Categoria, ID_Usuario, Nombre FROM Categorias "
                   + "WHERE ID_Usuario = ? ORDER BY Nombre";

        List<Categoria> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCategoria(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public void actualizar(Categoria categoria) throws SQLException {
        String sql = "UPDATE Categorias SET Nombre = ? WHERE ID_Categoria = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getIdCategoria());

            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(String idCategoria) throws SQLException {
        String sql = "DELETE FROM Categorias WHERE ID_Categoria = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idCategoria);
            ps.executeUpdate();
        }
    }

    @Override
    public boolean existeNombreParaUsuario(String idUsuario, String nombre) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Categorias WHERE ID_Usuario = ? AND Nombre = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idUsuario);
            ps.setString(2, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    @Override
    public String obtenerSiguienteId() throws SQLException {
        String sql = "SELECT ID_Categoria FROM Categorias";
        int maxNumero = 0;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("ID_Categoria");
                int numero = Integer.parseInt(id.substring(1));
                if (numero > maxNumero) {
                    maxNumero = numero;
                }
            }
        }

        int siguiente = maxNumero + 1;
        return String.format("C%03d", siguiente);
    }

    private Categoria mapearCategoria(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.setIdCategoria(rs.getString("ID_Categoria"));
        c.setIdUsuario(rs.getString("ID_Usuario"));
        c.setNombre(rs.getString("Nombre"));
        return c;
    }
}