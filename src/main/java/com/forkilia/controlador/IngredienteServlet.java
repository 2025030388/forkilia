package com.forkilia.controlador;

import com.forkilia.dao.IngredienteDAO;
import com.forkilia.dao.IngredienteDAOImpl;
import com.forkilia.modelo.Ingrediente;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "IngredienteServlet", urlPatterns = {"/api/ingredientes"})
public class IngredienteServlet extends HttpServlet {

    private final IngredienteDAO ingredienteDAO = new IngredienteDAOImpl();

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // GET → lista los ingredientes de un usuario. Requiere ?idUsuario=U001
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String idUsuario = request.getParameter("idUsuario");
        String jsonRespuesta;
        int statusCode;

        if (idUsuario == null || idUsuario.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Falta el idUsuario\"}";
        } else {
            try {
                List<Ingrediente> lista = ingredienteDAO.listarPorUsuario(idUsuario);
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < lista.size(); i++) {
                    Ingrediente ing = lista.get(i);
                    json.append(String.format(
                        "{\"idIngrediente\": \"%s\", \"nombre\": \"%s\", \"unidadBase\": \"%s\", "
                        + "\"stockActual\": %.2f, \"stockMinimo\": %.2f, \"costoUnitario\": %.4f, "
                        + "\"valorTotal\": %.2f, \"estado\": \"%s\"}",
                        ing.getIdIngrediente(), escaparJson(ing.getNombre()), escaparJson(ing.getUnidadBase()),
                        ing.getStockActual(), ing.getStockMinimo(), ing.getCostoUnitario(),
                        ing.calcularValorTotal(), ing.calcularEstado()
                    ));
                    if (i < lista.size() - 1) json.append(",");
                }
                json.append("]");
                jsonRespuesta = json.toString();
                statusCode = HttpServletResponse.SC_OK;

            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al listar ingredientes\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    // POST → crea un nuevo ingrediente con su inventario
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String idUsuario = request.getParameter("idUsuario");
        String nombre = request.getParameter("nombre");
        String unidadBase = request.getParameter("unidadBase");
        String stockActualStr = request.getParameter("stockActual");
        String stockMinimoStr = request.getParameter("stockMinimo");
        String costoUnitarioStr = request.getParameter("costoUnitario");

        String jsonRespuesta;
        int statusCode;

        if (idUsuario == null || nombre == null || unidadBase == null
                || stockActualStr == null || stockMinimoStr == null || costoUnitarioStr == null) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Faltan campos requeridos\"}";
        } else {
            try {
                Ingrediente ing = new Ingrediente();
                ing.setIdIngrediente(ingredienteDAO.obtenerSiguienteId());
                ing.setIdInventario(ingredienteDAO.obtenerSiguienteIdInventario());
                ing.setIdUsuario(idUsuario);
                ing.setNombre(nombre);
                ing.setUnidadBase(unidadBase);
                ing.setStockActual(Double.parseDouble(stockActualStr));
                ing.setStockMinimo(Double.parseDouble(stockMinimoStr));
                ing.setCostoUnitario(Double.parseDouble(costoUnitarioStr));

                ingredienteDAO.crear(ing);

                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = "{\"exito\": true, \"mensaje\": \"Ingrediente creado correctamente\"}";

            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al crear ingrediente: " + e.getMessage() + "\"}";
            } catch (NumberFormatException e) {
                statusCode = HttpServletResponse.SC_BAD_REQUEST;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Los valores numéricos no son válidos\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    // PUT → actualiza un ingrediente existente
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, String> datos = leerParametrosDelCuerpo(request);

        String jsonRespuesta;
        int statusCode;

        try {
            Ingrediente ing = new Ingrediente();
            ing.setIdIngrediente(datos.get("idIngrediente"));
            ing.setNombre(datos.get("nombre"));
            ing.setUnidadBase(datos.get("unidadBase"));
            ing.setStockActual(Double.parseDouble(datos.get("stockActual")));
            ing.setStockMinimo(Double.parseDouble(datos.get("stockMinimo")));
            ing.setCostoUnitario(Double.parseDouble(datos.get("costoUnitario")));

            ingredienteDAO.actualizar(ing);

            statusCode = HttpServletResponse.SC_OK;
            jsonRespuesta = "{\"exito\": true, \"mensaje\": \"Ingrediente actualizado correctamente\"}";

        } catch (SQLException e) {
            e.printStackTrace();
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al actualizar\"}";
        } catch (Exception e) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Datos inválidos\"}";
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    // DELETE → elimina un ingrediente. Requiere ?idIngrediente=I001
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String idIngrediente = request.getParameter("idIngrediente");
        String jsonRespuesta;
        int statusCode;

        if (idIngrediente == null || idIngrediente.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Falta el idIngrediente\"}";
        } else {
            try {
                ingredienteDAO.eliminar(idIngrediente);
                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = "{\"exito\": true, \"mensaje\": \"Ingrediente eliminado\"}";
            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al eliminar\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    private Map<String, String> leerParametrosDelCuerpo(HttpServletRequest request) throws IOException {
        Map<String, String> mapa = new HashMap<>();
        StringBuilder cuerpo = new StringBuilder();
        String linea;
        try (var reader = request.getReader()) {
            while ((linea = reader.readLine()) != null) {
                cuerpo.append(linea);
            }
        }
        for (String par : cuerpo.toString().split("&")) {
            String[] partes = par.split("=", 2);
            if (partes.length == 2) {
                mapa.put(
                    java.net.URLDecoder.decode(partes[0], "UTF-8"),
                    java.net.URLDecoder.decode(partes[1], "UTF-8")
                );
            }
        }
        return mapa;
    }

    private String escaparJson(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}