package com.example;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.TextFormatter;

public class PrimaryController {

    private int numSimulations = 0;
    private ListCell<SimListItem> selectedSimItem;

    @FXML
    private ListView<SimListItem> simList;

    @FXML
    private AnchorPane graphContainer;

    @FXML
    private Tab metricsTab;

    @FXML
    private Label configuration_label;

    @FXML
    private Button run_button;

    // Northbound input parameters

    @FXML
    private ChoiceBox<Integer> nb_lanes;
    @FXML
    private CheckBox n_buslane;
    @FXML
    private CheckBox n_leftturn;
    @FXML
    private TextField txt_nn;
    @FXML
    private TextField txt_ne;
    @FXML
    private TextField txt_nw;

    // Eastbound input parameters
    @FXML
    private ChoiceBox<Integer> eb_lanes;
    @FXML
    private CheckBox e_buslane;
    @FXML
    private CheckBox e_leftturn;
    @FXML
    private TextField txt_en;
    @FXML
    private TextField txt_ee;
    @FXML
    private TextField txt_es;

    // Southbound input parameters
    @FXML
    private ChoiceBox<Integer> sb_lanes;
    @FXML
    private CheckBox s_buslane;
    @FXML
    private CheckBox s_leftturn;
    @FXML
    private TextField txt_se;
    @FXML
    private TextField txt_ss;
    @FXML
    private TextField txt_sw;

    // Westbound input parameters
    @FXML
    private ChoiceBox<Integer> wb_lanes;
    @FXML
    private CheckBox w_buslane;
    @FXML
    private CheckBox w_leftturn;
    @FXML
    private TextField txt_wn;
    @FXML
    private TextField txt_ws;
    @FXML
    private TextField txt_ww;

    // Pedestrian crossing input parameters
    @FXML
    private CheckBox pc_enabled;
    @FXML
    private TextField crossing_duration;
    @FXML
    private TextField crossing_requests;
    @FXML
    private Label lbl_duration;
    @FXML
    private Label lbl_requests;

    @FXML
    public void initialize() {

        simList.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        SimListItem newSimulationItem = new SimListItem("New Simulation");

        nb_lanes.getItems().addAll(1, 2, 3, 4, 5);
        sb_lanes.getItems().addAll(1, 2, 3, 4, 5);
        eb_lanes.getItems().addAll(1, 2, 3, 4, 5);
        wb_lanes.getItems().addAll(1, 2, 3, 4, 5);

        restrictTextFieldsToNumbers();
        simList.getItems().add(newSimulationItem);

        simList.setCellFactory(param -> new ListCell<SimListItem>() {
            @Override
            protected void updateItem(SimListItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (item.getSimName().equals("New Simulation")) {
                        // New simulation item: to add new sims
                        HBox hbox = new HBox(10);
                        Text titleText = new Text(item.getSimName());
                        Button addButton = new Button("+");

                        // Action for the "Add Simulation" button
                        addButton.setOnAction(event -> addNewSimulation());

                        hbox.getChildren().addAll(titleText, addButton);
                        setGraphic(hbox);
                    } else {
                        HBox box = new HBox(10);
                        Text titleText = new Text(item.getSimName());
                        Button deleteButton = new Button("Delete");

                        deleteButton.setOnAction(event -> {
                            simList.getItems().remove(item);

                            if (selectedSimItem != null && selectedSimItem.getItem().equals(item)) {
                                selectedSimItem.setStyle("");
                                selectedSimItem = null;
                            }

                        });

                        box.getChildren().addAll(titleText, deleteButton);
                        setGraphic(box);
                    }

                    this.setOnMouseClicked(event -> {
                        if (!item.getSimName().equals("New Simulation")) {
                            simList.getItems().forEach(simItem -> {
                                if (selectedSimItem != null) {
                                    selectedSimItem.setStyle("");

                                }

                                setStyle("-fx-background-color: lightblue;");

                                selectedSimItem = this;
                            });

                            setStyle("-fx-background-color: lightblue;");

                            // For later use, load in details from simulation to text labels here
                            // loadSimulationDetails(item);
                        }

                    });

                }

            }
        });

