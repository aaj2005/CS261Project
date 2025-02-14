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

    public SimulationComponents(int lanes_arm1, int lanes_arm2, int lanes_arm3, int lanes_arm4) {
//        this.lanes = lanes;
        int max_out = Math.max(Math.max(Math.max(lanes_arm1,lanes_arm2),lanes_arm3),lanes_arm4);
//        int max_out_arm2 = Math.max(Math.max(lanes_arm1,lanes_arm3),lanes_arm4);
//        int max_out_arm3 = Math.max(Math.max(lanes_arm1,lanes_arm2),lanes_arm4);
//        int max_out_arm4 = Math.max(Math.max(lanes_arm1,lanes_arm2),lanes_arm3);
        corners = new Rectangle[]{
                new Rectangle(300-(max_out * lane_w), 300-lanes_arm1 * lane_w),
                new Rectangle(300-lanes_arm2 * lane_w, 300-max_out * lane_w),
                new Rectangle( 300-lanes_arm3 * lane_w, 300-max_out * lane_w),
                new Rectangle(300-max_out * lane_w, 300-lanes_arm4 * lane_w)
        };

        lane_separation = new Rectangle[]{
                new Rectangle( corners[0].getWidth() + 20 *max_out, 0 ,1, Math.min(corners[0].getHeight(),corners[1].getHeight())),
                new Rectangle(sim_w - Math.min(corners[1].getWidth(),corners[3].getWidth()), corners[1].getHeight() + 20 *max_out, Math.min(corners[1].getWidth(),corners[3].getWidth()),1 ),
                new Rectangle(sim_w-(corners[3].getWidth() + 20 *max_out), sim_h-Math.min(corners[2].getHeight(), corners[3].getHeight()), 1, Math.min(corners[2].getHeight(),corners[3].getHeight()) ),
                new Rectangle( 0, sim_h - (corners[2].getHeight() + 20 *max_out), Math.min(corners[0].getWidth(),corners[2].getWidth()), 1 ),

        };


        corners[0].setFill(Color.BLACK);
        corners[1].setFill(Color.BLUE);
        corners[2].setFill(Color.RED);
        corners[3].setFill(Color.YELLOW);

        corners[0].setX(0);
        corners[0].setY(0);

        corners[1].setX(sim_w - corners[0].getWidth());
        corners[1].setY(0);

        corners[2].setX(0);
        corners[2].setY(sim_h - corners[2].getHeight());

        corners[3].setX(sim_w - corners[3].getWidth());
        corners[3].setY(sim_h - corners[3].getHeight());

    }

    public Rectangle[] getCorners() {
        return corners;
    }

    public Rectangle[] getLane_separation() {
        return lane_separation;
    }
}
