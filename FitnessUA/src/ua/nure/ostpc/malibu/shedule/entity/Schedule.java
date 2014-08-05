/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Description of Schedule.
 * 
 * @author engsyst
 */
public class Schedule implements Serializable, Comparable<Schedule> {
	private static final long serialVersionUID = 1L;

	public enum Status {
		DRAFT, CLOSED, CURRENT, FUTURE;
	};

	private Status status;
	private Period period;
	private Map<Date, DaySchedule> dayScheduleMap;
	private Set<Assignment> assignments = new TreeSet<Assignment>();

	public Schedule() {
	}

	public Schedule(Status status, Period period,
			Map<Date, DaySchedule> dayScheduleMap) {
		this.status = status;
		this.period = period;
		this.dayScheduleMap = dayScheduleMap;
	}

	public Schedule(Status status, Period period, Set<Assignment> assignments) {
		this.status = status;
		this.period = period;
		this.assignments = assignments;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
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
	 * @param period
	 */
	public void setPeriod(Period period) {
		this.period = period;
	}

	public Map<Date, DaySchedule> getDayScheduleMap() {
		return dayScheduleMap;
	}

	public void setDayScheduleMap(Map<Date, DaySchedule> dayScheduleMap) {
		this.dayScheduleMap = dayScheduleMap;
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
		sb.append("Schedule [status=");
		sb.append(status.name());
		sb.append(", periodId=");
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
