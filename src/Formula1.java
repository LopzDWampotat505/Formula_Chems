import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Formula1 {

    private final String nombreComp = "Formula 1";
    private final List<Temporada> listaTemporada = new ArrayList<>();

    public void addTemporada(Temporada t) {
        if (t != null) listaTemporada.add(t);
    }

    public static void iniciar() {
        Formula1 campeonato = new Formula1();

        // 1. Abre tu ventana de registro y pausa el sistema hasta que se cierre
        Temporada temporada = campeonato.abrirRegistroYEsperar();

        // Si se cerró la ventana en la tachita sin guardar nada, el simulador se apaga
        if (temporada == null) {
            System.out.println("Registro cancelado. Cerrando simulador.");
            return;
        }

        campeonato.addTemporada(temporada);

        // 2. Prepara a los pilotos y las pistas en memoria
        List<Driver> rosterCompleto = campeonato.construirRoster(temporada);
        campeonato.asignarRosterACarreras(temporada, rosterCompleto);

        // 3. Dispara la interfaz principal de la simulación
        campeonato.ejecutarSimulacion(temporada);
    }

    private Temporada abrirRegistroYEsperar() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        CountDownLatch latch = new CountDownLatch(1);
        RegistroF1GUI[] ventanaRef = new RegistroF1GUI[1];

        SwingUtilities.invokeLater(() -> {
            RegistroF1GUI ventana = new RegistroF1GUI();
            ventanaRef[0] = ventana;

            // Este listener detecta cuando le das al botón verde que ejecuta "this.dispose()"
            ventana.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    latch.countDown();
                }
            });
            ventana.setVisible(true);
        });

        // El programa se congela aquí hasta que la ventana se cierra
        try { latch.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        return (ventanaRef[0] != null) ? ventanaRef[0].getTemporada() : null;
    }

    private List<Driver> construirRoster(Temporada t) {
        List<Driver> roster = new ArrayList<>();
        for (Escuderia e : t.getEscuderia()) {
            for (Driver d : e.getDrivers()) {
                if (!roster.contains(d)) {
                    roster.add(d);
                    t.registrarPiloto(d);
                }
            }
        }
        return roster;
    }

    private void asignarRosterACarreras(Temporada t, List<Driver> roster) {
        for (Carrera c : t.getCarrera()) {
            c.getRoster().clear();
            c.getRoster().addAll(roster);
        }
    }

    private void ejecutarSimulacion(Temporada temporada) {
        // AQUÍ ESTÁ LA MAGIA: ADIÓS CONSOLA, HOLA MAINGUI
        SwingUtilities.invokeLater(() -> {

            // INTENTO A: Si MainGUI pide un SimuladorController en su constructor
            SimuladorController controlador = new SimuladorController(temporada);
            MainGUI ventanaPrincipal = new MainGUI(controlador);

            // OJO: Si la línea de arriba te marca error rojo en 'new MainGUI(...)',
            // significa que tu amiga programó MainGUI para recibir la clase Temporada directo.
            // En ese caso, borra las dos líneas de arriba y descomenta esta:
            // MainGUI ventanaPrincipal = new MainGUI(temporada);

            ventanaPrincipal.setVisible(true);
        });
    }
}