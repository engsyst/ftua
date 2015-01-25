package ua.nure.ostpc.malibu.shedule.client;

import java.util.Date;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.service.DateUtil;

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

		Date startDrawDate = DateUtil.subDays(schedule.getPeriod().getStartDate(), startDayOfWeek);
		System.out.println("startDrawDate: " + startDrawDate);
		int k = endDayOfWeek % 7;
		System.out.println("k " + k);
		Date endDrawDate = DateUtil.subDays(schedule.getPeriod().getEndDate(), (6 - endDayOfWeek));
		System.out.println("endDrawDate: " + endDrawDate);
		
		int duration = (int) DateUtil.subDays(schedule.getPeriod().getStartDate(),
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
		for (int i = 0; i < 7; i++) {
			if (i == 0)
				System.out.print("--Head--\t");
			else {
				System.out.print(daysOfWeek[i - 1] + "\t");
			}
		}
		System.out.println("");
	}



	public static void main(String[] args) {
		DAOFactory msdf = DAOFactory.getDAOFactory(DAOFactory.MSSQL);
		ScheduleDAO sd = msdf.getScheduleDAO();
		Schedule s = sd.getSchedule(5);
		
		Draft draft = new Draft(s);
		draft.draw();
	}
}
