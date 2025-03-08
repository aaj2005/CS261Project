package com.example;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;
import java.util.function.Consumer;

public class PrimaryController {
    @FXML private ListView<Simulation> simList;
    @FXML private AnchorPane graphContainer;
    @FXML private Tab metricsTab;
    @FXML private Button run_button;
    @FXML private Button pause_button;
    @FXML private Label sim_title;

    // Northbound controls
    @FXML private ChoiceBox<Integer> nb_lanes;
    @FXML private CheckBox n_buslane;
    @FXML private TextField txt_ns, txt_ne, txt_nw;

    // Eastbound controls
    @FXML private ChoiceBox<Integer> eb_lanes;
    @FXML private CheckBox e_buslane;
    @FXML private TextField txt_en, txt_ew, txt_es;

    // Southbound controls
    @FXML private ChoiceBox<Integer> sb_lanes;
    @FXML private CheckBox s_buslane;
    @FXML private TextField txt_se, txt_sn, txt_sw;

    // Westbound controls
    @FXML private ChoiceBox<Integer> wb_lanes;
    @FXML private CheckBox w_buslane;
    @FXML private TextField txt_wn, txt_ws, txt_we;

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

    @FXML private Label junc_score;

    @FXML private AnchorPane sim_anchor;

    private SimulationManager simulationManager;
    private int selectedIndex = -1;
    private SimulationComponents simComponent;

    @FXML
    public void initialize() {


        simulationManager = new SimulationManager(simList.getItems());

        // Add the starting two simulation list items
        simList.getItems().add(new Simulation("Simulation 1"));
        simList.getItems().add(new Simulation("New Simulation"));

        simList.setCellFactory(param -> new SimulationCellFactory(simulationManager, this));
        simList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        RestrictTextFields();

        // Select the first listcell after it has been created
        selectedIndex = 0;
        if (!simList.getItems().isEmpty()) {
            simList.getSelectionModel().select(0);

        }
        simList.getSelectionModel().select(0);
        simList.requestFocus();
        simList.refresh();

        // Trigger the metrics tab to load the simulation data
        metricsTab.setOnSelectionChanged(event -> {
            if (metricsTab.isSelected()) loadSimulationData();
        });

        // Set the valid options fo the choice boxes
        initialise_choiceboxes();

        // Set listeners for inputs that change UI configuration (i.e. number of lanes)
        setDynamicListeners();
        run_button.setOnAction(event -> runSimulation());
        pause_button.setOnAction(event -> stopSimulation());
        pause_button.setDisable(true);

        // Create the sim UI component with default values
        simComponent = new SimulationComponents(
                1,1,1,1, false,
                false, false,
                false, false,
                false, false,
                false, false,
                false,false,
                false, false,
                new float[] {0,0,0,0}, new float[] {0,0,0,0}, new float[] {0,0,0,0}, new float[] {0,0,0,0},
                0,0
        );

        // Attach sim UI to the anchor frame
        AnchorPane childPane = simComponent.getRoot();
        sim_anchor.getChildren().add(childPane);

        // Clip the anchor pane
        Rectangle clip = new Rectangle();
        clip.setWidth(600);
        clip.setHeight(600);
        sim_anchor.setClip(clip);

    }

    private void togglePedestrianInputs(boolean enabled) {
        // Enable or disable the labels and text inputs
        lbl_duration.setDisable(!enabled);
        lbl_requests.setDisable(!enabled);
        crossing_duration.setDisable(!enabled);
        crossing_requests.setDisable(!enabled);
    }

