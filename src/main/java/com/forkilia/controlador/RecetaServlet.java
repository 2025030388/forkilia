package com.forkilia.controlador;

import com.forkilia.dao.RecetaDAO;
import com.forkilia.dao.RecetaDAOImpl;
import com.forkilia.modelo.Receta;
import com.forkilia.modelo.RecetaIngrediente;

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

@WebServlet(name = "RecetaServlet", urlPatterns = {"/api/recetas"})
public class RecetaServlet extends HttpServlet {

    private final RecetaDAO recetaDAO = new RecetaDAOImpl();

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // GET → dos modos:
    //   ?idUsuario=U001            -> lista de recetas para el catálogo (sin ingredientes)
    //   ?idReceta=R001             -> detalle completo de una receta (con ingredientes vinculados)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String idReceta = request.getParameter("idReceta");
        String idUsuario = request.getParameter("idUsuario");

        String jsonRespuesta;
        int statusCode;

        try {
            if (idReceta != null && !idReceta.isBlank()) {
                Receta receta = recetaDAO.obtenerDetalle(idReceta);
                if (receta == null) {
                    statusCode = HttpServletResponse.SC_NOT_FOUND;
                    jsonRespuesta = "{\"exito\": false, \"mensaje\": \"No se encontró la receta\"}";
                } else {
                    statusCode = HttpServletResponse.SC_OK;
                    jsonRespuesta = recetaADetalleJson(receta);
                }
            } else if (idUsuario != null && !idUsuario.isBlank()) {
                List<Receta> lista = recetaDAO.listarPorUsuario(idUsuario);
                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = listaRecetasAJson(lista);
            } else {
                statusCode = HttpServletResponse.SC_BAD_REQUEST;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Falta idUsuario o idReceta\"}";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al consultar recetas\"}";
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    // POST → crea la receta base (sin ingredientes aún)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String jsonRespuesta;
        int statusCode;

        try {
            Receta receta = new Receta();
            receta.setIdReceta(recetaDAO.obtenerSiguienteId());
            receta.setIdUsuario(request.getParameter("idUsuario"));
            receta.setIdCategoria(request.getParameter("idCategoria"));
            receta.setNombre(request.getParameter("nombre"));
            receta.setRendimiento(parseIntSeguro(request.getParameter("rendimiento"), 1));
            receta.setTiempoPrepMin(parseIntSeguro(request.getParameter("tiempoPrepMin"), 0));
            receta.setMargen(parseDoubleSeguro(request.getParameter("margen"), 0));
            receta.setDificultad(valorOPredeterminado(request.getParameter("dificultad"), "Media"));
            receta.setTip(request.getParameter("tip"));
            receta.setImagen(request.getParameter("imagen"));
            receta.setProcedimiento(request.getParameter("procedimiento"));

            if (receta.getIdUsuario() == null || receta.getIdCategoria() == null
                    || receta.getNombre() == null || receta.getNombre().isBlank()) {
                statusCode = HttpServletResponse.SC_BAD_REQUEST;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Faltan campos requeridos (usuario, categoría o nombre)\"}";
            } else {
                recetaDAO.crear(receta);
                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = String.format(
                    "{\"exito\": true, \"idReceta\": \"%s\", \"mensaje\": \"Receta creada correctamente\"}",
                    receta.getIdReceta()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al crear la receta: " + e.getMessage() + "\"}";
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    // PUT → actualiza los datos generales de la receta (no los ingredientes vinculados)
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, String> datos = leerParametrosDelCuerpo(request);
        String jsonRespuesta;
        int statusCode;

        String idReceta = datos.get("idReceta");

        if (idReceta == null || idReceta.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Falta el idReceta\"}";
        } else {
            try {
                Receta receta = new Receta();
                receta.setIdReceta(idReceta);
                receta.setIdCategoria(datos.get("idCategoria"));
                receta.setNombre(datos.get("nombre"));
                receta.setRendimiento(parseIntSeguro(datos.get("rendimiento"), 1));
                receta.setTiempoPrepMin(parseIntSeguro(datos.get("tiempoPrepMin"), 0));
                receta.setMargen(parseDoubleSeguro(datos.get("margen"), 0));
                receta.setDificultad(valorOPredeterminado(datos.get("dificultad"), "Media"));
                receta.setTip(datos.get("tip"));
                receta.setImagen(datos.get("imagen"));
                receta.setProcedimiento(datos.get("procedimiento"));

                recetaDAO.actualizar(receta);

                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = "{\"exito\": true, \"mensaje\": \"Receta actualizada correctamente\"}";

            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al actualizar la receta\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    // DELETE → elimina la receta. Requiere ?idReceta=R001
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String idReceta = request.getParameter("idReceta");
        String jsonRespuesta;
        int statusCode;

        if (idReceta == null || idReceta.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Falta el idReceta\"}";
        } else {
            try {
                recetaDAO.eliminar(idReceta);
                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = "{\"exito\": true, \"mensaje\": \"Receta eliminada\"}";
            } catch (SQLException e) {
                statusCode = HttpServletResponse.SC_CONFLICT;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"No se puede eliminar: esta receta ya tiene pedidos asociados\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    private String listaRecetasAJson(List<Receta> lista) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Receta r = lista.get(i);
            json.append(String.format(
                "{\"idReceta\": \"%s\", \"idCategoria\": \"%s\", \"nombreCategoria\": \"%s\", "
                + "\"nombre\": \"%s\", \"rendimiento\": %d, \"tiempoPrepMin\": %d, "
                + "\"costo\": %.2f, \"precio\": %.2f, \"margen\": %.2f, \"dificultad\": \"%s\", "
                + "\"tip\": %s, \"imagen\": %s}",
                r.getIdReceta(), r.getIdCategoria(), escaparJson(r.getNombreCategoria()),
                escaparJson(r.getNombre()), r.getRendimiento(), r.getTiempoPrepMin(),
                r.getCosto(), r.getPrecio(), r.getMargen(), r.getDificultad(),
                aJsonStringONull(r.getTip()), aJsonStringONull(r.getImagen())
            ));
            if (i < lista.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }

    private String recetaADetalleJson(Receta r) {
        StringBuilder json = new StringBuilder();
        json.append(String.format(
            "{\"idReceta\": \"%s\", \"idCategoria\": \"%s\", \"nombreCategoria\": \"%s\", "
            + "\"nombre\": \"%s\", \"rendimiento\": %d, \"tiempoPrepMin\": %d, "
            + "\"costo\": %.2f, \"precio\": %.2f, \"margen\": %.2f, \"dificultad\": \"%s\", "
            + "\"tip\": %s, \"imagen\": %s, \"procedimiento\": %s, \"gananciaNeta\": %.2f, \"ingredientes\": [",
            r.getIdReceta(), r.getIdCategoria(), escaparJson(r.getNombreCategoria()),
            escaparJson(r.getNombre()), r.getRendimiento(), r.getTiempoPrepMin(),
            r.getCosto(), r.getPrecio(), r.getMargen(), r.getDificultad(),
            aJsonStringONull(r.getTip()), aJsonStringONull(r.getImagen()),
            aJsonStringONull(r.getProcedimiento()), r.calcularGananciaNeta()
        ));

        List<RecetaIngrediente> ingredientes = r.getIngredientes();
        for (int i = 0; i < ingredientes.size(); i++) {
            RecetaIngrediente ri = ingredientes.get(i);
            json.append(String.format(
                "{\"idIngrediente\": \"%s\", \"nombreIngrediente\": \"%s\", \"cantidad\": %.2f, "
                + "\"unidad\": \"%s\", \"costoProporcional\": %.2f}",
                ri.getIdIngrediente(), escaparJson(ri.getNombreIngrediente()),
                ri.getCantidad(), ri.getUnidad(), ri.getCostoProporcional()
            ));
            if (i < ingredientes.size() - 1) json.append(",");
        }

        json.append("]}");
        return json.toString();
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

    private int parseIntSeguro(String valor, int porDefecto) {
        try {
            return valor == null || valor.isBlank() ? porDefecto : Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }

    private double parseDoubleSeguro(String valor, double porDefecto) {
        try {
            return valor == null || valor.isBlank() ? porDefecto : Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }

    private String valorOPredeterminado(String valor, String porDefecto) {
        return (valor == null || valor.isBlank()) ? porDefecto : valor;
    }

    private String escaparJson(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private String aJsonStringONull(String texto) {
        if (texto == null) return "null";
        return "\"" + escaparJson(texto) + "\"";
    }
}