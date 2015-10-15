package com.tembo.simkern;

import org.apache.log4j.Logger;

public class Schedulable  implements Comparable<Schedulable>
{

	private static Logger logger = Logger.getLogger(Sim.class.getPackage().getName());
	private static long schedulableId = 1;
    
    private long id = schedulableId++;

    /**
	 * @return the id
	 */
	long getId() {
		return id;
	}

	/**
     * Scheduled item requires time and priority
     * 
     * @param name - 
     * @param time
     * @param priority
     * @param simObj
     * @param bAutoReschedule - time is increment
     */
    public Schedulable(String name, double time, int priority, ISimObj simObj) 
    {
		this.deltaTime = time;
		this.priority = priority;
		this.name = name;
		this.simObj = simObj;
    }
    
	/**
	 * Execute the underlying object
	 * 
	 * @param currentSimTime
	 * @param nextScheduledEvent
	 * @param sim
	 * @throws SimSchedulingException 
	 * @throws OutOfResourceException 
	 */
	public void executeOccurrence(double currentSimTime, Schedulable nextScheduledOccurrence, Sim sim) throws SimSchedulingException, OutOfResourceException 
	{
		ISimObj simObj = nextScheduledOccurrence.simObj;
		simObj.execute(name,currentSimTime,priority,sim);		
	}
	
	/**
	 * 
	 * @param currentSimTime
	 */
	void computeNextTime(double currentSimTime) {
		this.absoluteTime = currentSimTime+deltaTime;
	}

	/**
	 * Set first scheduled event in activity
	 * @param firstSimTime
	 */
	void setFirstTime(double firstSimTime) {
		this.absoluteTime = firstSimTime;
	}
	
	/**
	 * Get the absolute time scheduled for
	 */
	double getAbsoluteTime()
	{
		return this.absoluteTime;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Schedulable [scheduleTime=" + absoluteTime + ", deltaTime=" + deltaTime + ", priority=" + priority
				+ ", name=" + name + "]";
	}

	/**
	 * Comparator
	 * key 1 is time
	 * key 2 is priority
	 * key 3 is id
	 */
	@Override
	public int compareTo(Schedulable schedulable) {
		
		if(absoluteTime < schedulable.absoluteTime)
		{
			return -1;

		}
		else if(absoluteTime > schedulable.absoluteTime)
		{
			return 1;
		}
		else 
		{
			if(priority < schedulable.priority)
			{
				return -1;
			}
			else if (priority > schedulable.priority)
			{
				return 1;
			}
			else
			{
				if(id < schedulable.id)
				{
					return -1;
				}
				else
				{
					return 1;
				}
			}
		}
	}

	protected double deltaTime;
	protected double absoluteTime;

	/**
	 * @return the time
	 */
	double getTime() {
		return deltaTime;
	}

	protected int priority = 1;

	/**
	 * @return the time
	 */
	double getPriority() {
		return priority;
	}

	protected String name = "ScheduledItem";

	/**
	 * 
	 * @return the name
	 */
	String getName()
	{
		return name;
	}
	
	protected ISimObj simObj;
	
	/**
	 * @return the simulation object
	 */
	ISimObj getSimObj()
	{
		return simObj;
	}
	
	protected boolean bAutoReschedule = false;

	/**
	 * @return auto reschedule
	 */
	boolean getBAutoReschedule()
	{
		return bAutoReschedule;
	}
}
