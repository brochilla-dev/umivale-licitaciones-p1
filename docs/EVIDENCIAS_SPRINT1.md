# Evidencias de Cierre · Sprint 1 (Sesión 11)

**Asignatura:** Proyecto de Informática I (UEV · Curso 2026/2027)  
**Cliente:** UMivale Activa · **Reto 3:** Búsqueda de Licitaciones Similares  
**Equipo 4:** Ignacio, Natanael, Nicolás, Kike, Pouria y Youssef  
**Fecha de cierre de Sprint 1:** 19 de noviembre de 2026  

---

## 1. Sprint Goal y Primer Incremento (P1)

- **Sprint Goal:** Permitir que un usuario busque licitaciones públicas comparables describiendo el servicio en texto libre sobre una muestra local de datos y obtenga un ranking ordenado con justificación comprensible y tratamiento explícito de datos faltantes.
- **Primer Incremento P1:**  
  *«Al terminar Sprint 1, un usuario podrá introducir una descripción libre de un servicio informático y seleccionar el tipo de contrato en una aplicación de escritorio Java OOP, y podremos demostrarlo ejecutando `mvn javafx:run`, realizando búsquedas reales sobre la muestra `mock.csv` (con lotes, sinónimos y campos faltantes) y comprobando la explicación de similitud en la ficha inferior.»*

---

## 2. Sprint Backlog Actualizado (`Done` / `En progreso` / `No iniciado`)

> Regla de `1_S11.pdf`: **Tarea concreta → Responsable → Evidencia observable** (no marcar como `Done` lo que no esté integrado, probado y comprendido por el equipo).

| ID / Tipo | Tarea concreta | Responsable(s) | Estado | Evidencia observable |
| :--- | :--- | :--- | :--- | :--- |
| T1 · Enabler | Inicializar repositorio GitHub, `.gitignore` y `README.md` | Nacho (`brochilla`) | **Done** | Commits `1213df2` y `b672b50` en `main` |
| T2 · Enabler | Configurar proyecto Maven con Java 17, JavaFX 21.0.2 y JUnit 5.10.2 (`pom.xml`) y corregir líneas sobrantes | Nacho, Natanael | **Done** | Commits `13c7fa6` y `3e99d90`; `mvn test` compila sin errores |
| T3 · Implementación | Crear muestra local de licitaciones (`mock.csv`) con servicios TI (CPV 72), lotes independientes, datos faltantes, suministros/obras y caso histórico 2022 | Nicolás, Natanael | **Done** | Commits `82ef1c2` y `b2f70b3` (`src/main/resources/data/mock.csv`, 16 filas de datos) |
| T4 · Implementación | Implementar cargador CSV (`CsvLicitacionLoader`) compatible con formato de 6 y 10 columnas y control de errores | Nicolás, Natanael | **Done** | Commits `82ef1c2` y `b2f70b3`; validado por `CsvLicitacionLoaderTest` (2/2 OK) |
| T5 · Implementación | Implementar modelo de dominio OOP (`Licitacion` y `ResultadoBusqueda`) con lotes y tratamiento de `"Dato no disponible"` | Nacho, Natanael | **Done** | Commits `5a68d97`, `9a31917` y `b2f70b3` |
| T6 · Implementación | Implementar motor de cálculo de similitud (`SimilitudService`) con pesos orientativos configurables, sinónimos TI, filtro por tipo/año y explicación en lenguaje claro | Natanael | **Done** | Commit `b2f70b3`; validado por `SimilitudServiceTest` (6/6 OK) |
| T7 · Implementación | Implementar interfaz de escritorio JavaFX (`MainApp`) con barra de búsqueda, tabla de ranking y panel de justificación/ficha | Nacho, Natanael | **Done** | Commits `9a31917`, `43d2f3b` y `b2f70b3` |
| T8 · Pruebas | Crear y ampliar tests unitarios JUnit 5 (`CsvLicitacionLoaderTest` y `SimilitudServiceTest`, incluyendo límite temporal 2024) | Natanael, Youssef | **Done** | Commits `b2f70b3` y `4503d45`; `mvn test` → 8/8 tests pasando |
| T9 · Prueba / Demo | Verificar ejecución en vivo de la UI JavaFX (`mvn javafx:run`) y comprobar los 3 casos de demo | Kike / Todo el equipo | **En progreso** | Pendiente de validar en el portátil de presentación antes de la Review |
| T10 · Evidencia / Doc | Cerrar evidencias de Sprint 1 (backlog, pruebas, IA, guía de defensa) y actualizar `Ficha_G1` / `Ficha_G2` | Pouria / Kike / Todo el equipo | **En progreso** | Documentos en `docs/`; pendiente de revisión final del equipo y commit desde cuenta de Kike/Pouria |
| T11 · Evolución (P2/S2) | Ingesta real XML de la Plataforma de Contratación y comparativa lado a lado de licitaciones | — | **No iniciado (Sprint 2)** | Queda fuera del alcance comprometido en Sprint 1 |

