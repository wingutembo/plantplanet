package com.tembo.plantPlanet.api;

import java.io.OutputStreamWriter;
import java.util.LinkedList;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.tembo.plantPlanet.OutOfAirException;
import com.tembo.plantPlanet.OutOfEnergyException;
import com.tembo.plantPlanet.OutOfLifeException;
import com.tembo.plantPlanet.OutOfNutrientsException;
import com.tembo.plantPlanet.OutOfWaterException;
import com.tembo.simkern.Sim;
import com.tembo.simkern.SimObj;
import com.tembo.simkern.SimSchedulingException;

public class World {
	
	private static Logger logger = Logger.getLogger(World.class.getPackage().getName());
    {
        ConsoleAppender ap = new ConsoleAppender();
        ap.setWriter(new OutputStreamWriter(System.err));
        // ap.setLayout(new PatternLayout("%d [%p|%c|%C{1}] %m%n"));
        ap.setLayout(new PatternLayout("%m%n"));
        logger.removeAllAppenders();
        logger.addAppender(ap);
        
        logger.setLevel(Level.DEBUG);

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
	
    /**
     * 
     * @param dCO2 the delta gain in CO2
     */
    public void freeCO2(double dCO2)
    {
    	freeCO2 += dCO2;
    }
    
    /**
     * 
     * @param dO2 the delta gain in O2
     */
    public void freeO2(double dO2)
    {
    	freeO2 += dO2;
    }
    
    /**
     * 
     * @param nutrients the delta gain in nutrients
     */
    public void freeNutrients(double nutrients)
    {
    	freeNutrients += nutrients;
    }
    
    /**
     * 
     * @param water the delta gain in water
     */
    public void freeWater(double water)
    {
    	freeWater += water;
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
	protected LinkedList<Decomposer> decomposers = new LinkedList<Decomposer>();
	private LinkedList<Waste> wastes = new LinkedList<Waste>(); // Organic waste is clumpy (?) 
	
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
	
	/**
	 * Make waste, not haste
	 * 
	 * @param sugarWaste
	 * @param nWaste
	 * @return
	 */
	public Waste createWaste(double sugarWaste, double nWaste)
	{
		Waste w = new Waste(sugarWaste,nWaste);
		wastes.addLast(w);
		return w;
	}

	/**
	 * plant biomass
	 * @return
	 */
	public double plantBiomass() {
		double biomass = 0;
		for(Plant p : plants)
		{
			biomass += p.biomass();
		}
		return biomass;
	}
	
	/**
	 * animal biomass
	 * @return
	 */
	public double animalBiomass() {
		double biomass = 0;
		for(Animal a : animals)
		{
			biomass += a.biomass();
		}
		return biomass;
	}

	/**
	 * Decomposer biomass
	 * @return
	 */
	public double decomposerBiomass() {
		double biomass = 0;
		for(Decomposer d : decomposers)
		{
			biomass += d.biomass();
		}
		return biomass;
	}
	
	/**
	 * Decomposer biomass
	 * @return
	 */
	public Waste wasteBiomass() {
		Waste biomass = new Waste(0.0,0.0);
		for(Waste w : wastes)
		{
			biomass.add(w);
		}
		return biomass;
	}


	///////////////////////////////////////////////////////////////////// Energy in the day time, not the night
	public class Night extends SimObj
	{
		@Override
		public void execute(String eventName, double currentTime, int priority,
				Sim sim) throws SimSchedulingException 
		{
			logger.trace("Night time @ Day"+currentTime/24.0);
			
			// Stop energy activity

			// Schedule the next night
			// For simplicity this is on 24 hour interval but could play with variable times later
			sim.scheduleDiscreteEvent(eventName, 24.0, priority, this);
			
			// No energy at night
			sim.cancelSchedulable(energyActivityId);
		}
		
	}

	long energyActivityId;
	
	public class Day extends SimObj
	{
		@Override
		public void execute(String eventName, double currentTime, int priority,
				Sim sim) throws SimSchedulingException 
		{

			logger.trace("Day time @ Day"+currentTime/24.0);

			// Schedule the next day
			// For simplicity this is on 24 hour interval but could play with variable times later
			sim.scheduleDiscreteEvent(eventName, 24.0, 1, this);
			
			// Start energy activity on one hour increments
			Energy energy = new Energy();
			sim.scheduleContinuousActivity("Energy", 0.0, 1.0, priority, energy);
			energyActivityId = energy.getActivityId();
		}
		
	}

	class Energy extends SimObj
	{
		@Override
		public void execute(String eventName, double currentTime, int priority,
				Sim sim) throws SimSchedulingException {

			logger.trace("Energy Refresh@"+currentTime/24.0+" from " + freeEnergy + " to " + MAX_FREE_ENERGY);

			// Simple - Energy is always at maximum. Restore energy
			freeEnergy = MAX_FREE_ENERGY;
			
		}

	}
	
	///////////////////////////////////////////////////////////////////// Water. Rain every couple of days change max in wet and dry. Treat water like an infinite resource.
	public class Dry extends SimObj
	{
		@Override
		public void execute(String eventName, double currentTime, int priority,
				Sim sim) throws SimSchedulingException 
		{
			maxWater = MAX_DRY_SEASON_WATER;
					
			logger.trace("Dry season @ Day"+currentTime/24.0);
			

			// Schedule next dry season in a year
			sim.scheduleDiscreteEvent(eventName, 24.0*180.0, priority, this);
			
		}
		
	}

	public class Wet extends SimObj
	{
		@Override
		public void execute(String eventName, double currentTime, int priority,
				Sim sim) throws SimSchedulingException 
		{
			maxWater = MAX_WET_SEASON_WATER;

			logger.trace("Wet season @ Day"+currentTime/24.0);
			
			// Schedule next wet season in a year
			sim.scheduleDiscreteEvent(eventName, 24.0*180.0, priority, this);
			
		}
		
	}

	public class Rain extends SimObj
	{
		@Override
		public void execute(String eventName, double currentTime, int priority,
				Sim sim) throws SimSchedulingException {

			logger.trace("Rain@"+currentTime/24.0+" from " + freeWater + " to " + maxWater);

			freeWater = maxWater;
			
		}
		
	}
	
	////////////////////////////////////////////////////////////////// Allocate to Resources to Organisms
	public class AllocateResources extends SimObj
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
	
	/**
	 * 
	 * @author angelmi
	 *
	 */
	public class Report extends SimObj
	{
		@Override
		public void execute(String eventName, double currentTime, int priority,
				Sim sim) throws SimSchedulingException {

			if(logger.isInfoEnabled())
			{
				logger.info(eventName+" Report@"+currentTime/24.0);
	
				logger.info("\tTotal Atmosphere: "+freeCO2+freeO2);
				logger.info("\t\tC02: "+freeCO2);
				logger.info("\t\tO2: "+freeO2);
				logger.info("\tTotal Water: "+freeWater);	
				logger.info("\tTotal Nutrients: "+freeNutrients);
				logger.info("\tFree Energy: "+freeEnergy);

				logger.info("\t# Plants: "+plants.size());
				logger.info("\t# Animals: "+animals.size());
				logger.info("\t# Decomposers: "+decomposers.size());
				
				logger.info("\tWaste: "+World.this.wastes.size());
				for(Waste w : wastes)
				{
					logger.info("\t\t\tWaste (n,s) = "+w.unusedN+","+w.unusedSugar);
				}
				
				logger.info("\t# Plant Biomass: "+getPlantBiomass());
				logger.info("\t# Animal Biomass: "+getAnimalBiomass());
				logger.info("\t# Decomposer Biomass: "+getDecomposerBiomass());
			}			
			
			// If this is the only thing scheduled, then cancel it
			if(plants.size()+animals.size()+decomposers.size()<=0)
			{
				// Unschedule all world activities
				World.this.unscheduleAllActivities(sim);
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

	public void setCO2(double d) {
		this.freeCO2 = d;
	}

	public void setO2(double d) {
		this.freeO2 = d;
	}

	/**
	 * Define size in terms of resource 
	 * TODO: Define size in terms of density of resources?
	 * @return
	 */
	public double getSize() {
		return this.freeCO2 + this.freeO2;
	}

	/**
	 * 
	 * @param a the animal that consumes the plant
	 */
	public void consumePlant(Sim sim, Animal a) 
	{
		// Remove a plant from the plant list
		Plant p = plants.removeFirst();
		
		// Unschedule all plant activities
		p.unscheduleAllActivities(sim);
		
		// Give up resources
		double usedSugar = p.getUsedSugarStorageCapacity();
		double usedN = p.getUsedNutrientStorageCapacity();
		double usedW = p.getUsedWaterStorageCapacity();
		
		// Consume resourses
		double unusedSugar = a.consumeSugar(usedSugar);
		double unusedN = a.consumeNutrients(usedN);
		double unusedWater = a.drink(usedW);
		
		// Return unused to the world
		this.freeWater += unusedWater;

		// Get biomass
		double biomass = p.biomass(); 
		Waste unusedBiomass = a.digest(biomass);
		if(unusedBiomass.notEmpty())
		{
		
			unusedBiomass.unusedN += unusedN;
			unusedBiomass.unusedSugar += unusedSugar;
			
			this.wastes.add(unusedBiomass);		
		}		
	}

	public void freeSugar(double wasteSugar) {
		if(wasteSugar > 0.0)
		{
			Waste unusedBiomass = new Waste(0.0,wasteSugar);		
			this.wastes.add(unusedBiomass);
		}
	}

	/**
	 * Kill the animal.
	 * Account for all the animal resources
	 * 
	 * @param a
	 * @param sim
	 */
	public void killAnimal(Animal a, Sim sim) 
	{
		// Remove this animal from animal list
		boolean bRemoved = animals.remove(a);
		if(!bRemoved)
		{
			logger.warn("Killed animal not found on animal list");
		}
		
		// Unschedule all animal activities
		a.unscheduleAllActivities(sim);
		
		// Give up resources
		double freedSugar = a.getUsedSugarStorageCapacity();
		double freedN = a.getUsedNutrientStorageCapacity();
		double freedW = a.getUsedWaterStorageCapacity();

		// Make the structure available
		Waste unusedBiomass = new Waste(freedN, freedSugar);
		if(unusedBiomass.notEmpty())
		{
			this.wastes.add(unusedBiomass);
		}
	}

	/**
	 * Decomposers decompose waste
	 * 
	 * @param sim
	 * @param decomposer
	 */
	public void consumeWaste(Sim sim, Decomposer decomposer) 
	{
		// Remove waste from the waste list
		Waste w = wastes.removeFirst();
		
		// Give up resources
		double usedSugar = w.unusedSugar;
		double usedN = w.unusedN;
		
		// Consume resources
		double unusedSugar = decomposer.consumeSugar(usedSugar);
		double unusedN = decomposer.consumeNutrients(usedN);

		// What to do with unused stuff?, Add it back the wastes list
		if(unusedSugar > 0.0 || unusedN > 0.0)
		{
			w.unusedN = unusedN;
			w.unusedSugar = unusedSugar;
			wastes.addLast(w);
		}
		
	}

	/**
	 * 
	 * @param decomposer
	 * @param sim
	 */
	public void killDecomposer(Decomposer decomposer, Sim sim) 
	{
		// Remove this animal from animal list
		boolean bRemoved = decomposers.remove(decomposer);
		if(!bRemoved)
		{
			logger.warn("Killed decomposer not found on decomposer list");
		}
		
		// Unschedule all animal activities
		decomposer.unscheduleAllActivities(sim);
		
		// Give up resources
		double freedSugar = decomposer.getUsedSugarStorageCapacity();
		double freedN = decomposer.getUsedNutrientStorageCapacity();
		double freedW = decomposer.getUsedWaterStorageCapacity();

		// Make the structure available
		Waste unusedBiomass = new Waste(freedN, freedSugar);
		if(unusedBiomass.notEmpty())
		{
			this.wastes.add(unusedBiomass);
		}

		freeWater(freedW);
	}

	public void unscheduleAllActivities(Sim sim) 
	{
		if(day != null)
		{
			day.unschedule(sim);
			this.day = null;
		}
		if(night != null)
		{
			night.unschedule(sim);
			this.night = null;
		}
		if(wet != null)
		{
			wet.unschedule(sim);
			this.wet = null;
		}
		if(dry != null)
		{
			dry.unschedule(sim);
			this.dry = null;
		}
		if(allocateResources != null)
		{
			allocateResources.unschedule(sim);
			this.allocateResources = null;
		}
		if(report != null)
		{
			report.unschedule(sim);
			this.report = null;
		}
		
	}

	private Day day = null;
	public SimObj createDay() {
		if(this.day == null)
		{
			this.day = this.new Day();
		}
		return this.day;
	}

	private Night night = null;
	public SimObj createNight() {
		if(this.night == null)
		{
			this.night = this.new Night();
		}
		return this.night;
	}

	private Wet wet = null;
	public SimObj createWet() {
		if(this.wet == null)
		{
			this.wet = this.new Wet();
		}
		return this.wet;
	}

	private Dry dry = null;
	public SimObj createDry() {
		if(this.dry == null)
		{
			this.dry = this.new Dry();
		}
		return this.dry;
	}

	private Rain rain = null;
	public SimObj createRain() {
		if(this.rain == null)
		{
			this.rain = this.new Rain();
		}
		return this.rain;
	}

	private AllocateResources allocateResources = null;
	public SimObj createAllocateResources() {
		if(this.allocateResources == null)
		{
			this.allocateResources = this.new AllocateResources();
		}
		return this.allocateResources;
	}

	private Report report = null;
	public SimObj createReport() {
		if(this.report == null)
		{
			this.report = this.new Report();
		}
		return this.report;
	}

}


