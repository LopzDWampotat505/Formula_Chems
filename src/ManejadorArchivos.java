import java.io.*;
import java.util.*;
import java.util.Arrays;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;


public class ManejadorArchivos {

    // --- GUARDAR EQUIPOS ---
    public static void guardarEquiposJson(Temporada t) {
        String nombreArchivo = t.getAño() + "_equipos.json";
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"año_temporada\": ").append(t.getAño()).append(",\n  \"escuderias\": [\n");
        List<Escuderia> escuderias = t.getEscuderia();
        for (int i = 0; i < escuderias.size(); i++) {
            Escuderia e = escuderias.get(i);
            sb.append("    { \"nombre\": \"").append(e.getNombre()).append("\",\n      \"vehiculos\": [");
            for (int j = 0; j < e.getVehiculos().size(); j++) {
                Vehiculo v = e.getVehiculos().get(j);
                sb.append("{ \"id\": ").append(j + 1).append(", \"motor\": ").append(v.getMotor())
                        .append(", \"llantas\": ").append(v.getLlantas()).append(", \"peso\": ").append(v.getPeso()).append(" }");
                if (j < e.getVehiculos().size() - 1) sb.append(",");
            }
            sb.append("],\n      \"pilotos\": [");
            for (int j = 0; j < e.getDrivers().size(); j++) {
                Driver d = e.getDrivers().get(j);
                sb.append("{ \"nombre\": \"").append(d.getNombre()).append("\", \"nacionalidad\": \"").append(d.getNacionalidad())
                        .append("\", \"habilidad_base\": ").append(d.getHabilidad()).append(" }");
                if (j < e.getDrivers().size() - 1) sb.append(",");
            }
            sb.append("]\n    }");
            if (i < escuderias.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n}");
        escribirTextoEnDisco(nombreArchivo, sb.toString());
    }

    // --- GUARDAR PROGRESO ---
    public static void guardarProgresoCarrerasJson(Temporada t) {
        String nombreArchivo = t.getAño() + "_progreso.json";
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"año_temporada\": ").append(t.getAño()).append(",\n  \"carreras\": [\n");
        for (int i = 0; i < t.getCarrera().size(); i++) {
            Carrera c = t.getCarrera().get(i);
            sb.append("    { \"ronda\": ").append(i + 1)
                    .append(", \"nombre_circuito\": \"").append(c.getNombre()).append("\"")
                    .append(", \"dificultad\": ").append(c.getNivelDificultad()).append(" }");
            if (i < t.getCarrera().size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n}");
        escribirTextoEnDisco(nombreArchivo, sb.toString());
    }

    // --- LECTURA VISUAL ---
    public static void leerArchivoJson(int año, String tipo, Component padre) {
        String nombreArchivo = año + "_" + tipo + ".json";
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) sb.append(linea).append("\n");

            JTextArea area = new JTextArea(sb.toString());
            area.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane scroll = new JScrollPane(area);
            scroll.setPreferredSize(new Dimension(500, 400));

