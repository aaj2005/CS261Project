package com.example;
import javafx.collections.ObservableList;

public class SimulationManager {
    private int numSimulations = 0;
    private ObservableList<SimListItem> simList;

    public SimulationManager(ObservableList<SimListItem> simList) {
        this.simList = simList;
    }

    public void addNewSimulation() {
        int newSimPosition = simList.size() - 1;
        SimListItem newSimItem = new SimListItem("Simulation " + ++numSimulations);
        simList.add(newSimPosition, newSimItem);
    }

    public void deleteSimulation(SimListItem item) {
        simList.remove(item);
    }
}
