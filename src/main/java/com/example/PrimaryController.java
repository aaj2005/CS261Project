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

    @FXML
    public void initialize() {
        simulationManager = new SimulationManager(simList.getItems());

        simList.getItems().add(new SimListItem("New Simulation"));
        simList.setCellFactory(param -> new SimulationCellFactory(simulationManager));
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
        VBox graph = Graph.createGraph(new SimulationData[] {
                new SimulationData("Run 1", new double[]{2.5, 3.0}, new double[]{5.0, 6.0}, new double[]{3.2, 4.0})
        });
        graphContainer.getChildren().setAll(graph);
    }

    private void runSimulation() {
        run_button.setDisable(true);
    }
}
