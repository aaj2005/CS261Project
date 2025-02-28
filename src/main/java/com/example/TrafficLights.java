package com.example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class TrafficLights {

    private double time1;
    private double time2;
    private double time3;
    private double time4;
    private double s_per_request;
    private double crossing_dur;

    // time1 = top junction
    // time2 = right junction
    // time3 = bottom junction
    // time4 = left junction
    public TrafficLights(double time1, double time2, double time3, double time4, double requests_ph, double crossing_dur) {
        this.time1 = time1;
        this.time2 = time2;
        this.time3 = time3;
        this.time4 = time4;
        this.s_per_request = 1/(requests_ph/3600);
        this.crossing_dur = crossing_dur;

    }

    private boolean[] light_status = {true,false,false,false};

    private boolean pedestrian_crossing = false;

    private double time_crossing = 0;
    private double time_traffic = 0;
    private double time_traffic1 = 0;
    private double time_traffic2 = 0;
    private double time_traffic3 = 0;
    private double time_traffic4 = 0;

    private boolean run_crossing =false;


    public void run_lights(){
        //time1+time2+time3+time4
        Timeline crossing_timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            if (time_crossing <= s_per_request +crossing_dur + 0.1){
                if (time_crossing >= s_per_request){
                    pedestrian_crossing = true;
                }else{
                    time_crossing+=0.1;
                }
                if (run_crossing){
                    light_status[0]= false;
                    light_status[1]= false;
                    light_status[2]= false;
                    light_status[3]= false;
                    if (time_crossing >= s_per_request +crossing_dur){
                        run_crossing = false;
                        pedestrian_crossing = false;
                        time_crossing = 0;
                    }else{
                        time_crossing+=0.1;
                    }
                }


            }else{
                time_crossing=0;
            }

        }));

        Timeline traffic_timeline = new Timeline(new KeyFrame(Duration.millis(100), event ->{
            time_traffic+=0.1;
            if (time_traffic - Math.floor(time_traffic) >= 0.8){
                System.out.println("Current Time: " + time_traffic);
                System.out.println("Light 1: "+ light_status[0] + " Light 2: "+ light_status[1] + " Light 3: "+ light_status[2] + " Light 4: "+ light_status[3]);
                System.out.println("Pedestrian Crossing:" + pedestrian_crossing);
                System.out.println("----------------------------------------------------");
            }

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

            if (pedestrian_crossing &&  (time_traffic1 >=time1 || time_traffic2 >=time2 || time_traffic3 >= time3 || time_traffic4 >= time4 ) ){
                run_crossing = true;
            }else{
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

}
