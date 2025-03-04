package com.example;

public class Stats {
    public double max_wait_time;
    public double max_queue_length;

    // these two variables are used to get average wait time
    public double total_wait_time; // the cumulative wait times of all the cars in seconds
    public int num_cars;           // the number of cars whose wait times cumulate to total_wait_time

    public final double weight_max_wait_time = 1;
    public final double weight_max_queue_length = 1;
    public final double weight_average_wait_time = 1;

    public double overall_score;

    /*
     * Holds three variables:
     * maxmimum wait wait time in seconds
     * maximum queue length in cars
     * average wait time in seconds
     */
    public Stats(double max_wait_time, double max_queue_length, double total_wait_time, int num_cars) {
        this.max_wait_time = max_wait_time;
        this.max_queue_length = max_queue_length;
        this.total_wait_time = total_wait_time;
        this.num_cars = num_cars;                
    }

    public String toString() {
        return "Max wait time: " + this.getMaxWaitTime() + "s\nMax queue length: " + this.getMaxQueueLength() + "cars\nAverage wait time: " + this.getAverageWaitTime() + "s\nOverall score: " + this.getOverallScore();
    }

    public double getMaxWaitTime() { return this.max_wait_time; }
    public double getMaxQueueLength() { return this.max_queue_length; }
    public double getAverageWaitTime() {
        if (this.num_cars == 0) { return 0; }
        return this.total_wait_time/this.num_cars;
    }

    /*
     * a simple w1*a1 + w2*a2 + w3*a3 calculation
     */
    public double getOverallScore() {
        return this.getMaxWaitTime() * this.weight_max_wait_time +
            this.getMaxQueueLength() * this.weight_max_queue_length +
           this.getAverageWaitTime() * this.weight_average_wait_time;
    }

    /*
     * Given two stats objects, returns a combined stats object
     * These two stats objects can represent different roads, and the combined stats object provides statistics over the two roads
     */
    public static Stats combine(Stats a, Stats b) {
        return new Stats(
            Math.max(a.max_wait_time, b.max_wait_time),
            Math.max(a.max_queue_length, b.max_queue_length),
            a.total_wait_time + b.total_wait_time,
            a.num_cars + b.num_cars
        );
    }
}
