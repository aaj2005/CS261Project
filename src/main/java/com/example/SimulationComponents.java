package com.example;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class SimulationComponents {


    private ArrayList<Rectangle> lanes;

    private Rectangle[] corners;
    private Rectangle[] lane_separation;

    private int[] number_of_lanes = {1,1,1,1,1};

    public static final double sim_w = 600;
    public static final double sim_h = 600;

    public static final double lane_w = 20;


    // junction arm: left - top - bottom - right
    public SimulationComponents(int lanes_arm1, int lanes_arm2, int lanes_arm3, int lanes_arm4) {

        // number of lanes exiting junction for each arm
        int max_out = Math.max(Math.max(Math.max(lanes_arm1,lanes_arm2),lanes_arm3),lanes_arm4);

        corners = new Rectangle[]{
                new Rectangle( // top left
                        0,0, // X and Y
                        (sim_w/2)-(max_out * lane_w), (sim_h/2)-lanes_arm1 * lane_w // Width and Height
                ),
                new Rectangle( // top right
                        sim_w - ((sim_w/2)-lanes_arm2 * lane_w), 0, // X and Y
                        (sim_w/2)-lanes_arm2 * lane_w, (sim_h/2)-max_out * lane_w // Width and Height
                ),
                new Rectangle( // bottom left
                        0, sim_h - ((sim_h/2)-max_out * lane_w), // X and Y
                        (sim_w/2)-lanes_arm3 * lane_w, (sim_h/2)-max_out * lane_w // Width and Height
                ),
                new Rectangle( // bottom right
                        sim_w - ((sim_w/2)-max_out * lane_w), sim_h - ((sim_h/2)-lanes_arm4 * lane_w), // X and Y
                        (sim_w/2)-max_out * lane_w, (sim_h/2)-lanes_arm4 * lane_w // Width and Height
                ),
        };


        // line dividing incoming/outgoing lanes for each junction arm
        lane_separation = new Rectangle[]{
                new Rectangle( // top junction arm
                        corners[0].getWidth() + lane_w *max_out, 0, // X and Y
                        1, Math.min(corners[0].getHeight(),corners[1].getHeight()) // Width and Height
                ), // right junction arm
                new Rectangle(
                        sim_w - Math.min(corners[1].getWidth(),corners[3].getWidth()), corners[1].getHeight() + lane_w *max_out, // X and Y
                        Math.min(corners[1].getWidth(),corners[3].getWidth()),1  // Width and Height
                ),
                new Rectangle( // bottom junction arm
                        sim_w-(corners[3].getWidth() + lane_w *max_out), sim_h-Math.min(corners[2].getHeight(), // X and Y
                        corners[3].getHeight()), 1, Math.min(corners[2].getHeight(),corners[3].getHeight()) // Width and Height
                ),
                new Rectangle( // left junction arm
                        0, sim_h - (corners[2].getHeight() + lane_w *max_out), // X and Y
                        Math.min(corners[0].getWidth(),corners[2].getWidth()), 1 // Width and Height
                ),

        };


        corners[0].setFill(Color.BLACK);
        corners[1].setFill(Color.BLUE);
        corners[2].setFill(Color.RED);
        corners[3].setFill(Color.YELLOW);


    }

    public Rectangle[] getCorners() {
        return corners;
    }

    public Rectangle[] getLane_separation() {
        return lane_separation;
    }
}
