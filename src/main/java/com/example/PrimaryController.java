package com.example;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;


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

    // Result labels
    @FXML private Label avg_wait_north;
    @FXML private Label avg_wait_east;
    @FXML private Label avg_wait_south;
    @FXML private Label avg_wait_west;

    @FXML private Label max_wait_north;
    @FXML private Label max_wait_east;
    @FXML private Label max_wait_south;
    @FXML private Label max_wait_west;

    @FXML private Label max_queue_north;
    @FXML private Label max_queue_east;
    @FXML private Label max_queue_south;
    @FXML private Label max_queue_west;


    @FXML private AnchorPane sim_anchor;


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

        SimulationComponents simComponent = new SimulationComponents(
                3,4,3,3, true,
                true, true,
                true, true,
                true, true,
                true, true,
                true,false,
                false, false
        );

        AnchorPane childPane = simComponent.getRoot();
        sim_anchor.getChildren().add(childPane);

        // Clip the anchor pane
        Rectangle clip = new Rectangle();
        clip.setWidth(600);
        clip.setHeight(600);
        childPane.setClip(clip);


    }

    private void togglePedestrianInputs(boolean enabled) {
        lbl_duration.setDisable(!enabled);
        lbl_requests.setDisable(!enabled);
        crossing_duration.setDisable(!enabled);
        crossing_requests.setDisable(!enabled);
    }

    private void loadSimulationData() {
        SimulationData[] runs = new SimulationData[5];
        runs[0] = new SimulationData("Run 1", new Double[]{2.5, 3.0, 4.5, 1.5}, new Double[]{5.0, 6.0, 8.0, 3.0}, new Double[]{3.2, 4.0, 5.5, 2.5});
        runs[1] = new SimulationData("Run 2", new Double[]{2.8, 3.5, 4.8, 1.8}, new Double[]{5.5, 6.5, 8.5, 3.5}, new Double[]{3.5, 4.2, 5.8, 2.8});
        runs[2] = new SimulationData("Run 3", new Double[]{3.0, 3.8, 5.0, 2.0}, new Double[]{6.0, 7.0, 9.0, 4.0}, new Double[]{3.8, 4.5, 6.0, 3.0});
        runs[3] = new SimulationData("Run 4", new Double[]{2.6, 3.2, 4.6, 1.6}, new Double[]{5.2, 6.2, 8.2, 3.2}, new Double[]{3.3, 4.1, 5.6, 2.6});
        runs[4] = new SimulationData("Run 5", new Double[]{2.6, 3.2, 4.6, 1.6}, new Double[]{5.2, 6.2, 8.2, 3.2}, new Double[]{3.3, 4.1, 5.6, 2.6});

        VBox graph = Graph.createGraph(runs);
        graphContainer.setTopAnchor(graph, 0.0);
        graphContainer.setBottomAnchor(graph, 0.0);
        graphContainer.setLeftAnchor(graph, 0.0);
        graphContainer.setRightAnchor(graph, 0.0);
        graphContainer.getChildren().setAll(graph);
    }


    private void runSimulation() {

        run_button.setDisable(true);

        DynamicComponents.roads = new BaseRoad[] {
                new BaseRoad(10, new double[] {0,3,5,2}, 4, Cardinal.N, true, false),
                new BaseRoad(10, new double[] {0,0,0,0}, 2, Cardinal.E, true, true),
                new BaseRoad(10, new double[] {0,0,0,0}, 1, Cardinal.S, true, false),
                new BaseRoad(10, new double[] {0,0,0,0}, 2, Cardinal.W)
        };

        DynamicComponents.pedestrian_crossing = new PedestrianCrossing(1, 9.98);

        try {
            StatWrapper sc = new StatWrapper(DynamicComponents.roads);
            System.out.println(sc.run().toString());
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        try {
            StatRoad[] roads = new StatRoad[4];
            for (int i=0; i<4; i++) { roads[i] = new StatRoad(DynamicComponents.roads[i]); }

            // North
            StatCalculator sc1 = new StatCalculator(roads, 0);
            Stats northResult = sc1.run();

            // East
            StatCalculator sc2 = new StatCalculator(roads, 1);
            Stats eastResult = sc2.run();

            // South
            StatCalculator sc3 = new StatCalculator(roads, 2);
            Stats southResult = sc3.run();

            // West
            StatCalculator sc4 = new StatCalculator(roads, 3);
            Stats westResult = sc4.run();

            // Set result data
            Double[] avgWaitTime = {northResult.getAverageWaitTime(), eastResult.getAverageWaitTime(), southResult.getAverageWaitTime(), westResult.getAverageWaitTime()};
            Double[] maxWaitTime = {northResult.getMaxWaitTime(), eastResult.getMaxWaitTime(), southResult.getMaxWaitTime(), westResult.getMaxWaitTime()};
            Double[] maxQueueLength = {northResult.getMaxQueueLength(), eastResult.getMaxQueueLength(), southResult.getMaxQueueLength(), westResult.getMaxQueueLength()};
            SimulationData results = new SimulationData(selectedCell.getItem().getSimName(), avgWaitTime, maxWaitTime, maxQueueLength);
            selectedCell.getItem().setResultsData(results);

            // Set all the result values on UI
            avg_wait_north.setText("Exiting North: " + String.format("%.2f", avgWaitTime[0]));
            avg_wait_east.setText("Exiting East: " + String.format("%.2f", avgWaitTime[1]));
            avg_wait_south.setText("Exiting South: " + String.format("%.2f", avgWaitTime[2]));
            avg_wait_west.setText("Exiting West: " + String.format("%.2f", avgWaitTime[3]));

            max_wait_north.setText("Exiting North: " + String.format("%.2f", maxWaitTime[0]));
            max_wait_east.setText("Exiting East: " + String.format("%.2f", maxWaitTime[1]));
            max_wait_south.setText("Exiting South: " + String.format("%.2f", maxWaitTime[2]));
            max_wait_west.setText("Exiting West: " + String.format("%.2f", maxWaitTime[3]));

            max_queue_north.setText("North: " + String.format("%.2f", maxQueueLength[0]));
            max_queue_east.setText("East: " + String.format("%.2f", maxQueueLength[1]));
            max_queue_south.setText("South: " + String.format("%.2f", maxQueueLength[2]));
            max_queue_west.setText("West: " + String.format("%.2f", maxQueueLength[3]));



        } catch (Exception e) {
            System.out.println(e.toString());
        }

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

        // Load results
        SimulationData results = selectedSim.getResultsData();
        if (results != null) {
            avg_wait_north.setText("Exiting North: " + String.format("%.2f", results.getAvgWaitTime()[0]));
            avg_wait_east.setText("Exiting East: " + String.format("%.2f", results.getAvgWaitTime()[1]));
            avg_wait_south.setText("Exiting South: " + String.format("%.2f", results.getAvgWaitTime()[2]));
            avg_wait_west.setText("Exiting West: " + String.format("%.2f", results.getAvgWaitTime()[3]));

            max_wait_north.setText("Exiting North: " + String.format("%.2f", results.getMaxWaitTime()[0]));
            max_wait_east.setText("Exiting East: " + String.format("%.2f", results.getMaxWaitTime()[1]));
            max_wait_south.setText("Exiting South: " + String.format("%.2f", results.getMaxWaitTime()[2]));
            max_wait_west.setText("Exiting West: " + String.format("%.2f", results.getMaxWaitTime()[3]));

            max_queue_north.setText("North: " + String.format("%.2f", results.getMaxQueueLength()[0]));
            max_queue_east.setText("East: " + String.format("%.2f", results.getMaxQueueLength()[1]));
            max_queue_south.setText("South: " + String.format("%.2f", results.getMaxQueueLength()[2]));
            max_queue_west.setText("West: " + String.format("%.2f", results.getMaxQueueLength()[3]));

        } else {
            // Load empty vals
            avg_wait_north.setText("Exiting North:");
            avg_wait_east.setText("Exiting East:");
            avg_wait_south.setText("Exiting South:");
            avg_wait_west.setText("Exiting West:");

            max_wait_north.setText("Exiting North:");
            max_wait_east.setText("Exiting East:");
            max_wait_south.setText("Exiting South:");
            max_wait_west.setText("Exiting West:");

            max_queue_north.setText("North:");
            max_queue_east.setText("East:");
            max_queue_south.setText("South:");
            max_queue_west.setText("West:");
        }
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
