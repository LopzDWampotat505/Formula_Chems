import java.util.ArrayList;
import java.util.List;

public class Temporada {
    private short año;
    private List<Carrera> carreras = new ArrayList<Carrera>();
    private List<Escuderia> escuderias = new ArrayList<Escuderia>();

    public Temporada (short a, Carrera c, Escuderia e){
        año = a;
        addCarrera(c);
        addEscuderia(e);
    }
    private short getAño(){
        return año;
    }
    public void addCarrera(Carrera c){
        //c.setCountry(this);
        if(c != null) {
            carreras.add(a);
        } else {
            System.out.println("Una carrera debe de existir en la temporada");
        }
    }
    public void addEscuderia(Escuderia e){
        //c.setCountry(this);
        if(e != null) {
            escuderias.add(e);
        } else {
            System.out.println("Una escuderia debe de existir en la temporada");
        }
    }
}
