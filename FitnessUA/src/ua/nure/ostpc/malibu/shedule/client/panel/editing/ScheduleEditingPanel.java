package ua.nure.ostpc.malibu.shedule.client.panel.editing;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
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
public class ScheduleEditingPanel extends SimplePanel {
	private final ScheduleEditingServiceAsync scheduleEditingService = GWT
			.create(ScheduleEditingService.class);
	private final ScheduleManagerServiceAsync scheduleManagerService = GWT
			.create(ScheduleManagerService.class);

	public enum Mode {
		CREATION, EDITING, VIEW
	};

	private Mode mode;
	private Schedule currentSchedule;
	private Date startDate;
	private Date endDate;
	private List<Club> clubs;
	private List<Employee> employees;
	private LinkedHashMap<String, Employee> employeeMap;
	private Preference preference;
	private List<Category> categories;
	private List<ScheduleWeekTable> weekTables;

	private DateBox startDateBox;
	private DateBox endDateBox;
	private Button applyButton;
	private Button generateScheduleButton;
	private Button saveScheduleButton;
	private Button resetScheduleButton;
	private Button executionButton;
	private AbsolutePanel schedulePanel;

	public ScheduleEditingPanel() {
		this(Mode.CREATION);
	}

	public ScheduleEditingPanel(Mode mode, long periodId) {
		this(mode);
		getScheduleFromServer(periodId);
		Timer timer = new Timer() {
			private int count;

			@Override
			public void run() {
				if (count < 20) {
					if (currentSchedule != null) {
						cancel();
						drawSchedule(currentSchedule);
					}
					count++;
				} else {
					SC.warn("Невозможно пулучить данные с сервера!");
					cancel();
				}
			}
		};
		timer.scheduleRepeating(AppConstants.asyncDelay);
	}

	private ScheduleEditingPanel(Mode mode) {
		this.mode = mode;
		getStartDateFromServer();
		getClubsFromServer();
		getEmployeesFromServer();
		getPreferenceFromServer();
		getCategoriesFromServer();
		Timer timer = new Timer() {
			private int count;

			@Override
			public void run() {
				if (count < 30) {
					if (startDate != null && clubs != null && employees != null
							&& preference != null && categories != null) {
						cancel();
						setEmployeeMap();
						drawPage();
					}
					count++;
				} else {
					SC.warn("Невозможно пулучить данные с сервера!");
					cancel();
				}
			}
		};
		timer.scheduleRepeating(AppConstants.asyncDelay);
	}

	public Mode getMode() {
		return mode;
	}

	public long getPeriodId() {
		long periodId = 0;
		if (currentSchedule != null) {
			periodId = currentSchedule.getPeriod().getPeriodId();
		}
		return periodId;
	}

	private void getStartDateFromServer() {
		scheduleEditingService.getStartDate(new AsyncCallback<Date>() {

			@Override
			public void onSuccess(Date result) {
				startDate = result;
			}

			@Override
			public void onFailure(Throwable caught) {
				SC.warn("Невозможно получить начальную дату графика с сервера!");
			}
		});
	}

