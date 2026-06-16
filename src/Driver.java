public class Driver {

    private String nombre;
    private float habilidad;
    private float factorHabilidad;
    private String nacionalidad;
    private Vehiculo vehiculo;

    public Driver(String n, float hab, Vehiculo v){
        nombre = n;
        setHabilidad(hab);
        setVehiculo(v);
    }

    public String getNombre() {
        return nombre;
    }
    public float getHabilidad() {
        return factorHabilidad;
    }
    public String getNacionalidad() {
        return nacionalidad;
    }
    public Vehiculo getVehiculo() {
        return vehiculo;
    }
    public void setHabilidad(float h) {
        if(h < 0.0F || h > 10.0F) {
            throw new IllegalArgumentException("Canaliza la habilidad en un factor de 0 a 10");
        }
        this.habilidad = h;
    }

    public void setVehiculo(Vehiculo v) {
        vehiculo = v;
    }
    public void calculateFactorHabilidad (float fH) {
        fH = habilidad/10;
        this.factorHabilidad = fH;
    }

}
