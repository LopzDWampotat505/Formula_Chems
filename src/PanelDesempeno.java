import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;


/*
 * Gráfica de barras horizontales dibujada a mano con Graphics2D — sin
 * dependencias externas (sin JFreeChart). Muestra el desempeño de la
 * temporada (puntos) por piloto o por escudería, según se le indique
 * mediante setDatos().
 */
public class PanelDesempeno extends JPanel {


    private String[] etiquetas = new String[0];
    private int[]    valores   = new int[0];
    private Color[]  colores   = new Color[0];
    private String   titulo    = "Desempeño de la temporada";


    private static final int MARGEN_IZQ   = 160; // espacio para el nombre
    private static final int MARGEN_DER   = 60;  // espacio para el valor numérico
    private static final int MARGEN_SUP   = 36;
    private static final int ALTO_BARRA   = 26;
    private static final int ESPACIO_FILA = 14;


    public PanelDesempeno() {
        setBackground(Color.WHITE);
    } // fin del constructor PanelDesempeno


    // Actualiza los datos a graficar y repinta el panel
    public void setDatos(String titulo, String[] etiquetas, int[] valores, Color[] colores) {
        this.titulo    = titulo;
        this.etiquetas = etiquetas;
        this.valores   = valores;
        this.colores   = colores;
        revalidate();
        repaint();
    } // fin del metodo setDatos


    @Override
    public Dimension getPreferredSize() {
        int alto = MARGEN_SUP + etiquetas.length * (ALTO_BARRA + ESPACIO_FILA) + 20;
        return new Dimension(560, Math.max(alto, 200));
    } // fin del metodo getPreferredSize


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        int ancho = getWidth();


        // --- Título ---
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2.drawString(titulo, 10, 20);


        if (etiquetas.length == 0) {
            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g2.drawString("Aún no hay datos para graficar. Simula al menos una carrera.", 10, 50);
            return;
        }


        int maxValor = 1;
        for (int v : valores) maxValor = Math.max(maxValor, v);


        int anchoDisponible = ancho - MARGEN_IZQ - MARGEN_DER;
        if (anchoDisponible < 50) anchoDisponible = 50;


        Font fuenteEtiqueta = new Font("SansSerif", Font.PLAIN, 12);
        Font fuenteValor    = new Font("Monospaced", Font.BOLD, 12);
        FontMetrics fmEtiqueta = g2.getFontMetrics(fuenteEtiqueta);


        int y = MARGEN_SUP;
        for (int i = 0; i < etiquetas.length; i++) {
            int valor = valores[i];
            int anchoBarra = (int) ((valor / (double) maxValor) * anchoDisponible);
            Color color = (colores != null && i < colores.length && colores[i] != null)
                    ? colores[i] : new Color(120, 120, 120);


            // --- Etiqueta (nombre) ---
            g2.setColor(Color.DARK_GRAY);
            g2.setFont(fuenteEtiqueta);
            String etiqueta = etiquetas[i];
            // Recorta si no cabe en el margen izquierdo
            while (fmEtiqueta.stringWidth(etiqueta) > MARGEN_IZQ - 14 && etiqueta.length() > 3) {
                etiqueta = etiqueta.substring(0, etiqueta.length() - 2);
            }
            int yTextoBase = y + ALTO_BARRA / 2 + fmEtiqueta.getAscent() / 2 - 2;
            g2.drawString(etiqueta, MARGEN_IZQ - fmEtiqueta.stringWidth(etiqueta) - 10, yTextoBase);


            // --- Riel de fondo ---
            g2.setColor(new Color(235, 235, 235));
            g2.fillRoundRect(MARGEN_IZQ, y, anchoDisponible, ALTO_BARRA, 6, 6);


            // --- Barra ---
            g2.setColor(color);
            g2.fillRoundRect(MARGEN_IZQ, y, Math.max(anchoBarra, 4), ALTO_BARRA, 6, 6);


            // --- Valor numérico ---
            g2.setColor(Color.BLACK);
            g2.setFont(fuenteValor);
            g2.drawString(String.valueOf(valor), MARGEN_IZQ + anchoDisponible + 10, yTextoBase);


            y += ALTO_BARRA + ESPACIO_FILA;
        }
    } // fin del metodo paintComponent
} // fin de la clase PanelDesempeno
