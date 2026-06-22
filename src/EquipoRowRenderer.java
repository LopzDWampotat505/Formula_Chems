import java.awt.Component;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class EquipoRowRenderer extends DefaultTableCellRenderer {
    private List<Driver> pilotos;
    private Temporada temporada;

    public EquipoRowRenderer(List<Driver> pilotos, Temporada temporada) {
        this.pilotos = pilotos;
        this.temporada = temporada;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (row < pilotos.size()) {
            c.setBackground(ColoresEscuderia.getFondoFila(pilotos.get(row), temporada));
        } else {
            c.setBackground(table.getBackground());
        }
        return c;
    }
}