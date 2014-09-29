package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Description of schedule.
 * 
 * @author engsyst
 */
public class Schedule implements Serializable, IsSerializable,
		Comparable<Schedule> {
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

	public Schedule(Period period, Schedule.Status status,
			Map<Date, List<ClubDaySchedule>> dayScheduleMap,
			List<ClubPref> clubPrefs) {
		this.period = period;
		this.status = status;
		this.dayScheduleMap = dayScheduleMap;
		this.clubPrefs = clubPrefs;
	}

	/**
	 * Automation of making Schedule
	 * 
	 * @return
	 */
	public boolean generate() {
		boolean ok = false;
		if(status != Schedule.Status.DRAFT) return ok;
//		Set<Date> dates =  dayScheduleMap.keySet();
//		Iterator<Date> dIter = dates.iterator();
//		while (dIter.hasNext()) {
//			List<ClubDaySchedule> daySchedules = dayScheduleMap.get(dIter.next());
//		}
		Date[] dates = (Date[]) dayScheduleMap.keySet().toArray();
		// By dates
		for (int i = 0; i < dates.length; i++) {
			ClubDaySchedule[] clubDaySchedules = (ClubDaySchedule[]) dayScheduleMap.get(dates[i]).toArray();
			// By clubs in day
			for (int j = 0; j < clubDaySchedules.length; j++) {
				Shift[] shifts = (Shift[]) clubDaySchedules[j].getShifts().toArray();
				// By shifts in club
				for (int k = 0; k < shifts.length; k++) {
					Employee[] emps = (Employee[]) shifts[k].getEmployees().toArray();
					if (emps.length < shifts[k].getQuantityOfEmployees());
					// By employee in shift
					for (int l = 0; l < emps.length; l++) {
						
					}
					
				}
				
			}
		}
		return ok;
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

	public void setDayScheduleMap(
			Map<Date, List<ClubDaySchedule>> dayScheduleMap) {
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
			} else {
				if (o.period.getPeriodId() < this.period.getPeriodId()) {
					cmp = 1;
				}
			}
			return cmp;
		} else
			throw new ClassCastException();
	}

	public void merge(Schedule s) {
		if (s == null)
			throw new NullPointerException();
		if (s instanceof Schedule) {
			if (status == Status.DRAFT && !locked) {

				for (Date currDate : s.getDayScheduleMap().keySet()) {
					if (dayScheduleMap.containsKey(currDate)
							&& dayScheduleMap.get(currDate) != null) {
						for (ClubDaySchedule modClubDaySchedule : s.dayScheduleMap
								.get(currDate)) {
							for (ClubDaySchedule originClubDaySchedule : dayScheduleMap
									.get(currDate)) {
								if (!dayScheduleMap.get(currDate).contains(
										modClubDaySchedule)) {
									// Origin - first
									// Mod - second
									for (Shift originShift : originClubDaySchedule
											.getShifts()) {
										for (Shift modShift : modClubDaySchedule
												.getShifts()) {
											for (Employee originEmp : originShift
													.getEmployees()) {
												// origin has & mod no = removed
												if (!modShift.getEmployees()
														.contains(originEmp)) {
													originShift.getEmployees()
															.remove(originEmp);
												}
											}
											for (Employee modEmp : modShift
													.getEmployees()) {
												// mod has & origin no = add
												if (!originShift.getEmployees()
														.contains(modEmp)) {
													if (originShift
															.getEmployees()
															.size() < modShift
															.getQuantityOfEmployees()) {
														originShift
																.getEmployees()
																.add(modEmp);
													}
												}
											}

										}
									}
									if (modClubDaySchedule.getShifts().size() > 0) {
										for (Shift cShift : modClubDaySchedule
												.getShifts()) {
											if (cShift.getEmployees().size() > 0) {
												modClubDaySchedule.getShifts()
														.add(cShift);
											}
										}
									}
								} else {
									dayScheduleMap.put(currDate, s
											.getDayScheduleMap().get(currDate));
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
