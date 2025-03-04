package com.example;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

public class SimulationCellFactory extends ListCell<Simulation> {
    private final SimulationManager simulationManager;
    private final PrimaryController controller;

    public SimulationCellFactory(SimulationManager simulationManager, PrimaryController controller) {
        this.simulationManager = simulationManager;
        this.controller = controller;
    }

    @Override
    protected void updateItem(Simulation item, boolean empty) {
        super.updateItem(item, empty);
        System.out.println("updateItem called with: " + (item == null ? "NULL" : item.getSimName()) + ", empty: " + empty);
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

        Button addButton = new Button("add");
        addButton.getStyleClass().add("button");
        addButton.setOnAction(event -> simulationManager.addNewSimulation());
        hbox.getChildren().addAll(titleText, addButton);
        return hbox;
    }

    private HBox createRegularSimulationCell(Simulation item) {
        HBox hbox = new HBox(10);
        hbox.setPrefHeight(75);
        hbox.setPrefHeight(75);
        hbox.setStyle("-fx-alignment: center-left; -fx-padding: 5px;");

        Text titleText = new Text(item.getSimName());


        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button renameButton = new Button();
        FontIcon renameIcon = new FontIcon(FontAwesomeSolid.PENCIL_ALT);
        renameIcon.setIconSize(30);  // Set size
//        renameIcon.setStyle("-fx-icon-color: white;");  // Set color to white
        renameIcon.setIconColor(Color.WHITE);
        renameButton.setGraphic(renameIcon);
        renameButton.getStyleClass().add("button-rename");

        Button deleteButton = new Button();
        FontIcon deleteIcon = new FontIcon(FontAwesomeSolid.TRASH);
        deleteIcon.setIconSize(30);  // Set size
        deleteIcon.setIconColor(Color.WHITE);
//        deleteIcon.setStyle("-fx-icon-color: white;");  // Set color to white
        deleteButton.setGraphic(deleteIcon);
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

        if (controller.getSelectedCell() != this) {
            setStyle(visible ? "-fx-background-color: lightgrey;" : "");
        } else {
            setStyle("");
        }
    }

    private void handleRename(Simulation item, HBox box) {
        TextField renameField = new TextField();
        renameField.setOnAction(e -> validateAndApplyRename(renameField, item, box));
        renameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validateAndApplyRename(renameField, item, box);
        });

        box.getChildren().set(0, renameField);
        renameField.requestFocus();
    }

    private void validateAndApplyRename(TextField renameField, Simulation item, HBox box) {
        String newName = renameField.getText().trim();
        if (!newName.isEmpty()) {
            item.setSimName(newName);
        }
        Text updatedText = new Text(item.getSimName());
        controller.getSim_title().setText(item.getSimName().toUpperCase());
        box.getChildren().set(0, updatedText);

        // Force update to refresh UI
        setText(null);
    }
}
