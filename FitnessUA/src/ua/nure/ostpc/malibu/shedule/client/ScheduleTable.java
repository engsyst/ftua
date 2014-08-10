package ua.nure.ostpc.malibu.shedule.client;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
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

	private Date startDate;
	private Date endDate;
	private Map<Long, SelectItem> clubPrefSelectItems;
	private Map<Long, ListBox> empOnShiftListBoxes;

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

	public ScheduleTable(Date startDate, Date endDate) {
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

	public static ScheduleTable drawScheduleTable(Date currentDate,
			int daysInTable, List<Club> dependentClubs, List<Employee> employees) {
		Date startDate = new Date(currentDate.getTime());
		Date endDate = new Date(currentDate.getTime());
		CalendarUtil.addDaysToDate(endDate, daysInTable - 1);
		ScheduleTable scheduleTable = new ScheduleTable(startDate, endDate);
		scheduleTable.setWidth("1040px");
		scheduleTable.setBorderWidth(1);
		scheduleTable.drawTimeLine();
		scheduleTable.drawClubColumn(dependentClubs, employees);
		scheduleTable.drawWorkSpace(dependentClubs);
		return scheduleTable;
	}

	private void drawTimeLine() {
		getColumnFormatter().setStyleName(0, "clubColumn");
		insertRow(0);
		insertCell(0, 0);
		setText(0, 0, "День недели");
		insertRow(1);
		insertCell(1, 0);
		setText(1, 0, "Дата");
		Date currentDate = new Date(startDate.getTime());
		int headColunm = 1;
		while (currentDate.getTime() <= endDate.getTime()) {
			insertCell(0, headColunm);
			insertCell(1, headColunm);
			setText(0, headColunm,
					dayOfWeekMap.get(dayOfWeekFormat.format(currentDate)));
			setText(1, headColunm, tableDateFormat.format(currentDate));
			headColunm++;
			CalendarUtil.addDaysToDate(currentDate, 1);
		}
	}

	private void drawClubColumn(List<Club> dependentClubs,
			List<Employee> employees) {
		int rowNumber = 2;
		clubPrefSelectItems = new LinkedHashMap<Long, SelectItem>();
		empOnShiftListBoxes = new LinkedHashMap<Long, ListBox>();
		for (Club club : dependentClubs) {
			insertRow(rowNumber);
			insertCell(rowNumber, 0);

			AbsolutePanel clubTotalPanel = new AbsolutePanel();
			clubTotalPanel.setWidth("295px");
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

			SelectItem clubPrefSelectItem = new SelectItem();
			clubPrefSelectItem.setTextBoxStyle("item");
			clubPrefSelectItem.setMultiple(true);
			clubPrefSelectItem.setTitle("");
			clubPrefSelectItem
					.setMultipleAppearance(MultipleAppearance.PICKLIST);

			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			for (Employee employee : employees) {
				map.put(String.valueOf(employee.getEmployeeId()),
						employee.getNameForSchedule());
			}
			clubPrefSelectItem.setValueMap(map);
			employeesInClubForm.setItems(clubPrefSelectItem);
			clubPrefSelectItems.put(club.getClubId(), clubPrefSelectItem);
			clubPanel.add(employeesInClubForm, 0, 30);

			clubTotalPanel.add(clubPanel, 0, 0);

			AbsolutePanel clubEmpPanel = new AbsolutePanel();
			clubEmpPanel.setStyleName("borderPanel");
			clubEmpPanel.setWidth("105px");
			clubEmpPanel.setHeight("70px");

			Label clubEmpLabel = new Label("Кол-во человек на смене");
			clubEmpLabel.setWidth("93px");
			clubEmpLabel.setStyleName("smallLabel");
			clubEmpPanel.add(clubEmpLabel, 5, 2);

			ListBox quantityOfEmpOnShiftListBox = new ListBox(false);
			for (int i = 1; i <= 20; i++) {
				quantityOfEmpOnShiftListBox.addItem(String.valueOf(i));
			}
			quantityOfEmpOnShiftListBox.setSelectedIndex(0);
			quantityOfEmpOnShiftListBox.getElement().setId(
					String.valueOf(club.getClubId()));
			quantityOfEmpOnShiftListBox.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					ListBox listBox = (ListBox) event.getSource();
					Window.alert(listBox.getElement().getId());
				}
			});

			clubEmpPanel.add(quantityOfEmpOnShiftListBox, 29, 30);
			empOnShiftListBoxes.put(club.getClubId(),
					quantityOfEmpOnShiftListBox);
			clubTotalPanel.add(clubEmpPanel, 190, 0);

			setWidget(rowNumber, 0, clubTotalPanel);
			rowNumber++;
		}
	}

	private void drawWorkSpace(List<Club> dependentClubs) {
		int column = 1;
		int row = 2;
		int clubsInTable = dependentClubs.size();
		int daysInTable = getDaysInTable();
		int endColumn = column + daysInTable;
		int endRow = row + clubsInTable;
		for (int startColumn = column; startColumn < endColumn; startColumn++) {
			for (int startRow = row; startRow < endRow; startRow++) {
				insertCell(startRow, startColumn);
			}
		}
	}
}
