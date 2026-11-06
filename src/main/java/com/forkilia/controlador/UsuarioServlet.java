package com.forkilia.controlador;

import com.forkilia.dao.UsuarioDAO;
import com.forkilia.dao.UsuarioDAOImpl;
import com.forkilia.modelo.Usuario;
import com.forkilia.servicio.AutenticacionService;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "UsuarioServlet", urlPatterns = {"/api/usuarios"})
public class UsuarioServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private final AutenticacionService authService = new AutenticacionService();



    // OPTIONS → responde al "preflight" que el navegador manda antes de un PUT
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // GET → dos modos:
    //   sin parámetros      -> lista todos los usuarios (para el Admin)
    //   ?idUsuario=U001     -> detalle de un solo usuario (para Mi Perfil / Configuración)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String idUsuario = request.getParameter("idUsuario");
        String jsonRespuesta;
        int statusCode;

        try {
            if (idUsuario != null && !idUsuario.isBlank()) {
                Usuario u = usuarioDAO.buscarPorId(idUsuario);
                if (u == null) {
                    statusCode = HttpServletResponse.SC_NOT_FOUND;
                    jsonRespuesta = "{\"exito\": false, \"mensaje\": \"No se encontró el usuario\"}";
                } else {
                    statusCode = HttpServletResponse.SC_OK;
                    jsonRespuesta = usuarioADetalleJson(u);
                }
            } else {
                List<Usuario> usuarios = usuarioDAO.listarTodos();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < usuarios.size(); i++) {
                    json.append(usuarioADetalleJson(usuarios.get(i)));
                    if (i < usuarios.size() - 1) json.append(",");
                }
                json.append("]");
                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = json.toString();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al consultar usuarios\"}";
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    // POST → crear un nuevo usuario
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String nombre = request.getParameter("nombre");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String rol = request.getParameter("rol");

        String jsonRespuesta;
        int statusCode;

        if (nombre == null || username == null || email == null || password == null || rol == null
                || nombre.isBlank() || username.isBlank() || email.isBlank() || password.isBlank() || rol.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Todos los campos son requeridos\"}";
        } else {
            try {
                Usuario nuevo = new Usuario();
                nuevo.setIdUsuario(generarSiguienteId());
                nuevo.setUsername(username);
                nuevo.setPassword(authService.encriptarPassword(password));
                nuevo.setEmail(email);
                nuevo.setNombre(nombre);
                nuevo.setRol(rol);
                nuevo.setFechaCreacion(LocalDate.now());

                usuarioDAO.crear(nuevo);

                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = String.format(
                    "{\"exito\": true, \"idUsuario\": \"%s\", \"mensaje\": \"Usuario creado correctamente\"}",
                    nuevo.getIdUsuario()
                );

            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al crear usuario: " + e.getMessage() + "\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    // PUT → tres modos según el campo "accion":
    //   accion=reactivar         -> reactiva una cuenta desactivada
    //   accion=actualizarNegocio -> actualiza NombreNegocio/Direccion/TelefonoNegocio/CorreoContacto
    //   (sin accion)             -> actualiza los datos generales (nombre, username, email, rol, password opcional)
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, String> datos = leerParametrosDelCuerpo(request);

        String idUsuario = datos.get("idUsuario");
        String accion = datos.get("accion");

        String jsonRespuesta;
        int statusCode;

        if (idUsuario == null || idUsuario.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Falta el ID del usuario\"}";
        } else if ("reactivar".equals(accion)) {
            try {
                usuarioDAO.actualizarActivo(idUsuario, true);
                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = "{\"exito\": true, \"mensaje\": \"Usuario reactivado\"}";
            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al reactivar el usuario\"}";
            }
        } else if ("actualizarNegocio".equals(accion)) {
            try {
                usuarioDAO.actualizarDatosNegocio(
                    idUsuario,
                    datos.get("nombreNegocio"),
                    datos.get("direccion"),
                    datos.get("telefonoNegocio"),
                    datos.get("correoContacto")
                );
                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = "{\"exito\": true, \"mensaje\": \"Datos del negocio actualizados\"}";
            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al actualizar los datos del negocio\"}";
            }
        } else {
            String nombre = datos.get("nombre");
            String username = datos.get("username");
            String email = datos.get("email");
            String rol = datos.get("rol");
            String nuevaPassword = datos.get("password");

            try {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(idUsuario);
                usuario.setNombre(nombre);
                usuario.setUsername(username);
                usuario.setEmail(email);
                usuario.setRol(rol);

                if (nuevaPassword != null && !nuevaPassword.isBlank()) {
                    usuario.setPassword(authService.encriptarPassword(nuevaPassword));
                }

                usuarioDAO.actualizar(usuario);

                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = "{\"exito\": true, \"mensaje\": \"Usuario actualizado correctamente\"}";

            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al actualizar usuario\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    // DELETE → desactiva un usuario (no lo borra, preserva su historial).
    // Requiere ?idUsuario=U001
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
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
                usuarioDAO.actualizarActivo(idUsuario, false);
                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = "{\"exito\": true, \"mensaje\": \"Usuario desactivado\"}";
            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al desactivar el usuario\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    private String usuarioADetalleJson(Usuario u) {
        return String.format(
            "{\"idUsuario\": \"%s\", \"username\": \"%s\", \"nombre\": \"%s\", \"email\": \"%s\", \"rol\": \"%s\", "
            + "\"activo\": %b, \"fechaCreacion\": \"%s\", \"nombreNegocio\": %s, \"direccion\": %s, "
            + "\"telefonoNegocio\": %s, \"correoContacto\": %s}",
            u.getIdUsuario(), u.getUsername(), escaparJson(u.getNombre()), u.getEmail(), u.getRol(),
            u.isActivo(), u.getFechaCreacion() != null ? u.getFechaCreacion().toString() : "",
            aJsonStringONull(u.getNombreNegocio()), aJsonStringONull(u.getDireccion()),
            aJsonStringONull(u.getTelefonoNegocio()), aJsonStringONull(u.getCorreoContacto())
        );
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
                String clave = java.net.URLDecoder.decode(partes[0], "UTF-8");
                String valor = java.net.URLDecoder.decode(partes[1], "UTF-8");
                mapa.put(clave, valor);
            }
        }
        return mapa;
    }

    private String generarSiguienteId() throws SQLException {
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        int maxNumero = 0;

        for (Usuario u : usuarios) {
            String idSinLetra = u.getIdUsuario().substring(1);
            int numero = Integer.parseInt(idSinLetra);
            if (numero > maxNumero) {
                maxNumero = numero;
            }
        }

        int siguiente = maxNumero + 1;
        return String.format("U%03d", siguiente);
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