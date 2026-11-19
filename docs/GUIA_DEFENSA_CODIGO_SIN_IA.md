# Guía de Defensa del Código en Lenguaje Natural (SIN IA)

Este documento resume qué hace cada clase del proyecto, qué entra, qué sale, qué regla del reto de UMivale implementa y cómo realizar cambios pequeños en vivo si el profesor lo pide durante la microdefensa de **Sprint 1**.

---

## Flujo completo de la aplicación (de principio a fin)

1. Al arrancar `MainApp.start(...)`, se crea un `CsvLicitacionLoader` que lee `/data/mock.csv` y lo transforma en una lista de objetos `Licitacion`.
2. El usuario escribe una descripción libre en el campo de texto, elige el tipo de contrato (`Servicios`, `Suministros`, `Obras` o `Todos`) y pulsa **Buscar**.
3. `MainApp.ejecutarBusqueda(...)` delega el trabajo a `SimilitudService.buscarSimilares(consulta, tipoContrato, baseLicitaciones)`.
4. `SimilitudService` filtra por tipo de contrato y año mínimo (`2024`), calcula una puntuación ponderada para cada licitación usando sus datos reales disponibles y devuelve una lista ordenada de `ResultadoBusqueda`.
5. `MainApp` muestra esa lista en la `TableView` y, cuando el usuario selecciona una fila, `mostrarDetalle(...)` rellena el cuadro inferior con la ficha resumida y la explicación en lenguaje claro.

---

## Explicación clase por clase

### 1. `Licitacion` (`com.umivale.licitaciones.model`)
- **¿Qué responsabilidad tiene?**  
  Representar una licitación pública o un lote independiente de una licitación como un objeto del dominio, normalizando sus datos y protegiendo los campos que faltan.
- **¿Qué recibe y qué produce?**  
  - Recibe en sus constructores los campos de la licitación (en versión completa de 10 campos: expediente, objeto, lote, tipo de contrato, CPV, organismo, tipo de organismo, importe, duración en meses y fecha de publicación; o versión simplificada de 6 `String`s).  
  - Produce getters listos para consultar y mostrar en interfaz (`getImporteFormateado()`, `getDuracionFormateada()`, `getAnioPublicacion()`, `contarCamposCompletos()`, `tieneDatosFaltantes()`).
- **¿Qué regla del reto implementa?**  
  1. **Lotes independientes:** Cada lote (`Lote 1`, `Lote 2`, o `"Único"`) se modela como una unidad comparable propia enlazada a su `expediente`.  
  2. **No inventar datos faltantes:** Si un texto viene vacío o nulo, se guarda como `"Dato no disponible"`; si `importe` o `duracionMeses` no son válidos o son `<= 0`, se guardan como `null` y su getter formateado devuelve `"Dato no disponible"`.  
  3. **Base para el desempate:** `contarCamposCompletos()` cuenta cuántos de los 5 campos opcionales clave tienen dato real.
- **¿Con qué otras clases se relaciona?**  
  Es creada por `CsvLicitacionLoader`, evaluada por `SimilitudService` y envuelta dentro de `ResultadoBusqueda`.
- **¿Qué error o caso límite contempla?**  
  Importes escritos con símbolo `€`, espacios o coma decimal (`parsearDouble`), fechas no válidas al extraer el año (`getAnioPublicacion()` devuelve `null` en lugar de romper) y strings vacíos/nulos.

---

### 2. `ResultadoBusqueda` (`com.umivale.licitaciones.model`)
- **¿Qué responsabilidad tiene?**  
  Unir una `Licitacion` con el resultado de evaluarla frente a una búsqueda concreta: su porcentaje de similitud, si supera el corte de relevancia y la explicación en texto claro.
