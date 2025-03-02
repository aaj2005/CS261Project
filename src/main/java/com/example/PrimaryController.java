package com.example;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class PrimaryController {
    @FXML private ListView<Simulation> simList;
    @FXML private AnchorPane graphContainer;
    @FXML private Tab metricsTab;
    @FXML private Button run_button;

    // Northbound controls
    @FXML private ChoiceBox<Integer> nb_lanes;
    @FXML private CheckBox n_buslane, n_leftturn;
    @FXML private TextField txt_nn, txt_ne, txt_nw;

    // Eastbound controls
    @FXML private ChoiceBox<Integer> eb_lanes;
    @FXML private CheckBox e_buslane, e_leftturn;
    @FXML private TextField txt_en, txt_ee, txt_es;

    // Southbound controls
    @FXML private ChoiceBox<Integer> sb_lanes;
    @FXML private CheckBox s_buslane, s_leftturn;
    @FXML private TextField txt_se, txt_ss, txt_sw;

    // Westbound controls
    @FXML private ChoiceBox<Integer> wb_lanes;
    @FXML private CheckBox w_buslane, w_leftturn;
    @FXML private TextField txt_wn, txt_ws, txt_ww;

    // Pedestrian crossing controls
    @FXML private CheckBox pc_enabled;
    @FXML private Label lbl_duration, lbl_requests;
    @FXML private TextField crossing_duration, crossing_requests;


    private SimulationManager simulationManager;
    private ListCell<Simulation> selectedCell;

    @FXML
    public void initialize() {
        simulationManager = new SimulationManager(simList.getItems());


        simList.getItems().add(new Simulation("Simulation 1"));
        simList.getItems().add(new Simulation("New Simulation"));

        simList.setCellFactory(param -> new SimulationCellFactory(simulationManager, this));
        simList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        Platform.runLater(() -> {
            if (!simList.getItems().isEmpty()) {
                simList.getSelectionModel().select(0);
                selectedCell = (ListCell<Simulation>) simList.lookup(".list-cell");
            }
        });

        // Update `selectedCell` when a new selection is made
        simList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedCell = (ListCell<Simulation>) simList.lookup(".list-cell");
        });

        metricsTab.setOnSelectionChanged(event -> {
            if (metricsTab.isSelected()) loadSimulationData();
        });

        setListeners();

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

    public void setSelectedCell(ListCell<Simulation> cell) {
        if (selectedCell != null) {
            selectedCell.setStyle("");
        }
        selectedCell = cell;
        if (selectedCell != null) {
            selectedCell.setStyle("-fx-background-color: lightblue;");
        }
    }

    public ListCell<Simulation> getSelectedCell() {
        return selectedCell;
    }

    public void setListeners() {

        nb_lanes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            Simulation selectedSim = selectedCell.getItem();
            selectedSim.setNorthNumLanes(newVal);

        });
        n_buslane.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = selectedCell.getItem();
            selectedSim.setNorthBusLane(newValue);
            System.out.print("SET value to " + newValue);
            lbl_duration.setText(newValue.toString());
        });
        n_leftturn.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = selectedCell.getItem();
            selectedSim.setNorthLeftTurn(newValue);
        });
        eb_lanes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            Simulation selectedSim = selectedCell.getItem();
            selectedSim.setEastNumLanes(newVal);
        });
        e_buslane.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = selectedCell.getItem();
            selectedSim.setEastBusLane(newValue);
        });
        e_leftturn.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = selectedCell.getItem();
            selectedSim.setEastLeftTurn(newValue);
        });
        sb_lanes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            Simulation selectedSim = selectedCell.getItem();
            selectedSim.setSouthNumLanes(newVal);
        });
        s_buslane.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = selectedCell.getItem();
            selectedSim.setSouthBusLane(newValue);
        });
        s_leftturn.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = selectedCell.getItem();
            selectedSim.setSouthLeftTurn(newValue);
        });
        wb_lanes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            Simulation selectedSim = selectedCell.getItem();
            selectedSim.setWestNumLanes(newVal);
        });
        w_buslane.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = selectedCell.getItem();
            selectedSim.setWestBusLane(newValue);
        });
        w_leftturn.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = selectedCell.getItem();
            selectedSim.setWestLeftTurn(newValue);
        });

    }


}
