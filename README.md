# 🏎️ Sistema de Simulación de Fórmula 1 — Formula Chems

**Universidad de las Américas Puebla**
Departamento de Computación, Electrónica y Mecatrónica
Materia: Programación Orientada a Objetos (POO)
Fecha de entrega: 22 de junio de 2026

---

## 📋 Descripción

Sistema orientado a objetos desarrollado en Java que registra, administra y **simula** temporadas completas de Fórmula 1. El sistema modela fielmente el formato real de la competencia: tres sesiones clasificatorias (Q1, Q2, Q3) que determinan la parrilla de salida, seguidas de la carrera del Gran Premio del domingo.

La arquitectura está diseñada para almacenar de forma independiente el historial estadístico de cualquier año de competición y reproducir sus resultados de manera interactiva, con una interfaz gráfica completa construida en Java Swing.

---

## 🚀 Objetivos

El objetivo principal es demostrar la viabilidad del paradigma POO para administrar, modelar y recrear la complejidad de la Fórmula 1.

**Objetivos específicos:**

- **Administrar escuderías:** Registrar los datos de cada equipo, sus pilotos asignados y las características técnicas de sus monoplazas (motor, llantas, peso en escala 0–10).
- **Calendarizar premios:** Especificar nombre, dificultad y tiempos históricos (mínimo/máximo) de cada Gran Premio en la temporada.
- **Registrar clasificaciones:** Almacenar los tiempos de Q1, Q2 y Q3 para definir la parrilla de salida mediante la fórmula de eliminación dinámica `(N-10)/2`.
- **Controlar resultados:** Documentar las posiciones finales de la carrera y actualizar el Campeonato Mundial de Pilotos y Constructores.
- **Generar reportes:** Producir tablas de clasificación, resultados de GP y campeonato, tanto en consola como en ventanas gráficas con colores por escudería.
- **Persistir datos:** Guardar y cargar temporadas completas en formato JSON para continuar sesiones previas.

---

## 🛠️ Arquitectura del Sistema

El diseño sigue un flujo de dependencias lógico y estricto que permite escalar a cualquier cantidad de escuderías, pilotos y circuitos.

### Jerarquía de clases

```
Formula1
└── Temporada
    ├── List<Escuderia>
    │   ├── List<Driver>
    │   │   └── Vehiculo
    │   └── List<Vehiculo>
    └── List<Carrera>  (abstract)
        ├── Q1          ──┐
        ├── Q2          ──┤── Motor estadístico gaussiano
        ├── Q3          ──┘
        └── GranPremio  ── Algoritmo probabilístico de rebases
            └── Clasification  ── Juez oficial del fin de semana
```

### Decisiones de diseño clave

**Motor de simulación gaussiano:** Las sesiones Q1, Q2 y Q3 calculan los tiempos de vuelta usando una distribución normal centrada en `maxTiempo - bonoHabilidad - bonoVehiculo`, con desviaciones estándar de 1.2, 1.0 y 0.8 respectivamente, simulando el apretamiento progresivo de los tiempos conforme avanza la clasificación.

**Algoritmo de rebases (Gran Premio):** Cada piloto recibe una puntuación compuesta de `habilidad × 0.50 + motor × 0.30 + azar × 0.20`. El resultado se ordena por esa puntuación, simulando que la habilidad y el auto influyen en el resultado pero con un factor de imprevisibilidad.

**Fórmula dinámica de eliminación:** Con `N` pilotos inscritos, se eliminan `(N-10)/2` en Q1 y la misma cantidad en Q2, dejando siempre 10 pilotos para Q3. Con el grid estándar de 22 pilotos se eliminan 6 por ronda.

**Parrilla oficial:** La posición final en la parrilla combina los tres clasificatorios — posiciones 1–10 por tiempos de Q3, 11–16 por tiempos de Q2 (eliminados), 17–22 por tiempos de Q1 (eliminados).

**Campeonato a prueba de resimulaciones:** `actualizarCampeonato()` reinicia todos los puntos a cero y los recalcula desde la primera carrera de la temporada, garantizando consistencia aunque se resimule cualquier GP.

---

## 💻 Interfaz Gráfica (GUI)

La aplicación cuenta con dos ventanas principales construidas en Java Swing:

**Ventana de Registro (`RegistroF1GUI`):** Asistente de configuración en tres pestañas secuenciales:
- *Temporada:* año, circuitos con nombre/dificultad/tiempos, con botón de autocarga desde JSON.
- *Vehículos:* registro con sliders de motor, llantas y peso.
- *Pilotos:* nombre, nacionalidad, habilidad y asignación de vehículo.
- *Escuderías:* agrupación de pilotos y vehículos con validación de mínimos/máximos reglamentarios.

