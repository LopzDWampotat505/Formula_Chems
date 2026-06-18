import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class ManejadorArchivos {

    /**
     * TAREA 1A: Guarda la configuración estática de los equipos.
     * Anida Escuderías -> Vehículos y Pilotos.
     */
    public static void guardarEquiposJson(Temporada t) {
        String nombreArchivo = t.getAño() + "_equipos.json";
        StringBuilder sb = new StringBuilder();

        sb.append("{\n");
        sb.append("  \"año_temporada\": ").append(t.getAño()).append(",\n");
        sb.append("  \"escuderias\": [\n");

        List<Escuderia> escuderias = t.getEscuderia();
        for (int i = 0; i < escuderias.size(); i++) {
            Escuderia e = escuderias.get(i);
            sb.append("    {\n");
            sb.append("      \"nombre\": \"").append(e.getNombre()).append("\",\n");

            // Anidamos los vehículos del equipo
            sb.append("      \"vehiculos\": [\n");
            List<Vehiculo> vehiculos = e.getVehiculos();
            for (int j = 0; j < vehiculos.size(); j++) {
                Vehiculo v = vehiculos.get(j);
                sb.append("        { \"id\": ").append(j + 1)
                        .append(", \"motor\": ").append(v.getMotor())
                        .append(", \"llantas\": ").append(v.getLlantas())
                        .append(", \"peso\": ").append(v.getPeso()).append(" }");
                if (j < vehiculos.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("      ],\n");

            // Anidamos los pilotos del equipo
            sb.append("      \"pilotos\": [\n");
            List<Driver> pilotos = e.getDrivers();
            for (int j = 0; j < pilotos.size(); j++) {
                Driver d = pilotos.get(j);
                sb.append("        { \"nombre\": \"").append(d.getNombre()).append("\", ")
                        .append("\"nacionalidad\": \"").append(d.getNacionalidad()).append("\", ")
                        .append("\"habilidad_base\": ").append(d.getHabilidad()).append(" }"); // Habilidad cruda
                if (j < pilotos.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("      ]\n");
            sb.append("    }");

            if (i < escuderias.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n");
        sb.append("}");

        escribirTextoEnDisco(nombreArchivo, sb.toString());
    }

    /**
     * TAREA 1B: Guarda el progreso dinámico de las carreras.
     * Implementa la Opción B (formato tabla) para los mapas de tiempos.
     */
    public static void guardarProgresoCarrerasJson(Temporada t) {
        String nombreArchivo = t.getAño() + "_progreso.json";
        StringBuilder sb = new StringBuilder();

        sb.append("{\n");
        sb.append("  \"año_temporada\": ").append(t.getAño()).append(",\n");
        sb.append("  \"carreras\": [\n");

        List<Carrera> listaCarreras = t.getCarrera();
        for (int i = 0; i < listaCarreras.size(); i++) {
            Carrera c = listaCarreras.get(i);
            sb.append("    {\n");
            sb.append("      \"ronda\": ").append(i + 1).append(",\n");
            sb.append("      \"nombre_circuito\": \"").append(c.getNombre()).append("\",\n");

            // Tabla de clasificación del Sábado (Opción B)
            sb.append("      \"resultados_sabado\": [\n");
            if (c.getClasificacion() != null && !c.getClasificacion().getParrillaFinal().isEmpty()) {
                Clasification juez = c.getClasificacion();
                List<Driver> grid = juez.getParrillaFinal();

                for (int j = 0; j < grid.size(); j++) {
                    Driver d = grid.get(j);
                    sb.append("        { \"pos\": ").append(j + 1)
                            .append(", \"piloto\": \"").append(d.getNombre()).append("\", ")
                            .append("\"nacionalidad\": \"").append(d.getNacionalidad()).append("\", ")
                            .append("\"q1\": \"").append(obtenerTiempoString(d, juez.getTiemposQ1())).append("\", ")
                            .append("\"q2\": \"").append(obtenerTiempoString(d, juez.getTiemposQ2())).append("\", ")
                            .append("\"q3\": \"").append(obtenerTiempoString(d, juez.getTiemposQ3())).append("\" }");
                    if (j < grid.size() - 1) sb.append(",");
                    sb.append("\n");
                }
            }
            sb.append("      ],\n");

            // Tabla de resultados del Domingo
            sb.append("      \"resultados_domingo\": [\n");
            if (c.getGranPremio() != null && c.getGranPremio().getResultadosFinales() != null && !c.getGranPremio().getResultadosFinales().isEmpty()) {
                List<Driver> podio = c.getGranPremio().getResultadosFinales();
                int[] escalaPuntos = {25, 18, 15, 12, 10, 8, 6, 4, 2, 1};

                for (int j = 0; j < podio.size(); j++) {
                    Driver d = podio.get(j);
                    int pts = (j < 10) ? escalaPuntos[j] : 0;
                    sb.append("        { \"pos\": ").append(j + 1)
                            .append(", \"piloto\": \"").append(d.getNombre()).append("\", ")
                            .append("\"nacionalidad\": \"").append(d.getNacionalidad()).append("\", ")
                            .append("\"puntos_ganados\": ").append(pts).append(" }");
                    if (j < podio.size() - 1) sb.append(",");
                    sb.append("\n");
                }
            }
            sb.append("      ]\n");
            sb.append("    }");

            if (i < listaCarreras.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n");
        sb.append("}");

        escribirTextoEnDisco(nombreArchivo, sb.toString());
    }


    /**
     * OPCIÓN 9: Lee e imprime en consola un archivo JSON del año indicado.
     * tipo puede ser "equipos" o "progreso"
     */
    public static void leerArchivoJson(int año, String tipo) {
        String nombreArchivo = año + "_" + tipo + ".json";
        System.out.println("\n[LEYENDO JSON] " + nombreArchivo);
        System.out.println("=".repeat(60));

        try (BufferedReader lector = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                System.out.println(linea);
            }
        } catch (IOException e) {
            System.out.println("[ERROR] No se pudo leer el archivo: " + e.getMessage());
            System.out.println("Asegurate de haber guardado el JSON primero (Opcion 8).");
        }

        System.out.println("=".repeat(60) + "\n");
    }

    // --- MÉTODOS DE APOYO ---

    private static String obtenerTiempoString(Driver d, Map<Driver, Float> mapa) {
        if (mapa == null || !mapa.containsKey(d)) return "";
        return String.format("%.3fs", mapa.get(d));
    }

    private static void escribirTextoEnDisco(String archivo, String texto) {
        try (FileWriter fw = new FileWriter(archivo);
             PrintWriter escritor = new PrintWriter(fw)) {
            escritor.print(texto);
            System.out.println("[JSON CREADO] Archivo guardado: " + archivo);
        } catch (IOException e) {
            System.out.println("[ERROR] No se pudo escribir el JSON: " + e.getMessage());
        }
    }
}