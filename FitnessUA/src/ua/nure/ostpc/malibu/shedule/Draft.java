package ua.nure.ostpc.malibu.shedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubDaySchedule;
import ua.nure.ostpc.malibu.shedule.entity.ClubPref;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Preference;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Shift;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;
import ua.nure.ostpc.malibu.shedule.shared.DateUtil;

public class Draft {
	private Schedule schedule;
	private String[][] table;
	private Set<Club> clubs;
	public final static String daysOfWeek[] = new String[] {
		"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"
	};

	public Draft(Schedule s) {
		schedule = s;
	}
	
	public void draw() {
		System.out.println(schedule.getPeriod().getStartDate());
		int startDayOfWeek = DateUtil.dayOfWeak(schedule.getPeriod().getStartDate());
		System.out.println("startDayOfWeek " + startDayOfWeek);

		System.out.println(schedule.getPeriod().getEndDate());
		int endDayOfWeek = DateUtil.dayOfWeak(schedule.getPeriod().getEndDate());
		System.out.println("endDayOfWeek " + endDayOfWeek);

		Date startDrawDate = DateUtil.addDays(schedule.getPeriod().getStartDate(), startDayOfWeek);
		System.out.println("startDrawDate: " + startDrawDate);
		int k = endDayOfWeek % 7;
		System.out.println("k " + k);
		Date endDrawDate = DateUtil.addDays(schedule.getPeriod().getEndDate(), (6 - endDayOfWeek));
		System.out.println("endDrawDate: " + endDrawDate);
		
		int duration = (int) DateUtil.duration(schedule.getPeriod().getStartDate(),
				schedule.getPeriod().getEndDate()) + startDayOfWeek + (6 - endDayOfWeek) + 1;
		System.out.println(duration);
		
		int clubCount = schedule.getDayScheduleMap().values().size();
		table = new String[clubCount + 1][duration + 1];
		for (int i = 0; i < table.length; i++) {
			if ((i % 7) == 0) {
				System.out.println("");
				drawHeader();
			}
			for (int j = 0; j < table[i].length / (table[i].length / 7); j++) {
				if (j == 0)
					System.out.print("---------\t");
				else {
					System.out.print(daysOfWeek[(j - 1) % 7] + "\t");
				}
			}
			System.out.println("");
		}
	}

	
	private void drawHeader() {
		System.out.print("--Head--\t");
		for (int i = 0; i < daysOfWeek.length; i++) {
			System.out.print(daysOfWeek[i] + "\t");
		}
		System.out.println("");
	}



	public static void main(String[] args) {
		DAOFactory msdf = DAOFactory.getDAOFactory(DAOFactory.MSSQL);
		Set<Club> clubs = new HashSet<Club>(msdf.getClubDAO().getAllOuterClubs());
		Preference prefs = msdf.getPreferenceDAO().getLastPreference();
//		Schedule s = msdf.getScheduleDAO().getSchedule(5);
		
		Date d = new Date(System.currentTimeMillis());
		Schedule s  = newEmptyShedule(d, DateUtil.addDays(d, 0 - 5), clubs, prefs);
		System.err.println(s);
		Draft draft = new Draft(s);
		draft.draw();
	}
	public static Schedule newEmptyShedule(Date start, Date end,
			Set<Club> clubs, Preference prefs) {
		assert start != null : "Start can not be a null";
		assert end != null : "End can not be a null";
		assert start.compareTo(end) <= 0 : "Start date must be before or equal end date";
		assert clubs != null : "clubs can not be a null";

		Schedule s = new Schedule();
		s.setPeriod(new Period(start, end));
		s.setStatus(Status.DRAFT);
		s.setDayScheduleMap(new HashMap<Date, List<ClubDaySchedule>>());
		s.setClubPrefs(new ArrayList<ClubPref>());
		Date d = new Date(start.getTime());
		while (!d.after(end)) {
			List<ClubDaySchedule> cdShedules = new ArrayList<ClubDaySchedule>();
			for (Club c : clubs) {
				ClubDaySchedule cds = new ClubDaySchedule();
				cds.setDate(d);
				cds.setWorkHoursInDay(prefs.getWorkHoursInDay());
				cds.setShiftsNumber(prefs.getShiftsNumber());
				cds.setClub(c);
				cds.setShifts(new ArrayList<Shift>());
				for (int i = 0; i < cds.getShiftsNumber(); i++) {
					Shift shift = new Shift();
					shift.setShiftNumber(i);
					shift.setQuantityOfEmployees(1);
					shift.setEmployees(new ArrayList<Employee>());
					cds.getShifts().add(shift);
				}
				cdShedules.add(cds);
			}
			s.getDayScheduleMap().put(d, cdShedules);
			d = DateUtil.addDays(d, 0 - 1);
		}
		return s;
	}
}
