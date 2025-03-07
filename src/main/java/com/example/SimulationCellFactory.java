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

    /*
     * Handles when cell items in the simulation list are updated on the main UI
     */
    @Override
    protected void updateItem(Simulation item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            setStyle("-fx-background-color: transparent; -fx-border-width: 0;"); // Remove background and borders
            setPrefHeight(0);  // Make the empty cell height 0 to remove gaps
            return;
        }

        if (controller.getSelectedCell() == this) {
            setStyle("-fx-background-color: lightblue;");
        } else {
            setStyle("");
        }


        if (item.getSimName().equals("New Simulation")) {
            setPrefHeight(50);
            setGraphic(createNewSimulationCell());
            getStyleClass().add("new-simulation");

        } else {
            setPrefHeight(75);
            setGraphic(createRegularSimulationCell(item));
        }

        this.setOnMouseClicked(event -> {
            System.out.println("Clicked: " + item.getSimName() + " (Index: " + getIndex() + ")");
            if (!item.getSimName().equals("New Simulation")) {
                controller.setSelectedCell(this);
            }
        });
    }

    /*
     * Creates a simulation item that's used to add new simulations. Contains a button with callback function to populate list with new items
     */
    private HBox createNewSimulationCell() {
        // Create elements and style them
        HBox hbox = new HBox(10);
        hbox.setPrefHeight(40);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Text titleText = new Text("New Simulation");
        VBox textContainer = new VBox(titleText);
        textContainer.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addButton = new Button();
        addButton.getStyleClass().add("plus-button");
        FontIcon addIcon = new FontIcon(FontAwesomeSolid.PLUS);
        addIcon.setIconSize(30);
        addIcon.setIconColor(Color.WHITE);
        addButton.setGraphic(addIcon);

        addButton.getStyleClass().add("button");
        addButton.setOnAction(event -> simulationManager.addNewSimulation());

        hbox.getChildren().addAll(textContainer, spacer, addButton);

        return hbox;
    }

    /*
     * Creates normal simulation items, with buttons for deleting and renaming
     */
    private HBox createRegularSimulationCell(Simulation item) {
        // Create elements and style them
        HBox hbox = new HBox(10);
        hbox.setPrefHeight(75);
        hbox.setStyle("-fx-alignment: center-left; -fx-padding: 5px;");

        Text titleText = new Text(item.getSimName());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Rename button with styling
        Button renameButton = new Button();
        FontIcon renameIcon = new FontIcon(FontAwesomeSolid.PENCIL_ALT);
        renameIcon.setIconSize(30);
        renameIcon.setIconColor(Color.WHITE);
        renameButton.setGraphic(renameIcon);
        renameButton.getStyleClass().add("button-rename");

        // Delete button with styling
        Button deleteButton = new Button();
        FontIcon deleteIcon = new FontIcon(FontAwesomeSolid.TRASH);
        deleteIcon.setIconSize(30);
        deleteIcon.setIconColor(Color.WHITE);
        deleteButton.setGraphic(deleteIcon);
        deleteButton.getStyleClass().add("button-delete");

        renameButton.setVisible(false);
        deleteButton.setVisible(false);

        // Add listeners to only show the buttons when the mouse hovers on the simulation item
        hbox.setOnMouseEntered(event -> updateButtonVisibility(renameButton, deleteButton, true));
        hbox.setOnMouseExited(event -> updateButtonVisibility(renameButton, deleteButton, false));

        // Add callback function for when each button is triggerd
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

    /*
     * Validate the renaming of a simulation item
     */
    private void validateAndApplyRename(TextField renameField, Simulation item, HBox box) {
        String newName = renameField.getText().trim();

        // Check new name isn't empty
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
