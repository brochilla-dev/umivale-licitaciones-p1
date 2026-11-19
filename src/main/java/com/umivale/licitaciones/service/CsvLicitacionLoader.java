package com.umivale.licitaciones.service;

import com.umivale.licitaciones.model.Licitacion;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Carga licitaciones de muestra desde un archivo CSV incluido en resources.
 * Soporta tanto el formato completo de 10 columnas como el formato simplificado de 6 columnas.
 */
public class CsvLicitacionLoader {

    public List<Licitacion> cargarDesdeRecurso(String rutaRecurso) {
        List<Licitacion> licitaciones = new ArrayList<>();

        InputStream inputStream = getClass().getResourceAsStream(rutaRecurso);
        if (inputStream == null) {
            throw new IllegalArgumentException("No se encontró el archivo CSV en: " + rutaRecurso);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String linea;
            boolean esPrimeraLinea = true;

            while ((linea = reader.readLine()) != null) {
                if (esPrimeraLinea) {
                    esPrimeraLinea = false;
                    continue;
                }
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] columnas = linea.split(";", -1);

                if (columnas.length >= 10) {
                    String expediente = limpiarDato(columnas, 0);
                    String objeto = limpiarDato(columnas, 1);
                    String lote = limpiarDato(columnas, 2);
                    String tipoContrato = limpiarDato(columnas, 3);
                    String cpv = limpiarDato(columnas, 4);
                    String organismo = limpiarDato(columnas, 5);
                    String tipoOrganismo = limpiarDato(columnas, 6);
                    Double importe = Licitacion.parsearDouble(obtenerCrudo(columnas, 7));
                    Integer duracionMeses = Licitacion.parsearEntero(obtenerCrudo(columnas, 8));
                    String fechaPublicacion = limpiarDato(columnas, 9);

                    licitaciones.add(new Licitacion(
                            expediente, objeto, lote, tipoContrato, cpv,
                            organismo, tipoOrganismo, importe, duracionMeses, fechaPublicacion
                    ));
                } else {
                    String expediente = limpiarDato(columnas, 0);
                    String titulo = limpiarDato(columnas, 1);
                    String tipoContrato = limpiarDato(columnas, 2);
                    String organismo = limpiarDato(columnas, 3);
                    String presupuesto = limpiarDato(columnas, 4);
                    String fechaLimite = limpiarDato(columnas, 5);

                    licitaciones.add(new Licitacion(
                            expediente, titulo, tipoContrato, organismo, presupuesto, fechaLimite
                    ));
                }
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el CSV de licitaciones: " + e.getMessage(), e);
        }

        return licitaciones;
    }

    private String obtenerCrudo(String[] columnas, int indice) {
        if (indice >= columnas.length || columnas[indice] == null) {
            return "";
        }
        return columnas[indice].trim();
    }

    private String limpiarDato(String[] columnas, int indice) {
        String valor = obtenerCrudo(columnas, indice);
        if (valor.isEmpty()) {
            return Licitacion.DATO_NO_DISPONIBLE;
        }
        return valor;
    }
}