            JDialog dlg = new JDialog((Window) SwingUtilities.getWindowAncestor(padre), "Visor: " + nombreArchivo);
            dlg.add(scroll);
            dlg.pack();
            dlg.setLocationRelativeTo(padre);
            dlg.setVisible(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(padre, "Archivo no encontrado: " + nombreArchivo);
        }
    }

    // --- CARGA DE DATOS COMPLETA (vehículos, pilotos y escuderías con sus relaciones) ---
    public static void cargarDatosDesdeJson(int año, List<GranPremio> gps, List<Vehiculo> vehs,
                                            List<Driver> pils, List<Escuderia> escs,
                                            DefaultListModel<String> mGPs, DefaultListModel<String> mVeh,
                                            DefaultListModel<String> mPil, DefaultListModel<String> mEsc) throws IOException {

        gps.clear(); vehs.clear(); pils.clear(); escs.clear();
        mGPs.clear(); mVeh.clear(); mPil.clear(); mEsc.clear();

        // ── 1. CARRERAS (progreso.json) ───────────────────────────────────────
        try (BufferedReader br = new BufferedReader(new FileReader(año + "_progreso.json"))) {
            String l;
            while ((l = br.readLine()) != null) {
                if (l.contains("\"nombre_circuito\":")) {
                    String nom = extraerCadena(l, "nombre_circuito");
                    int dif = 7;
                    if (l.contains("\"dificultad\":")) {
                        try { dif = extraerEntero(l, "dificultad"); } catch (Exception ignored) {}
                    }
                    gps.add(new GranPremio(nom, dif, 75.0f, 85.0f, new ArrayList<>()));
                    mGPs.addElement(nom + " - Dif: " + dif);
                }
            }
        } catch (Exception e) {
            throw new IOException("No se pudo leer " + año + "_progreso.json: " + e.getMessage());
        }

        // ── 2. EQUIPOS (equipos.json) ─────────────────────────────────────────
        // El JSON tiene esta forma:
        // { "escuderias": [
        //     { "nombre": "...", "vehiculos": [...], "pilotos": [...] },
        //     ...
        // ]}
        String archivoEq = año + "_equipos.json";
        String contenido;
        try (BufferedReader br = new BufferedReader(new FileReader(archivoEq))) {
            StringBuilder sb = new StringBuilder();
            String l;
            while ((l = br.readLine()) != null) sb.append(l).append("\n");
            contenido = sb.toString();
        } catch (Exception e) {
            throw new IOException("No se pudo leer " + archivoEq + ": " + e.getMessage());
        }

        int inicioArray = contenido.indexOf("\"escuderias\"");
        if (inicioArray == -1) throw new IOException("El archivo de equipos no tiene la clave 'escuderias'.");

        int idVehGlobal = 0;
        int posCorchete = contenido.indexOf('[', inicioArray);
        int posFin = contenido.lastIndexOf(']');
        if (posCorchete == -1 || posFin == -1) throw new IOException("Formato de JSON de equipos incorrecto.");

        int pos = posCorchete + 1;
        while (pos < posFin) {
            int abre = contenido.indexOf('{', pos);
            if (abre == -1 || abre >= posFin) break;

            int cierra = encontrarCierreObjeto(contenido, abre);
            if (cierra == -1) break;

            String bloqueEsc = contenido.substring(abre, cierra + 1);
            pos = cierra + 1;

            if (!bloqueEsc.contains("\"nombre\":")) continue;
            String nomEsc = extraerCadena(bloqueEsc, "nombre");

            // ── Extraer vehículos de esta escudería ──
            List<Vehiculo> vehiculosEsc = new ArrayList<>();
            int inicioVehs = bloqueEsc.indexOf("\"vehiculos\"");
            if (inicioVehs != -1) {
                int abreVehs = bloqueEsc.indexOf('[', inicioVehs);
                int cierraVehs = bloqueEsc.indexOf(']', abreVehs);
                if (abreVehs != -1 && cierraVehs != -1) {
                    String listaVehs = bloqueEsc.substring(abreVehs + 1, cierraVehs);
                    int pv = 0;
                    while (pv < listaVehs.length()) {
                        int av = listaVehs.indexOf('{', pv);
                        if (av == -1) break;
                        int cv = listaVehs.indexOf('}', av);
                        if (cv == -1) break;
                        String bv = listaVehs.substring(av, cv + 1);
                        pv = cv + 1;
                        try {
                            byte motor   = (byte) extraerEntero(bv, "motor");
                            byte llantas = (byte) extraerEntero(bv, "llantas");
                            byte peso    = (byte) extraerEntero(bv, "peso");
                            Vehiculo v = new Vehiculo(motor, llantas, peso);
                            vehiculosEsc.add(v);
                            vehs.add(v);
                            mVeh.addElement("Auto#" + (++idVehGlobal) + " [" + nomEsc + "] (M:" + motor + " L:" + llantas + " P:" + peso + ")");
                        } catch (Exception ignored) {}
                    }
                }
            }

            // ── Extraer pilotos de esta escudería ──
            List<Driver> pilotosEsc = new ArrayList<>();
            int inicioPils = bloqueEsc.indexOf("\"pilotos\"");
            if (inicioPils != -1) {
                int abrePils = bloqueEsc.indexOf('[', inicioPils);
                int cierraPils = bloqueEsc.indexOf(']', abrePils);
                if (abrePils != -1 && cierraPils != -1) {
                    String listaPils = bloqueEsc.substring(abrePils + 1, cierraPils);
                    int pp = 0;
                    while (pp < listaPils.length()) {
                        int ap = listaPils.indexOf('{', pp);
                        if (ap == -1) break;
                        int cp = listaPils.indexOf('}', ap);
                        if (cp == -1) break;
                        String bp = listaPils.substring(ap, cp + 1);
                        pp = cp + 1;
                        try {
                            String nomP = extraerCadena(bp, "nombre");
                            String nac  = extraerCadena(bp, "nacionalidad");
                            // habilidad_base ya se guarda como factor (0.0-1.0), lo convertimos a escala 0-10
                            float  hab  = extraerFloat(bp, "habilidad_base") * 10.0f;
                            Vehiculo vAsignado = vehiculosEsc.isEmpty()
                                    ? new Vehiculo((byte)5,(byte)5,(byte)5)
                                    : vehiculosEsc.get(pilotosEsc.size() % vehiculosEsc.size());
                            Driver d = new Driver(nomP, nac, hab, vAsignado);
                            pilotosEsc.add(d);
                            pils.add(d);
                            mPil.addElement(nomP + " [" + nac + "]");
                        } catch (Exception ignored) {}
                    }
                }
            }

            // ── Construir la escudería ──
            Vehiculo vDefault = new Vehiculo((byte)5,(byte)5,(byte)5);
            Vehiculo v1 = vehiculosEsc.size() >= 1 ? vehiculosEsc.get(0) : vDefault;
            Vehiculo v2 = vehiculosEsc.size() >= 2 ? vehiculosEsc.get(1) : vDefault;
            List<Driver> pBase = pilotosEsc.size() >= 2 ? pilotosEsc
                    : new ArrayList<>(Arrays.asList(
                    new Driver("Piloto A", "UNK", 5.0f, vDefault),
                    new Driver("Piloto B", "UNK", 5.0f, vDefault)));
            Escuderia esc = new Escuderia(nomEsc, v1, v2, pBase);
            escs.add(esc);
            String etiqueta = nomEsc + " (" + pBase.size() + " pilotos)" + (pilotosEsc.size() < 2 ? " ⚠" : "");
            mEsc.addElement(etiqueta);
        }
    }

    // ── Helpers de parseo ────────────────────────────────────────────────────

    /** Extrae el valor de una clave de cadena: "clave": "valor" */
    private static String extraerCadena(String bloque, String clave) {
        int idx = bloque.indexOf("\"" + clave + "\":");
        if (idx == -1) return "";
        int ini = bloque.indexOf('"', idx + clave.length() + 3) + 1;
        int fin = bloque.indexOf('"', ini);
        return bloque.substring(ini, fin);
    }

    /** Extrae el valor de una clave numérica entera: "clave": 42 */
    private static int extraerEntero(String bloque, String clave) {
        int idx = bloque.indexOf("\"" + clave + "\":");
        if (idx == -1) throw new IllegalArgumentException("Clave no encontrada: " + clave);
        int ini = idx + clave.length() + 3;
        while (ini < bloque.length() && !Character.isDigit(bloque.charAt(ini)) && bloque.charAt(ini) != '-') ini++;
        int fin = ini;
        while (fin < bloque.length() && (Character.isDigit(bloque.charAt(fin)) || bloque.charAt(fin) == '-')) fin++;
        return Integer.parseInt(bloque.substring(ini, fin).trim());
    }

    /** Extrae el valor de una clave numérica float: "clave": 3.14 */
    private static float extraerFloat(String bloque, String clave) {
        int idx = bloque.indexOf("\"" + clave + "\":");
        if (idx == -1) throw new IllegalArgumentException("Clave no encontrada: " + clave);
        int ini = idx + clave.length() + 3;
        while (ini < bloque.length() && !Character.isDigit(bloque.charAt(ini)) && bloque.charAt(ini) != '-') ini++;
        int fin = ini;
        while (fin < bloque.length() && (Character.isDigit(bloque.charAt(fin)) || bloque.charAt(fin) == '.' || bloque.charAt(fin) == '-')) fin++;
        return Float.parseFloat(bloque.substring(ini, fin).trim());
    }

    /**
     * Encuentra el índice del '}' de cierre balanceado del objeto que abre en {@code desde}.
     * Maneja objetos anidados y cadenas con llaves dentro correctamente.
     */
    private static int encontrarCierreObjeto(String texto, int desde) {
        int profundidad = 0;
        boolean enCadena = false;
        for (int i = desde; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == '"' && (i == 0 || texto.charAt(i - 1) != '\\')) enCadena = !enCadena;
            if (enCadena) continue;
            if (c == '{') profundidad++;
            else if (c == '}') {
                profundidad--;
                if (profundidad == 0) return i;
            }
        }
        return -1;
    }

    private static void escribirTextoEnDisco(String archivo, String texto) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) { pw.print(texto); }
        catch (IOException e) { e.printStackTrace(); }
    }
}