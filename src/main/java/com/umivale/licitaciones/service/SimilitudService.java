package com.umivale.licitaciones.service;

import com.umivale.licitaciones.model.Licitacion;
import com.umivale.licitaciones.model.ResultadoBusqueda;

import java.text.Normalizer;
import java.util.*;

/**
 * Motor de cálculo de similitud entre una descripción libre de necesidad y una lista de licitaciones.
 *
 * Implementa las reglas acordadas con UMivale:
 * - El tipo de contrato es un filtro obligatorio (si no se elige "Todos").
 * - Horizonte temporal configurable (por defecto desde enero de 2024).
 * - Pesos orientativos configurables (objeto ~50%, CPV ~15%, organismo ~10%, antigüedad/duración ~10%, importe ~15%).
 * - Nunca se inventan datos faltantes: solo se puntúa con lo disponible y, a igual similitud,
 *   las licitaciones con datos completos aparecen antes que las incompletas.
 * - Distingue resultados relevantes de resultados "menos parecidos".
 */
public class SimilitudService {

    // Pesos orientativos configurables (punto de partida de UMivale a contrastar con datos)
    private double pesoObjeto = 0.50;
    private double pesoCpv = 0.15;
    private double pesoOrganismo = 0.10;
    private double pesoAntiguedad = 0.10;
    private double pesoImporte = 0.15;

