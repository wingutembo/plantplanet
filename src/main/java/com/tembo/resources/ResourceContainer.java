package com.tembo.resources;

public class ResourceContainer 
{
	private double max_capacity = 1.0; // This is size
	
	private double used_capacity = 0.0;

	/**
	 * Initialize the resource container
	 * @param initial_max_capacity
	 */
	public ResourceContainer(double initial_max_capacity)
	{
		this.max_capacity = initial_max_capacity;
	}
	
	/**
	 * Increment the size of the resource container
	 * @param incr
	 */
	public void grow(double incr)
	{
		max_capacity += incr;
	}

	/**
	 * Add to the resource
	 * @param incr
	 * @return the actual amount enqueued
	 */
	public double enque(final double incr)
	{
		double actual_incr;
		double waste;
		
		if(incr > max_capacity)
		{
			// Can only store to capacity
			actual_incr = max_capacity;
			waste = incr - max_capacity;
		}
		else
		{
			// Can store everything
			actual_incr = incr;
			waste = 0.0;
		}
		
		used_capacity =+ actual_incr;
		
		return waste;
	}
	
	/**
	 * Decrement the resource
	 * @param decr
	 */
	public void deque(double decr)
	{
		used_capacity -= decr;
		if(used_capacity < 0.0)
		{
			used_capacity = 0.0;
		}
	}
	
	
}
