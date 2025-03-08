package com.example;

import java.util.ArrayList;

/*
 * TODO:
 * actually calculate stats - Rayan (?)
 * Rayan write tests for Filip's part
 * Filip write tests for Rayan's part
 * 
 * Should I put a link to my image or pseudocode somewhere to explain this code?
 * Why is the max allowed flow for the parameters I gave less than 500? Seems low.
 */

/*
 * StatCalculator uses the currently-set parameters to determine three statistics:
 * average car wait time at the junction, maximum wait time and max queue length.
 * How to use this class:
 * Instantiate it with the road that you want to do the calculation for
 *  (ie: StatCalculator(0) calculates the statistics for cars entering the junction from road 0)
 * After instantiating, call run()
 */
public class StatCalculator {
    // the roads being simulated
    private StatRoad[] roads;

    // the time that is currently being simulated (in seconds)
    private double t = 0;
    
    // the last time that the pedestrian light was due to turn green
    private double last_ped_light = -getPedLightRate();

    private double jam_length = 0; // length of the traffic jam on the specified road in metres
    
    // the initial length of the jam 
    // we need to track this in order to see if at the end of a cycle, the jam has lengthened
    // if it has lengthened, since each cycle is exactly the same, the jam will continue to lengthen to infinity
    // -1 means that the variable is yet to be assigned a value to
    private double initial_jam_length = -1;

    private int   road; // the road under inspection
    private double jam_lengthening_rate; // the rate at which the jam lengthens in metres per second

    // the total amount of time that all cars (on the specified road) have cumulatively waited for
    // used to calculate average wait time
    private double total_wait_time = 0;

    private double cycle_length; // gets the length of a traffic light cycle in seconds

    private double max_jam_length = 0; // the maximum length of the queue in metres

    private double max_wait_time = 0;  // the maximum wait time in seconds

    // a mathematical variable used in several places
    // represents how many cars enter a traffic jam each second
    private double r_prime;

    // another variable, used to represent the amount of space a car takes up
    private double cs;
    
    // keeps track of where cars are and how long they have been waiting
    // used to help calculate max wait time
    private ArrayList<StatCar> cars = new ArrayList<>();

    /*
     * Constructor for StatCalculator
     * @param road The road to run the calculations for
     */
    public StatCalculator(StatRoad[] roads, int road) throws InvalidParametersException {
        this.roads = roads;
        this.road = road;
        this.calculateJamLengtheningRate();
    }

    /*
     * Calculates how quickly the jam lengthens in metres per second
     * Saves the calculated variable in this.jamLengtheningRate
     */
    private void calculateJamLengtheningRate() throws InvalidParametersException {
        double r = this.getArrivalRate();
        int num_lanes = this.roads[this.road].lanes;
        this.cs = (BaseCar.length + BaseCar.distance)/num_lanes;
        
        if (DoubleCompare.approximatelyEqual(r, 0)) { // avoids a divisionby0 error
            this.jam_lengthening_rate = 0;
            this.r_prime = 0;
        } else {
            this.r_prime = 1/(1/r - cs/ BaseCar.max_speed);
            this.jam_lengthening_rate = this.r_prime * cs;

            if (DoubleCompare.geq(0, this.jam_lengthening_rate)) {
                throw new InvalidParametersException("Cars are too close together! Either the inbound rate of vehicles is too high.");
            }
        }
    }

    /*
     * Calculates how may seconds it takes for the jam to deplete
     */
    private double getJamDepletionTime() {
        return this.jam_length / BaseCar.max_speed;
    }