**Ventana Principal (`MainGUI`):** Panel de control de la simulación con flujo paso a paso:
- Botones de sesión habilitados secuencialmente (Q1 → Q2 → Q3 → GP).
- Tablas flotantes de clasificación, resultados y campeonato, coloreadas por escudería.
- Gráfica de barras horizontales de desempeño (`PanelDesempeno`), con vista por piloto o por constructor.
- Controles de avance: siguiente carrera, resimular GP, Fast Forward (resto de la temporada).
- Guardado y lectura de archivos JSON.

---

## 📂 Estructura del Código

```
src/
├── Main.java                  — Punto de entrada
├── Formula1.java              — Orquestador del ciclo de vida
├── SimuladorController.java   — Lógica de flujo de simulación
│
├── — Entidades de dominio —
├── Temporada.java             — Gestión de año, carreras y campeonato
├── Escuderia.java             — Equipo con pilotos y vehículos
├── Driver.java                — Piloto con habilidad y factorHabilidad
├── Vehiculo.java              — Monoplaza (motor, llantas, peso)
│
├── — Motor de simulación —
├── Carrera.java               — Clase abstracta base
├── Q1.java                    — Clasificación Q1 (σ = 1.2)
├── Q2.java                    — Clasificación Q2 (σ = 1.0)
├── Q3.java                    — Clasificación Q3 (σ = 0.8)
├── GranPremio.java            — Carrera del domingo
├── Clasification.java         — Juez: filtrado, parrilla y tiempos
│
├── — Interfaz gráfica —
├── RegistroF1GUI.java         — Ventana de configuración de temporada
├── MainGUI.java               — Panel principal de simulación
├── VentanaTabla.java          — Tabla flotante de resultados
├── VentanaDesempeno.java      — Gráfica de desempeño de temporada
├── PanelDesempeno.java        — Barras horizontales con Graphics2D
├── EquipoRowRenderer.java     — Coloreado de filas por escudería
├── ColoresEscuderia.java      — Mapa de colores oficiales F1
│
├── — Servicios —
├── GeneradorReportes.java     — Formateo de tablas y datos
└── ManejadorArchivos.java     — Persistencia JSON (lectura y escritura)
```

---

## 📊 Diagrama de Clases (UML)

🔗 [Visualización interactiva en Canva](https://canva.link/rb0jp1bbkowsngo)

---

## ⚙️ Requisitos de Ejecución

- **Java:** JDK 17 o superior
- **IDE recomendado:** IntelliJ IDEA, Eclipse o NetBeans
- **Dependencias externas:** Ninguna — solo la biblioteca estándar de Java (Swing incluido)

### Compilación y ejecución

```bash
# Compilar todos los archivos
javac src/*.java -d out/

# Ejecutar
java -cp out/ Main
```

Al iniciarse, el programa abre automáticamente la ventana de registro. Una vez configurada la temporada, se lanza la interfaz principal de simulación.

---

## 🏁 Flujo de una Sesión

1. **Configurar temporada** — Registrar vehículos, pilotos, escuderías y circuitos (o cargar desde JSON).
2. **Simular Q1** — Participan los `N` pilotos. Se eliminan `(N-10)/2` por tiempos más lentos.
3. **Simular Q2** — Participan los sobrevivientes. Se eliminan otros `(N-10)/2`.
4. **Simular Q3** — Los 10 mejores compiten por la pole position.
5. **Generar parrilla** — Se combina Q3 + eliminados Q2 + eliminados Q1.
6. **Simular Gran Premio** — Algoritmo probabilístico sobre la parrilla. Se asignan puntos (25-18-15-12-10-8-6-4-2-1).
7. **Consultar tablas** — Clasificación, resultados GP, campeonato y gráfica de desempeño.
8. **Avanzar o resimular** — Siguiente carrera, resimular solo el GP, o Fast Forward al resto del año.
9. **Guardar** — Exportar equipos y progreso a JSON para continuar en otra sesión.

---

## 👥 Equipo de Desarrollo

| Integrante | Rol Principal | Participación |
|:---|:---|:---:|
| **Juan Pablo López Moreno** | Investigador y Gestor de Información | 33.3% |
| **Jose Maria Blancarte López** | Redactor y Analista Principal | 33.3% |
| **Amy Marianee Ramírez Sánchez** | Editora y Control de Calidad | 33.4% |

---

## 📄 Referencias

- Formula 1 — 2026 F1 Regulations: https://www.formula1.com/en/page/2026-f1-regulations
- What is F1? Formula 1 Explained: https://www.formula1.com/en/page/what-is-f1