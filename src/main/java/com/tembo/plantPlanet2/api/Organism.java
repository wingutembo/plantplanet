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
	


}
