package com.example;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.Map;
import java.util.function.Consumer;


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
    private int selectedIndex = -1;
    private SimulationComponents simComponent;

    @FXML
    public void initialize() {
        simulationManager = new SimulationManager(simList.getItems());

        simList.getItems().add(new Simulation("Simulation 1"));
        simList.getItems().add(new Simulation("New Simulation"));

        simList.setCellFactory(param -> new SimulationCellFactory(simulationManager, this));
        simList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Select the first listcell after it has been created (after the initialize function finishes)
        selectedIndex = 0;
        if (!simList.getItems().isEmpty()) {
            simList.getSelectionModel().select(0);

        }
        simList.getSelectionModel().select(0);
        simList.requestFocus();
        simList.refresh();
//        Platform.runLater(() -> {
//            Simulation sim = simList.getItems().get(0);
//
//            if (!simList.getItems().isEmpty()) {
//                simList.getSelectionModel().select(0);
//                simList.requestFocus();
//            }
//        });

        // Trigger the metrics tab to load the simulation data
        metricsTab.setOnSelectionChanged(event -> {
            if (metricsTab.isSelected()) loadSimulationData();
        });

        initialise_choiceboxes();

        // Set listens for inputs that change UI configuration (i.e. number of lanes)
        setDynamicListeners();
        run_button.setOnAction(event -> runSimulation());

        // Create the sim UI component
        simComponent = new SimulationComponents(
                1,1,1,1, false,
                false, false,
                false, false,
                false, false,
                false, false,
                false,false,
                false, false,
                new float[] {0,0,0,0}, new float[] {0,0,0,0}, new float[] {0,0,0,0}, new float[] {0,0,0,0}
        );

        AnchorPane childPane = simComponent.getRoot();
        sim_anchor.getChildren().add(childPane);

        // Clip the anchor pane
        Rectangle clip = new Rectangle();
        clip.setWidth(600);
        clip.setHeight(600);
        sim_anchor.setClip(clip);

    }

    private void togglePedestrianInputs(boolean enabled) {
        lbl_duration.setDisable(!enabled);
        lbl_requests.setDisable(!enabled);
        crossing_duration.setDisable(!enabled);
        crossing_requests.setDisable(!enabled);
    }

    private void loadSimulationData() {
        ObservableList<Simulation> simulations = simList.getItems();

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

        // Fill the runs array with non-null SimulationData objects
        for (Simulation simulation : simulations) {
            SimulationData data = simulation.getResultsData();
            if (data != null) {
                runs[index++] = data;
            }
        }

        VBox graph = Graph.createGraph(runs);
        graphContainer.setTopAnchor(graph, 0.0);
        graphContainer.setBottomAnchor(graph, 0.0);
        graphContainer.setLeftAnchor(graph, 0.0);
        graphContainer.setRightAnchor(graph, 0.0);
        graphContainer.getChildren().setAll(graph);
    }


    private void runSimulation() {

        run_button.setDisable(true);

        simComponent.start_simulation();

        Simulation sim = getSelectedCell().getItem();

        DynamicComponents.roads = new BaseRoad[] {
                new BaseRoad(10, new double[] {sim.getNorth_north_vph(), sim.getNorth_east_vph(), 0, sim.getNorth_west_vph()}, 4, Cardinal.N, sim.getNorth_left_turn(), false),
                new BaseRoad(10, new double[] {sim.getEast_north_vph(), sim.getEast_east_vph(),sim.getEast_south_vph(), 0}, 2, Cardinal.E, sim.getEast_left_turn(), true),
                new BaseRoad(10, new double[] {0,sim.getSouth_east_vph(), sim.getSouth_south_vph(), sim.getSouth_west_vph()}, 1, Cardinal.S, sim.getSouth_left_turn(), false),
                new BaseRoad(10, new double[] {sim.getWest_north_vph(),0,sim.getWest_south_vph(), sim.getWest_west_vph()}, 2, Cardinal.W, sim.getWest_left_turn(), false)
        };

        DynamicComponents.pedestrian_crossing = new PedestrianCrossing(1, 9.98);

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
            SimulationData results = new SimulationData(getSelectedCell().getItem().getSimName(), avgWaitTime, maxWaitTime, maxQueueLength);
            getSelectedCell().getItem().setResultsData(results);

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

        simComponent.stop_simulation();

        sim_anchor.getChildren().clear();

        resetSimulationUI();

        selectedIndex = cell.getIndex();
        selectedCell = getSelectedCell();
        simList.getSelectionModel().select(selectedIndex);
        run_button.setDisable(false);

        if (selectedCell != null) {
            System.out.println("Selecting: " + ((Simulation) selectedCell.getItem()).getSimName());

            if (!selectedCell.getItem().getSimName().equals("New Simulation")) {
                selectedCell.setStyle("-fx-background-color: lightblue; -fx-font-weight: bold;");

            }

            loadParameterValues(selectedCell.getItem());
        }
        System.out.println("Selected cell: " + (selectedCell != null ? selectedCell.getIndex() : "None"));
    }


    public ListCell<Simulation> getSelectedCell() {
        return getCellByIndex(selectedIndex);
    }

    public void setDynamicListeners() {
        // Sets up listeners for input parameters that change the UI configuration (i.e. number of lanes)
        nb_lanes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            Simulation selectedSim = getSelectedCell().getItem();
            selectedSim.setNorthNumLanes(newVal);
            resetSimulationUI();
        });
        n_buslane.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = getSelectedCell().getItem();
            selectedSim.setNorthBusLane(newValue);
            System.out.print("SET value to " + newValue + " for " + selectedSim.getSimName());
            lbl_duration.setText(newValue.toString());
            resetSimulationUI();
        });
        n_leftturn.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = getSelectedCell().getItem();
            selectedSim.setNorthLeftTurn(newValue);
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
        e_leftturn.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = getSelectedCell().getItem();
            selectedSim.setEastLeftTurn(newValue);
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
        s_leftturn.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = getSelectedCell().getItem();
            selectedSim.setSouthLeftTurn(newValue);
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
        w_leftturn.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = getSelectedCell().getItem();
            selectedSim.setWestLeftTurn(newValue);
            resetSimulationUI();
        });
        pc_enabled.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Simulation selectedSim = getSelectedCell().getItem();
            selectedSim.setPedestrianCrossings(newValue);
            togglePedestrianInputs(newValue);
            resetSimulationUI();
        });

        txt_nn.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // When focus is lost
                updateSimulationValue(txt_nn, selectedSim -> selectedSim.setNorth_north_vph(parseInt(txt_nn.getText())));
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

        txt_ee.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                updateSimulationValue(txt_ee, selectedSim -> selectedSim.setEast_east_vph(parseInt(txt_ee.getText())));
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

        txt_ss.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                updateSimulationValue(txt_ss, selectedSim -> selectedSim.setSouth_south_vph(parseInt(txt_ss.getText())));
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

        txt_ww.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                updateSimulationValue(txt_ww, selectedSim -> selectedSim.setWest_west_vph(parseInt(txt_ww.getText())));
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


    public void loadParameterValues(Simulation selectedSim) {
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

        crossing_duration.setText(selectedSim.getDuration_of_crossings().toString());
        crossing_requests.setText(selectedSim.getRequests_per_hour().toString());

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


    public void resetSimulationUI() {
        sim_anchor.getChildren().clear();
        Simulation sim = getSelectedCell().getItem();

        float[] vph_1 = new float[] {sim.getNorth_north_vph().floatValue(), sim.getNorth_east_vph().floatValue(), 0, sim.getNorth_west_vph().floatValue()};
        float[] vph_2 = new float[] {sim.getEast_north_vph().floatValue(), sim.getEast_east_vph().floatValue(), sim.getEast_south_vph().floatValue(), 0};
        float[] vph_3 = new float[] {0, sim.getSouth_east_vph().floatValue(), sim.getSouth_south_vph().floatValue(), sim.getSouth_west_vph().floatValue()};
        float[] vph_4 = new float[] {sim.getWest_north_vph().floatValue(), 0, sim.getWest_south_vph().floatValue(), sim.getWest_west_vph().floatValue()};


        simComponent = new SimulationComponents(
                sim.getNorth_num_lanes(), sim.getEast_num_lanes(), sim.getSouth_num_lanes(), sim.getWest_num_lanes(), sim.getPedestrian_crossings(),
                sim.getNorth_left_turn(), true,
                sim.getEast_left_turn(), true,
                sim.getSouth_left_turn(), true,
                sim.getWest_left_turn(), true,
                sim.getNorth_bus_lane(), sim.getEast_bus_lane(),
                sim.getSouth_bus_lane(), sim.getWest_bus_lane(),
                vph_1, vph_2, vph_3, vph_4
        );
        AnchorPane childPane = simComponent.getRoot();
        sim_anchor.getChildren().add(childPane);
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
}