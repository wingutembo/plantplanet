package com.tembo.plantPlanet.api;

import org.apache.log4j.Logger;

import com.tembo.plantPlanet.OutOfAirException;
import com.tembo.plantPlanet.OutOfEnergyException;
import com.tembo.plantPlanet.OutOfLifeException;
import com.tembo.plantPlanet.OutOfNutrientsException;
import com.tembo.plantPlanet.OutOfWaterException;
import com.tembo.plantPlanet.api.Decomposer.Report;
import com.tembo.simkern.Sim;
import com.tembo.simkern.SimObj;
import com.tembo.simkern.SimSchedulingException;

public class Animal extends Organism 
{

	private static Logger logger = Logger.getLogger(Animal.class.getPackage().getName());
	
	double usedO2StorageCapacity = 0.0;
	double maxO2Absorbable = 10.0;
	
	/**
	 * World constructor
	 * @param world
	 */
	public Animal(World world) {
		super(world);
	}

	/**
	 * Biomass is the sum of its components
	 * @return the biomass
	 */
	@Override
	public double biomass() 
	{
		double biomass = super.biomass();
		biomass+=maxO2Absorbable;
		return biomass;
	}

	/**
	 * The number of units to grow by 
	 *     O2 is specific to animal
	 * @return
	 */
	protected int growthIncrement()
	{
		// Sugar + water
		return super.growthIncrement()+1;
	}
	
	/**
	 * Add 1 unit to all the resources
	 */
	protected void growByOne()
	{
		super.growByOne();
		maxO2Absorbable++;
	}

	/**
	 * convert biomass into nutrients and sugar
	 * @param biomass
	 * @return the waste
	 */
	public Waste digest(double biomass) 
	{
		double unusedN = this.consumeNutrients(biomass);
		double unusedSugar = this.consumeSugar(biomass);
		return new Waste(unusedN, unusedSugar);
	}


	private Hunt hunt;
	
	/**
	 * Returns a store energy
	 * @return
	 */
	public Hunt createHunt()
	{
		if(this.hunt == null)
		{
			this.hunt = this.new Hunt();
		}
		return this.hunt;
	}

	/**
	 * 
	 * @param sim
	 */
	@Override
	public void unscheduleAllActivities(Sim sim) 
	{
		super.unscheduleAllActivities(sim);
		
		if(hunt != null)
		{
			hunt.unschedule(sim);
			this.hunt = null;
		}
		
		if(report != null)
		{
			report.unschedule(sim);
			this.report = null;
		}

	}

	/**
	 * 
	 * @author angelmi
	 *
	 */
	public class Hunt extends SimObj
	{
		/**
		 * Default constructor is private
		 */
		private Hunt()
		{
			
		}

		@Override
		public void execute(String eventName, double currentTime, int priority,
				Sim sim) throws SimSchedulingException, OutOfWaterException, OutOfEnergyException, OutOfNutrientsException, OutOfAirException, OutOfLifeException 
		{
			double plantBiomass = world.plantBiomass();
			
			double probCatch = plantBiomass / world.getSize();
			
			double r = Math.random();
			
			if(r < probCatch)
			{
				// Simulate catch
				world.consumePlant(sim, Animal.this);

			}
			
			// Always use a unit of energy for the hunt
			useEnergy(1.0);
			
			if(usedSugarStorageCapacity <= 0.0)
			{
				usedSugarStorageCapacity = 0.0;
				world.killAnimal(Animal.this, sim);
			}
		}
	}

	
	private Report report;

	/**
	 * create the event
	 * @return
	 */
	public SimObj createReport() {
		if(this.report == null)
		{
			this.report = this.new Report();
		}
		return this.report;
	}

	/**
	 * 
	 * @author angelmi
	 *
	 */
	public class Report extends SimObj
	{
		/**
		 * Default constructor is private
		 */
		private Report()
		{
			
		}
		
		@Override
		public void execute(String eventName, double currentTime, int priority,
				Sim sim) throws SimSchedulingException {

			if(logger.isInfoEnabled())
			{
				logger.info(eventName+" Report@"+currentTime/24.0);

				logger.info("\t\tID = "+getOrgId());
				logger.info("\t\t\tO2    = "+usedO2StorageCapacity+" of "+maxO2Absorbable);
				logger.info("\t\t\tH2O    = "+usedWaterStorageCapacity+" of "+maxWaterAbsorbable);
				logger.info("\t\t\tN      = "+usedNutrientStorageCapacity+" of "+maxNutrientsAbsorbable);
				logger.info("\t\t\tSugar  = "+usedSugarStorageCapacity+" of "+maxSugarStorable);
			}
		}
	}
	

}


