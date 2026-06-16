import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Q3 extends Carrera {
    private Random generadorEstadistico;

    public Q3(String nombre, int dificultad, float minT, float maxT, boolean urbano, List<Driver> roster) {
        super(nombre, dificultad, minT, maxT, urbano, roster);
        this.generadorEstadistico = new Random();
    }

    public Map<Driver, Float> simular(List<Driver> pilotosParticipantes) {
        Map<Driver, Float> resultados = new HashMap<>();
        float minT = getMinTiempo(); 
        float maxT = getMaxTiempo(); 
        boolean urbano = isUrbano();

        for (Driver d : pilotosParticipantes) {
            int habilidad = urbano ? d.getHabUrbana() : d.getHabTradicional();
            float bonoHabilidad = (habilidad / 5.0f) * 2.5f; 
            float bonoVehiculo = (d.getVehiculo().getMotor() / 10.0f) * 2.5f;
            
            float tiempoMediaMu = maxT - bonoHabilidad - bonoVehiculo;
            float desviacionEstandar = 0.8f; // CAOS BAJO EN Q3 (Concentración total)
            
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
        getClasificacion().recibirTiemposQ3(simular(participantes));
    }
}
