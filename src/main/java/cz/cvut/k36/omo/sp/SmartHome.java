package cz.cvut.k36.omo.sp;

import cz.cvut.k36.omo.sp.simulation.Simulation;
import lombok.Getter;

import java.util.logging.Logger;

public class SmartHome {

    /**
     * Before starting the simulation, you need to set the parameters:
     *  configuration number 1 or 2,
     *  date and time in the String in format "yyyy-MM-ddTHH:mm:ss",
     *  simulation duration in minutes.
     *
     * @param arc array of strings.
     */
    public static void main(String... arc) {
        Simulation simulation = new Simulation(1, "2022-01-09T07:12:00", 1200);
        simulation.start();
    }
}