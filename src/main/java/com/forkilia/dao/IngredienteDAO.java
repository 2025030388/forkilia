package com.forkilia.dao;

import com.forkilia.modelo.Ingrediente;
import java.sql.SQLException;
import java.util.List;

public interface IngredienteDAO {

    void crear(Ingrediente ingrediente) throws SQLException;

    List<Ingrediente> listarPorUsuario(String idUsuario) throws SQLException;

    void actualizar(Ingrediente ingrediente) throws SQLException;

    void eliminar(String idIngrediente) throws SQLException;

    // NUEVO: genera el siguiente ID de Ingrediente e Inventario consultando la BD
    String obtenerSiguienteId() throws SQLException;

    String obtenerSiguienteIdInventario() throws SQLException;
}