    /*
     * Calculates all the statistics about the chosen road
     * @throws InvalidParametersException when parameters are invalid and the traffic jam grows to infinity
     * @throws InvalidMethodCallException when method is called more than once
     */
    public Stats run() throws InvalidParametersException, InvalidMethodCallException {
        if (!DoubleCompare.approximatelyEqual(t,0)) {
            throw new InvalidMethodCallException("run() method in StatCalculator cannot be called more than once!");
        }

        /*
         * METHOD OVERVIEW:
         * this method computes one cycle of the traffic light system
         * the cycle starts with the pedestrian lights going green,
         * followed by road 0's lights going green.
         * one cycle is defined as starting at this state and ending at this state at some later point in time
         * over the course of one cycle, the statistics are calculated
         */

        // keeps track of when the light turned red
        // used to see how long the light was red for
        // which in turn is used to calculate the length of the traffic jam that builds up
        double t_red_start = this.t;


        while (!this.endOfCycleReached()) {
            // red light from road 0 to road n
            this.tickRedLight(0, this.road);

            // calculate how long individual cars have to wait during the red light
            this.processWaitingCars(this.t-t_red_start);

            // lengthen the traffic jam due to the fact that the light was red
            this.jam_length += (this.t-t_red_start)*this.jam_lengthening_rate;

            // increase the total wait time
            this.total_wait_time += this.getWaitTime(this.jam_length, this.t-t_red_start);
            
            // update max queue length
            this.max_jam_length = Math.max(this.max_jam_length, this.jam_length);

            // compute the cumulative wait time for this cycle
            this.total_wait_time += this.getWaitTime(this.jam_length, this.t - t_red_start);

            // calculate when the green light will end
            double t_green_end = this.t + getRoadLightLength(this.road);

            // calculate which cars manage to escape the jam this green light
            this.moveWaitingCars(getRoadLightLength(this.road));

            // the traffic jam take n seconds to deplete
            // the green light is on for g seconds
            // hence the traffic jam will actually deplete for s = min(g, n) seconds
            double jam_depletion_time = Math.min(this.getJamDepletionTime(), getRoadLightLength(this.road));

            // jump to the end of the green light
            this.t = t_green_end;

            // decrease the size of the jam
            this.jam_length -= jam_depletion_time* BaseCar.max_speed;

            // if the initialjamlength hasn't been set yet,
            // set it to be length of the jam after the green traffic light has just turned red
            if (initial_jam_length < 0) {
                this.initial_jam_length = this.jam_length;
            }

            // calculate the length of the red light from when the light turns red for road n
            // to when the light turns green for road 0
            // this allows us to check if the end of the cycle has been reached
            t_red_start = this.t;
            tickRedLight(this.road+1, 0);
        }

        // first cycle done! Now store how long the cycle was
        this.cycle_length = this.t;

        // continue the simulation up until the end of next green light for the road currently under inspection
        // this allows us to compare the queue length at this point in cycle 2 to
        // the queue length at this point in cycle 1 (this.initialqueuelength)
        tickRedLight(0, this.road);
        this.processWaitingCars(this.t-t_red_start);
        this.jam_length += (this.t-t_red_start)*this.jam_lengthening_rate;
        this.total_wait_time += this.getWaitTime(this.jam_length, this.t-t_red_start);
        this.moveWaitingCars(this.getRoadLightLength(this.road));
        this.max_jam_length = Math.max(this.max_jam_length, this.jam_length); // final check for max queue length
        double jam_depletion_time = Math.min(this.getJamDepletionTime(), getRoadLightLength(this.road));
        this.t += jam_depletion_time;
        this.jam_length -= jam_depletion_time* BaseCar.max_speed;

        // check if jam length grows to infinity
        // if so, throw exception
        if (DoubleCompare.greaterThan(this.jam_length, this.initial_jam_length)) {
            throw new InvalidParametersException("Traffic length grows to infinity!");
        }

        // deplete the cars in the queue to potentially update max wait time
        while(!this.cars.isEmpty()) {
            t_red_start = this.t;
            tickRedLight(this.road+1, this.road);
            this.addWaitTime(this.t-t_red_start);
            this.moveWaitingCars(this.getRoadLightLength(road));
        }

        return new Stats(
            this.max_wait_time,
            this.max_jam_length/this.cs,
            this.total_wait_time,
            (int)(this.cycle_length*this.getArrivalRate())
        );
    }

    /*
     * Checks if the end of a traffic cycle has been reached
     * ie: the state of the traffic lights will repeat themselves after this point onwards
     */
    private boolean endOfCycleReached() {
        // a new cycle begins once some conditions are met:
        // 1) road 0 is the next road to have its green light turn on
            // (this condition is implicit because this function only runs when
            //  road 0 is the next road to have its green light turn on)
        // 2) the pedestrian light is just turning red
        
        double mod = getPedLightRate() == 0 ? 0 : this.t % getPedLightRate();
        return DoubleCompare.approximatelyEqual(mod, getPedLightLength())

            // we also check that the time isn't ped_light_length, because that is the beginning of the first cycle
            // and we are only checking for when cycles end
            && !DoubleCompare.approximatelyEqual(this.t, getPedLightLength());
    }

