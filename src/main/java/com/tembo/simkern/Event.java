package com.tembo.simkern;

import org.apache.log4j.Logger;

/**
 * 
 * Events have to be rescheduled. Use for discrete events.
 * @author angelmi
 *
 */
public class Event extends Schedulable
{
    private static Logger logger = Logger.getLogger(Sim.class.getPackage().getName());

	/**
	 * Constructor
	 * @param eventName - the event name
	 * @param time - the absolute simulation time
	 * @param priority - the priority relative to other events scheduled at same time
	 * @param simObj - the object passed back at sim time
	 */
	Event(String eventName, double time, int priority, SimObj simObj)
	{
		super(eventName,time,priority,simObj);
		this.bAutoReschedule = false;
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
	public void executeEvent(double currentSimTime, Event nextScheduledEvent, Sim sim) throws SimSchedulingException, OutOfResourceException 
	{
		SimObj theEvent = nextScheduledEvent.simObj;
		theEvent.execute(name,currentSimTime,priority,sim);		
	}

}
