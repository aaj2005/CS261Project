package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class PrimaryController {
    @FXML private ListView<SimListItem> simList;
    @FXML private AnchorPane graphContainer;
    @FXML private Tab metricsTab;
    @FXML private CheckBox pc_enabled;
    @FXML private TextField crossing_duration, crossing_requests;
    @FXML private Label lbl_duration, lbl_requests;
    @FXML private Button run_button;



    private SimulationManager simulationManager;
    private ListCell<SimListItem> selectedCell;

    @FXML
    public void initialize() {
//        simList.setPrefHeight(simList.getItems().size() * 50); // Adjust height based on items
        simulationManager = new SimulationManager(simList.getItems());


        simList.getItems().add(new SimListItem("New Simulation"));
        simList.setCellFactory(param -> new SimulationCellFactory(simulationManager, this));
        simList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        simList.getSelectionModel().clearSelection();

        metricsTab.setOnSelectionChanged(event -> {
            if (metricsTab.isSelected()) loadSimulationData();
        });

        pc_enabled.selectedProperty().addListener((obs, oldVal, newVal) -> togglePedestrianInputs(newVal));
        run_button.setOnAction(event -> runSimulation());
    }

    private void togglePedestrianInputs(boolean enabled) {
        lbl_duration.setDisable(!enabled);
        lbl_requests.setDisable(!enabled);
        crossing_duration.setDisable(!enabled);
        crossing_requests.setDisable(!enabled);
    }


    private void loadSimulationData() {
        SimulationData[] runs = new SimulationData[5];
        runs[0] = new SimulationData("Run 1", new double[]{2.5, 3.0, 4.5, 1.5}, new double[]{5.0, 6.0, 8.0, 3.0}, new double[]{3.2, 4.0, 5.5, 2.5});
        runs[1] = new SimulationData("Run 2", new double[]{2.8, 3.5, 4.8, 1.8}, new double[]{5.5, 6.5, 8.5, 3.5}, new double[]{3.5, 4.2, 5.8, 2.8});
        runs[2] = new SimulationData("Run 3", new double[]{3.0, 3.8, 5.0, 2.0}, new double[]{6.0, 7.0, 9.0, 4.0}, new double[]{3.8, 4.5, 6.0, 3.0});
        runs[3] = new SimulationData("Run 4", new double[]{2.6, 3.2, 4.6, 1.6}, new double[]{5.2, 6.2, 8.2, 3.2}, new double[]{3.3, 4.1, 5.6, 2.6});
        runs[4] = new SimulationData("Run 5", new double[]{2.6, 3.2, 4.6, 1.6}, new double[]{5.2, 6.2, 8.2, 3.2}, new double[]{3.3, 4.1, 5.6, 2.6});



        VBox graph = Graph.createGraph(runs);
        graphContainer.setTopAnchor(graph, 0.0);
        graphContainer.setBottomAnchor(graph, 0.0);
        graphContainer.setLeftAnchor(graph, 0.0);
        graphContainer.setRightAnchor(graph, 0.0);
        graphContainer.getChildren().setAll(graph);
    }


    private void runSimulation() {
        run_button.setDisable(true);
    }

    public void setSelectedCell(ListCell<SimListItem> cell) {
        if (selectedCell != null) {
            selectedCell.setStyle("");
        }
        selectedCell = cell;
        if (selectedCell != null) {
            selectedCell.setStyle("-fx-background-color: lightblue;");
        }
    }

    public ListCell<SimListItem> getSelectedCell() {
        return selectedCell;
    }


}
