/**
 * 
 */
package com.tembo.plantPlanet;

import static org.junit.Assert.fail;

import java.io.OutputStreamWriter;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tembo.plantPlanet.api.Decomposer;
import com.tembo.plantPlanet.api.Waste;
import com.tembo.plantPlanet.api.World;
import com.tembo.simkern.Sim;
import com.tembo.simkern.SimSchedulingException;

/**
 * @author angelmi
 *
 */
public class OrganismTest {

    static Logger logger = Logger.getLogger(OrganismTest.class.getPackage().getName());
    {
    	// Console Appender
        ConsoleAppender ap = new ConsoleAppender();
        ap.setWriter(new OutputStreamWriter(System.err));
        ap.setLayout(new PatternLayout("%d [%p|%c|%C{1}] %m%n"));
        logger.removeAllAppenders();
        logger.addAppender(ap);
        logger.setLevel(Level.ALL);

        // File Appender
        FileAppender fa = new FileAppender();
        fa.setName("OrganismTestFileLogger");
        fa.setFile("OrganismTest.log");
        fa.setLayout(new PatternLayout("%d [%p|%c|%C{1}] %m%n"));
        fa.setThreshold(Level.ALL);
        fa.setAppend(true);
        fa.activateOptions();
        logger.setLevel(Level.ALL);
        
    	Logger rootLogger = Logger.getRootLogger();
    	rootLogger.setLevel(Level.DEBUG);
        rootLogger.addAppender(fa);
        logger.debug("PRINT THIS MESSAGE");
    }
    
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void decomposers() 
	{
        // Create a Simulation
    	Sim sim = new Sim();

    	// Create a world
        World w = new World();
        w.setCO2(100.0);
        w.setO2(100.0);
        
        // Create a world with waste, so the decomposer has something to decompose
        Waste waste = w.createWaste(100.0,100.0);

        // Create a decomposer
        Decomposer d = w.createDecomposer();

    	// Digest, because that is what digesters do
    	try {
			sim.scheduleContinuousActivity("Digest", 1.0, 8, d.createDigest());
			sim.scheduleContinuousActivity("Report", 1.0, 18, d.createReport());
	    	sim.scheduleContinuousActivity("Report", 1.0, 100, w.new Report());
		} catch (SimSchedulingException e) {
			e.printStackTrace();
			fail(e.toString());
		}

    	// Run the simulation for one year
    	try {
			sim.runSim(365.0*24.0);
		} catch (SimSchedulingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class TestWorld extends World
	{
		/*
		 * (non-Javadoc)
		 * @see com.tembo.plantPlanet2.api.World#createDecomposer()
		 */
		@Override
		public Decomposer createDecomposer()
		{
			Decomposer d = new TestDecomposer(this);
			TestWorld.this.decomposers.add(d);
			return d;
		}		
	}
	
	class TestDecomposer extends Decomposer
	{

		public TestDecomposer(World world) {
			super(world);
		}
		
	}

}
