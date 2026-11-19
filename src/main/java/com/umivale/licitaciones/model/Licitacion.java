package com.umivale.licitaciones.model;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Representa una licitación pública (o un lote independiente de una licitación)
 * procedente de la Plataforma de Contratación del Sector Público.
 */
public class Licitacion {

    public static final String DATO_NO_DISPONIBLE = "Dato no disponible";

    private final String expediente;
    private final String objeto;
    private final String lote;
    private final String tipoContrato;
    private final String cpv;
    private final String organismo;
    private final String tipoOrganismo;
    private final Double importe;
    private final Integer duracionMeses;
    private final String fechaPublicacion;

    /**
     * Constructor completo con todos los atributos relevantes para la comparación.
     */
    public Licitacion(
            String expediente,
            String objeto,
            String lote,
            String tipoContrato,
            String cpv,
            String organismo,
            String tipoOrganismo,
            Double importe,
            Integer duracionMeses,
            String fechaPublicacion) {
        this.expediente = normalizarTexto(expediente);
        this.objeto = normalizarTexto(objeto);
        this.lote = normalizarLote(lote);
        this.tipoContrato = normalizarTexto(tipoContrato);
        this.cpv = normalizarTexto(cpv);
        this.organismo = normalizarTexto(organismo);
        this.tipoOrganismo = normalizarTexto(tipoOrganismo);
        this.importe = (importe != null && importe > 0) ? importe : null;
        this.duracionMeses = (duracionMeses != null && duracionMeses > 0) ? duracionMeses : null;
        this.fechaPublicacion = normalizarTexto(fechaPublicacion);
    }

    /**
     * Constructor simplificado de 6 campos (compatible con formato CSV básico).
     */
    public Licitacion(
            String expediente,
            String titulo,
            String tipoContrato,
            String organismo,
            String presupuesto,
            String fechaLimite) {
        this(
                expediente,
                titulo,
                "Único",
                tipoContrato,
                DATO_NO_DISPONIBLE,
                organismo,
                DATO_NO_DISPONIBLE,
                parsearDouble(presupuesto),
                null,
                fechaLimite
        );
    }

    private static String normalizarTexto(String valor) {
        if (valor == null || valor.trim().isEmpty()
                || valor.trim().equalsIgnoreCase("No disponible")
                || valor.trim().equalsIgnoreCase(DATO_NO_DISPONIBLE)) {
            return DATO_NO_DISPONIBLE;
        }
        return valor.trim();
    }

    private static String normalizarLote(String valor) {
        if (valor == null || valor.trim().isEmpty()
                || valor.trim().equalsIgnoreCase("No disponible")
                || valor.trim().equalsIgnoreCase(DATO_NO_DISPONIBLE)) {
            return "Único";
        }
        return valor.trim();
    }

    public static Double parsearDouble(String texto) {
        if (texto == null) {
            return null;
        }
        String limpio = texto.trim().replace("€", "").replace(" ", "");
        if (limpio.isEmpty()
                || limpio.equalsIgnoreCase("No disponible")
                || limpio.equalsIgnoreCase(DATO_NO_DISPONIBLE)) {
            return null;
        }
        try {
            double valor = Double.parseDouble(limpio.replace(",", "."));
            return valor > 0 ? valor : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Integer parsearEntero(String texto) {
        if (texto == null) {
            return null;
        }
        String limpio = texto.trim();
        if (limpio.isEmpty()
                || limpio.equalsIgnoreCase("No disponible")
                || limpio.equalsIgnoreCase(DATO_NO_DISPONIBLE)) {
            return null;
        }
        try {
            int valor = Integer.parseInt(limpio);
            return valor > 0 ? valor : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String getExpediente() {
        return expediente;
    }

    public String getObjeto() {
        return objeto;
    }

    public String getTitulo() {
        return objeto;
    }

    public String getLote() {
        return lote;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public String getCpv() {
        return cpv;
    }

    public String getOrganismo() {
        return organismo;
    }

    public String getTipoOrganismo() {
        return tipoOrganismo;
    }

    public Double getImporte() {
        return importe;
    }

    public Integer getDuracionMeses() {
        return duracionMeses;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public String getImporteFormateado() {
        if (importe == null) {
            return DATO_NO_DISPONIBLE;
        }
        NumberFormat formato = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-ES"));
        return formato.format(importe);
    }

    public String getDuracionFormateada() {
        if (duracionMeses == null) {
            return DATO_NO_DISPONIBLE;
        }
        return duracionMeses + (duracionMeses == 1 ? " mes" : " meses");
    }

    /**
     * Extrae el año de publicación si la fecha está en formato YYYY-MM-DD; devuelve null si no es válida.
     */
    public Integer getAnioPublicacion() {
        if (esDatoNoDisponible(fechaPublicacion) || fechaPublicacion.length() < 4) {
            return null;
        }
        try {
            return Integer.parseInt(fechaPublicacion.substring(0, 4));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Indica cuántos campos opcionales clave tienen información real (regla de desempate de UMivale).
     */
    public int contarCamposCompletos() {
        int completos = 0;
        if (importe != null) completos++;
        if (duracionMeses != null) completos++;
        if (!esDatoNoDisponible(cpv)) completos++;
        if (!esDatoNoDisponible(organismo)) completos++;
        if (!esDatoNoDisponible(tipoOrganismo)) completos++;
        return completos;
    }

    public boolean tieneDatosFaltantes() {
        return importe == null
                || duracionMeses == null
                || esDatoNoDisponible(cpv)
                || esDatoNoDisponible(organismo);
    }

    public static boolean esDatoNoDisponible(String valor) {
        return valor == null
                || valor.trim().isEmpty()
                || valor.equalsIgnoreCase(DATO_NO_DISPONIBLE)
                || valor.equalsIgnoreCase("No disponible");
    }

    @Override
    public String toString() {
        return expediente + " (" + lote + ") - " + objeto;
    }
}
