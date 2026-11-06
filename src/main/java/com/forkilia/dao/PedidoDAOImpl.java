package com.forkilia.dao;

import com.forkilia.conexion.ConexionBD;
import com.forkilia.modelo.Pedido;
import com.forkilia.modelo.PedidoDetalleItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAOImpl implements PedidoDAO {

    @Override
    public void crear(Pedido pedido, List<PedidoDetalleItem> detalle) throws SQLException {
        String sqlPrecioReceta = "SELECT Precio FROM Recetas WHERE ID_Receta = ?";
        String sqlEsEmpresa = "SELECT Empresa FROM Clientes WHERE ID_Cliente = ?";
        String sqlPedido = "INSERT INTO Pedidos "
                          + "(ID_Pedido, Folio, ID_Cliente, ID_Usuario, Tipo, Mesa, Total, Anticipo, Status, Notas, Descuento_Aplicado) "
                          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO Pedido_Detalle (ID_Pedido, ID_Receta, Cantidad, Precio_Unitario) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false);

            try {
                double total = 0;

                // Tomamos el precio ACTUAL de cada receta directamente de la base de datos
                // (nunca confiamos en un precio que venga del frontend) y lo dejamos fijo
                // en Pedido_Detalle, para que si el precio de la receta cambia después,
                // el historial de este pedido no se altere.
                try (PreparedStatement psPrecio = con.prepareStatement(sqlPrecioReceta)) {
                    for (PedidoDetalleItem item : detalle) {
                        psPrecio.setString(1, item.getIdReceta());
                        try (ResultSet rs = psPrecio.executeQuery()) {
                            if (rs.next()) {
                                item.setPrecioUnitario(rs.getDouble("Precio"));
                            } else {
                                throw new SQLException("La receta " + item.getIdReceta() + " no existe");
                            }
                        }
                        total += item.calcularSubtotal();
                    }
                }

                // Beneficio de cliente empresa: 10% de descuento sobre el total del pedido
                boolean clienteEsEmpresa = false;
                try (PreparedStatement psEmpresa = con.prepareStatement(sqlEsEmpresa)) {
                    psEmpresa.setString(1, pedido.getIdCliente());
                    try (ResultSet rs = psEmpresa.executeQuery()) {
                        if (rs.next()) {
                            String empresa = rs.getString("Empresa");
                            clienteEsEmpresa = empresa != null && !empresa.isBlank();
                        }
                    }
                }

                if (clienteEsEmpresa) {
                    total = total * 0.90; // 10% de descuento
                }

                pedido.setTotal(total);
                pedido.setDescuentoAplicado(clienteEsEmpresa);

                try (PreparedStatement ps = con.prepareStatement(sqlPedido)) {
                    ps.setString(1, pedido.getIdPedido());
                    ps.setString(2, pedido.getFolio());
                    ps.setString(3, pedido.getIdCliente());
                    ps.setString(4, pedido.getIdUsuario());
                    ps.setString(5, pedido.getTipo());
                    ps.setString(6, pedido.getMesa());
                    ps.setDouble(7, pedido.getTotal());
                    ps.setDouble(8, pedido.getAnticipo());
                    ps.setString(9, pedido.getStatus());
                    ps.setString(10, pedido.getNotas());
                    ps.setBoolean(11, pedido.isDescuentoAplicado());
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(sqlDetalle)) {
                    for (PedidoDetalleItem item : detalle) {
                        ps.setString(1, pedido.getIdPedido());
                        ps.setString(2, item.getIdReceta());
                        ps.setInt(3, item.getCantidad());
                        ps.setDouble(4, item.getPrecioUnitario());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                // Notificamos al Emprendedor de que entró un pedido nuevo
                try (PreparedStatement psNotif = con.prepareStatement(
                        "INSERT INTO Notificaciones (ID_Notificacion, ID_Usuario, Tipo, Mensaje, Leida) VALUES (?, ?, ?, ?, FALSE)")) {
                    String idNotificacion = generarSiguienteIdNotificacion(con);
                    String mensaje = String.format("Nuevo pedido %s por $%.2f", pedido.getFolio(), pedido.getTotal());
                    psNotif.setString(1, idNotificacion);
                    psNotif.setString(2, pedido.getIdUsuario());
                    psNotif.setString(3, "pedido_nuevo");
                    psNotif.setString(4, mensaje);
                    psNotif.executeUpdate();
                }

                con.commit();

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    @Override
    public List<Pedido> listarPorUsuario(String idUsuario) throws SQLException {
        String sql = "SELECT p.ID_Pedido, p.Folio, p.ID_Cliente, c.Nombre AS NombreCliente, c.Telefono AS TelefonoCliente, "
                   + "p.ID_Usuario, p.Tipo, p.Mesa, p.Total, p.Anticipo, p.Status, p.Notas, p.Fecha, p.Descuento_Aplicado "
                   + "FROM Pedidos p "
                   + "JOIN Clientes c ON p.ID_Cliente = c.ID_Cliente "
                   + "WHERE p.ID_Usuario = ? "
                   + "ORDER BY p.Fecha DESC";

        List<Pedido> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPedido(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public Pedido obtenerDetalle(String idPedido) throws SQLException {
        String sqlPedido = "SELECT p.ID_Pedido, p.Folio, p.ID_Cliente, c.Nombre AS NombreCliente, c.Telefono AS TelefonoCliente, "
                          + "p.ID_Usuario, p.Tipo, p.Mesa, p.Total, p.Anticipo, p.Status, p.Notas, p.Fecha, p.Descuento_Aplicado "
                          + "FROM Pedidos p "
                          + "JOIN Clientes c ON p.ID_Cliente = c.ID_Cliente "
                          + "WHERE p.ID_Pedido = ?";

        String sqlDetalle = "SELECT pd.ID_Receta, r.Nombre AS NombreReceta, pd.Cantidad, pd.Precio_Unitario "
                           + "FROM Pedido_Detalle pd "
                           + "JOIN Recetas r ON pd.ID_Receta = r.ID_Receta "
                           + "WHERE pd.ID_Pedido = ?";

        Pedido pedido = null;

        try (Connection con = ConexionBD.obtenerConexion()) {

            try (PreparedStatement ps = con.prepareStatement(sqlPedido)) {
                ps.setString(1, idPedido);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        pedido = mapearPedido(rs);
                    } else {
                        return null;
                    }
                }
            }

            List<PedidoDetalleItem> detalle = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(sqlDetalle)) {
                ps.setString(1, idPedido);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        PedidoDetalleItem item = new PedidoDetalleItem();
                        item.setIdReceta(rs.getString("ID_Receta"));
                        item.setNombreReceta(rs.getString("NombreReceta"));
                        item.setCantidad(rs.getInt("Cantidad"));
                        item.setPrecioUnitario(rs.getDouble("Precio_Unitario"));
                        detalle.add(item);
                    }
                }
            }
            pedido.setDetalle(detalle);
        }

        return pedido;
    }

    @Override
    public void actualizarStatus(String idPedido, String status) throws SQLException {
        String sql = "UPDATE Pedidos SET Status = ? WHERE ID_Pedido = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, idPedido);
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(String idPedido) throws SQLException {
        String sql = "DELETE FROM Pedidos WHERE ID_Pedido = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idPedido);
            ps.executeUpdate();
        }
        // Pedido_Detalle y Tickets se eliminan solos (ON DELETE CASCADE)
    }

    private String generarSiguienteIdNotificacion(Connection con) throws SQLException {
        String sql = "SELECT ID_Notificacion FROM Notificaciones";
        int maxNumero = 0;

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("ID_Notificacion");
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

    @Override
    public String obtenerSiguienteId() throws SQLException {
        String sql = "SELECT ID_Pedido FROM Pedidos";
        int maxNumero = 0;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("ID_Pedido"); // formato "PD001"
                int numero = Integer.parseInt(id.substring(2));
                if (numero > maxNumero) {
                    maxNumero = numero;
                }
            }
        }

        return String.format("PD%03d", maxNumero + 1);
    }

    @Override
    public String obtenerSiguienteFolio() throws SQLException {
        String sql = "SELECT Folio FROM Pedidos";
        int maxNumero = 0;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String folio = rs.getString("Folio"); // formato "F0001"
                try {
                    int numero = Integer.parseInt(folio.substring(1));
                    if (numero > maxNumero) {
                        maxNumero = numero;
                    }
                } catch (NumberFormatException ignorado) {
                    // si algún folio no sigue el formato esperado, simplemente lo saltamos
                }
            }
        }

        return String.format("F%04d", maxNumero + 1);
    }

    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setIdPedido(rs.getString("ID_Pedido"));
        p.setFolio(rs.getString("Folio"));
        p.setIdCliente(rs.getString("ID_Cliente"));
        p.setNombreCliente(rs.getString("NombreCliente"));
        p.setTelefonoCliente(rs.getString("TelefonoCliente"));
        p.setIdUsuario(rs.getString("ID_Usuario"));
        p.setTipo(rs.getString("Tipo"));
        p.setMesa(rs.getString("Mesa"));
        p.setTotal(rs.getDouble("Total"));
        p.setAnticipo(rs.getDouble("Anticipo"));
        p.setStatus(rs.getString("Status"));
        p.setNotas(rs.getString("Notas"));
        p.setDescuentoAplicado(rs.getBoolean("Descuento_Aplicado"));

        Timestamp fecha = rs.getTimestamp("Fecha");
        if (fecha != null) {
            p.setFecha(fecha.toLocalDateTime());
        }

        return p;
    }
}