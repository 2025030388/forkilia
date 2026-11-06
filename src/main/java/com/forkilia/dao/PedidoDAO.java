package com.forkilia.dao;

import com.forkilia.modelo.Pedido;
import com.forkilia.modelo.PedidoDetalleItem;
import java.sql.SQLException;
import java.util.List;

public interface PedidoDAO {

    // Crea el pedido junto con su detalle (Pedido_Detalle) en una sola transacción.
    // El Total se calcula server-side a partir de los precios reales de cada receta.
    void crear(Pedido pedido, List<PedidoDetalleItem> detalle) throws SQLException;

    // Lista los pedidos de un usuario (con nombre de cliente, sin el detalle de productos)
    List<Pedido> listarPorUsuario(String idUsuario) throws SQLException;

    // Trae un pedido con su detalle completo de productos
    Pedido obtenerDetalle(String idPedido) throws SQLException;

    // Cambia únicamente el status (pendiente, preparando, listo, entregado)
    void actualizarStatus(String idPedido, String status) throws SQLException;

    // Elimina el pedido (Pedido_Detalle y Tickets se borran en cascada)
    void eliminar(String idPedido) throws SQLException;

    String obtenerSiguienteId() throws SQLException;

    String obtenerSiguienteFolio() throws SQLException;
}