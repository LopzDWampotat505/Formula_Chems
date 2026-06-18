import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Q2 extends Carrera {
    private Random generadorEstadistico;

    public Q2(String nombre, int dificultad, float minT, float maxT, List<Driver> roster) {
        super(nombre, dificultad, minT, maxT, roster);
        this.generadorEstadistico = new Random();
    }

    public Map<Driver, Float> simular(List<Driver> pilotosParticipantes) {
        Map<Driver, Float> resultados = new HashMap<>();
        float minT = getMinTiempo();
        float maxT = getMaxTiempo();

        for (Driver d : pilotosParticipantes) {
            float bonoHabilidad = d.getHabilidad() * 2.5f;
            float bonoVehiculo = (d.getVehiculo().getMotor() / 10.0f) * 2.5f;

            float tiempoMediaMu = maxT - bonoHabilidad - bonoVehiculo;
            float desviacionEstandar = 1.0f; // Varianza ajustada a Q2

            float valorGaussiano = (float) generadorEstadistico.nextGaussian();
            float tiempoCalculado = tiempoMediaMu + (valorGaussiano * desviacionEstandar);

            if (tiempoCalculado < minT) {
                tiempoCalculado = minT + (generadorEstadistico.nextFloat() * 0.2f);
            }
            resultados.put(d, tiempoCalculado);
        }
        return resultados;
    }

    @Override
    public void simularSesion(List<Driver> participantes) {
        getClasificacion().recibirTiemposQ2(simular(participantes));
    }
}