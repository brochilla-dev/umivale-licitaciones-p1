# UMivale · Buscador de Licitaciones Comparables (`umivale-licitaciones-p1`)

Aplicación de escritorio OOP en **Java + JavaFX** desarrollada por el **Equipo 4** (Ignacio, Natanael, Nicolás, Kike y Pouria) para el **Reto 3 de UMivale Activa** en la asignatura *Proyecto de Informática I* (Universidad Europea de Valencia, 2026/2027).

## Qué resuelve (Incremento de Sprint 1)

Permite introducir una descripción libre de un servicio informático y obtener un ranking ordenado de licitaciones comparables sobre una muestra local (`mock.csv`), aplicando las reglas acordadas con UMivale:

- **Filtro obligatorio por tipo de contrato** (`Servicios`, `Suministros`, `Obras` o `Todos`).
- **Horizonte temporal configurable** (por defecto desde `2024`, últimos 3 años) sin limitación geográfica.
- **Lotes independientes:** cada lote se evalúa y muestra enlazado a su expediente.
- **Cálculo de similitud con pesos orientativos configurables** y diccionario de sinónimos del dominio TI (evitando depender solo de palabras clave exactas).
- **Transparencia ante datos faltantes:** los campos ausentes muestran `"Dato no disponible"` sin inventar valores y, a igual parecido, las licitaciones completas aparecen antes.
- **Explicación en lenguaje claro:** separa resultados `Relevante` y `Menos parecida` y detalla en la ficha inferior por qué coincide cada licitación.

## Estructura OOP del proyecto

```text
src/
├── main/
│   ├── java/com/umivale/licitaciones/
│   │   ├── model/
│   │   │   ├── Licitacion.java            # Entidad de dominio (lote, normalización, datos faltantes)
│   │   │   └── ResultadoBusqueda.java     # Resultado con % similitud, relevancia y explicación
│   │   ├── service/
│   │   │   ├── CsvLicitacionLoader.java   # Carga local CSV (compatible con 6 y 10 columnas)
│   │   │   └── SimilitudService.java      # Motor de filtrado, puntuación, desempate y explicación
│   │   └── ui/
│   │       └── MainApp.java               # Ventana de escritorio JavaFX (sin lógica de negocio)
│   └── resources/data/
│       └── mock.csv                       # Muestra local de 16 licitaciones TI y casos límite
└── test/java/com/umivale/licitaciones/service/
    ├── CsvLicitacionLoaderTest.java       # Pruebas JUnit 5 de carga CSV, lotes y errores
    └── SimilitudServiceTest.java          # Pruebas JUnit 5 de similitud, sinónimos, filtros y fechas
```

## Requisitos y ejecución

- **JDK 21** (compilación configurada para Java 17) y **Apache Maven 3.9+**.
- Ejecutar pruebas unitarias (JUnit 5):
  ```bash
  mvn test
  ```
- Arrancar la aplicación de escritorio JavaFX:
  ```bash
  mvn javafx:run
  ```

## Documentación y evidencias de Sprint 1

- [Evidencias de cierre de Sprint 1 (Sesión 11)](docs/EVIDENCIAS_SPRINT1.md): Sprint Goal, Sprint Backlog actualizado, tabla de commits, tabla de pruebas, decisiones, trazas de uso de IA, DoD y guía para Review / Retrospectiva / Sprint 2.
- [Guía de defensa del código en lenguaje natural (sin IA)](docs/GUIA_DEFENSA_CODIGO_SIN_IA.md): explicación de cada clase, reglas implementadas y guía de cambios rápidos en vivo.
