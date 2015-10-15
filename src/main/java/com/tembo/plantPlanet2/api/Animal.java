package com.tembo.plantPlanet2.api;

import org.apache.log4j.Logger;

public class Animal extends Organism 
{

	private static Logger logger = Logger.getLogger(Animal.class.getPackage().getName());
	
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

}