---

## 3. Repositorio, Commits Relevantes y Evidencia Individual

### Commits verificados en `main` (`git log`)

| Hash | Autor en Git | Fecha | Mensaje | Qué aportó / cambió |
| :--- | :--- | :--- | :--- | :--- |
| `1213df2` | `brochilla` | 2026-11-19 | `Initial commit` | Creación del repositorio y `README.md` inicial |
| `b672b50` | `Nacho` | 2026-11-19 | `feat(setup): configurar estructura Maven con JavaFX y JUnit` | Añadido `.gitignore` |
| `13c7fa6` | `Nacho` | 2026-11-19 | `commit nacho: añadir pom.xml con JavaFX y JUnit` | Definición inicial de `pom.xml` (Java 17, JavaFX 21.0.2, JUnit 5.10.2) |
| `5a68d97` | `Nacho` | 2026-11-19 | `feat(model): implementar clases Licitacion y ResultadoBusqueda` | Creación de archivos base del paquete `model` |
| `82ef1c2` | `Nicolás Salazar` | 2026-11-19 | `feat: añadir mock.csv y CsvLicitacionLoader` | Primer `mock.csv` y clase `CsvLicitacionLoader` |
| `3e99d90` | `Nata` | 2026-11-19 | `minor changes` | Limpieza de líneas `cat`/`EOF` sobrantes en `pom.xml` |
| `9a31917` | `Nacho` | 2026-11-19 | `arreglo el nombre y hago push: renombrar a Licitacion y feat(ui): añadir interfaz JavaFX MainApp` | Renombrado a `Licitacion.java` y primera versión de `MainApp.java` |
| `43d2f3b` | `Nacho` | 2026-11-19 | `fix(ui): corregir package en MainApp` | Corrección de declaración `package` en `MainApp.java` |
| `b2f70b3` | `Nata` | 2026-11-19 | `arreglo de todo` | Implementación completa de `Licitacion`, `ResultadoBusqueda`, `SimilitudService`, ampliación de `mock.csv` (16 filas / 10 columnas) y `CsvLicitacionLoader`, panel de detalle en `MainApp` y 7 tests JUnit |
| `4503d45` | `Youssef lyoussi` | 2026-11-19 | `test: comprobar el límite temporal desde 2024` | Añadido test `horizonteTemporalIncluyeDesde2024YExcluyeAniosAnteriores` en `SimilitudServiceTest` |

### Control de aportación individual (`git shortlog -sn --all`)

- `Nacho` (5 commits) + `brochilla` (1 commit) → Nacho
- `Nata` (2 commits) → Natanael
- `Nicolás Salazar` (1 commit) → Nicolás
- `Youssef lyoussi` (1 commit) → Youssef
- **Aviso importante para cerrar Sprint 1:** Antes de la Review, **Kike y Pouria** deben subir al menos un commit con la revisión final de estas evidencias, actualización de documentación o pruebas de la demo para que conste la participación individual de los 6 integrantes.

