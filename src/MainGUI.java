import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainGUI extends JFrame {

    private SimuladorController controlador;
    private GeneradorReportes reporte;

    // --- Elementos de la interfaz ---
    private JLabel lblTitulo;
    private JButton btnQ1, btnQ2, btnQ3, btnGP;
    private JButton btnClasif, btnResGP, btnCampeonato, btnDesempeno;
    private JButton btnResimular, btnAvanzar, btnFastForward;
    private JButton btnGuardarJSON, btnLeerJSON;

    // --- Variables de estado de la carrera actual ---
    private boolean q1Hecha = false;
    private boolean q2Hecha = false;
    private boolean q3Hecha = false;
    private boolean gpHecho = false;

    private final Color F1_RED = new Color(255, 30, 0);
    private final Color F1_BLACK = new Color(21, 21, 30);

    // Busca el método constructor MainGUI y actualiza los paneles correspondientes:
    public MainGUI(SimuladorController controlador) {
        this.controlador = controlador;
        this.reporte = new GeneradorReportes();

        setTitle("Simulador Fórmula 1 - " + controlador.getTemporada().getAño());
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- ENCABEZADO ---
        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        lblTitulo = new JLabel();
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        panelHeader.add(lblTitulo);
        add(panelHeader, BorderLayout.NORTH);

        // --- CONTENEDOR CENTRAL ---
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        // 1. Panel Sesiones
        JPanel panelSesiones = new JPanel(new GridLayout(1, 4, 10, 10));
        panelSesiones.setBorder(BorderFactory.createTitledBorder("Sesiones del fin de semana"));
        btnQ1 = new JButton("Simular Q1");
        btnQ2 = new JButton("Simular Q2");
        btnQ3 = new JButton("Simular Q3");
        btnGP = new JButton("Simular GP");
        panelSesiones.add(btnQ1); panelSesiones.add(btnQ2);
        panelSesiones.add(btnQ3); panelSesiones.add(btnGP);

        // 2. Panel Tablas
        JPanel panelTablas = new JPanel(new GridLayout(1, 4, 10, 10));
        panelTablas.setBorder(BorderFactory.createTitledBorder("Consultar tablas"));
        btnClasif = new JButton("Ver Clasificación");
        btnResGP = new JButton("Ver Resultados GP");
        btnCampeonato = new JButton("Ver Campeonato");
        btnDesempeno = new JButton("Ver Desempeño");
        panelTablas.add(btnClasif); panelTablas.add(btnResGP);
        panelTablas.add(btnCampeonato); panelTablas.add(btnDesempeno);

        // 3. Panel Control (Cambiado a 4 columnas para albergar el botón de reinicio)
        JPanel panelControl = new JPanel(new GridLayout(1, 4, 10, 10));
        panelControl.setBorder(BorderFactory.createTitledBorder("Control de temporada"));
        btnResimular = new JButton("Resimular Solo GP");
        btnAvanzar = new JButton("Avanzar a Siguiente Ca...");
        btnFastForward = new JButton("Fast Forward (Resto del...");

        JButton btnReiniciar = new JButton("🔄 Otra Temporada");
        btnReiniciar.setBackground(F1_BLACK);
        btnReiniciar.setForeground(Color.WHITE);
        btnReiniciar.setOpaque(true);
        btnReiniciar.setBorderPainted(false);

        panelControl.add(btnResimular); panelControl.add(btnAvanzar);
        panelControl.add(btnFastForward); panelControl.add(btnReiniciar);

        // 4. Panel JSON
        JPanel panelJSON = new JPanel(new GridLayout(1, 2, 10, 10));
        panelJSON.setBorder(BorderFactory.createTitledBorder("Archivos (JSON)"));
        btnGuardarJSON = new JButton("Guardar JSON");
        btnLeerJSON = new JButton("Leer JSON Guardado");
        panelJSON.add(btnGuardarJSON); panelJSON.add(btnLeerJSON);

        panelCentral.add(panelSesiones);
        panelCentral.add(Box.createVerticalStrut(10));
        panelCentral.add(panelTablas);
        panelCentral.add(Box.createVerticalStrut(10));
        panelCentral.add(panelControl);
        panelCentral.add(Box.createVerticalStrut(10));
        panelCentral.add(panelJSON);

        add(panelCentral, BorderLayout.CENTER);

        actualizarInterfaz();

        // ══════════════════════════════════════════════════════════════
        //  COMPORTAMIENTO NUEVO DE LOS BOTONES MODIFICADOS
        // ══════════════════════════════════════════════════════════════

        // El botón leer JSON ya no imprime en consola, abre los visores flotantes directamente
        btnLeerJSON.addActionListener(e -> {
            int año = controlador.getTemporada().getAño();
            ManejadorArchivos.leerArchivoJson(año, "equipos", this);
            ManejadorArchivos.leerArchivoJson(año, "progreso", this);
        });

        // El botón de regresar cierra la ventana actual y despierta un ciclo de Formula1 nuevo
        btnReiniciar.addActionListener(e -> {
            int conf = JOptionPane.showConfirmDialog(this, "¿Cerrar esta simulación y configurar otra temporada desde el inicio?", "Simular otra temporada", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {

                // 1. CERRAR TODAS LAS VENTANAS SECUNDARIAS ABIERTAS
                // Esto busca todas las ventanas de tipo JDialog (VentanaTabla, VentanaDesempeno, Visores JSON)
                for (Window w : Window.getWindows()) {
                    if (w instanceof JDialog) {
                        w.dispose();
                    }
                }

                // 2. Cerrar la ventana principal actual
                this.dispose();

                // 3. Reiniciar el ciclo de Formula1
                // Usamos un hilo nuevo para asegurar que el registro de datos no tenga basura del anterior
                new Thread(() -> Formula1.iniciar()).start();
            }
        });

        // (Conserva todos los demás listeners idénticos de btnQ1, btnQ2, btnQ3, btnGP, etc.)
        btnQ1.addActionListener(e -> {
            Carrera c = controlador.getCarreraActual();
            Q1 q1 = new Q1(c.getNombre(), c.getNivelDificultad(), c.getMinTiempo(), c.getMaxTiempo(), c.getRoster());
            c.getClasificacion().recibirTiemposQ1(q1.simular(c.getRoster()));
            q1Hecha = true;
            actualizarInterfaz();
        });

        btnQ2.addActionListener(e -> {
            Carrera c = controlador.getCarreraActual();
            Q2 q2 = new Q2(c.getNombre(), c.getNivelDificultad(), c.getMinTiempo(), c.getMaxTiempo(), c.getRoster());
            c.getClasificacion().recibirTiemposQ2(q2.simular(c.getClasificacion().obtenerSobrevivientesQ1()));
            q2Hecha = true;
            actualizarInterfaz();
        });

        btnQ3.addActionListener(e -> {
            Carrera c = controlador.getCarreraActual();
            Q3 q3 = new Q3(c.getNombre(), c.getNivelDificultad(), c.getMinTiempo(), c.getMaxTiempo(), c.getRoster());
            // AQUÍ ESTABA EL ERROR DE ESCRITURA:
            c.getClasificacion().recibirTiemposQ3(q3.simular(c.getClasificacion().obtenerSobrevivientesQ2()));
            c.getClasificacion().generarParrillaOficial();
            q3Hecha = true;
            actualizarInterfaz();
        });

        btnGP.addActionListener(e -> {
            Carrera c = controlador.getCarreraActual();
            GranPremio gp = new GranPremio(c.getNombre(), c.getNivelDificultad(), c.getMinTiempo(), c.getMaxTiempo(), c.getRoster());
            c.setGranPremio(gp);
            gp.simularCarreraFinal(c.getClasificacion().getParrillaFinal());
            controlador.getTemporada().actualizarCampeonato();
            gpHecho = true;
            actualizarInterfaz();
        });

        btnClasif.addActionListener(e -> {
            Carrera c = controlador.getCarreraActual();
            if(!q1Hecha) { mostrarError("Aún no hay tiempos de Q1."); return; }
            Object[][] dClasi = reporte.generarDatosClasificacion(c);
            VentanaTabla vC = new VentanaTabla(this, "Clasificación - " + c.getNombre(),
                    new String[]{"POS", "PILOTO", "NAC", "TIEMPO Q1", "TIEMPO Q2", "TIEMPO Q3"});
            vC.actualizarDatos(dClasi, c.getClasificacion().getParrillaFinal(), controlador.getTemporada());
            vC.setVisible(true);
        });

        btnResGP.addActionListener(e -> {
            Carrera c = controlador.getCarreraActual();
            if(!gpHecho) { mostrarError("Aún no se ha corrido el Gran Premio."); return; }
            Object[][] dGP = reporte.generarDatosCarrera(c, controlador.getTemporada());
            VentanaTabla vGP = new VentanaTabla(this, "Resultados GP - " + c.getNombre(),
                    new String[]{"POS", "PILOTO", "NAC", "RESULTADO", "PTS TOTAL"});
            vGP.actualizarDatos(dGP, c.getGranPremio().getResultadosFinales(), controlador.getTemporada());
            vGP.setVisible(true);
        });

        btnCampeonato.addActionListener(e -> {
            Temporada t = controlador.getTemporada();
            List<Driver> orden = reporte.ordenCampeonato(t);
            Object[][] dCamp = new Object[orden.size()][4];
            for (int i = 0; i < orden.size(); i++) {
                Driver d = orden.get(i);
                dCamp[i][0] = i + 1;
                dCamp[i][1] = d.getNombre();
                dCamp[i][2] = d.getNacionalidad();
                dCamp[i][3] = t.getCampeonato().get(d);
            }
            VentanaTabla vC = new VentanaTabla(this, "Campeonato Mundial - " + t.getAño(),
                    new String[]{"POS", "PILOTO", "NAC", "PUNTOS"});
            vC.actualizarDatos(dCamp, orden, t);
            vC.setVisible(true);
        });

        btnDesempeno.addActionListener(e -> {
            VentanaDesempeno vD = new VentanaDesempeno(this, reporte);
            vD.refrescar(controlador.getTemporada());
            vD.setVisible(true);
        });

        btnResimular.addActionListener(e -> {
            controlador.resimularGranPremio(controlador.getCarreraActual());
            JOptionPane.showMessageDialog(this, "Gran Premio resimulado con la misma parrilla de salida.");
        });

        btnAvanzar.addActionListener(e -> {
            controlador.avanzarRonda();
            q1Hecha = false; q2Hecha = false; q3Hecha = false; gpHecho = false;
            actualizarInterfaz();
        });

        btnFastForward.addActionListener(e -> {
            controlador.fastForward();
            q1Hecha = true; q2Hecha = true; q3Hecha = true; gpHecho = true;
            actualizarInterfaz();
            JOptionPane.showMessageDialog(this, "¡Temporada finalizada! Revisa el campeonato y desempeño.");
        });

        btnGuardarJSON.addActionListener(e -> {
            ManejadorArchivos.guardarEquiposJson(controlador.getTemporada());
            ManejadorArchivos.guardarProgresoCarrerasJson(controlador.getTemporada());
            JOptionPane.showMessageDialog(this, "Archivos JSON guardados en la carpeta del proyecto.");
        });
    }

    // --- MÉTODOS DE APOYO ---

    private void actualizarInterfaz() {
        if (!controlador.hayMasCarreras()) {
            lblTitulo.setText("🏁 Temporada " + controlador.getTemporada().getAño() + " Finalizada 🏁");
            btnQ1.setEnabled(false); btnQ2.setEnabled(false); btnQ3.setEnabled(false); btnGP.setEnabled(false);
            btnAvanzar.setEnabled(false); btnFastForward.setEnabled(false); btnResimular.setEnabled(false);
            return;
        }

        Carrera c = controlador.getCarreraActual();
        String estado = "Listo para Q1";
        if (q1Hecha) estado = "Q1 completada (15 pasan a Q2)";
        if (q2Hecha) estado = "Q2 completada (10 pasan a Q3)";
        if (q3Hecha) estado = "Q3 completada. Parrilla de salida lista.";
        if (gpHecho) estado = "¡Gran Premio completado! Puntos actualizados.";

        lblTitulo.setText("🟢 GP DE " + c.getNombre().toUpperCase() + " — Ronda " +
                controlador.getPistaProgreso() + "/" + controlador.getTotalPistas() + " — " + estado);

        // Lógica de habilitar/deshabilitar botones paso a paso
        btnQ1.setEnabled(!q1Hecha);
        btnQ2.setEnabled(q1Hecha && !q2Hecha);
        btnQ3.setEnabled(q2Hecha && !q3Hecha);
        btnGP.setEnabled(q3Hecha && !gpHecho);

        btnAvanzar.setEnabled(gpHecho);
        btnResimular.setEnabled(gpHecho);
        btnFastForward.setEnabled(!gpHecho); // Si ya lo corriste, usas avanzar normal
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Sin datos", JOptionPane.WARNING_MESSAGE);
    }
}