package com.example;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class SimulationCellFactory extends ListCell<SimListItem> {
    private final SimulationManager simulationManager;
    private final PrimaryController controller;

    public SimulationCellFactory(SimulationManager simulationManager, PrimaryController controller) {
        this.simulationManager = simulationManager;
        this.controller = controller;
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

        this.setOnMouseClicked(event -> {
            if (!item.getSimName().equals("New Simulation")) {
                controller.setSelectedCell(this);
            }
        });
    }

    private HBox createNewSimulationCell() {
        HBox hbox = new HBox(10);
        hbox.setPrefHeight(20);
        Text titleText = new Text("New Simulation");
        Button addButton = new Button("+");
        addButton.getStyleClass().add("button");
        addButton.setOnAction(event -> simulationManager.addNewSimulation());
        hbox.getChildren().addAll(titleText, addButton);
        return hbox;
    }

    private HBox createRegularSimulationCell(SimListItem item) {
        HBox hbox = new HBox(10);
        hbox.setPrefHeight(75);
       hbox.setPrefHeight(75);
       hbox.setStyle("-fx-alignment: center-left; -fx-padding: 5px;");

        Text titleText = new Text(item.getSimName());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button renameButton = new Button("RENAME");
        renameButton.getStyleClass().add("button-rename");

        Button deleteButton = new Button("DELETE");
        deleteButton.getStyleClass().add("button-delete");

        renameButton.setVisible(false);
        deleteButton.setVisible(false);

        hbox.setOnMouseEntered(event -> updateButtonVisibility(renameButton, deleteButton, true));
        hbox.setOnMouseExited(event -> updateButtonVisibility(renameButton, deleteButton, false));

        deleteButton.setOnAction(event -> simulationManager.deleteSimulation(item));
        renameButton.setOnAction(event -> handleRename(item, hbox));

        hbox.getChildren().addAll(titleText, spacer, renameButton, deleteButton);
        return hbox;
    }

    private void updateButtonVisibility(Button renameButton, Button deleteButton, boolean visible) {
        renameButton.setVisible(visible);
        deleteButton.setVisible(visible);
        setStyle(visible ? "-fx-background-color: lightgrey;" : "");
    }

    private void handleRename(SimListItem item, HBox box) {
        TextField renameField = new TextField(item.getSimName());
        renameField.setOnAction(e -> validateAndApplyRename(renameField, item, box));
        renameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validateAndApplyRename(renameField, item, box);
        });

        box.getChildren().set(0, renameField);
        renameField.requestFocus();
    }

    private void validateAndApplyRename(TextField renameField, SimListItem item, HBox box) {
        String newName = renameField.getText().trim();
        if (!newName.isEmpty()) {
            item.setSimName(newName);
        }
        Text updatedText = new Text(item.getSimName());
        box.getChildren().set(0, updatedText);

        // Force update to refresh UI
        setText(null);
    }
}
