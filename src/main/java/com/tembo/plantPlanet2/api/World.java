package com.tembo.plantPlanet2.api;

import java.io.OutputStreamWriter;
import java.util.LinkedList;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.tembo.plantPlanet2.OutOfAirException;
import com.tembo.plantPlanet2.OutOfEnergyException;
import com.tembo.plantPlanet2.OutOfLifeException;
import com.tembo.plantPlanet2.OutOfNutrientsException;
import com.tembo.plantPlanet2.OutOfWaterException;
import com.tembo.simkern.ISimObj;
import com.tembo.simkern.Sim;
import com.tembo.simkern.SimSchedulingException;

public class World {
	
	private static Logger logger = Logger.getLogger(World.class.getPackage().getName());
    {
        ConsoleAppender ap = new ConsoleAppender();
        ap.setWriter(new OutputStreamWriter(System.err));
        ap.setLayout(new PatternLayout("%d [%p|%c|%C{1}] %m%n"));
        logger.removeAllAppenders();
        logger.addAppender(ap);
        
        logger.setLevel(Level.ALL);

    }

	private double freeCO2 = 100000;
	private double freeO2 = 0;
    private double freeWater = 100000;
    private double freeNutrients = 100000;
    
    private static final double MAX_FREE_ENERGY = 100000;
	public static final double MAX_DRY_SEASON_WATER = 50000;
	public static final double MAX_WET_SEASON_WATER = 100000;
	public double maxWater = MAX_WET_SEASON_WATER;

	private double freeEnergy = MAX_FREE_ENERGY;
	private double freeWaste = 0;
	private double freeDeath = 0;
    
	/**
	 * Default constructor, use all default values
	 */
	public World()
	{
		
	}
	
	/**
	 * Constructor, set all initial values
	 * 
	 * @param freeCO2
	 * @param freeO2
	 * @param freeWater
	 * @param freeNutrients
	 * @param freeEnergy
	 */
	public World(double freeCO2, double freeO2, double freeWater, double freeNutrients, double freeEnergy)
	{
		this.freeCO2 = freeCO2;
		this.freeO2 = freeO2;
		this.freeWater = freeWater;
		this.freeNutrients = freeNutrients;
		this.freeEnergy = freeEnergy;
	}
	
	/** Free as in available **/
	
    public double getFreeCO2() {
		return freeCO2;
	}

    public double getFreeO2() {
		return freeO2;
	}

	public double getFreeWater() {
		return freeWater;
	}

	public double getFreeNutrients() {
		return freeNutrients;
	}

	public double getFreeEnergy() {
		return freeEnergy;
	}
	
	public double getFreeWaste() {
		return freeWaste;
	}

	public double getFreeDeath() {
		return freeDeath;
	}

    /**
     * 
     * @param dCO2 the delta loss of CO2
     * @throws OutOfAirException 
     */
    public void useCO2(double dCO2) throws OutOfAirException
    {
    	freeCO2 -= dCO2;
    	
    	if(freeCO2 < 0)
    	{
    		freeCO2 = 0;
    		
    		throw new OutOfAirException();
    	}
    	
    }
    
    /**
     * 
     * @param dCO2 the delta gain in CO2
     */
    public void freeCO2(double dCO2)
    {
    	freeCO2 += dCO2;
    }
    
    /**
     * This is waste sugar
     * @param dWaste the change in waste
     */
    public void freeWaste(double dWaste)
    {
    	freeWaste += dWaste;
    }
    
    /**
     * 
     * @param dWater
     * @throws OutOfWaterException 
     */
    public void useWater(double dWater) throws OutOfWaterException
    {
    	freeWater -= dWater;

    	if(freeWater < 0)
    	{
    		freeWater = 0;
    		
    		throw new OutOfWaterException();
    	}
    }
    
    /**
     * 
     * @param dNutrients
     * @throws OutOfNutrientsException 
     */
    public void useNutrients(double dNutrients) throws OutOfNutrientsException
    {
    	freeNutrients -= dNutrients;

    	if(freeNutrients < 0)
    	{
    		freeNutrients = 0;
    		
    		throw new OutOfNutrientsException();
    	}
    }

