package com.forkilia.dao;

import com.forkilia.conexion.ConexionBD;
import com.forkilia.modelo.Merma;
import com.forkilia.servicio.ClasificadorMermaService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MermaDAOImpl implements MermaDAO {

    private final ClasificadorMermaService clasificador = new ClasificadorMermaService();

    @Override
    public void crear(Merma merma) throws SQLException {
        String sqlCosto = "SELECT Costo_Unitario FROM Inventario WHERE ID_Ingrediente = ?";
        String sqlInsertar = "INSERT INTO Mermas "
                            + "(ID_Merma, ID_Usuario, ID_Ingrediente, ID_Receta, Cantidad_Perdida, Costo_Unitario, Costo_Total) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion()) {

            double costoUnitario;
            try (PreparedStatement ps = con.prepareStatement(sqlCosto)) {
                ps.setString(1, merma.getIdIngrediente());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        costoUnitario = rs.getDouble("Costo_Unitario");
                    } else {
                        throw new SQLException("El ingrediente no existe en el inventario");
                    }
                }
            }

            double costoTotal = merma.getCantidadPerdida() * costoUnitario;

            try (PreparedStatement ps = con.prepareStatement(sqlInsertar)) {
                ps.setString(1, merma.getIdMerma());
                ps.setString(2, merma.getIdUsuario());
                ps.setString(3, merma.getIdIngrediente());
                if (merma.getIdReceta() == null || merma.getIdReceta().isBlank()) {
                    ps.setNull(4, java.sql.Types.VARCHAR);
                } else {
                    ps.setString(4, merma.getIdReceta());
                }
                ps.setDouble(5, merma.getCantidadPerdida());
                ps.setDouble(6, costoUnitario);
                ps.setDouble(7, costoTotal);
                ps.executeUpdate();
            }

            merma.setCostoUnitario(costoUnitario);
            merma.setCostoTotal(costoTotal);
        }
    }

    @Override
    public List<Merma> listarPorUsuario(String idUsuario) throws SQLException {
        String sql = "SELECT m.ID_Merma, m.ID_Usuario, m.ID_Ingrediente, i.Nombre AS NombreIngrediente, "
                   + "m.ID_Receta, r.Nombre AS NombreReceta, m.Cantidad_Perdida, m.Costo_Unitario, "
                   + "m.Costo_Total, m.Fecha "
                   + "FROM Mermas m "
                   + "JOIN Ingredients i ON m.ID_Ingrediente = i.ID_Ingrediente "
                   + "LEFT JOIN Recetas r ON m.ID_Receta = r.ID_Receta "
                   + "WHERE m.ID_Usuario = ? "
                   + "ORDER BY m.Fecha DESC";

        List<Merma> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Merma m = new Merma();
                    m.setIdMerma(rs.getString("ID_Merma"));
                    m.setIdUsuario(rs.getString("ID_Usuario"));
                    m.setIdIngrediente(rs.getString("ID_Ingrediente"));
                    m.setNombreIngrediente(rs.getString("NombreIngrediente"));
                    m.setIdReceta(rs.getString("ID_Receta"));
                    m.setNombreReceta(rs.getString("NombreReceta"));
                    m.setCantidadPerdida(rs.getDouble("Cantidad_Perdida"));
                    m.setCostoUnitario(rs.getDouble("Costo_Unitario"));
                    m.setCostoTotal(rs.getDouble("Costo_Total"));

                    Timestamp fecha = rs.getTimestamp("Fecha");
                    if (fecha != null) {
                        m.setFecha(fecha.toLocalDateTime());
                    }

                    m.setCategoria(clasificador.clasificar(m.getNombreIngrediente()));

                    lista.add(m);
                }
            }
        }
        return lista;
    }

    @Override
    public void eliminar(String idMerma) throws SQLException {
        String sql = "DELETE FROM Mermas WHERE ID_Merma = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idMerma);
            ps.executeUpdate();
        }
    }

    @Override
    public String obtenerSiguienteId() throws SQLException {
        String sql = "SELECT ID_Merma FROM Mermas";
        int maxNumero = 0;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("ID_Merma"); // formato "M001"
                int numero = Integer.parseInt(id.substring(1));
                if (numero > maxNumero) {
                    maxNumero = numero;
                }
            }
        }

        return String.format("M%03d", maxNumero + 1);
    }
}