package com.forkilia.controlador;

import com.forkilia.dao.CategoriaDAO;
import com.forkilia.dao.CategoriaDAOImpl;
import com.forkilia.modelo.Categoria;

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

@WebServlet(name = "CategoriaServlet", urlPatterns = {"/api/categorias"})
public class CategoriaServlet extends HttpServlet {

    private final CategoriaDAO categoriaDAO = new CategoriaDAOImpl();

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

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
                List<Categoria> lista = categoriaDAO.listarPorUsuario(idUsuario);
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < lista.size(); i++) {
                    Categoria c = lista.get(i);
                    json.append(String.format(
                        "{\"idCategoria\": \"%s\", \"nombre\": \"%s\"}",
                        c.getIdCategoria(), escaparJson(c.getNombre())
                    ));
                    if (i < lista.size() - 1) json.append(",");
                }
                json.append("]");
                jsonRespuesta = json.toString();
                statusCode = HttpServletResponse.SC_OK;

            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al listar categorías\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String idUsuario = request.getParameter("idUsuario");
        String nombre = request.getParameter("nombre");

        String jsonRespuesta;
        int statusCode;

        if (idUsuario == null || nombre == null || idUsuario.isBlank() || nombre.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Faltan campos requeridos\"}";
        } else {
            try {
                if (categoriaDAO.existeNombreParaUsuario(idUsuario, nombre.trim())) {
                    statusCode = HttpServletResponse.SC_CONFLICT;
                    jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Ya tienes una categoría con ese nombre\"}";
                } else {
                    Categoria categoria = new Categoria();
                    categoria.setIdCategoria(categoriaDAO.obtenerSiguienteId());
                    categoria.setIdUsuario(idUsuario);
                    categoria.setNombre(nombre.trim());

                    categoriaDAO.crear(categoria);

                    statusCode = HttpServletResponse.SC_OK;
                    jsonRespuesta = String.format(
                        "{\"exito\": true, \"idCategoria\": \"%s\", \"mensaje\": \"Categoría creada correctamente\"}",
                        categoria.getIdCategoria()
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al crear categoría: " + e.getMessage() + "\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, String> datos = leerParametrosDelCuerpo(request);
        String idCategoria = datos.get("idCategoria");
        String nombre = datos.get("nombre");

        String jsonRespuesta;
        int statusCode;

        if (idCategoria == null || nombre == null || idCategoria.isBlank() || nombre.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Faltan campos requeridos\"}";
        } else {
            try {
                Categoria categoria = new Categoria();
                categoria.setIdCategoria(idCategoria);
                categoria.setNombre(nombre.trim());

                categoriaDAO.actualizar(categoria);

                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = "{\"exito\": true, \"mensaje\": \"Categoría actualizada correctamente\"}";

            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al actualizar categoría\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String idCategoria = request.getParameter("idCategoria");
        String jsonRespuesta;
        int statusCode;

        if (idCategoria == null || idCategoria.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Falta el idCategoria\"}";
        } else {
            try {
                categoriaDAO.eliminar(idCategoria);
                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = "{\"exito\": true, \"mensaje\": \"Categoría eliminada\"}";
            } catch (SQLException e) {
                statusCode = HttpServletResponse.SC_CONFLICT;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"No se puede eliminar: hay recetas usando esta categoría\"}";
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