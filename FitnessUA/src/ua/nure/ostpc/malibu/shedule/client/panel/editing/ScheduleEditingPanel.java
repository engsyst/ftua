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

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.client.MyEventDialogBox;
import ua.nure.ostpc.malibu.shedule.client.module.PrefEditForm;
import ua.nure.ostpc.malibu.shedule.entity.Category;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubDaySchedule;
import ua.nure.ostpc.malibu.shedule.entity.ClubPref;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Preference;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;
import ua.nure.ostpc.malibu.shedule.entity.ScheduleViewData;
import ua.nure.ostpc.malibu.shedule.entity.Shift;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
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
		this(Mode.CREATION, null);
	}

	public ScheduleEditingPanel(Mode mode, Long periodId) {
		this.mode = mode;
		getScheduleViewData(periodId);
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

	private void getScheduleViewData(Long periodId) {
		AppState.scheduleManagerService.getScheduleVewData(periodId,
				new AsyncCallback<ScheduleViewData>() {

					@Override
					public void onSuccess(ScheduleViewData result) {
						if (result != null) {
							startDate = result.getStartDate();
							clubs = result.getClubs();
							employees = result.getEmployees();
							preference = result.getPrefs();
							categories = result.getCategories();
							currentSchedule = result.getSchedule();
						} else {
							startDate = new Date(System.currentTimeMillis());
							clubs = new ArrayList<Club>();
							employees = new ArrayList<Employee>();
							preference = new Preference();
							categories = new ArrayList<Category>();
						}
						setEmployeeMap();
						drawPage();
					}

					@Override
					public void onFailure(Throwable caught) {
						SC.warn("Невозможно получить данные с сервера!");
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
		drawTopLine();
		drawControlPanel();
		if (currentSchedule != null
				&& (mode == Mode.VIEW || mode == Mode.EDITING)) {
			drawSchedule(currentSchedule);
		}
	}

	private void drawTopLine() {
		AppState.moduleContentGrayPanel.clear();
		if (mode == Mode.VIEW) {
			return;
		}
		final AbsolutePanel mainPanel = new AbsolutePanel();
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

		final Image prefImage = new Image("img/settings.png");
		prefImage.setSize("21px", "22px");
		final PushButton prefButton = new PushButton(prefImage);
		prefButton.setSize("21px", "23px");

		prefButton.addClickHandler(prefButtonClickHandler);

		mainPanel.add(prefButton, 110, 5);

		AppState.moduleContentGrayPanel.add(mainPanel, 5, 5);
	}

	private void drawControlPanel() {
		final AbsolutePanel rootPanel = new AbsolutePanel();
		rootPanel.setSize("100%", "100%");
		rootPanel.setStyleName("ScheduleBlock");
		AbsolutePanel headerPanel = new AbsolutePanel();
		headerPanel.setStyleName("headerPanel");

		Image groupImage = new Image("img/group.png");
		groupImage.setSize("90px", "75px");
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
		Image startCalendarIcon = new Image("img/schedule.png");
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
		Image endCalendarIcon = new Image("img/schedule.png");
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
				if (weekTables != null && weekTables.size() != 0) {
					oldSchedule = getSchedule();
					weekTables.clear();
				}
				ClubPrefSelectItem.removeData();
				EmpOnShiftListBox.removeData();
				schedulePanel.clear();
				Schedule newSchedule = Schedule.newEmptyShedule(
						periodStartDate, periodEndDate,
						new HashSet<Club>(clubs), preference);
				if (oldSchedule != null) {
					Map<Date, List<ClubDaySchedule>> newDayScheduleMap = newSchedule
							.getDayScheduleMap();
					Map<Date, List<ClubDaySchedule>> oldDayScheduleMap = oldSchedule
							.getDayScheduleMap();
					Iterator<Entry<Date, List<ClubDaySchedule>>> it = oldDayScheduleMap
							.entrySet().iterator();
					while (it.hasNext()) {
						Entry<Date, List<ClubDaySchedule>> entry = it.next();
						Date date = entry.getKey();
						if (newDayScheduleMap.containsKey(date)) {
							newDayScheduleMap.put(date, entry.getValue());
						}
					}
					oldSchedule.setDayScheduleMap(newDayScheduleMap);
					oldSchedule.getPeriod().setPeriod(periodStartDate,
							periodEndDate);
					drawSchedule(oldSchedule);
				} else {
					drawSchedule(newSchedule);
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
				AppState.scheduleManagerService.generate(schedule,
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

	private void setGenerateEnable(boolean value) {
		applyButton.setEnabled(value);
		saveScheduleButton.setEnabled(value);
		generateScheduleButton.setEnabled(value);
		executionButton.setEnabled(value);
	}

	private void saveSchedule(Schedule schedule) {
		if (mode == Mode.CREATION) {
			AppState.scheduleEditingService.insertSchedule(schedule,
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
			AppState.scheduleEditingService.updateSchedule(schedule,
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

	private Schedule getSchedule() {
		Period period = getPeriod();
		Status status = getStatus();
		Map<Date, List<ClubDaySchedule>> dayScheduleMap = getDayScheduleMap();
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

	private Map<Date, List<ClubDaySchedule>> getDayScheduleMap() {
		Map<Date, List<ClubDaySchedule>> dayScheduleMap = new HashMap<Date, List<ClubDaySchedule>>();
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
			dayScheduleMap.put(new Date(currentDate.getTime()),
					clubDayScheduleList);
			CalendarUtil.addDaysToDate(currentDate, 1);
		}
		return dayScheduleMap;
	}

	private List<ClubPref> getClubPrefs() {
		return ClubPrefSelectItem.getClubPrefs();
	}

	private void drawEmptySchedule(Date periodStartDate, Date periodEndDate) {
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
		if (employees != null)
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
		if (startDateBox == null)
			startDateBox = new DateBox();
		startDateBox.setValue(startDate);
		endDate = period.getEndDate();
		if (endDateBox == null)
			endDateBox = new DateBox();
		endDateBox.setValue(endDate);
		drawEmptySchedule(period.getStartDate(), period.getEndDate());
		setDataInEmpOnShiftListBox(schedule);
		ClubPrefSelectItem.setClubPrefs(schedule.getClubPrefs());
		Date currentDate = new Date(startDate.getTime());
		Map<Date, List<ClubDaySchedule>> dayScheduleMap = schedule
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
						shiftItem.setValues(employeeIdList.toArray());
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

	private ClickHandler prefButtonClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			final MyEventDialogBox dlg = new MyEventDialogBox();
			dlg.setAnimationEnabled(true);
			dlg.setAutoHideEnabled(true);
			dlg.setText("Настройки графика работ");
			VerticalPanel panel = new VerticalPanel();
			panel.add(new PrefEditForm());
			dlg.add(panel);

			dlg.center();
		}
	};

}
