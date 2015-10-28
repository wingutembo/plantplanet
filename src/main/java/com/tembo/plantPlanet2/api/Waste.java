package com.tembo.plantPlanet2.api;

class Waste {

	double unusedN;
	double unusedSugar;

	public Waste(double unusedN, double unusedSugar) 
	{
		this.unusedN = unusedN;
		this.unusedSugar = unusedSugar;
	}

	public boolean notEmpty() {
		return unusedN > 0 || unusedSugar > 0;
	}

	/**
	 * Use this to accumulate waste
	 * @param waste
	 */
	public void add(Waste waste)
	{
		unusedN += waste.unusedN;
		unusedSugar += waste.unusedSugar;
	}
}
