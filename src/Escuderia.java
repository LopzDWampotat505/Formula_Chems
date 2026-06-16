import java.util.ArrayList;
import java.util.List;

public class Escuderia {
    private String nombre;
    private final List<Driver> listaDrivers;
    private final List<Vehiculo> listaVehiculos;


    private static final byte MIN_PILOTOS = 2;
    private static final byte MAX_PILOTOS = 8;

    public Escuderia(String n, Vehiculo v1, Vehiculo v2, List<Driver> drivers){
        if(v1 == null || v2 ==null){
            throw new IllegalArgumentException("Una escudería debe de tener vehiculos existentes registrados");
        }

        if(drivers == null){
            throw new IllegalArgumentException("La lista de pilotos de una escudería no debe de ser inexistentes");
        }
        if(drivers.size() < MIN_PILOTOS || drivers.size() > MAX_PILOTOS){
            throw new IllegalArgumentException("La lista de pilotos debe tener entre "+MIN_PILOTOS+" y "+MAX_PILOTOS" para ser valida.");
        }
        if(drivers.contains(null)){
            throw new IllegalArgumentException("La lista de pilotos de una escudería no debe de tener pilosos inexistentes");
        }

        this.nombre = n;
        this.listaVehiculos = new ArrayList<>();
        this.listaVehiculos.add(v1);
        this.listaVehiculos.add(v2);

        this.listaDrivers = new ArrayList<>(drivers);
    }

    public void addDriver(Driver d) {
        if (d == null) {
            throw new IllegalArgumentException("El piloto no puede ser inexistente.");
        }
        if (this.listaDrivers.size() >= MAX_PILOTOS) {
            throw new IllegalStateException("No se pueden agregar más pilotos. El límite máximo es " + MAX_PILOTOS);
        }
        this.listaDrivers.add(d);
    }

    public String getNombre(){
        return nombre;
    }

    public List<Vehiculo> getVehiculos() {
        return ArrayList<>(this.listaVehiculos);
    }

    public List<Driver> getDrivers() {
        return new ArrayList<>(this.listaDrivers);
    }

}
