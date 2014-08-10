package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Description of EmployeePref.
 * 
 * @author engsyst
 */
public class EmployeePref implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;

	public int minDays;
	public int maxDays;

	public EmployeePref() {
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
