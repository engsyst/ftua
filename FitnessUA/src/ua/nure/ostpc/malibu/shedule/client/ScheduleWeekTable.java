package ua.nure.ostpc.malibu.shedule.client;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Category;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Preference;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.smartgwt.client.types.MultiComboBoxLayoutStyle;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.MultiComboBoxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.layout.HLayout;

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
	private Map<Long, SelectItem> clubPrefSelectItems;
	private Map<Long, ListBox> empOnShiftListBoxes;
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
			int daysInTable, List<Club> dependentClubs,
			List<Employee> employees, Preference preference,
			List<Category> categories) {
		Date startDate = new Date(currentDate.getTime());
		Date endDate = new Date(currentDate.getTime());
		CalendarUtil.addDaysToDate(endDate, daysInTable - 1);
		ScheduleWeekTable scheduleTable = new ScheduleWeekTable(startDate,
				endDate);
		scheduleTable.setWidth("1040px");
		scheduleTable.setBorderWidth(1);
		scheduleTable.drawTimeLine();
		LinkedHashMap<String, String> categoryMap = new LinkedHashMap<String, String>();
		for (Category category : categories) {
			categoryMap.put(String.valueOf(category.getCategoryId()) + "c", "<"
					+ category.getTitle() + ">");
		}
		LinkedHashMap<String, String> employeeMap = new LinkedHashMap<String, String>();
		for (Employee employee : employees) {
			employeeMap.put(String.valueOf(employee.getEmployeeId()) + "e",
					employee.getNameForSchedule());
		}
		scheduleTable.drawClubColumn(dependentClubs, employeeMap, categoryMap);
		scheduleTable.drawWorkSpace(dependentClubs.size());
		scheduleTable.insertShifts(dependentClubs.size(), employeeMap,
				preference);
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
		Date currentDate = new Date(getFirstDateOfWeek().getTime());
		int headColumn = 1;
		while (headColumn <= 7) {
			insertCell(0, headColumn);
			insertCell(1, headColumn);
			setText(0, headColumn,
					dayOfWeekMap.get(dayOfWeekFormat.format(currentDate)));
			setText(1, headColumn, tableDateFormat.format(currentDate));
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

	private void drawClubColumn(List<Club> dependentClubs,
			LinkedHashMap<String, String> employeeMap,
			LinkedHashMap<String, String> categoryMap) {
		int rowNumber = 2;
		clubPrefSelectItems = new LinkedHashMap<Long, SelectItem>();
		empOnShiftListBoxes = new LinkedHashMap<Long, ListBox>();
		rowClubMap = new LinkedHashMap<Integer, Long>();
		for (Club club : dependentClubs) {
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

			SelectItem clubPrefSelectItem = new SelectItem();
			clubPrefSelectItem.setTextBoxStyle("item");
			clubPrefSelectItem.setMultiple(true);
			clubPrefSelectItem.setShowTitle(false);
			clubPrefSelectItem
					.setMultipleAppearance(MultipleAppearance.PICKLIST);

			LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
			valueMap.putAll(categoryMap);
			valueMap.putAll(employeeMap);

			clubPrefSelectItem.setValueMap(valueMap);
			employeesInClubForm.setItems(clubPrefSelectItem);
			clubPrefSelectItems.put(club.getClubId(), clubPrefSelectItem);
			clubPanel.add(employeesInClubForm, 0, 20);

			clubTotalPanel.add(clubPanel, 0, 0);

			AbsolutePanel clubEmpPanel = new AbsolutePanel();
			clubEmpPanel.setWidth("170px");
			clubEmpPanel.setHeight("30px");

			Label clubEmpLabel = new Label("Человек на смене");
			clubEmpLabel.setWidth("120px");
			clubEmpLabel.setStyleName("smallLabel");
			clubEmpPanel.add(clubEmpLabel, 0, 4);

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

			clubEmpPanel.add(quantityOfEmpOnShiftListBox, 120, 0);
			empOnShiftListBoxes.put(club.getClubId(),
					quantityOfEmpOnShiftListBox);
			clubTotalPanel.add(clubEmpPanel, 0, 50);

			setWidget(rowNumber, 0, clubTotalPanel);
			rowNumber++;
		}
	}

	private void drawWorkSpace(int clubsInTable) {
		for (int column = 1; column <= 7; column++) {
			for (int row = 2; row < clubsInTable + 2; row++) {
				insertCell(row, column);
				getCellFormatter().setStyleName(row, column, "dayCell");
			}
		}
	}

	private void insertShifts(int clubsInTable,
			LinkedHashMap<String, String> employeeMap, Preference preference) {
		int column = ((Integer.parseInt(dayOfWeekFormat.format(startDate)) + 6) % 7) + 1;
		int row = 2;
		int daysInTable = getDaysInTable();
		int endColumn = column + daysInTable;
		int endRow = row + clubsInTable;
		for (int startColumn = column; startColumn < endColumn; startColumn++) {
			getColumnFormatter().setStyleName(column, "scheduleColumn");
			for (int startRow = row; startRow < endRow; startRow++) {
				FlexTable shiftsTable = new FlexTable();
				for (int beforeRow = 0; beforeRow < preference
						.getShiftsNumber(); beforeRow++) {
					shiftsTable.insertRow(beforeRow);
					shiftsTable.insertCell(beforeRow, 0);
					shiftsTable.getCellFormatter().setStyleName(beforeRow, 0,
							"shiftTableCell");
					MultiComboBoxItem multiComboBoxItem = new MultiComboBoxItem();
					multiComboBoxItem.setValueMap(employeeMap);
					multiComboBoxItem
							.setLayoutStyle(MultiComboBoxLayoutStyle.VERTICAL);
					multiComboBoxItem.setShowTitle(false);
					multiComboBoxItem.setWidth(103);
					DynamicForm dynamicForm = new DynamicForm();
					dynamicForm.setItems(multiComboBoxItem);
					HLayout hLayout = new HLayout();
					hLayout.addChild(dynamicForm);
					long clubId = rowClubMap.get(startRow);
					ListBox listBox = empOnShiftListBoxes.get(clubId);
					int employeesOnShift = Integer.valueOf(listBox
							.getValue(listBox.getSelectedIndex()));
					hLayout.setHeight((employeesOnShift + 1) * 30);
					shiftsTable.setWidget(beforeRow, 0, hLayout);
				}
				setWidget(startRow, startColumn, shiftsTable);
			}
		}
	}
}
