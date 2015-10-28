package com.tembo.plantPlanet2;

import java.io.OutputStreamWriter;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.tembo.plantPlanet2.api.Animal;
import com.tembo.plantPlanet2.api.Decomposer;
import com.tembo.plantPlanet2.api.Plant;
import com.tembo.plantPlanet2.api.World;
import com.tembo.simkern.Sim;
import com.tembo.simkern.SimSchedulingException;

/**
 * Hello world!
 *
 */
public class App 
{
    static Logger logger = Logger.getLogger(App.class.getPackage().getName());
    {
        ConsoleAppender ap = new ConsoleAppender();
        ap.setWriter(new OutputStreamWriter(System.err));
        // ap.setLayout(new PatternLayout("%d [%p|%c|%C{1}] %m%n"));
        ap.setLayout(new PatternLayout("%m%n"));
        logger.removeAllAppenders();
        logger.addAppender(ap);
        
        logger.setLevel(Level.DEBUG);

    }

    public static void main( String[] args ) throws SimSchedulingException
    {
    	// Create the world!
        World w = new World();
        Plant p = w.createPlant();
        Animal a = w.createAnimal();
        Decomposer d = w.createDecomposer();
        
        // Create a Simulation
    	Sim sim = new Sim();
    	
    	// Day starts at 0.0
    	sim.scheduleDiscreteEvent("Day", 0.0, 2, w.new Day());
    	// Night starts at 12.0
    	sim.scheduleDiscreteEvent("Night", 12.0, 2, w.new Night());

    	// Start the rainy season at 0.0
    	sim.scheduleDiscreteEvent("Wet", 0.0, 3, w.new Wet());
    	// Start the dry season at 180.0 days
    	sim.scheduleDiscreteEvent("Dry", 180.0*24.0, 3, w.new Dry());
    	// Start the cycle of rain - for now fixed at every 3 days
    	sim.scheduleContinuousActivity("Rain", 24.0*3, 4, w.new Rain());

    	// Schedule the allocation of resources
    	sim.scheduleContinuousActivity("Allocate", 1.0, 5, w.new AllocateResources());

    	// Schedule the creation of stored energy
    	sim.scheduleContinuousActivity("StoreEnergy", 1.0, 6, p.createStoreEnergy());
    	
    	// Hunt or Digest - Do one or the other
    	sim.scheduleContinuousActivity("Hunt", 1.0, 7, a.createHunt());
    	sim.scheduleContinuousActivity("Digest", 1.0, 8, d.createDigest());
    	
    	// Schedule the creation of stored energy
    	sim.scheduleContinuousActivity("Grow", 1.0, 10, p.createGrow());
    	sim.scheduleContinuousActivity("Grow", 1.0, 10, a.createGrow());
    	sim.scheduleContinuousActivity("Grow", 1.0, 10, d.createGrow());

    	// Schedule the report for once a day, after everything else is update
    	sim.scheduleContinuousActivity("Report", 24.0, 100, w.new Report());

    	// Run the simulation for one year
    	sim.runSim(365.0*24.0);

    }


}
