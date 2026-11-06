package com.forkilia.controlador;
import com.forkilia.modelo.Usuario;
import com.forkilia.modelo.Administrador;
import com.forkilia.modelo.Emprendedor;
import com.forkilia.servicio.AutenticacionService;
import com.forkilia.servicio.CuentaInactivaException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@WebServlet(name = "LoginServlet", urlPatterns = {"/api/login"})
public class LoginServlet extends HttpServlet {
    private final AutenticacionService authService = new AutenticacionService();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String jsonRespuesta;
        int statusCode;
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
            jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Usuario y contraseña son requeridos\"}";
        } else {
            try {
                Usuario usuario = authService.login(username, password);
                if (usuario != null) {

                    // Aquí aplicamos herencia y polimorfismo: según el rol,
                    // creamos la clase HIJA correspondiente (Administrador o Emprendedor).
                    // Ambas son un Usuario (herencia), pero cada una sabe responder
                    // panelPrincipal() a su manera (polimorfismo).
                    Usuario usuarioTipado = usuario.esAdmin()
                        ? new Administrador(usuario)
                        : new Emprendedor(usuario);


                    statusCode = HttpServletResponse.SC_OK;
                    jsonRespuesta = String.format(
                        "{\"exito\": true, \"idUsuario\": \"%s\", \"nombre\": \"%s\", \"rol\": \"%s\"}",
                        usuarioTipado.getIdUsuario(), usuarioTipado.getNombre(), usuarioTipado.getRol()
                    );
                } else {
                    statusCode = HttpServletResponse.SC_UNAUTHORIZED;
                    jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Usuario o contraseña incorrectos\"}";
                }
            } catch (CuentaInactivaException e) {
                statusCode = HttpServletResponse.SC_FORBIDDEN;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"" + e.getMessage() + "\"}";
            } catch (Exception e) {
                e.printStackTrace();
                statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                jsonRespuesta = "{\"exito\": false, \"mensaje\": \"Error del servidor: " + e.getMessage() + "\"}";
            }
        }
        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonRespuesta);
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        response.getWriter().print("{\"mensaje\": \"Usa POST para iniciar sesión\"}");
    }  
}