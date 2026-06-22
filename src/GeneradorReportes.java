import java.util.List;
import java.util.Map;

public class GeneradorReportes {

    // TABLA 1: Clasificación del Sábado (Orden estricto de Tiempos)
    public void imprimirParrillaClasificacion(Carrera c) {
        System.out.println("\n=========================================================================================");
        System.out.println("                 PARRILLA DE SALIDA (Sábado) - " + c.getNombre().toUpperCase());
        System.out.println("=========================================================================================");
        System.out.printf("%-4s | %-18s | %-4s | %-10s | %-10s | %-10s\n",
                "POS", "PILOTO", "NAC", "TIEMPO Q1", "TIEMPO Q2", "TIEMPO Q3");
        System.out.println("-----------------------------------------------------------------------------------------");

        Clasification juez = c.getClasificacion();
        List<Driver> parrilla = juez.getParrillaFinal();

        if(parrilla == null || parrilla.isEmpty()) {
            System.out.println("La clasificación aún no se ha corrido.");
            return;
        }

        for (int i = 0; i < parrilla.size(); i++) {
            Driver piloto = parrilla.get(i);
            String tQ1 = buscarTiempo(piloto, juez.getTiemposQ1());
            String tQ2 = buscarTiempo(piloto, juez.getTiemposQ2());
            String tQ3 = buscarTiempo(piloto, juez.getTiemposQ3());

            System.out.printf("%-4d | %-18s | %-4s | %-10s | %-10s | %-10s\n",
                    (i + 1), piloto.getNombre(), piloto.getNacionalidad(), tQ1, tQ2, tQ3);
        }
        System.out.println("=========================================================================================\n");
    }

    // TABLA 2: Carrera del Domingo (Rebases y Puntos)
    // TABLA 2: Carrera del Domingo (Rebases y Puntos)
    public void imprimirResultadosCarrera(Carrera c, Temporada t) {
        System.out.println("\n=======================================================================");
        System.out.println("               RESULTADOS DEL GRAN PREMIO (Domingo)");
        System.out.println("=======================================================================");
        System.out.printf("%-4s | %-18s | %-4s | %-10s | %-7s\n",
                "POS", "PILOTO", "NAC", "TIEMPO GP", "PTS TOTAL");
        System.out.println("-----------------------------------------------------------------------");

        // CORRECCIÓN: Primero validamos que c.getGranPremio() no sea null para evitar la explosión
        if(c.getGranPremio() == null || c.getGranPremio().getResultadosFinales() == null || c.getGranPremio().getResultadosFinales().isEmpty()) {
            System.out.println("El Gran Premio aún no se ha corrido.");
            System.out.println("=======================================================================\n");
            return;
        }

        List<Driver> resultadosGP = c.getGranPremio().getResultadosFinales();

        for (int i = 0; i < resultadosGP.size(); i++) {
            Driver piloto = resultadosGP.get(i);
            String tGP = "Finalizó " + (i + 1) + "º";
            int puntosTotales = t.getCampeonato().get(piloto);

            System.out.printf("%-4d | %-18s | %-4s | %-10s | %-7d\n",
                    (i + 1), piloto.getNombre(), piloto.getNacionalidad(), tGP, puntosTotales);
        }
        System.out.println("=======================================================================\n");
    }

    public void imprimirTablaTemporada(Temporada t) {
        System.out.println("\n--- CAMPEONATO MUNDIAL TRAS " + t.getCantidadCarreras() + " PISTAS ---");
        t.getCampeonato().entrySet().stream()
                .sorted(Map.Entry.<Driver, Integer>comparingByValue().reversed())
                .forEach(e -> System.out.println(e.getKey().getNombre() + " - " + e.getValue() + " pts"));
    }

    private String buscarTiempo(Driver pilotoBuscado, Map<Driver, Float> mapaSesion) {
        if (mapaSesion == null || mapaSesion.isEmpty() || !mapaSesion.containsKey(pilotoBuscado)) {
            return "Eliminado";
        }
        return String.format("%.3fs", mapaSesion.get(pilotoBuscado));
    }
    public List<Driver> ordenCampeonato(Temporada t) {
        return t.getCampeonato().entrySet().stream()
                .sorted(Map.Entry.<Driver, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toList());
    }

    public Object[][] generarDatosClasificacion(Carrera c) {
        Clasification juez = c.getClasificacion();
        List<Driver> parrilla = juez.getParrillaFinal();
        if(parrilla == null || parrilla.isEmpty()) return new Object[0][0];

        Object[][] datos = new Object[parrilla.size()][6];
        for (int i = 0; i < parrilla.size(); i++) {
            Driver p = parrilla.get(i);
            datos[i][0] = i + 1;
            datos[i][1] = p.getNombre();
            datos[i][2] = p.getNacionalidad();
            datos[i][3] = buscarTiempo(p, juez.getTiemposQ1());
            datos[i][4] = buscarTiempo(p, juez.getTiemposQ2());
            datos[i][5] = buscarTiempo(p, juez.getTiemposQ3());
        }
        return datos;
    }

    public Object[][] generarDatosCarrera(Carrera c, Temporada t) {
        if(c.getGranPremio() == null || c.getGranPremio().getResultadosFinales() == null) return new Object[0][0];
        List<Driver> resultados = c.getGranPremio().getResultadosFinales();
        Object[][] datos = new Object[resultados.size()][5];
        for (int i = 0; i < resultados.size(); i++) {
            Driver p = resultados.get(i);
            datos[i][0] = i + 1;
            datos[i][1] = p.getNombre();
            datos[i][2] = p.getNacionalidad();
            datos[i][3] = "Finalizó " + (i+1) + "º";
            datos[i][4] = t.getCampeonato().get(p);
        }
        return datos;
    }
}