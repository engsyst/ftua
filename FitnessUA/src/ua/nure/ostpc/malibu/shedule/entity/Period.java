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
	private static final long serialVersionUID = 1L;

	private long periodId;
	private Date startDate;
	private Date endDate;
	private long lastPeriodId;

	public Period() {
	}

	public Period(long periodId) {
		this.periodId = periodId;
	}

	public Period(Date startDate, Date endDate) {
		setPeriod(startDate, endDate);
	}
	public int getDurationDays(){
		long ddd = getDuration();
		ddd=ddd/(24 * 60 * 60 * 1000);
		int days =(int) ddd;
		return days ;
	}
	public Period(long periodId, Date startDate, Date endDate, long lastPeriodId) {
		this.periodId = periodId;
		setPeriod(startDate, endDate);
		this.lastPeriodId = lastPeriodId;
	}

	/**
	 * Sets period ID.
	 * 
	 * @param periodId
	 */
	public void setPeriod_Id(long periodId) {
		this.periodId = periodId;
	}

	/**
	 * Returns period ID
	 * 
	 * @return periodId
	 */
	public long getPeriodId() {
		return this.periodId;
	}

	/**
	 * Description of the method setPeriod.
	 * 
	 * @param startDate
	 * @param endDate
	 */
	public void setPeriod(Date startDate, Date endDate) {
		if (startDate.compareTo(endDate) >= 0)
			throw new IllegalArgumentException("StartDate must less EndDate");
		this.startDate = startDate;
		this.endDate = endDate;
	}

	/**
	 * Returns startDate.
	 * 
	 * @return startDate
	 */
	public Date getStartDate() {
		return this.startDate;
	}

	/**
	 * Returns endDate.
	 * 
	 * @return endDate
	 */
	public Date getEndDate() {
		return this.endDate;
	}

	/**
	 * Sets last period ID.
	 * 
	 * @param lastPeriodId
	 */
	public void setLastPriodId(long lastPeriodId) {
		this.lastPeriodId = lastPeriodId;
	}

	/**
	 * Returns last period ID.
	 * 
	 * @return lastPeriodId
	 */
	public long getLastPeriodId() {
		return this.lastPeriodId;
	}

	/**
	 * Returns duration.
	 * 
	 * @return duration
	 */
	public long getDuration() {
		return endDate.getTime() - startDate.getTime();
	}

	@Override
	public int hashCode() {
		return new Long(periodId).hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Period [shedulePeriodId=");
		sb.append(periodId);
		sb.append(", startDate=");
		sb.append(startDate);
		sb.append(", endDate=");
		sb.append(endDate);
		sb.append(", lastPeriodId=");
		sb.append(lastPeriodId);
		sb.append("]");
		return sb.toString();
	}
}