- **¿Qué recibe y qué produce?**  
  - Recibe una `Licitacion`, una `puntuacionSimilitud` (acotada entre `0.0` y `100.0`), el booleano `relevante` y el texto `explicacion`.  
  - Expone estos valores más getters delegados (`getObjeto()`, `getLote()`, `getImporteFormateado()`, `getEtiquetaRelevancia()`, `getSimilitudFormateada()`) para que la tabla JavaFX (`PropertyValueFactory`) los lea directamente sin meter lógica en la vista.
- **¿Qué regla del reto implementa?**  
  - Separar visualmente resultados relevantes de `"Menos parecida"` (`getEtiquetaRelevancia()`).  
  - Acompañar cada resultado con una justificación legible para que el usuario confíe sin tener que abrir el pliego.
- **¿Con qué otras clases se relaciona?**  
  Contiene una `Licitacion`, la crea `SimilitudService` y la muestra `MainApp` en la tabla y en el panel de detalle.
- **¿Qué error o caso límite contempla?**  
  Acota la puntuación al rango `[0, 100]` (`Math.max(0.0, Math.min(100.0, ...))`) y asigna una explicación por defecto si llega vacía.

---

### 3. `CsvLicitacionLoader` (`com.umivale.licitaciones.service`)
- **¿Qué responsabilidad tiene?**  
  Leer un fichero CSV empaquetado en `src/main/resources` (actualmente `/data/mock.csv`) y convertir cada línea en un objeto `Licitacion`.
- **¿Qué recibe y qué produce?**  
  - Recibe la ruta del recurso (`String rutaRecurso`) en `cargarDesdeRecurso(...)`.  
  - Produce una `List<Licitacion>`.
- **¿Qué regla del reto implementa?**  
  Carga datos locales para el incremento P1 manteniendo compatibilidad tanto con CSVs sencillos (6 columnas) como con CSVs completos del reto (10 columnas con lote, CPV, tipo de organismo y duración), convirtiendo celdas vacías en `"Dato no disponible"`.
- **¿Con qué otras clases se relaciona?**  
  Instancia objetos `Licitacion` y es llamada al arranque por `MainApp.cargarDatosIniciales()` (y por los tests).
- **¿Qué error o caso límite contempla?**  
  - Si el archivo no existe en recursos (`inputStream == null`), lanza `IllegalArgumentException` con un mensaje claro.  
  - Salta la línea de cabecera, ignora líneas en blanco, usa `split(";", -1)` para no perder columnas vacías al final de la línea y lee en `UTF-8`.

---

### 4. `SimilitudService` (`com.umivale.licitaciones.service`)
- **¿Qué responsabilidad tiene?**  
  Contener toda la lógica de filtrado, cálculo de similitud, ordenación y generación de explicaciones.
- **¿Qué recibe y qué produce?**  
  - `buscarSimilares(String consulta, String tipoContrato, List<Licitacion> licitaciones)` recibe el texto libre del usuario, el filtro de tipo de contrato y la lista completa de licitaciones.  
  - Devuelve `List<ResultadoBusqueda>` ordenada de mayor a menor similitud.
- **¿Qué reglas del reto implementa?**  
  1. **Tipo de contrato obligatorio:** Si no se elige `"Todos"`, descarta contratos de otro tipo (no mezcla `Servicios` con `Suministros` u `Obras`).  
  2. **Horizonte temporal configurable:** Por defecto `anioMinimo = 2024` (3 años desde enero 2024), modificable con `setAnioMinimo(...)` sin hardcodear una condición fija inmutable.  
  3. **Pesos orientativos configurables:** `pesoObjeto = 0.50`, `pesoCpv = 0.15`, `pesoImporte = 0.15`, `pesoOrganismo = 0.10`, `pesoAntiguedad = 0.10` (ajustables con `configurarPesos(...)`).  
  4. **Búsqueda más allá de la palabra exacta:** Normaliza texto (minúsculas, sin tildes), elimina *stop words*, comprueba grupos de sinónimos IT (`GRUPOS_SINONIMOS`, p. ej. *helpdesk* ↔ *atención a usuarios*) y raíces comunes de `>= 5` letras.  
  5. **No inventar datos faltantes:** Solo suma y divide por `pesoAplicado` de los criterios que sí tienen dato (en consulta libre sin importe de referencia, no inventa `pesoImporte`). A igual puntuación, `contarCamposCompletos()` ordena antes las licitaciones completas.  
  6. **Separación por relevancia y explicación clara:** Marca como `relevante` si supera `umbralRelevancia` (`35.0`) y al menos `0.25` en objeto; redacta en `construirExplicacion(...)` por qué coincide y avisa si faltan datos.
