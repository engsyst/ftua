package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Description of schedule.
 * 
 * @author engsyst
 */
public class Schedule implements Serializable, IsSerializable, Comparable<Schedule> {
	private static final long serialVersionUID = 1L;

	public enum Status {
		DRAFT, CLOSED, CURRENT, FUTURE;
	};

	private Period period;
	private Status status;
	private Map<Date, List<ClubDaySchedule>> dayScheduleMap;
	private List<ClubPref> clubPrefs;
	private long timeStamp;
	private boolean locked;

	public Schedule() {
	}

	public Schedule(Period period, Schedule.Status status, Map<Date, List<ClubDaySchedule>> dayScheduleMap, List<ClubPref> clubPrefs) {
		this.period = period;
		this.status = status;
		this.dayScheduleMap = dayScheduleMap;
		this.clubPrefs = clubPrefs;
	}

	// temporary
	public Set<Assignment> getAssignments() {
		return null;
	}

	/**
	 * Returns period.
	 * 
	 * @return period
	 */
	public Period getPeriod() {
		return period;
	}

	/**
	 * Sets a value to attribute period.
	 * 
	 * @param period
	 */
	public void setPeriod(Period period) {
		this.period = period;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Map<Date, List<ClubDaySchedule>> getDayScheduleMap() {
		return dayScheduleMap;
	}

	public void setDayScheduleMap(Map<Date, List<ClubDaySchedule>> dayScheduleMap) {
		this.dayScheduleMap = dayScheduleMap;
	}

	public List<ClubPref> getClubPrefs() {
		return clubPrefs;
	}

	public void setClubPrefs(List<ClubPref> clubPrefs) {
		this.clubPrefs = clubPrefs;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || (obj.getClass() != this.getClass()))
			return false;
		Schedule otherSchedule = (Schedule) obj;
		boolean result = (this.period.getPeriodId() == otherSchedule.getPeriod().getPeriodId());
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
		sb.append(", period=");
		sb.append(period);
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
			}
			else {
				if (o.period.getPeriodId() < this.period.getPeriodId()) {
					cmp = 1;
				}
			}
			return cmp;
		}
		else
			throw new ClassCastException();
	}

	//TODO Need to discuss and finish
	public void merge(Schedule s) {
		if (s == null)
			throw new NullPointerException();
		if (s instanceof Schedule) {
			if (status == Status.DRAFT && !locked) {

				for (Date currDate : s.getDayScheduleMap().keySet()) {
					if (dayScheduleMap.containsKey(currDate) && dayScheduleMap.get(currDate) != null) {
						for (ClubDaySchedule fromDay : s.dayScheduleMap.get(currDate)) {
							for (ClubDaySchedule toDay : dayScheduleMap.get(currDate)) {
								if (!dayScheduleMap.get(currDate).contains(fromDay)) {
									// TODO And what if no? What is club object?
									if (toDay.getClub() == fromDay.getClub()) {
										for (ClubPref pref : s.getClubPrefs()) {
											if (!clubPrefs.contains(pref)) {
												clubPrefs.add(pref);
											}
										}
										for (Shift inShift : toDay.getShifts()) {
											for (Shift toShift : fromDay.getShifts()) {
												for (Employee emp : inShift.getEmployees()) {
													if (toShift.getEmployees().contains(emp)) {
														toShift.getEmployees().remove(emp);
													}
												}
											}
										}
										if (fromDay.getShifts().size() > 0) {
											for (Shift cShift : fromDay.getShifts()) {
												if (cShift.getEmployees().size() > 0) {
													fromDay.getShifts().add(cShift);
												}
											}
										}
									}
								}
								else {
									dayScheduleMap.put(currDate, s.getDayScheduleMap().get(currDate));
								}
							}
						}
					}
				}
			}
			timeStamp = System.currentTimeMillis(); // Right?
		}
	}
}
