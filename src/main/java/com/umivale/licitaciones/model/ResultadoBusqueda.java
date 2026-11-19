package com.umivale.licitaciones.model;

import java.util.Locale;

/**
 * Representa un resultado individual devuelto por el buscador de similitud,
 * vinculando la licitación con su puntuación y una explicación comprensible.
 */
public class ResultadoBusqueda {

    private final Licitacion licitacion;
    private final double puntuacionSimilitud;
    private final boolean relevante;
    private final String explicacion;

    public ResultadoBusqueda(
            Licitacion licitacion,
            double puntuacionSimilitud,
            boolean relevante,
            String explicacion) {
        this.licitacion = licitacion;
        this.puntuacionSimilitud = Math.max(0.0, Math.min(100.0, puntuacionSimilitud));
        this.relevante = relevante;
        this.explicacion = (explicacion == null || explicacion.isBlank())
                ? "Coincidencia calculada sobre los datos disponibles."
                : explicacion;
    }

    public Licitacion getLicitacion() {
        return licitacion;
    }

    public double getPuntuacionSimilitud() {
        return puntuacionSimilitud;
    }

    public boolean isRelevante() {
        return relevante;
    }

    public String getEtiquetaRelevancia() {
        return relevante ? "Relevante" : "Menos parecida";
    }

    public String getExplicacion() {
        return explicacion;
    }

    // Getters expuestos para PropertyValueFactory en la tabla de JavaFX (MainApp)

    public String getSimilitudFormateada() {
        return String.format(Locale.forLanguageTag("es-ES"), "%.1f %%", puntuacionSimilitud);
    }

    public String getExpediente() {
        return licitacion.getExpediente();
    }

    public String getObjeto() {
        return licitacion.getObjeto();
    }

    public String getLote() {
        return licitacion.getLote();
    }

    public String getTipoContrato() {
        return licitacion.getTipoContrato();
    }

    public String getCpv() {
        return licitacion.getCpv();
    }

    public String getOrganismo() {
        return licitacion.getOrganismo();
    }

    public String getTipoOrganismo() {
        return licitacion.getTipoOrganismo();
    }

    public String getImporteFormateado() {
        return licitacion.getImporteFormateado();
    }

    public String getDuracionFormateada() {
        return licitacion.getDuracionFormateada();
    }

    public String getFechaPublicacion() {
        return licitacion.getFechaPublicacion();
    }
}
