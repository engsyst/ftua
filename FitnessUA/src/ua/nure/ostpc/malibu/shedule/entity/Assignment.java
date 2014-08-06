/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Date;

public class Assignment implements Serializable, Comparable<Assignment> {
	private static final long serialVersionUID = 1L;

	private long assignmentId;
	private Period period;
	private Club club;
	private Date date;
	private int shift;
	private Employee employee;
	private long periodId;
	private long clubId;

	public Assignment() {
	}

	public Assignment(long assignmentId, Period period, Club club, Date date,
			int shift, Employee employee) {
		this.assignmentId = assignmentId;
		this.period = period;
		this.club = club;
		this.date = date;
		this.shift = shift;
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
	public int getShift() {
		return shift;
	}

	/**
	 * Sets a value to attribute halfOfDay.
	 * 
	 * @param shift
	 */
	public void setShift(int shift) {
		this.shift = shift;
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
		boolean result = (this.assignmentId == otherAssignment
				.getAssignmentId())
				&& (this.getEmployee().getEmployeeId() == otherAssignment
						.getEmployee().getEmployeeId());
		return result;
	}

	@Override
	public int hashCode() {
		int hashCode = new Long(assignmentId).hashCode();
		hashCode *= new Long(employee.getEmployeeId()).hashCode();
		return hashCode * 31;
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
		sb.append(", shift=");
		sb.append(shift);
		sb.append(", employeeId=");
		sb.append(employee.getEmployeeId());
		sb.append("]");
		return sb.toString();
	}

	@Override
	public int compareTo(Assignment o) {
		if (o == null)
			throw new NullPointerException();
		if (o instanceof Assignment) {
			int cmp = 0;
			if (o.period.getPeriodId() > this.period.getPeriodId()) {
				cmp = -1;
			} else {
				if (o.period.getPeriodId() < this.period.getPeriodId()) {
					cmp = 1;
				}
			}
			if (cmp == 0) {
				if (o.getEmployee().getEmployeeId() > this.employee
						.getEmployeeId()) {
					cmp = -1;
				} else {
					if (o.getEmployee().getEmployeeId() < this.employee
							.getEmployeeId()) {
						cmp = 1;
					}
				}
			}
			return cmp;
		} else
			throw new ClassCastException();
	}
}
