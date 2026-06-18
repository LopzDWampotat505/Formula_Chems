import java.util.ArrayList;
import java.util.List;

// Corrección: Le quitamos el guion bajo para que coincida con el constructor
public class GranPremio extends Carrera {
    private List<Driver> tablaPosicionesFinales;

    public GranPremio(String nombre, int dificultad, float minT, float maxT, List<Driver> roster) {
        super(nombre, dificultad, minT, maxT, roster);
        this.tablaPosicionesFinales = new ArrayList<>();
    }

    public void simularCarreraFinal(List<Driver> parrillaSalida) {
        List<Driver> carrera = new ArrayList<>(parrillaSalida);
        for(int i = 0; i < carrera.size() - 1; i++) {
            if(Math.random() > 0.75) {
                Driver temp = carrera.get(i);
                carrera.set(i, carrera.get(i+1));
                carrera.set(i+1, temp);
            }
        }
        this.tablaPosicionesFinales = carrera;
    }

    public List<Driver> getResultadosFinales() { return tablaPosicionesFinales; }

    @Override
    public void simularSesion(List<Driver> participantes) {
        simularCarreraFinal(participantes);
    }
}