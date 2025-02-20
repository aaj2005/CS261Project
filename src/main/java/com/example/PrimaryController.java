package com.example;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Tab;
import javafx.scene.text.Text;

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
    public void initialize() {
        SimListItem newSimulationItem = new SimListItem("New Simulation");

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
                        // Special "New simulation" item: display only the title and a button
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
    }

    private void addNewSimulation() {
        int newSimPosition = simList.getItems().size() - 1;
        SimListItem newSimItem = new SimListItem("Simulation " + ++numSimulations);
        simList.getItems().add(newSimPosition, newSimItem);
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
}
