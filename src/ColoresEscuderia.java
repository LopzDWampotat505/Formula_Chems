import java.awt.Color;
import java.util.HashMap;
import java.util.Map;


/*
 * Mapa estático de colores oficiales por escudería, usado por los
 * TableCellRenderer de la GUI para pintar cada fila con el color de
 * su equipo. Se indexa por el nombre de la escudería tal como se
 * registra en CargadorDatos.
 */
public class ColoresEscuderia {


    private static final Map<String, Color> COLORES = new HashMap<>();
    private static final Color COLOR_DEFAULT = new Color(200, 200, 200);


    static {
        COLORES.put("Oracle Red Bull Racing",  new Color(24, 75, 219));   // Azul eléctrico profundo
        COLORES.put("Scuderia Ferrari",        new Color(220, 20, 60));   // Rojo carmesí intenso
        COLORES.put("McLaren F1 Team",         new Color(255, 145, 0));   // Naranja papaya cálido
        COLORES.put("Mercedes-AMG Petronas",   new Color(0, 229, 210));   // Turquesa "Petronas" brillante
        COLORES.put("Aston Martin Aramco",     new Color(0, 95, 85));     // British Racing Green profundo
        COLORES.put("Williams Racing",         new Color(0, 110, 255));   // Azul Williams vibrante
        COLORES.put("BWT Alpine F1 Team",      new Color(255, 155, 198)); // Rosa pastel suave y moderno
        COLORES.put("Stake F1 / Audi",         new Color(70, 230, 70));   // Verde neón equilibrado
        COLORES.put("Visa Cash App RB F1",     new Color(85, 160, 255));  // Azul cielo suave
        COLORES.put("MoneyGram Haas F1",       new Color(150, 155, 160)); // Gris grafito elegante
        COLORES.put("Cadillac F1 Team",        new Color(0, 65, 50));
    } // fin del bloque estático


    // Retorna el color oficial de la escudería, o un gris neutro si no se encuentra
    public static Color get(String nombreEscuderia) {
        return COLORES.getOrDefault(nombreEscuderia, COLOR_DEFAULT);
    } // fin del metodo get


    // Sobrecarga de conveniencia: obtiene el color a partir del piloto
    public static Color get(Driver d, Temporada temporada) {
        for (Escuderia e : temporada.getEscuderia()) {
            if (e.getDrivers().contains(d)) {
                return get(e.getNombre());
            }
        }
        return COLOR_DEFAULT;
    } // fin del metodo get (sobrecarga)


    /*
     * Variante translúcida del color de equipo, pensada para pintar el
     * fondo de una fila de JTable sin saturar el texto encima.
     */
    public static Color getFondoFila(String nombreEscuderia) {
        Color base = get(nombreEscuderia);
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), 55);
    } // fin del metodo getFondoFila


    // Sobrecarga de conveniencia: color de fondo translúcido a partir del piloto
    public static Color getFondoFila(Driver d, Temporada temporada) {
        Color base = get(d, temporada);
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), 55);
    } // fin del metodo getFondoFila (sobrecarga)
} // fin de la clase ColoresEscuderia
