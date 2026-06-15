import java.util.ArrayList;
import java.util.List;

public class Formula1 {
    private String nombreComp = "Formula 1";
    private List<Temporada> listaTemporada = new ArrayList<Temporada>();

    public Formula1(Temporada a){
        addTemporada(a);
    }

    public String getNombreComp() {
        return nombreComp;
    }

    public void addTemporada(Temporada a){
        //c.setCountry(this);
        if(a != null) {
            listaTemporada.add(a);
        } else {
            System.out.println("Una temporada debe de existir en F1");
        }
    }

}
