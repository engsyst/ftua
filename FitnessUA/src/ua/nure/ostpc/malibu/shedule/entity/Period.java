/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Singleton
 * 
 * @author engsyst
 */
public class Period implements Serializable {
	/**
	 * Description of the property startDate.
	 */
	private Date startDate = new Date();

	/**
	 * Description of the property endDate.
	 */
	private Date endDate = new Date();

	/**
	 * Description of the property duration.
	 */
	private int duration = 0;

	// Start of user code (user defined attributes for Period)

	// End of user code

	/**
	 * The constructor.
	 */
	public Period(Date startDate, Date endDate) {
		super();
		setPeriod(startDate, endDate);
	}

	/**
	 * Description of the method setPeriod.
	 * @param startDate 
	 * @param endDate 
	 */
	/*package*/void setPeriod(Date startDate, Date endDate) {
		if (startDate.compareTo(endDate) >= 0)
			throw new IllegalArgumentException("StartDate must less EndDate");
		this.startDate = startDate;
		this.endDate = endDate;
	}

	/**
	 * Returns startDate.
	 * @return startDate 
	 */
	public Date getStartDate() {
		return this.startDate;
	}

	/**
	 * Returns endDate.
	 * @return endDate 
	 */
	public Date getEndDate() {
		return this.endDate;
	}

	/**
	 * Returns duration.
	 * @return duration 
	 */
	public int getDuration() {
		return this.duration;
	}

	/**
	 * Sets a value to attribute duration. 
	 * @param newDuration 
	 */
	public void setDuration(int newDuration) {
		this.duration = newDuration;
	}

}
