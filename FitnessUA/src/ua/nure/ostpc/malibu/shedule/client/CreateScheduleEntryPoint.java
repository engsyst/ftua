package ua.nure.ostpc.malibu.shedule.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.Path;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CreateScheduleEntryPoint implements EntryPoint {
	private final CreateScheduleServiceAsync createScheduleService = GWT
			.create(CreateScheduleService.class);
	private Date startDate;
	private Collection<Club> dependentClubs;
	private Map<Long, List<Employee>> employeesByClubs;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void onModuleLoad() {
		getStartDateFromServer();
		getClubsAndEmployeesFromServer();
		Timer timer = new Timer() {
			private int count;

			@Override
			public void run() {
				if (count < 15) {
					if (startDate != null && dependentClubs != null
							&& employeesByClubs != null) {
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
		timer.scheduleRepeating(100);
	}

	private void getStartDateFromServer() {
		createScheduleService.getStartDate(new AsyncCallback<Date>() {

			@Override
			public void onSuccess(Date result) {
				startDate = result;
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Cannot get start date from server!");
			}
		});
	}

	private void getClubsAndEmployeesFromServer() {
		createScheduleService
				.getDependentClubs(new AsyncCallback<List<Club>>() {

					@Override
					public void onSuccess(List<Club> result) {
						dependentClubs = result;
						getEmployeesFromServer();
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Cannot get dependent clubs from server!");
					}
				});
	}

	private void getEmployeesFromServer() {
		List<Long> clubsId = new ArrayList<Long>();
		for (Club club : dependentClubs) {
			clubsId.add(club.getClubId());
		}
		createScheduleService.getEmployeesByClubsId(clubsId,
				new AsyncCallback<Map<Long, List<Employee>>>() {

					@Override
					public void onSuccess(Map<Long, List<Employee>> result) {
						employeesByClubs = result;

					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Cannot get employees from server!");

					}
				});
	}

	private void drawPage() {
		final RootPanel rootPanel = RootPanel.get("scheduleContainer");
		rootPanel.setSize("100%", "100%");

		AbsolutePanel headerPanel = new AbsolutePanel();
		headerPanel.setStyleName("headerPanel");

		Image groupImage = new Image("img/group.png");
		groupImage.setSize("75px", "75px");
		headerPanel.add(groupImage, 10, 15);

		CaptionPanel schedulePlanningPanel = new CaptionPanel(
				"Schedule to period from/to");
		schedulePlanningPanel.setSize("550px", "60px");

		AbsolutePanel datePanel = new AbsolutePanel();
		datePanel.setSize("550px", "45px");

		Label startLabel = new Label("Start");
		startLabel.setWidth("50px");
		datePanel.add(startLabel, 10, 15);
		final DatePicker startDatePicker = new DatePicker();
		startDatePicker.setSize("205px", "191px;");
		final DateTimeFormat dateFormat = DateTimeFormat
				.getFormat("dd/MM/yyyy");
		final DateBox startDateBox = new DateBox(startDatePicker, new Date(),
				new DateBox.DefaultFormat(dateFormat));
		startDateBox.setSize("75px", "16px");
		datePanel.add(startDateBox, 70, 10);
		Image startCalendarIcon = new Image(
				"createSchedule/sc/skins/Enterprise/images/DynamicForm/date_control.png");
		startCalendarIcon.setSize("31px", "28px");
		datePanel.add(startCalendarIcon, 160, 10);

		startDatePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {

			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				startDateBox.getDatePicker().setVisible(true);
				Date date = event.getValue();
				startDateBox.setValue(date);
			}
		});

		startCalendarIcon.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				startDateBox.getTextBox().fireEvent(event);
			}
		});

		Label endLabel = new Label("End");
		endLabel.setWidth("50px");
		datePanel.add(endLabel, 220, 15);
		final DatePicker endDatePicker = new DatePicker();
		endDatePicker.setSize("205px", "191px;");
		final DateBox endDateBox = new DateBox(endDatePicker, new Date(),
				new DateBox.DefaultFormat(dateFormat));
		endDateBox.setSize("75px", "16px");
		datePanel.add(endDateBox, 280, 10);
		Image endCalendarIcon = new Image(
				"createSchedule/sc/skins/Enterprise/images/DynamicForm/date_control.png");
		endCalendarIcon.setSize("31px", "28px");
		datePanel.add(endCalendarIcon, 370, 10);

		endDatePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {

			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				endDateBox.getDatePicker().setVisible(true);
				Date date = event.getValue();
				endDateBox.setValue(date);
			}
		});

		endCalendarIcon.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				endDateBox.getTextBox().fireEvent(event);
			}
		});

		final Button applyButton = new Button("Apply");
		applyButton.setSize("95px", "30px");
		datePanel.add(applyButton, 435, 10);

		schedulePlanningPanel.add(datePanel);
		headerPanel.add(schedulePlanningPanel, 95, 10);

		AbsolutePanel controlPanel = new AbsolutePanel();
		controlPanel.setSize("325px", "45px");

		Button generateScheduleButton = new Button("Generate");
		generateScheduleButton.setSize("110px", "30px");
		controlPanel.add(generateScheduleButton, 10, 10);

		Button saveScheduleButton = new Button("Save");
		saveScheduleButton.setSize("90px", "30px");
		controlPanel.add(saveScheduleButton, 125, 10);

		Button resetScheduleButton = new Button("Reset");
		resetScheduleButton.setSize("90px", "30px");
		controlPanel.add(resetScheduleButton, 220, 10);

		headerPanel.add(controlPanel, 740, 30);

		final SubmitButton logoutButton = new SubmitButton("Log out");
		logoutButton.setSize("80px", "30px");

		final FormPanel logoutFormPanel = new FormPanel();
		logoutFormPanel.setStyleName("logoutPanel");
		logoutFormPanel.setSize("80px", "30px");
		logoutFormPanel.add(logoutButton);
		logoutFormPanel.setMethod(FormPanel.METHOD_POST);
		logoutFormPanel.setAction(Path.COMMAND__LOGOUT);

		logoutFormPanel.addSubmitHandler(new FormPanel.SubmitHandler() {

			@Override
			public void onSubmit(SubmitEvent event) {
				logoutButton.click();

			}
		});

		logoutFormPanel
				.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {

					@Override
					public void onSubmitComplete(SubmitCompleteEvent event) {
						Window.Location.replace(Path.COMMAND__LOGIN);

					}
				});

		headerPanel.add(logoutFormPanel);
		rootPanel.add(headerPanel, 0, 0);

		startDateBox.setValue(startDate);
		endDateBox.setValue(startDate);

		final AbsolutePanel schedulePanel = new AbsolutePanel();
		schedulePanel.setVisible(false);
		schedulePanel.setWidth("100%");

		rootPanel.add(schedulePanel, 0, 100);

		final DateTimeFormat tableDateFormat = DateTimeFormat
				.getFormat("dd.MM.yyyy");
		final DateTimeFormat dayOfWeekFormat = DateTimeFormat.getFormat("c");

		final Map<String, String> dayOfWeekMap = new HashMap<String, String>();
		dayOfWeekMap.put("0", "Sunday");
		dayOfWeekMap.put("1", "Monday");
		dayOfWeekMap.put("2", "Tuesday");
		dayOfWeekMap.put("3", "Wednesday");
		dayOfWeekMap.put("4", "Thursday");
		dayOfWeekMap.put("5", "Friday");
		dayOfWeekMap.put("6", "Saturday");

		applyButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				applyButton.setFocus(false);
				Date periodStartDate = startDateBox.getValue();
				Date periodEndDate = endDateBox.getValue();
				if (periodStartDate == null || periodEndDate == null
						|| periodStartDate.after(periodEndDate)) {
					Window.alert("Start period date or end period date is incorrect!");
					return;
				}
				if (periodStartDate.after(periodEndDate)) {
					Window.alert("Start period date more than end period date!");
					return;
				}
				if (periodStartDate.before(startDate)) {
					Window.alert("Start period date less than necessary start date ("
							+ dateFormat.format(startDate) + ")!");
					return;
				}
				schedulePanel.clear();
				schedulePanel.setVisible(true);
				drawSchedule(periodStartDate, periodEndDate);
			}

			private void drawSchedule(Date periodStartDate, Date periodEndDate) {
				int numberOfDays = CalendarUtil.getDaysBetween(periodStartDate,
						periodEndDate) + 1;

				int tablesHeight = 20;
				Date currentDate = new Date(periodStartDate.getTime());
				while (numberOfDays != 0) {
					int daysInTable = numberOfDays >= 7 ? 7 : numberOfDays;
					numberOfDays -= daysInTable;
					FlexTable table = drawTable(currentDate, daysInTable);
					schedulePanel.add(table, 10, tablesHeight);
					tablesHeight += table.getOffsetHeight();
					tablesHeight += 20;
				}
				schedulePanel.setHeight(tablesHeight + "px");
			}

			private FlexTable drawTable(Date currentDate, int daysInTable) {
				Date startDate = new Date(currentDate.getTime());
				Date endDate = new Date(currentDate.getTime());
				CalendarUtil.addDaysToDate(currentDate, daysInTable);
				CalendarUtil.addDaysToDate(endDate, daysInTable - 1);
				FlexTable table = new FlexTable();
				table.setWidth("1040px");
				table.setBorderWidth(1);

				table.getColumnFormatter().setStyleName(0, "clubColumn");
				table.insertRow(0);
				table.insertCell(0, 0);
				table.setText(0, 0, "Day of week");
				table.insertCell(0, 1);
				table.insertRow(1);
				table.insertCell(1, 0);
				table.setText(1, 0, "Date");
				table.insertCell(1, 1);

				int rowNumber = 2;
				for (Club club : dependentClubs) {
					table.insertRow(rowNumber);
					table.insertCell(rowNumber, 0);

					AbsolutePanel clubPanel = new AbsolutePanel();
					clubPanel.setWidth("200px");
					clubPanel.setHeight("70px");

					Label clubLabel = new Label(club.getTitle());
					clubLabel.setWidth("190px");

					clubPanel.add(clubLabel, 10, 0);

					DynamicForm employeesInClubForm = new DynamicForm();
					employeesInClubForm.setStyleName("selectItem");
					SelectItem selectItem = new SelectItem();
					selectItem.setTextBoxStyle("item");
					selectItem.setMultiple(true);
					selectItem.setTitle("");
					selectItem
							.setMultipleAppearance(MultipleAppearance.PICKLIST);

					List<Employee> employees = employeesByClubs.get(club
							.getClubId());
					List<String> employeesLastNames = new ArrayList<String>();
					for (Employee employee : employees) {
						employeesLastNames.add(employee.getLastName() + " "
								+ employee.getFirstName().charAt(0) + "."
								+ employee.getSecondName().charAt(0) + ".");
					}

					selectItem.setValueMap(employeesLastNames
							.toArray(new String[] {}));
					employeesInClubForm.setItems(selectItem);
					clubPanel.add(employeesInClubForm, 0, 30);
					table.setWidget(rowNumber, 0, clubPanel);

					rowNumber++;
				}

				int headColunm = 2;
				while (startDate.getTime() <= endDate.getTime()) {
					table.insertCell(0, headColunm);
					table.insertCell(1, headColunm);
					table.setText(0, headColunm,
							dayOfWeekMap.get(dayOfWeekFormat.format(startDate)));
					table.setText(1, headColunm,
							tableDateFormat.format(startDate));
					headColunm++;
					CalendarUtil.addDaysToDate(startDate, 1);
				}
				return table;
			}

		});

	}
}
