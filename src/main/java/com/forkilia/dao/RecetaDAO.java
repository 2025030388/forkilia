 package com.forkilia.dao;

import com.forkilia.modelo.Receta;
import java.sql.SQLException;
import java.util.List;

public interface RecetaDAO {

    // Crea la receta base (sin ingredientes todavía; costo/precio arrancan en 0)
    void crear(Receta receta) throws SQLException;

    // Lista las recetas de un usuario para el catálogo (sin el detalle de ingredientes)
    List<Receta> listarPorUsuario(String idUsuario) throws SQLException;

    // Trae una receta con todos sus ingredientes vinculados (vista expandida)
    Receta obtenerDetalle(String idReceta) throws SQLException;

    // Actualiza los datos generales de la receta (no toca los ingredientes vinculados)
    void actualizar(Receta receta) throws SQLException;

    // Elimina la receta (Receta_Ingrediente se borra en cascada;
    // si tiene pedidos asociados, Pedido_Detalle la bloquea con ON DELETE RESTRICT)
    void eliminar(String idReceta) throws SQLException;

    // Vincula un ingrediente del inventario a la receta, calcula su costo proporcional
    // (regla de 3 con conversión de unidades) y recalcula el costo/precio total de la receta
    void vincularIngrediente(String idReceta, String idIngrediente, double cantidad, String unidad) throws SQLException;

    // Quita un ingrediente de la receta y recalcula el costo/precio total
    void desvincularIngrediente(String idReceta, String idIngrediente) throws SQLException;

    String obtenerSiguienteId() throws SQLException;
}