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
public class Schedule implements Serializable, Comparable<Schedule> {
	private static final long serialVersionUID = 1L;
	private Period period;
	private Set<Assignment> assignments = new TreeSet<Assignment>();

	public Schedule() {
	}

	public Schedule(Period period) {
		this.period = period;
	}

	public Schedule(Period period, Set<Assignment> assignments) {
		this.period = period;
		this.assignments = assignments;
	}

	/**
	 * Returns period.
	 * 
	 * @return period
	 */
	public Period getPeriod() {
		return this.period;
	}

	/**
	 * Sets a value to attribute period.
	 * 
	 * @param newPeriod
	 */
	public void setPeriod(Period newPeriod) {
		this.period = newPeriod;
	}

	public Set<Assignment> getAssignments() {
		return assignments;
	}

	public void setAssignments(Set<Assignment> assignments) {
		this.assignments = assignments;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || (obj.getClass() != this.getClass()))
			return false;
		Schedule otherSchedule = (Schedule) obj;
		boolean result = (this.period.getPeriodId() == otherSchedule
				.getPeriod().getPeriodId());
		return result;
	}

	@Override
	public int hashCode() {
		return period.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Schedule [periodId=");
		sb.append(period.getPeriodId());
		sb.append(", numberOfAssignments=");
		sb.append(assignments.size());
		sb.append("]");
		return sb.toString();
	}

	@Override
	public int compareTo(Schedule o) {
		if (o == null)
			throw new NullPointerException();
		if (o instanceof Schedule) {
			int cmp = 0;
			if (o.period.getPeriodId() > this.period.getPeriodId()) {
				cmp = -1;
			} else {
				if (o.period.getPeriodId() < this.period.getPeriodId()) {
					cmp = 1;
				}
			}
			return cmp;
		} else
			throw new ClassCastException();
	}
}
