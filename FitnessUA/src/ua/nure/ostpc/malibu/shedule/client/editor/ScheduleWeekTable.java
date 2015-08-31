package ua.nure.ostpc.malibu.shedule.client.editor;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubDaySchedule;
import ua.nure.ostpc.malibu.shedule.shared.DateUtil;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
			int daysInTable, List<Club> clubs,
			LinkedHashMap<String, String> employeeMap,
			Map<Date, List<ClubDaySchedule>> dayScheduleMap) {
		Date startDate = new Date(currentDate.getTime());
		Date endDate = new Date(currentDate.getTime());
		CalendarUtil.addDaysToDate(endDate, daysInTable - 1);
		ScheduleWeekTable scheduleTable = new ScheduleWeekTable(startDate,
				endDate);
		scheduleTable.setStyleName("mainTable");
		scheduleTable.setWidth("1040px");
		scheduleTable.setBorderWidth(1);
		int weekOfYear = getWeekOfYear(startDate);
		scheduleTable.drawTimeLine(weekOfYear);
		scheduleTable.drawClubColumn(clubs);
		scheduleTable.drawWorkSpace(clubs.size());
		scheduleTable.drawShifts(clubs.size(), employeeMap, dayScheduleMap);
		return scheduleTable;
	}

	private void drawTimeLine(int weekOfYear) {
		getColumnFormatter().setStyleName(0, "clubColumn");
		insertRow(0);
		insertCell(0, 0);
		insertRow(1);
		insertCell(1, 0);
		setText(0, 0, "Клубы");
		setText(1, 0, "Неделя: " + weekOfYear);
		getFlexCellFormatter().addStyleName(0, 0, "mainHeader");
		getFlexCellFormatter().addStyleName(1, 0, "secondHeader");
		Date currentDate = new Date(getFirstDateOfWeek(startDate).getTime());
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

	private static Date getFirstDateOfWeek(Date date) {
		Date firstDateOfWeek = new Date(date.getTime());
		int dayOfWeek = Integer.parseInt(dayOfWeekFormat
				.format(firstDateOfWeek));
		if (dayOfWeek != 1) {
			CalendarUtil.addDaysToDate(firstDateOfWeek, -((dayOfWeek + 6) % 7));
		}
		return firstDateOfWeek;
	}

	@SuppressWarnings("deprecation")
	public static int getWeekOfYear(Date date) {
		Date yearStart = new Date(date.getYear(), 0, 0);
		yearStart = getFirstDateOfWeek(yearStart);
		yearStart = DateUtil.addDays(yearStart, -1);
		int weekNumber = (int) ((date.getTime() - yearStart.getTime()) / (7 * 24 * 60 * 60 * 1000));
		weekNumber++;
		return weekNumber;
	}

	private void drawClubColumn(List<Club> clubs) {
		int rowNumber = 2;
		rowClubMap = new LinkedHashMap<Integer, Long>();
		if (clubs != null)
			for (Club club : clubs) {
				rowClubMap.put(rowNumber, club.getClubId());
				insertRow(rowNumber);
				insertCell(rowNumber, 0);

				VerticalPanel clubPanel = new VerticalPanel();
				clubPanel.setStyleName("draftVPanel");
				clubPanel
						.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				clubPanel
						.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

				Label clubLabel = new Label(club.getTitle());
				clubLabel.setStyleName("clubLabel");
				clubPanel.add(clubLabel);

				DynamicForm employeesInClubForm = new DynamicForm();
				employeesInClubForm.setStyleName("selectItem");
				ClubPrefSelectItem clubPrefSelectItem = new ClubPrefSelectItem(
						club.getClubId());
				ClubPrefSelectItem.addClubPrefSelectItem(clubPrefSelectItem);
				employeesInClubForm.setItems(clubPrefSelectItem);
				clubPanel.add(employeesInClubForm);

				HorizontalPanel clubEmpPanel = new HorizontalPanel();
				clubEmpPanel.setStyleName("draftVPanel");
				clubEmpPanel
						.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				clubEmpPanel
						.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

				Label clubEmpLabel = new Label("Человек на смене");
				clubEmpLabel.setStyleName("smallLabel");
				clubEmpPanel.add(clubEmpLabel);

				EmpOnShiftListBox empOnShiftListBox = new EmpOnShiftListBox(
						club.getClubId());
				EmpOnShiftListBox.addEmpOnShiftListBox(empOnShiftListBox);

				clubEmpPanel.add(empOnShiftListBox);
				clubPanel.add(clubEmpPanel);

				setWidget(rowNumber, 0, clubPanel);
				getCellFormatter().setStyleName(rowNumber, 0, "mainHeader");
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
			LinkedHashMap<String, String> employeeMap,
			Map<Date, List<ClubDaySchedule>> dayScheduleMap) {
		int column = ((Integer.parseInt(dayOfWeekFormat.format(startDate)) + 6) % 7) + 1;
		int row = 2;
		int daysInTable = getDaysInTable();
		int endColumn = column + daysInTable;
		int endRow = row + clubsInTable;
		Date currentDate = new Date(startDate.getTime());
		for (int startColumn = column; startColumn < endColumn; startColumn++) {
			List<ClubDaySchedule> clubDayScheduleList = dayScheduleMap
					.get(currentDate);
			if (clubDayScheduleList != null && !clubDayScheduleList.isEmpty()) {
				for (int startRow = row; startRow < endRow; startRow++) {
					long clubId = rowClubMap.get(startRow);
					int numberOfShiftsInClubDay = 0;
					int workHoursInClubDay = 0;
					for (ClubDaySchedule clubDaySchedule : clubDayScheduleList) {
						if (clubDaySchedule.getClub().getClubId() == clubId) {
							numberOfShiftsInClubDay = clubDaySchedule
									.getShiftsNumber();
							workHoursInClubDay = clubDaySchedule
									.getWorkHoursInDay();
							break;
						}
					}
					ShiftItem.setNumberOfShifts(
							new Date(currentDate.getTime()), clubId,
							numberOfShiftsInClubDay);
					ShiftItem.setWorkHoursInDay(
							new Date(currentDate.getTime()), clubId,
							workHoursInClubDay);
					FlexTable shiftsTable = new FlexTable();
					for (int shiftNumber = 0; shiftNumber < numberOfShiftsInClubDay; shiftNumber++) {
						shiftsTable.insertRow(shiftNumber);
						shiftsTable.insertCell(shiftNumber, 0);
						shiftsTable.getCellFormatter().setStyleName(
								shiftNumber, 0, "shiftTableCell");
						int employeesOnShift = EmpOnShiftListBox
								.getEmployeesOnShift(clubId);
						ShiftItem shiftItem = new ShiftItem(currentDate,
								clubId, shiftNumber + 1, employeesOnShift,
								employeeMap);
						EmpOnShiftListBox.addShiftItem(shiftItem);
						shiftsTable.setWidget(shiftNumber, 0,
								shiftItem.getShiftLayout());
					}
					setWidget(startRow, startColumn, shiftsTable);
				}
			}
			CalendarUtil.addDaysToDate(currentDate, 1);
		}
	}
}
