package com.example;

import javafx.geometry.Pos;
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
            setStyle("-fx-background-color: transparent; -fx-border-width: 0;"); // Remove background and borders
            setPrefHeight(0);  // Make the empty cell height 0 to remove gaps
            return;
        }

        // Reset styling for non-empty cells
        setStyle("");


        if (item.getSimName().equals("New Simulation")) {
            setPrefHeight(50);
            setGraphic(createNewSimulationCell());
            getStyleClass().add("new-simulation");

        } else {
            setPrefHeight(75);

            setGraphic(createRegularSimulationCell(item));
        }

//        this.setOnMouseClicked(event -> {
//            if (!item.getSimName().equals("New Simulation")) {
//                controller.setSelectedCell(this);
//            }
//        });

        this.setOnMouseClicked(event -> {
            // If the clicked item is a regular cell (not "New Simulation"), apply selected styling manually
            if (!item.getSimName().equals("New Simulation")) {
                // Manually trigger the selection state based on your tracking mechanism
                controller.setSelectedCell(this);  // Assuming this method tracks selected cells and updates styling

                // Add the "selected" style class (or your own mechanism for selected styling)
                getStyleClass().add("selected");
                setStyle("-fx-background-color: lightblue; -fx-font-weight: bold;");

                // Optionally remove the "selected" style from other cells if needed

            } else {
                // Handle clicking on the "New Simulation" cell if needed (no selection change here)
                controller.setSelectedCell(this);
            }
        });
    }

    private HBox createNewSimulationCell() {
        HBox hbox = new HBox(10);
        hbox.setPrefHeight(40);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Text titleText = new Text("New Simulation");

        VBox textContainer = new VBox(titleText);
        textContainer.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addButton = new Button();
        FontIcon addIcon = new FontIcon(FontAwesomeSolid.PLUS);
        addIcon.setIconSize(30);  // Set size
        addIcon.setIconColor(Color.WHITE);
        addButton.setGraphic(addIcon);

        addButton.getStyleClass().add("button");
        addButton.setOnAction(event -> simulationManager.addNewSimulation());

        hbox.getChildren().addAll(textContainer, spacer, addButton);



        return hbox;
    }

    private HBox createRegularSimulationCell(Simulation item) {
        HBox hbox = new HBox(10);
        hbox.setPrefHeight(75);
        hbox.setStyle("-fx-alignment: center-left; -fx-padding: 5px;");

        Text titleText = new Text(item.getSimName());


        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button renameButton = new Button();
        FontIcon renameIcon = new FontIcon(FontAwesomeSolid.PENCIL_ALT);
        renameIcon.setIconSize(30);  // Set size
        renameIcon.setIconColor(Color.WHITE);
        renameButton.setGraphic(renameIcon);
        renameButton.getStyleClass().add("button-rename");

        Button deleteButton = new Button();
        FontIcon deleteIcon = new FontIcon(FontAwesomeSolid.TRASH);
        deleteIcon.setIconSize(30);  // Set size
        deleteIcon.setIconColor(Color.WHITE);
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
