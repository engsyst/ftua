package ua.nure.ostpc.malibu.shedule.client;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubDaySchedule;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Shift;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.shared.AssignmentInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.smartgwt.client.util.SC;

public class CopyOfScheduleDraft extends SimplePanel {

	private final ScheduleDraftServiceAsync scheduleDraftServiceAsync = GWT
			.create(ScheduleDraftService.class);

	private static Map<String, String> dayOfWeekMap;
	private static DateTimeFormat tableDateFormat;
	private static DateTimeFormat dayOfWeekFormat;

	private Employee employee = new Employee();
	private Schedule schedule = new Schedule();
	private Set<Club> clubs = new HashSet<Club>();
	private Period period = new Period();
	private String[] surnames;
	private int counts;
	private Map<Club, List<Employee>> empToClub;
	private Map<Club, Integer> shiftsOnClub = new HashMap<Club, Integer>();
	private Map<Club, Integer> countPeopleOnClubShift = new HashMap<Club, Integer>();

	static {
		dayOfWeekMap = new HashMap<String, String>();
		dayOfWeekMap.put("0", "Вс");
		dayOfWeekMap.put("1", "Пн");
		dayOfWeekMap.put("2", "Вт");
		dayOfWeekMap.put("3", "Ср");
		dayOfWeekMap.put("4", "Чт");
		dayOfWeekMap.put("5", "Пт");
		dayOfWeekMap.put("6", "Сб");
		tableDateFormat = DateTimeFormat.getFormat("dd.MM.yyyy");
		dayOfWeekFormat = DateTimeFormat.getFormat("c");
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String[] getSurnames() {
		return surnames;
	}

	public void setSurnames(String[] surnames) {
		this.surnames = surnames;
	}

	public int getCounts() {
		return counts;
	}

	public void setCounts(int counts) {
		this.counts = counts;
	}

	public Set<Club> getClubs() {
		return clubs;
	}

	public void setClubs(Set<Club> clubs) {
		this.clubs = clubs;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public Map<Club, List<Employee>> getEmpToClub() {
		return empToClub;
	}

	public void setEmpToClub(Map<Club, List<Employee>> empToClub) {
		this.empToClub = empToClub;
	}

	public Map<Club, Integer> getShiftsOnClub() {
		return shiftsOnClub;
	}

	public void setShiftsOnClub(Map<Club, Integer> shiftsOnClub) {
		this.shiftsOnClub = shiftsOnClub;
	}

	public Map<Club, Integer> getCountPeopleOnClubShift() {
		return countPeopleOnClubShift;
	}

	public void setCountPeopleOnClubShift(
			Map<Club, Integer> countPeopleOnClubShift) {
		this.countPeopleOnClubShift = countPeopleOnClubShift;
	}

	public CopyOfScheduleDraft(long periodId) {
		doSomething(periodId);
	}

	public void doSomething(long periodId) {
		this.period.setPeriodId(periodId);

		scheduleDraftServiceAsync.getEmployee(new AsyncCallback<Employee>() {

			@Override
			public void onSuccess(Employee result) {
				setEmployee(result);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
		try {
			scheduleDraftServiceAsync.getEmpToClub(this.period.getPeriodId(),
					new AsyncCallback<Map<Club, List<Employee>>>() {

						@Override
						public void onFailure(Throwable caught) {
							SC.say("Проблемы с сервером, пожалуйста обратитесь к системному администратору \n Код ошибки 6");
						}

						@Override
						public void onSuccess(Map<Club, List<Employee>> result) {
							setEmpToClub(result);
						}

					});
		} catch (Exception e) {
			Window.alert(e.getMessage());
		}

		scheduleDraftServiceAsync.getScheduleById(periodId,
				new AsyncCallback<Schedule>() {

					@Override
					public void onSuccess(Schedule result) {
						schedule = result;
						if (schedule == null) {
							SC.say("Указанного графика не существует либо он имеет статус черновика");
						}
					}

					@Override
					public void onFailure(Throwable caught) {
					}
				});
		Timer timer = new Timer() {
			private int count;

			@Override
			public void run() {
				if (count < 30) {
					if (empToClub != null && schedule != null) {
						cancel();
						drawPage();
					}
					count++;
				} else {
					SC.say("Проблемы с сервером, пожалуйста обратитесь к системному администратору \n Код ошибки 7");
					cancel();
				}
			}
		};
		timer.scheduleRepeating(AppConstants.ASYNC_DELAY);
	}

	public void drawPage() {
		setCountShiftsParametres(this.schedule);
		DockPanel dockPanel = new DockPanel();
		setWidget(dockPanel);
		dockPanel.setSize("100%", "100%");

		final InlineLabel Greetings = new InlineLabel();
		Greetings.setText("Добро пожаловать в черновик" + " "
				+ employee.getLastName());
		AbsolutePanel absolutePanel = new AbsolutePanel();
		absolutePanel.setStyleName("TableBlock");
		final FlexTable flexTable = new FlexTable();
		flexTable.addStyleName("MainTable");
		flexTable.addStyleName("mainTable");
		absolutePanel.add(flexTable, 10, 10);
		flexTable.setSize("100px", "100px");
		dockPanel.add(absolutePanel, DockPanel.CENTER);
		flexTable.insertRow(0);
		flexTable.setText(0, 0, " ");

		flexTable.getCellFormatter().addStyleName(0, 1, "secondHeader");
		drawClubColumn(flexTable);
		drawTimeLine(flexTable, schedule.getPeriod().getStartDate(), schedule
				.getPeriod().getEndDate(), absolutePanel);
		insertClubPrefs(flexTable, 2);
		flexTable.removeCell(0, 8);// Kostil
		flexTable.removeCell(0, 8);// More kostil
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
			final CheckBox checkbox = new CheckBox();
			if (innerFlexTable.getText(row, 0).split(" ").length >= getCountPeopleOnClubShifts(getClubByRow(rownumber))
					&& innerFlexTable.getText(row, 0).contains(
							employee.getLastName()) == false) {
				if (innerFlexTable.getText(row, 0) == ""
						|| innerFlexTable.getText(row, 0) == " ") {
					checkbox.setEnabled(true);
					checkbox.setValue(false);
					setCheckBoxes(checkbox, col, rownumber, innerFlexTable,
							reserveFlexTable, row);
				} else {
					checkbox.setEnabled(false);
				}
			} else if (innerFlexTable.getText(row, 0).contains(
					employee.getLastName()) == true) {
				checkbox.setValue(true);
				setCheckBoxes(checkbox, col, rownumber, innerFlexTable,
						reserveFlexTable, row);
			} else {
				setCheckBoxes(checkbox, col, rownumber, innerFlexTable,
						reserveFlexTable, row);
			}
			innerFlexTable.setWidget(i, 1, checkbox);
		}
		flexTable = reserveFlexTable;
		return innerFlexTable;
	}

	private void makeOthersDisabled(FlexTable flexTable, int column,
			int rowNumber, boolean isEnabled) {
		setCounts(0);
		for (int row = 1; row < flexTable.getRowCount(); row++) {
			if (row == rowNumber) {
				continue;
			} else {
				FlexTable innerFlexTable = (FlexTable) flexTable.getWidget(row,
						column);
				for (int i = 0; i < getCountShiftsOnClub(getClubByRow(rowNumber)); i++) {
					if (isEnabled == false) {
						CheckBox checkbox = (CheckBox) innerFlexTable
								.getWidget(i, 1);
						checkbox.setEnabled(false);
						innerFlexTable.setWidget(i, 1, checkbox);
					} else {
						FlexTable innerCopyFlexTable = (FlexTable) flexTable
								.getWidget(rowNumber, column);
						int count = 0;
						for (int j = 0; j < getCountShiftsOnClub(getClubByRow(rowNumber)); j++) {
							CheckBox checkbox = (CheckBox) innerCopyFlexTable
									.getWidget(j, 1);
							if (checkbox.getValue() == true) {
								count++;
							}
						}
						if (count >= 1) {
							return;
						} else {
							CheckBox checkbox = (CheckBox) innerFlexTable
									.getWidget(i, 1);
							if ((innerFlexTable.getText(i, 0).equals("") || innerFlexTable
									.getText(i, 0).equals(" "))
									&& innerFlexTable.getText(row, 0)
											.split(" ").length >= getCountPeopleOnClubShifts(getClubByRow(row))) {

								checkbox.setEnabled(true);
							}
							innerFlexTable.setWidget(i, 1, checkbox);
						}
					}
				}
				flexTable.setWidget(row, column, innerFlexTable);
			}
		}
	}

	private void drawTimeLine(FlexTable flexTable, Date startdate,
			Date enddate, AbsolutePanel absolutePanel) {
		Date startDate = startdate;
		Date endDate = enddate;
		Date currentDate = new Date(startDate.getTime());
		int count = 1;
		// flexTable.insertCell(0, 2);
		// flexTable.setText(0, 2, "V.I.P");
		flexTable.getCellFormatter().addStyleName(0, 2, "secondHeader");
		while (currentDate.getTime() <= endDate.getTime()) {
			for (int i = 0; i <= getClubs().size(); i++) {
				flexTable.insertCell(i, count);
			}
			// flexTable.setText(0, count, tableDateFormat.format(currentDate));
			VerticalPanel vp = new VerticalPanel();
			InlineLabel label1 = new InlineLabel(
					tableDateFormat.format(currentDate));
			InlineLabel label2 = new InlineLabel(
					dayOfWeekMap.get(dayOfWeekFormat.format(currentDate)));
			vp.add(label1);
			vp.add(label2);
			flexTable.setWidget(0, count, vp);
			flexTable.getCellFormatter().addStyleName(0, count, "secondHeader");
			count++;
			if (count == 8) {
				setContent(flexTable, 1);
			} else if (count > 8) {
				for (int i = 0; i < flexTable.getRowCount(); i++) {
					flexTable.removeCell(i, 8);
				}
				makeNewTable(absolutePanel, currentDate, endDate);
				return;
			} else {
				CalendarUtil.addDaysToDate(currentDate, 1);
			}
			if (currentDate.getTime() > endDate.getTime()) {
				setNotFullContent(flexTable, 1, count);
			}
		}
	}

	private void drawClubColumn(FlexTable flexTable) {
		Iterator<Club> iter = clubs.iterator();
		for (int i = 0; i < this.getClubs().size(); i++) {
			Club club = iter.next();
			flexTable.insertRow(i + 1);
			// flexTable.insertCell(i + 1, 0);
			// flexTable.insertCell(i + 1, 2);
			flexTable.setText(i + 1, 0, club.getTitle());
//			try {
//				// flexTable.setText(i + 1, 1,
//				// Integer.toString(getCountPeopleOnClubShifts(club)));
//			} catch (Exception ex) {
//				SC.say("Проблемы с сервером, пожалуйста обратитесь к системному администратору \n Код ошибки 8");
//			}
		}
	}

	private void insertClubPrefs(FlexTable flexTable, int column) {

		for (int i = 0; i < this.getClubs().size(); i++) {
			Set<String> set = new HashSet<String>();
			ListBox comboBox = new ListBox();
			for (Club club : this.empToClub.keySet()) {
				if (club.getTitle()
						.equals(flexTable.getText(i + 1, column - 2))) {
					for (Employee employee : this.empToClub.get(club)) {
						set.add(employee.getLastName());
					}
				}

			}
			for (String item : set) {
				boolean isContain = false;
				comboBox.addItem(item);
				if (item.equals(this.employee.getLastName())) {
					isContain = true;
					applyDataRowStyles(flexTable, isContain, i + 1);
				} else {
					isContain = false;
				}
			}

			VerticalPanel vp = new VerticalPanel();
			InlineLabel label = new InlineLabel(flexTable.getText(i + 1,
					column - 2));
			label.setStyleName("ClubName");
			vp.add(label);
			InlineLabel label1 = new InlineLabel(
					"Число рабочих на смене: "
							+ Integer
									.toString(getCountPeopleOnClubShifts(getClubByRow(i + 1))));
			vp.add(label1);
			vp.add(comboBox);
			flexTable.setWidget(i + 1, column - 2, vp);
		}
	}

	private void setContent(FlexTable flexTable, int column) {
		for (int i = column; i <= column + 6; i++) {
			for (int j = 1; j < flexTable.getRowCount(); j++) {
				flexTable.setWidget(
						j,
						i,
						InsertInTable(flexTable,
								getCountShiftsOnClub(getClubByRow(j)), i, j));
			}
		}
	}

	private void setNotFullContent(FlexTable flexTable, int column, int count) {
		for (int i = column; i < count; i++) {
			for (int j = 1; j < flexTable.getRowCount(); j++) {
				flexTable.setWidget(
						j,
						i,
						InsertInTable(flexTable,
								getCountShiftsOnClub(getClubByRow(j)), i, j));
			}
		}
	}

	private void applyDataRowStyles(FlexTable flexTable, boolean isContain,
			int row) {
		HTMLTable.RowFormatter rf = flexTable.getRowFormatter();
		if (isContain) {
			rf.addStyleName(row, "FlexTable-OddRow");
		}
	}

	private void makeNewTable(AbsolutePanel absolutePanel, Date newStartDate,
			Date newFinalDate) {
		CalendarUtil.addDaysToDate(newStartDate, 1);
		FlexTable flexTable = new FlexTable();
		flexTable.setStyleName("MainTable");
		flexTable.addStyleName("mainTable");
		absolutePanel.add(flexTable, 10, 10);
		flexTable.setSize("100px", "100px");

		flexTable.insertRow(0);
		flexTable.setText(0, 0, " ");
		// flexTable.getCellFormatter().addStyleName(0, 1, "secondHeader");
		drawClubColumn(flexTable);
		drawTimeLine(flexTable, newStartDate, newFinalDate, absolutePanel);
		insertClubPrefs(flexTable, 2);
		flexTable.removeCell(0, flexTable.getCellCount(0) - 1);
		flexTable.removeCell(0, flexTable.getCellCount(0) - 1);
	}

	private int getCountShiftsOnClub(Club club) {
		return this.shiftsOnClub.get(club);
	}

	private int getCountPeopleOnClubShifts(Club club) {
		return this.countPeopleOnClubShift.get(club);
	}

	private void setCountShiftsParametres(Schedule schedule) {
//		try {
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
				Integer countShiftsonClub = daySchedule.getShiftsNumber();
				this.shiftsOnClub.put(club, countShiftsonClub);

				Integer countPeopleOnClubShift = daySchedule.getShifts().get(0)
						.getQuantityOfEmployees();
				this.countPeopleOnClubShift.put(club, countPeopleOnClubShift);
				this.clubs.add(club);
			}

//		} catch (Exception ex) {
//			SC.say("Проблемы с сервером, пожалуйста обратитесь к системному администратору \n Код ошибки 9");
//		}
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
//		SC.say("Проблемы с сервером, пожалуйста обратитесь к системному администратору \n Код ошибки 10");
		return null;
	}

	private Date getDateByColumn(FlexTable flexTable, int column) {
		Map<java.sql.Date, List<ClubDaySchedule>> notRight = schedule
				.getDayScheduleMap();
		Set<java.sql.Date> set = notRight.keySet();
		Iterator<java.sql.Date> iterator = set.iterator();
		while (iterator.hasNext()) {
			Date date = iterator.next();
			for (int i = 1; i < flexTable.getCellCount(0); i++) {
				VerticalPanel vp = (VerticalPanel) flexTable.getWidget(0,
						column);
				try {
					InlineLabel label = (InlineLabel) vp.getWidget(0);
					if (i == column && tableDateFormat.format(date).equals(
					// flexTable.getText(0, column)) {
							label.getText())) {
						return date;
					}
				} catch (Exception ex) {
					SC.say(ex.getMessage());
				}
			}
		}
		SC.say("Проблемы с сервером, пожалуйста обратитесь к системному администратору \n Код ошибки 11");
		return null;
	}

	private void sendMessageToServer(int column, int row,
			final boolean isAdded, FlexTable flexTable, int TheNumberOfShift) {
		AssignmentInfo inform = new AssignmentInfo();
		inform.setAdded(isAdded);
		inform.setClub(getClubByRow(row));
		inform.setDate(getDateByColumn(flexTable, column));
		inform.setPeriodId(this.period.getPeriodId());
		inform.setRowNumber(TheNumberOfShift);
		scheduleDraftServiceAsync.updateShift(inform, this.employee,
				new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.say("Проблемы с сервером, пожалуйста обратитесь к системному администратору \n Код ошибки 12");
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(Boolean result) {
						if (!result) {
							SC.say("Простите, но данное место уже занято"
									+ " пожалуйста нажмите кнопку обновить");
						} else {
							if (!isAdded) {
								SC.say("Фамилия успешно удалена");
								// RootPanel rootPanel = RootPanel
								// .get("nameFieldContainer");
								// rootPanel.clear();
							} else {
								SC.say("Фамилия успешно добавлена");
							}
						}
					}
				});
	}

	private void setCheckBoxes(final CheckBox checkbox, final int col,
			final int rownumber, final FlexTable innerFlexTable,
			final FlexTable reserveFlexTable, final int row) {

		checkbox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String surnames = "";
				setCounts(0);
				if (checkbox.getValue() == false) {
					sendMessageToServer(col, rownumber, false,
							reserveFlexTable, row);
					surnames = innerFlexTable.getText(row, 0);
					surnames = surnames.replace(employee.getLastName(), "");
					innerFlexTable.setText(row, 0, surnames);
					makeOthersDisabled(reserveFlexTable, col, rownumber, true);
				} else {
					sendMessageToServer(col, rownumber, true, reserveFlexTable,
							row);
					surnames = innerFlexTable.getText(row, 0);
					surnames = surnames + " " + employee.getLastName();
					innerFlexTable.setText(row, 0, surnames);
					setCounts(getCounts() + 1);
					makeOthersDisabled(reserveFlexTable, col, rownumber, false);
				}
			}
		});
	}
}