    /*
     * "Fast-forwards" the simulation up until the traffic light for rtarget turns green
     * @param rNext the road whose traffic light is about to turn green
     * @param rTarget the road whose traffic light we are waiting for to turn green
     */
    private void tickRedLight(int rNext, int rTarget) {
        // iterate over every road in between rnext and rtarget
        for (int road : getTrafficLightCycle(rNext, rTarget)) {
            // for every road, move time forward by the length of its green light
            this.t += getRoadLightLength(road);
        }

        // keep the pedestrian light green for as many times as it should be turned green for
        if (getPedLightRate() != 0) {
            while (DoubleCompare.geq(t, this.last_ped_light + getPedLightRate())) {
                this.t += getPedLightLength();
                this.last_ped_light += getPedLightRate();
            }
        }
    }

    /*
     * Returns a list of all the traffic lights that will be green in-between rNext and rTarget
     * @param rNext the first traffic light in the sequence
     * @param rTarget the first traffic light not in the sequence
     */
    private int[] getTrafficLightCycle(int rNext, int rTarget) {
        int len = getRemainder(rTarget-rNext, 4);
        int[] cycle = new int[len];
        for (int i=0; i<len; i++) {
            int num = getRemainder(rNext+i, 4);
            cycle[i] = num;
        }
        return cycle;
    }

    /*
     * Gets the positive remainder of a division
     * @param num the numerator
     * @param dem the denominator
     * For example, (-1)/4 gives 3
     */
    private int getRemainder(int num, int dem) {
        return ((num%dem)+dem)%dem;
    }

    private double getPedLightRate() {
        return DynamicComponents.pedestrian_crossing.ped_light_rate;
    }

    private double getPedLightLength() {
        return DynamicComponents.pedestrian_crossing.ped_light_length;
    }

    private double getRoadLightLength(int r) {
        return this.roads[r].actual_light_duration;
    }

    /*
     * gets the cumulative wait time on a road
     * @param queueLen length of the initial queue (at the start of the red light) in metres
     * @param t length of the red light in seconds
     */
    private double getWaitTime(double queue_len, double t) {
        // okay so scrap whatever calculation we were doing
        // this calculation is much simpler and achieves the same result
        // r cars arrive per second where r = jam_lengthening_rate/car.length
        // so in t seconds, t*r cars arrive
        // arriving cars wait on average t/2 seconds
        return t*this.r_prime*(queue_len/this.cs) // how long the cars in the initial queue have to wait
             + t*this.r_prime*t/2;                // how long the arriving cars have to wait
    }

    /*
     * returns the arrival rate of cars to the junction
     */
    private double getArrivalRate() {
        return this.roads[this.road].getTotalVph()/3600;
    }

    /*
     * Given the duration of a red light, updates how long cars have been waiting
     * @param t_red the length of the red light in seconds
     */
    private void processWaitingCars(double t_red) {
        this.addWaitTime(t_red);
        this.addWaitingCars(t_red);
    }

    /*
     * updates the wait time of cars in a jam with the length of the red light
     * @param t_red the length of the red light in seconds
     */
    private void addWaitTime(double t_red) {
        // the cars already waiting in the queue now wait an additional t_red seconds
        for (StatCar car : this.cars) {
            car.t += t_red;
        }
    }

    /*
     * Given the duration of a red light, updates the list of waiting cars with
     * the cars that have joined the jam during the red light
     * @param t_red the length of the red light in seconds
     */
    private void addWaitingCars(double t_red) {
        for (int i=0; i<t_red*this.r_prime; i++) {
            this.cars.add(new StatCar(t_red-i/r_prime, this.cars.size()*this.cs));
        }
    }

    /*
     * Given the duration of a green light,
     * this method moves all the cars in the traffic jam,
     * removes those that have entered the junction and
     * potentially updates the max wait time with one of the removed stats
     * @param t_green length of a green light in seconds
     */
    private void moveWaitingCars(double t_green) {
        ArrayList<StatCar> temp = new ArrayList<>();
        
        for (StatCar car : this.cars) {
            // move cars
            car.d -= BaseCar.max_speed*t_green;
            
            // check if any of the cars exiting the jam have waited the longest
            // and update max_wait_time accordingly
            if (car.d < 0) {
                this.max_wait_time = Math.max(this.max_wait_time, car.t);
            }
            
            // if they have not left the junction, add them back into the list
            else {
                temp.add(car);
            }
        }

        this.cars = temp;
    }
}
