package com.forkilia.controlador;

import com.forkilia.dao.ClienteDAO;
import com.forkilia.dao.ClienteDAOImpl;
import com.forkilia.modelo.Cliente;

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

@WebServlet(name = "ClienteServlet", urlPatterns = {"/api/clientes"})
public class ClienteServlet extends HttpServlet {

    private final ClienteDAO clienteDAO = new ClienteDAOImpl();

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // GET → lista los clientes de un usuario. Requiere ?idUsuario=U001
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
                List<Cliente> lista = clienteDAO.listarPorUsuario(idUsuario);
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < lista.size(); i++) {
                    Cliente c = lista.get(i);
                    json.append(String.format(
                        "{\"idCliente\": \"%s\", \"nombre\": \"%s\", \"telefono\": %s, \"direccion\": %s, "
                        + "\"empresa\": %s, \"numPersonas\": %s, \"esEmpresa\": %b}",
                        c.getIdCliente(), escaparJson(c.getNombre()),
                        aJsonStringONull(c.getTelefono()), aJsonStringONull(c.getDireccion()),
                        aJsonStringONull(c.getEmpresa()), aJsonNumeroONull(c.getNumPersonas()),
                        c.esEmpresa()
                    ));
                    if (i < lista.size() - 1) json.append(",");
                }
                json.append("]");
                jsonRespuesta = json.toString();
                statusCode = HttpServletResponse.SC_OK;

            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al listar clientes\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    // POST → crea un nuevo cliente. Requiere idUsuario y nombre.
    // empresa/numPersonas son opcionales (solo si es cliente empresa)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String idUsuario = request.getParameter("idUsuario");
        String nombre = request.getParameter("nombre");
        String telefono = request.getParameter("telefono");
        String direccion = request.getParameter("direccion");
        String empresa = request.getParameter("empresa");
        String numPersonasStr = request.getParameter("numPersonas");

        String jsonRespuesta;
        int statusCode;

        if (idUsuario == null || nombre == null || idUsuario.isBlank() || nombre.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Faltan campos requeridos\"}";
        } else {
            try {
                Cliente cliente = new Cliente();
                cliente.setIdCliente(clienteDAO.obtenerSiguienteId());
                cliente.setIdUsuario(idUsuario);
                cliente.setNombre(nombre.trim());
                cliente.setTelefono(telefono);
                cliente.setDireccion(direccion);
                cliente.setEmpresa((empresa == null || empresa.isBlank()) ? null : empresa.trim());
                cliente.setNumPersonas(parseIntSeguro(numPersonasStr));

                clienteDAO.crear(cliente);

                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = String.format(
                    "{\"exito\": true, \"idCliente\": \"%s\", \"mensaje\": \"Cliente creado correctamente\"}",
                    cliente.getIdCliente()
                );
            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al crear cliente: " + e.getMessage() + "\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    // PUT → actualiza un cliente existente
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, String> datos = leerParametrosDelCuerpo(request);
        String idCliente = datos.get("idCliente");

        String jsonRespuesta;
        int statusCode;

        if (idCliente == null || idCliente.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Falta el idCliente\"}";
        } else {
            try {
                Cliente cliente = new Cliente();
                cliente.setIdCliente(idCliente);
                cliente.setNombre(datos.get("nombre"));
                cliente.setTelefono(datos.get("telefono"));
                cliente.setDireccion(datos.get("direccion"));

                String empresa = datos.get("empresa");
                cliente.setEmpresa((empresa == null || empresa.isBlank()) ? null : empresa.trim());
                cliente.setNumPersonas(parseIntSeguro(datos.get("numPersonas")));

                clienteDAO.actualizar(cliente);

                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = "{\"exito\": true, \"mensaje\": \"Cliente actualizado correctamente\"}";

            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al actualizar cliente\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    // DELETE → elimina un cliente. Requiere ?idCliente=CL001
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String idCliente = request.getParameter("idCliente");
        String jsonRespuesta;
        int statusCode;

        if (idCliente == null || idCliente.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Falta el idCliente\"}";
        } else {
            try {
                clienteDAO.eliminar(idCliente);
                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = "{\"exito\": true, \"mensaje\": \"Cliente eliminado\"}";
            } catch (SQLException e) {
                statusCode = HttpServletResponse.SC_CONFLICT;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"No se puede eliminar: este cliente ya tiene pedidos registrados\"}";
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

    private Integer parseIntSeguro(String valor) {
        try {
            return (valor == null || valor.isBlank()) ? null : Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String escaparJson(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String aJsonStringONull(String texto) {
        if (texto == null || texto.isBlank()) return "null";
        return "\"" + escaparJson(texto) + "\"";
    }

    private String aJsonNumeroONull(Integer numero) {
        return numero == null ? "null" : numero.toString();
    }
}