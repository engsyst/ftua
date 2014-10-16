package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;

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

	private Set<Employee> getInvolvedInDate(List<ClubDaySchedule> daySchedules) {
		Set<Employee> clubDayEmps = new HashSet<Employee>();
		for (ClubDaySchedule c : daySchedules) {
			clubDayEmps.addAll( c.getEmployees());
		}
		return clubDayEmps;
	}

	private void sortByPriority(List<Employee> emps, Club club) {
		final List<Employee> prefEmps = getPreferredEmps(club, emps);
		final Map<Employee, Integer> ass = getAssignments();

		Comparator<Employee> comparator = new Comparator<Employee>() {
			@Override
			public int compare(Employee o1, Employee o2) {
				boolean in1 = prefEmps.contains(o1);
				boolean in2 = prefEmps.contains(o2);
				if ((in1 && in2) || (!in1 && !in2)) {
					return Integer.compare(o1.getMaxDays() - ass.get(o1),
							o2.getMaxDays() - ass.get(o2));
				}
				return Boolean.compare(in1, in2);
			}
		};
		Collections.sort(emps, comparator);
	}

	public Map<Employee, Integer> getAssignments() {
		HashMap<Employee, Integer> ass = new HashMap<Employee, Integer>();
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
					Integer count = ass.get(e);
					ass.put(e, count == null ? 0 : ++count);
				}
			}
		}
		return null;
	}
	
	/**
	 * Automation of Schedule making
	 * 
	 * @return
	 */
	public boolean generate() {
		boolean ok = true;
		if(status != Schedule.Status.DRAFT) return !ok;
		
		// get all Employees
		DAOFactory df = DAOFactory.getDAOFactory(DAOFactory.MSSQL);
		EmployeeDAO ed = df.getEmployeeDAO();
		
		ArrayList<Employee> allEmps = (ArrayList<Employee>) ed.getAllEmployee();
		if (allEmps == null) return !ok;

		Set<Employee> involvedEmps = new HashSet<Employee>();
		
		// By date
		Set<Date> dates =  dayScheduleMap.keySet();
		Iterator<Date> dIter = dates.iterator();
		while (dIter.hasNext()) {
			List<ClubDaySchedule> daySchedules = dayScheduleMap.get(dIter.next());
			involvedEmps = getInvolvedInDate(daySchedules);
			
			// By club
			ListIterator<ClubDaySchedule> cdsIter = daySchedules.listIterator();
			while (cdsIter.hasNext()) {
				// get next schedule of club at this date
				ClubDaySchedule clubDaySchedule = cdsIter.next();
				
				// get free Employees
				ArrayList<Employee> freeEmps = (ArrayList<Employee>) allEmps.clone();
				freeEmps.removeAll(involvedEmps);
				if (clubDaySchedule.isFull()) continue;
				sortByPriority(freeEmps, clubDaySchedule.getClub());
				if (!clubDaySchedule.addEmployeesToShifts(freeEmps) && freeEmps.isEmpty()) return !ok;
				clubDaySchedule.addEmployeesToShifts(freeEmps);
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
	
	

	/**
	 * @param club
	 * @param emps
	 * @return list of preferred employees for club
	 */
	private List<Employee> getPreferredEmps(Club club, List<Employee> emps) {
		List<Employee> re = new ArrayList<Employee>();
		for (ClubPref cp : clubPrefs) {
			for (Employee e : emps) 
				if (cp.getClubId() == e.getEmployeeId()) {
					re.add(e);
					break;
				}
		}
		if (re.isEmpty()) re = null;
		return re;
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
