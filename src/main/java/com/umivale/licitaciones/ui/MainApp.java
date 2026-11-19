package com.umivale.licitaciones.ui;

import com.umivale.licitaciones.model.Licitacion;
import com.umivale.licitaciones.model.ResultadoBusqueda;
import com.umivale.licitaciones.service.CsvLicitacionLoader;
import com.umivale.licitaciones.service.SimilitudService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

/**
 * Interfaz gráfica de escritorio en JavaFX para el buscador de licitaciones de UMivale (Sprint 1).
 */
public class MainApp extends Application {

    private List<Licitacion> baseLicitaciones;
    private final SimilitudService similitudService = new SimilitudService();
    private TableView<ResultadoBusqueda> tablaResultados;
    private TextArea txtDetalle;
    private Label lblEstado;

    @Override
    public void start(Stage stage) {
        cargarDatosIniciales();

        // Panel superior de búsqueda
        Label lblTitulo = new Label("Buscador de Licitaciones Comparables · UMivale (Sprint 1)");
        lblTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField txtConsulta = new TextField();
        txtConsulta.setPromptText("Describe el servicio (ej. soporte a usuarios y puestos de trabajo)...");
        HBox.setHgrow(txtConsulta, Priority.ALWAYS);

        ComboBox<String> cmbTipoContrato = new ComboBox<>();
        cmbTipoContrato.getItems().addAll("Servicios", "Suministros", "Obras", "Todos");
        cmbTipoContrato.setValue("Servicios");

        Button btnBuscar = new Button("Buscar");
        btnBuscar.setDefaultButton(true);
        btnBuscar.setStyle("-fx-background-color: #005A9C; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox barraBusqueda = new HBox(10, new Label("Servicio:"), txtConsulta, new Label("Tipo:"), cmbTipoContrato, btnBuscar);
        barraBusqueda.setAlignment(Pos.CENTER_LEFT);

        VBox panelSuperior = new VBox(10, lblTitulo, barraBusqueda);
        panelSuperior.setPadding(new Insets(15));
        panelSuperior.setStyle("-fx-background-color: #F4F6F8; -fx-border-color: #D0D7DE; -fx-border-width: 0 0 1 0;");

        // Tabla central de resultados
        tablaResultados = construirTabla();

        // Panel de justificación clara de la licitación seleccionada
        Label lblDetalleTitulo = new Label("Justificación de similitud y ficha resumida:");
        lblDetalleTitulo.setStyle("-fx-font-weight: bold;");

        txtDetalle = new TextArea("Selecciona una licitación en la tabla para ver por qué se considera comparable.");
        txtDetalle.setEditable(false);
        txtDetalle.setWrapText(true);
        txtDetalle.setPrefRowCount(4);

        tablaResultados.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            mostrarDetalle(seleccionado);
        });

        // Barra inferior de estado
        lblEstado = new Label("Datos cargados: " + baseLicitaciones.size()
                + " licitaciones disponibles en muestra local (horizonte configurable desde "
                + similitudService.getAnioMinimo() + ").");
        lblEstado.setPadding(new Insets(6, 0, 0, 0));

        VBox panelInferior = new VBox(6, lblDetalleTitulo, txtDetalle, lblEstado);
        panelInferior.setPadding(new Insets(10, 15, 12, 15));
        panelInferior.setStyle("-fx-background-color: #F9FAFB; -fx-border-color: #D0D7DE; -fx-border-width: 1 0 0 0;");

        // Evento de búsqueda
        btnBuscar.setOnAction(e -> ejecutarBusqueda(txtConsulta.getText(), cmbTipoContrato.getValue()));

        BorderPane root = new BorderPane();
        root.setTop(panelSuperior);
        root.setCenter(tablaResultados);
        root.setBottom(panelInferior);

