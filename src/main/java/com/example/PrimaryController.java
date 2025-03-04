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
    @FXML private Label sim_title;

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

        // Select the first listcell after it has been created (after the initialize function finishes)
        Platform.runLater(() -> {
            if (!simList.getItems().isEmpty()) {
                simList.getSelectionModel().select(0);
                selectedCell = (ListCell<Simulation>) simList.lookup(".list-cell");
            }
        });

        // Trigger the metrics tab to load the simulation data
        metricsTab.setOnSelectionChanged(event -> {
            if (metricsTab.isSelected()) loadSimulationData();
        });

        initialise_choiceboxes();

        // Set listens for inputs that change UI configuration (i.e. number of lanes)
        setDynamicListeners();
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
        if (selectedCell != null && selectedCell.getItem() != null) {
            System.out.println("SAVING PARAM FOR SIM: " + cell.getItem().getSimName());
            selectedCell.setStyle("");
            Simulation selectedSim = selectedCell.getItem();
            selectedSim.setNumberParameters(
                    Integer.valueOf(txt_nn.getText()),
                    Integer.valueOf(txt_ne.getText()),
                    Integer.valueOf(txt_nw.getText()),
                    Integer.valueOf(txt_en.getText()),
                    Integer.valueOf(txt_ee.getText()),
                    Integer.valueOf(txt_es.getText()),
                    Integer.valueOf(txt_se.getText()),
                    Integer.valueOf(txt_ss.getText()),
                    Integer.valueOf(txt_sw.getText()),
                    Integer.valueOf(txt_wn.getText()),
                    Integer.valueOf(txt_ws.getText()),
                    Integer.valueOf(txt_ww.getText()),
                    Integer.valueOf(crossing_duration.getText()),
                    Integer.valueOf(crossing_requests.getText())
            );
        }

        selectedCell = cell;

        if (selectedCell != null) {
            System.out.println("Setting to bloo");
            selectedCell.setStyle("-fx-background-color: lightblue;");
            loadParameterValues(selectedCell.getItem());
        }

        System.out.println("NEW SELECTED SIM: " + cell.getItem().getSimName());
    }

    public ListCell<Simulation> getSelectedCell() {
        return selectedCell;
    }

    public void setDynamicListeners() {
        // Sets up listeners for input parameters that change the UI configuration (i.e. number of lanes)
        nb_lanes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            Simulation selectedSim = selectedCell.getItem();
            selectedSim.setNorthNumLanes(newVal);

        });
        n_buslane.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = selectedCell.getItem();
            selectedSim.setNorthBusLane(newValue);
            System.out.print("SET value to " + newValue + " for " + selectedSim.getSimName());
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
        pc_enabled.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = selectedCell.getItem();
            selectedSim.setPedestrianCrossings(newValue);
            togglePedestrianInputs(newValue);
        });
    }

    public void loadParameterValues(Simulation selectedSim) {
        System.out.println("North num lanes for " + selectedSim.getSimName() + " is " + selectedSim.getNorth_num_lanes());
        sim_title.setText(selectedSim.getSimName().toUpperCase());

        nb_lanes.setValue(selectedSim.getNorth_num_lanes());
        n_buslane.setSelected(selectedSim.getNorth_bus_lane());
        n_leftturn.setSelected(selectedSim.getNorth_left_turn());
        txt_nn.setText(selectedSim.getNorth_north_vph().toString());
        txt_ne.setText(selectedSim.getNorth_east_vph().toString());
        txt_nw.setText(selectedSim.getNorth_west_vph().toString());

        eb_lanes.setValue(selectedSim.getEast_num_lanes());
        e_buslane.setSelected(selectedSim.getEast_bus_lane());
        e_leftturn.setSelected(selectedSim.getEast_left_turn());
        txt_ee.setText(selectedSim.getEast_east_vph().toString());
        txt_en.setText(selectedSim.getEast_north_vph().toString());
        txt_es.setText(selectedSim.getEast_south_vph().toString());

        sb_lanes.setValue(selectedSim.getSouth_num_lanes());
        s_buslane.setSelected(selectedSim.getSouth_bus_lane());
        s_leftturn.setSelected(selectedSim.getSouth_left_turn());
        txt_se.setText(selectedSim.getSouth_east_vph().toString());
        txt_ss.setText(selectedSim.getSouth_south_vph().toString());
        txt_sw.setText(selectedSim.getSouth_west_vph().toString());

        wb_lanes.setValue(selectedSim.getWest_num_lanes());
        w_buslane.setSelected(selectedSim.getWest_bus_lane());
        w_leftturn.setSelected(selectedSim.getWest_left_turn());
        txt_wn.setText(selectedSim.getWest_north_vph().toString());
        txt_ws.setText(selectedSim.getWest_south_vph().toString());
        txt_ww.setText(selectedSim.getWest_west_vph().toString());

        pc_enabled.setSelected(selectedSim.getPedestrian_crossings());
        togglePedestrianInputs(pc_enabled.isSelected());

        System.out.println(selectedSim.getPedestrian_crossings());
        crossing_duration.setText(selectedSim.getDuration_of_crossings().toString());
        System.out.println(selectedSim.getDuration_of_crossings());
        crossing_requests.setText(selectedSim.getRequests_per_hour().toString());
        System.out.println(selectedSim.getRequests_per_hour());
    }

    public void initialise_choiceboxes() {
        nb_lanes.getItems().addAll(1,2,3,4,5);
        nb_lanes.setValue(1);
        eb_lanes.getItems().addAll(1,2,3,4,5);
        eb_lanes.setValue(1);
        sb_lanes.getItems().addAll(1,2,3,4,5);
        sb_lanes.setValue(1);
        wb_lanes.getItems().addAll(1,2,3,4,5);
        wb_lanes.setValue(1);
    }

    public Label getSim_title() {
        return sim_title;
    }
}
