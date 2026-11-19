package com.umivale.licitaciones.service;

import com.umivale.licitaciones.model.Licitacion;
import com.umivale.licitaciones.model.ResultadoBusqueda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimilitudServiceTest {

    private SimilitudService service;
    private List<Licitacion> muestra;

    @BeforeEach
    void setUp() {
        service = new SimilitudService();
        muestra = new CsvLicitacionLoader().cargarDesdeRecurso("/data/mock.csv");
    }

    @Test
    void buscarSoporteUsuariosEncuentraSimilaresYOrdenaPorPuntuacion() {
        List<ResultadoBusqueda> resultados = service.buscarSimilares(
                "soporte a usuarios y puestos de trabajo", "Servicios", muestra
        );

        assertFalse(resultados.isEmpty(), "Debe devolver resultados comparables");
        // Comprobar orden descendente de similitud
        for (int i = 1; i < resultados.size(); i++) {
            assertTrue(
                    resultados.get(i - 1).getPuntuacionSimilitud() >= resultados.get(i).getPuntuacionSimilitud(),
                    "Los resultados deben venir ordenados de mayor a menor similitud"
            );
        }
        // Ningún resultado debe ser de Suministros ni anterior a 2024
        assertTrue(resultados.stream().allMatch(r -> "Servicios".equalsIgnoreCase(r.getTipoContrato())));
        assertTrue(resultados.stream().noneMatch(r -> "EXP-2022-099".equals(r.getExpediente())));
    }

    @Test
    void buscarConSinonimosEncuentraLicitacionesSinPalabraExacta() {
        List<ResultadoBusqueda> resultados = service.buscarSimilares(
                "helpdesk microinformatico", "Servicios", muestra
        );

        assertFalse(resultados.isEmpty(), "Debe encontrar resultados usando sinónimos del dominio");
        assertTrue(
                resultados.stream().anyMatch(r -> r.getObjeto().toLowerCase().contains("atencion a usuarios")),
                "Debe detectar 'atención a usuarios' como equivalente a 'helpdesk'"
        );
    }

    @Test
    void tipoContratoObligatorioNoMezclaServiciosConSuministros() {
        List<ResultadoBusqueda> soloServicios = service.buscarSimilares(
                "puestos de trabajo", "Servicios", muestra
        );
        assertTrue(soloServicios.stream().noneMatch(r -> "EXP-2026-011".equals(r.getExpediente())),
                "El suministro de ordenadores no debe mezclarse con contratos de Servicios");

        List<ResultadoBusqueda> soloSuministros = service.buscarSimilares(
                "puestos de trabajo", "Suministros", muestra
        );
        assertTrue(soloSuministros.stream().anyMatch(r -> "EXP-2026-011".equals(r.getExpediente())));
    }

    @Test
    void aIgualSimilitudLaLicitacionCompletaVaAntesQueLaIncompleta() {
        Licitacion incompleta = new Licitacion(
                "EXP-INC", "Desarrollo de aplicaciones Java", "Único",
                "Servicios", "72200000", "Mutua Ejemplo", "Mutua",
                null, null, "2026-01-10"
        );
        Licitacion completa = new Licitacion(
                "EXP-COMP", "Desarrollo de aplicaciones Java", "Único",
                "Servicios", "72200000", "Mutua Ejemplo", "Mutua",
                120000.0, 24, "2026-01-10"
        );

        List<ResultadoBusqueda> resultados = service.buscarSimilares(
                "desarrollo aplicaciones java", "Servicios", List.of(incompleta, completa)
        );

        assertEquals(2, resultados.size());
        assertEquals("EXP-COMP", resultados.get(0).getExpediente(),
                "A igual parecido, la licitación completa debe ir por delante de la que tiene datos faltantes");
    }

    @Test
    void consultaVaciaDevuelveListaVacia() {
        assertTrue(service.buscarSimilares("   ", "Servicios", muestra).isEmpty());
    }
    @Test
void horizonteTemporalIncluyeDesde2024YExcluyeAniosAnteriores() {
    Licitacion de2023 = new Licitacion(
            "EXP-2023",
            "Desarrollo de aplicaciones Java",
            "Único",
            "Servicios",
            "72200000",
            "Mutua Ejemplo",
            "Mutua",
            120000.0,
            24,
            "2023-12-31"
    );

    Licitacion de2024 = new Licitacion(
            "EXP-2024",
            "Desarrollo de aplicaciones Java",
            "Único",
            "Servicios",
            "72200000",
            "Mutua Ejemplo",
            "Mutua",
            120000.0,
            24,
            "2024-01-01"
    );

    List<ResultadoBusqueda> resultados = service.buscarSimilares(
            "desarrollo aplicaciones java",
            "Servicios",
            List.of(de2023, de2024)
    );

    assertTrue(resultados.stream()
            .anyMatch(r -> "EXP-2024".equals(r.getExpediente())));

    assertFalse(resultados.stream()
            .anyMatch(r -> "EXP-2023".equals(r.getExpediente())));
}
}
