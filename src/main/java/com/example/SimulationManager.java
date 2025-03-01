package com.example;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class SimulationManager {
    private int numSimulations = 0;
    private ObservableList<SimListItem> simList;

    public SimulationManager(ObservableList<SimListItem> simList) {
        this.simList = simList;
    }

    public void addNewSimulation() {
        if (simList.size() - 1 >= 8) {
            showAlert("Simulation Limit Reached", "You can only have up to 8 simulations.");
            return;
        }
        int newSimPosition = simList.size() - 1;
        SimListItem newSimItem = new SimListItem("Simulation " + ++numSimulations);
        simList.add(newSimPosition, newSimItem);
    }

    public void deleteSimulation(SimListItem item) {
        simList.remove(item);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
