/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;

/**
 * Description of empPrferences.
 * 
 * @author engsyst
 */
public class EmpPrferences implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Description of the property minDays.
	 */
	public int minDays;

	/**
	 * Description of the property maxDays.
	 */
	public int maxDays;

	/**
	 * The constructor.
	 */
	public EmpPrferences() {
	}
	
	/**
	 * Returns minDays.
	 * 
	 * @return minDays
	 */
	public int getMinDays() {
		return this.minDays;
	}

	/**
	 * Sets a value to attribute minDays.
	 * 
	 * @param newMinDays
	 */
	public void setMinDays(int newMinDays) {
		this.minDays = newMinDays;
	}

	/**
	 * Returns maxDays.
	 * 
	 * @return maxDays
	 */
	public int getMaxDays() {
		return this.maxDays;
	}

	/**
	 * Sets a value to attribute maxDays.
	 * 
	 * @param newMaxDays
	 */
	public void setMaxDays(int newMaxDays) {
		this.maxDays = newMaxDays;
	}

}
