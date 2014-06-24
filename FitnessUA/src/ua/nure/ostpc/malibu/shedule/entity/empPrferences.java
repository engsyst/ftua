/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;

// Start of user code (user defined imports)

// End of user code

/**
 * Description of empPrferences.
 * 
 * @author engsyst
 */
public class empPrferences implements Serializable {
	/**
	 * Description of the property minDays.
	 */
	public int minDays = 0;

	/**
	 * Description of the property maxDays.
	 */
	public int maxDays = 0;

	// Start of user code (user defined attributes for empPrferences)

	// End of user code

	/**
	 * The constructor.
	 */
	public empPrferences() {
		// Start of user code constructor for empPrferences)
		super();
		// End of user code
	}

	// Start of user code (user defined methods for empPrferences)

	// End of user code
	/**
	 * Returns minDays.
	 * @return minDays 
	 */
	public int getMinDays() {
		return this.minDays;
	}

	/**
	 * Sets a value to attribute minDays. 
	 * @param newMinDays 
	 */
	public void setMinDays(int newMinDays) {
		this.minDays = newMinDays;
	}

	/**
	 * Returns maxDays.
	 * @return maxDays 
	 */
	public int getMaxDays() {
		return this.maxDays;
	}

	/**
	 * Sets a value to attribute maxDays. 
	 * @param newMaxDays 
	 */
	public void setMaxDays(int newMaxDays) {
		this.maxDays = newMaxDays;
	}

}
