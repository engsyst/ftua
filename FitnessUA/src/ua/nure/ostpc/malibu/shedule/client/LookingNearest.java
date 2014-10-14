package ua.nure.ostpc.malibu.shedule.client;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubDaySchedule;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Shift;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class LookingNearest extends SimplePanel {

	private final ScheduleManagerServiceAsync scheduleManagerService = GWT
			.create(ScheduleManagerService.class);
	private static DateTimeFormat tableDateFormat = DateTimeFormat
			.getFormat("dd.MM.yyyy");

	private Schedule schedule;
	private Set<Club> clubs = new HashSet<Club>();
	private Map<Club, Integer> shiftsOnClub = new HashMap<Club, Integer>();

	public LookingNearest() {
		Date dateTime = new Date(System.currentTimeMillis());
		scheduleManagerService.getCurrentSchedule(dateTime,
				new AsyncCallback<Schedule>() {

					@Override
					public void onSuccess(Schedule result) {
						setSchedule(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Возникли проблемы с сервером, обратитесь к системному администратору");
					}
				});
		Timer timer = new Timer() {
			private int count;

			@Override
			public void run() {
				if (count < 10) {
					if (schedule != null) {
						cancel();
						drawPage();
					}
					count++;
				} else {
					Window.alert("Текущее расписание не составлено");
					cancel();
				}
			}
		};
		timer.scheduleRepeating(100);
	}

	private void drawPage() {
		Map<Date, List<ClubDaySchedule>> dayScheduleMap = schedule
				.getDayScheduleMap();
		Set<Date> dates = dayScheduleMap.keySet();
		FlexTable flexTable = new FlexTable();
		DrawTimeLine(flexTable, dates);
		setCountShiftsParametres(schedule);
		drawClubColumn(flexTable);
		setContent(flexTable,1);
		setWidget(flexTable);
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	private void fillWithColumns(FlexTable flexTable, int column, int rowCount,
			Date finalDate) {
		Date currentDate = new Date(finalDate.getTime());
		for (int row = 0; row < rowCount; row++) {
			for (int i = column; i < 8; i++) {
				flexTable.insertCell(row, i);
				if (row == 0) {
					CalendarUtil.addDaysToDate(currentDate, 1);
					flexTable.setText(row, i,
							tableDateFormat.format(currentDate));
				} else {
					flexTable.setText(row, i, "");
				}
			}
		}
	}

	private void DrawTimeLine(FlexTable flexTable, Set<Date> dates) {
		int count = 1;
		flexTable.setStyleName("myBestFlexTable");
		flexTable.insertRow(0);
		flexTable.insertCell(0, 0);
		flexTable.setText(0, 0, "");
		for (Date date : dates) {
			if (date != null && count < 9) {
				flexTable.insertCell(0, count);
				flexTable.setText(0, count, tableDateFormat.format(date));
				count++;
			} else {
				return;
			}
		}
		if (dates.size() < 7) {
			int milicount = 1;
			for (Date date : dates) {
				if (milicount == count - 1) {
					fillWithColumns(flexTable, count, flexTable.getRowCount(),
							date);
				} else {
					milicount++;
				}
			}
		}
	}

	private void drawClubColumn(FlexTable flexTable) {
		Iterator<Club> iter = clubs.iterator();
		for (int i = 0; i < this.clubs.size(); i++) {
			Club club = iter.next();
			flexTable.insertRow(i + 1);
			flexTable.insertCell(i + 1, 1);
			flexTable.insertCell(i + 1, 2);
			flexTable.setText(i + 1, 0, club.getTitle());
		}
	}

	private void setContent(FlexTable flexTable, int column) {
		for (int i = column; i <= column + 7; i++) {
			for (int j = 1; j < flexTable.getRowCount(); j++) {
				flexTable.setWidget(
						j,
						i,
						InsertInTable(flexTable,
								getCountShiftsOnClub(getClubByRow(j)), i, j));
			}
		}
	}
	private Club getClubByRow(int row) {
		Iterator<Club> iterator = this.clubs.iterator();
		int count = 1;
		while (iterator.hasNext()) {
			if (row == count) {
				return iterator.next();
			} else {
				count++;
				iterator.next();
			}
		}
		return null;
	}
	private int getCountShiftsOnClub(Club club) {
		return this.shiftsOnClub.get(club);
	}
	private void setCountShiftsParametres(Schedule schedule) {
		try {
			Map<java.sql.Date, List<ClubDaySchedule>> notRight = schedule
					.getDayScheduleMap();
			Set<java.sql.Date> lst = notRight.keySet();
			Iterator<java.sql.Date> iterator = lst.iterator();
			List<ClubDaySchedule> clubDayScheduleList = notRight.get(iterator
					.next());
			Iterator<ClubDaySchedule> iter = clubDayScheduleList.iterator();
			while (iter.hasNext()) {
				ClubDaySchedule daySchedule = iter.next();
				Club club = daySchedule.getClub();
				Integer countShiftsonClub = daySchedule.getShifts().size();
				this.shiftsOnClub.put(club, countShiftsonClub);
				this.clubs.add(club);
			}

		} catch (Exception ex) {
			Window.alert("Cannot get data from Schedule (ShiftParams) "
					+ ex.getMessage());
		}
	}
	private FlexTable InsertInTable(FlexTable flexTable, int CountShifts,
			int column, int rowNumber) {
		final FlexTable innerFlexTable = new FlexTable();
		final FlexTable reserveFlexTable = flexTable;
		final int col = column;
		final int rownumber = rowNumber;
		innerFlexTable.setStyleName("reserveTable");

		for (int i = 0; i < CountShifts; i++) {
			final int row = i;
			innerFlexTable.insertRow(i);
			String employees = "";
			List<Employee> emps = getEmployeeListFromShift(this.schedule,
					getDateByColumn(flexTable, column), i,
					getClubByRow(rownumber));
			if (emps != null) {
				Iterator<Employee> iterator = emps.iterator();
				while (iterator.hasNext()) {
					employees = employees + iterator.next().getLastName() + " ";
				}
			}
			innerFlexTable.setText(i, 0, employees); // Pay attention
			innerFlexTable.insertCell(i, 1);
		}
		flexTable = reserveFlexTable;
		return innerFlexTable;
	}
	private Date getDateByColumn(FlexTable flexTable, int column) {
		Map<java.sql.Date, List<ClubDaySchedule>> notRight = schedule
				.getDayScheduleMap();
		Set<java.sql.Date> set = notRight.keySet();
		Iterator<java.sql.Date> iterator = set.iterator();
		while (iterator.hasNext()) {
			Date date = iterator.next();
			for (int i = 3; i < flexTable.getCellCount(0); i++) {
				if (i == column
						&& tableDateFormat.format(date).equals(
								flexTable.getText(0, column))) {
					return date;
				}
			}
		}
		Window.alert("There is mistake within getDateByColumn, please check your methods");
		return null;
	}
	private List<Employee> getEmployeeListFromShift(Schedule schedule,
			Date date, int rowNumber, Club club) {
		Map<java.sql.Date, List<ClubDaySchedule>> notRight = schedule // Lets
																		// find
																		// mistake
																		// there)))
				.getDayScheduleMap();
		List<ClubDaySchedule> clubDaySchedule = notRight.get(date);
		Iterator<ClubDaySchedule> iterator = clubDaySchedule.iterator();
		while (iterator.hasNext()) {
			ClubDaySchedule clds = iterator.next();
			List<Shift> shifts = clds.getShifts();
			Iterator<Shift> iter = shifts.iterator();
			int count = 0;
			while (iter.hasNext()) {
				if (count == rowNumber
						&& club.getClubId() == clds.getClub().getClubId()) {
					return iter.next().getEmployees();
				} else {
					count++;
					iter.next();
				}

			}
		}
		Window.alert("There is a mistake within getEmployeeListFromShift, it returns null");
		return null;
	}

}
