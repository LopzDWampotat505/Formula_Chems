import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Cargando base de datos...");

        // --- 1. INYECCIÓN DE DATOS ---
        Vehiculo autoMax = new Vehiculo((byte)10, (byte)10, (byte)8);
        Vehiculo autoCheco = new Vehiculo((byte)10, (byte)10, (byte)8);
        Vehiculo autoLando = new Vehiculo((byte)10, (byte)10, (byte)8);
        Vehiculo autoOscar = new Vehiculo((byte)10, (byte)10, (byte)8);

        Driver verstappen = new Driver("Max Verstappen", "NED", 9.8f, autoMax);
        Driver perez = new Driver("Sergio Pérez", "MEX", 8.8f, autoCheco);
        Driver norris = new Driver("Lando Norris", "GBR", 9.5f, autoLando);
        Driver piastri = new Driver("Oscar Piastri", "AUS", 9.0f, autoOscar);

        List<Driver> pilotosRedBull = new ArrayList<>();
        pilotosRedBull.add(verstappen); pilotosRedBull.add(perez);

        List<Driver> pilotosMcLaren = new ArrayList<>();
        pilotosMcLaren.add(norris); pilotosMcLaren.add(piastri);

        List<Driver> rosterCompleto = new ArrayList<>();
        rosterCompleto.addAll(pilotosRedBull);
        rosterCompleto.addAll(pilotosMcLaren);

        for(int i = 5; i <= 20; i++) {
            Vehiculo autoBot = new Vehiculo((byte)8, (byte)10, (byte)8);
            Driver bot = new Driver("Piloto Bot " + i, "---", 7.5f, autoBot);
            rosterCompleto.add(bot);
        }

        Escuderia redBull = new Escuderia("Oracle Red Bull Racing", autoMax, autoCheco, pilotosRedBull);
        Escuderia mcLaren = new Escuderia("McLaren F1 Team", autoLando, autoOscar, pilotosMcLaren);

        GranPremio gpBahrein = new GranPremio("Bahrein", 7, 91.0f, 97.0f, rosterCompleto);
        GranPremio gpMonaco = new GranPremio("Mónaco", 10, 71.0f, 77.0f, rosterCompleto);

        Temporada temporada2026 = new Temporada((short)2026, gpBahrein, redBull);
        temporada2026.addEscuderia(mcLaren);
        temporada2026.addCarrera(gpMonaco);

        Formula1 campeonatoMundial = new Formula1(temporada2026);
        for(Driver d : rosterCompleto) { temporada2026.registrarPiloto(d); }

        GeneradorReportes periodista = new GeneradorReportes();
        int totalPistas = temporada2026.getCantidadCarreras();
        int pistaProgreso = 1;
        boolean campeonatoActivo = true;

        System.out.println("=== SIMULADOR " + campeonatoMundial.getNombreComp() + " 2026 ===");

        // --- 2. CICLO DEL MENÚ ---
        while (pistaProgreso <= totalPistas && campeonatoActivo) {
            int pistaEnPantalla = pistaProgreso;
            Carrera circuitoActual = temporada2026.getCarrera(pistaEnPantalla);

            System.out.println("\n>>> LLEGAMOS AL GP DE " + circuitoActual.getNombre().toUpperCase() + " <<<");
            // Eliminamos la auto-simulación. Ahora el usuario decide cuándo arrancar.

            boolean enMenu = true;
            boolean finDeSemanaSimulado = false; // Variable de seguridad

            while (enMenu) {
                Carrera circuitoViendo = temporada2026.getCarrera(pistaEnPantalla);

                System.out.println("\n--- PANEL: " + circuitoViendo.getNombre() + " ---");
                System.out.println("1. Simular Fin de Semana (Clasificación y GP)");
                System.out.println("2. Ver tablas detalladas del evento");
                System.out.println("3. Ver Tabla Global de Puntos");
                System.out.println("4. Resimular SOLO el Gran Premio");
                System.out.println("5. Resimular TODO el fin de semana");
                System.out.println("6. Fast Forward (Correr resto del año)");
                System.out.println("7. Avanzar a la siguiente fecha");
                System.out.println("8. Guardar JSONs de la temporada (equipos + progreso)");
                System.out.println("9. Leer JSON guardado (equipos o progreso)");
                System.out.println("10. Salir");
                System.out.print("Elige una opción: ");

                int opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1:
                        if (!finDeSemanaSimulado) {
                            System.out.println("\n[SISTEMA] Simulando Clasificación y Gran Premio...");
                            ejecutarEmbudoDeSimulacion(circuitoViendo, temporada2026);
                            finDeSemanaSimulado = true;
                            System.out.println("[SISTEMA] ¡Fin de semana completado!");
                        } else {
                            System.out.println("Ya simulaste este evento. Usa las opciones 4 o 5 para resimular.");
                        }
                        break;
                    case 2:
                        periodista.imprimirParrillaClasificacion(circuitoViendo);
                        periodista.imprimirResultadosCarrera(circuitoViendo, temporada2026);
                        break;
                    case 3:
                        periodista.imprimirTablaTemporada(temporada2026);
                        break;
                    case 4:
                        if (finDeSemanaSimulado) {
                            circuitoViendo.getGranPremio().simularSesion(circuitoViendo.getClasificacion().getParrillaFinal());
                            temporada2026.actualizarCampeonato(); // Recalcular puntos justos
                            System.out.println("¡GP Resimulado y puntos recalculados!");
                        } else {
                            System.out.println("Primero debes simular el fin de semana inicial (Opción 1).");
                        }
                        break;
                    case 5:
                        ejecutarEmbudoDeSimulacion(circuitoViendo, temporada2026);
                        finDeSemanaSimulado = true;
                        System.out.println("¡Fin de semana reiniciado por completo y puntos recalculados!");
                        break;
                    case 6:
                        System.out.println("\n>>> INICIANDO FAST FORWARD <<<");
                        while (pistaProgreso <= totalPistas) {
                            Carrera pistaRapida = temporada2026.getCarrera(pistaProgreso);

                            if (pistaProgreso == pistaEnPantalla && finDeSemanaSimulado) {
                                pistaProgreso++;
                                continue;
                            }
                            System.out.println("Simulando en background: GP de " + pistaRapida.getNombre() + "...");
                            ejecutarEmbudoDeSimulacion(pistaRapida, temporada2026);
                            pistaProgreso++;
                        }
                        System.out.println("\n¡Todas las carreras han sido simuladas con éxito!");
                        periodista.imprimirTablaTemporada(temporada2026);
                        enMenu = false;
                        break;
                    case 7:
                        if(finDeSemanaSimulado) {
                            pistaProgreso++;
                            enMenu = false;
                        } else {
                            System.out.println("No puedes avanzar sin correr la carrera, usa Fast Forward (6) si quieres saltarla.");
                        }
                        break;
                    case 8: // Guarda ambos JSONs
                        ManejadorArchivos.guardarEquiposJson(temporada2026);
                        ManejadorArchivos.guardarProgresoCarrerasJson(temporada2026);
                        System.out.println("[SISTEMA] JSONs generados: " + temporada2026.getAño() + "_equipos.json y " + temporada2026.getAño() + "_progreso.json");
                        break;

                    case 9: // Lee un JSON guardado
                        System.out.println("¿Qué archivo quieres leer?");
                        System.out.println("  1. Equipos  (" + temporada2026.getAño() + "_equipos.json)");
                        System.out.println("  2. Progreso (" + temporada2026.getAño() + "_progreso.json)");
                        System.out.print("Elige (1 o 2): ");
                        int tipoJson = scanner.nextInt();
                        scanner.nextLine();
                        if (tipoJson == 1) {
                            ManejadorArchivos.leerArchivoJson(temporada2026.getAño(), "equipos");
                        } else if (tipoJson == 2) {
                            ManejadorArchivos.leerArchivoJson(temporada2026.getAño(), "progreso");
                        } else {
                            System.out.println("Opción inválida.");
                        }
                        break;
                    case 10:
                        enMenu = false;
                        campeonatoActivo = false;
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            }
        }
        System.out.println("\n=== TEMPORADA FINALIZADA ===");
    }

    private static void ejecutarEmbudoDeSimulacion(Carrera c, Temporada t) {
        Q1 q1 = new Q1(c.getNombre(), c.getNivelDificultad(), c.getMinTiempo(), c.getMaxTiempo(), c.getRoster());
        Q2 q2 = new Q2(c.getNombre(), c.getNivelDificultad(), c.getMinTiempo(), c.getMaxTiempo(), c.getRoster());
        Q3 q3 = new Q3(c.getNombre(), c.getNivelDificultad(), c.getMinTiempo(), c.getMaxTiempo(), c.getRoster());
        GranPremio gp = new GranPremio(c.getNombre(), c.getNivelDificultad(), c.getMinTiempo(), c.getMaxTiempo(), c.getRoster());
        c.setGranPremio(gp);
        Clasification juez = c.getClasificacion();

        juez.recibirTiemposQ1(q1.simular(c.getRoster()));
        List<Driver> pasanQ2 = juez.obtenerSobrevivientesQ1();

        juez.recibirTiemposQ2(q2.simular(pasanQ2));
        List<Driver> pasanQ3 = juez.obtenerSobrevivientesQ2();

        juez.recibirTiemposQ3(q3.simular(pasanQ3));

        juez.generarParrillaOficial();
        gp.simularSesion(juez.getParrillaFinal());

        // Ahora usamos el actualizador maestro
        t.actualizarCampeonato();
    }
}