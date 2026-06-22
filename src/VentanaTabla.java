import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Ventana secundaria reutilizable para mostrar cualquier tabla de
 * resultados (clasificación, resultados de GP, campeonato) usando
 * un JDialog no-modal: se puede dejar abierta mientras se sigue
 * interactuando con la ventana principal.
 *
 * Usa EquipoRowRenderer (de tu compañero) para colorear cada fila
 * con el color oficial de la escudería del piloto en esa fila.
 */
public class VentanaTabla extends JDialog {

    private final DefaultTableModel modelo;
    private final JTable tabla;

    public VentanaTabla(Frame propietario, String tituloInicial, String[] encabezados) {
        super(propietario, tituloInicial, false); // false = no-modal
        setSize(640, 420);
        setLocationRelativeTo(propietario);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE); // se oculta, no se destruye (se reutiliza)

        // El modelo empieza vacío; se llena con actualizarDatos()
        modelo = new DefaultTableModel(encabezados, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabla de solo lectura
            }
        };
        tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tabla.setFillsViewportHeight(true);

        JScrollPane scroll = new JScrollPane(tabla);
        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Reemplaza los datos de la tabla y, si se provee el orden de pilotos
     * y la temporada, instala un EquipoRowRenderer para colorear las filas
     * por escudería.
     *
     * @param filas      filas en formato Object[][] (ej. desde GeneradorReportes)
     * @param ordenFilas lista de pilotos en el MISMO orden que las filas, usada
     *                   por el renderer para saber a qué equipo pintar cada fila
     * @param temporada  temporada actual, para que el renderer pueda resolver
     *                   a qué escudería pertenece cada piloto
     */
    public void actualizarDatos(Object[][] filas, List<Driver> ordenFilas, Temporada temporada) {
        modelo.setRowCount(0); // limpia filas previas sin tocar las columnas
        for (Object[] fila : filas) {
            modelo.addRow(fila);
        }

        if (ordenFilas != null && temporada != null) {
            EquipoRowRenderer renderer = new EquipoRowRenderer(ordenFilas, temporada);
            for (int col = 0; col < tabla.getColumnCount(); col++) {
                tabla.getColumnModel().getColumn(col).setCellRenderer(renderer);
            }
        }

        if (filas.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Todavía no hay datos para mostrar en esta tabla.\n" +
                    "Simula la sesión correspondiente primero.",
                    "Sin datos", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