    /*
    * Gets all the results data from each simulation and renders it in the graph container for the metrics tab
    */
    private void loadSimulationData() {
        ObservableList<Simulation> simulations = simList.getItems();

        stopSimulation();
        // Count how many non-null SimulationData objects there are
        int validCount = 0;
        for (Simulation simulation : simulations) {
            if (simulation.getResultsData() != null) {
                validCount++;
            }
        }

        // Create an array to store the non-null SimulationData objects
        SimulationData[] runs = new SimulationData[validCount];
        int index = 0;

        // Fill the array with non-null SimulationData objects
        for (Simulation simulation : simulations) {
            SimulationData data = simulation.getResultsData();
            if (data != null) {
                runs[index++] = data;
            }
        }

        // Create the graphs
        VBox graph = Graph.createGraph(runs);
        graphContainer.setTopAnchor(graph, 0.0);
        graphContainer.setBottomAnchor(graph, 0.0);
        graphContainer.setLeftAnchor(graph, 0.0);
        graphContainer.setRightAnchor(graph, 0.0);
        graphContainer.getChildren().setAll(graph);
    }


    private void runSimulation() {
        SetToPlayMode();
        Simulation sim = getSelectedCell().getItem();

        // Calculate where the left and right turn lanes are needed
        boolean north_left_turn = sim.getNorth_east_vph() > 0;
        boolean north_right_turn = sim.getNorth_west_vph() > 0;

        boolean east_left_turn = sim.getEast_south_vph() > 0;
        boolean east_right_turn = sim.getEast_north_vph() > 0;

        boolean south_left_turn = sim.getSouth_east_vph() > 0;
        boolean south_right_turn = sim.getSouth_west_vph() > 0;

        boolean west_left_turn = sim.getWest_north_vph() > 0;
        boolean west_right_turn = sim.getWest_south_vph() > 0;

        // Load values into the config for the stats calculation
        DynamicComponents.roads = new BaseRoad[] {
            new BaseRoad(10, new double[] {0, sim.getNorth_east_vph(), sim.getNorth_south_vph(), sim.getNorth_west_vph()},
            sim.getNorth_num_lanes(), Cardinal.N, north_left_turn, north_right_turn),
            new BaseRoad(10, new double[] {sim.getEast_north_vph(), 0,sim.getEast_south_vph(), sim.getEast_west_vph()},
            sim.getEast_num_lanes(), Cardinal.E, east_left_turn, east_right_turn),
            new BaseRoad(10, new double[] {sim.getSouth_north_vph(), sim.getSouth_east_vph(), 0, sim.getSouth_west_vph()},
            sim.getSouth_num_lanes(), Cardinal.S, south_left_turn, south_right_turn),
            new BaseRoad(10, new double[] {sim.getWest_north_vph(), sim.getWest_east_vph(), sim.getWest_south_vph(), 0},
            sim.getWest_num_lanes(), Cardinal.W, west_left_turn, west_right_turn),
        };

        if (sim.getPedestrian_crossings()) {
            DynamicComponents.pedestrian_crossing = new PedestrianCrossing(sim.getDuration_of_crossings(), sim.getRequests_per_hour());
        } else {
            DynamicComponents.pedestrian_crossing = new PedestrianCrossing(0, 0);
        }

        try {
            StatRoad[] roads = new StatRoad[4];
            for (int i=0; i<4; i++) { roads[i] = new StatRoad(DynamicComponents.roads[i]); }

            // North lane calculation
            StatCalculator sc1 = new StatCalculator(roads, 0);
            Stats northResult = sc1.run();

            // East lane calculation
            StatCalculator sc2 = new StatCalculator(roads, 1);
            Stats eastResult = sc2.run();

            // South lane calculation
            StatCalculator sc3 = new StatCalculator(roads, 2);
            Stats southResult = sc3.run();

            // West lane calculation
            StatCalculator sc4 = new StatCalculator(roads, 3);
            Stats westResult = sc4.run();

            // Set result data
            Double[] maxWaitTime = {northResult.getAverageWaitTime(), eastResult.getAverageWaitTime(), southResult.getAverageWaitTime(), westResult.getAverageWaitTime()};
            Double[] avgWaitTime = {northResult.getMaxWaitTime(), eastResult.getMaxWaitTime(), southResult.getMaxWaitTime(), westResult.getMaxWaitTime()};
            Double[] maxQueueLength = {northResult.getMaxQueueLength(), eastResult.getMaxQueueLength(), southResult.getMaxQueueLength(), westResult.getMaxQueueLength()};
            SimulationData results = new SimulationData(getSelectedCell().getItem().getSimName(), avgWaitTime, maxWaitTime, maxQueueLength, (northResult.getOverallScore() + eastResult.getOverallScore() + southResult.getOverallScore() + westResult.getOverallScore())/4.0);
            getSelectedCell().getItem().setResultsData(results);

            // Set all the result values on UI
            avg_wait_north.setText("Exiting South: " + String.format("%.2f", avgWaitTime[0]));
            avg_wait_east.setText("Exiting West: " + String.format("%.2f", avgWaitTime[1]));
            avg_wait_south.setText("Exiting North: " + String.format("%.2f", avgWaitTime[2]));
            avg_wait_west.setText("Exiting East: " + String.format("%.2f", avgWaitTime[3]));

            max_wait_north.setText("Exiting South: " + String.format("%.2f", maxWaitTime[0]));
            max_wait_east.setText("Exiting West: " + String.format("%.2f", maxWaitTime[1]));
            max_wait_south.setText("Exiting North: " + String.format("%.2f", maxWaitTime[2]));
            max_wait_west.setText("Exiting East: " + String.format("%.2f", maxWaitTime[3]));

            max_queue_north.setText("North: " + String.format("%.0f", maxQueueLength[0]));
            max_queue_east.setText("East: " + String.format("%.0f", maxQueueLength[1]));
            max_queue_south.setText("South: " + String.format("%.0f", maxQueueLength[2]));
            max_queue_west.setText("West: " + String.format("%.0f", maxQueueLength[3]));

            junc_score.setText(String.format("%.2f", northResult.getOverallScore()));

        } catch (Exception e) {
            // Show a warning alert if the input parameters for the stats functions aren't valid
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Parameters Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            SetToPauseMode();
            return;
        }

        simComponent.start_simulation();
    }

