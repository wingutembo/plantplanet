package com.tembo.simkern;

public interface ISimObj 
{
	public void execute(String eventName, double currentTime, int priority, Sim sim)  throws SimSchedulingException, OutOfResourceException;
}
