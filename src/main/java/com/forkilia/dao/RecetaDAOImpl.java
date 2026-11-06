package com.forkilia.dao;

import com.forkilia.conexion.ConexionBD;
import com.forkilia.modelo.Receta;
import com.forkilia.modelo.RecetaIngrediente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecetaDAOImpl implements RecetaDAO {

    @Override
    public void crear(Receta receta) throws SQLException {
        String sql = "INSERT INTO Recetas "
                    + "(ID_Receta, ID_Usuario, ID_Categoria, Nombre, Rendimiento, Tiempo_Prep_Min, "
                    + "Costo, Precio, Margen, Dificultad, Tip, Imagen, Procedimiento) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, receta.getIdReceta());
            ps.setString(2, receta.getIdUsuario());
            ps.setString(3, receta.getIdCategoria());
            ps.setString(4, receta.getNombre());
            ps.setInt(5, receta.getRendimiento());
            ps.setInt(6, receta.getTiempoPrepMin());
            ps.setDouble(7, 0.0); // costo arranca en 0, se calcula al vincular ingredientes
            ps.setDouble(8, 0.0); // precio arranca en 0 por la misma razón
            ps.setDouble(9, receta.getMargen());
            ps.setString(10, receta.getDificultad());
            ps.setString(11, receta.getTip());
            ps.setString(12, receta.getImagen());
            ps.setString(13, receta.getProcedimiento());

            ps.executeUpdate();
        }
    }

    @Override
    public List<Receta> listarPorUsuario(String idUsuario) throws SQLException {
        String sql = "SELECT r.ID_Receta, r.ID_Usuario, r.ID_Categoria, c.Nombre AS NombreCategoria, "
                    + "r.Nombre, r.Rendimiento, r.Tiempo_Prep_Min, r.Costo, r.Precio, r.Margen, "
                    + "r.Dificultad, r.Tip, r.Imagen, r.Procedimiento "
                    + "FROM Recetas r "
                    + "JOIN Categorias c ON r.ID_Categoria = c.ID_Categoria "
                    + "WHERE r.ID_Usuario = ? "
                    + "ORDER BY r.Nombre";

        List<Receta> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearReceta(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public Receta obtenerDetalle(String idReceta) throws SQLException {
        String sqlReceta = "SELECT r.ID_Receta, r.ID_Usuario, r.ID_Categoria, c.Nombre AS NombreCategoria, "
                          + "r.Nombre, r.Rendimiento, r.Tiempo_Prep_Min, r.Costo, r.Precio, r.Margen, "
                          + "r.Dificultad, r.Tip, r.Imagen, r.Procedimiento "
                          + "FROM Recetas r "
                          + "JOIN Categorias c ON r.ID_Categoria = c.ID_Categoria "
                          + "WHERE r.ID_Receta = ?";

        String sqlIngredientes = "SELECT ri.ID_Ingrediente, i.Nombre AS NombreIngrediente, "
                                + "ri.Cantidad, ri.Unidad, i.Unidad_Base, inv.Costo_Unitario "
                                + "FROM Receta_Ingrediente ri "
                                + "JOIN Ingredients i ON ri.ID_Ingrediente = i.ID_Ingrediente "
                                + "JOIN Inventario inv ON i.ID_Ingrediente = inv.ID_Ingrediente "
                                + "WHERE ri.ID_Receta = ? "
                                + "ORDER BY i.Nombre";

        Receta receta = null;

        try (Connection con = ConexionBD.obtenerConexion()) {

            try (PreparedStatement ps = con.prepareStatement(sqlReceta)) {
                ps.setString(1, idReceta);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        receta = mapearReceta(rs);
                    } else {
                        return null; // no existe esa receta
                    }
                }
            }

            List<RecetaIngrediente> ingredientes = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(sqlIngredientes)) {
                ps.setString(1, idReceta);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        RecetaIngrediente ri = new RecetaIngrediente();
                        ri.setIdReceta(idReceta);
                        ri.setIdIngrediente(rs.getString("ID_Ingrediente"));
                        ri.setNombreIngrediente(rs.getString("NombreIngrediente"));
                        ri.setCantidad(rs.getDouble("Cantidad"));
                        ri.setUnidad(rs.getString("Unidad"));

                        double costoUnitario = rs.getDouble("Costo_Unitario");
                        String unidadBase = rs.getString("Unidad_Base");
                        double cantidadEnUnidadBase = convertir(ri.getCantidad(), ri.getUnidad(), unidadBase);
                        ri.setCostoProporcional(cantidadEnUnidadBase * costoUnitario);

                        ingredientes.add(ri);
                    }
                }
            }
            receta.setIngredientes(ingredientes);
        }

        return receta;
    }

    @Override
    public void actualizar(Receta receta) throws SQLException {
        String sql = "UPDATE Recetas SET ID_Categoria = ?, Nombre = ?, Rendimiento = ?, "
                    + "Tiempo_Prep_Min = ?, Margen = ?, Dificultad = ?, Tip = ?, Imagen = ?, Procedimiento = ? "
                    + "WHERE ID_Receta = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, receta.getIdCategoria());
            ps.setString(2, receta.getNombre());
            ps.setInt(3, receta.getRendimiento());
            ps.setInt(4, receta.getTiempoPrepMin());
            ps.setDouble(5, receta.getMargen());
            ps.setString(6, receta.getDificultad());
            ps.setString(7, receta.getTip());
            ps.setString(8, receta.getImagen());
            ps.setString(9, receta.getProcedimiento());
            ps.setString(10, receta.getIdReceta());

            ps.executeUpdate();
        }

        // El margen pudo haber cambiado, así que recalculamos el precio con el costo actual
        recalcularCostoYPrecio(receta.getIdReceta());
    }

    @Override
    public void eliminar(String idReceta) throws SQLException {
        String sql = "DELETE FROM Recetas WHERE ID_Receta = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idReceta);
            ps.executeUpdate();
        }
        // Si la receta ya se usó en un Pedido_Detalle, MySQL rechaza el DELETE
        // (ON DELETE RESTRICT) y la excepción sube para que el Servlet la traduzca.
    }

    @Override
    public void vincularIngrediente(String idReceta, String idIngrediente, double cantidad, String unidad) throws SQLException {
        String sql = "INSERT INTO Receta_Ingrediente (ID_Receta, ID_Ingrediente, Cantidad, Unidad) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idReceta);
            ps.setString(2, idIngrediente);
            ps.setDouble(3, cantidad);
            ps.setString(4, unidad);

            ps.executeUpdate();
        }

        recalcularCostoYPrecio(idReceta);
    }

    @Override
    public void desvincularIngrediente(String idReceta, String idIngrediente) throws SQLException {
        String sql = "DELETE FROM Receta_Ingrediente WHERE ID_Receta = ? AND ID_Ingrediente = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idReceta);
            ps.setString(2, idIngrediente);
            ps.executeUpdate();
        }

        recalcularCostoYPrecio(idReceta);
    }

    @Override
    public String obtenerSiguienteId() throws SQLException {
        String sql = "SELECT ID_Receta FROM Recetas";
        int maxNumero = 0;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("ID_Receta"); // formato "R001"
                int numero = Integer.parseInt(id.substring(1));
                if (numero > maxNumero) {
                    maxNumero = numero;
                }
            }
        }

        int siguiente = maxNumero + 1;
        return String.format("R%03d", siguiente);
    }

    /**
     * Recorre todos los ingredientes vinculados a la receta, suma su costo
     * proporcional (con conversión de unidades) para obtener el Costo Verdadero
     * Total, y con el margen guardado calcula el Precio de venta final:
     * Precio = Costo / (1 - margen/100)
     */
    private void recalcularCostoYPrecio(String idReceta) throws SQLException {
        String sqlSumaCosto = "SELECT ri.Cantidad, ri.Unidad, i.Unidad_Base, inv.Costo_Unitario "
                             + "FROM Receta_Ingrediente ri "
                             + "JOIN Ingredients i ON ri.ID_Ingrediente = i.ID_Ingrediente "
                             + "JOIN Inventario inv ON i.ID_Ingrediente = inv.ID_Ingrediente "
                             + "WHERE ri.ID_Receta = ?";

        String sqlMargen = "SELECT Margen FROM Recetas WHERE ID_Receta = ?";
        String sqlActualizar = "UPDATE Recetas SET Costo = ?, Precio = ? WHERE ID_Receta = ?";

        try (Connection con = ConexionBD.obtenerConexion()) {

            double costoTotal = 0;
            try (PreparedStatement ps = con.prepareStatement(sqlSumaCosto)) {
                ps.setString(1, idReceta);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        double cantidad = rs.getDouble("Cantidad");
                        String unidad = rs.getString("Unidad");
                        String unidadBase = rs.getString("Unidad_Base");
                        double costoUnitario = rs.getDouble("Costo_Unitario");

                        double cantidadEnUnidadBase = convertir(cantidad, unidad, unidadBase);
                        costoTotal += cantidadEnUnidadBase * costoUnitario;
                    }
                }
            }

            double margen = 0;
            try (PreparedStatement ps = con.prepareStatement(sqlMargen)) {
                ps.setString(1, idReceta);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        margen = rs.getDouble("Margen");
                    }
                }
            }

            double precioFinal;
            if (margen >= 100) {
                // Un margen de 100% o más haría la división por cero o negativa;
                // en ese caso, evitamos el error y dejamos el precio igual al costo.
                precioFinal = costoTotal;
            } else {
                precioFinal = costoTotal / (1 - (margen / 100.0));
            }

            try (PreparedStatement ps = con.prepareStatement(sqlActualizar)) {
                ps.setDouble(1, costoTotal);
                ps.setDouble(2, precioFinal);
                ps.setString(3, idReceta);
                ps.executeUpdate();
            }
        }
    }

    /**
     * Convierte una cantidad de una unidad a otra, para poder comparar
     * la cantidad usada en la receta contra la unidad base del inventario.
     * Soporta g<->kg, ml<->L. Unidades tipo "pzas", "dientes", "rebanadas"
     * deben coincidir exactamente (no son convertibles entre sí).
     */
    private double convertir(double cantidad, String unidadOrigen, String unidadDestino) {
        if (unidadOrigen.equalsIgnoreCase(unidadDestino)) {
            return cantidad;
        }
        if (unidadOrigen.equalsIgnoreCase("g") && unidadDestino.equalsIgnoreCase("kg")) {
            return cantidad / 1000.0;
        }
        if (unidadOrigen.equalsIgnoreCase("kg") && unidadDestino.equalsIgnoreCase("g")) {
            return cantidad * 1000.0;
        }
        if (unidadOrigen.equalsIgnoreCase("ml") && unidadDestino.equalsIgnoreCase("L")) {
            return cantidad / 1000.0;
        }
        if (unidadOrigen.equalsIgnoreCase("L") && unidadDestino.equalsIgnoreCase("ml")) {
            return cantidad * 1000.0;
        }
        // Unidades no convertibles entre sí (ej. "pzas" contra "kg"): no se puede
        // calcular el costo correctamente, así que se asume 1:1 como último recurso
        // para no tronar, pero esto debería evitarse desde el formulario del frontend.
        return cantidad;
    }

    private Receta mapearReceta(ResultSet rs) throws SQLException {
        Receta r = new Receta();
        r.setIdReceta(rs.getString("ID_Receta"));
        r.setIdUsuario(rs.getString("ID_Usuario"));
        r.setIdCategoria(rs.getString("ID_Categoria"));
        r.setNombreCategoria(rs.getString("NombreCategoria"));
        r.setNombre(rs.getString("Nombre"));
        r.setRendimiento(rs.getInt("Rendimiento"));
        r.setTiempoPrepMin(rs.getInt("Tiempo_Prep_Min"));
        r.setCosto(rs.getDouble("Costo"));
        r.setPrecio(rs.getDouble("Precio"));
        r.setMargen(rs.getDouble("Margen"));
        r.setDificultad(rs.getString("Dificultad"));
        r.setTip(rs.getString("Tip"));
        r.setImagen(rs.getString("Imagen"));
        r.setProcedimiento(rs.getString("Procedimiento"));
        return r;
    }
}