    public void stopSimulation() {
        simComponent.stop_simulation();
        SetToPauseMode();
    }

    public void setSelectedCell(ListCell<Simulation> cell) {
        ListCell<Simulation> selectedCell = getSelectedCell();
        if (selectedCell == cell) {
            return;
        }

        if (selectedCell != null) {
            System.out.println("Deselecting: " + selectedCell.getIndex());
        }
        if (selectedCell != null && selectedCell.getItem() != null) {
            System.out.println("SAVING PARAM FOR SIM: " + cell.getItem().getSimName());
            selectedCell.setStyle("");
            Simulation selectedSim = selectedCell.getItem();
            selectedSim.setNumberParameters(
                    Integer.valueOf(txt_ns.getText()),
                    Integer.valueOf(txt_ne.getText()),
                    Integer.valueOf(txt_nw.getText()),
                    Integer.valueOf(txt_en.getText()),
                    Integer.valueOf(txt_ew.getText()),
                    Integer.valueOf(txt_es.getText()),
                    Integer.valueOf(txt_se.getText()),
                    Integer.valueOf(txt_sn.getText()),
                    Integer.valueOf(txt_sw.getText()),
                    Integer.valueOf(txt_wn.getText()),
                    Integer.valueOf(txt_ws.getText()),
                    Integer.valueOf(txt_we.getText()),
                    Integer.valueOf(crossing_duration.getText()),
                    Integer.valueOf(crossing_requests.getText())
            );
        }

        stopSimulation();

        sim_anchor.getChildren().clear();

        resetSimulationUI();

        selectedIndex = cell.getIndex();
        selectedCell = getSelectedCell();
        simList.getSelectionModel().select(selectedIndex);
        SetToPauseMode();

        if (selectedCell != null) {
            System.out.println("Selecting: " + ((Simulation) selectedCell.getItem()).getSimName());

            if (!selectedCell.getItem().getSimName().equals("New Simulation")) {
                selectedCell.setStyle("-fx-background-color: lightblue; -fx-font-weight: bold;");

            }

            loadParameterValues(selectedCell.getItem());
        }
        System.out.println("Selected cell: " + (selectedCell != null ? selectedCell.getIndex() : "None"));
    }

