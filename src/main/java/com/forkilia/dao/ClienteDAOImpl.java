package com.forkilia.dao;

import com.forkilia.conexion.ConexionBD;
import com.forkilia.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public void crear(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO Clientes (ID_Cliente, ID_Usuario, Nombre, Telefono, Direccion, Empresa, NumPersonas) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cliente.getIdCliente());
            ps.setString(2, cliente.getIdUsuario());
            ps.setString(3, cliente.getNombre());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getDireccion());
            ps.setString(6, cliente.getEmpresa());

            if (cliente.getNumPersonas() == null) {
                ps.setNull(7, Types.INTEGER);
            } else {
                ps.setInt(7, cliente.getNumPersonas());
            }

            ps.executeUpdate();
        }
    }

    @Override
    public List<Cliente> listarPorUsuario(String idUsuario) throws SQLException {
        String sql = "SELECT ID_Cliente, ID_Usuario, Nombre, Telefono, Direccion, Empresa, NumPersonas FROM Clientes "
                   + "WHERE ID_Usuario = ? ORDER BY Nombre";

        List<Cliente> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCliente(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public Cliente buscarPorId(String idCliente) throws SQLException {
        String sql = "SELECT ID_Cliente, ID_Usuario, Nombre, Telefono, Direccion, Empresa, NumPersonas "
                   + "FROM Clientes WHERE ID_Cliente = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
                return null;
            }
        }
    }

    @Override
    public void actualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE Clientes SET Nombre = ?, Telefono = ?, Direccion = ?, Empresa = ?, NumPersonas = ? "
                   + "WHERE ID_Cliente = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getDireccion());
            ps.setString(4, cliente.getEmpresa());

            if (cliente.getNumPersonas() == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, cliente.getNumPersonas());
            }

            ps.setString(6, cliente.getIdCliente());

            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(String idCliente) throws SQLException {
        String sql = "DELETE FROM Clientes WHERE ID_Cliente = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idCliente);
            ps.executeUpdate();
        }
        // Si el cliente ya tiene pedidos, MySQL lo bloquea (ON DELETE RESTRICT en Pedidos.ID_Cliente)
    }

    @Override
    public String obtenerSiguienteId() throws SQLException {
        String sql = "SELECT ID_Cliente FROM Clientes";
        int maxNumero = 0;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("ID_Cliente"); // formato "CL001"
                int numero = Integer.parseInt(id.substring(2));
                if (numero > maxNumero) {
                    maxNumero = numero;
                }
            }
        }

        int siguiente = maxNumero + 1;
        return String.format("CL%03d", siguiente);
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setIdCliente(rs.getString("ID_Cliente"));
        c.setIdUsuario(rs.getString("ID_Usuario"));
        c.setNombre(rs.getString("Nombre"));
        c.setTelefono(rs.getString("Telefono"));
        c.setDireccion(rs.getString("Direccion"));
        c.setEmpresa(rs.getString("Empresa"));

        int numPersonas = rs.getInt("NumPersonas");
        c.setNumPersonas(rs.wasNull() ? null : numPersonas);

        return c;
    }
}