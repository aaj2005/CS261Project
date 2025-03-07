package com.example;

public class SimulationData {
    private String name;
    private Double score;
    private Double[] avgWaitTime;
    private Double[] maxWaitTime;
    private Double[] maxQueueLength;

    public SimulationData(String name, Double[] avgWaitTime, Double[] maxWaitTime, Double[] maxQueueLength, double score ) {
        this.name = name;
        this.avgWaitTime = avgWaitTime;
        this.maxWaitTime = maxWaitTime;
        this.maxQueueLength = maxQueueLength;
        this.score = score;
    }

    // Getters and setters
    public Double[] getAvgWaitTime() {
        return avgWaitTime;
    }

    public Double[] getMaxWaitTime() {
        return maxWaitTime;
    }

    public Double[] getMaxQueueLength() {
        return maxQueueLength;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getScore() {
        return score;
    }
}
