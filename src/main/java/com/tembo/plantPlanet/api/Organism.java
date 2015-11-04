package com.tembo.plantPlanet.api;

import com.tembo.plantPlanet.OutOfAirException;
import com.tembo.plantPlanet.OutOfEnergyException;
import com.tembo.plantPlanet.OutOfLifeException;
import com.tembo.plantPlanet.OutOfNutrientsException;
import com.tembo.plantPlanet.OutOfWaterException;
import com.tembo.simkern.SimObj;
import com.tembo.simkern.Sim;
import com.tembo.simkern.SimSchedulingException;

public abstract class Organism {

	protected World world;
	
	private static long orgCounter = 1;

	// Organizm identifier
	private long orgId;
	
	public long getOrgId()
	{
		return orgId;
	}
	
	/**
	 * Default constructor is null
	 */
	private Organism()
	{
		orgId = orgCounter++;
	}
	
	/**
	 * The Organism needs to live in the world
	 * @param world
	 */
	public Organism(World world)
	{
		orgId = orgCounter++;
		this.world = world;
	}


	double usedWaterStorageCapacity = 0.0;
	double maxWaterAbsorbable = 10.0;

	/**
	 * @return the usedWaterStorageCapacity
	 */
	public double getUsedWaterStorageCapacity() {
		return usedWaterStorageCapacity;
	}

	/**
	 * everything drinks the water
	 * @param available - provided by the world
	 * @return not used
	 */
	public double drink(double available) 
	{
		double absorbable = (maxWaterAbsorbable - usedWaterStorageCapacity); 
		if(absorbable>available)
		{
			absorbable = available;
		}
		usedWaterStorageCapacity += absorbable;
		
		return available - absorbable;
	}
	
	/**
	 * everything has biomass
	 * @return the computed biomass (size)
	 */
	public double biomass()
	{
		return maxSugarStorable + maxNutrientsAbsorbable + maxWaterAbsorbable;
	}
	
	/** 
	 * Sugar Queue
	 * 
	 */
	double usedSugarStorageCapacity = 10.0; 
	double maxSugarStorable = 10.0;

	/**
	 * @return the usedSugarStorageCapacity
	 */
	public double getUsedSugarStorageCapacity() {
		return usedSugarStorageCapacity;
	}

	/**
	 * @return the maxSugarStorable
	 */
	public double getMaxSugarStorable() {
		return maxSugarStorable;
	}

	/**
	 * consume sugar
	 * @param available - provided by the consumed
	 * @return not used
	 */
	public double consumeSugar(double available) 
	{
		double absorbable = (maxSugarStorable - usedSugarStorageCapacity); 
		if(absorbable>available)
		{
			absorbable = available;
		}
		usedSugarStorageCapacity += absorbable;
		
		return available - absorbable;
	}

	/**
	 * Mineral Queue
	 */
	double usedNutrientStorageCapacity = 0.0;
	double maxNutrientsAbsorbable = 10.0;

	/**
	 * @return the usedNutrientStorageCapacity
	 */
	public double getUsedNutrientStorageCapacity() {
		return usedNutrientStorageCapacity;
	}

	/**
	 * @return the maxNutrientsAbsorbable
	 */
	public double getMaxNutrientsAbsorbable() {
		return maxNutrientsAbsorbable;
	}
	
	/**
	 * absorb
	 * 
	 * @param usedN
	 * @return
	 */
	public double consumeNutrients(double available) {
		double absorbable = (maxNutrientsAbsorbable - usedNutrientStorageCapacity); 
		if(absorbable>available)
		{
			absorbable = available;
		}
		usedNutrientStorageCapacity += absorbable;
		
		return available - absorbable;
	}

	/**
	 * Everything grows.
	 * Growth requires a sugar and an O2 and a mineral, gives off a CO2
	 */	
	public void grow()
	{
		int growthIncr = growthIncrement();

		// Only grow if there is enough sugar and enough minerals to grow each resource
		if(usedSugarStorageCapacity >= growthIncr && usedNutrientStorageCapacity >= growthIncr && usedWaterStorageCapacity >= growthIncr)
		{
			// Use up the sugars to grow
			usedNutrientStorageCapacity -= growthIncr;
			usedSugarStorageCapacity -= growthIncr;
			usedWaterStorageCapacity -= growthIncr;
			
			// Grow
			growByOne();
		}
	}	
	
	/**
	 * The number of units to grow by
	 * @return
	 */
	protected int growthIncrement()
	{
		// Sugar + water + minerals
		return 3;
	}
	
	/**
	 * Add 1 unit to all the resources
	 */
	protected void growByOne()
	{
		maxWaterAbsorbable++;
		maxSugarStorable++;
		maxNutrientsAbsorbable++;
	}

	/**
	 * 
	 * @param sim
	 */
	public void unscheduleAllActivities(Sim sim) 
	{
		if(grow != null)
		{
			sim.cancelSchedulable(grow.getActivityId());
			grow = null;
		}
	}
	
	private Grow grow;
	
	/**
	 * Will create a "Grow" object
	 * @return a grow
	 */
	public SimObj createGrow() 
	{
		if(this.grow == null)
		{
			this.grow = this.new Grow();
		}
		return this.grow;
	}
		
	/**
	 * 
	 * @author angelmi
	 *
	 */
	public class Grow extends SimObj
	{

		@Override
		public void execute(String eventName, double currentTime, int priority,
				Sim sim) throws SimSchedulingException, OutOfWaterException, OutOfEnergyException, OutOfNutrientsException, OutOfAirException, OutOfLifeException 
		{
			grow();
		}
	}
	
	/**
	 * 
	 * @param amtOfEnergyUsed
	 */
	protected void useEnergy(double amtOfEnergyUsed)
	{
		// Always use a unit of energy for the hunt
		usedSugarStorageCapacity -= amtOfEnergyUsed;
		// Need to free a CO2;
		world.freeCO2(1.0);

	}

}
