package com.tembo.plantPlanet;

import java.io.OutputStreamWriter;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.tembo.plantPlanet.api.Animal;
import com.tembo.plantPlanet.api.Decomposer;
import com.tembo.plantPlanet.api.Plant;
import com.tembo.plantPlanet.api.World;
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

        // File Appender
        FileAppender fa = new FileAppender();
        fa.setName("SimFileLogger");
        fa.setFile("Sim.log");
        // fa.setLayout(new PatternLayout("%d [%p|%c|%C{1}] %m%n"));
        fa.setLayout(new PatternLayout("%m%n"));
        fa.setThreshold(Level.DEBUG);
        fa.setAppend(true);
        fa.activateOptions();
        
    	Logger rootLogger = Logger.getRootLogger();
    	rootLogger.setLevel(Level.DEBUG);
        rootLogger.addAppender(fa);
        logger.debug("PRINT THIS MESSAGE");
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
    	sim.scheduleDiscreteEvent("Day", 0.0, 2, w.createDay());
    	// Night starts at 12.0
    	sim.scheduleDiscreteEvent("Night", 12.0, 2, w.createNight());

    	// Start the rainy season at 0.0
    	sim.scheduleDiscreteEvent("Wet", 0.0, 3, w.createWet());
    	// Start the dry season at 180.0 days
    	sim.scheduleDiscreteEvent("Dry", 180.0*24.0, 3, w.createDry());
    	// Start the cycle of rain - for now fixed at every 3 days
    	sim.scheduleContinuousActivity("Rain", 24.0*3, 4, w.createRain());

    	// Schedule the allocation of resources
    	sim.scheduleContinuousActivity("Allocate", 1.0, 5, w.createAllocateResources());

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
    	sim.scheduleContinuousActivity("World Report", 24.0, 100, w.new Report());
		sim.scheduleContinuousActivity("P1 Report", 1.0, 18, p.createReport());
		sim.scheduleContinuousActivity("A1 Report", 1.0, 18, a.createReport());
		sim.scheduleContinuousActivity("D1 Report", 1.0, 18, d.createReport());

    	// Run the simulation for one year
    	sim.runSim(365.0*24.0);

    }


}
