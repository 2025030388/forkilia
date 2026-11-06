package com.forkilia.dao;

import com.forkilia.modelo.Categoria;
import java.sql.SQLException;
import java.util.List;

public interface CategoriaDAO {

    void crear(Categoria categoria) throws SQLException;

    List<Categoria> listarPorUsuario(String idUsuario) throws SQLException;

    void actualizar(Categoria categoria) throws SQLException;

    void eliminar(String idCategoria) throws SQLException;

    boolean existeNombreParaUsuario(String idUsuario, String nombre) throws SQLException;

    String obtenerSiguienteId() throws SQLException;
}