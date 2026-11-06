package com.forkilia.controlador;

import com.forkilia.dao.RecetaDAO;
import com.forkilia.dao.RecetaDAOImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "RecetaIngredienteServlet", urlPatterns = {"/api/recetas/ingredientes"})
public class RecetaIngredienteServlet extends HttpServlet {

    private final RecetaDAO recetaDAO = new RecetaDAOImpl();

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // POST → vincula un ingrediente a una receta.
    // Requiere: idReceta, idIngrediente, cantidad, unidad
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String idReceta = request.getParameter("idReceta");
        String idIngrediente = request.getParameter("idIngrediente");
        String cantidadStr = request.getParameter("cantidad");
        String unidad = request.getParameter("unidad");

        String jsonRespuesta;
        int statusCode;

        if (idReceta == null || idIngrediente == null || cantidadStr == null || unidad == null
                || idReceta.isBlank() || idIngrediente.isBlank() || unidad.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Faltan campos requeridos\"}";
        } else {
            try {
                double cantidad = Double.parseDouble(cantidadStr);
                if (cantidad <= 0) {
                    statusCode = HttpServletResponse.SC_BAD_REQUEST;
                    jsonRespuesta = "{\"exito\": false, \"mensaje\": \"La cantidad debe ser mayor a 0\"}";
                } else {
                    recetaDAO.vincularIngrediente(idReceta, idIngrediente, cantidad, unidad);
                    statusCode = HttpServletResponse.SC_OK;
                    jsonRespuesta = "{\"exito\": true, \"mensaje\": \"Ingrediente vinculado correctamente\"}";
                }
            } catch (NumberFormatException e) {
                statusCode = HttpServletResponse.SC_BAD_REQUEST;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"La cantidad no es un número válido\"}";
            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_CONFLICT;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Ese ingrediente ya está vinculado a esta receta\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    // DELETE → desvincula un ingrediente de una receta.
    // Requiere ?idReceta=R001&idIngrediente=I001
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String idReceta = request.getParameter("idReceta");
        String idIngrediente = request.getParameter("idIngrediente");

        String jsonRespuesta;
        int statusCode;

        if (idReceta == null || idIngrediente == null || idReceta.isBlank() || idIngrediente.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Faltan idReceta o idIngrediente\"}";
        } else {
            try {
                recetaDAO.desvincularIngrediente(idReceta, idIngrediente);
                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = "{\"exito\": true, \"mensaje\": \"Ingrediente desvinculado correctamente\"}";
            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al desvincular el ingrediente\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }
}