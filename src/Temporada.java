import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Temporada {
    private short año;
    private List<Carrera> carreras = new ArrayList<>();
    private List<Escuderia> escuderias = new ArrayList<>();
    private Map<Driver, Integer> campeonatoMundial = new HashMap<>();

    public Temporada (short a, Carrera c, Escuderia e){
        año = a;
        addCarrera(c);
        addEscuderia(e);
    }

    public short getAño(){ return año; }

    public void addCarrera(Carrera c){
        if(c != null) { carreras.add(c); }
        else { System.out.println("Una carrera debe de existir en la temporada"); }
    }

    public void addEscuderia(Escuderia e){
        if(e != null) { escuderias.add(e); }
        else { System.out.println("Una escuderia debe de existir en la temporada"); }
    }

    public void registrarPiloto(Driver d) { campeonatoMundial.put(d, 0); }

    // NUEVO MÉTODO: A prueba de resimulaciones
    public void actualizarCampeonato() {
        // 1. Ponemos los puntos de todos los pilotos a cero
        for (Driver d : campeonatoMundial.keySet()) {
            campeonatoMundial.put(d, 0);
        }

        // 2. Volvemos a sumar recorriendo TODAS las carreras del año
        int[] puntos = {25, 18, 15, 12, 10, 8, 6, 4, 2, 1};
        for (Carrera c : carreras) {
            // Si la carrera ya tiene un Gran Premio con resultados, asignamos los puntos
            if (c.getGranPremio() != null && c.getGranPremio().getResultadosFinales() != null && !c.getGranPremio().getResultadosFinales().isEmpty()) {
                List<Driver> resultados = c.getGranPremio().getResultadosFinales();
                for (int i = 0; i < resultados.size() && i < 10; i++) {
                    Driver ganador = resultados.get(i);
                    campeonatoMundial.put(ganador, campeonatoMundial.get(ganador) + puntos[i]);
                }
            }
        }
    }

    public int getCantidadCarreras() { return carreras.size(); }
    public List<Carrera> getCarrera() { return this.carreras; }

    // Devuelve una carrera por número de ronda (1 = primera carrera)
    public Carrera getCarrera(int numeroRonda) {
        if (numeroRonda < 1 || numeroRonda > carreras.size()) {
            throw new IndexOutOfBoundsException("No existe la ronda " + numeroRonda + ". Total carreras: " + carreras.size());
        }
        return carreras.get(numeroRonda - 1);
    }
    public Map<Driver, Integer> getCampeonato() { return campeonatoMundial; }

    public List<Escuderia> getEscuderia() { return this.escuderias; }

    public Map<Escuderia, Integer> getPuntosPorEscuderia() {
        Map<Escuderia, Integer> puntos = new HashMap<>();
        for (Escuderia e : escuderias) puntos.put(e, 0);
        for (Map.Entry<Driver, Integer> entry : campeonatoMundial.entrySet()) {
            for (Escuderia e : escuderias) {
                if (e.getDrivers().contains(entry.getKey())) {
                    puntos.put(e, puntos.get(e) + entry.getValue());
                    break;
                }
            }
        }
        return puntos;
    }

}