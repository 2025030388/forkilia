package com.forkilia.dao;

import com.forkilia.conexion.ConexionBD;
import com.forkilia.modelo.Ingrediente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IngredienteDAOImpl implements IngredienteDAO {

    @Override
    public void crear(Ingrediente ingrediente) throws SQLException {
        String sqlIngrediente = "INSERT INTO Ingredients (ID_Ingrediente, ID_Usuario, Nombre, Unidad_Base) VALUES (?, ?, ?, ?)";
        String sqlInventario = "INSERT INTO Inventario (ID_Inventario, ID_Ingrediente, Stock_Actual, Stock_Minimo, Costo_Unitario) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false); // vamos a insertar en 2 tablas, deben tener éxito juntas o ninguna

            try {
                try (PreparedStatement ps1 = con.prepareStatement(sqlIngrediente)) {
                    ps1.setString(1, ingrediente.getIdIngrediente());
                    ps1.setString(2, ingrediente.getIdUsuario());
                    ps1.setString(3, ingrediente.getNombre());
                    ps1.setString(4, ingrediente.getUnidadBase());
                    ps1.executeUpdate();
                }

                try (PreparedStatement ps2 = con.prepareStatement(sqlInventario)) {
                    ps2.setString(1, ingrediente.getIdInventario());
                    ps2.setString(2, ingrediente.getIdIngrediente());
                    ps2.setDouble(3, ingrediente.getStockActual());
                    ps2.setDouble(4, ingrediente.getStockMinimo());
                    ps2.setDouble(5, ingrediente.getCostoUnitario());
                    ps2.executeUpdate();
                }

                con.commit(); // ambas tablas se guardaron bien, confirmamos los cambios

            } catch (SQLException e) {
                con.rollback(); // algo falló: deshacemos lo que sí se alcanzó a insertar
                throw e;
            }
        }
    }

    @Override
    public List<Ingrediente> listarPorUsuario(String idUsuario) throws SQLException {
        String sql = "SELECT i.ID_Ingrediente, i.ID_Usuario, i.Nombre, i.Unidad_Base, "
                    + "inv.ID_Inventario, inv.Stock_Actual, inv.Stock_Minimo, inv.Costo_Unitario "
                    + "FROM Ingredients i "
                    + "JOIN Inventario inv ON i.ID_Ingrediente = inv.ID_Ingrediente "
                    + "WHERE i.ID_Usuario = ? "
                    + "ORDER BY i.Nombre";

        List<Ingrediente> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearIngrediente(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public void actualizar(Ingrediente ingrediente) throws SQLException {
        String sqlIngrediente = "UPDATE Ingredients SET Nombre = ?, Unidad_Base = ? WHERE ID_Ingrediente = ?";
        String sqlInventario = "UPDATE Inventario SET Stock_Actual = ?, Stock_Minimo = ?, Costo_Unitario = ? WHERE ID_Ingrediente = ?";

        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps1 = con.prepareStatement(sqlIngrediente)) {
                    ps1.setString(1, ingrediente.getNombre());
                    ps1.setString(2, ingrediente.getUnidadBase());
                    ps1.setString(3, ingrediente.getIdIngrediente());
                    ps1.executeUpdate();
                }

                try (PreparedStatement ps2 = con.prepareStatement(sqlInventario)) {
                    ps2.setDouble(1, ingrediente.getStockActual());
                    ps2.setDouble(2, ingrediente.getStockMinimo());
                    ps2.setDouble(3, ingrediente.getCostoUnitario());
                    ps2.setString(4, ingrediente.getIdIngrediente());
                    ps2.executeUpdate();
                }

                con.commit();

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    @Override
    public void eliminar(String idIngrediente) throws SQLException {
        String sql = "DELETE FROM Ingredients WHERE ID_Ingrediente = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idIngrediente);
            ps.executeUpdate();
        }
    }

    @Override
    public String obtenerSiguienteId() throws SQLException {
        String sql = "SELECT ID_Ingrediente FROM Ingredients";
        int maxNumero = 0;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("ID_Ingrediente"); // formato "I001"
                int numero = Integer.parseInt(id.substring(1));
                if (numero > maxNumero) {
                    maxNumero = numero;
                }
            }
        }

        return String.format("I%03d", maxNumero + 1);
    }

    @Override
    public String obtenerSiguienteIdInventario() throws SQLException {
        String sql = "SELECT ID_Inventario FROM Inventario";
        int maxNumero = 0;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("ID_Inventario"); // formato "INV001"
                int numero = Integer.parseInt(id.substring(3));
                if (numero > maxNumero) {
                    maxNumero = numero;
                }
            }
        }

        return String.format("INV%03d", maxNumero + 1);
    }

    private Ingrediente mapearIngrediente(ResultSet rs) throws SQLException {
        Ingrediente ing = new Ingrediente();
        ing.setIdIngrediente(rs.getString("ID_Ingrediente"));
        ing.setIdUsuario(rs.getString("ID_Usuario"));
        ing.setNombre(rs.getString("Nombre"));
        ing.setUnidadBase(rs.getString("Unidad_Base"));
        ing.setIdInventario(rs.getString("ID_Inventario"));
        ing.setStockActual(rs.getDouble("Stock_Actual"));
        ing.setStockMinimo(rs.getDouble("Stock_Minimo"));
        ing.setCostoUnitario(rs.getDouble("Costo_Unitario"));
        return ing;
    }
}