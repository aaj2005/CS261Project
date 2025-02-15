package com.example;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class PrimaryController {

    @FXML
    private ListView<SimListItem> simList;

    @FXML
    public void initialize() {
        SimListItem startingSim = new SimListItem("Simulation 1");

        simList.getItems().add(startingSim);

        simList.setCellFactory(param -> new ListCell<SimListItem>() {
            @Override
            protected void updateItem(SimListItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
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
        });
    }
}
