import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegistroF1GUI extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private Temporada temporada;
    private List<Vehiculo> vehiculosRegistrados = new ArrayList<>();
    private List<Driver> pilotosRegistrados = new ArrayList<>();
    private List<Escuderia> escuderiasRegistradas = new ArrayList<>();
    private List<GranPremio> gpPendientes = new ArrayList<>();

    private DefaultListModel<String> modeloGPs = new DefaultListModel<>();
    private DefaultListModel<String> modeloVehiculos = new DefaultListModel<>();
    private DefaultListModel<String> modeloPilotos = new DefaultListModel<>();
    // Línea aproximada 19 en RegistroF1GUI.java
    private DefaultListModel<String> modeloEscuderias = new DefaultListModel<>();

    // --- Colores Oficiales F1 ---
    private final Color F1_RED = new Color(255, 30, 0);
    private final Color F1_BLACK = new Color(21, 21, 30);

    public RegistroF1GUI() {
        setTitle("Registro Temporada F1");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(crearPanelTemporada(), "Temporada");
        add(cardPanel, BorderLayout.CENTER);
    }

    public Temporada getTemporada() { return temporada; }

    // Busca el método crearPanelTemporada() y reemplázalo por este:
    private JPanel crearPanelTemporada() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Configurar Gran Premio"));

        panelFormulario.add(new JLabel("Año de la temporada:"));

        // CORRECCIÓN: Contenedor con botón de carga para pre-cargar datos existentes
        JPanel panelAnoAcciones = new JPanel(new BorderLayout(5, 0));
        JTextField txtAno = new JTextField("2026");
        JButton btnCargarJSON = new JButton("📂 Auto-Cargar JSON");
        panelAnoAcciones.add(txtAno, BorderLayout.CENTER);
        panelAnoAcciones.add(btnCargarJSON, BorderLayout.EAST);
        panelFormulario.add(panelAnoAcciones);

        panelFormulario.add(new JLabel("Nombre del Circuito:"));
        JTextField txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Dificultad (1-10):"));
        JSlider sliderDif = new JSlider(1, 10, 7);
        sliderDif.setMajorTickSpacing(1);
        sliderDif.setPaintTicks(true);
        sliderDif.setPaintLabels(true);
        panelFormulario.add(sliderDif);

        panelFormulario.add(new JLabel("Tiempos (Mín / Máx):"));
        JPanel panelTiempos = new JPanel(new FlowLayout());
        JTextField txtMin = new JTextField("90.0", 5);
        JTextField txtMax = new JTextField("97.0", 5);
        panelTiempos.add(txtMin);
        panelTiempos.add(new JLabel("-"));
        panelTiempos.add(txtMax);
        panelFormulario.add(panelTiempos);

        JButton btnAgregarGP = new JButton("Agregar GP a la lista");
        btnAgregarGP.setBackground(F1_RED);
        btnAgregarGP.setForeground(Color.WHITE);
        btnAgregarGP.setOpaque(true);
        btnAgregarGP.setBorderPainted(false);

        panelFormulario.add(new JLabel(""));
        panelFormulario.add(btnAgregarGP);

        JList<String> listaGPs = new JList<>(modeloGPs);
        JScrollPane scrollGPs = new JScrollPane(listaGPs);
        scrollGPs.setBorder(BorderFactory.createTitledBorder("Calendario de Carreras"));

        JButton btnComenzar = new JButton("Comenzar Registro de Equipos");
        btnComenzar.setBackground(F1_BLACK);
        btnComenzar.setForeground(Color.WHITE);
        btnComenzar.setOpaque(true);
        btnComenzar.setBorderPainted(false);

        // --- EVENTO CARGAR JSON ---
        btnCargarJSON.addActionListener(e -> {
            try {
                int año = Integer.parseInt(txtAno.getText().trim());
                ManejadorArchivos.cargarDatosDesdeJson(
                        año, gpPendientes, vehiculosRegistrados, pilotosRegistrados, escuderiasRegistradas,
                        modeloGPs, modeloVehiculos, modeloPilotos, modeloEscuderias
                );

                // Si la carga trajo carreras y escuderías, inicializamos un objeto temporada base útil
                if (!gpPendientes.isEmpty() && !escuderiasRegistradas.isEmpty()) {
                    temporada = new Temporada((short) año, gpPendientes.get(0), escuderiasRegistradas.get(0));
                    for (int i = 1; i < gpPendientes.size(); i++) temporada.addCarrera(gpPendientes.get(i));
                    for (int i = 1; i < escuderiasRegistradas.size(); i++) temporada.addEscuderia(escuderiasRegistradas.get(i));
                }
                JOptionPane.showMessageDialog(this, "¡JSON leído con éxito! Datos cargados en las listas. Puedes editarlos en las pestañas.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Asegúrate de haber guardado archivos de ese año primero.\nError: " + ex.getMessage(), "Sin datos", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnAgregarGP.addActionListener(e -> {
            try {
                String nom = txtNombre.getText();
                if (nom.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debes ingresar un nombre.");
                    return;
                }
                int dif = sliderDif.getValue();
                float min = Float.parseFloat(txtMin.getText());
                float max = Float.parseFloat(txtMax.getText());

                GranPremio gp = new GranPremio(nom, dif, min, max, new ArrayList<>());
                gpPendientes.add(gp);
                modeloGPs.addElement(nom + " - Dif: " + dif);
                txtNombre.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Revisa que los tiempos sean números válidos.");
            }
        });

        btnComenzar.addActionListener(e -> {
            if (gpPendientes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Agrega al menos una carrera.");
                return;
            }
            try {
                short ano = Short.parseShort(txtAno.getText());

                // Si cargamos de JSON, respetamos la primera escudería real, de lo contrario creamos la dummy
                Escuderia escInicial = escuderiasRegistradas.isEmpty() ?
                        new Escuderia("Dummy", new Vehiculo((byte)5,(byte)5,(byte)5), new Vehiculo((byte)5,(byte)5,(byte)5), Arrays.asList(new Driver("Dummy", "DUM", 5.0f, new Vehiculo((byte)5,(byte)5,(byte)5)), new Driver("Dummy", "DUM", 5.0f, new Vehiculo((byte)5,(byte)5,(byte)5))))
                        : escuderiasRegistradas.get(0);

                temporada = new Temporada(ano, gpPendientes.get(0), escInicial);

                // Evitamos duplicar el índice 0 que ya pusimos en el constructor
                for (int i = 1; i < gpPendientes.size(); i++) {
                    temporada.addCarrera(gpPendientes.get(i));
                }
                for (int i = 1; i < escuderiasRegistradas.size(); i++) {
                    temporada.addEscuderia(escuderiasRegistradas.get(i));
                }

                cardPanel.add(crearPanelEquipos(), "Equipos");
                cardLayout.show(cardPanel, "Equipos");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al procesar el año: " + ex.getMessage());
            }
        });

        panel.add(panelFormulario, BorderLayout.NORTH);
        panel.add(scrollGPs, BorderLayout.CENTER);
        panel.add(btnComenzar, BorderLayout.SOUTH);

        return panel;
    }


    private JPanel crearPanelEquipos() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Vehículos", crearPanelVehiculos());
        tabs.addTab("Pilotos", crearPanelPilotos());
        tabs.addTab("Escuderías", crearPanelEscuderias());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelVehiculos() {
        JPanel panel = new JPanel(new GridLayout(1, 2));

        JPanel form = new JPanel(new GridLayout(6, 1));
        form.setBorder(BorderFactory.createTitledBorder("Nuevo Vehículo"));

        JTextField txtNombre = new JTextField();
        JSlider sMotor = new JSlider(0, 10, 8);
        JSlider sLlantas = new JSlider(0, 10, 8);
        JSlider sPeso = new JSlider(0, 10, 8);

        JButton btnAdd = new JButton("Registrar Vehículo");
        btnAdd.setBackground(F1_RED);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setOpaque(true);
        btnAdd.setBorderPainted(false);

        form.add(new JLabel("Identificador:"));
        form.add(txtNombre);
        form.add(new JLabel("Motor (0-10):"));
        form.add(sMotor);
        form.add(new JLabel("Llantas (0-10):"));
        form.add(sLlantas);
        form.add(new JLabel("Peso (0-10):"));
        form.add(sPeso);
        form.add(btnAdd);

        JList<String> lista = new JList<>(modeloVehiculos);
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(BorderFactory.createTitledBorder("Lista de Vehículos"));

        btnAdd.addActionListener(e -> {
            String nom = txtNombre.getText();
            if (nom.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ponle nombre al vehículo.");
                return;
            }
            byte m = (byte) sMotor.getValue();
            byte l = (byte) sLlantas.getValue();
            byte p = (byte) sPeso.getValue();

            Vehiculo v = new Vehiculo(m, l, p);
            vehiculosRegistrados.add(v);
            modeloVehiculos.addElement(nom + " (M:" + m + " L:" + l + " P:" + p + ")");
            txtNombre.setText("");
        });

        panel.add(form);
        panel.add(scroll);
        return panel;
    }

    private JPanel crearPanelPilotos() {
        JPanel panel = new JPanel(new GridLayout(1, 2));

        JPanel form = new JPanel(new GridLayout(5, 2));
        form.setBorder(BorderFactory.createTitledBorder("Nuevo Piloto"));

        JTextField txtNombre = new JTextField();
        JTextField txtNac = new JTextField();
        JComboBox<String> cbVehiculo = new JComboBox<>();
        JSlider sHab = new JSlider(0, 100, 75);

        JButton btnAdd = new JButton("Registrar Piloto");
        btnAdd.setBackground(F1_RED);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setOpaque(true);
        btnAdd.setBorderPainted(false);

        JButton btnRefrescarV = new JButton("↻ Cargar Autos");

        form.add(new JLabel("Nombre:"));
        form.add(txtNombre);
        form.add(new JLabel("Nacionalidad (3 letras):"));
        form.add(txtNac);
        form.add(new JLabel("Vehículo:"));
        JPanel pnlAuto = new JPanel(new BorderLayout());
        pnlAuto.add(cbVehiculo, BorderLayout.CENTER);
        pnlAuto.add(btnRefrescarV, BorderLayout.EAST);
        form.add(pnlAuto);
        form.add(new JLabel("Habilidad (0-100):"));
        form.add(sHab);
        form.add(new JLabel(""));
        form.add(btnAdd);

        JList<String> lista = new JList<>(modeloPilotos);
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(BorderFactory.createTitledBorder("Lista de Pilotos"));

        btnRefrescarV.addActionListener(e -> {
            cbVehiculo.removeAllItems();
            for (int i = 0; i < modeloVehiculos.size(); i++) {
                cbVehiculo.addItem(modeloVehiculos.get(i));
            }
        });

        btnAdd.addActionListener(e -> {
            String nom = txtNombre.getText();
            String nac = txtNac.getText().toUpperCase();
            if (nom.isEmpty() || nac.length() > 3 || nac.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Datos inválidos o nacionalidad muy larga.");
                return;
            }
            int vIdx = cbVehiculo.getSelectedIndex();
            if (vIdx < 0) {
                JOptionPane.showMessageDialog(this, "Selecciona un vehículo.");
                return;
            }
            try {
                float hab = sHab.getValue() / 10.0f;
                Vehiculo v = vehiculosRegistrados.get(vIdx);
                Driver d = new Driver(nom, nac, hab, v);
                pilotosRegistrados.add(d);
                modeloPilotos.addElement(nom + " [" + nac + "]");
                txtNombre.setText("");
                txtNac.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        panel.add(form);
        panel.add(scroll);
        return panel;
    }

    private JPanel crearPanelEscuderias() {
        JPanel panel = new JPanel(new GridLayout(1, 2));

        // --- LADO IZQUIERDO: FORMULARIO ---
        JPanel form = new JPanel(new GridLayout(6, 1));
        form.setBorder(BorderFactory.createTitledBorder("Nueva Escudería"));

        JTextField txtNombre = new JTextField();
        JComboBox<String> cbV1 = new JComboBox<>();
        JComboBox<String> cbV2 = new JComboBox<>();
        JList<String> listaPilotosDisp = new JList<>(modeloPilotos);
        JScrollPane scrollDisp = new JScrollPane(listaPilotosDisp);

        JButton btnRefrescar = new JButton("↻ Cargar Listas");

        JButton btnAdd = new JButton("Registrar Escudería");
        btnAdd.setBackground(F1_RED);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setOpaque(true);
        btnAdd.setBorderPainted(false);

        form.add(new JLabel("Nombre Escudería:"));
        form.add(txtNombre);
        form.add(new JLabel("Selecciona Vehículo 1 y 2:"));
        JPanel pnlV = new JPanel(new GridLayout(1, 2));
        pnlV.add(cbV1);
        pnlV.add(cbV2);
        form.add(pnlV);
        form.add(new JLabel("Pilotos (Ctrl+Clic):"));
        form.add(scrollDisp);

        JPanel pnlBotones = new JPanel(new FlowLayout());
        pnlBotones.add(btnRefrescar);
        pnlBotones.add(btnAdd);
        form.add(pnlBotones);

        // --- LADO DERECHO: LISTA Y BOTÓN DE ARRANQUE ---
        JPanel panelDerecho = new JPanel(new BorderLayout());

        JList<String> lista = new JList<>(modeloEscuderias);
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(BorderFactory.createTitledBorder("Escuderías Listas"));
        panelDerecho.add(scroll, BorderLayout.CENTER);

        // EL BOTÓN MÁGICO PARA INICIAR LA SIMULACIÓN
        JButton btnFinalizar = new JButton("¡Finalizar y arrancar simulación!");
        btnFinalizar.setBackground(new Color(30, 140, 70)); // Verde "Bandera Verde"
        btnFinalizar.setForeground(Color.WHITE);
        btnFinalizar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnFinalizar.setOpaque(true);
        btnFinalizar.setBorderPainted(false);
        btnFinalizar.setPreferredSize(new Dimension(0, 45)); // Hacerlo un poco más alto

        panelDerecho.add(btnFinalizar, BorderLayout.SOUTH);

        // --- EVENTOS ---
        btnRefrescar.addActionListener(e -> {
            cbV1.removeAllItems();
            cbV2.removeAllItems();
            for (int i = 0; i < modeloVehiculos.size(); i++) {
                cbV1.addItem(modeloVehiculos.get(i));
                cbV2.addItem(modeloVehiculos.get(i));
            }
        });

        btnAdd.addActionListener(e -> {
            String nom = txtNombre.getText();
            int i1 = cbV1.getSelectedIndex();
            int i2 = cbV2.getSelectedIndex();
            int[] pIdxs = listaPilotosDisp.getSelectedIndices();

            if (nom.isEmpty() || i1 < 0 || i2 < 0 || pIdxs.length < 2) {
                JOptionPane.showMessageDialog(this, "Faltan datos o pilotos insuficientes (Mínimo 2).");
                return;
            }

            Vehiculo v1 = vehiculosRegistrados.get(i1);
            Vehiculo v2 = vehiculosRegistrados.get(i2);
            List<Driver> pilotos = new ArrayList<>();
            for (int idx : pIdxs) {
                pilotos.add(pilotosRegistrados.get(idx));
            }

            Escuderia esc = new Escuderia(nom, v1, v2, pilotos);
            escuderiasRegistradas.add(esc);
            temporada.addEscuderia(esc);
            for (Driver d : pilotos) {
                temporada.registrarPiloto(d);
            }

            modeloEscuderias.addElement(nom + " (" + pilotos.size() + " pilotos)");
            txtNombre.setText("");
        });

        btnFinalizar.addActionListener(e -> {
            if (escuderiasRegistradas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debes registrar al menos una escudería para correr la temporada.");
                return;
            }
            // Esto cierra la ventana y libera el Latch en Formula1.java
            this.dispose();
        });

        panel.add(form);
        panel.add(panelDerecho);
        return panel;
    }



    public static void mostrar() {
        SwingUtilities.invokeLater(() -> new RegistroF1GUI().setVisible(true));
    }
}