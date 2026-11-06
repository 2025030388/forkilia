package com.forkilia.dao;

import com.forkilia.modelo.Merma;
import java.sql.SQLException;
import java.util.List;

public interface MermaDAO {

    // Crea el registro de merma. Toma el Costo_Unitario actual del inventario
    // y calcula el Costo_Total automáticamente (no se recibe del frontend).
    void crear(Merma merma) throws SQLException;

    // Lista las mermas de un usuario, con la categoría ya calculada
    List<Merma> listarPorUsuario(String idUsuario) throws SQLException;

    void eliminar(String idMerma) throws SQLException;

    String obtenerSiguienteId() throws SQLException;
}