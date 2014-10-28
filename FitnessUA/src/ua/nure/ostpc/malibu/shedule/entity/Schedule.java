package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.Const;

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

	public Map<Employee, Integer> getCountOfAssignmentsForEmps() {
		HashMap<Employee, Integer> ass = new HashMap<Employee, Integer>();
		Set<Date> dates = dayScheduleMap.keySet();
		// By date
		Iterator<Date> dIter = dates.iterator();
		while (dIter.hasNext()) {
			List<ClubDaySchedule> daySchedules = dayScheduleMap.get(dIter.next());
			
			// By club
			ListIterator<ClubDaySchedule> cdsIter = daySchedules.listIterator();
			while (cdsIter.hasNext()) {
				// get next schedule of club at this date
				ClubDaySchedule clubDaySchedule = cdsIter.next();
				Set<Employee> ce = clubDaySchedule.getEmployees();
				for (Employee e : ce) {
					Integer count = ass.get(e);
					ass.put(e, count == null ? 0 : ++count);
				}
			}
		}
		return null;
	}
	
	public int getCountOfAllNeededAssignmentsInDay(Date d) {
			List<ClubDaySchedule> daySchedules = dayScheduleMap.get(d);
			int shiftsCount = 0;
			// By club
			ListIterator<ClubDaySchedule> cdsIter = daySchedules.listIterator();
			while (cdsIter.hasNext()) {
				// get next schedule of club at this date
				ClubDaySchedule clubDaySchedule = cdsIter.next();
				shiftsCount += clubDaySchedule.getShiftsNumber();
			}
		return shiftsCount;
	}
	
	/**
	 * Set to every Employee count of their Assignments. Start from <b>start</b>,
	 * end <b>not</b> set to zero assignments from previous dates.
	 * You should set them manually.
	 */
	public void recountAssignments(Date start) {
		Set<Date> dates =  dayScheduleMap.keySet();
		// By date
		Iterator<Date> dIter = dates.iterator();
		while (dIter.hasNext()) {
			Date d = dIter.next();
			List<ClubDaySchedule> daySchedules = dayScheduleMap.get(d);

			// By club
			ListIterator<ClubDaySchedule> cdsIter = daySchedules.listIterator();
			while (cdsIter.hasNext()) {
				// get next schedule of club at this date
				ClubDaySchedule clubDaySchedule = cdsIter.next();
				Set<Employee> ce = clubDaySchedule.getEmployees();
				if (d.compareTo(start) >= 0)
					for (Employee e : ce) 
						e.incAssignment();
			}
		}
	}
	
	/**
	 * Set to every Employee count of their Assignments to zero
	 */
	public void setAssignmentsToZero() {
		Set<Date> dates =  dayScheduleMap.keySet();
		// By date
		Iterator<Date> dIter = dates.iterator();
		while (dIter.hasNext()) {
			List<ClubDaySchedule> daySchedules = dayScheduleMap.get(dIter.next());
			
			// By club
			ListIterator<ClubDaySchedule> cdsIter = daySchedules.listIterator();
			while (cdsIter.hasNext()) {
				// get next schedule of club at this date
				ClubDaySchedule clubDaySchedule = cdsIter.next();
				Set<Employee> ce = clubDaySchedule.getEmployees();
				for (Employee e : ce) {
					e.setAssignment(0);
				}
			}
		}
	}
	
	/**
	 * Returns preferred employees for represented club. If club == null returns
	 * employees preferred for any club.
	 * 
	 * @param club
	 * @param emps
	 * @return list of preferred employees for club
	 */
	public List<Employee> getPreferredEmps(List<Employee> emps, Club club) {
		if (emps == null) 
			throw new IllegalArgumentException("Employees can not be null");
		List<Employee> re = new ArrayList<Employee>();
//		if (clubPrefs == null) return re;
		if (club == null) {
			for (ClubPref cp : clubPrefs) {
				for (Employee e : emps)
					if (cp.getEmployeeId() == e.getEmployeeId()) {
						re.add(e);
						break;
					}
			}
		} else {
			for (ClubPref cp : clubPrefs) {
				if (club.getClubId() == cp.getClubId()) {
					for (Employee e : emps) {
						if (cp.getEmployeeId() == e.getEmployeeId()) {
							re.add(e);
							break;
						}
					}
				}
			}
		}
		return re;
	}

//	/**
//	 * Returns unpreferred employees assigned to represented club. 
//	 * 
//	 * @param club
//	 * @param emps
//	 * @return list of unpreferred employees assigned to club
//	 */
//	public List<Employee> getUnpreferredAssignments(Club club) {
//		List<Employee> re = new ArrayList<Employee>();
////		if (clubPrefs == null) return re;
//		if (club == null) {
//			for (ClubPref cp : clubPrefs) {
//				for (Employee e : emps)
//					if (cp.getEmployeeId() == e.getEmployeeId()) {
//						re.add(e);
//						break;
//					}
//			}
//		} else {
//			for (ClubPref cp : clubPrefs) {
//				if (club.getClubId() == cp.getClubId()) {
//					for (Employee e : emps) {
//						if (cp.getEmployeeId() == e.getEmployeeId()) {
//							re.add(e);
//							break;
//						}
//					}
//				}
//			}
//		}
//		return re;
//	}
	
	public void sortClubsByPrefs(List<ClubDaySchedule> ds, List<Employee> emps){
		
		final Map<ClubDaySchedule, Integer> m = new HashMap<ClubDaySchedule, Integer>();
		for (ClubDaySchedule c : ds) {
			List<Employee> pe = getPreferredEmps(emps, c.getClub());
			m.put(c, pe.size());
		}
		
		Comparator<ClubDaySchedule> comp = new Comparator<ClubDaySchedule>() {

			@Override
			public int compare(ClubDaySchedule o1, ClubDaySchedule o2) {
				int res = Integer.compare(m.get(o2), m.get(o1));
				return res;
			}
		};
		Collections.sort(ds, comp);
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
		StringBuilder builder = new StringBuilder();
		switch (Const.TO_STRING_MODE) {
		case info:
			builder.append("Schedule [period=");
			builder.append(period);
			builder.append(", status=");
			builder.append(status);
			builder.append("]");
			break;
		case normal:
			builder.append("Schedule [period=");
			builder.append(period);
			builder.append(", status=");
			builder.append(status);
			builder.append(", \n\tdayScheduleMap=");
			builder.append(dayScheduleMap);
			builder.append(", \n\tclubPrefs=");
			builder.append(clubPrefs);
			builder.append("]");
			break;
		case debug:
			builder.append("Schedule [period=");
			builder.append(period);
			builder.append(", status=");
			builder.append(status);
			builder.append(", \n\tdayScheduleMap=");
			builder.append(dayScheduleMap);
			builder.append(", clubPrefs=");
			builder.append(clubPrefs);
			builder.append(", timeStamp=");
			builder.append(timeStamp);
			builder.append(", locked=");
			builder.append(locked);
			builder.append("]");
			break;
		case fullInfo:
		case fullNormal:
		case fullDebug:
			builder.append("Schedule [period=");
			builder.append(period);
			builder.append(", status=");
			builder.append(status);
			builder.append(", dayScheduleMap=");
			builder.append(dayScheduleMap);
			builder.append(", clubPrefs=");
			builder.append(clubPrefs);
			builder.append(", timeStamp=");
			builder.append(timeStamp);
			builder.append(", locked=");
			builder.append(locked);
			builder.append("]");
			break;
		default:
			break;
		
		}
		return builder.toString();
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
