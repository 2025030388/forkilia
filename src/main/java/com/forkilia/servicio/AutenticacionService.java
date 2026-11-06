package com.forkilia.servicio;
import com.forkilia.dao.UsuarioDAO;
import com.forkilia.dao.UsuarioDAOImpl;
import com.forkilia.modelo.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;
public class AutenticacionService 
{
    private final UsuarioDAO usuarioDAO;
    public AutenticacionService()
    {
        this.usuarioDAO = new UsuarioDAOImpl();
    }
    /**
     * Intenta iniciar sesión.
     * Devuelve el Usuario si las credenciales son correctas, o null si no lo son.
     * Lanza CuentaInactivaException si la cuenta existe y la contraseña es correcta,
     * pero el usuario fue desactivado por un Admin.
     */
    public Usuario login(String username, String passwordEscrita) throws SQLException, CuentaInactivaException 
    {
        Usuario usuario = usuarioDAO.buscarPorUsername(username);
        if (usuario == null) {
            return null; // no existe ese username
        }
        boolean coincide = BCrypt.checkpw(passwordEscrita, usuario.getPassword());
        if (!coincide) {
            return null; // contraseña incorrecta
        }
        if (!usuario.isActivo()) {
            throw new CuentaInactivaException("Esta cuenta fue desactivada. Contacta al administrador de Forkilia.");
        }
        return usuario; // login correcto
    }
    /**
     * Convierte una contraseña en texto plano a un hash seguro,
     * listo para guardarse en la base de datos.
     * Se usa cuando el Admin crea un nuevo usuario.
     */
    public String encriptarPassword(String passwordPlano) {
        return BCrypt.hashpw(passwordPlano, BCrypt.gensalt());
    }
}