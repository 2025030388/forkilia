package com.forkilia.dao;

import com.forkilia.modelo.Cliente;
import java.sql.SQLException;
import java.util.List;

public interface ClienteDAO {

    void crear(Cliente cliente) throws SQLException;

    List<Cliente> listarPorUsuario(String idUsuario) throws SQLException;

    Cliente buscarPorId(String idCliente) throws SQLException;

    void actualizar(Cliente cliente) throws SQLException;

    void eliminar(String idCliente) throws SQLException;

    String obtenerSiguienteId() throws SQLException;
}