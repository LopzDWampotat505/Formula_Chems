import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Ventana secundaria que muestra la gráfica de barras de desempeño
 * de la temporada (PanelDesempeno, de tu compañero), con un par de
 * JRadioButton para alternar entre ver los puntos por piloto o por
 * escudería. Sigue el mismo patrón no-modal que VentanaTabla: se crea
 * una sola vez y se reutiliza/refresca cada vez que se abre.
 */
public class VentanaDesempeno extends JDialog {

    private final PanelDesempeno panelDesempeno;
    private final JRadioButton radioPorPiloto;
    private final JRadioButton radioPorEquipo;
    private final GeneradorReportes periodista;

    public VentanaDesempeno(Frame propietario, GeneradorReportes periodista) {
        super(propietario, "Desempeño de la Temporada", false); // false = no-modal
        this.periodista = periodista;

        setSize(640, 480);
        setLocationRelativeTo(propietario);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Controles arriba: piloto vs. escudería ---
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPorPiloto = new JRadioButton("Por piloto", true);
        radioPorEquipo = new JRadioButton("Por escudería");
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(radioPorPiloto);
        grupo.add(radioPorEquipo);

        controles.add(radioPorPiloto);
        controles.add(radioPorEquipo);
        add(controles, BorderLayout.NORTH);

        // --- La gráfica, dentro de un scroll por si hay muchas filas ---
        panelDesempeno = new PanelDesempeno();
        JScrollPane scroll = new JScrollPane(panelDesempeno);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Cuando se cambia de radio, se vuelve a calcular la gráfica con la
        // temporada más reciente (se la pasamos en cada refresco, ver abajo)
        radioPorPiloto.addActionListener(e -> refrescar(temporadaActual));
        radioPorEquipo.addActionListener(e -> refrescar(temporadaActual));
    }

    // Guardamos la última temporada usada para poder refrescar al cambiar el radio
    private Temporada temporadaActual;

    /**
     * Recalcula y repinta la gráfica con los datos más recientes de la
     * temporada, respetando el modo seleccionado (piloto o escudería).
     */
    public void refrescar(Temporada temporada) {
        this.temporadaActual = temporada;

        if (radioPorEquipo.isSelected()) {
            Map<Escuderia, Integer> puntos = temporada.getPuntosPorEscuderia();
            List<Map.Entry<Escuderia, Integer>> ordenado = new ArrayList<>(puntos.entrySet());
            ordenado.sort((a, b) -> b.getValue() - a.getValue());

            String[] etiquetas = new String[ordenado.size()];
            int[] valores = new int[ordenado.size()];
            Color[] colores = new Color[ordenado.size()];
            for (int i = 0; i < ordenado.size(); i++) {
                etiquetas[i] = ordenado.get(i).getKey().getNombre();
                valores[i] = ordenado.get(i).getValue();
                colores[i] = ColoresEscuderia.get(etiquetas[i]);
            }
            panelDesempeno.setDatos("Puntos por escudería", etiquetas, valores, colores);
        } else {
            List<Driver> orden = periodista.ordenCampeonato(temporada);
            String[] etiquetas = new String[orden.size()];
            int[] valores = new int[orden.size()];
            Color[] colores = new Color[orden.size()];
            for (int i = 0; i < orden.size(); i++) {
                Driver d = orden.get(i);
                etiquetas[i] = d.getNombre();
                valores[i] = temporada.getCampeonato().getOrDefault(d, 0);
                colores[i] = ColoresEscuderia.get(d, temporada);
            }
            panelDesempeno.setDatos("Puntos por piloto", etiquetas, valores, colores);
        }
    }
}
