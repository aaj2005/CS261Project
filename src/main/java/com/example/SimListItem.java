package com.example;

public class SimListItem {
    private String simName;

    public SimListItem(String simName) {
        this.simName = simName;
    }

    public String getSimName() {
        return simName;
    }

    public void setSimName(String simName) {
        this.simName = simName;
    }

    @Override
    public String toString() {
        return simName;
    }
}