    /**
     * 
     * @param dEnergy
     */
	public void useEnergy(double dEnergy) throws OutOfEnergyException
	{
    	freeEnergy -= dEnergy;

    	if(freeEnergy < 0)
    	{
    		freeEnergy = 0;
    		
    		throw new OutOfEnergyException();
    	}
	}
	
	
	private LinkedList<Plant> plants = new LinkedList<Plant>();
	private LinkedList<Animal> animals = new LinkedList<Animal>();
	private LinkedList<Decomposer> decomposers = new LinkedList<Decomposer>();
	
	/**
	 * create a plant
	 * @return the plant
	 */
	public Plant createPlant()
	{
		Plant p = new Plant(this);
		plants.add(p);
		return p;
	}
	
	public Plant createPlant(double maxEnergyAbsorbable, double maxCO2Absorbable, double maxNutrientsAbsorbable, double maxSugarStorable, double usedSugarStorageCapacity)
	{
		Plant p = new Plant(this, maxEnergyAbsorbable, maxCO2Absorbable, maxNutrientsAbsorbable, maxSugarStorable, usedSugarStorageCapacity);
		plants.add(p);
		return p;
	}

	/**
	 * create an animal
	 * @return the animal
	 */
	public Animal createAnimal()
	{
		Animal a = new Animal(this);
		animals.add(a);
		return a;
	}
	
	/**
	 * create an decomposer
	 * @return the animal
	 */
	public Decomposer createDecomposer()
	{
		Decomposer d = new Decomposer(this);
		decomposers.add(d);
		return d;
	}

	///////////////////////////////////////////////////////////////////// Energy in the day time, not the night
	public class Night implements ISimObj
	{
		@Override
		public void execute(String eventName, double currentTime, int priority,
				Sim sim) throws SimSchedulingException 
		{
			logger.debug("Night time @ Day"+currentTime/24.0);
			
			// Stop energy activity

			// Schedule the next night
			// For simplicity this is on 24 hour interval but could play with variable times later
			sim.scheduleDiscreteEvent(eventName, 24.0, priority, this);
			
			// No energy at night
			sim.cancelSchedulable(energyActivityId);
		}
		
	}

	long energyActivityId;
	
	public class Day implements ISimObj
	{
		@Override
		public void execute(String eventName, double currentTime, int priority,
				Sim sim) throws SimSchedulingException 
		{

			logger.debug("Day time @ Day"+currentTime/24.0);

			// Schedule the next day
			// For simplicity this is on 24 hour interval but could play with variable times later
			sim.scheduleDiscreteEvent(eventName, 24.0, 1, this);
			
			// Start energy activity on one hour increments
			energyActivityId = sim.scheduleContinuousActivity("Energy", 0.0, 1.0, priority, new Energy());
			
		}
		
	}

	class Energy implements ISimObj
	{
		@Override
		public void execute(String eventName, double currentTime, int priority,
				Sim sim) throws SimSchedulingException {

			logger.debug("Energy Refresh@"+currentTime/24.0+" from " + freeEnergy + " to " + MAX_FREE_ENERGY);

			// Simple - Energy is always at maximum. Restore energy
			freeEnergy = MAX_FREE_ENERGY;
			
		}
		
	}
	
	///////////////////////////////////////////////////////////////////// Water. Rain every couple of days change max in wet and dry. Treat water like an infinite resource.
	public class Dry implements ISimObj
	{
		@Override
		public void execute(String eventName, double currentTime, int priority,
				Sim sim) throws SimSchedulingException 
		{
			maxWater = MAX_DRY_SEASON_WATER;
					
			logger.debug("Dry season @ Day"+currentTime/24.0);
			

			// Schedule next dry season in a year
			sim.scheduleDiscreteEvent(eventName, 24.0*180.0, priority, this);
			
		}
		
	}

	public class Wet implements ISimObj
	{
		@Override
		public void execute(String eventName, double currentTime, int priority,
				Sim sim) throws SimSchedulingException 
		{
			maxWater = MAX_WET_SEASON_WATER;

			logger.debug("Wet season @ Day"+currentTime/24.0);
			
			// Schedule next wet season in a year
			sim.scheduleDiscreteEvent(eventName, 24.0*180.0, priority, this);
			
		}
		
	}

	public class Rain implements ISimObj
	{
		@Override
		public void execute(String eventName, double currentTime, int priority,
				Sim sim) throws SimSchedulingException {

			logger.debug("Rain@"+currentTime/24.0+" from " + freeWater + " to " + maxWater);

			freeWater = maxWater;
			
		}
		
	}
	
