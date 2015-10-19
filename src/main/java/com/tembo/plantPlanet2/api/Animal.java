package com.tembo.plantPlanet2.api;

import org.apache.log4j.Logger;

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
		double biomass = maxWaterAbsorbable;
		return biomass;
	}

	/**
	 * The number of units to grow by
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

	public void hunt() {
		// TODO Auto-generated method stub
		
	}

}
