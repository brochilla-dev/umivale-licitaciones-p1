package com.umivale.licitaciones.service;

import com.umivale.licitaciones.model.Licitacion;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el CSV de licitaciones: " + e.getMessage(), e);
        }

        return licitaciones;
    }

    private String limpiarDato(String[] columnas, int indice) {
        if (indice >= columnas.length || columnas[indice] == null || columnas[indice].trim().isEmpty()) {
            return "No disponible";
        }
        return columnas[indice].trim();
    }
}
