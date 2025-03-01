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
    private double duration_crossing =0;
    private double time_traffic= 0; // current time duration for traffic light 1
    private double time_traffic2 = 0; // current time duration for traffic light 1
    private double time_traffic3 = 0; // current time duration for traffic light 2
    private double time_traffic1 = 0; // current time duration for traffic light 3
    private double time_traffic4 = 0; // current time duration for traffic light 4
    private int nextlight = 0;

    private boolean run_crossing =false;    // disable all lights and allow pedestrians to move


    public void run_lights(){
        Timeline traffic_timeline = new Timeline(new KeyFrame(Duration.millis(100), event ->{
            time_traffic +=0.1;
            time_crossing +=0.1;
            if (run_crossing){
                
                duration_crossing += 0.1;
                if (duration_crossing >= crossing_dur){
                    run_crossing = false;
                    duration_crossing = 0;
                    time_crossing = 0;
                    light_status[nextlight] = true;
                    time_traffic1 = 0;
                    time_traffic2 = 0;
                    time_traffic3 = 0;
                    time_traffic4 = 0;
                }

            }
            else if (light_status[0]){
                time_traffic1 +=0.1;
                if (time_traffic1 >= time1 -0.05){
                    if (time_crossing >= s_per_request){
                        light_status[0] = false;
                        run_crossing = true;
                    }
                    else{
                        light_status[0] = false;
                        light_status[1] = true;
                        time_traffic1 = 0;
                        time_traffic2 = 0;
                        nextlight = 2;
                    }
                    
                }
            }
            else if (light_status[1]){
                time_traffic2 +=0.1;
                if (time_traffic2 >= time2 -0.05){
                    if (time_crossing >= s_per_request){
                        light_status[1] = false;
                        run_crossing = true;

                    }
                    else{
                        light_status[1] = false;
                        light_status[2] = true;
                        time_traffic2 = 0;
                        time_traffic3 = 0;
                        nextlight = 3;
                    }
                    
                }
            }
            else if (light_status[2]){
                time_traffic3 +=0.1;
                if (time_traffic3 >= time3 -0.05){
                    if (time_crossing >= s_per_request){
                        light_status[2] = false;
                        run_crossing = true;
                    }
                    else{
                        light_status[2] = false;
                        light_status[3] = true;
                        time_traffic3 = 0;
                        time_traffic4 = 0;
                        nextlight = 0;
                    }
                    
                }
            }
            else if (light_status[3]){
                time_traffic4 +=0.1;
                if (time_traffic4 >= time4 -0.05){
                    if (time_crossing >= s_per_request){
                        light_status[3] = false;
                        run_crossing = true;
                    }
                    else{
                        light_status[3] = false;
                        light_status[0] = true;
                        time_traffic4 = 0;
                        time_traffic1 = 0;
                        nextlight = 1;
                    }
                    
                }
            }
            if (time_traffic - Math.floor(time_traffic) >= 0.8){
                System.out.println("Time traffic1: " + time_traffic1 + " Time traffic2: " + time_traffic2 + " Time traffic3: " + time_traffic3 + " Time traffic4: " + time_traffic4);
                System.out.println("Current Time: " + (time_traffic));
                System.out.println("Light 1: "+ light_status[0] + " Light 2: "+ light_status[1] + " Light 3: "+ light_status[2] + " Light 4: "+ light_status[3]);
                System.out.println("Pedestrian Crossing:" + pedestrian_crossing);
                System.out.println("Run crossing: " + run_crossing);
                System.out.println("Next light: " + nextlight);
                System.out.println("----------------------------------------------------");
            }
            

        }));

        traffic_timeline.setCycleCount(Timeline.INDEFINITE);
        traffic_timeline.play();


    }

    public boolean[] getLight_status() {
        return light_status;
    }
}
