package com.tembo.plantPlanet2.api.water;

public class WaterBank 
{
	private int waterReserves = 100000;
	
	public Water[] makeWater()
	{
		return null;
	}

	public static int countWaterMoney(Water[] waterMoney)
	{
		int amt = 0;
		for(Water s : waterMoney)
		{
			amt += s.getValue();
		}
		return amt;
	}

}
