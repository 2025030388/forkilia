package com.forkilia.servicio;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Como la tabla Mermas (ni Ingredients) tiene una columna de "categoría"
 * (Proteínas, Verduras, etc.), este servicio asigna una categoría en el
 * backend según palabras clave encontradas en el nombre del ingrediente.
 * Si ninguna palabra clave coincide, cae en "Otros".
 *
 * El orden de las entradas importa: se evalúan de arriba hacia abajo y
 * se usa la primera coincidencia.
 */
public class ClasificadorMermaService {

    private static final Map<String, String> PALABRAS_CLAVE = new LinkedHashMap<>();

    static {
        // Proteínas
        PALABRAS_CLAVE.put("carne", "Proteínas");
        PALABRAS_CLAVE.put("pollo", "Proteínas");
        PALABRAS_CLAVE.put("pescado", "Proteínas");
        PALABRAS_CLAVE.put("camaron", "Proteínas");
        PALABRAS_CLAVE.put("camarón", "Proteínas");
        PALABRAS_CLAVE.put("chorizo", "Proteínas");
        PALABRAS_CLAVE.put("huevo", "Proteínas");
        PALABRAS_CLAVE.put("tocino", "Proteínas");
        PALABRAS_CLAVE.put("jamon", "Proteínas");
        PALABRAS_CLAVE.put("jamón", "Proteínas");

        // Verduras
        PALABRAS_CLAVE.put("chile", "Verduras");
        PALABRAS_CLAVE.put("cebolla", "Verduras");
        PALABRAS_CLAVE.put("jitomate", "Verduras");
        PALABRAS_CLAVE.put("tomate", "Verduras");
        PALABRAS_CLAVE.put("lechuga", "Verduras");
        PALABRAS_CLAVE.put("jamaica", "Verduras");
        PALABRAS_CLAVE.put("cilantro", "Verduras");
        PALABRAS_CLAVE.put("zanahoria", "Verduras");
        PALABRAS_CLAVE.put("papa", "Verduras");
        PALABRAS_CLAVE.put("aguacate", "Verduras");
        PALABRAS_CLAVE.put("limon", "Verduras");
        PALABRAS_CLAVE.put("limón", "Verduras");

        // Granos y harinas
        PALABRAS_CLAVE.put("tortilla", "Granos");
        PALABRAS_CLAVE.put("harina", "Granos");
        PALABRAS_CLAVE.put("arroz", "Granos");
        PALABRAS_CLAVE.put("frijol", "Granos");
        PALABRAS_CLAVE.put("pasta", "Granos");
        PALABRAS_CLAVE.put("pan", "Granos");

        // Lácteos
        PALABRAS_CLAVE.put("queso", "Lácteos");
        PALABRAS_CLAVE.put("crema", "Lácteos");
        PALABRAS_CLAVE.put("leche", "Lácteos");
        PALABRAS_CLAVE.put("mantequilla", "Lácteos");
        PALABRAS_CLAVE.put("yogurt", "Lácteos");

        // Bebidas
        PALABRAS_CLAVE.put("agua", "Bebidas");
        PALABRAS_CLAVE.put("refresco", "Bebidas");
        PALABRAS_CLAVE.put("jugo", "Bebidas");
    }

    public String clasificar(String nombreIngrediente) {
        if (nombreIngrediente == null) {
            return "Otros";
        }
        String nombreMinuscula = nombreIngrediente.toLowerCase();

        for (Map.Entry<String, String> entrada : PALABRAS_CLAVE.entrySet()) {
            if (nombreMinuscula.contains(entrada.getKey())) {
                return entrada.getValue();
            }
        }
        return "Otros";
    }
}