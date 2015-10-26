package com.tembo.simkern;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class Sim 
{
    private static Logger logger = Logger.getLogger(Sim.class.getPackage().getName());
    {
        ConsoleAppender ap = new ConsoleAppender();
        ap.setWriter(new OutputStreamWriter(System.err));
        // ap.setLayout(new PatternLayout("%d [%p|%c|%C{1}] %m%n"));
        ap.setLayout(new PatternLayout("%m%n"));
        logger.removeAllAppenders();
        logger.addAppender(ap);
        
        logger.setLevel(Level.DEBUG);

    }

	private double currentSimTime = 0.0;
	private PriorityQueue<Schedulable> schedule = new PriorityQueue<Schedulable>();
	private Map<Long,Schedulable> schedulables = new HashMap<Long,Schedulable>();
	
	private boolean bStarted = false;
	
	/**
	 * Schedule an event in the future
	 * @param eventName a name for the event
	 * @param deltaTime time from current time to schedule event
	 * @param priority the priority to break ties in scheduling time
	 * @param simObj the event object
	 * @throws SimSchedulingException
	 * @return id 
	 */
	public long scheduleDiscreteEvent(String eventName, double delta, int priority, SimObj simObj) throws SimSchedulingException
	{
		Event event = new Event(eventName,delta,priority,simObj);
		event.setFirstTime(currentSimTime+delta);
		if(bStarted && delta <= 0.0)
		{
			throw new SimSchedulingException(eventName+" must be schedule in future (>0.0)");
		}
		schedule.add(event);
		schedulables.put(event.getId(), event);
		
		return event.getId();
	}
	
	/**
	 * Schedule a continuous activity
	 * @param activityName a name for the event
	 * @param delta time from current time
	 * @param increment the continuous time increment
	 * @param priority the priority to break ties in scheduling time
	 * @param simObj the event object
	 * @throws SimSchedulingException
	 */
	public void scheduleContinuousActivity(String activityName, double increment, int priority, SimObj simObj) throws SimSchedulingException
	{
		this.scheduleContinuousActivity(activityName, 0.0, increment, priority, simObj);
	}
	
	public void scheduleContinuousActivity(String activityName, double delta, double increment, int priority, SimObj simObj) throws SimSchedulingException
	{
		Activity activity = new Activity(activityName,increment,priority,simObj);
		activity.setFirstTime(this.currentSimTime+delta);
		if(increment <= 0.0)
		{
			throw new SimSchedulingException(activityName+" must have positive time increment (>0.0)");
		}
		schedule.add(activity);
		schedulables.put(activity.getId(), activity);
		simObj.setActivityId(activity.getId());
	}
	
	/**
	 * Computes the next time
	 * @param sched
	 * @throws SimSchedulingException
	 */
	private void scheduleNextContinuousActivity(Schedulable sched) throws SimSchedulingException
	{
		sched.computeNextTime(this.currentSimTime);
		if(sched.deltaTime <= 0.0)
		{
			throw new SimSchedulingException(sched.name+" must have positive time increment (>0.0)");
		}
		schedule.add(sched);
	}
	
	/**
	 * Removes the schedulable with the id
	 * @param energyActivityId
	 */
	List<Long> canceledSchedulables = null;
	public void cancelSchedulable(long id) 
	{		
		// the long should have the same hash as the actual object to be removed
		if(canceledSchedulables==null)
		{
			canceledSchedulables = new ArrayList<Long>();
		}
		canceledSchedulables.add(id);
		
	}
	
	/**
	 * Executes the simulation
	 * @throws SimSchedulingException 
	 */
	public void runSim(double endTime) throws SimSchedulingException
	{
		if(logger.isTraceEnabled())
		{
			logger.trace("START");
			for(Schedulable s : this.schedule)
			{
				logger.trace(s);
			}
		}
		
		try {

			// Keep going until there is nothing left to do
			while(!schedule.isEmpty())
			{
				
				// Initiate the next event
				Schedulable nextScheduledOccurrence = schedule.remove();
				this.currentSimTime = nextScheduledOccurrence.getAbsoluteTime();
				// Is simulation over?
				if(this.currentSimTime>endTime)
				{
					break;
				}
				// Execute the event
				nextScheduledOccurrence.executeOccurrence(this.currentSimTime, nextScheduledOccurrence, this);
				
				// if this is 
				if(nextScheduledOccurrence.bAutoReschedule)
				{				
					scheduleNextContinuousActivity(nextScheduledOccurrence);
				}
				
				if(canceledSchedulables != null)
				{
					// Remove all the canceled schedulables
					for(Long id : canceledSchedulables)
					{
						// Remove it from the list
						Schedulable sched = this.schedulables.remove(id);
						if(sched!=null)
						{
							// Remove it from the schedule
							boolean bRemoved = this.schedule.remove(sched);
							logger.debug(bRemoved?"Canceled "+id:"Failed to cancel "+id);
						}
						else
						{
							logger.debug("Failed to cancel "+id+". Not Found.");
						}
					}
					canceledSchedulables = null;
	
					if(logger.isDebugEnabled())
					{
						logger.debug("Cancel at "+this.currentSimTime);
						for(Schedulable s : this.schedule)
						{
							logger.debug(s);
						}
					}
				}
			}
			
			if(schedule.isEmpty())
			{
				logger.info("Simulation has no scheduled events. Terminated at "+this.currentSimTime);
			}
			else if(this.currentSimTime>endTime)
			{
				logger.info("Simulation time exceeds end time. Terminated at "+endTime);
			}
			else
			{
				logger.warn("Simulation terminated. Shouldn't be able to get here");
			}
		} catch (OutOfResourceException e) {
 			logger.info("Ran out of resource ",e);
		}
	}

}



