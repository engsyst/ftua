package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import ua.nure.ostpc.malibu.shedule.Const;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * ClubDaySchedule.
 * 
 * @author engsyst
 */
public class ClubDaySchedule implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;

	private long clubDayScheduleId;
	private long schedulePeriodId;
	private Date date;
	private Club club;
	private int shiftsNumber;
	private int workHoursInDay;
	private List<Shift> shifts;

	public ClubDaySchedule() {
	}

	public ClubDaySchedule(long clubDayScheduleId, long schedulePeriodId,
			Date date, Club club, int shiftsNumber, int workHoursInDay,
			List<Shift> shifts) {
		this.clubDayScheduleId = clubDayScheduleId;
		this.schedulePeriodId = schedulePeriodId;
		this.date = date;
		this.club = club;
		this.shiftsNumber = shiftsNumber;
		this.workHoursInDay = workHoursInDay;
		this.shifts = shifts;
	}

	public long getClubDayScheduleId() {
		return clubDayScheduleId;
	}

	public void setClubDayScheduleId(long clubDayScheduleId) {
		this.clubDayScheduleId = clubDayScheduleId;
	}

	public long getSchedulePeriodId() {
		return schedulePeriodId;
	}

	public void setSchedulePeriodId(long schedulePeriodId) {
		this.schedulePeriodId = schedulePeriodId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Club getClub() {
		return club;
	}

	public void setClub(Club club) {
		this.club = club;
	}

	public int getShiftsNumber() {
		return shiftsNumber;
	}

	public void setShiftsNumber(int shiftsNumber) {
		this.shiftsNumber = shiftsNumber;
	}

	public int getWorkHoursInDay() {
		return workHoursInDay;
	}

	public void setWorkHoursInDay(int workHoursInDay) {
		this.workHoursInDay = workHoursInDay;
	}

	public List<Shift> getShifts() {
		return shifts;
	}

	public void setShifts(List<Shift> shifts) {
		this.shifts = shifts;
	}

	public boolean isFull() {
		for (Shift s : shifts) {
			if (!s.isFull())
				return false;
		}
		return true;
	}

	public boolean isEmpty() {
		for (Shift s : shifts) {
			if (!s.isEmpty()) return false;
		}
		return true;
	}
	
	public List<Employee> getEmployees() {
		ArrayList<Employee> emps = new ArrayList<Employee>();
		for (Shift s : shifts) {
			List<Employee> shiftEmps = s.getEmployees();
			if (shiftEmps != null)
				emps.addAll(shiftEmps);
		}
		return emps;
	}

	public TreeMap<Employee, Integer> getEmployeesWithPriority() {
		// TODO getEmployeesWithPriority
		List<Employee> emps = new ArrayList<Employee>();
		TreeMap<Employee, Integer> PriorityEmps = new TreeMap<Employee, Integer>();
		for (Shift s : shifts) {
			emps.addAll(s.getEmployees());
		}
		return PriorityEmps;
	}

	public boolean containsEmployeeInShifts(long employeeId) {
		if (shifts != null) {
			for (Shift shift : shifts) {
				if (shift.containsEmployee(employeeId)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		switch (Const.TO_STRING_MODE) {
		case info:
			builder.append("ClubDaySchedule [Id=");
			builder.append(clubDayScheduleId);
			builder.append(date);
			builder.append(", ");
			builder.append(club);
			builder.append(", shiftsNumber=");
			builder.append(shiftsNumber);
			builder.append("]");
			break;
		case normal:
			builder.append("ClubDaySchedule [Id=");
			builder.append(clubDayScheduleId);
			builder.append(", ");
			builder.append(date);
			builder.append(", shiftsNumber=");
			builder.append(shiftsNumber);
			builder.append(", \n\t");
			builder.append(club);
			builder.append(", \n\t");
			builder.append(shifts);
			builder.append("]");
			break;
		case debug:
		case fullInfo:
		case fullNormal:
		case fullDebug:
			builder.append("ClubDaySchedule [Id=");
			builder.append(clubDayScheduleId);
			builder.append(", PeriodId=");
			builder.append(schedulePeriodId);
			builder.append(", ");
			builder.append(date);
			builder.append(", \n\t");
			builder.append(club);
			builder.append(", \n\t");
			builder.append(shifts);
			builder.append(", shiftsNumber=");
			builder.append(shiftsNumber);
			builder.append(", workHoursInDay=");
			builder.append(workHoursInDay);
			builder.append("]");
			break;
		default:
			break;

		}
		return builder.toString();
	}

	/*
	 * @Override public String toString() { StringBuilder sb = new
	 * StringBuilder(); sb.append("ClubDaySchedule [clubDayScheduleId=");
	 * sb.append(clubDayScheduleId); sb.append(", schedulePeriodId=");
	 * sb.append(schedulePeriodId); sb.append(", clubId=");
	 * sb.append(club.getClubId()); sb.append(", date="); sb.append(date);
	 * sb.append(", shiftsNumber="); sb.append(shiftsNumber);
	 * sb.append(", workHoursInDay="); sb.append(workHoursInDay);
	 * sb.append("]"); return sb.toString(); }
	 */
	/**
	 * Fill employees from emps to all {@link Shift}s in this club at this date
	 * and <b>remove</b> their from emps
	 * 
	 * @param emps
	 * @return true if Shift is full
	 * @see {@link Shift}
	 */
	public boolean assignEmployeesToShifts(List<Employee> emps) {
		boolean full = true;
		for (Shift s : shifts) {
			full = s.addEmployees(emps, date);
		}
		return full;
	}

}