        simList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        simList.getSelectionModel().clearSelection();

        metricsTab.setOnSelectionChanged(event -> {
            if (metricsTab.isSelected()) {
                loadSimulationData();
            }
        });

        pc_enabled.selectedProperty().addListener((obs, oldVal, newVal) -> {
            boolean enabled = newVal;
            lbl_duration.setDisable(!enabled);
            lbl_requests.setDisable(!enabled);
            crossing_duration.setDisable(!enabled);
            crossing_requests.setDisable(!enabled);
        });

        run_button.setOnAction(event -> {
            run_button.setDisable(true);

            // Get all parameter values - maybe check if they are empty?
            // if (hasInvalidInput()) {
            // System.out.println("Error: Please fill in all parameters.");
            // run_button.setDisable(false); // Re-enable button if input is invalid
            // return;
            // }

            // Call simulation function here
        });

    }

    private void addNewSimulation() {
        int newSimPosition = simList.getItems().size() - 1;
        SimListItem newSimItem = new SimListItem("Simulation " + ++numSimulations);
        simList.getItems().add(newSimPosition, newSimItem);
    }

    private void loadSimulationData() {
        SimulationData[] runs = new SimulationData[5];
        runs[0] = new SimulationData("Run 1", new double[] { 2.5, 3.0, 4.5, 1.5 }, new double[] { 5.0, 6.0, 8.0, 3.0 },
                new double[] { 3.2, 4.0, 5.5, 2.5 });
        runs[1] = new SimulationData("Run 2", new double[] { 2.8, 3.5, 4.8, 1.8 }, new double[] { 5.5, 6.5, 8.5, 3.5 },
                new double[] { 3.5, 4.2, 5.8, 2.8 });
        runs[2] = new SimulationData("Run 3", new double[] { 3.0, 3.8, 5.0, 2.0 }, new double[] { 6.0, 7.0, 9.0, 4.0 },
                new double[] { 3.8, 4.5, 6.0, 3.0 });
        runs[3] = new SimulationData("Run 4", new double[] { 2.6, 3.2, 4.6, 1.6 }, new double[] { 5.2, 6.2, 8.2, 3.2 },
                new double[] { 3.3, 4.1, 5.6, 2.6 });
        runs[4] = new SimulationData("Run 5", new double[] { 2.6, 3.2, 4.6, 1.6 }, new double[] { 5.2, 6.2, 8.2, 3.2 },
                new double[] { 3.3, 4.1, 5.6, 2.6 });

        VBox graph = Graph.createGraph(runs);
        graphContainer.setTopAnchor(graph, 0.0);
        graphContainer.setBottomAnchor(graph, 0.0);
        graphContainer.setLeftAnchor(graph, 0.0);
        graphContainer.setRightAnchor(graph, 0.0);
        graphContainer.getChildren().setAll(graph);
    }

    public void restrictTextFieldsToNumbers() {
        restrictToNumbers(txt_nn);
        restrictToNumbers(txt_ne);
        restrictToNumbers(txt_nw);
        restrictToNumbers(txt_en);
        restrictToNumbers(txt_ee);
        restrictToNumbers(txt_es);
        restrictToNumbers(txt_se);
        restrictToNumbers(txt_ss);
        restrictToNumbers(txt_sw);
        restrictToNumbers(txt_wn);
        restrictToNumbers(txt_ws);
        restrictToNumbers(txt_ww);
        restrictToNumbers(crossing_duration);
        restrictToNumbers(crossing_requests);
    }

    public void restrictToNumbers(TextField textField) {
        textField.setTextFormatter(new TextFormatter<>(change -> (change.getText().matches("[0-9]*")) ? change : null));
    }

}
