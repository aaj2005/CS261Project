package com.example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class TrafficLights {

    private double time1; // time for top junction lights to be on
    private double time2; // time for right junction lights to be on
    private double time3; // time for bottom junction lights to be on
    private double time4; // time for left junction lights to be on
    private double s_per_request; // seconds per request for pedestrian crossings
    private double crossing_dur; // pedestrian crossing duration

    // time1 = top junction
    // time2 = right junction
    // time3 = bottom junction
    // time4 = left junction
    public TrafficLights(double time1, double time2, double time3, double time4, double requests_ph, double crossing_dur) {
        this.time1 = time1;
        this.time2 = time2;
        this.time3 = time3;
        this.time4 = time4;
        this.s_per_request = 1/(requests_ph/3600); // convert from requests per hour to seconds per request
        this.crossing_dur = crossing_dur;
        if (time1+time2+time3+time4 == 0){
            light_status[0] = false;
        }

    }

    private boolean[] light_status = {true,false,false,false};

    private boolean pedestrian_crossing = false; // run pedestrian crossings after current traffic lights finish

    private double time_crossing = 0; // current time duration for crossing
    private double time_traffic= 0; // current time duration for traffic light 1
    private double time_traffic2 = 0; // current time duration for traffic light 1
    private double time_traffic3 = 0; // current time duration for traffic light 2
    private double time_traffic1 = 0; // current time duration for traffic light 3
    private double time_traffic4 = 0; // current time duration for traffic light 4

    private boolean run_crossing =false; // disable all lights and allow pedestrians to move


    public void run_lights(){
        Timeline crossing_timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            // run this part of the code as long as we still didn't fully finish 1 entire cycle of waiting for
            // pedestrian lights to go on (determined by seconds per request) and for the pedestrians to cross (crossing duration)
            if (time_crossing <= s_per_request +crossing_dur ){
                // if it is time for pedestrians to cross, let traffic lights know that and wait
                if (time_crossing >= s_per_request){
                    pedestrian_crossing = true;
                }else{
                    time_crossing+=0.1; // increment the clock
                }
                // if the current traffic light has ended and pedestrians want to cross, let them cross
                if (run_crossing){
                    light_status[0]= false;
                    light_status[1]= false;
                    light_status[2]= false;
                    light_status[3]= false;
                    // if crossing time is reached, reset everything back
                    if (time_crossing >= s_per_request +crossing_dur -0.1){
                        run_crossing = false;
                        pedestrian_crossing = false;
                        time_crossing = 0;
                    }else{
                        time_crossing+=0.1; // increment the clock
                    }
                }


            }else{ // reset clock
                time_crossing=0;
            }

        }));

        Timeline traffic_timeline = new Timeline(new KeyFrame(Duration.millis(100), event ->{
            time_traffic +=0.1;
            if (time_traffic - Math.floor(time_traffic) >= 0.8){
                System.out.println("Current Time: " + time_traffic);
                System.out.println("Light 1: "+ light_status[0] + " Light 2: "+ light_status[1] + " Light 3: "+ light_status[2] + " Light 4: "+ light_status[3]);
                System.out.println("Pedestrian Crossing:" + pedestrian_crossing);
                System.out.println("----------------------------------------------------");
            }

            // if pedestrians are not crossing, increment time for currently running traffic light
            if (!run_crossing){
                if (light_status[0]){
                    time_traffic1+=0.1;
                }else if (light_status[1]){
                    time_traffic2+=0.1;
                }else if (light_status[2]){
                    time_traffic3+=0.1;
                }else{
                    time_traffic4+=0.1;
                }
            }
            // if pedestrians want to cross, and a traffic light has finished running, let pedestrians cross
            if (pedestrian_crossing &&  (time_traffic1 >=time1 || time_traffic2 >=time2 || time_traffic3 >= time3 || time_traffic4 >= time4 ) ){
                run_crossing = true;
            }else if (time1+time2+time3+time4 != 0){ // edge case condition
                // if traffic light has finished: reset its time, disable it, and enable the next one
                if (time_traffic1 >=time1){
                    time_traffic1 = 0;
                    light_status[0] = false;
                    light_status[1] = true;
                }else if (time_traffic2 >=time2){
                    time_traffic2 = 0;
                    light_status[1] = false;
                    light_status[2] = true;
                }else if (time_traffic3 >=time3){
                    time_traffic3 = 0;
                    light_status[2] = false;
                    light_status[3] = true;
                }else if (time_traffic4 >= time4){
                    time_traffic4 = 0;
                    light_status[3] = false;
                    light_status[0] = true;
                }
            }


        }));

        crossing_timeline.setCycleCount(Timeline.INDEFINITE);
        traffic_timeline.setCycleCount(Timeline.INDEFINITE);
        crossing_timeline.play();
        traffic_timeline.play();


    }

    public boolean[] getLight_status() {
        return light_status;
    }
}
