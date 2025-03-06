package com.example;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class SimulationManager {
    private int numSimulations = 1;
    private ObservableList<Simulation> simList;

    public SimulationManager(ObservableList<Simulation> simList) {
        this.simList = simList;
    }

    public void addNewSimulation() {
        if (simList.size() - 1 >= 8) {
            showAlert("Simulation Limit Reached", "You can only have up to 8 simulations.");
            return;
        }
        int newSimPosition = simList.size() - 1;
        Simulation newSimItem = new Simulation("Simulation " + ++numSimulations);
        simList.add(newSimPosition, newSimItem);
    }

    public void deleteSimulation(Simulation item) {
        if (simList.size() - 1 <= 1) {
            showAlert("Cannot Delete", "At least one simulation must remain.");
            return;
        }
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
