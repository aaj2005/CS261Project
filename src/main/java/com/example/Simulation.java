package com.example;

public class Simulation {
    private String simName;
    private Integer north_num_lanes = 0;
    private Boolean north_bus_lane, north_left_turn = false;
    private Integer north_north_vph, north_east_vph, north_west_vph = 0;

    private Integer east_num_lanes = 0;
    private Boolean east_bus_lane, east_left_turn = false;
    private Integer east_north_vph, east_east_vph, east_south_vph = 0;

    private Integer south_num_lanes = 0;
    private Boolean south_bus_lane, south_left_turn = false;
    private Integer south_east_vph, south_south_vph, south_west_vph = 0;

    private Integer west_num_lanes = 0;
    private Boolean west_bus_lane, west_left_turn = false;
    private Integer west_north_vph, west_south_vph, west_west_vph = 0;

    private Boolean pedestrian_crossings = false;
    private Integer duration_of_crossings = 0;
    private Integer requests_per_house = 0;


    public Simulation(String simName) {
        this.simName = simName;
    }

    public void setNumberParameters() {

    }

    public void setNorthNumLanes(Integer north_num_lanes) {
        this.north_num_lanes = north_num_lanes;
    }
    public void setNorthBusLane(Boolean north_bus_lane) {
        this.north_bus_lane = north_bus_lane;
    }
    public void setNorthLeftTurn(Boolean north_left_turn) {
        this.north_left_turn = north_left_turn;
    }

    public void setEastNumLanes(Integer east_num_lanes) {
        this.east_num_lanes = east_num_lanes;
    }
    public void setEastBusLane(Boolean east_bus_lane) {
        this.east_bus_lane = east_bus_lane;
    }
    public void setEastLeftTurn(Boolean east_left_turn) {
        this.east_left_turn = east_left_turn;
    }

    public void setSouthNumLanes(Integer south_num_lanes) {
        this.south_num_lanes = south_num_lanes;
    }
    public void setSouthBusLane(Boolean south_bus_lane) {
        this.south_bus_lane = south_bus_lane;
    }
    public void setSouthLeftTurn(Boolean south_left_turn) {
        this.south_left_turn = south_left_turn;
    }

    public void setWestNumLanes(Integer west_num_lanes) {
        this.west_num_lanes = west_num_lanes;
    }
    public void setWestBusLane(Boolean west_bus_lane) {
        this.west_bus_lane = west_bus_lane;
    }
    public void setWestLeftTurn(Boolean west_left_turn) {
        this.west_left_turn = west_left_turn;
    }


    public void setPedestrianCrossings(Boolean pedestrian_crossings) {
        this.pedestrian_crossings = pedestrian_crossings;
    }

    public String getSimName() {
        return simName;
    }

    public void setSimName(String simName) {
        this.simName = simName;
    }

    @Override
    public String toString() {
        return simName;
    }
}
