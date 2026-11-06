package com.forkilia.dao;

import com.forkilia.modelo.Notificacion;
import java.sql.SQLException;
import java.util.List;

public interface NotificacionDAO {

    // Lista las notificaciones de un usuario, incluyendo las globales (ID_Usuario = NULL)
    List<Notificacion> listarPorUsuario(String idUsuario) throws SQLException;

    void marcarLeida(String idNotificacion) throws SQLException;

    void eliminar(String idNotificacion) throws SQLException;

    // Se usa internamente desde otros DAOs (ej. al crear un pedido) para avisarle al Emprendedor
    void crear(Notificacion notificacion) throws SQLException;

    String obtenerSiguienteId() throws SQLException;
}