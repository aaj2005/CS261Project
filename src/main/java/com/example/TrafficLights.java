package com.example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import javafx.util.Duration;

public class TrafficLights {
    public static final ImagePattern green_pedestrian = new ImagePattern(
    new Image(TrafficLights.class.getResource("/youcanwalk.png").toExternalForm()));
    public static final ImagePattern green_light = new ImagePattern(
    new Image(TrafficLights.class.getResource("/light_green.png").toExternalForm()));
    public static final ImagePattern red_light = new ImagePattern(
    new Image(TrafficLights.class.getResource("/light_red.png").toExternalForm()));
    public static final ImagePattern yellow_light = new ImagePattern(
    new Image(TrafficLights.class.getResource("/light_yellow.png").toExternalForm()));
    public static final ImagePattern intermediate_light = new ImagePattern(
    new Image(TrafficLights.class.getResource("/light_intermediate.png").toExternalForm()));
    public static final ImagePattern red_pedestrian = new ImagePattern(
    new Image(TrafficLights.class.getResource("/donotfkingwalk.png").toExternalForm()));

    private double time1; // time for top junction lights to be on
    private double time2; // time for right junction lights to be on
    private double time3; // time for bottom junction lights to be on
    private double time4; // time for left junction lights to be on
    private double s_per_request; // seconds per request for pedestrian crossings
    private double crossing_dur; // pedestrian crossing duration
    private Road[] out_junc;