---

## 4. Registro de Pruebas

### 4.1. Pruebas automatizadas ejecutadas (`mvn test` → **8/8 OK**, 0 fallos, 0 errores, 0 omitidos)

| Caso | Archivo / Método de prueba | Resultado esperado | Resultado obtenido | Incidencia |
| :--- | :--- | :--- | :--- | :--- |
| P01 · Carga CSV y datos faltantes | `CsvLicitacionLoaderTest.cargarMockCsvDevuelveLicitacionesYTrataDatosFaltantes` | Carga las 16 filas de `mock.csv`; en `EXP-2026-003` marca `"Dato no disponible"` en organismo y duración, y marca `tieneDatosFaltantes() == true` | Pasa (`Tests run: 2, Failures: 0`) | Ninguna |
| P02 · Tratamiento de lotes independientes | `CsvLicitacionLoaderTest.cargarMockCsvDevuelveLicitacionesYTrataDatosFaltantes` | `EXP-2026-001` se carga como 2 unidades separadas (`Lote 1` y `Lote 2`) | Pasa (`Tests run: 2, Failures: 0`) | Ninguna |
| P03 · Error controlado si falta el CSV | `CsvLicitacionLoaderTest.cargarRecursoInexistenteLanzaIllegalArgumentException` | Lanza `IllegalArgumentException` con el mensaje `"No se encontró el archivo CSV..."` al pedir `/data/no_existe.csv` | Pasa (`Tests run: 2, Failures: 0`) | Ninguna |
| P04 · Búsqueda libre y ordenación | `SimilitudServiceTest.buscarSoporteUsuariosEncuentraSimilaresYOrdenaPorPuntuacion` | Para `"soporte a usuarios y puestos de trabajo"` (`Servicios`), devuelve resultados ordenados de mayor a menor similitud, todos de `Servicios` y sin `EXP-2022-099` | Pasa (`Tests run: 6, Failures: 0`) | Ninguna |
| P05 · Búsqueda por sinónimos TI | `SimilitudServiceTest.buscarConSinonimosEncuentraLicitacionesSinPalabraExacta` | `"helpdesk microinformatico"` encuentra `"Atencion a usuarios CAU..."` aunque no coincida literalmente la palabra | Pasa (`Tests run: 6, Failures: 0`) | Ninguna |
| P06 · Filtro obligatorio por tipo de contrato | `SimilitudServiceTest.tipoContratoObligatorioNoMezclaServiciosConSuministros` | `"puestos de trabajo"` en `Servicios` excluye `EXP-2026-011` (`Suministros`); al buscar en `Suministros` sí aparece | Pasa (`Tests run: 6, Failures: 0`) | Ninguna |
| P07 · Desempate a favor de completas | `SimilitudServiceTest.aIgualSimilitudLaLicitacionCompletaVaAntesQueLaIncompleta` | Ante igual objeto y puntuación, `EXP-COMP` (con importe y duración) aparece antes que `EXP-INC` (con campos `null`) | Pasa (`Tests run: 6, Failures: 0`) | Ninguna |
| P08 · Consulta vacía + Límite temporal 2024 | `SimilitudServiceTest.consultaVaciaDevuelveListaVacia` y `horizonteTemporalIncluyeDesde2024YExcluyeAniosAnteriores` | Consulta en blanco devuelve lista vacía; licitación de `2024-01-01` entra y `2023-12-31` se excluye por el horizonte por defecto (`anioMinimo = 2024`) | Pasa (`Tests run: 6, Failures: 0`) | Ninguna |

### 4.2. Pruebas manuales de interfaz de escritorio (`mvn javafx:run`) — *Validar en vivo antes de la Review*