	////////////////////////////////////////////////////////////////// Allocate to Resources to Plants
	public class AllocateResources implements ISimObj
	{

		@Override
		public void execute(String eventName, double currentTime, int priority,
				Sim sim) throws SimSchedulingException, OutOfWaterException, OutOfEnergyException, OutOfNutrientsException, OutOfAirException, OutOfLifeException 
		{
			// Allocate resources to each plant
			int nPlants = plants.size();
			int nAnimals = animals.size();
			int nDecomposers = decomposers.size();
			int nOrganisms = nPlants+nAnimals+nDecomposers;
			
			if(nOrganisms<=0)
			{
				throw new OutOfLifeException();
			}
			
			// All organisms drink
			double maxWaterPerOrganism = freeWater / nOrganisms;
						
			if(nPlants > 0)
			{

				// Only plants can absorb energy directly
				double maxEnergyPerPlant = freeEnergy / nPlants;
				
				// Only plants can absorb co2
				double maxCO2PerPlant = freeCO2 / nPlants;
				
				// Only plants can absorb nutrients directly
				double maxNutrientsPerPlant = freeNutrients / nPlants;
				
				for(Plant p : plants)
				{
					// An organism can only take in proportion to its size.
					
					double unusedWater = p.drink(maxWaterPerOrganism);
					useWater(maxWaterPerOrganism - unusedWater);
					
					double unusedEnergy = p.photosynthesize(maxEnergyPerPlant);
					useEnergy(maxEnergyPerPlant - unusedEnergy);
					
					double unusedCO2 = p.breath(maxCO2PerPlant);
					useCO2(maxCO2PerPlant - unusedCO2);
					
					double unusedNutrients = p.eat(maxNutrientsPerPlant);
					useNutrients(maxNutrientsPerPlant - unusedNutrients);
				}
			}
			
			if(nAnimals > 0)
			{

				// Only non-plants absorb oxygen
				double maxO2PerNonPlant = freeO2 / (nOrganisms - nPlants);
				
				for(Animal a : animals)
				{

					// An organism can only take in proportion to its size.
					double unusedWater = a.drink(maxWaterPerOrganism);
					freeWater -= maxWaterPerOrganism - unusedWater;
									
					
				}
			}
			
			if(nDecomposers > 0)
			{

				// Only non-plants absorb oxygen
				double maxO2PerNonPlant = freeO2 / (nOrganisms - nPlants);
				
				for(Decomposer d : decomposers)
				{

					// An organism can only take in proportion to its size.
					double unusedWater = d.drink(maxWaterPerOrganism);
					freeWater -= maxWaterPerOrganism - unusedWater;
									
					
				}
			}
		}
		
	}

	public class Report implements ISimObj
	{
		@Override
		public void execute(String eventName, double currentTime, int priority,
				Sim sim) throws SimSchedulingException {

			if(logger.isInfoEnabled())
			{
				logger.info("Report@"+currentTime/24.0);
	
				logger.info("\tTotal Atmosphere: "+freeCO2+freeO2);
				logger.info("\t\tC02: "+freeCO2);
				logger.info("\t\tO2: "+freeO2);
	
				logger.info("\tTotal Nutrients: "+freeNutrients);
				logger.info("\tFree Energy: "+freeEnergy);

				logger.info("\t# Plants: "+plants.size());
				logger.info("\t# Animals: "+animals.size());
				logger.info("\t# Decomposers: "+decomposers.size());
				
				logger.info("\tFree Waste: "+freeWaste);
				logger.info("\tFree Death: "+freeDeath);
				
				logger.info("\t# Plant Biomass: "+getPlantBiomass());
				logger.info("\t# Animal Biomass: "+getAnimalBiomass());
				logger.info("\t# Decomposer Biomass: "+getDecomposerBiomass());
			}			
			
		}

		private double getDecomposerBiomass() {
			double biomass = 0.0;
			for(Decomposer d : decomposers)
			{
				biomass += d.biomass();
			}
			return biomass;
		}

		private double getAnimalBiomass() {
			double biomass = 0.0;
			for(Animal a : animals)
			{
				biomass += a.biomass();
			}
			return biomass;
		}

		private double getPlantBiomass() {
			double biomass = 0.0;
			for(Plant p : plants)
			{
				biomass += p.biomass();
			}
			return biomass;
		}
		
	}
	

}


