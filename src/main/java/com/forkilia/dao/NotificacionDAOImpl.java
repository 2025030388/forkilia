package com.forkilia.dao;

import com.forkilia.conexion.ConexionBD;
import com.forkilia.modelo.Notificacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class NotificacionDAOImpl implements NotificacionDAO {

    @Override
    public List<Notificacion> listarPorUsuario(String idUsuario) throws SQLException {
        String sql = "SELECT * FROM Notificaciones WHERE ID_Usuario = ? OR ID_Usuario IS NULL ORDER BY Fecha DESC";

        List<Notificacion> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notificacion n = new Notificacion();
                    n.setIdNotificacion(rs.getString("ID_Notificacion"));
                    n.setIdUsuario(rs.getString("ID_Usuario"));
                    n.setTipo(rs.getString("Tipo"));
                    n.setMensaje(rs.getString("Mensaje"));
                    n.setLeida(rs.getBoolean("Leida"));

                    Timestamp fecha = rs.getTimestamp("Fecha");
                    if (fecha != null) {
                        n.setFecha(fecha.toLocalDateTime());
                    }

                    lista.add(n);
                }
            }
        }
        return lista;
    }

    @Override
    public void marcarLeida(String idNotificacion) throws SQLException {
        String sql = "UPDATE Notificaciones SET Leida = TRUE WHERE ID_Notificacion = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idNotificacion);
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(String idNotificacion) throws SQLException {
        String sql = "DELETE FROM Notificaciones WHERE ID_Notificacion = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idNotificacion);
            ps.executeUpdate();
        }
    }

    @Override
    public void crear(Notificacion notificacion) throws SQLException {
        String sql = "INSERT INTO Notificaciones (ID_Notificacion, ID_Usuario, Tipo, Mensaje, Leida) VALUES (?, ?, ?, ?, FALSE)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, notificacion.getIdNotificacion());
            if (notificacion.getIdUsuario() == null) {
                ps.setNull(2, java.sql.Types.VARCHAR);
            } else {
                ps.setString(2, notificacion.getIdUsuario());
            }
            ps.setString(3, notificacion.getTipo());
            ps.setString(4, notificacion.getMensaje());

            ps.executeUpdate();
        }
    }

    @Override
    public String obtenerSiguienteId() throws SQLException {
        String sql = "SELECT ID_Notificacion FROM Notificaciones";
        int maxNumero = 0;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("ID_Notificacion"); // formato "N001"
                try {
                    int numero = Integer.parseInt(id.substring(1));
                    if (numero > maxNumero) {
                        maxNumero = numero;
                    }
                } catch (NumberFormatException ignorado) {
                }
            }
        }

        return String.format("N%03d", maxNumero + 1);
    }
}