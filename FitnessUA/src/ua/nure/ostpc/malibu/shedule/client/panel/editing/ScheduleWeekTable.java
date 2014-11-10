package ua.nure.ostpc.malibu.shedule.client.panel.editing;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Preference;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.smartgwt.client.widgets.form.DynamicForm;

/**
 * Custom <code>FlexTable</code> class realization.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class ScheduleWeekTable extends FlexTable {
	private static Map<String, String> dayOfWeekMap;
	private static DateTimeFormat tableDateFormat;
	private static DateTimeFormat dayOfWeekFormat;

	private Date startDate;
	private Date endDate;
	private Map<Integer, Long> rowClubMap;

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

	public ScheduleWeekTable(Date startDate, Date endDate) {
		super();
		this.startDate = new Date(startDate.getTime());
		this.endDate = new Date(endDate.getTime());
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public int getDaysInTable() {
		return CalendarUtil.getDaysBetween(startDate, endDate) + 1;
	}

	public static ScheduleWeekTable drawScheduleTable(Date currentDate,
			int daysInTable, List<Club> clubs, Preference preference,
			LinkedHashMap<String, String> employeeMap,
			LinkedHashMap<String, String> valueMap) {
		Date startDate = new Date(currentDate.getTime());
		Date endDate = new Date(currentDate.getTime());
		CalendarUtil.addDaysToDate(endDate, daysInTable - 1);
		ScheduleWeekTable scheduleTable = new ScheduleWeekTable(startDate,
				endDate);
		scheduleTable.setStyleName("mainTable");
		scheduleTable.setWidth("1040px");
		scheduleTable.setBorderWidth(1);
		scheduleTable.drawTimeLine();
		scheduleTable.drawClubColumn(clubs, valueMap);
		scheduleTable.drawWorkSpace(clubs.size());
		scheduleTable.drawShifts(clubs.size(), employeeMap, preference);
		return scheduleTable;
	}

	private void drawTimeLine() {
		getColumnFormatter().setStyleName(0, "clubColumn");
		insertRow(0);
		insertCell(0, 0);
		insertRow(1);
		insertCell(1, 0);
		setText(0, 0, "Дата");
		setText(1, 0, "День недели");
		getFlexCellFormatter().addStyleName(0, 0, "mainHeader");
		getFlexCellFormatter().addStyleName(1, 0, "secondHeader");
		Date currentDate = new Date(getFirstDateOfWeek().getTime());
		int headColumn = 1;
		while (headColumn <= 7) {
			insertCell(0, headColumn);
			insertCell(1, headColumn);
			setText(1, headColumn,
					dayOfWeekMap.get(dayOfWeekFormat.format(currentDate)));
			setText(0, headColumn, tableDateFormat.format(currentDate));
			getFlexCellFormatter().addStyleName(0, headColumn, "mainHeader");
			getFlexCellFormatter().addStyleName(1, headColumn, "secondHeader");
			headColumn++;
			CalendarUtil.addDaysToDate(currentDate, 1);
		}
	}

	private Date getFirstDateOfWeek() {
		Date firstDateOfWeek = new Date(startDate.getTime());
		int dayOfWeek = Integer.parseInt(dayOfWeekFormat
				.format(firstDateOfWeek));
		if (dayOfWeek != 1) {
			CalendarUtil.addDaysToDate(firstDateOfWeek, -((dayOfWeek + 6) % 7));
		}
		return firstDateOfWeek;
	}

	private void drawClubColumn(List<Club> clubs,
			LinkedHashMap<String, String> valueMap) {
		int rowNumber = 2;
		rowClubMap = new LinkedHashMap<Integer, Long>();
		for (Club club : clubs) {
			rowClubMap.put(rowNumber, club.getClubId());
			insertRow(rowNumber);
			insertCell(rowNumber, 0);

			AbsolutePanel clubTotalPanel = new AbsolutePanel();
			clubTotalPanel.setWidth("175px");
			clubTotalPanel.setHeight("80px");

			AbsolutePanel clubPanel = new AbsolutePanel();
			clubPanel.setWidth("170px");
			clubPanel.setHeight("50px");

			Label clubLabel = new Label(club.getTitle());
			clubLabel.setWidth("170px");
			clubPanel.add(clubLabel, 0, 0);

			DynamicForm employeesInClubForm = new DynamicForm();
			employeesInClubForm.setStyleName("selectItem");
			ClubPrefSelectItem clubPrefSelectItem = new ClubPrefSelectItem(
					club.getClubId(), valueMap);
			ClubPrefSelectItem.addClubPrefSelectItem(clubPrefSelectItem);
			employeesInClubForm.setItems(clubPrefSelectItem);
			clubPanel.add(employeesInClubForm, 0, 20);

			clubTotalPanel.add(clubPanel, 0, 0);

			AbsolutePanel clubEmpPanel = new AbsolutePanel();
			clubEmpPanel.setWidth("170px");
			clubEmpPanel.setHeight("30px");

			Label clubEmpLabel = new Label("Человек на смене");
			clubEmpLabel.setWidth("120px");
			clubEmpLabel.setStyleName("smallLabel");
			clubEmpPanel.add(clubEmpLabel, 0, 4);

			EmpOnShiftListBox empOnShiftListBox = new EmpOnShiftListBox(
					club.getClubId());
			EmpOnShiftListBox.addEmpOnShiftListBox(empOnShiftListBox);

			clubEmpPanel.add(empOnShiftListBox, 120, 0);
			clubTotalPanel.add(clubEmpPanel, 0, 50);

			setWidget(rowNumber, 0, clubTotalPanel);
			rowNumber++;
		}
	}

	private void drawWorkSpace(int clubsInTable) {
		for (int column = 1; column <= 7; column++) {
			getColumnFormatter().setStyleName(column, "scheduleColumn");
			for (int row = 2; row < clubsInTable + 2; row++) {
				insertCell(row, column);
				getCellFormatter().setStyleName(row, column, "dayCell");
			}
		}
	}

	private void drawShifts(int clubsInTable,
			LinkedHashMap<String, String> employeeMap, Preference preference) {
		int column = ((Integer.parseInt(dayOfWeekFormat.format(startDate)) + 6) % 7) + 1;
		int row = 2;
		int daysInTable = getDaysInTable();
		int endColumn = column + daysInTable;
		int endRow = row + clubsInTable;
		Date currentDate = new Date(startDate.getTime());
		ShiftItem.setEmployeeMap(employeeMap);
		for (int startColumn = column; startColumn < endColumn; startColumn++) {
			for (int startRow = row; startRow < endRow; startRow++) {
				FlexTable shiftsTable = new FlexTable();
				for (int shiftNumber = 0; shiftNumber < preference
						.getShiftsNumber(); shiftNumber++) {
					shiftsTable.insertRow(shiftNumber);
					shiftsTable.insertCell(shiftNumber, 0);
					shiftsTable.getCellFormatter().setStyleName(shiftNumber, 0,
							"shiftTableCell");
					long clubId = rowClubMap.get(startRow);
					int employeesOnShift = EmpOnShiftListBox
							.getEmployeesOnShift(clubId);
					ShiftItem shiftItem = new ShiftItem(currentDate, clubId,
							shiftNumber + 1, employeesOnShift, employeeMap);
					EmpOnShiftListBox.addShiftItem(shiftItem);
					shiftsTable.setWidget(shiftNumber, 0,
							shiftItem.getShiftLayout());
				}
				setWidget(startRow, startColumn, shiftsTable);
			}
			CalendarUtil.addDaysToDate(currentDate, 1);
		}
	}
}
