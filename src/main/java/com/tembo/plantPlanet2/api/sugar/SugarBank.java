package com.tembo.plantPlanet2.api.sugar;

import java.util.ArrayList;

import com.tembo.plantPlanet2.api.CarbonDioxide;
import com.tembo.plantPlanet2.api.Energy;
import com.tembo.plantPlanet2.api.water.Water;

/**
 * A sugar factory
 * @author angelmi
 *
 */
final public class SugarBank 
{
	private int sugarReserves;
	private int exchangeRate = 10; // sugar output to sugar input rate
	
	/**
	 * Sugar factory
	 * 
	 * @param sugarMoney need money to make money
	 * @param co2Money
	 * @param h2oMoney
	 * @param energyMoney
	 * @return
	 */
	public Sugar[] makeSugarLoan(Sugar[] sugarMoney, CarbonDioxide[] co2Money, Water[] h2oMoney, Energy[] energyMoney)
	{
		int sugarAmt = countSugarMoney(sugarMoney);
		
		// Need x parts CO2, water
		
		// Money for nothing
		
		// For each co2, used need to remove from co2 bank and add to o2 bank
		
		// Remove from o2 bank
		
		// Remove from energy bank
		
		Sugar[] x = makeSugarMoney(sugarAmt);
		
		return x;
	}
	
	/**
	 * Utility
	 * @param sugarMoney
	 * @return
	 */
	public static int countSugarMoney(Sugar[] sugarMoney)
	{
		int amt = 0;
		for(Sugar s : sugarMoney)
		{
			amt += s.getValue();
		}
		return amt;
	}
	
	public static Sugar[] makeSugarMoney(int sugarMoney)
	{
		int h = sugarMoney / 100;
		int hrem = sugarMoney % 100;
		int d = hrem / 10;
		int p = d % 10;
		
		ArrayList<Sugar>  a = new ArrayList<Sugar>();
		
		for(int i = 0; i < h; i++)
		{
			a.add(new SugarDollar());
		}
		
		for(int i = 0; i < d; i++)
		{
			a.add(new SugarDime());
		}

		for(int i = 0; i < p; i++)
		{
			a.add(new SugarPenny());
		}

		return a.toArray(new Sugar[a.size()]);
	}
	
}
