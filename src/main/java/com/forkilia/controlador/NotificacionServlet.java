package com.forkilia.controlador;

import com.forkilia.dao.NotificacionDAO;
import com.forkilia.dao.NotificacionDAOImpl;
import com.forkilia.modelo.Notificacion;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "NotificacionServlet", urlPatterns = {"/api/notificaciones"})
public class NotificacionServlet extends HttpServlet {

    private final NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // GET → lista las notificaciones de un usuario (incluye las globales). Requiere ?idUsuario=U001
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
                List<Notificacion> lista = notificacionDAO.listarPorUsuario(idUsuario);
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < lista.size(); i++) {
                    Notificacion n = lista.get(i);
                    json.append(String.format(
                        "{\"idNotificacion\": \"%s\", \"tipo\": \"%s\", \"mensaje\": \"%s\", \"leida\": %b, \"fecha\": \"%s\"}",
                        n.getIdNotificacion(), n.getTipo(), escaparJson(n.getMensaje()), n.isLeida(),
                        n.getFecha() != null ? n.getFecha().format(FORMATO_FECHA) : ""
                    ));
                    if (i < lista.size() - 1) json.append(",");
                }
                json.append("]");
                jsonRespuesta = json.toString();
                statusCode = HttpServletResponse.SC_OK;

            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al listar notificaciones\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    // PUT → marca una notificación como leída. Requiere idNotificacion
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, String> datos = leerParametrosDelCuerpo(request);
        String idNotificacion = datos.get("idNotificacion");

        String jsonRespuesta;
        int statusCode;

        if (idNotificacion == null || idNotificacion.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Falta el idNotificacion\"}";
        } else {
            try {
                notificacionDAO.marcarLeida(idNotificacion);
                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = "{\"exito\": true, \"mensaje\": \"Notificación marcada como leída\"}";
            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al actualizar la notificación\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    // DELETE → elimina una notificación. Requiere ?idNotificacion=N001
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String idNotificacion = request.getParameter("idNotificacion");
        String jsonRespuesta;
        int statusCode;

        if (idNotificacion == null || idNotificacion.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Falta el idNotificacion\"}";
        } else {
            try {
                notificacionDAO.eliminar(idNotificacion);
                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = "{\"exito\": true, \"mensaje\": \"Notificación eliminada\"}";
            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al eliminar la notificación\"}";
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