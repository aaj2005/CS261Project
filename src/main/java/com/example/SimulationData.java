package com.example;

public class SimulationData {
    private String name;
    private double score;
    private double[] avgWaitTime; 
    private double[] maxWaitTime;  
    private double[] avgQueueLength; 

    public SimulationData(String name, double[] avgWaitTime, double[] maxWaitTime, double[] avgQueueLength) {
        this.name = name;
        this.avgWaitTime = avgWaitTime;
        this.maxWaitTime = maxWaitTime;
        this.avgQueueLength = avgQueueLength;
        this.score = calculatePerformanceScore();
    }

    private double calculatePerformanceScore() {
        double sumofaveragewaiting = 0, sumofmaxwaiting = 0, sumofmaxqueues = 0;

        for (int i = 0; i < 4; i++) {
            sumofaveragewaiting += avgWaitTime[i];
            sumofmaxwaiting += maxWaitTime[i];
            sumofmaxqueues += avgQueueLength[i];
        }

        double avgWait = sumofaveragewaiting / 4;
        double maxWait = sumofmaxwaiting / 4;
        double avgQueue = sumofmaxqueues / 4;

        return (1 / (avgWait + 1)) * 100 - (maxWait / 10) + (50 / (avgQueue + 1));
        //Not sure what the actual metric we're using is yet this is just a placeholder
    }

    public String getName() {
        return name;
    }

    public double getScore() {
        return score;
    }

    public double[] getAvgWaitTime() {
        return avgWaitTime;
    }

    public double[] getMaxWaitTime() {
        return maxWaitTime;
    }

    public double[] getAvgQueueLength() {
        return avgQueueLength;
    }
}
