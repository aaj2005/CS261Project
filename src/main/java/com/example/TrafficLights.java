package com.example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.BoundingBox;
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

    private double time1; // time for top junction lights to be on in seconds
    private double time2; // time for right junction lights to be on in seconds
    private double time3; // time for bottom junction lights to be on in seconds
    private double time4; // time for left junction lights to be on in seconds
    private double s_per_request; // seconds per request for pedestrian crossings
    private double crossing_dur; // pedestrian crossing duration
    private Road[] in_junc;
    private BoundingBox junction_rectangle;

    private static double default_time = 10; // the default time that a traffic light is on for in seconds
    private static double priority_multiplier = 2; // the amount of time longer that a traffic light is on for in seconds for each priority unit it thas
    // a traffic light with priority n stays on for default_time + priority_multiplier*n

    // time1 = top junction
    // time2 = right junction
    // time3 = bottom junction
    // time4 = left junction
    public TrafficLights(int[] priorities, double requests_ph, double crossing_dur,Road[] in_junc, BoundingBox junction_rectangle) {
        this.time1 = priorities[0]*priority_multiplier + default_time;
        this.time2 = priorities[1]*priority_multiplier + default_time;
        this.time3 = priorities[2]*priority_multiplier + default_time;
        this.time4 = priorities[3]*priority_multiplier + default_time;

        this.in_junc = in_junc;
        this.s_per_request = 1/(requests_ph/3600); // convert from requests per hour to seconds per request
        this.crossing_dur = crossing_dur;
        if (time1+time2+time3+time4 == 0){
            light_status[0] = false;
        }

        this.junction_rectangle = junction_rectangle;

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

    private Timeline traffic_timeline;

    public Rectangle[] create_rectangles(Rectangle[] lanes, double scale_factor, double[] center, int max_lanes){
        double resize = max_lanes /4.0;
        if (resize < 0.3){
            resize = 0.3333;
        }
        lights = new Rectangle[]{
            new Rectangle(
                lanes[0].getX() + lanes[0].getWidth()/2 - ((((center[0]+center[1])/2)/scale_factor)/2)*resize,
                lanes[0].getY() + lanes[0].getHeight() ,
                (((center[0] + center[1])/2)/scale_factor)*resize,
                ((center[0]+center[1])/scale_factor)*resize
            ),
            new Rectangle(
                lanes[1].getX() - (((center[0] + center[1])/2)/scale_factor)*resize,
                lanes[1].getY() + lanes[1].getHeight()/2 - (((center[0] + center[1])/scale_factor)/2)*resize,
                (((center[0] + center[1])/2)/scale_factor)*resize,
                ((center[0] + center[1])/scale_factor)*resize
            ),
            new Rectangle(
                lanes[2].getX() + lanes[2].getWidth()/2 - ((((center[0] + center[1])/2)/scale_factor)/2)*resize,
                lanes[2].getY() - ((center[0] + center[1])/scale_factor)*resize ,
                (((center[0] + center[1])/2)/scale_factor)*resize,
                ((center[0] + center[1])/scale_factor)*resize
            ),
            new Rectangle(
                lanes[3].getX() + lanes[3].getWidth(),
                lanes[3].getY() + lanes[3].getHeight()/2 - (((center[0] + center[1])/scale_factor)/2)*resize,
                (((center[0] + center[1])/2)/scale_factor)*resize,
                ((center[0] + center[1])/scale_factor)*resize
            ),
            new Rectangle(
                center[0] - (((center[0] + center[1])/scale_factor)/2)*resize,
                center[1] - (((2*(center[0] + center[1]))/scale_factor)/2)*resize,
                ((center[0] + center[1])/scale_factor)*resize,
                ((2*(center[0] + center[1]))/scale_factor)*resize
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
        traffic_timeline = new Timeline(new KeyFrame(Duration.millis(100), event ->{
            time_traffic +=0.1;
            time_crossing +=0.1;

            if (run_crossing && !car_in_junction){
                lights[4].setFill(green_pedestrian);
                if (duration_crossing >= crossing_dur * 0.7){
                    lights[nextlight].setFill(intermediate_light);
                }
                
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
                    check_car_in_junction(); // repeatedly check to see if a car is still in junction
                }else{
                    if (light_status[0]){
                        if (time_traffic1 >= time1 * 0.7){
                            lights[0].setFill(yellow_light);
                        }
                        else{
                            lights[0].setFill(green_light);
                        }
                        time_traffic1 +=0.1;
                        if (time_traffic1 >= time1 -0.05){
                            check_car_in_junction();
                            if (time_crossing >= s_per_request){
                                light_status[0] = false;
                                lights[0].setFill(red_light);
                                run_crossing = true;
                                nextlight = 1;
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
                        if (time_traffic2 >= time1 * 0.7){
                            lights[1].setFill(yellow_light);
                        }
                        else{
                            lights[1].setFill(green_light);
                        }
                        time_traffic2 +=0.1;
                        if (time_traffic2 >= time2 -0.05){
                            check_car_in_junction();
                            if (time_crossing >= s_per_request){
                                light_status[1] = false;
                                lights[1].setFill(red_light);
                                run_crossing = true;
                                nextlight = 2;

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
                        if (time_traffic3 >= time1 * 0.7){
                            lights[2].setFill(yellow_light);
                        }
                        else{
                            lights[2].setFill(green_light);
                        }
                        time_traffic3 +=0.1;
                        if (time_traffic3 >= time3 -0.05){
                            check_car_in_junction();
                            if (time_crossing >= s_per_request){
                                light_status[2] = false;
                                lights[2].setFill(red_light);
                                run_crossing = true;
                                nextlight = 3;
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
                        if (time_traffic4 >= time1 * 0.7){
                            lights[3].setFill(yellow_light);
                        }
                        else{
                            lights[3].setFill(green_light);
                        }
                        time_traffic4 +=0.1;
                        if (time_traffic4 >= time4 -0.05){
                            check_car_in_junction();
                            if (time_crossing >= s_per_request){
                                light_status[3] = false;
                                lights[3].setFill(red_light);
                                run_crossing = true;
                                nextlight = 0;
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
    public void lights_start(){
        if (traffic_timeline != null){
            traffic_timeline.play();
        }else{
            run_lights();
        }
    }

    public void lights_stop(){
        traffic_timeline.stop();
    }



    private void check_car_in_junction(){
        this.car_in_junction = false;
        for (Road road: this.in_junc) {
            if (road.existsCarInJunction(this.junction_rectangle)) {
                this.car_in_junction = true;
                return;
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
