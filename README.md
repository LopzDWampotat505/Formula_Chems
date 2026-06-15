# Sistema de Simulación y Gestión de Fórmula 1 (F1-POO)

Este proyecto consiste en el análisis y diseño de un sistema orientado a objetos desarrollado en **Java** para registrar, administrar y simular las temporadas de la **Fórmula 1 (F1)**. El sistema es capaz de almacenar de forma independiente el historial estadístico de cualquier año de competición y reproducir sus resultados de forma clara.

El proyecto fue desarrollado por estudiantes del **Departamento de Computación, Electrónica y Mecatrónica** de la **Universidad de las Américas Puebla (UDLAP)**.

## 🚀 Objetivos del Proyecto

El objetivo principal es diseñar e implementar una herramienta que simplifique la complejidad de los datos generados en la F1 (equipos, pilotos, tiempos y resultados) utilizando el paradigma de **Programación Orientada a Objetos (POO)**.

### Objetivos Específicos
* **Administrar escuderías:** Registrar los datos de cada equipo, sus pilotos asignados y las características técnicas de sus monoplazas.
* **Calendarizar premios:** Especificar los circuitos, nombres, ubicaciones y periodos de cada Gran Premio en la temporada.
* **Registrar clasificaciones:** Almacenar de manera precisa los tiempos de las sesiones de clasificación (Q1, Q2 y Q3) para definir las posiciones de la parrilla de salida.
* **Controlar resultados:** Documentar las posiciones finales alcanzadas por los pilotos en la carrera definitiva de cada premio.
* **Generar reportes:** Producir de forma automatizada las tablas resumen del calendario y los resultados de cada competencia.

---

## 🛠️ Arquitectura del Sistema (Paradigma POO)

El diseño del sistema sigue un flujo de dependencias lógicas riguroso para asegurar la consistencia de los datos y otorgar flexibilidad en el escalado del software:

1. **Estructura Temporal:** La clase principal `F1` rige las `Temporada`s, y cada temporada controla de forma independiente tanto su calendario de `Carrera`s como su lista de `Escuderia`s.
2. **Recursos Operativos:** Las escuderías agrupan de forma separada pero vinculada a los `Drivers` (pilotos) y sus `Vehiculo`s. Esto permite modificar los atributos de un auto o conductor sin alterar la estructura del equipo.
3. **Flujo de Competencia:** La clase `Carrera` gestiona de manera independiente las tres etapas de eliminación lógica (`Q1`, `Q2`, `Q3`) por medio de una clase mediadora `Clasificacion`, la cual filtra los tiempos de menor a mayor para transferir la parrilla de salida definitiva al `Gran Premio`.

### Atributos Clave para Futuras Simulaciones
El modelo no es solo una base de datos estática; está estructurado para que en fases posteriores se puedan implementar algoritmos matemáticos avanzados utilizando:
* **Pilotos:** Nivel de habilidad (`habilidades`).
* **Vehículos:** Tipo de motor (`motor`), peso del monoplaza (`peso`) y estado/tipo de neumáticos (`llantas`).

---

## 📊 Diagrama de Clases (UML)

Para una mejor comprensión de las asociaciones, agregaciones y herencias del proyecto, puedes consultar los diagramas interactivos en el siguiente enlace:
🔗 [Visualización del Diagrama UML en Canva](https://canva.link/0gsla7v19ifsfgk)

---

## 💻 Requisitos de Ejecución

* **Lenguaje:** Java (JDK 17 o superior recomendado)
* **Paradigma:** Programación Orientada a Objetos (POO)
* **IDE recomendado:** IntelliJ IDEA, Eclipse o NetBeans

---

## 📂 Estructura del Código

[cite_start]El código fuente está organizado de la siguiente manera dentro del directorio `src`, separando la lógica principal de las sesiones de clasificación y los componentes técnicos[cite: 6, 70, 157]:

```text
Formula Chems
└── src
    ├── Carrera.java
    ├── Clasification.java
    ├── Drivers.java
    ├── Escuderia.java
    ├── Formula1.java
    ├── Gran_Premio.java
    ├── Main.java
    ├── Q1.java
    ├── Q2.java
    ├── Q3.java
    ├── Temporada.java
    └── Vehiculo.java

---
## 👥 Equipo de Desarrollo

| Integrante | Rol Principal | Participación |
| :--- | :--- | :---: |
| **Juan Pablo López Moreno** | Gestor de Información / Investigador | 33.3% |
| **Jose Maria Blancarte López** | Redactor y Analista Principal | 33.3% |
| **Amy Marianee Ramírez Sánchez** | Editora y Control de Calidad | 33.4% |

---

## 📄 Licencia y Contexto
Este proyecto fue realizado con fines académicos para la materia de **Programación Orientada a Objetos** con fecha de entrega del **11 de junio de 2026**.