    /*
     * Utility function to simplify getting the current selected list cell
     */
    public ListCell<Simulation> getSelectedCell() {
        return getCellByIndex(selectedIndex);
    }

    /*
     * Sets up listeners for input parameters that change the UI configuration (i.e. number of lanes)
     */
    public void setDynamicListeners() {

        nb_lanes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            Simulation selectedSim = getSelectedCell().getItem();
            selectedSim.setNorthNumLanes(newVal);
            resetSimulationUI();
        });
        n_buslane.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = getSelectedCell().getItem();
            selectedSim.setNorthBusLane(newValue);
            resetSimulationUI();
        });
        eb_lanes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            Simulation selectedSim = getSelectedCell().getItem();
            selectedSim.setEastNumLanes(newVal);
            resetSimulationUI();
        });
        e_buslane.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = getSelectedCell().getItem();
            selectedSim.setEastBusLane(newValue);
            resetSimulationUI();
        });
        sb_lanes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            Simulation selectedSim = getSelectedCell().getItem();
            selectedSim.setSouthNumLanes(newVal);
            resetSimulationUI();
        });
        s_buslane.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = getSelectedCell().getItem();
            selectedSim.setSouthBusLane(newValue);
            resetSimulationUI();
        });
        wb_lanes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            Simulation selectedSim = getSelectedCell().getItem();
            selectedSim.setWestNumLanes(newVal);
            resetSimulationUI();
        });
        w_buslane.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = getSelectedCell().getItem();
            selectedSim.setWestBusLane(newValue);
            resetSimulationUI();
        });
        pc_enabled.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = getSelectedCell().getItem();
            selectedSim.setPedestrianCrossings(newValue);
            togglePedestrianInputs(newValue);
            resetSimulationUI();
        });

        txt_ns.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // When focus is lost
                updateSimulationValue(txt_ns, selectedSim -> selectedSim.setNorth_south_vph(parseInt(txt_ns.getText())));
            }
        });

        txt_ne.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                updateSimulationValue(txt_ne, selectedSim -> selectedSim.setNorth_east_vph(parseInt(txt_ne.getText())));
            }
        });

        txt_nw.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                updateSimulationValue(txt_nw, selectedSim -> selectedSim.setNorth_west_vph(parseInt(txt_nw.getText())));
            }
        });

        txt_en.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                updateSimulationValue(txt_en, selectedSim -> selectedSim.setEast_north_vph(parseInt(txt_en.getText())));
            }
        });

        txt_ew.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                updateSimulationValue(txt_ew, selectedSim -> selectedSim.setEast_west_vph(parseInt(txt_ew.getText())));
            }
        });

        txt_es.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                updateSimulationValue(txt_es, selectedSim -> selectedSim.setEast_south_vph(parseInt(txt_es.getText())));
            }
        });

        txt_se.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                updateSimulationValue(txt_se, selectedSim -> selectedSim.setSouth_east_vph(parseInt(txt_se.getText())));
            }
        });

        txt_sn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                updateSimulationValue(txt_sn, selectedSim -> selectedSim.setSouth_north_vph(parseInt(txt_sn.getText())));
            }
        });

        txt_sw.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                updateSimulationValue(txt_sw, selectedSim -> selectedSim.setSouth_west_vph(parseInt(txt_sw.getText())));
            }
        });

        txt_wn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                updateSimulationValue(txt_wn, selectedSim -> selectedSim.setWest_north_vph(parseInt(txt_wn.getText())));
            }
        });

        txt_ws.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                updateSimulationValue(txt_ws, selectedSim -> selectedSim.setWest_south_vph(parseInt(txt_ws.getText())));
            }
        });

        txt_we.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                updateSimulationValue(txt_we, selectedSim -> selectedSim.setWest_east_vph(parseInt(txt_we.getText())));
            }
        });

        crossing_duration.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                updateSimulationValue(crossing_duration, selectedSim -> selectedSim.setDuration_of_crossings(parseInt(crossing_duration.getText())));
            }
        });

        crossing_requests.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                updateSimulationValue(crossing_requests, selectedSim -> selectedSim.setRequests_per_hour(parseInt(crossing_requests.getText())));
            }
        });
    }

    private void updateSimulationValue(TextField textField, Consumer<Simulation> updateAction) {
        if (!textField.getText().isEmpty()) {
            Simulation selectedSim = getSelectedCell().getItem();
            if (selectedSim != null) {
                updateAction.accept(selectedSim);
                resetSimulationUI();
            }
        }
    }

    private int parseInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0; // Default to 0 if input is invalid
        }
    }

    /*
     * Load the values from the simulation object in the UI elements, when switching between simulations
     */
    public void loadParameterValues(Simulation selectedSim) {
        sim_title.setText(selectedSim.getSimName().toUpperCase());

        nb_lanes.setValue(selectedSim.getNorth_num_lanes());
        n_buslane.setSelected(selectedSim.getNorth_bus_lane());
        txt_ns.setText(selectedSim.getNorth_south_vph().toString());
        txt_ne.setText(selectedSim.getNorth_east_vph().toString());
        txt_nw.setText(selectedSim.getNorth_west_vph().toString());

        eb_lanes.setValue(selectedSim.getEast_num_lanes());
        e_buslane.setSelected(selectedSim.getEast_bus_lane());
        txt_ew.setText(selectedSim.getEast_west_vph().toString());
        txt_en.setText(selectedSim.getEast_north_vph().toString());
        txt_es.setText(selectedSim.getEast_south_vph().toString());

        sb_lanes.setValue(selectedSim.getSouth_num_lanes());
        s_buslane.setSelected(selectedSim.getSouth_bus_lane());
        txt_se.setText(selectedSim.getSouth_east_vph().toString());
        txt_sn.setText(selectedSim.getSouth_north_vph().toString());
        txt_sw.setText(selectedSim.getSouth_west_vph().toString());

        wb_lanes.setValue(selectedSim.getWest_num_lanes());
        w_buslane.setSelected(selectedSim.getWest_bus_lane());
        txt_wn.setText(selectedSim.getWest_north_vph().toString());
        txt_ws.setText(selectedSim.getWest_south_vph().toString());
        txt_we.setText(selectedSim.getWest_east_vph().toString());

        pc_enabled.setSelected(selectedSim.getPedestrian_crossings());
        togglePedestrianInputs(pc_enabled.isSelected());

        crossing_duration.setText(selectedSim.getDuration_of_crossings().toString());
        crossing_requests.setText(selectedSim.getRequests_per_hour().toString());

        // Load results if the simulation has been previously run
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

            junc_score.setText(String.format("%.2f", results.getScore()));

        } else {
            // Otherwise, clear the results labels to make sure they are empty
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

            junc_score.setText("");
        }
    }

    /*
     * Sets the valid options and starting values for the lane number choice boxes
     */
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

    /*
     * Reinitialise the simulation UI component when changing between simulations or changing parameters
     */
    public void resetSimulationUI() {
        sim_anchor.getChildren().clear();
        Simulation sim = getSelectedCell().getItem();

        float[] vph_1 = new float[] {0, sim.getNorth_east_vph().floatValue(), sim.getNorth_south_vph().floatValue(), sim.getNorth_west_vph().floatValue()};
        float[] vph_2 = new float[] {sim.getEast_north_vph().floatValue(), 0, sim.getEast_south_vph().floatValue(), sim.getEast_west_vph().floatValue()};
        float[] vph_3 = new float[] {sim.getSouth_north_vph().floatValue(), sim.getSouth_east_vph().floatValue(), 0, sim.getSouth_west_vph().floatValue()};
        float[] vph_4 = new float[] {sim.getWest_north_vph().floatValue(), sim.getWest_east_vph().floatValue(), sim.getWest_south_vph().floatValue(), 0};

        System.out.println(Arrays.toString(vph_1));
        System.out.println(Arrays.toString(vph_2));
        System.out.println(Arrays.toString(vph_3));
        System.out.println(Arrays.toString(vph_4));
        System.out.println();
        // Calculate whether left and right turn lanes are needed
        boolean north_left_turn = sim.getNorth_east_vph() > 0;
        boolean north_right_turn = sim.getNorth_west_vph() > 0;

        boolean east_left_turn = sim.getEast_south_vph() > 0;
        boolean east_right_turn = sim.getEast_north_vph() > 0;

        boolean south_left_turn = sim.getSouth_east_vph() > 0;
        boolean south_right_turn = sim.getSouth_west_vph() > 0;

        boolean west_left_turn = sim.getWest_north_vph() > 0;
        boolean west_right_turn = sim.getWest_south_vph() > 0;

        double crossing_rph = Double.valueOf(crossing_requests.getText());
        double crossing_dur = Double.valueOf(crossing_duration.getText());

        simComponent = new SimulationComponents(
                sim.getNorth_num_lanes(), sim.getEast_num_lanes(), sim.getSouth_num_lanes(), sim.getWest_num_lanes(), sim.getPedestrian_crossings(),
                north_left_turn, north_right_turn,
                east_left_turn, east_right_turn,
                south_left_turn, south_right_turn,
                west_left_turn, west_right_turn,
                sim.getNorth_bus_lane(), sim.getEast_bus_lane(),
                sim.getSouth_bus_lane(), sim.getWest_bus_lane(),
                vph_1, vph_2, vph_3, vph_4,
                (sim.getPedestrian_crossings()) ? crossing_rph : 0, (sim.getPedestrian_crossings()) ? crossing_dur : 0
        );

        // Anchor the simulation to the anchor pane on the main UI
        AnchorPane childPane = simComponent.getRoot();
        sim_anchor.getChildren().add(childPane);
        SetToPauseMode();
    }

    private ListCell<Simulation> getCellByIndex(int index) {
        for (Node node : simList.lookupAll(".list-cell")) {
            if (node instanceof ListCell) {
                ListCell<Simulation> cell = (ListCell<Simulation>) node;
                if (cell.getIndex() == index) {
                    return cell;
                }
            }
        }
        return null; // Return null if the cell isn't currently rendered
    }

    private void SetToPlayMode() {
        run_button.setDisable(true);
        pause_button.setDisable(false);
        pause_button.requestFocus();
    }

    private void SetToPauseMode() {
        run_button.setDisable(false);
        pause_button.setDisable(true);
    }

    /*
     * Add input validation to the text fields in the main UI
     */
    private void RestrictTextFields() {

        // Truncate simulation title with an ellipsis if it's too long
        sim_title.setTextOverrun(OverrunStyle.ELLIPSIS);
        sim_title.setMaxWidth(400);
        sim_title.setWrapText(false);

        // Restrict all the text fields
//        Validator.restrictToNumbers(txt_ns, 3657);
//        Validator.restrictToNumbers(txt_ne, 3657);
//        Validator.restrictToNumbers(txt_nw, 3657);
//
//        Validator.restrictToNumbers(txt_en, 3657);
//        Validator.restrictToNumbers(txt_ew, 3657);
//        Validator.restrictToNumbers(txt_es, 3657);
//
//        Validator.restrictToNumbers(txt_se, 3657);
//        Validator.restrictToNumbers(txt_sw, 3657);
//        Validator.restrictToNumbers(txt_sn, 3657);
//
//        Validator.restrictToNumbers(txt_wn, 3657);
//        Validator.restrictToNumbers(txt_ws, 3657);
//        Validator.restrictToNumbers(txt_we, 3657);
//
//        Validator.restrictToNumbers(crossing_duration, 3657);
//        Validator.restrictToNumbers(crossing_duration, 3657);
    }
}