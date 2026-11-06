package com.forkilia.controlador;

import com.forkilia.dao.MermaDAO;
import com.forkilia.dao.MermaDAOImpl;
import com.forkilia.modelo.Merma;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "MermaServlet", urlPatterns = {"/api/mermas"})
public class MermaServlet extends HttpServlet {

    private final MermaDAO mermaDAO = new MermaDAOImpl();
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // GET → lista las mermas de un usuario. Requiere ?idUsuario=U001
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
                List<Merma> lista = mermaDAO.listarPorUsuario(idUsuario);
                jsonRespuesta = listaMermasAJson(lista);
                statusCode = HttpServletResponse.SC_OK;
            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al listar mermas\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    // POST → registra una nueva merma. Requiere idUsuario, idIngrediente, cantidadPerdida.
    // idReceta es opcional (puede registrarse sin venir de una receta cocinada).
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String idUsuario = request.getParameter("idUsuario");
        String idIngrediente = request.getParameter("idIngrediente");
        String idReceta = request.getParameter("idReceta");
        String cantidadStr = request.getParameter("cantidadPerdida");

        String jsonRespuesta;
        int statusCode;

        if (idUsuario == null || idIngrediente == null || cantidadStr == null
                || idUsuario.isBlank() || idIngrediente.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Faltan campos requeridos\"}";
        } else {
            try {
                double cantidad = Double.parseDouble(cantidadStr);
                if (cantidad <= 0) {
                    statusCode = HttpServletResponse.SC_BAD_REQUEST;
                    jsonRespuesta = "{\"exito\": false, \"mensaje\": \"La cantidad debe ser mayor a 0\"}";
                } else {
                    Merma merma = new Merma();
                    merma.setIdMerma(mermaDAO.obtenerSiguienteId());
                    merma.setIdUsuario(idUsuario);
                    merma.setIdIngrediente(idIngrediente);
                    merma.setIdReceta(idReceta);
                    merma.setCantidadPerdida(cantidad);

                    mermaDAO.crear(merma);

                    statusCode = HttpServletResponse.SC_OK;
                    jsonRespuesta = String.format(
                        "{\"exito\": true, \"idMerma\": \"%s\", \"mensaje\": \"Merma registrada correctamente\"}",
                        merma.getIdMerma()
                    );
                }
            } catch (NumberFormatException e) {
                statusCode = HttpServletResponse.SC_BAD_REQUEST;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"La cantidad no es un número válido\"}";
            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al registrar la merma: " + e.getMessage() + "\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    // DELETE → elimina una merma. Requiere ?idMerma=M001
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String idMerma = request.getParameter("idMerma");
        String jsonRespuesta;
        int statusCode;

        if (idMerma == null || idMerma.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Falta el idMerma\"}";
        } else {
            try {
                mermaDAO.eliminar(idMerma);
                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = "{\"exito\": true, \"mensaje\": \"Merma eliminada\"}";
            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al eliminar la merma\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    private String listaMermasAJson(List<Merma> lista) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Merma m = lista.get(i);
            json.append(String.format(
                "{\"idMerma\": \"%s\", \"idIngrediente\": \"%s\", \"nombreIngrediente\": \"%s\", "
                + "\"categoria\": \"%s\", \"idReceta\": %s, \"nombreReceta\": %s, "
                + "\"cantidadPerdida\": %.2f, \"costoUnitario\": %.4f, \"costoTotal\": %.2f, \"fecha\": \"%s\"}",
                m.getIdMerma(), m.getIdIngrediente(), escaparJson(m.getNombreIngrediente()),
                m.getCategoria(), aJsonStringONull(m.getIdReceta()), aJsonStringONull(m.getNombreReceta()),
                m.getCantidadPerdida(), m.getCostoUnitario(), m.getCostoTotal(),
                m.getFecha() != null ? m.getFecha().format(FORMATO_FECHA) : ""
            ));
            if (i < lista.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }

    private String escaparJson(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String aJsonStringONull(String texto) {
        if (texto == null || texto.isBlank()) return "null";
        return "\"" + escaparJson(texto) + "\"";
    }
}