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

        // Barra inferior de estado
        lblEstado = new Label("Datos cargados: " + baseLicitaciones.size() + " licitaciones disponibles en muestra local.");
        lblEstado.setPadding(new Insets(10, 15, 10, 15));

        // Evento de búsqueda
        btnBuscar.setOnAction(e -> ejecutarBusqueda(txtConsulta.getText(), cmbTipoContrato.getValue()));

        BorderPane root = new BorderPane();
        root.setTop(panelSuperior);
        root.setCenter(tablaResultados);
        root.setBottom(lblEstado);

        Scene scene = new Scene(root, 1150, 600);
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
            return;
        }

        List<ResultadoBusqueda> resultados = similitudService.buscarSimilares(consulta, tipoContrato, baseLicitaciones);
        tablaResultados.setItems(FXCollections.observableArrayList(resultados));

        if (resultados.isEmpty()) {
            lblEstado.setText("No se encontraron licitaciones similares para '" + consulta + "' con tipo '" + tipoContrato + "'.");
        } else {
            lblEstado.setText("Mostrando " + resultados.size() + " licitaciones ordenadas por similitud (Tipo: " + tipoContrato + ").");
        }
    }

    private TableView<ResultadoBusqueda> construirTabla() {
        TableView<ResultadoBusqueda> tabla = new TableView<>();
        tabla.setPlaceholder(new Label("Introduce una descripción arriba y pulsa 'Buscar' para ver resultados."));

        TableColumn<ResultadoBusqueda, String> colSim = new TableColumn<>("Similitud");
        colSim.setCellValueFactory(new PropertyValueFactory<>("similitudFormateada"));
        colSim.setPrefWidth(85);

        TableColumn<ResultadoBusqueda, String> colObjeto = new TableColumn<>("Objeto del contrato");
        colObjeto.setCellValueFactory(new PropertyValueFactory<>("objeto"));
        colObjeto.setPrefWidth(320);

        TableColumn<ResultadoBusqueda, String> colLote = new TableColumn<>("Lote");
        colLote.setCellValueFactory(new PropertyValueFactory<>("lote"));
        colLote.setPrefWidth(70);

        TableColumn<ResultadoBusqueda, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoContrato"));
        colTipo.setPrefWidth(85);

        TableColumn<ResultadoBusqueda, String> colCpv = new TableColumn<>("CPV");
        colCpv.setCellValueFactory(new PropertyValueFactory<>("cpv"));
        colCpv.setPrefWidth(85);

        TableColumn<ResultadoBusqueda, String> colOrg = new TableColumn<>("Organismo");
        colOrg.setCellValueFactory(new PropertyValueFactory<>("organismo"));
        colOrg.setPrefWidth(190);

        TableColumn<ResultadoBusqueda, String> colImporte = new TableColumn<>("Importe");
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importeFormateado"));
        colImporte.setPrefWidth(100);

        TableColumn<ResultadoBusqueda, String> colDuracion = new TableColumn<>("Duración");
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracionFormateada"));
        colDuracion.setPrefWidth(90);

        TableColumn<ResultadoBusqueda, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPublicacion"));
        colFecha.setPrefWidth(95);

        tabla.getColumns().addAll(colSim, colObjeto, colLote, colTipo, colCpv, colOrg, colImporte, colDuracion, colFecha);
        return tabla;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
