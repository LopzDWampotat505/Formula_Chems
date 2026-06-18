import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Clasification {
    private Map<Driver, Float> timeQ1 = new HashMap<>();
    private Map<Driver, Float> timeQ2 = new HashMap<>();
    private Map<Driver, Float> timeQ3 = new HashMap<>();
    private List<Driver> parrillaFinal = new ArrayList<>();

    public void recibirTiemposQ1(Map<Driver, Float> tiempos) { this.timeQ1 = tiempos; }
    public void recibirTiemposQ2(Map<Driver, Float> tiempos) { this.timeQ2 = tiempos; }
    public void recibirTiemposQ3(Map<Driver, Float> tiempos) { this.timeQ3 = tiempos; }

    public List<Driver> obtenerSobrevivientesQ1() { return ordenarYCortar(timeQ1, 15); }
    public List<Driver> obtenerSobrevivientesQ2() { return ordenarYCortar(timeQ2, 10); }

    //  Ordenan a los eliminados por tiempo
    private List<Driver> obtenerEliminadosQ1() {
        return timeQ1.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .skip(15) // Ignora a los 15 mejores
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private List<Driver> obtenerEliminadosQ2() {
        return timeQ2.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .skip(10) // Ignora a los 10 mejores
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // El método ahora se construye a sí mismo sin necesidad de parámetros externos
    public void generarParrillaOficial() {
        parrillaFinal.clear();
        parrillaFinal.addAll(ordenarYCortar(timeQ3, 10)); // Del 1 al 10 (Orden perfecto)
        parrillaFinal.addAll(obtenerEliminadosQ2());      // Del 11 al 15 (Orden perfecto)
        parrillaFinal.addAll(obtenerEliminadosQ1());      // Del 16 al 20 (Orden perfecto)
    }

    private List<Driver> ordenarYCortar(Map<Driver, Float> mapaTiempos, int limite) {
        return mapaTiempos.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(limite)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public Map<Driver, Float> getTiemposQ1() { return timeQ1; }
    public Map<Driver, Float> getTiemposQ2() { return timeQ2; }
    public Map<Driver, Float> getTiemposQ3() { return timeQ3; }
    public List<Driver> getParrillaFinal() { return parrillaFinal; }
}