    private int anioMinimo = 2024;
    private double umbralRelevancia = 35.0;

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "al", "con", "de", "del", "el", "en", "es", "la", "las", "lo", "los",
            "o", "para", "por", "que", "se", "su", "un", "una", "unas", "unos", "y",
            "contrato", "contratacion", "licitacion", "servicio", "servicios"
    );

    /**
     * Grupos de sinónimos del dominio tecnológico para no depender solo de palabras clave exactas.
     */
    private static final List<Set<String>> GRUPOS_SINONIMOS = List.of(
            Set.of("soporte", "atencion", "usuarios", "usuario", "microinformatico", "microinformatica", "puesto", "puestos", "trabajo", "helpdesk", "cau", "incidencias"),
            Set.of("desarrollo", "software", "aplicaciones", "aplicacion", "programacion", "mantenimiento", "evolutivo", "web", "java", "sistemas"),
            Set.of("seguridad", "ciberseguridad", "auditoria", "ens", "iso", "proteccion", "consultoria", "normativo", "cumplimiento", "reglamento"),
            Set.of("ia", "inteligencia", "artificial", "asesoramiento", "gobernanza", "datos"),
            Set.of("nube", "cloud", "alojamiento", "hosting", "infraestructura", "servidores", "cpd", "backup"),
            Set.of("redes", "red", "comunicaciones", "equipos", "informaticos", "hardware")
    );

    public int getAnioMinimo() {
        return anioMinimo;
    }

    public void setAnioMinimo(int anioMinimo) {
        this.anioMinimo = anioMinimo;
    }

    public double getUmbralRelevancia() {
        return umbralRelevancia;
    }

    public void setUmbralRelevancia(double umbralRelevancia) {
        this.umbralRelevancia = umbralRelevancia;
    }

    public void configurarPesos(double pesoObjeto, double pesoCpv, double pesoImporte, double pesoOrganismo, double pesoAntiguedad) {
        this.pesoObjeto = pesoObjeto;
        this.pesoCpv = pesoCpv;
        this.pesoImporte = pesoImporte;
        this.pesoOrganismo = pesoOrganismo;
        this.pesoAntiguedad = pesoAntiguedad;
    }

    /**
     * Busca y ordena licitaciones comparables para una consulta de texto libre.
     */
    public List<ResultadoBusqueda> buscarSimilares(String consulta, String tipoContrato, List<Licitacion> licitaciones) {
        if (consulta == null || consulta.isBlank() || licitaciones == null || licitaciones.isEmpty()) {
            return List.of();
        }

        Set<String> tokensConsulta = tokenizar(consulta);
        if (tokensConsulta.isEmpty()) {
            return List.of();
        }

        List<ResultadoBusqueda> resultados = new ArrayList<>();

        for (Licitacion lic : licitaciones) {
            // 1. Filtro obligatorio por tipo de contrato (si no es "Todos")
            if (tipoContrato != null && !tipoContrato.equalsIgnoreCase("Todos")) {
                if (!lic.getTipoContrato().equalsIgnoreCase(tipoContrato)) {
                    continue;
                }
            }

            // 2. Filtro por horizonte temporal configurable (por defecto desde enero 2024)
            Integer anio = lic.getAnioPublicacion();
            if (anio != null && anio < anioMinimo) {
                continue;
            }

            // Sin objeto válido no se puede valorar similitud
            if (Licitacion.esDatoNoDisponible(lic.getObjeto())) {
                continue;
            }

            ResultadoBusqueda resultado = evaluarLicitacion(tokensConsulta, lic);
            if (resultado.getPuntuacionSimilitud() > 0.0) {
                resultados.add(resultado);
            }
        }

        // Ordenación: primero mayor similitud; a empate, primero la licitación con más datos completos
        resultados.sort(Comparator
                .comparingDouble(ResultadoBusqueda::getPuntuacionSimilitud).reversed()
                .thenComparing(r -> r.getLicitacion().contarCamposCompletos(), Comparator.reverseOrder())
                .thenComparing(ResultadoBusqueda::getObjeto));

        return resultados;
    }

    private ResultadoBusqueda evaluarLicitacion(Set<String> tokensConsulta, Licitacion lic) {
        Set<String> tokensObjeto = tokenizar(lic.getObjeto());

        // Criterio 1: Objeto y alcance (coincidencia exacta + sinónimos del dominio)
        List<String> coincidenciasExactas = new ArrayList<>();
        List<String> coincidenciasSemanticas = new ArrayList<>();

        for (String tokenQ : tokensConsulta) {
            if (tokensObjeto.contains(tokenQ)) {
                coincidenciasExactas.add(tokenQ);
            } else if (coincidePorSinonimo(tokenQ, tokensObjeto)) {
                coincidenciasSemanticas.add(tokenQ);
            }
        }

        double aciertosObjeto = coincidenciasExactas.size() + 0.75 * coincidenciasSemanticas.size();
        double scoreObjeto = Math.min(1.0, aciertosObjeto / tokensConsulta.size());

        // Si no hay ninguna relación con el objeto ni por palabras ni por sinónimos, descartamos ruido
        if (scoreObjeto == 0.0) {
            return new ResultadoBusqueda(lic, 0.0, false, "Sin coincidencia en el objeto del contrato.");
        }

        double sumaPonderada = scoreObjeto * pesoObjeto;
        double pesoAplicado = pesoObjeto;

        // Criterio 2: CPV (orientativo; familia 72 corresponde a servicios TI priorizados por UMivale)
        double scoreCpv = 0.0;
        boolean tieneCpv = !Licitacion.esDatoNoDisponible(lic.getCpv());
        if (tieneCpv) {
            scoreCpv = lic.getCpv().startsWith("72") ? 1.0 : 0.35;
            sumaPonderada += scoreCpv * pesoCpv;
            pesoAplicado += pesoCpv;
        }

        // Criterio 3: Tipo de organismo (prioridad a mutuas, sanitario y Seguridad Social, sin excluir otros)
        double scoreOrganismo = 0.0;
        boolean tieneOrganismo = !Licitacion.esDatoNoDisponible(lic.getTipoOrganismo())
                || !Licitacion.esDatoNoDisponible(lic.getOrganismo());
        if (tieneOrganismo) {
            scoreOrganismo = evaluarAfinidadOrganismo(lic);
            sumaPonderada += scoreOrganismo * pesoOrganismo;
            pesoAplicado += pesoOrganismo;
        }

        // Criterio 4: Antigüedad / vigencia (contratos más recientes puntúan mejor)
        Integer anio = lic.getAnioPublicacion();
        if (anio != null) {
            double scoreAnio = (anio >= 2026) ? 1.0 : (anio == 2025 ? 0.85 : 0.70);
            sumaPonderada += scoreAnio * pesoAntiguedad;
            pesoAplicado += pesoAntiguedad;
        }

        // Nota: el criterio de importe (pesoImporte) no se inventa cuando la consulta libre no incluye importe de referencia.
        // Solo se normaliza sobre los criterios efectivamente evaluables.
        double puntuacionFinal = (pesoAplicado > 0) ? (sumaPonderada / pesoAplicado) * 100.0 : 0.0;
        puntuacionFinal = Math.round(puntuacionFinal * 10.0) / 10.0;

        boolean esRelevante = puntuacionFinal >= umbralRelevancia && scoreObjeto >= 0.25;
        String explicacion = construirExplicacion(
                lic, scoreObjeto, coincidenciasExactas, coincidenciasSemanticas,
                tieneCpv, scoreCpv, scoreOrganismo, esRelevante
        );

        return new ResultadoBusqueda(lic, puntuacionFinal, esRelevante, explicacion);
    }

    private double evaluarAfinidadOrganismo(Licitacion lic) {
        String combinado = normalizar(lic.getTipoOrganismo() + " " + lic.getOrganismo());
        if (combinado.contains("mutua") || combinado.contains("umivale") || combinado.contains("seguridad social")) {
            return 1.0;
        }
        if (combinado.contains("sanitari") || combinado.contains("salud") || combinado.contains("hospital")) {
            return 0.9;
        }
        if (combinado.contains("administracion") || combinado.contains("ministerio") || combinado.contains("generalitat") || combinado.contains("junta")) {
            return 0.65;
        }
        return 0.45;
    }

    private String construirExplicacion(
            Licitacion lic,
            double scoreObjeto,
            List<String> exactas,
            List<String> semanticas,
            boolean tieneCpv,
            double scoreCpv,
            double scoreOrganismo,
            boolean esRelevante) {

        StringBuilder sb = new StringBuilder();
        sb.append(esRelevante ? "[Relevante] " : "[Menos parecida] ");

        if (!exactas.isEmpty()) {
            sb.append("Coincidencia directa en objeto: ").append(String.join(", ", exactas)).append(". ");
        }
        if (!semanticas.isEmpty()) {
            sb.append("Coincidencia por sinónimos/contexto: ").append(String.join(", ", semanticas)).append(". ");
        }
        sb.append(String.format(Locale.forLanguageTag("es-ES"), "Afinidad de objeto: %.0f%%. ", scoreObjeto * 100));

        if (!"Único".equalsIgnoreCase(lic.getLote())) {
            sb.append("Unidad comparable: ").append(lic.getLote()).append(" del expediente ").append(lic.getExpediente()).append(". ");
        }

        if (tieneCpv) {
            sb.append("CPV ").append(lic.getCpv());
            if (scoreCpv >= 1.0) {
                sb.append(" (servicios TI prioritarios)");
            }
            sb.append(". ");
        } else {
            sb.append("CPV no disponible. ");
        }

        if (scoreOrganismo >= 0.9) {
            sb.append("Organismo altamente comparable (").append(lic.getOrganismo()).append("). ");
        } else if (!Licitacion.esDatoNoDisponible(lic.getOrganismo())) {
            sb.append("Organismo: ").append(lic.getOrganismo()).append(". ");
        }

        if (lic.tieneDatosFaltantes()) {
            sb.append("Aviso: contiene algún dato no disponible (valorada solo con información confirmada).");
        }

        return sb.toString().trim();
    }

    private boolean coincidePorSinonimo(String tokenConsulta, Set<String> tokensObjeto) {
        for (Set<String> grupo : GRUPOS_SINONIMOS) {
            if (grupo.contains(tokenConsulta)) {
                for (String tokenObj : tokensObjeto) {
                    if (grupo.contains(tokenObj)) {
                        return true;
                    }
                }
            }
        }
        // Coincidencia por raíz común (>= 5 caracteres) para plurales o derivaciones simples
        if (tokenConsulta.length() >= 5) {
            String raiz = tokenConsulta.substring(0, 5);
            for (String tokenObj : tokensObjeto) {
                if (tokenObj.length() >= 5 && tokenObj.startsWith(raiz)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Set<String> tokenizar(String texto) {
        String limpio = normalizar(texto);
        Set<String> tokens = new LinkedHashSet<>();
        for (String palabra : limpio.split("\\s+")) {
            if (palabra.length() >= 2 && !STOP_WORDS.contains(palabra)) {
                tokens.add(palabra);
            }
        }
        return tokens;
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinTildes = Normalizer.normalize(texto.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return sinTildes.replaceAll("[^a-z0-9\\s]", " ").trim();
    }
}
