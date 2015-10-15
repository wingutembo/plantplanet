package com.tembo.plantPlanet1;

import java.io.OutputStreamWriter;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.tembo.simkern.ISimObj;
import com.tembo.simkern.Sim;
import com.tembo.simkern.SimSchedulingException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    static Logger logger = Logger.getLogger(Sim.class.getPackage().getName());
    {
        ConsoleAppender ap = new ConsoleAppender();
        ap.setWriter(new OutputStreamWriter(System.err));
        ap.setLayout(new PatternLayout("%d [%p|%c|%C{1}] %m%n"));
        logger.removeAllAppenders();
        logger.addAppender(ap);
        
        logger.setLevel(Level.ALL);

    }
    
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * @throws SimSchedulingException 
     * 
     */
    long id = 0;
    
    public void testSim() throws SimSchedulingException
    {
    	Sim sim = new Sim();
    	sim.scheduleDiscreteEvent("Event 1", 0.0, 1, new MyEvent());
    	sim.scheduleDiscreteEvent("Event 2", 0.0, 2, new MyEvent());
    	id = sim.scheduleContinuousActivity("Activity 1", 1.0, 3, new MyActivity());
    	sim.scheduleContinuousActivity("Activity 2", 1.0, 4, new MyActivity());
    	
    	sim.runSim(100.0);
    }

    class MyEvent implements ISimObj
    {
    	int i = 1;
    	
    	@Override
    	public void execute(String occurrenceName, double currentTime, int priority, Sim sim) throws SimSchedulingException 
    	{
    		AppTest.logger.debug("Executing "+occurrenceName+" event at "+currentTime+" with priority="+priority);
    		
    		sim.scheduleDiscreteEvent(occurrenceName, i*1.0, priority, this);
    		i++;
    	}
    	
    }

    class MyActivity implements ISimObj
    {
    	@Override
    	public void execute(String occurrenceName, double currentTime, int priority, Sim sim) throws SimSchedulingException 
    	{
    		AppTest.logger.debug("Executing "+occurrenceName+" event at "+currentTime+" with priority="+priority);
    		
    		// Cancel Self
    		if(currentTime>=50.0 && priority==3)
    		{
    			sim.cancelSchedulable(id);
    		}
    	}
    	
    }
}

