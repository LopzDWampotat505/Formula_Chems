import java.util.List;

public abstract class Carrera {
    private String nombre;
    private int nivelDificultad;
    private float minTiempo;
    private float maxTiempo;
    private List<Driver> rosterPilotos;
    private Clasification clasificacion;
    private GranPremio granPremio;

    public Carrera(String nombre, int nivelDificultad, float minTiempo, float maxTiempo, List<Driver> roster) {
        this.nombre = nombre;
        this.nivelDificultad = nivelDificultad;
        this.minTiempo = minTiempo;
        this.maxTiempo = maxTiempo;
        this.rosterPilotos = roster;
        this.clasificacion = new Clasification();
    }

    public String getNombre() { return nombre; }
    public int getNivelDificultad() { return nivelDificultad; }
    public float getMinTiempo() { return minTiempo; }
    public float getMaxTiempo() { return maxTiempo; }
    public List<Driver> getRoster() { return rosterPilotos; }
    public Clasification getClasificacion() { return clasificacion; }
    public void setGranPremio(GranPremio gp) { this.granPremio = gp; }
    public GranPremio getGranPremio() { return granPremio; }

    public abstract void simularSesion(List<Driver> participantes);
}