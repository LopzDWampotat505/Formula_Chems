public class Driver {
    private String nombre;
    private float habilidad; // Escala de 0 a 10
    private float factorHabilidad;
    private String nacionalidad;
    private Vehiculo vehiculo;

    public Driver(String n, String nac, float hab, Vehiculo v){
        this.nombre = n;
        this.nacionalidad = nac;
        setHabilidad(hab);
        setVehiculo(v);
        calculateFactorHabilidad(); // Se calcula al nacer
    }

    public String getNombre() { return nombre; }
    public float getHabilidad() { return factorHabilidad; } // Retorna el factor 0.0 - 1.0
    public String getNacionalidad() { return nacionalidad; }
    public Vehiculo getVehiculo() { return vehiculo; }

    public void setHabilidad(float h) {
        if(h < 0.0F || h > 10.0F) {
            throw new IllegalArgumentException("Canaliza la habilidad en un factor de 0 a 10");
        }
        this.habilidad = h;
    }

    public void setVehiculo(Vehiculo v) { this.vehiculo = v; }

    public void calculateFactorHabilidad() {
        this.factorHabilidad = this.habilidad / 10.0f;
    }
}