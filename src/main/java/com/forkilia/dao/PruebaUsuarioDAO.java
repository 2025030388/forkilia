package com.forkilia.dao;

import com.forkilia.modelo.Usuario;
import java.time.LocalDate;

public class PruebaUsuarioDAO 
{
    public static void main(String[] args) 
    {
        UsuarioDAO dao = new UsuarioDAOImpl();

        try {
            // 1. Creamos un usuario de prueba
            Usuario nuevo = new Usuario();
            nuevo.setIdUsuario("U999");
            nuevo.setUsername("prueba_test");
            nuevo.setPassword("1234"); // luego la cambiaremos por un hash
            nuevo.setEmail("prueba@forkilia.com");
            nuevo.setNombre("Usuario de Prueba");
            nuevo.setRol("Emprendedor");
            nuevo.setFechaCreacion(LocalDate.now());

            dao.crear(nuevo);
            System.out.println("✅ Usuario creado correctamente");

            // 2. Lo buscamos de vuelta, para confirmar que sí quedó guardado
            Usuario encontrado = dao.buscarPorUsername("prueba_test");
            if (encontrado != null) {
                System.out.println("✅ Usuario encontrado: " + encontrado.getNombre()
                        + " | Rol: " + encontrado.getRol());
            } else {
                System.out.println("❌ No se encontró el usuario recién creado");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}