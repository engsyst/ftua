/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Date;

public class Assignment implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * See: {@link halfOfDay}
	 */
	public static final int FIRST_HALF = 1;

	/**
	 * See: {@link halfOfDay}
	 */
	public static final int SECOND_HALF = 2;

	private long assignmentId;
	private Period period;
	private Club club;
	private Date date;

	/**
	 * halfOfDay - <br />
	 * 1 - first half of the day<br />
	 * 2 - second half of the day
	 * <p/>
	 * Use final fields (@link FIRST_HALF}, (@link SECOND_HALF} 0 - reserved
	 */
	private int halfOfDay = 0;

	private Employee employee;
	private long periodId;
	private long clubId;

	public Assignment() {
	}

	public Assignment(long assignmentId, Period period, Club club, Date date,
			int halfOfDay, Employee employee) {
		this.assignmentId = assignmentId;
		this.period = period;
		this.club = club;
		this.date = date;
		this.halfOfDay = halfOfDay;
		this.employee = employee;
	}

	public long getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(long assignmentId) {
		this.assignmentId = assignmentId;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	/**
	 * Returns club. See: {@link Club}
	 * 
	 * @return club
	 */
	public Club getClub() {
		return club;
	}

	/**
	 * Sets a value to attribute club. See: {@link Club}
	 * 
	 * @param newClub
	 */
	public void setClub(Club newClub) {
		this.club = newClub;
	}

	/**
	 * Returns date.
	 * 
	 * @return date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Sets a value to attribute date.
	 * 
	 * @param newDate
	 */
	public void setDate(Date newDate) {
		this.date = newDate;
	}

	/**
	 * Returns halfOfDay. See: {@link halfOfDay}
	 * 
	 * @return halfOfDay
	 */
	public int getHalfOfDay() {
		return halfOfDay;
	}

	/**
	 * Sets a value to attribute halfOfDay.
	 * 
	 * @param newHalfOfDay
	 */
	public void setHalfOfDay(int newHalfOfDay) {
		this.halfOfDay = newHalfOfDay;
	}

	/**
	 * Returns employee. See: {@link Employee}
	 * 
	 * @return employee
	 */
	public Employee getEmployee() {
		return employee;
	}

	/**
	 * Sets a value to attribute employee. See: {@link Employee}
	 * 
	 * @param employee
	 */
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public long getPeriodId() {
		return periodId;
	}

	public void setPeriodId(long periodId) {
		this.periodId = periodId;
	}

	public long getClubId() {
		return clubId;
	}

	public void setClubId(long clubId) {
		this.clubId = clubId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || (obj.getClass() != this.getClass()))
			return false;
		Assignment otherAssignment = (Assignment) obj;
		boolean result = this.assignmentId == otherAssignment.getAssignmentId();
		return result;
	}

	@Override
	public int hashCode() {
		return new Long(assignmentId).hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Assignment [assignmentId=");
		sb.append(assignmentId);
		sb.append(", periodId=");
		sb.append(period.getPeriodId());
		sb.append(", clubId=");
		sb.append(club.getClubId());
		sb.append(", date=");
		sb.append(date);
		sb.append(", halfOfDate=");
		sb.append(halfOfDay);
		sb.append(", employeeId=");
		sb.append(employee.getEmployeeId());
		sb.append("]");
		return sb.toString();
	}
}