| Caso | Pasos en la app (`MainApp`) | Resultado esperado | Resultado obtenido | Incidencia |
| :--- | :--- | :--- | :--- | :--- |
| P09 · Demo flujo completo | Arrancar `mvn javafx:run`, escribir `"soporte a usuarios y puestos de trabajo"`, tipo `Servicios`, pulsar **Buscar** y seleccionar la 1.ª fila | La tabla muestra resultados con `% Similitud`, `Relevancia` (`Relevante` / `Menos parecida`) y el panel inferior explica en claro por qué coincide y muestra lotes/CPV/importe | *[ ] Pendiente de verificar en portátil de demo* | — |
| P10 · Caso de datos faltantes en UI | Buscar `"mantenimiento equipos informaticos"` y seleccionar `EXP-2026-003` | En la tabla y en la ficha inferior aparece `"Dato no disponible"` en Organismo y Duración + aviso en la explicación | *[ ] Pendiente de verificar en portátil de demo* | — |
| P11 · Búsqueda sin descripción | Dejar el campo vacío y pulsar **Buscar** | La barra inferior muestra aviso y la tabla no falla ni lanza excepción | *[ ] Pendiente de verificar en portátil de demo* | — |

---

## 5. Decisiones Relevantes y Cambios Respecto al Plan Inicial

1. **Corrección de configuración Maven (`pom.xml`):** El primer `pom.xml` subido contenía líneas de shell (`cat << 'EOF' > pom.xml` / `EOF`) que impedían compilar. Se limpió en local y se verificó la resolución de JavaFX 21.0.2 y JUnit 5.10.2 con JDK 21.
2. **Alineación del modelo OOP (`Licitacion` y `ResultadoBusqueda`):** Inicialmente se subió `Licitaciones.java` en plural y ambos archivos del modelo estaban vacíos (`0 bytes`). Se renombró a `Licitacion.java`, se corrigió el `package` en `MainApp` y se implementaron ambas clases respetando el constructor de 6 `String`s usado inicialmente y añadiendo el constructor completo de 10 campos.
3. **Ampliación compatible de `mock.csv` y `CsvLicitacionLoader`:** El CSV inicial tenía 5 licitaciones genéricas (4 de ellas fuera del foco IT: limpieza, botiquines, obras y vigilancia) y 6 columnas sin CPV, lote ni duración. Se amplió a **16 filas de servicios TI** (familia CPV 72, lotes separados `EXP-2026-001` y `EXP-2026-005`, casos incompletos y caso histórico de 2022) manteniendo compatibilidad hacia atrás con CSV de 6 columnas.
4. **Incorporación de respuestas de UMivale del 12/11 (`UMivale_Equipo4_Respuestas_12Nov.docx`):**
   - Horizonte temporal configurable por defecto desde **2024** (`anioMinimo = 2024`, en lugar de fijo en código).
   - Ámbito geográfico sin restricciones (toda España).
   - Pesos de similitud tratados como **punto de partida orientativo y configurable** (`configurarPesos(...)`), no como fórmula cerrada.
   - Prioridad absoluta a **no inventar datos** (el importe no se inventa cuando la consulta libre no incluye importe de referencia; los campos faltantes muestran `"Dato no disponible"` y las completas desempatan por delante).
5. **Documentación pendiente fuera del repositorio:** En la carpeta de asignatura aún figuran `Ficha_G1_Product_Goal_y_Alcance_v0_1.docx` y `Ficha_G2_Sprint1.docx` sin actualizar con el cierre de hoy; este documento y `README.md` sirven como fuente directa para actualizarlas.

---

## 6. Registro de Usos Significativos de IA (2–4 trazas)

> *Nota para el equipo: Revisad y ajustad estos registros antes de la Review para que reflejen con exactitud los prompts reales que usó cada miembro y las decisiones que tomasteis.*

