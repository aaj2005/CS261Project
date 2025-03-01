package com.example;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class SimulationCellFactory extends ListCell<SimListItem> {
    private final SimulationManager simulationManager;

    public SimulationCellFactory(SimulationManager simulationManager) {
        this.simulationManager = simulationManager;
    }

    @Override
    protected void updateItem(SimListItem item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        if (item.getSimName().equals("New Simulation")) {
            setGraphic(createNewSimulationCell());
        } else {
            setGraphic(createRegularSimulationCell(item));
        }
    }

    private HBox createNewSimulationCell() {
        HBox hbox = new HBox(5);
        Text titleText = new Text("New Simulation");
        Button addButton = new Button("+");
        addButton.getStyleClass().add("button");
        addButton.setOnAction(event -> simulationManager.addNewSimulation());
        hbox.getChildren().addAll(titleText, addButton);
        return hbox;
    }

    private HBox createRegularSimulationCell(SimListItem item) {
        HBox box = new HBox(10);
        box.setPrefHeight(75);
        box.setStyle("-fx-alignment: center-left; -fx-padding: 5px;");

        Text titleText = new Text(item.getSimName());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button renameButton = new Button("RENAME");
        renameButton.getStyleClass().add("button-rename");

        Button deleteButton = new Button("DELETE");
        deleteButton.getStyleClass().add("button-delete");

        renameButton.setVisible(false);
        deleteButton.setVisible(false);

        this.setOnMouseEntered(event -> updateButtonVisibility(renameButton, deleteButton, true));
        this.setOnMouseExited(event -> updateButtonVisibility(renameButton, deleteButton, false));

        deleteButton.setOnAction(event -> simulationManager.deleteSimulation(item));
        renameButton.setOnAction(event -> handleRename(item, box));

        box.getChildren().addAll(titleText, spacer, renameButton, deleteButton);
        return box;
    }

    private void updateButtonVisibility(Button renameButton, Button deleteButton, boolean visible) {
        renameButton.setVisible(visible);
        deleteButton.setVisible(visible);
        setStyle(visible ? "-fx-background-color: lightgrey;" : "");
    }

    private void handleRename(SimListItem item, HBox box) {
        TextField renameField = new TextField("");
        renameField.setOnAction(e -> validateAndApplyRename(renameField, item, box));
        renameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validateAndApplyRename(renameField, item, box);
        });
        box.getChildren().set(0, renameField);
        renameField.requestFocus();
    }

    private void validateAndApplyRename(TextField renameField, SimListItem item, HBox box) {
        String newName = renameField.getText().trim();
        item.setSimName(newName);
        Text updatedText = new Text(newName);
        box.getChildren().set(0, updatedText);
    }
}
