package com.example;

public class Stats {
    public final float max_wait_time;
    public final float max_queue_length;
    public final float average_wait_time;
    public final float weight_max_wait_time = 1;
    public final float weight_max_queue_length = 1;
    public final float weight_average_wait_time = 1;
    public       float overall_score;

    public Stats(float max_wait_time, float max_queue_length, float average_wait_time) {
        this.max_wait_time = max_wait_time;
        this.max_queue_length = max_queue_length;
        this.average_wait_time = average_wait_time;

        // a simple w1*a1 + w2*a2 + w3*a3 calculation
        this.overall_score =
                this.max_wait_time * this.weight_max_wait_time +
             this.max_queue_length * this.weight_max_queue_length +
            this.average_wait_time * this.weight_average_wait_time;
    }
}
