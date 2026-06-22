import java.util.List;


/*
 * Orquesta la simulación de un fin de semana de Gran Premio (Q1 → Q2 → Q3 → GP)
 * y las acciones de avance de temporada. Extraído de Main para que la GUI
 * (o cualquier otro cliente) solo tenga que invocar estos métodos sin
 * reimplementar el flujo de simulación.
 */
public class SimuladorController {


    private final Temporada temporada;
    private int              pistaProgreso; // Índice base 1 de la ronda actual


    public SimuladorController(Temporada temporada) {
        this.temporada     = temporada;
        this.pistaProgreso = 1;
    } // fin del constructor SimuladorController


    // --- Estado de la temporada ---
    public Temporada getTemporada()      { return temporada; }
    public int       getPistaProgreso()  { return pistaProgreso; }
    public int       getTotalPistas()    { return temporada.getCantidadCarreras(); }
    public boolean   hayMasCarreras()    { return pistaProgreso <= getTotalPistas(); }


    // Retorna el circuito correspondiente a la ronda actual
    public Carrera getCarreraActual() {
        return temporada.getCarrera(pistaProgreso);
    } // fin del metodo getCarreraActual


    // Retorna el circuito de una ronda específica (índice base 1)
    public Carrera getCarrera(int ronda) {
        return temporada.getCarrera(ronda);
    } // fin del metodo getCarrera


    /*
     * Embudo de simulación: Q1 → Q2 → Q3 → Gran Premio.
     * La fórmula (N-10)/2 vive en Clasification; aquí solo orquestamos.
     */
    public void simularFinDeSemana(Carrera c) {
        // CORREGIDO: Adaptado a tus constructores originales
        Q1 q1 = new Q1(c.getNombre(), c.getNivelDificultad(), c.getMinTiempo(), c.getMaxTiempo(), c.getRoster());
        Q2 q2 = new Q2(c.getNombre(), c.getNivelDificultad(), c.getMinTiempo(), c.getMaxTiempo(), c.getRoster());
        Q3 q3 = new Q3(c.getNombre(), c.getNivelDificultad(), c.getMinTiempo(), c.getMaxTiempo(), c.getRoster());
        GranPremio gp = new GranPremio(c.getNombre(), c.getNivelDificultad(), c.getMinTiempo(), c.getMaxTiempo(), c.getRoster());

        c.setGranPremio(gp);
        Clasification juez = c.getClasificacion();

        juez.recibirTiemposQ1(q1.simular(c.getRoster())); // Se usa simular() no simularFase()
        List<Driver> pasanQ2 = juez.obtenerSobrevivientesQ1();

        juez.recibirTiemposQ2(q2.simular(pasanQ2));
        List<Driver> pasanQ3 = juez.obtenerSobrevivientesQ2();

        juez.recibirTiemposQ3(q3.simular(pasanQ3));
        juez.generarParrillaOficial();

        gp.simularCarreraFinal(juez.getParrillaFinal());
        temporada.actualizarCampeonato();
    }


    // Resimula únicamente el Gran Premio del domingo, conservando la parrilla ya generada
    public void resimularGranPremio(Carrera c) {
        if (c.getGranPremio() == null) {
            throw new IllegalStateException("Primero debes simular el fin de semana.");
        }
        List<Driver> parrilla = c.getClasificacion().getParrillaFinal();
        c.getGranPremio().simularSesion(parrilla);
        temporada.actualizarCampeonato();
    } // fin del metodo resimularGranPremio


    // Avanza a la siguiente ronda del calendario
    public void avanzarRonda() {
        if (hayMasCarreras()) pistaProgreso++;
    } // fin del metodo avanzarRonda


    // Simula todas las rondas restantes del calendario de un tirón
    public void fastForward() {
        while (hayMasCarreras()) {
            Carrera c = getCarreraActual();
            simularFinDeSemana(c);
            pistaProgreso++;
        }
    } // fin del metodo fastForward
} // fin de la clase SimuladorController