### Uso 1 · Preparación del entorno Maven/JavaFX y diagnóstico de errores iniciales
- **Necesidad:** Configurar VS Code con JDK 21 y Maven 3.9.9 y resolver por qué fallaban los `import` tras hacer `git pull` (`Licitacion` no encontrada y `pom.xml` corrupto).
- **Herramienta y prompt significativo:** Asistente de código en el workspace (*"mira he hecho un pull con el trabajo que hicieron mis compañeros y me da este error... arregla todos los problemas que tenemos en el codigo"*).
- **Propuesta de la IA:** Detectó que `Licitaciones.java` y `ResultadoBusqueda.java` estaban en `0 bytes`, que `pom.xml` tenía texto `cat << 'EOF'` de shell y que faltaba `SimilitudService.java`; propuso la estructura completa de `model`, `service`, `mock.csv` ampliado y tests JUnit.
- **Decisión del equipo (aceptado / rechazado):** Se aceptó la separación OOP en `model` / `service` / `ui` y la compatibilidad doble (6 y 10 columnas) en `CsvLicitacionLoader`; se descartó añadir filtros avanzados de UI o carga XML para no salirse del incremento mínimo de Sprint 1.
- **Cambio manual y por qué:** Se integraron las ediciones en `main`, se validó la compilación y se amplió la suite de pruebas añadiendo un test específico del límite temporal de 2024 (`horizonteTemporalIncluyeDesde2024YExcluyeAniosAnteriores`, commit `4503d45`).
- **Aprendizaje:** Un `commit` con archivos vacíos o sin probar en otro equipo rompe la integración; ejecutar `mvn test` tras cada cambio garantiza que el código compilado y las interfaces entre capas encajan.

### Uso 2 · Diseño del motor de similitud y tratamiento de datos faltantes (`SimilitudService`)
- **Necesidad:** Traducir las reglas de negocio de UMivale (pesos orientativos, sinónimos, lotes independientes, no inventar datos faltantes y explicar el porqué) a una clase Java OOP mantenible sin depender todavía de bases de datos o APIs externas en P1.
- **Herramienta y prompt significativo:** Asistente de programación para generar `SimilitudService` alineado con las respuestas del cliente (12/11) y con `MainApp.java`.
- **Propuesta de la IA:** Tokenización con eliminación de *stop words*, diccionario de sinónimos por bloques IT (`GRUPOS_SINONIMOS`), cálculo ponderado normalizado solo sobre los criterios disponibles y ordenación con desempate por número de campos completos.
- **Decisión del equipo (aceptado / rechazado):** Se aceptó usar pesos configurables en atributos (`pesoObjeto`, `pesoCpv`, `pesoImporte`, `pesoOrganismo`, `pesoAntiguedad`) y no inventar puntuación de importe cuando la búsqueda por texto libre no aporta importe de referencia.
- **Cambio manual y por qué:** *(Completar por el equipo si modificasteis algún umbral, grupo de sinónimos o dato de `mock.csv` tras probarlo en vivo).*
- **Aprendizaje:** Normalizar la suma ponderada por el peso realmente aplicado evita penalizar artificialmente o inventar valores cuando un campo del pliego no está disponible.

### Uso 3 · Generación de casos de prueba unitarios y evidencias de Sprint 1
- **Necesidad:** Verificar automáticamente los criterios de aceptación antes del cierre de Sprint 1 y estructurar las evidencias exigidas en la Sesión 11.
- **Herramienta y prompt significativo:** Asistente de programación (*"sí, hazme las evidencias"* / generación inicial de `CsvLicitacionLoaderTest` y `SimilitudServiceTest`).
- **Propuesta de la IA:** Generó 7 tests unitarios iniciales cubriendo carga CSV, lotes, errores de ruta, sinónimos, separación Servicios/Suministros y desempate de datos completos.
- **Decisión del equipo (aceptado / rechazado):** Aceptada la batería base de tests y ampliada con el caso específico de frontera `2023-12-31` vs `2024-01-01`.
- **Cambio manual y por qué:** Se añadió manualmente el test `horizonteTemporalIncluyeDesde2024YExcluyeAniosAnteriores` en `SimilitudServiceTest.java` (commit `4503d45`) para comprobar explícitamente el cambio confirmado por UMivale el 12/11.
- **Aprendizaje:** Las pruebas automatizadas con casos límite reales del cliente (lotes, campos nulos, cambio de año) dan evidencia objetiva de "Done" más allá de que "la ventana arranque".

