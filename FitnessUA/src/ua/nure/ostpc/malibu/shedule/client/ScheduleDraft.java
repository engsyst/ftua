package ua.nure.ostpc.malibu.shedule.client;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.Path;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubDaySchedule;
import ua.nure.ostpc.malibu.shedule.entity.ClubPref;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Shift;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ScheduleDraft implements EntryPoint {
	private final ScheduleDraftServiceAsync scheduleDraftServiceAsync = GWT
			.create(ScheduleDraftService.class);
	private Employee employee = new Employee();
	private Schedule schedule = new Schedule();
	private Set<Club> clubs = new HashSet<Club>();
	private Period period = new Period();
	private String[] surnames;
	private int counts;
	private Map<Club, List<Employee>> empToClub;
	private Map<Club, Integer> ShiftsOnClub = new HashMap<Club, Integer>();
	private Map<Club, Integer> countPeopleOnClubShift = new HashMap<Club, Integer>();
	private static DateTimeFormat tableDateFormat = DateTimeFormat
			.getFormat("dd.MM.yyyy");

	/**
	 * @wbp.parser.entryPoint
	 */
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
		return ShiftsOnClub;
	}

	public void setShiftsOnClub(Map<Club, Integer> shiftsOnClub) {
		ShiftsOnClub = shiftsOnClub;
	}

	public Map<Club, Integer> getCountPeopleOnClubShift() {
		return countPeopleOnClubShift;
	}

	public void setCountPeopleOnClubShift(
			Map<Club, Integer> countPeopleOnClubShift) {
		this.countPeopleOnClubShift = countPeopleOnClubShift;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get("nameFieldContainer");
		rootPanel.setStyleName("MainPanel");
		long periodId = 0;
		try {
			periodId = Long.parseLong(Window.Location
					.getParameter(AppConstants.PERIOD_ID));
		} catch (NumberFormatException | NullPointerException e) {
			Window.alert("");
			Window.Location.replace(Path.COMMAND__SCHEDULE_MANAGER);
		}
		this.period.setPeriod_Id(periodId);

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
							Window.alert("Something wrong with clubPrefs");
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
							Window.alert("Указанного расписания не существует или оно имеет статус черновика");
							Window.Location
									.replace(Path.COMMAND__SCHEDULE_MANAGER);
						}
						Window.alert(schedule.toString());
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
					Window.alert("Cannot get data from server!");
					cancel();
				}
			}
		};
		timer.scheduleRepeating(300);
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private void drawPage() {

		SetCountShiftsParametres(this.schedule);

		String[] surnames = {};
		this.setSurnames(surnames);
		final InlineLabel Greetings = new InlineLabel();
		Greetings.setText("Добро пожаловать в черновик," + " "
				+ employee.getLastName());
		RootPanel rootPanel = RootPanel.get("nameFieldContainer");
		rootPanel.setStyleName("MainPanel");

		AbsolutePanel ExtraBlock = new AbsolutePanel();
		ExtraBlock.setStyleName("extrablock");
		rootPanel.add(ExtraBlock, 26, 24);
		ExtraBlock.setSize("441px", "107px");

		ExtraBlock.add(Greetings, 173, 10);
		Greetings.setSize("208px", "18px");

		AbsolutePanel absolutePanel = new AbsolutePanel();
		absolutePanel.setStyleName("TableBlock");
		rootPanel.add(absolutePanel);

		final FlexTable flexTable = new FlexTable();
		flexTable.setStyleName("MainTable");
		absolutePanel.add(flexTable, 10, 10);
		flexTable.setSize("100px", "100px");

		flexTable.insertRow(0);
		flexTable.setText(0, 0, " ");
		flexTable.insertCell(0, 1);
		flexTable.setText(0, 1, "Число рабочих на смене");
		DrawClubColumn(flexTable);
		DrawTimeLine(flexTable, schedule.getPeriod().getStartDate(), schedule
				.getPeriod().getEndDate(), absolutePanel);
		// SetContent(flexTable, 3);
		insertClubPrefs(flexTable, 2);
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);
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
			String values = "";
			for (String value : getSurnames()) {
				values = values + value + " ";
			}
			innerFlexTable.setText(i, 0, values);
			innerFlexTable.insertCell(i, 1);
			final CheckBox checkbox = new CheckBox();
			if (innerFlexTable.getText(row, 0).split(" ").length > GetCountPeopleOnClubShifts(getClubByRow(rownumber))
					&& innerFlexTable.getText(row, 0).contains(
							employee.getLastName()) == false) {
				checkbox.setEnabled(false);
			} else if (innerFlexTable.getText(row, 0).contains(
					employee.getLastName()) == true) {
				checkbox.setValue(true);
				checkbox.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						String surnames = "";
						setCounts(0);
						if (checkbox.getValue() == false) {
							surnames = innerFlexTable.getText(row, 0);
							surnames = surnames.replace(employee.getLastName(),
									"");
							innerFlexTable.setText(row, 0, surnames);
							MakeOthersDisabled(reserveFlexTable, col,
									rownumber, true);
						} else {
							surnames = innerFlexTable.getText(row, 0);
							surnames = surnames + " " + employee.getLastName();
							innerFlexTable.setText(row, 0, surnames);
							setCounts(getCounts() + 1);
							MakeOthersDisabled(reserveFlexTable, col,
									rownumber, false);
						}
					}
				});
			} else {
				checkbox.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						String surnames = "";
						if (checkbox.getValue() == false) {
							surnames = innerFlexTable.getText(row, 0);
							surnames = surnames.replace(employee.getLastName(),
									"");
							innerFlexTable.setText(row, 0, surnames);
							MakeOthersDisabled(reserveFlexTable, col,
									rownumber, true);

						} else {
							surnames = innerFlexTable.getText(row, 0);
							surnames = surnames + " " + employee.getLastName();
							innerFlexTable.setText(row, 0, surnames);
							MakeOthersDisabled(reserveFlexTable, col,
									rownumber, false);
						}
					}
				});
			}
			innerFlexTable.setWidget(i, 1, checkbox);
		}
		flexTable = reserveFlexTable;
		return innerFlexTable;
	}

	private void MakeOthersDisabled(FlexTable flexTable, int column,
			int rowNumber, boolean isEnabled) {
		setCounts(0);
		for (int row = 1; row < flexTable.getRowCount(); row++) {
			if (row == rowNumber) {
				continue;
			} else {
				FlexTable innerFlexTable = (FlexTable) flexTable.getWidget(row,
						column);
				for (int i = 0; i < GetCountShiftsOnClub(getClubByRow(rowNumber)); i++) {
					if (isEnabled == false) {
						CheckBox checkbox = (CheckBox) innerFlexTable
								.getWidget(i, 1);
						checkbox.setEnabled(false);
						innerFlexTable.setWidget(i, 1, checkbox);
					} else {
						FlexTable innerCopyFlexTable = (FlexTable) flexTable
								.getWidget(rowNumber, column);
						int count = 0;
						for (int j = 0; j < GetCountShiftsOnClub(getClubByRow(rowNumber)); j++) {
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
							checkbox.setEnabled(true);
							innerFlexTable.setWidget(i, 1, checkbox);
						}
					}
				}
				flexTable.setWidget(row, column, innerFlexTable);
			}
		}
	}

	private void DrawTimeLine(FlexTable flexTable, Date startdate,
			Date enddate, AbsolutePanel absolutePanel) {
		Date startDate = startdate;
		Date endDate = enddate;
		Date currentDate = new Date(startDate.getTime());
		int count = 3;
		flexTable.insertCell(0, 2);
		flexTable.setText(0, 2, "Предпочтительные личности");
		while (currentDate.getTime() <= endDate.getTime()) {
			for (int i = 0; i <= getClubs().size(); i++) {
				flexTable.insertCell(i, count);
			}
			flexTable.setText(0, count, tableDateFormat.format(currentDate));
			count++;
			if (count == 10) {
				SetContent(flexTable, 3);
				makeNewTable(absolutePanel, currentDate, endDate);
				return;
			} else {
				CalendarUtil.addDaysToDate(currentDate, 1);
			}
			if (currentDate.getTime() > endDate.getTime()) {
				SetNotFullContent(flexTable, 3, count);
			}
		}
	}

	private void DrawClubColumn(FlexTable flexTable) {
		Iterator<Club> iter = clubs.iterator();
		for (int i = 0; i < this.getClubs().size(); i++) {
			flexTable.insertRow(i + 1);
			flexTable.insertCell(i + 1, 1);
			flexTable.insertCell(i + 1, 2);
			flexTable.setText(i + 1, 0, iter.next().getTitle());
			try {
				flexTable.setText(i + 1, 1, Integer
						.toString(GetCountPeopleOnClubShifts(iter.next())));
			} catch (Exception ex) {
				Window.alert(ex.getMessage());
			}
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
			flexTable.setWidget(i + 1, column, comboBox);
		}
	}

	private void SetContent(FlexTable flexTable, int column) {
		for (int i = column; i <= column + 6; i++) {
			for (int j = 1; j < flexTable.getRowCount(); j++) {
				flexTable.setWidget(
						j,
						i,
						InsertInTable(flexTable,
								GetCountShiftsOnClub(getClubByRow(j)), i, j));
			}
		}
	}

	private void SetNotFullContent(FlexTable flexTable, int column, int count) {
		for (int i = column; i < count; i++) {
			for (int j = 1; j < flexTable.getRowCount(); j++) {
				flexTable.setWidget(
						j,
						i,
						InsertInTable(flexTable,
								GetCountShiftsOnClub(getClubByRow(j)), i, j));
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
		FlexTable flexTable = new FlexTable();
		flexTable.setStyleName("MainTable");
		absolutePanel.add(flexTable, 10, 10);
		flexTable.setSize("100px", "100px");

		flexTable.insertRow(0);
		flexTable.setText(0, 0, " ");
		flexTable.insertCell(0, 1);
		flexTable.setText(0, 1, "Число рабочих на смене");

		DrawClubColumn(flexTable);
		DrawTimeLine(flexTable, newStartDate, newFinalDate, absolutePanel);
		insertClubPrefs(flexTable, 2);
	}

	private int GetCountShiftsOnClub(Club club) {
		return this.ShiftsOnClub.get(club);
	}

	private int GetCountPeopleOnClubShifts(Club club) {
		return this.countPeopleOnClubShift.get(club);
	}

	private void SetCountShiftsParametres(Schedule schedule) {
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
				this.ShiftsOnClub.put(club, countShiftsonClub);
				
				Integer countPeopleOnClubShift = daySchedule.getShifts().get(0)
						.getQuantityOfEmployees(); // undefined
				this.countPeopleOnClubShift.put(club, countPeopleOnClubShift);
				this.clubs.add(club);
			}

		} catch (Exception ex) {
			Window.alert("Cannot get data from Schedule (ShiftParams)"
					+ ex.getMessage());
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
			}
		}
		return null;
	}

}
