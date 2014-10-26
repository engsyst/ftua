package ua.nure.ostpc.malibu.shedule.client.panel.creation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.client.ScheduleManagerService;
import ua.nure.ostpc.malibu.shedule.client.ScheduleManagerServiceAsync;
import ua.nure.ostpc.malibu.shedule.entity.Category;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubDaySchedule;
import ua.nure.ostpc.malibu.shedule.entity.ClubPref;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Preference;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Shift;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;

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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;

/**
 * Create schedule entry point.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class CreateScheduleEntryPoint extends SimplePanel {

	private final CreateScheduleServiceAsync createScheduleService = GWT
			.create(CreateScheduleService.class);

	private final ScheduleManagerServiceAsync scheduleManagerService = GWT
			.create(ScheduleManagerService.class);

	private Date startDate;
	private Date endDate;
	private List<Club> clubs;
	private List<Employee> employees;
	private LinkedHashMap<String, Employee> employeeMap;
	private Preference preference;
	private List<Category> categories;
	private List<ScheduleWeekTable> weekTables;

	private AbsolutePanel schedulePanel;

	public CreateScheduleEntryPoint() {
		getStartDateFromServer();
		getClubsFromServer();
		getEmployeesFromServer();
		getPreferenceFromServer();
		getCategoriesFromServer();
		Timer timer = new Timer() {
			private int count;

			@Override
			public void run() {
				if (count < 20) {
					if (startDate != null && clubs != null && employees != null
							&& preference != null && categories != null) {
						cancel();
						setEmployeeMap();
						drawPage();
					}
					count++;
				} else {
					Window.alert("Невозможно пулучить данные с сервера!");
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
				Window.alert("Невозможно получить начальную дату графика с сервера!");
			}
		});
	}

	private void getClubsFromServer() {
		createScheduleService
				.getDependentClubs(new AsyncCallback<List<Club>>() {

					@Override
					public void onSuccess(List<Club> result) {
						if (result != null) {
							clubs = result;
						} else {
							clubs = new ArrayList<Club>();
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Невозможно получить список клубов с сервера!");
					}
				});
	}

	private void getEmployeesFromServer() {
		createScheduleService.getEmployees(new AsyncCallback<List<Employee>>() {

			@Override
			public void onSuccess(List<Employee> result) {
				if (result != null) {
					employees = result;
				} else {
					employees = new ArrayList<Employee>();
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Невозможно получить список сотрудников с сервера!");
			}
		});
	}

	private void getPreferenceFromServer() {
		createScheduleService.getPreference(new AsyncCallback<Preference>() {

			@Override
			public void onSuccess(Preference result) {
				if (result != null) {
					preference = result;
				} else {
					preference = new Preference();
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Невозможно получить количестве смен с сервера!");
			}
		});
	}

	private void getCategoriesFromServer() {
		createScheduleService
				.getCategoriesWithEmployees(new AsyncCallback<List<Category>>() {

					@Override
					public void onSuccess(List<Category> result) {
						if (result != null) {
							categories = result;
						} else {
							categories = new ArrayList<Category>();
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Невозможно получить список категорий с сервера!");
					}
				});
	}

	private void setEmployeeMap() {
		employeeMap = new LinkedHashMap<String, Employee>();
		for (Employee employee : employees) {
			employeeMap.put(String.valueOf(employee.getEmployeeId()), employee);
		}
	}

	private void drawPage() {
		final AbsolutePanel rootPanel = new AbsolutePanel();
		rootPanel.setSize("100%", "100%");
		rootPanel.setStyleName("ScheduleBlock");
		AbsolutePanel headerPanel = new AbsolutePanel();
		headerPanel.setStyleName("headerPanel");

		Image groupImage = new Image("img/group.png");
		groupImage.setSize("75px", "75px");
		headerPanel.add(groupImage, 10, 15);

		CaptionPanel schedulePlanningPanel = new CaptionPanel(
				"График работы на период с/по");
		schedulePlanningPanel.setSize("550px", "60px");

		AbsolutePanel datePanel = new AbsolutePanel();
		datePanel.setSize("550px", "45px");

		Label startLabel = new Label("Начало");
		startLabel.setWidth("50px");
		datePanel.add(startLabel, 10, 15);
		final DatePicker startDatePicker = new DatePicker();
		final DateTimeFormat dateFormat = DateTimeFormat
				.getFormat("dd/MM/yyyy");
		final DateBox startDateBox = new DateBox(startDatePicker, new Date(),
				new DateBox.DefaultFormat(dateFormat));
		startDateBox.setSize("75px", "16px");
		datePanel.add(startDateBox, 70, 10);
		Image startCalendarIcon = new Image(
				"scheduleManager/sc/skins/Enterprise/images/DynamicForm/date_control.png");
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

		Label endLabel = new Label("Конец");
		endLabel.setWidth("50px");
		datePanel.add(endLabel, 220, 15);
		final DatePicker endDatePicker = new DatePicker();
		final DateBox endDateBox = new DateBox(endDatePicker, new Date(),
				new DateBox.DefaultFormat(dateFormat));
		endDateBox.setSize("75px", "16px");
		datePanel.add(endDateBox, 280, 10);
		Image endCalendarIcon = new Image(
				"scheduleManager/sc/skins/Enterprise/images/DynamicForm/date_control.png");
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

		final Button applyButton = new Button("Применить");
		applyButton.setSize("95px", "30px");
		datePanel.add(applyButton, 435, 10);

		schedulePlanningPanel.add(datePanel);
		headerPanel.add(schedulePlanningPanel, 110, 10);

		AbsolutePanel controlPanel = new AbsolutePanel();
		controlPanel.setSize("325px", "45px");

		final Button generateScheduleButton = new Button("Сгенерировать");
		generateScheduleButton.setSize("110px", "30px");
		controlPanel.add(generateScheduleButton, 10, 10);

		final Button saveScheduleButton = new Button("Сохранить");
		saveScheduleButton.setSize("90px", "30px");
		controlPanel.add(saveScheduleButton, 125, 10);

		Button resetScheduleButton = new Button("Сбросить");
		resetScheduleButton.setSize("90px", "30px");
		controlPanel.add(resetScheduleButton, 220, 10);

		headerPanel.add(controlPanel, 0, 85);

		rootPanel.add(headerPanel, 0, 0);

		schedulePanel = new AbsolutePanel();
		schedulePanel.setStyleName("schedulePanel");

		rootPanel.add(schedulePanel, 0, 135);

		startDateBox.setValue(startDate);
		endDateBox.setValue(startDate);

		applyButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				applyButton.setFocus(false);
				Date periodStartDate = startDateBox.getValue();
				Date periodEndDate = endDateBox.getValue();
				if (periodStartDate == null || periodEndDate == null
						|| periodStartDate.after(periodEndDate)) {
					Window.alert("Начальная или конечная дата графика работы указана некорректно!");
					return;
				}
				if (periodStartDate.after(periodEndDate)) {
					Window.alert("Начальная дата графика работы больше конечной даты!");
					return;
				}
				if (periodStartDate.before(startDate)) {
					Window.alert("Начальная дата графика работы меньше текущей начальной даты ("
							+ dateFormat.format(startDate)
							+ "). Графики работ перекрываются!");
					return;
				}
				if (weekTables != null) {
					weekTables.clear();
					ClubPrefSelectItem.removeData();
					EmpOnShiftListBox.removeData();
				}
				schedulePanel.clear();
				drawSchedule(periodStartDate, periodEndDate);
			}

			private void drawSchedule(Date periodStartDate, Date periodEndDate) {
				int numberOfDays = CalendarUtil.getDaysBetween(periodStartDate,
						periodEndDate) + 1;
				int tablesHeight = 20;
				startDate = new Date(periodStartDate.getTime());
				endDate = new Date(periodEndDate.getTime());
				weekTables = new ArrayList<ScheduleWeekTable>();
				Date startDate = new Date(periodStartDate.getTime());
				DateTimeFormat dayOfWeekFormat = DateTimeFormat.getFormat("c");
				ClubPrefSelectItem.setCategoryList(categories);
				LinkedHashMap<String, String> valueMap = ClubPrefSelectItem
						.getValueMap(employees, categories);
				LinkedHashMap<String, String> employeeMap = new LinkedHashMap<String, String>();
				for (Employee employee : employees) {
					employeeMap.put(String.valueOf(employee.getEmployeeId()),
							employee.getNameForSchedule());
				}
				EmpOnShiftListBox.setSchedulePanel(schedulePanel);
				while (numberOfDays != 0) {
					Date currentDate = new Date(startDate.getTime());
					while (!dayOfWeekFormat.format(currentDate).equals("0")
							&& !currentDate.equals(periodEndDate)) {
						CalendarUtil.addDaysToDate(currentDate, 1);
					}
					int daysInTable = CalendarUtil.getDaysBetween(startDate,
							currentDate) + 1;
					numberOfDays -= daysInTable;

					ScheduleWeekTable scheduleTable = ScheduleWeekTable
							.drawScheduleTable(startDate, daysInTable, clubs,
									preference, employeeMap, valueMap);

					weekTables.add(scheduleTable);
					CalendarUtil.addDaysToDate(startDate, daysInTable);
					schedulePanel.add(scheduleTable, 5, tablesHeight);
					tablesHeight += scheduleTable.getOffsetHeight();
					tablesHeight += 20;
				}
				schedulePanel.setHeight(tablesHeight + "px");
			}
		});

		generateScheduleButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				generateScheduleButton.setFocus(false);
				if (weekTables == null || weekTables.size() == 0) {
					Window.alert("Распиcание ещё не создано! Нажмите кнопку \"Применить\".");
					return;
				}
				Period period = getPeriod();
				Status status = getStatus();
				Map<java.sql.Date, List<ClubDaySchedule>> dayScheduleMap = getDayScheduleMap();
				List<ClubPref> clubPrefs = getClubPrefs();
				Schedule schedule = new Schedule(period, status,
						dayScheduleMap, clubPrefs);
				scheduleManagerService.generate(schedule,
						new AsyncCallback<Schedule>() {

							@Override
							public void onSuccess(Schedule result) {
								Window.alert("Расписание успешно сгенерировано!");
								writeSchedule(result);
							}

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Невозможно сгенерировать расписание на сервере!\n" 
										+ caught.getMessage());
							}
						});
			}
		});

		saveScheduleButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				saveScheduleButton.setFocus(false);
				if (weekTables == null || weekTables.size() == 0) {
					Window.alert("Распиcание ещё не создано! Нажмите кнопку \"Применить\".");
					return;
				}
				Period period = getPeriod();
				Status status = getStatus();
				Map<java.sql.Date, List<ClubDaySchedule>> dayScheduleMap = getDayScheduleMap();
				List<ClubPref> clubPrefs = getClubPrefs();
				Schedule schedule = new Schedule(period, status,
						dayScheduleMap, clubPrefs);
				createScheduleService.insertSchedule(schedule,
						new AsyncCallback<Schedule>() {

							@Override
							public void onSuccess(Schedule result) {
								Window.alert("Расписание успешно сохранено!");
								writeSchedule(result);
							}

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Невозможно сохранить созданное расписание на сервере!");
							}
						});
			}
		});

		resetScheduleButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				SC.confirm("Предупреждение!",
						"Вы действительно хотите сбросить график работы?",
						new BooleanCallback() {

							@Override
							public void execute(Boolean value) {
								if (value) {
									if (weekTables != null) {
										weekTables.clear();
										ClubPrefSelectItem.removeData();
										EmpOnShiftListBox.removeData();
									}
									schedulePanel.clear();
									endDateBox.setValue(new Date(startDate
											.getTime()));
								}
							}
						});
			}
		});

		setWidget(rootPanel);
	}

	private Period getPeriod() {
		Period period = new Period(startDate, endDate);
		return period;
	}

	private Status getStatus() {
		return Status.DRAFT;
	}

	private Map<java.sql.Date, List<ClubDaySchedule>> getDayScheduleMap() {
		Map<java.sql.Date, List<ClubDaySchedule>> dayScheduleMap = new HashMap<java.sql.Date, List<ClubDaySchedule>>();
		Date currentDate = new Date(startDate.getTime());
		while (currentDate.compareTo(endDate) <= 0) {
			List<ClubDaySchedule> clubDayScheduleList = new ArrayList<ClubDaySchedule>();
			List<ShiftItem> shiftItemList = EmpOnShiftListBox
					.getDateShiftItemMap().get(currentDate);
			for (Club club : clubs) {
				ClubDaySchedule clubDaySchedule = new ClubDaySchedule();
				clubDaySchedule.setDate(new Date(currentDate.getTime()));
				clubDaySchedule.setClub(club);
				clubDaySchedule.setShiftsNumber(preference.getShiftsNumber());
				clubDaySchedule.setWorkHoursInDay(preference
						.getWorkHoursInDay());
				List<Shift> shifts = new ArrayList<Shift>();
				for (ShiftItem shiftItem : shiftItemList) {
					if (shiftItem.getClubId() == club.getClubId()) {
						Shift shift = new Shift();
						shift.setShiftNumber(shiftItem.getShiftNumber());
						shift.setQuantityOfEmployees(EmpOnShiftListBox
								.getEmployeesOnShift(club.getClubId()));
						List<Employee> employees = new ArrayList<Employee>();
						HashSet<String> shiftValueSet = shiftItem
								.getPrevValueSet();
						for (String employeeId : shiftValueSet) {
							employees.add(employeeMap.get(employeeId));
						}
						shift.setEmployees(employees);
						shifts.add(shift);
					}
				}
				clubDaySchedule.setShifts(shifts);
				clubDayScheduleList.add(clubDaySchedule);
			}
			dayScheduleMap.put(new java.sql.Date(currentDate.getTime()),
					clubDayScheduleList);
			CalendarUtil.addDaysToDate(currentDate, 1);
		}
		return dayScheduleMap;
	}

	private List<ClubPref> getClubPrefs() {
		return ClubPrefSelectItem.getClubPrefs();
	}

	private void writeSchedule(Schedule schedule) {
		Map<Date, List<ShiftItem>> dateShiftItemMap = EmpOnShiftListBox
				.getDateShiftItemMap();
		Date currentDate = new Date(startDate.getTime());
		Map<java.sql.Date, List<ClubDaySchedule>> dayScheduleMap = schedule
				.getDayScheduleMap();
		while (currentDate.compareTo(endDate) <= 0) {
			List<ClubDaySchedule> clubDayScheduleList = dayScheduleMap
					.get(currentDate);
			List<ShiftItem> shiftItemList = dateShiftItemMap.get(currentDate);
			for (ClubDaySchedule clubDaySchedule : clubDayScheduleList) {
				List<Shift> shiftList = clubDaySchedule.getShifts();
				for (Shift shift : shiftList) {
					for (ShiftItem shiftItem : shiftItemList) {
						if (shift.getShiftNumber() == shiftItem
								.getShiftNumber()
								&& clubDaySchedule.getClub().getClubId() == shiftItem
										.getClubId()) {
							List<String> employeeIdList = new ArrayList<String>();
							for (Employee employee : shift.getEmployees()) {
								employeeIdList.add(String.valueOf(employee
										.getEmployeeId()));
							}
							shiftItem.setValue(employeeIdList.toArray());
						}
					}
				}
			}
			dateShiftItemMap
					.put(new Date(currentDate.getTime()), shiftItemList);
			CalendarUtil.addDaysToDate(currentDate, 1);
		}
		EmpOnShiftListBox.setDateShiftItemMap(dateShiftItemMap);
	}
}
