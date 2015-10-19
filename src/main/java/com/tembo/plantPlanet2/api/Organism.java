package com.tembo.plantPlanet2.api;

public abstract class Organism {

	protected World world;

	/**
	 * Default constructor is null
	 */
	private Organism()
	{
		
	}
	
	/**
	 * The Organism needs to live in the world
	 * @param world
	 */
	public Organism(World world)
	{
		this.world = world;
	}


	double usedWaterStorageCapacity = 0.0;
	double maxWaterAbsorbable = 10.0;

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
	public abstract double biomass();
	
	/** 
	 * Sugar Queue
	 * 
	 */
	double usedSugarStorageCapacity = 0.0; 
	double maxSugarStorable = 10.0;

	/**
	 * Mineral Queue
	 */
	double usedNutrientStorageCapacity = 0.0;
	double maxNutrientsAbsorbable = 10.0;

	
	/**
	 * Everything grows.
	 * Growth requires a sugar and an O2 and a mineral, gives off a CO2
	 */	
	public void grow()
	{
		int growthIncr = growthIncrement();

		// Only grow if there is enough sugar and enough minerals to grow each resource
		if(usedSugarStorageCapacity >= growthIncr && usedNutrientStorageCapacity >= growthIncr)
		{
			// Use up the sugars to grow
			usedSugarStorageCapacity -= growthIncr;
			usedNutrientStorageCapacity -= growthIncr;
			
			// Give off CO2
			world.freeCO2(growthIncr);
			
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
}
