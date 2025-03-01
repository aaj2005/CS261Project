package com.example;

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

    private double max_queue_length = 0;
    
    /*
     * Constructor for StatCalculator
     * @param road The road to run the calculations for
     */
    public StatCalculator(int road) {
        this.road = road;
        this.calculateJamLengtheningRate();
    }

    /*
     * Calculates how quickly the jam lengthens in metres per second
     * Saves the calculated variable in this.jamLengtheningRate
     */
    private void calculateJamLengtheningRate() {
        double r = this.getArrivalRate();
        
        if (DoubleCompare.approximatelyEqual(r, 0)) { // avoids a divisionby0 error
            this.jam_lengthening_rate = 0;
        } else {
            int num_lanes = ((Road)DynamicComponents.junction_elements.get(this.road)).lanes;
            double cs = (Car.length + Car.distance)/num_lanes;
            double r_prime = 1/(1/r - cs*Car.max_speed);
            this.jam_lengthening_rate = r_prime * cs;
        }
    }

    /*
     * Calculates how may seconds it takes for the jam to deplete
     */
    private double getJamDepletionTime() {
        return this.jam_length * Car.max_speed;
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
        double tRedStart = this.t;

        while (!this.endOfCycleReached()) {
            // red light from road 0 to road n
            this.tickRedLight(0, this.road);
            
            // lengthen the traffic jam due to the fact that the light was red
            this.jam_length += (this.t-tRedStart)*this.jam_lengthening_rate;

            // increase the total wait time
            this.total_wait_time += this.getWaitTime(this.jam_length, this.t-tRedStart);
            
            // update max queue length
            this.max_queue_length = Math.max(this.max_queue_length, this.jam_length);

            // calculate when the green light will end
            double t_green_end = this.t + getRoadLightLength(this.road);

            // the traffic jam take n seconds to deplete
            // the green light is on for g seconds
            // hence the traffic jam will actually deplete for s = min(g, n) seconds
            double jam_depletion_time = Math.min(this.getJamDepletionTime(), getRoadLightLength(this.road));

            // jump to the end of the green light
            t = t_green_end;

            // decrease the size of the jam
            this.jam_length -= jam_depletion_time*Car.max_speed;

            // if the initialjamlength hasn't been set yet,
            // set it to be length of the jam after the green traffic light has just turned red
            if (initial_jam_length < 0) {
                this.initial_jam_length = this.jam_length;
            }

            // calculate the length of the red light from when the light turns red for road n
            // to when the light turns green for road 0
            // this allows us to check if the end of the cycle has been reached
            tRedStart = this.t;
            tickRedLight(this.road+1, 0);
        }

        // first cycle done! Now store how long the cycle was
        this.cycle_length = this.t;

        // continue the simulation up until the end of next green light for the road currently under inspection
        // this allows us to compare the queue length at this point in cycle 2 to
        // the queue length at this point in cycle 1 (this.initialqueuelength)
        tickRedLight(0, this.road);
        this.jam_length += (this.t-tRedStart)*this.jam_lengthening_rate;
        this.total_wait_time += this.getWaitTime(this.jam_length, this.t-tRedStart);
        double jam_depletion_time = Math.min(this.getJamDepletionTime(), getRoadLightLength(this.road));
        this.t += jam_depletion_time;
        this.jam_length -= jam_depletion_time*Car.max_speed;
        this.max_queue_length = Math.max(this.max_queue_length, this.jam_length); // final check for max queue length

        if (DoubleCompare.greaterThan(this.jam_length, this.initial_jam_length)) {
            throw new InvalidParametersException("Traffic length grows to infinity!");
        }

        return new Stats(
            0,
            this.max_queue_length/ (Car.length + Car.distance),
            this.total_wait_time/(this.cycle_length*this.getArrivalRate())
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
        return DoubleCompare.approximatelyEqual(this.t % getPedLightRate(), getPedLightLength())

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
        while (DoubleCompare.geq(t, this.last_ped_light + getPedLightRate())) {
            this.t += getPedLightLength();
            this.last_ped_light += getPedLightRate();
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
        return ((PedestrianCrossing)DynamicComponents.junction_elements.get(DynamicComponents.junction_elements.size()-1)).ped_light_rate;
    }

    private double getPedLightLength() {
        return ((PedestrianCrossing)DynamicComponents.junction_elements.get(DynamicComponents.junction_elements.size()-1)).ped_light_length;
    }

    private double getRoadLightLength(int r) {
        return ((Road)DynamicComponents.junction_elements.get(r)).actual_light_duration;
    }

    /*
     * gets the cumulative wait time on a road
     * @param queueLen length of the initial queue (at the start of the red light) in metres
     * @param t length of the red light in seconds
     */
    private double getWaitTime(double queueLen, double t) {
        // okay so scrap whatever calculation we were doing
        // this calculation is much simpler and achieves the same result
        // r cars arrive per second where r = jam_lengthening_rate/car.length
        // so in t seconds, t*r cars arrive
        // arriving cars wait on average t/2 seconds

        double r = this.getArrivalRate();
        return t*r*(queueLen/Car.length) // how long the cars in the initial queue have to wait
             + t*r*t/2;                  // how long the arriving cars have to wait
    }

    /*
     * returns the arrival rate of cars to the junction
     */
    private double getArrivalRate() {
        return ((Road)DynamicComponents.junction_elements.get(this.road)).inbound_vph/3600;
    }
}
