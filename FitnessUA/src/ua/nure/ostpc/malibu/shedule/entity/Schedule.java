/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;
/**
 * Description of Shedule.
 * 
 * @author engsyst
 */
/**
 * Description of Shedule.
 * 
 * @author engsyst
 */
public class Schedule implements Serializable {

	/**
	 * Description of the property period.
	 */
	private Period period = null;
	
	private Set<Assignment> schedule = new TreeSet<Assignment>();


	public Schedule(Period period) {
		super();
		this.period = period;
	}

	/**
	 * Returns period.
	 * @return period 
	 */
	public Period getPeriod() {
		return this.period;
	}

	/**
	 * Sets a value to attribute period. 
	 * @param newPeriod 
	 */
	public void setPeriod(Period newPeriod) {
		this.period = newPeriod;
	}

	public Set<Assignment> getClubSchedule() {
		return null;
	}
	
	public Set<Assignment> getEmployeeSchedule() {
		return null;
	}
	
}
