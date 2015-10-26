package com.tembo.plantPlanet1;

import java.io.OutputStreamWriter;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.tembo.plantPlanet2.App;
import com.tembo.plantPlanet2.OutOfAirException;
import com.tembo.plantPlanet2.OutOfEnergyException;
import com.tembo.plantPlanet2.OutOfLifeException;
import com.tembo.plantPlanet2.OutOfNutrientsException;
import com.tembo.plantPlanet2.OutOfWaterException;
import com.tembo.plantPlanet2.api.Plant;
import com.tembo.plantPlanet2.api.Plant.StoreEnergy;
import com.tembo.plantPlanet2.api.World;
import com.tembo.plantPlanet2.api.World.AllocateResources;
import com.tembo.simkern.Sim;
import com.tembo.simkern.SimSchedulingException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class InterfaceTest extends TestCase 
{
    static Logger logger = Logger.getLogger(InterfaceTest.class.getPackage().getName());
    {
        ConsoleAppender ap = new ConsoleAppender();
        ap.setWriter(new OutputStreamWriter(System.err));
        ap.setLayout(new PatternLayout("%d [%p|%c|%C{1}] %m%n"));
        logger.removeAllAppenders();
        logger.addAppender(ap);
        
        logger.setLevel(Level.ALL);

    }

	public void testAllocateResourcesEmptyWorld()
	{
		World w = new World();
		AllocateResources ar = w.new AllocateResources();
    	Sim sim = new Sim();

		try {
			ar.execute("Test", 1.0, 1, sim);
			fail("No Organisms");
		} catch (OutOfWaterException | OutOfEnergyException
				| OutOfNutrientsException | OutOfAirException
				| OutOfLifeException | SimSchedulingException e) {
			logger.info("Expected Branch");
			// Expected Branch
		}
	}
	
	public void testAllocateResourcesAndStoreEnergyOnePlant()
	{
		double freeCO2 = 1000;
		double freeO2 = 0;
		double freeWater = 1000;
        double freeNutrients = 1000;
        double freeEnergy = 1000;

        double maxEnergyAbsorbable = 10.0;
        double maxCO2Absorbable = 10.0;
        double maxNutrientsAbsorbable = 10.0; 
        double maxSugarStorable = 10.0;
        double usedSugarStorageCapacity = 0.0;
        
        World w = new World(freeCO2, freeO2, freeWater, freeNutrients, freeEnergy);
		Plant p = w.createPlant(maxEnergyAbsorbable,maxCO2Absorbable,maxNutrientsAbsorbable,maxSugarStorable,usedSugarStorageCapacity);
		AllocateResources ar = w.new AllocateResources();
		StoreEnergy se = p.createStoreEnergy();
    	Sim sim = new Sim();

		try {
			ar.execute("TestAllocateResources", 1.0, 1, sim);
			
			assertEquals(990.0,w.getFreeCO2());
			assertEquals(990.0,w.getFreeWater());
			assertEquals(990.0,w.getFreeNutrients());
			assertEquals(990.0,w.getFreeEnergy());
			assertEquals(0.0,w.getFreeO2());
			
			assertEquals(10.0,p.getMaxEnergyAbsorbable());
			assertEquals(10.0,p.getMaxCO2Absorbable());
			assertEquals(10.0,p.getMaxNutrientsAbsorbable());
			assertEquals(10.0,p.getMaxSugarStorable());
			assertEquals(0.0,p.getUsedSugarStorageCapacity());
			
			se.execute("TestStoreEnergy", 1.0, 2, sim);
			
			assertEquals(0.0,p.getUsedCO2StorageCapacity());
			assertEquals(0.0,p.getUsedEnergyStorageCapacity());
			assertEquals(10.0,p.getUsedNutrientStorageCapacity());
			assertEquals(10.0,p.getUsedSugarStorageCapacity());
			
			assertEquals(990.0,w.getFreeCO2());
			assertEquals(990.0,w.getFreeWater());
			assertEquals(990.0,w.getFreeNutrients());
			assertEquals(990.0,w.getFreeEnergy());
			assertEquals(0.0,w.getFreeO2());
			
			
		} catch (OutOfWaterException | OutOfEnergyException
				| OutOfNutrientsException | OutOfAirException
				| OutOfLifeException | SimSchedulingException e) {
			Assert.fail(e.toString());
			// Expected Branch
		}
	}

}