	private void getClubsFromServer() {
		scheduleEditingService
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
						SC.warn("Невозможно получить список клубов с сервера!");
					}
				});
	}

	private void getEmployeesFromServer() {
		scheduleEditingService
				.getEmployees(new AsyncCallback<List<Employee>>() {

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
						SC.warn("Невозможно получить список сотрудников с сервера!");
					}
				});
	}

	private void getPreferenceFromServer() {
		scheduleEditingService.getPreference(new AsyncCallback<Preference>() {

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
				SC.warn("Невозможно получить количество смен с сервера!");
			}
		});
	}

	private void getCategoriesFromServer() {
		scheduleEditingService
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
						SC.warn("Невозможно получить список категорий с сервера!");
					}
				});
	}

	private void getScheduleFromServer(long periodId) {
		scheduleManagerService.getScheduleById(periodId,
				new AsyncCallback<Schedule>() {

					@Override
					public void onSuccess(Schedule result) {
						currentSchedule = result;
					}

					@Override
					public void onFailure(Throwable caught) {
						SC.warn("Невозможно получить график работы с сервера!");
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
		if (mode != Mode.VIEW) {
			drawTopLine();
		}
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
		startDateBox = new DateBox(startDatePicker, new Date(),
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
				if (mode == Mode.CREATION) {
					startDateBox.getDatePicker().setVisible(true);
					Date date = event.getValue();
					startDateBox.setValue(date);
				}
			}
		});

		startCalendarIcon.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (mode == Mode.CREATION) {
					startDateBox.getTextBox().fireEvent(event);
				}
			}
		});

		Label endLabel = new Label("Конец");
		endLabel.setWidth("50px");
		datePanel.add(endLabel, 220, 15);
		final DatePicker endDatePicker = new DatePicker();
		endDateBox = new DateBox(endDatePicker, new Date(),
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
				if (mode == Mode.CREATION) {
					endDateBox.getDatePicker().setVisible(true);
					Date date = event.getValue();
					endDateBox.setValue(date);
				}
			}
		});

		endCalendarIcon.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (mode == Mode.CREATION) {
					endDateBox.getTextBox().fireEvent(event);
				}
			}
		});

		applyButton = new Button("Применить");
		applyButton.setSize("95px", "30px");
		datePanel.add(applyButton, 435, 10);

		schedulePlanningPanel.add(datePanel);
		headerPanel.add(schedulePlanningPanel, 110, 10);

		AbsolutePanel controlPanel = new AbsolutePanel();
		controlPanel.setSize("325px", "45px");

		generateScheduleButton = new Button("Сгенерировать");
		generateScheduleButton.setSize("110px", "30px");
		controlPanel.add(generateScheduleButton, 10, 10);

		saveScheduleButton = new Button("Сохранить");
		saveScheduleButton.setSize("90px", "30px");
		controlPanel.add(saveScheduleButton, 125, 10);

		resetScheduleButton = new Button("Сбросить");
		resetScheduleButton.setEnabled(false);
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
					SC.warn("Начальная или конечная дата графика работы указана некорректно!");
					return;
				}
				if (periodStartDate.after(periodEndDate)) {
					SC.warn("Начальная дата графика работы больше конечной даты!");
					return;
				}
				if (periodStartDate.before(startDate)
						&& CalendarUtil.getDaysBetween(periodStartDate,
								startDate) != 0) {
					SC.warn("Начальная дата графика работы меньше текущей начальной даты ("
							+ dateFormat.format(startDate)
							+ "). Графики работ перекрываются!");
					return;
				}
				Schedule oldSchedule = null;
				if (weekTables != null) {
					oldSchedule = getSchedule();
					weekTables.clear();
				}
				ClubPrefSelectItem.removeData();
				EmpOnShiftListBox.removeData();
				schedulePanel.clear();
				drawEmptySchedule(periodStartDate, periodEndDate);
				if (oldSchedule != null) {
					Schedule newSchedule = getSchedule();
					Map<java.sql.Date, List<ClubDaySchedule>> newDayScheduleMap = newSchedule
							.getDayScheduleMap();
					Map<java.sql.Date, List<ClubDaySchedule>> oldDayScheduleMap = oldSchedule
							.getDayScheduleMap();
					Iterator<Entry<java.sql.Date, List<ClubDaySchedule>>> it = oldDayScheduleMap
							.entrySet().iterator();
					while (it.hasNext()) {
						Entry<java.sql.Date, List<ClubDaySchedule>> entry = it
								.next();
						java.sql.Date date = entry.getKey();
						if (newDayScheduleMap.containsKey(date)) {
							newDayScheduleMap.put(date, entry.getValue());
						}
					}
					oldSchedule.setDayScheduleMap(newDayScheduleMap);
					oldSchedule.getPeriod().setPeriod(periodStartDate,
							periodEndDate);
					drawSchedule(oldSchedule);
				}
				resetScheduleButton.setEnabled(true);
			}

		});

		generateScheduleButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				generateScheduleButton.setFocus(false);
				if (weekTables == null || weekTables.size() == 0) {
					SC.warn("Распиcание ещё не создано! Нажмите кнопку \"Применить\".");
					return;
				}
				setGenerateEnable(false);
				Schedule schedule = getSchedule();
				scheduleManagerService.generate(schedule,
						new AsyncCallback<Schedule>() {

							@Override
							public void onSuccess(Schedule result) {
								SC.say("Расписание успешно сгенерировано!");
								drawSchedule(result);
								setGenerateEnable(true);
							}

							@Override
							public void onFailure(Throwable caught) {
								SC.warn("Невозможно сгенерировать расписание на сервере!\n"
										+ caught.getMessage());
								setGenerateEnable(true);
							}
						});
			}
		});

		saveScheduleButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				saveScheduleButton.setFocus(false);
				if (weekTables == null || weekTables.size() == 0) {
					SC.warn("Распиcание ещё не создано! Нажмите кнопку \"Применить\".");
					return;
				}
				disableBeforeSave();
				Schedule schedule = getSchedule();
				saveSchedule(schedule);
			}
		});

		resetScheduleButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				resetScheduleButton.setFocus(false);
				if (weekTables == null || weekTables.size() == 0) {
					SC.warn("Нет созданного расписания!");
					return;
				}
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
									resetScheduleButton.setEnabled(false);
								}
							}
						});
			}
		});

		if (mode == Mode.EDITING) {
			startDateBox.setEnabled(false);
			endDateBox.setEnabled(false);
			applyButton.setEnabled(false);
		}
		if (mode == Mode.VIEW) {
			startDateBox.setEnabled(false);
			endDateBox.setEnabled(false);
			applyButton.setVisible(false);
			controlPanel.setVisible(false);
		}

		setWidget(rootPanel);
	}

	private void saveSchedule(Schedule schedule) {
		if (mode == Mode.CREATION) {
			scheduleEditingService.insertSchedule(schedule,
					new AsyncCallback<Schedule>() {

						@Override
						public void onSuccess(Schedule result) {
							SC.say("Расписание успешно сохранено!");
							mode = Mode.EDITING;
							currentSchedule = result;
							drawSchedule(result);
							enableAfterSave();
						}

						@Override
						public void onFailure(Throwable caught) {
							SC.warn("Невозможно сохранить созданное расписание на сервере!");
							enableAfterSave();
						}
					});
		} else {
			scheduleEditingService.updateSchedule(schedule,
					new AsyncCallback<Schedule>() {

						@Override
						public void onSuccess(Schedule result) {
							SC.say("Расписание успешно сохранено!");
							mode = Mode.EDITING;
							currentSchedule = result;
							drawSchedule(result);
							enableAfterSave();
						}

						@Override
						public void onFailure(Throwable caught) {
							SC.warn("Невозможно сохранить расписание на сервере!");
							enableAfterSave();
						}
					});
		}
	}

	private void setGenerateEnable(boolean value) {
		applyButton.setEnabled(value);
		saveScheduleButton.setEnabled(value);
		generateScheduleButton.setEnabled(value);
		executionButton.setEnabled(value);
	}

	private void disableBeforeSave() {
		startDateBox.setEnabled(false);
		endDateBox.setEnabled(false);
		applyButton.setEnabled(false);
		saveScheduleButton.setEnabled(false);
		generateScheduleButton.setEnabled(false);
		resetScheduleButton.setEnabled(false);
		executionButton.setEnabled(false);
	}

	private void enableAfterSave() {
		saveScheduleButton.setEnabled(true);
		generateScheduleButton.setEnabled(true);
		executionButton.setEnabled(true);
	}

	private void drawTopLine() {
		AbsolutePanel topLinePanel = RootPanel.get("topGreyLine");
		AbsolutePanel mainPanel = new AbsolutePanel();
		mainPanel.setStyleName("greyLine");
		executionButton = new Button("К исполнению");

		executionButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				executionButton.setFocus(false);
				if (weekTables == null || weekTables.size() == 0) {
					SC.warn("Распиcание ещё не создано! Нажмите кнопку \"Применить\".");
					return;
				}
				if (currentSchedule != null
						&& currentSchedule.getStatus() == Status.CURRENT) {
					SC.warn("Текущее расписание не может быть назначено к исполнению!");
					return;
				}
				disableBeforeSave();
				Schedule schedule = getSchedule();
				schedule.setStatus(Status.FUTURE);
				saveSchedule(schedule);
			}
		});

		mainPanel.add(executionButton, 0, 5);
		topLinePanel.add(mainPanel, 5, 5);
	}

	private Schedule getSchedule() {
		Period period = getPeriod();
		Status status = getStatus();
		Map<java.sql.Date, List<ClubDaySchedule>> dayScheduleMap = getDayScheduleMap();
		List<ClubPref> clubPrefs = getClubPrefs();
		Schedule schedule = new Schedule(period, status, dayScheduleMap,
				clubPrefs);
		return schedule;
	}

	private Period getPeriod() {
		Period period = new Period(startDate, endDate);
		if (mode == Mode.EDITING && currentSchedule != null) {
			period.setPeriodId(currentSchedule.getPeriod().getPeriodId());
		}
		return period;
	}

	private Status getStatus() {
		if (currentSchedule != null) {
			return currentSchedule.getStatus();
		}
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

	private void drawEmptySchedule(Date periodStartDate, Date periodEndDate) {
		drawEmptySchedule(periodStartDate, periodEndDate, true);
	}

	private void drawEmptySchedule(Date periodStartDate, Date periodEndDate,
			boolean addOnMainPanel) {
		int numberOfDays = CalendarUtil.getDaysBetween(periodStartDate,
				periodEndDate) + 1;
		startDate = new Date(periodStartDate.getTime());
		endDate = new Date(periodEndDate.getTime());
		weekTables = new ArrayList<ScheduleWeekTable>();
		EmpOnShiftListBox.removeData();
		ClubPrefSelectItem.removeData();
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
		}
		if (addOnMainPanel) {
			addWeekTablesOnSchedulePanel();
		}
	}

	private void drawSchedule(Schedule schedule) {
		if (weekTables != null) {
			weekTables.clear();
		}
		ClubPrefSelectItem.removeData();
		EmpOnShiftListBox.removeData();
		currentSchedule = schedule;
		Period period = schedule.getPeriod();
		startDate = period.getStartDate();
		startDateBox.setValue(startDate);
		endDate = period.getEndDate();
		endDateBox.setValue(endDate);
		drawEmptySchedule(period.getStartDate(), period.getEndDate(), false);
		setDataInEmpOnShiftListBox(schedule);
		ClubPrefSelectItem.setClubPrefs(schedule.getClubPrefs());
		Date currentDate = new Date(startDate.getTime());
		Map<java.sql.Date, List<ClubDaySchedule>> dayScheduleMap = schedule
				.getDayScheduleMap();
		while (currentDate.compareTo(endDate) <= 0) {
			List<ClubDaySchedule> clubDayScheduleList = dayScheduleMap
					.get(currentDate);
			for (ClubDaySchedule clubDaySchedule : clubDayScheduleList) {
				List<Shift> shiftList = clubDaySchedule.getShifts();
				for (Shift shift : shiftList) {
					ShiftItem shiftItem = EmpOnShiftListBox.getShiftItem(
							currentDate, clubDaySchedule.getClub().getClubId(),
							shift.getShiftNumber());
					if (shiftItem != null) {
						List<String> employeeIdList = new ArrayList<String>();
						if (shift.getEmployees() != null) {
							for (Employee employee : shift.getEmployees()) {
								employeeIdList.add(String.valueOf(employee
										.getEmployeeId()));
							}
						}
						shiftItem.setValue(employeeIdList.toArray());
						shiftItem.changeNumberOfEmployees(shift
								.getQuantityOfEmployees());
						if (mode == Mode.VIEW) {
							shiftItem.disable();
						}
						EmpOnShiftListBox.updateShiftItem(shiftItem);
					}
				}
			}
			CalendarUtil.addDaysToDate(currentDate, 1);
		}
		if (mode == Mode.VIEW) {
			EmpOnShiftListBox.toView();
			ClubPrefSelectItem.toView();
		} else {
			if (schedule.getStatus() == Status.CURRENT) {
				EmpOnShiftListBox.disableElementsForCurrentSchedule();
			}
		}
		addWeekTablesOnSchedulePanel();
	}

	private void setDataInEmpOnShiftListBox(Schedule schedule) {
		List<ClubDaySchedule> clubDaySchedules = schedule.getDayScheduleMap()
				.get(schedule.getPeriod().getStartDate());
		List<Long> clubIdList = new ArrayList<Long>();
		for (ClubDaySchedule clubDaySchedule : clubDaySchedules) {
			long clubId = clubDaySchedule.getClub().getClubId();
			if (!clubIdList.contains(clubId)) {
				clubIdList.add(clubId);
				int quantityOfEmployees = clubDaySchedule.getShifts().get(0)
						.getQuantityOfEmployees();
				EmpOnShiftListBox.setEmpOnShiftForClub(clubId,
						quantityOfEmployees);
			}
		}
	}

	private void addWeekTablesOnSchedulePanel() {
		int tablesHeight = 20;
		for (ScheduleWeekTable scheduleTable : weekTables) {
			schedulePanel.add(scheduleTable, 5, tablesHeight);
			tablesHeight += scheduleTable.getOffsetHeight();
			tablesHeight += 20;
		}
		schedulePanel.setHeight(tablesHeight + "px");
	}
}