    // time1 = top junction
    // time2 = right junction
    // time3 = bottom junction
    // time4 = left junction
    public TrafficLights(double time1, double time2, double time3, double time4, double requests_ph, double crossing_dur,Road[] out_junc ) {
        this.time1 = time1;
        this.time2 = time2;
        this.time3 = time3;
        this.time4 = time4;
        this.out_junc = out_junc;
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
    private Rectangle[] lights;

    private boolean run_crossing =false;    // disable all lights and allow pedestrians to move

    private boolean car_in_junction = false;
    
    public Rectangle[] create_rectangles(Rectangle[] lanes, double scale_factor, double[] center){
        lights = new Rectangle[]{
            new Rectangle(
                lanes[0].getX() + lanes[0].getWidth()/2 - (300/scale_factor)/2,
                lanes[0].getY() + lanes[0].getHeight() ,
                300/scale_factor,
                600/scale_factor
            ),
            new Rectangle(
                lanes[1].getX(),
                lanes[1].getY() + lanes[1].getHeight()/2 - (600/scale_factor)/2,
                300/scale_factor,
                600/scale_factor
            ),
            new Rectangle(
                lanes[2].getX() + lanes[2].getWidth()/2 - (300/scale_factor)/2,
                lanes[2].getY() ,
                300/scale_factor,
                600/scale_factor
            ),
            new Rectangle(
                lanes[3].getX() + lanes[3].getWidth(),
                lanes[3].getY() + lanes[3].getHeight()/2 - (600/scale_factor)/2,
                300/scale_factor,
                600/scale_factor
            ),
            new Rectangle(
                center[0] - (800/scale_factor)/2,
                center[1] - (1400/scale_factor)/2,
                800/scale_factor,
                1400/scale_factor
            )

        };
        lights[0].setFill(green_light);
        lights[1].setFill(red_light);
        lights[2].setFill(red_light);
        lights[3].setFill(red_light);
        lights[0].setRotate(180);
        lights[1].setRotate(270);
        lights[3].setRotate(90);
        lights[4].setFill(red_pedestrian);
        

        return lights;
    }


    public void run_lights(){
        Timeline traffic_timeline = new Timeline(new KeyFrame(Duration.millis(100), event ->{
            time_traffic +=0.1;
            time_crossing +=0.1;

            if (run_crossing && !car_in_junction){
                lights[0].setFill(red_light);
                lights[1].setFill(red_light);
                lights[2].setFill(red_light);
                lights[3].setFill(red_light);
                lights[4].setFill(green_pedestrian);
                
                duration_crossing += 0.1;
                if (duration_crossing >= crossing_dur){
                    run_crossing = false;
                    duration_crossing = 0;
                    time_crossing = 0;
                    light_status[nextlight] = true;
                    lights[nextlight].setFill(green_light);
                    lights[4].setFill(red_pedestrian);
                    time_traffic1 = 0;
                    time_traffic2 = 0;
                    time_traffic3 = 0;
                    time_traffic4 = 0;
                }

            }else{

                if (car_in_junction){
                    if(run_crossing){
                        duration_crossing -= 0.1;
                    }
                    check_car_in_junction(); // repeatedly check to see if a car is still in junction
                }else{
                    if (light_status[0]){
                        lights[0].setFill(green_light);
                        time_traffic1 +=0.1;
                        if (time_traffic1 >= time1 -0.05){
                            check_car_in_junction();
                            if (time_crossing >= s_per_request){
                                light_status[0] = false;
                                run_crossing = true;
                            }
                            else{

                                light_status[0] = false;
                                light_status[1] = true;
                                lights[0].setFill(red_light);
                                if (car_in_junction){
                                    lights[1].setFill(intermediate_light);
                                }
                                else{
                                    lights[1].setFill(green_light);
                                }
                                time_traffic1 = 0;
                                time_traffic2 = 0;
                                nextlight = 2;
                            }

                        }
                    }
                    else if (light_status[1]){
                        lights[1].setFill(green_light);
                        time_traffic2 +=0.1;
                        if (time_traffic2 >= time2 -0.05){
                            check_car_in_junction();
                            if (time_crossing >= s_per_request){
                                light_status[1] = false;
                                run_crossing = true;

                            }
                            else{
                                light_status[1] = false;
                                light_status[2] = true;
                                lights[1].setFill(red_light);
                                if (car_in_junction){
                                    lights[2].setFill(intermediate_light);
                                }
                                else{
                                    lights[2].setFill(green_light);
                                }
                                time_traffic2 = 0;
                                time_traffic3 = 0;
                                nextlight = 3;
                            }

                        }
                    }
                    else if (light_status[2]){
                        lights[2].setFill(green_light);
                        time_traffic3 +=0.1;
                        if (time_traffic3 >= time3 -0.05){
                            check_car_in_junction();
                            if (time_crossing >= s_per_request){
                                light_status[2] = false;
                                run_crossing = true;
                            }
                            else{
                                light_status[2] = false;
                                light_status[3] = true;
                                lights[2].setFill(red_light);
                                if (car_in_junction){
                                    lights[3].setFill(intermediate_light);
                                }
                                else{
                                    lights[3].setFill(green_light);
                                }
                                time_traffic3 = 0;
                                time_traffic4 = 0;
                                nextlight = 0;
                            }

                        }
                    }
                    else if (light_status[3]){
                        lights[3].setFill(green_light);
                        time_traffic4 +=0.1;
                        if (time_traffic4 >= time4 -0.05){
                            check_car_in_junction();
                            if (time_crossing >= s_per_request){
                                light_status[3] = false;
                                run_crossing = true;
                            }
                            else{
                                light_status[3] = false;
                                light_status[0] = true;
                                lights[3].setFill(red_light);
                                if (car_in_junction){
                                    lights[0].setFill(intermediate_light);
                                }
                                else{
                                    lights[0].setFill(green_light);
                                }
                                time_traffic4 = 0;
                                time_traffic1 = 0;
                                nextlight = 1;
                            }

                        }
                    }

                }

            }
//            if (time_traffic - Math.floor(time_traffic) >= 0.8){
//                System.out.println("Current Time: " + (time_traffic));
//                System.out.println("Light 1: "+ light_status[0] + " Light 2: "+ light_status[1] + " Light 3: "+ light_status[2] + " Light 4: "+ light_status[3]);
//                System.out.println("Time traffic1: " + time_traffic1 + " Time traffic2: " + time_traffic2 + " Time traffic3: " + time_traffic3 + " Time traffic4: " + time_traffic4);
//                System.out.println("Pedestrian Crossing:" + pedestrian_crossing);
//                System.out.println("Run crossing: " + run_crossing);
//                System.out.println("Next light: " + nextlight);
//                System.out.println("----------------------------------------------------");
//            }
            

        }));

        traffic_timeline.setCycleCount(Timeline.INDEFINITE);
        traffic_timeline.play();


    }

    private void check_car_in_junction(){
        car_in_junction = false;
        for (Road road: out_junc){
            for (Lane lane: road.getLanes()){
                if (!lane.getCars().isEmpty()) {
                    car_in_junction = true;
                    break;
                }
            }
        }
    }

    public boolean[] getLight_status() {
        return light_status;
    }

    public boolean isCar_in_junction() {
        return car_in_junction;
    }
}
