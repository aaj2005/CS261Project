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


    public String getName() {
        return name;
    }

    public double getScore() {
        return score;
    }

    private double calculatePerformanceScore() {
        double sumofaveragewaiting = 0, sumofmaxwaiting = 0, sumofmaxqueues = 0;

        for (int i = 0; i < 4; i++) {
            sumofaveragewaiting += avgWaitTime[i];
            sumofmaxwaiting += maxWaitTime[i];
            sumofmaxqueues += maxQueueLength[i];
        }

        double avgWait = sumofaveragewaiting / 4;
        double maxWait = sumofmaxwaiting / 4;
        double avgQueue = sumofmaxqueues / 4;

        return (1 / (avgWait + 1)) * 100 - (maxWait / 10) + (50 / (avgQueue + 1));
        //Not sure what the actual metric we're using is yet this is just a placeholder
    }

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
}