---

## 7. Checklist Definition of Done (DoD) · Sprint 1

| Criterio de Done (`1_S11.pdf` y `Ficha_G2_Sprint1.docx`) | Estado | Notas |
| :--- | :---: | :--- |
| **1. Funciona:** Flujo completo de principio a fin (texto libre → cálculo → tabla → ficha detalle) | ✅ / ⏳ | Lógica y tests 8/8 OK; verificar arranque visual JavaFX (`mvn javafx:run`) antes de salir a presentar |
| **2. Cumple:** Criterios de aceptación comprobados con casos concretos | ✅ | 8 pruebas JUnit automatizadas + 3 casos de demo definidos |
| **3. Se entiende:** Código OOP separado (`model`/`service`/`ui`) y explicable sin IA por el equipo | ⏳ | Repasar juntos [Guía de defensa sin IA](GUIA_DEFENSA_CODIGO_SIN_IA.md) durante 10 min |
| Repositorio común integrado y compilando en `main` | ✅ | Todo en `origin/main` hasta commit `4503d45` |
| Evidencia individual de los 6 miembros | ⏳ | Ya constan Nacho, Natanael, Nicolás y Youssef; faltan commits identificables de Kike y Pouria |
| 2–4 usos significativos de IA trazados y cambios manuales justificados | ✅ | Documentado en la sección 6 (revisar prompts exactos) |

---

## 8. Guion Rápido para la Segunda Parte de Hoy (2:00–4:00)

### Sprint Review (10 min)
1. Mostrar `mvn javafx:run` con 2 búsquedas reales:
   - `"soporte a usuarios y puestos de trabajo"` (`Servicios`) → mostrar lotes `Lote 1`/`Lote 2`, `Relevante` vs `Menos parecida` y `"Dato no disponible"` en `EXP-2026-003`.
   - `"helpdesk microinformatico"` → demostrar que encuentra `"Atencion a usuarios CAU..."` por sinónimos y que **no mezcla** suministros (`EXP-2026-011`) ni contratos de 2022 (`EXP-2022-099`).
2. Enseñar resultado de `mvn test` (`8/8` pasando) y las evidencias de este documento.

### Retrospectiva Breve (2:50–3:10) — Puntos reales del Sprint 1
- **Qué funcionó bien:** Estructura OOP limpia por capas (`model`, `service`, `ui`), suite de tests JUnit para validar reglas de negocio de UMivale sin depender solo de pruebas manuales.
- **Qué falló / aprendimos:** Se subieron archivos vacíos (`0 bytes`), `pom.xml` con líneas rotas y desajustes de nombres/columnas entre compañeros porque no se probaba `mvn test` antes de hacer `push`.
- **Qué cambiaremos en Sprint 2:**
  1. Nadie hace `push` a `main` sin ejecutar `mvn test` y revisar `git diff` antes del commit.
  2. Acordar primero los atributos/métodos de las clases y trabajar en ramas pequeñas o tareas bien delimitadas desde el primer día para que los 6 miembros tengan commits continuos.

### Propuesta para Sprint Planning 2 (3:10–4:00)
- **Foco candidato Sprint 2:**
  1. Ingesta real de **XML** de la Plataforma de Contratación del Sector Público (y lectura de los **5 pliegos de referencia** cuando estén cargados).
  2. Incorporar **importe de referencia y duración** en la búsqueda para activar el criterio del ~15 % de importe (orden de magnitud 1/3–3x) y la comparativa (`"importe un X% mayor"`).
  3. **Vista comparativa en tabla** seleccionando 2–4 licitaciones lado a lado, filtro configurable en la UI (año mínimo, tipo de organismo) y resaltado visual de términos coincidentes.
  4. Actualizar `Ficha_G1_Product_Goal_y_Alcance` a v0.2 y validar los pesos orientativos con datos reales.
