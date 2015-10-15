package com.tembo.simkern;

import org.apache.log4j.Logger;

/**
 * Activities are continuous.
 * @author angelmi
 *
 */
public class Activity extends Schedulable
{
    private static Logger logger = Logger.getLogger(Sim.class.getPackage().getName());

	/**
	 * Constructor
	 * @param activityName - the activity name
	 * @param time - the absolute simulation time
	 * @param priority - the priority relative to other activitys scheduled at same time
	 * @param simObj - the object passed back at sim time
	 */
	Activity(String activityName, double time, int priority, ISimObj simObj)
	{
		super(activityName,time,priority,simObj);
		this.bAutoReschedule = true;
	}
	
	/**
	 * Execute the underlying object
	 * 
	 * @param currentSimTime
	 * @param nextScheduledActivity
	 * @param sim
	 * @throws SimSchedulingException 
	 * @throws OutOfResourceException 
	 */
	public void executeActivity(double currentSimTime, Activity nextScheduledActivity, Sim sim) throws SimSchedulingException, OutOfResourceException 
	{
		ISimObj theSimObj = nextScheduledActivity.simObj;
		theSimObj.execute(name,currentSimTime,priority,sim);		
	}

	
	

}
