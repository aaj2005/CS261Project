package com.example;

public class Stats {
    public final double max_wait_time;
    public final double max_queue_length;
    public final double average_wait_time;
    public final double weight_max_wait_time = 1;
    public final double weight_max_queue_length = 1;
    public final double weight_average_wait_time = 1;
    public       double overall_score;

    public Stats(double max_wait_time, double max_queue_length, double average_wait_time) {
        this.max_wait_time = max_wait_time;
        this.max_queue_length = max_queue_length;
        this.average_wait_time = average_wait_time;

        // a simple w1*a1 + w2*a2 + w3*a3 calculation
        this.overall_score =
                this.max_wait_time * this.weight_max_wait_time +
             this.max_queue_length * this.weight_max_queue_length +
            this.average_wait_time * this.weight_average_wait_time;
    }

    public String toString() {
        return "Max wait time: " + max_wait_time + "s\nMax queue length: " + max_queue_length + "cars\nAverage wait time: " + average_wait_time + "s\nOverall score: " + overall_score;
    }
}
