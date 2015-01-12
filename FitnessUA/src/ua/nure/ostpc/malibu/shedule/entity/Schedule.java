package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
	private GenFlags mode;

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
				List<Employee> ce = clubDaySchedule.getEmployees();
				for (Employee e : ce) {
					Integer count = ass.get(e);
					ass.put(e, count == null ? 0 : ++count);
				}
			}
		}
		return ass;
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
	
	private Set<Employee> getInvolvedInDate(List<ClubDaySchedule> daySchedules) {
		Set<Employee> clubDayEmps = new HashSet<Employee>();
		for (ClubDaySchedule c : daySchedules) {
			clubDayEmps.addAll(c.getEmployees());
		}
		return clubDayEmps;
	}

	private void sortEmpsByPriority(List<Employee> toSort,
			final List<Employee> prefered, final Date start, final Date end, final EmplyeeObjective emplyeeObjective) {

		Comparator<Employee> comparator = new Comparator<Employee>() {
			@Override
			public int compare(Employee o1, Employee o2) {
				final boolean in1 = prefered.contains(o1);
				final boolean in2 = prefered.contains(o2);
				if ((in1 && in2) || (!in1 && !in2)) {
					return ((Double) emplyeeObjective.getObjectiveValue(start, end, o2)).compareTo(emplyeeObjective.getObjectiveValue(start, end, o1));
//					return ((Integer) (o1.getMaxDays() - o1.getLastAssignments()))
//							.compareTo(o2.getMaxDays() - o2.getLastAssignments());
					// (o1.getMaxDays() - o1.getMin()) / 2 - o1.getAssignment(),
					// (o2.getMaxDays() - o2.getMin()) / 2 -
					// o2.getAssignment());
				}
				return ((Boolean) in2).compareTo(in1);
			}
		};

		Collections.sort(toSort, comparator);
		// System.out.println(toSort);
	}

	private void moveEmpsToPreferredClub(Schedule s) {
		TreeSet<Date> dates = new TreeSet<Date>();
		dates.addAll(s.getDayScheduleMap().keySet());
		Iterator<Date> dIter = dates.iterator();
		while (dIter.hasNext()) {
			Date d = dIter.next();
			List<ClubDaySchedule> daySchedules = s.getDayScheduleMap().get(d);

			// By club
			ListIterator<ClubDaySchedule> cdsIter = daySchedules.listIterator();
			while (cdsIter.hasNext()) {
				// get next schedule of club at this date
				ClubDaySchedule clubDaySchedule = cdsIter.next();

				List<Shift> shifts = clubDaySchedule.getShifts();
				for (Shift sh : shifts) {
					sh.getEmployees();
				}
			}
		}
	}

	/**
	 * Set to every Employee count of their Assignments. Start from <b>start</b>,
	 * end <b>not</b> set to zero assignments from previous dates.
	 * You should set them manually.
	 */
	public void recountAssignments(java.util.Date start) {
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
				List<Employee> ce = clubDaySchedule.getEmployees();
				if (d.compareTo(start) >= 0)
					for (Employee e : ce) 
						e.addAssignment(d, 1);
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
				List<Employee> ce = clubDaySchedule.getEmployees();
				for (Employee e : ce) {
					e.clearAssignments();
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
				return ((Integer) m.get(o2)).compareTo(m.get(o1));
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

	public Schedule generate(final List<Employee> allEmps, final Preference prefs, final EmplyeeObjective emplyeeObjective) {
		mode = prefs.getMode();
		Set<Employee> involvedEmps = new HashSet<Employee>();

		// By date
		TreeSet<Date> dates = new TreeSet<Date>(getDayScheduleMap().keySet());
		
		Date firstDate = dates.first();
		recountAssignments(firstDate);
		
		Iterator<Date> dIter = dates.iterator();
		while (dIter.hasNext()) {
			Date d = dIter.next();
			List<ClubDaySchedule> daySchedules = getDayScheduleMap().get(d);
			int shiftsNumber = daySchedules.get(0).getShiftsNumber();
			int workHoursInDay = daySchedules.get(0).getWorkHoursInDay();
			int workHoursInShift = workHoursInDay / shiftsNumber;
			int maxWorkDays = prefs.getWorkHoursInWeek() / workHoursInShift;
			int maxContDays = prefs.getWorkContinusHours() / workHoursInShift;
			
			// Clubs with preferences will be assigned first
			sortClubsByPrefs(daySchedules, allEmps);
			
			// Reset all assignments to zero after each week
			long diff = (d.getTime() - firstDate.getTime())
					/ (1000 * 60 * 60 * 24);
			if ((diff % 7) == 0) {
				firstDate = d;
			}
			
			// Employees what can be assigned
			ArrayList<Employee> freeEmps = new ArrayList<Employee>(allEmps);

			// Check restrictions
			if (mode.isSet(GenFlags.CHECK_MAX_DAYS)) {
				ListIterator<Employee> eIter = freeEmps.listIterator();
				while (eIter.hasNext()) {
					Employee e = (Employee) eIter.next();
					if (e.getAssignments(firstDate, d) > e.getMaxDays()) {
						e.addAssignment(d, 0);
						eIter.remove();
						System.out.println("CHECK_MAX_DAYS Removed: " + e);
					}
				}
			}
			if (mode.isSet(GenFlags.CHECK_MAX_HOURS_IN_WEEK)) {
//				Map<Employee, Integer> as = s.getCountOfAssignmentsForEmps();
				
				ListIterator<Employee> eIter = freeEmps.listIterator();
				while (eIter.hasNext()) {
					Employee e = (Employee) eIter.next();
					int realDays = e.getAssignments(firstDate, d);
					if (realDays > maxWorkDays) {
						System.out.println("realDays = " + realDays + "> maxWorkDays = " + maxWorkDays);
						e.addAssignment(d, 0);
						eIter.remove();
						System.out.println("CHECK_MAX_HOURS_IN_WEEK Removed: " + e);
					}
				}
			}
			if (mode.isSet(GenFlags.WEEKEND_AFTER_MAX_HOURS)) {
//				Map<Employee, Integer> as = s.getCountOfAssignmentsForEmps();
				
				ListIterator<Employee> eIter = freeEmps.listIterator();
				while (eIter.hasNext()) {
					Employee e = (Employee) eIter.next();
					int realDays = e.getLastAssignments();
					if (realDays >= maxContDays) {
						System.out.println("realDays = " + realDays + "> maxContDays = " + maxContDays);
						e.addAssignment(d, 0);
						eIter.remove();
						System.out.println("WEEKEND_AFTER_MAX_HOURS Removed: " + e);
					} 
				}
			}
			
			// By club
			ListIterator<ClubDaySchedule> cdsIter = daySchedules.listIterator();
			while (cdsIter.hasNext()) {
				// get next schedule of club at this date
				ClubDaySchedule clubDaySchedule = cdsIter.next();
				
//				System.out.println("-- ClubDaySchedule --\n" + clubDaySchedule);

				involvedEmps = getInvolvedInDate(daySchedules);
				
				System.out.println("-- InvolvedEmps -- Size: " + involvedEmps.size() + "\n" + involvedEmps);
				
				freeEmps.removeAll(involvedEmps);
				
				System.out.println("-- FreeEmps -- Size: " + freeEmps.size() + "\n" + freeEmps);

				// check restrictions

				if (clubDaySchedule.isFull())
					continue;

				// Arrange by the objective function
				sortEmpsByPriority(freeEmps, getPreferredEmps(freeEmps, clubDaySchedule.getClub()), firstDate, d, emplyeeObjective);
				
//				System.out.println("-- FreeEmps sorted before -- Size: " + freeEmps.size() + "\n" + freeEmps);

				// if shifts in date not full and not enough free employees
				if (mode.isSet(GenFlags.SCHEDULE_CAN_EMPTY)) {
					if (!freeEmps.isEmpty())
						clubDaySchedule.assignEmployeesToShifts(freeEmps);
				} else {
					clubDaySchedule.assignEmployeesToShifts(freeEmps);
				}
				
//				System.out.println("-- FreeEmps sorted after -- Size: " + freeEmps.size() + "\n" + freeEmps);
			}
		}
		return this;
	}
	
//	public void merge(Schedule s) {
//		if (s == null)
//			throw new NullPointerException();
//		if (s instanceof Schedule) {
//			if (status == Status.DRAFT && !locked) {
//
//				for (Date currDate : s.getDayScheduleMap().keySet()) {
//					if (dayScheduleMap.containsKey(currDate)
//							&& dayScheduleMap.get(currDate) != null) {
//						for (ClubDaySchedule modClubDaySchedule : s.dayScheduleMap
//								.get(currDate)) {
//							for (ClubDaySchedule originClubDaySchedule : dayScheduleMap
//									.get(currDate)) {
//								if (!dayScheduleMap.get(currDate).contains(
//										modClubDaySchedule)) {
//									// Origin - first
//									// Mod - second
//									for (Shift originShift : originClubDaySchedule
//											.getShifts()) {
//										for (Shift modShift : modClubDaySchedule
//												.getShifts()) {
//											for (Employee originEmp : originShift
//													.getEmployees()) {
//												// origin has & mod no = removed
//												if (!modShift.getEmployees()
//														.contains(originEmp)) {
//													originShift.getEmployees()
//															.remove(originEmp);
//												}
//											}
//											for (Employee modEmp : modShift
//													.getEmployees()) {
//												// mod has & origin no = add
//												if (!originShift.getEmployees()
//														.contains(modEmp)) {
//													if (originShift
//															.getEmployees()
//															.size() < modShift
//															.getQuantityOfEmployees()) {
//														originShift
//																.getEmployees()
//																.add(modEmp);
//													}
//												}
//											}
//
//										}
//									}
//									if (modClubDaySchedule.getShifts().size() > 0) {
//										for (Shift cShift : modClubDaySchedule
//												.getShifts()) {
//											if (cShift.getEmployees().size() > 0) {
//												modClubDaySchedule.getShifts()
//														.add(cShift);
//											}
//										}
//									}
//								} else {
//									dayScheduleMap.put(currDate, s
//											.getDayScheduleMap().get(currDate));
//								}
//							}
//						}
//					}
//				}
//			}
//			timeStamp = System.currentTimeMillis(); // Right?
//		}
//	}
}
