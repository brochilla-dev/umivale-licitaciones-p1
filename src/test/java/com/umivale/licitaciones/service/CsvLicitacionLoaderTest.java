package com.umivale.licitaciones.service;

import com.umivale.licitaciones.model.Licitacion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvLicitacionLoaderTest {

    private final CsvLicitacionLoader loader = new CsvLicitacionLoader();

    @Test
    void cargarMockCsvDevuelveLicitacionesYTrataDatosFaltantes() {
        List<Licitacion> licitaciones = loader.cargarDesdeRecurso("/data/mock.csv");

        assertFalse(licitaciones.isEmpty(), "La muestra local debe contener licitaciones");
        assertEquals(16, licitaciones.size(), "Debe cargar las 16 filas de muestra (incluyendo lotes)");

        // Caso con datos faltantes (EXP-2026-003 no tiene organismo ni duración)
        Licitacion sinDatos = licitaciones.stream()
                .filter(l -> "EXP-2026-003".equals(l.getExpediente()))
                .findFirst()
                .orElseThrow();

        assertEquals(Licitacion.DATO_NO_DISPONIBLE, sinDatos.getOrganismo());
        assertEquals(Licitacion.DATO_NO_DISPONIBLE, sinDatos.getDuracionFormateada());
        assertTrue(sinDatos.tieneDatosFaltantes());

        // Caso de lotes independientes (EXP-2026-001 tiene Lote 1 y Lote 2)
        long lotesExp1 = licitaciones.stream()
                .filter(l -> "EXP-2026-001".equals(l.getExpediente()))
                .count();
        assertEquals(2, lotesExp1, "Los lotes de una licitación deben cargarse como unidades independientes");
    }

    @Test
    void cargarRecursoInexistenteLanzaIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> loader.cargarDesdeRecurso("/data/no_existe.csv")
        );
        assertTrue(ex.getMessage().contains("No se encontró el archivo CSV"));
    }
}
