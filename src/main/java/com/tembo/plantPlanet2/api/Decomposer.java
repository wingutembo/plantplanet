package com.tembo.plantPlanet2.api;

import org.apache.log4j.Logger;

public class Decomposer extends Organism {

	private static Logger logger = Logger.getLogger(Decomposer.class.getPackage().getName());

	public Decomposer(World world) {
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
