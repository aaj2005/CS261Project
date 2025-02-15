package com.example;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class PrimaryController {

    private int numSimulations = 0;
    @FXML
    private ListView<SimListItem> simList;

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

                        });

                        box.getChildren().addAll(titleText, deleteButton);
                        setGraphic(box);
                    }


                }

            }
        });
    }

    private void addNewSimulation() {
            int newSimPosition = simList.getItems().size() - 1;
            SimListItem newSimItem = new SimListItem("Simulation " + ++numSimulations);
            simList.getItems().add(newSimPosition, newSimItem);
    }
}
