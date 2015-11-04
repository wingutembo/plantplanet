package com.tembo.plantPlanet.api;

import org.apache.log4j.Logger;

import com.tembo.plantPlanet.api.Animal.Report;
import com.tembo.simkern.SimObj;
import com.tembo.simkern.Sim;
import com.tembo.simkern.SimSchedulingException;


/**
 * For any plant you define, you must implement this interface.
 * The environment will provide you with the available energy, water, co2, and nutrients
 */
public class Plant extends Organism
{

	private static Logger logger = Logger.getLogger(Plant.class.getPackage().getName());

	double usedCO2StorageCapacity = 0.0;
	double usedEnergyStorageCapacity = 0.0;
	
	// Energy, CO2 cannot be stored
	double maxEnergyAbsorbable = 10.0;
	double maxCO2Absorbable = 10.0;


	/** 
	 * World Constructor use defaults
	 */
	public Plant(World world)
	{
		super(world);
	}
	
	/**
	 * World Constructor set initial size and stored energy
	 * 
	 * @param world
	 * @param maxEnergyAbsorbable
	 * @param maxCO2Absorbable
	 * @param maxNutrientsAbsorbable
	 * @param maxSugarStorable
	 * @param usedSugarStorageCapacity
	 */
	public Plant(World world, double maxEnergyAbsorbable, double maxCO2Absorbable, double maxNutrientsAbsorbable, double maxSugarStorable, double usedSugarStorageCapacity)
	{
		super(world);
		
		this.maxEnergyAbsorbable = maxEnergyAbsorbable;
		this.maxCO2Absorbable = maxCO2Absorbable;
		this.maxNutrientsAbsorbable = maxNutrientsAbsorbable;
		this.maxSugarStorable = maxSugarStorable;
		this.usedSugarStorageCapacity = usedSugarStorageCapacity;
	}
	

	/**
	 * @return the usedCO2StorageCapacity
	 */
	public double getUsedCO2StorageCapacity() {
		return usedCO2StorageCapacity;
	}

	/**
	 * @return the usedEnergyStorageCapacity
	 */
	public double getUsedEnergyStorageCapacity() {
		return usedEnergyStorageCapacity;
	}

	/**
	 * @return the maxEnergyAbsorbable
	 */
	public double getMaxEnergyAbsorbable() {
		return maxEnergyAbsorbable;
	}

	/**
	 * @return the maxCO2Absorbable
	 */
	public double getMaxCO2Absorbable() {
		return maxCO2Absorbable;
	}

	/**
	 * eat the nutrients
	 * @param available - provided by the world
	 * @return not used
	 */
	public double eat(double available) 
	{
		double absorbable = (maxNutrientsAbsorbable - usedNutrientStorageCapacity); 
		if(absorbable>available)
		{
			absorbable = available;
		}
		usedNutrientStorageCapacity += absorbable;
		
		return available - absorbable;
	}

	/**
	 * breath the CO2
	 * @param available - provided by the world
	 * @return not used
	 */
	public double breath(double available) 
	{
		double absorbable = (maxCO2Absorbable - usedCO2StorageCapacity); 
		if(absorbable>available)
		{
			absorbable = available;
		}
		usedCO2StorageCapacity += absorbable;
		
		return available - absorbable;
	}

	/**
	 * photosynthesize
	 * @param available - provided by the world
	 * @return not used
	 */
	public double photosynthesize(double available) 
	{
		double absorbable = (maxEnergyAbsorbable - usedEnergyStorageCapacity); 
		if(absorbable>available)
		{
			absorbable = available;
		}
		usedEnergyStorageCapacity += absorbable;
		
		return available - absorbable;
	}

	/**
	 * Biomass is the sum of its components
	 * @return the biomass
	 */
	@Override
	public double biomass() 
	{
		double biomass = super.biomass();
		biomass += maxEnergyAbsorbable + maxCO2Absorbable;
		return biomass;
	}
	
	/**
	 * The number of units to grow by
	 * @return
	 */
	protected int growthIncrement()
	{
		// Sugar + water
		return super.growthIncrement()+2;
	}
	
	/**
	 * Add 1 unit to all the resources
	 */
	protected void growByOne()
	{
		super.growByOne();
		maxEnergyAbsorbable++;
		maxCO2Absorbable++;
	}

	private StoreEnergy storeEnergy;
	
	/**
	 * Returns a store energy
	 * @return
	 */
	public StoreEnergy createStoreEnergy()
	{
		if(this.storeEnergy == null)
		{
			this.storeEnergy = this.new StoreEnergy();
		}
		return this.storeEnergy;
	}
	
	/**
	 * 
	 * @param sim
	 */
	@Override
	public void unscheduleAllActivities(Sim sim) 
	{
		super.unscheduleAllActivities(sim);
		
		if(storeEnergy != null)
		{
			storeEnergy.unschedule(sim);
			this.storeEnergy = null;
		}

		if(report != null)
		{
			report.unschedule(sim);
			this.report = null;
		}

}

	/**
	 * Process the StoreEnergy event
	 * 
	 * @author angelmi
	 *
	 */
	public class StoreEnergy extends SimObj
	{
		/**
		 * Default constructor can only be constructed by this object
		 */
		private StoreEnergy()
		{
			
		}
		
		@Override
		public void execute(String eventName, double currentTime, int priority,
				Sim sim) throws SimSchedulingException 
		{

			logger.trace("Store Energy@"+currentTime/24.0);

			// Equal parts CO2, H20, Energy make a unit of sugar			
			double newSugar = Math.min(Math.min(usedEnergyStorageCapacity, usedCO2StorageCapacity), usedWaterStorageCapacity);
			
			// Reduce used by amount required to make new sugar
			usedEnergyStorageCapacity -= newSugar;
			usedCO2StorageCapacity -= newSugar;
			usedWaterStorageCapacity -= newSugar;
			
			double wasteSugar;
			
			if(newSugar>(maxSugarStorable-usedSugarStorageCapacity))
			{
				wasteSugar = newSugar - maxSugarStorable;
				usedSugarStorageCapacity = maxSugarStorable;
			}
			else
			{
				wasteSugar = 0.0;
				usedSugarStorageCapacity += newSugar;
			}
			
			// Energy cannot be stored, it should go to 0
			// Energy is simply lost
			usedEnergyStorageCapacity = 0.0;
			
			// CO2 cannot be stored, it should go to 0
			// CO2 must be conserved
			world.freeCO2(usedCO2StorageCapacity);
			usedCO2StorageCapacity = 0.0;
			
			// Excess sugar is donated to the world
			world.freeSugar(wasteSugar); 
			// O2 is by-product of each sugar made
			world.freeO2(newSugar);
			
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
				logger.info("\t\t\tCO2    = "+usedCO2StorageCapacity+" of "+maxCO2Absorbable);
				logger.info("\t\t\tH2O    = "+usedWaterStorageCapacity+" of "+maxWaterAbsorbable);
				logger.info("\t\t\tN      = "+usedNutrientStorageCapacity+" of "+maxNutrientsAbsorbable);
				logger.info("\t\t\tSugar  = "+usedSugarStorageCapacity+" of "+maxSugarStorable);
				logger.info("\t\t\tEnergy = "+usedEnergyStorageCapacity+" of "+maxEnergyAbsorbable);
			}
		}
	}

}