        Scene scene = new Scene(root, 1200, 680);
        stage.setTitle("UMivale · Comparador de Licitaciones (P1 - Sprint 1)");
        stage.setScene(scene);
        stage.show();
    }

    private void cargarDatosIniciales() {
        CsvLicitacionLoader loader = new CsvLicitacionLoader();
        try {
            baseLicitaciones = loader.cargarDesdeRecurso("/data/mock.csv");
        } catch (Exception e) {
            baseLicitaciones = List.of();
            System.err.println("Error cargando mock.csv: " + e.getMessage());
        }
    }

    private void ejecutarBusqueda(String consulta, String tipoContrato) {
        if (consulta == null || consulta.trim().isEmpty()) {
            lblEstado.setText("Aviso: Introduce una descripción libre del servicio antes de buscar.");
            tablaResultados.setItems(FXCollections.observableArrayList());
            txtDetalle.setText("Introduce una descripción libre del servicio para obtener resultados comparables.");
            return;
        }

        List<ResultadoBusqueda> resultados = similitudService.buscarSimilares(consulta, tipoContrato, baseLicitaciones);
        tablaResultados.setItems(FXCollections.observableArrayList(resultados));

        if (resultados.isEmpty()) {
            lblEstado.setText("No se encontraron licitaciones similares para '" + consulta + "' con tipo '" + tipoContrato + "'.");
            txtDetalle.setText("Sin coincidencias relevantes para esta búsqueda.");
        } else {
            long relevantes = resultados.stream().filter(ResultadoBusqueda::isRelevante).count();
            long menosParecidas = resultados.size() - relevantes;
            lblEstado.setText("Encontradas " + resultados.size() + " licitaciones (" + relevantes
                    + " relevantes, " + menosParecidas + " menos parecidas) · Tipo: " + tipoContrato + ".");
            tablaResultados.getSelectionModel().selectFirst();
        }
    }

    private void mostrarDetalle(ResultadoBusqueda resultado) {
        if (resultado == null) {
            txtDetalle.setText("Selecciona una licitación en la tabla para ver por qué se considera comparable.");
            return;
        }
        String detalle = String.format(
                "Expediente: %s (%s) | Tipo: %s | Organismo: %s (%s)%n"
                        + "Objeto: %s%n"
                        + "CPV: %s | Importe: %s | Duración: %s | Fecha: %s%n"
                        + "Similitud (%s): %s",
                resultado.getExpediente(),
                resultado.getLote(),
                resultado.getTipoContrato(),
                resultado.getOrganismo(),
                resultado.getTipoOrganismo(),
                resultado.getObjeto(),
                resultado.getCpv(),
                resultado.getImporteFormateado(),
                resultado.getDuracionFormateada(),
                resultado.getFechaPublicacion(),
                resultado.getSimilitudFormateada(),
                resultado.getExplicacion()
        );
        txtDetalle.setText(detalle);
    }

    private TableView<ResultadoBusqueda> construirTabla() {
        TableView<ResultadoBusqueda> tabla = new TableView<>();
        tabla.setPlaceholder(new Label("Introduce una descripción arriba y pulsa 'Buscar' para ver resultados."));

        TableColumn<ResultadoBusqueda, String> colSim = new TableColumn<>("Similitud");
        colSim.setCellValueFactory(new PropertyValueFactory<>("similitudFormateada"));
        colSim.setPrefWidth(80);

        TableColumn<ResultadoBusqueda, String> colRel = new TableColumn<>("Relevancia");
        colRel.setCellValueFactory(new PropertyValueFactory<>("etiquetaRelevancia"));
        colRel.setPrefWidth(105);

        TableColumn<ResultadoBusqueda, String> colObjeto = new TableColumn<>("Objeto del contrato");
        colObjeto.setCellValueFactory(new PropertyValueFactory<>("objeto"));
        colObjeto.setPrefWidth(310);

        TableColumn<ResultadoBusqueda, String> colLote = new TableColumn<>("Lote");
        colLote.setCellValueFactory(new PropertyValueFactory<>("lote"));
        colLote.setPrefWidth(65);

        TableColumn<ResultadoBusqueda, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoContrato"));
        colTipo.setPrefWidth(80);

        TableColumn<ResultadoBusqueda, String> colCpv = new TableColumn<>("CPV");
        colCpv.setCellValueFactory(new PropertyValueFactory<>("cpv"));
        colCpv.setPrefWidth(80);

        TableColumn<ResultadoBusqueda, String> colOrg = new TableColumn<>("Organismo");
        colOrg.setCellValueFactory(new PropertyValueFactory<>("organismo"));
        colOrg.setPrefWidth(185);

        TableColumn<ResultadoBusqueda, String> colImporte = new TableColumn<>("Importe");
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importeFormateado"));
        colImporte.setPrefWidth(115);

        TableColumn<ResultadoBusqueda, String> colDuracion = new TableColumn<>("Duración");
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracionFormateada"));
        colDuracion.setPrefWidth(95);

        TableColumn<ResultadoBusqueda, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPublicacion"));
        colFecha.setPrefWidth(90);

        tabla.getColumns().addAll(List.of(
                colSim, colRel, colObjeto, colLote, colTipo, colCpv, colOrg, colImporte, colDuracion, colFecha
        ));
        return tabla;
    }

    public static void main(String[] args) {
    launch(args);
}
}
