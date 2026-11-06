package com.forkilia.servicio;

import com.forkilia.dao.UsuarioDAO;
import com.forkilia.dao.UsuarioDAOImpl;
import com.forkilia.modelo.Usuario;
import java.time.LocalDate;

public class PruebaLogin
{
    public static void main(String[] args) {
    AutenticacionService auth = new AutenticacionService();
    String hash = auth.encriptarPassword("cheflux2024");
    System.out.println("HASH GENERADO: " + hash);
    }
}
