package ua.nure.ostpc.malibu.shedule.client;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;

/**
 * Custom <code>FlexTable</code> class realization.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class ScheduleTable extends FlexTable {
	private static Map<String, String> dayOfWeekMap;
	private static DateTimeFormat tableDateFormat;
	private static DateTimeFormat dayOfWeekFormat;

	static {
		dayOfWeekMap = new HashMap<String, String>();
		dayOfWeekMap.put("0", "Sunday");
		dayOfWeekMap.put("1", "Monday");
		dayOfWeekMap.put("2", "Tuesday");
		dayOfWeekMap.put("3", "Wednesday");
		dayOfWeekMap.put("4", "Thursday");
		dayOfWeekMap.put("5", "Friday");
		dayOfWeekMap.put("6", "Saturday");
		tableDateFormat = DateTimeFormat.getFormat("dd.MM.yyyy");
		dayOfWeekFormat = DateTimeFormat.getFormat("c");
	}

	public static ScheduleTable drawScheduleTable(Date currentDate,
			int daysInTable, List<Club> dependentClubs,
			Map<Long, List<Employee>> employeesByClubs) {
		Date startDate = new Date(currentDate.getTime());
		Date endDate = new Date(currentDate.getTime());
		CalendarUtil.addDaysToDate(currentDate, daysInTable);
		CalendarUtil.addDaysToDate(endDate, daysInTable - 1);
		ScheduleTable table = new ScheduleTable();
		table.setWidth("1040px");
		table.setBorderWidth(1);

		table.getColumnFormatter().setStyleName(0, "clubColumn");
		table.insertRow(0);
		table.insertCell(0, 0);
		table.setText(0, 0, "Day of week");
		table.insertRow(1);
		table.insertCell(1, 0);
		table.setText(1, 0, "Date");

		int rowNumber = 2;
		for (Club club : dependentClubs) {
			table.insertRow(rowNumber);
			table.insertCell(rowNumber, 0);

			AbsolutePanel clubTotalPanel = new AbsolutePanel();
			clubTotalPanel.setWidth("350px");
			clubTotalPanel.setHeight("70px");

			AbsolutePanel clubPanel = new AbsolutePanel();
			clubPanel.setStyleName("borderPanel");
			clubPanel.setWidth("190px");
			clubPanel.setHeight("70px");

			Label clubLabel = new Label(club.getTitle());
			clubLabel.setWidth("170px");
			clubPanel.add(clubLabel, 10, 2);

			DynamicForm employeesInClubForm = new DynamicForm();
			employeesInClubForm.setStyleName("selectItem");

			SelectItem clubPrefsSelectItem = new SelectItem();
			clubPrefsSelectItem.setTextBoxStyle("item");
			clubPrefsSelectItem.setMultiple(true);
			clubPrefsSelectItem.setTitle("");
			clubPrefsSelectItem
					.setMultipleAppearance(MultipleAppearance.PICKLIST);

			List<Employee> employees = employeesByClubs.get(club.getClubId());
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			for (Employee employee : employees) {
				map.put(String.valueOf(employee.getEmployeeId()),
						employee.getNameForSchedule());
			}
			clubPrefsSelectItem.setValueMap(map);
			employeesInClubForm.setItems(clubPrefsSelectItem);
			clubPanel.add(employeesInClubForm, 0, 30);

			clubTotalPanel.add(clubPanel, 0, 0);

			AbsolutePanel clubShiftPanel = new AbsolutePanel();
			clubShiftPanel.setStyleName("borderPanel");
			clubShiftPanel.setWidth("80px");
			clubShiftPanel.setHeight("70px");

			Label shiftsNumberLabel = new Label("Number of shifts");
			shiftsNumberLabel.setWidth("70px");
			shiftsNumberLabel.setStyleName("smallLabel");
			clubShiftPanel.add(shiftsNumberLabel, 5, 2);

			ListBox shiftsNumberListBox = new ListBox(false);
			for (int i = 1; i <= 24; i++) {
				shiftsNumberListBox.addItem(String.valueOf(i));
			}
			shiftsNumberListBox.setSelectedIndex(0);
			clubShiftPanel.add(shiftsNumberListBox, 16, 30);
			clubTotalPanel.add(clubShiftPanel, 190, 0);

			AbsolutePanel clubEmpPanel = new AbsolutePanel();
			clubEmpPanel.setStyleName("borderPanel");
			clubEmpPanel.setWidth("80px");
			clubEmpPanel.setHeight("70px");

			Label clubEmpLabel = new Label("Employees on shift");
			clubEmpLabel.setWidth("70px");
			clubEmpLabel.setStyleName("smallLabel");
			clubEmpPanel.add(clubEmpLabel, 5, 2);

			ListBox clubEmpListBox = new ListBox(false);
			for (int i = 1; i <= 20; i++) {
				clubEmpListBox.addItem(String.valueOf(i));
			}
			clubEmpListBox.setSelectedIndex(0);
			clubEmpPanel.add(clubEmpListBox, 16, 30);
			clubTotalPanel.add(clubEmpPanel, 270, 0);

			table.setWidget(rowNumber, 0, clubTotalPanel);
			rowNumber++;
		}

		int headColunm = 1;

		while (startDate.getTime() <= endDate.getTime()) {
			table.insertCell(0, headColunm);
			table.insertCell(1, headColunm);
			table.setText(0, headColunm,
					dayOfWeekMap.get(dayOfWeekFormat.format(startDate)));
			table.setText(1, headColunm, tableDateFormat.format(startDate));
			headColunm++;
			CalendarUtil.addDaysToDate(startDate, 1);
		}
		return table;
	}
}