- **¿Con qué otras clases se relaciona?**  
  Recibe `Licitacion`, devuelve `ResultadoBusqueda` y es invocada por `MainApp`.
- **¿Qué error o caso límite contempla?**  
  Consulta nula, vacía o formada solo por *stop words* devuelve lista vacía sin fallar; si la licitación no tiene objeto (`"Dato no disponible"`), se descarta de la valoración; si no hay coincidencia alguna en el objeto (`scoreObjeto == 0.0`), se descarta para evitar ruido.

---

### 5. `MainApp` (`com.umivale.licitaciones.ui`)
- **¿Qué responsabilidad tiene?**  
  Proporcionar la ventana de escritorio JavaFX con la que interactúa el usuario (entrada de datos, tabla de resultados y panel de detalle), **sin contener lógica de negocio**.
- **¿Qué recibe y qué produce?**  
  Recibe las acciones del usuario (texto de búsqueda, selector de tipo de contrato, clic en botón **Buscar**, selección de una fila en la tabla) y actualiza los controles visuales (`TableView`, `TextArea` de detalle y `Label` de estado).
- **¿Qué regla del reto implementa?**  
  - Entrada prioritaria por descripción libre en una interfaz sencilla para usuarios no técnicos.  
  - Mostrar al usuario resumen de relevancia (cuántas relevantes vs. menos parecidas), tabla ordenada y ficha detallada con explicación clara sin jerga del algoritmo.
- **¿Con qué otras clases se relaciona?**  
  Usa `CsvLicitacionLoader` para cargar la muestra al iniciar, `SimilitudService` para buscar y `ResultadoBusqueda` para poblar las columnas de la tabla.
- **¿Qué error o caso límite contempla?**  
  Si el usuario pulsa **Buscar** con el texto vacío, muestra un aviso en `lblEstado` y limpia la tabla en vez de lanzar una búsqueda vacía; si falla la carga de `mock.csv`, captura la excepción y arranca con lista vacía mostrando el error en consola.

---

## Cambios pequeños en vivo que puede pedir el profesor (y dónde tocar)

Si durante la microdefensa el profesor pide modificar una regla rápida, todos los parámetros clave están en `src/main/java/com/umivale/licitaciones/service/SimilitudService.java`:

1. **Cambiar el horizonte de años** (p. ej., incluir desde 2022 o solo desde 2025):  
   Editar `private int anioMinimo = 2024;` (línea 29) o llamar a `similitudService.setAnioMinimo(2025);` desde `MainApp`.
2. **Cambiar el corte entre «Relevante» y «Menos parecida»:**  
   Editar `private double umbralRelevancia = 35.0;` (línea 30).
3. **Ajustar los pesos orientativos:**  
   Editar los atributos `pesoObjeto`, `pesoCpv`, `pesoOrganismo`, `pesoAntiguedad`, `pesoImporte` (líneas 23–27) o llamar a `similitudService.configurarPesos(...)`.
4. **Añadir un sinónimo nuevo del dominio** (p. ej. añadir `"portal"` o `"ticketing"`):  
   Añadir la palabra (en minúsculas y sin tildes) dentro de uno de los `Set.of(...)` de `GRUPOS_SINONIMOS` (líneas 41–48).
5. **Volver a comprobar tras el cambio:**
   - Tests: `mvn test`
   - Arrancar ventana: `mvn javafx:run`
