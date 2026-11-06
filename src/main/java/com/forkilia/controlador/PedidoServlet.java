package com.forkilia.controlador;

import com.forkilia.dao.PedidoDAO;
import com.forkilia.dao.PedidoDAOImpl;
import com.forkilia.modelo.Pedido;
import com.forkilia.modelo.PedidoDetalleItem;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "PedidoServlet", urlPatterns = {"/api/pedidos"})
public class PedidoServlet extends HttpServlet {

    private final PedidoDAO pedidoDAO = new PedidoDAOImpl();
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // GET → dos modos:
    //   ?idUsuario=U001   -> lista de pedidos (sin detalle de productos)
    //   ?idPedido=PD001   -> detalle completo con productos
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String idPedido = request.getParameter("idPedido");
        String idUsuario = request.getParameter("idUsuario");

        String jsonRespuesta;
        int statusCode;

        try {
            if (idPedido != null && !idPedido.isBlank()) {
                Pedido pedido = pedidoDAO.obtenerDetalle(idPedido);
                if (pedido == null) {
                    statusCode = HttpServletResponse.SC_NOT_FOUND;
                    jsonRespuesta = "{\"exito\": false, \"mensaje\": \"No se encontró el pedido\"}";
                } else {
                    statusCode = HttpServletResponse.SC_OK;
                    jsonRespuesta = pedidoADetalleJson(pedido);
                }
            } else if (idUsuario != null && !idUsuario.isBlank()) {
                List<Pedido> lista = pedidoDAO.listarPorUsuario(idUsuario);
                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = listaPedidosAJson(lista);
            } else {
                statusCode = HttpServletResponse.SC_BAD_REQUEST;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Falta idUsuario o idPedido\"}";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al consultar pedidos\"}";
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    // POST → crea un pedido nuevo.
    // Requiere: idUsuario, idCliente, tipo, anticipo, notas (opcional), mesa (opcional),
    // idsReceta (lista separada por comas, ej: "R001,R002") y cantidades (lista paralela, ej: "2,1")
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String idUsuario = request.getParameter("idUsuario");
        String idCliente = request.getParameter("idCliente");
        String tipo = request.getParameter("tipo");
        String mesa = request.getParameter("mesa");
        String anticipoStr = request.getParameter("anticipo");
        String notas = request.getParameter("notas");
        String idsRecetaStr = request.getParameter("idsReceta");
        String cantidadesStr = request.getParameter("cantidades");

        String jsonRespuesta;
        int statusCode;

        if (idUsuario == null || idCliente == null || tipo == null
                || idsRecetaStr == null || cantidadesStr == null
                || idsRecetaStr.isBlank() || cantidadesStr.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Faltan campos requeridos o no seleccionaste productos\"}";
        } else {
            try {
                String[] idsReceta = idsRecetaStr.split(",");
                String[] cantidades = cantidadesStr.split(",");

                if (idsReceta.length != cantidades.length) {
                    statusCode = HttpServletResponse.SC_BAD_REQUEST;
                    jsonRespuesta = "{\"exito\": false, \"mensaje\": \"La lista de productos y cantidades no coincide\"}";
                } else {
                    List<PedidoDetalleItem> detalle = new ArrayList<>();
                    for (int i = 0; i < idsReceta.length; i++) {
                        PedidoDetalleItem item = new PedidoDetalleItem();
                        item.setIdReceta(idsReceta[i].trim());
                        item.setCantidad(Integer.parseInt(cantidades[i].trim()));
                        detalle.add(item);
                    }

                    Pedido pedido = new Pedido();
                    pedido.setIdPedido(pedidoDAO.obtenerSiguienteId());
                    pedido.setFolio(pedidoDAO.obtenerSiguienteFolio());
                    pedido.setIdCliente(idCliente);
                    pedido.setIdUsuario(idUsuario);
                    pedido.setTipo(tipo);
                    pedido.setMesa((mesa == null || mesa.isBlank()) ? null : mesa);
                    pedido.setAnticipo(parseDoubleSeguro(anticipoStr, 0));
                    pedido.setStatus("pendiente");
                    pedido.setNotas(notas);

                    pedidoDAO.crear(pedido, detalle);

                    statusCode = HttpServletResponse.SC_OK;
                    jsonRespuesta = String.format(
                        "{\"exito\": true, \"idPedido\": \"%s\", \"folio\": \"%s\", \"mensaje\": \"Pedido creado correctamente\"}",
                        pedido.getIdPedido(), pedido.getFolio()
                    );
                }
            } catch (NumberFormatException e) {
                statusCode = HttpServletResponse.SC_BAD_REQUEST;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Alguna cantidad o el anticipo no son números válidos\"}";
            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al crear el pedido: " + e.getMessage() + "\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    // PUT → cambia el status del pedido. Requiere idPedido y status
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, String> datos = leerParametrosDelCuerpo(request);
        String idPedido = datos.get("idPedido");
        String status = datos.get("status");

        String jsonRespuesta;
        int statusCode;

        if (idPedido == null || status == null || idPedido.isBlank() || status.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Faltan idPedido o status\"}";
        } else {
            try {
                pedidoDAO.actualizarStatus(idPedido, status);
                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = "{\"exito\": true, \"mensaje\": \"Status actualizado correctamente\"}";
            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al actualizar el status\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

    // DELETE → elimina el pedido. Requiere ?idPedido=PD001
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String idPedido = request.getParameter("idPedido");
        String jsonRespuesta;
        int statusCode;

        if (idPedido == null || idPedido.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Falta el idPedido\"}";
        } else {
            try {
                pedidoDAO.eliminar(idPedido);
                statusCode = HttpServletResponse.SC_OK;
                jsonRespuesta = "{\"exito\": true, \"mensaje\": \"Pedido eliminado\"}";
            } catch (SQLException e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error al eliminar el pedido\"}";
            }
        }

        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }

private String listaPedidosAJson(List<Pedido> lista) {
    StringBuilder json = new StringBuilder("[");
    for (int i = 0; i < lista.size(); i++) {
        Pedido p = lista.get(i);
        json.append(String.format(
            "{\"idPedido\": \"%s\", \"folio\": \"%s\", \"nombreCliente\": \"%s\", \"telefonoCliente\": %s, "
            + "\"tipo\": \"%s\", \"mesa\": %s, \"total\": %.2f, \"anticipo\": %.2f, \"restante\": %.2f, "
            + "\"status\": \"%s\", \"notas\": %s, \"fecha\": \"%s\", \"descuentoAplicado\": %b}",
            p.getIdPedido(), p.getFolio(), escaparJson(p.getNombreCliente()),
            aJsonStringONull(p.getTelefonoCliente()), p.getTipo(), aJsonStringONull(p.getMesa()),
            p.getTotal(), p.getAnticipo(), p.calcularRestante(), p.getStatus(),
            aJsonStringONull(p.getNotas()), p.getFecha() != null ? p.getFecha().format(FORMATO_FECHA) : "",
            p.isDescuentoAplicado()
        ));
        if (i < lista.size() - 1) json.append(",");
    }
    json.append("]");
    return json.toString();
}

private String pedidoADetalleJson(Pedido p) {
    StringBuilder json = new StringBuilder();
    json.append(String.format(
        "{\"idPedido\": \"%s\", \"folio\": \"%s\", \"idCliente\": \"%s\", \"nombreCliente\": \"%s\", "
        + "\"telefonoCliente\": %s, \"tipo\": \"%s\", \"mesa\": %s, \"total\": %.2f, \"anticipo\": %.2f, "
        + "\"restante\": %.2f, \"status\": \"%s\", \"notas\": %s, \"fecha\": \"%s\", \"descuentoAplicado\": %b, \"detalle\": [",
        p.getIdPedido(), p.getFolio(), p.getIdCliente(), escaparJson(p.getNombreCliente()),
        aJsonStringONull(p.getTelefonoCliente()), p.getTipo(), aJsonStringONull(p.getMesa()),
        p.getTotal(), p.getAnticipo(), p.calcularRestante(), p.getStatus(),
        aJsonStringONull(p.getNotas()), p.getFecha() != null ? p.getFecha().format(FORMATO_FECHA) : "",
        p.isDescuentoAplicado()
    ));

    List<PedidoDetalleItem> detalle = p.getDetalle();
    for (int i = 0; i < detalle.size(); i++) {
        PedidoDetalleItem item = detalle.get(i);
        json.append(String.format(
            "{\"idReceta\": \"%s\", \"nombreReceta\": \"%s\", \"cantidad\": %d, \"precioUnitario\": %.2f, \"subtotal\": %.2f}",
            item.getIdReceta(), escaparJson(item.getNombreReceta()), item.getCantidad(),
            item.getPrecioUnitario(), item.calcularSubtotal()
        ));
        if (i < detalle.size() - 1) json.append(",");
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

    private double parseDoubleSeguro(String valor, double porDefecto) {
        try {
            return valor == null || valor.isBlank() ? porDefecto : Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            return porDefecto;
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
}