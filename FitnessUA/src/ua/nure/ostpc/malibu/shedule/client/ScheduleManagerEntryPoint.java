package ua.nure.ostpc.malibu.shedule.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Right;
import ua.nure.ostpc.malibu.shedule.entity.Role;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;

import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

public class ScheduleManagerEntryPoint implements EntryPoint {

	private final ScheduleManagerServiceAsync scheduleManagerService = GWT
			.create(ScheduleManagerService.class);

	private List<Period> periodList;
	private Map<Long, Status> scheduleStatusMap;
	List<Role> roles = null;
	private int counter = 1;
	private Boolean isResponsible = false;
	public void onModuleLoad() {
		getAllPeriodsFromServer();
		getScheduleStatusMapFromServer();
		Timer timer = new Timer() {
			private int count;

			@Override
			public void run() {
				if (count < 10) {
					if (periodList != null && scheduleStatusMap != null) {
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

	private void getAllPeriodsFromServer() {
		scheduleManagerService.getAllPeriods(new AsyncCallback<List<Period>>() {

			@Override
			public void onSuccess(List<Period> result) {
				if (result != null) {
					periodList = result;
				} else {
					periodList = new ArrayList<Period>();
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Cannot get clubs from server!");
			}
		});
	}

	private void getScheduleStatusMapFromServer() {
		scheduleManagerService
				.getScheduleStatusMap(new AsyncCallback<Map<Long, Status>>() {

					@Override
					public void onSuccess(Map<Long, Status> result) {
						if (result != null) {
							scheduleStatusMap = result;
						} else {
							scheduleStatusMap = new HashMap<Long, Status>();
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Cannot get clubs from server!");
					}
				});
	}

	private void drawPage() {
		final ListGrid listGrid = new ListGrid() {
			@Override
			protected Canvas createRecordComponent(final ListGridRecord record,
					Integer colNum) {
				String fieldName = this.getFieldName(colNum);
				if (fieldName.equals("Статус")) {
					IButton button = new IButton();
					button.setHeight(18);
					button.setWidth(60);
					button.setTitle("");
					button.setIcon("/img/" + record.getAttribute("Статус")
							+ ".png");
					button.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							SC.say("Статус расписания: "
									+ record.getAttribute("Статус"));
						}
					});
					return button;
				} else if (fieldName.equals("view")) {
					IButton button = new IButton();
					button.setHeight(18);
					button.setWidth(65);
					button.setTitle("");
					button.setIcon("/img/view_icon.png");
					button.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							SC.say("Режим просмотра выбран");
						}
					});
					return button;
				} else if (fieldName.equals("edit")) {
					IButton button = new IButton();
					button.setHeight(18);
					button.setWidth(65);
					button.setTitle("");
					button.setIcon("/img/file_edit.png");
					button.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							scheduleManagerService
									.userRoles(new AsyncCallback<List<Role>>() {

										@Override
										public void onFailure(Throwable caught) {
											// TODO Auto-generated method stub

										}

										@Override
										public void onSuccess(List<Role> result) {
											roles = result;
											for(Role role : roles){
												if (role.getRight() == Right.RESPONSIBLE_PERSON)
												{
													isResponsible = true;
												}
													
											}
											if (isResponsible == true)
											{
												Window.alert("Before");
												long periodId = Long.parseLong(record
														.getAttribute("Редактирование"));
												Window.alert(String.valueOf(periodId));
												scheduleManagerService.lockSchedule(periodId,
														new AsyncCallback<Boolean>() {


															@Override
															public void onSuccess(Boolean result) {
																if (result) {
																	SC.say("Режим редактирования запущен");
																	
																	
																} else {
																	SC.say("Режим редактирования не запущен");
																}
															}

															@Override
															public void onFailure(Throwable caught) {
																SC.say("ошибка!!");
															}
														});
											}
											else 
											{
												SC.say("Режим черновика запущен");
											}
										}
									});
							

						}
					});
					return button;
				} else if (fieldName.equals("emails")) {
					IButton button = new IButton();
					button.setHeight(18);
					button.setWidth(65);
					button.setTitle("");
					button.setIcon("/img/mail_send.png");
					button.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							SC.say("Расписание отправлено");
						}
					});
					return button;
				} else {
					return null;
				}
			}
		};
		listGrid.setShowRecordComponents(true);
		listGrid.setShowRecordComponentsByCell(true);
		listGrid.setCanRemoveRecords(true);
		ListGridField rowNum = new ListGridField("itemNum", "No.");
		rowNum.setWidth(35);
		rowNum.setCellFormatter(new CellFormatter() {
			public String format(Object value, ListGridRecord record,
					int rowNum, int colNum) {
				return rowNum + "";
			}
		});
		ListGridField status = new ListGridField("Статус");
		ListGridField start = new ListGridField("Дата начала");
		ListGridField end = new ListGridField("Дата окончания");
		ListGridField view = new ListGridField("view", "Просмотр");
		ListGridField edit = new ListGridField("edit", "Редактирование");
		ListGridField emails = new ListGridField("emails", "Отправить");
		listGrid.setFields(rowNum, status, start, end, view, edit, emails);
		int number = 1;
		for (Period period : periodList) {
			ListGridRecord rec = new ListGridRecord();
			rec.setAttribute("rowNum", period.getPeriodId());
			rec.setAttribute("Статус",
					scheduleStatusMap.get(period.getPeriodId()));
			rec.setAttribute("Дата начала", period.getStartDate());
			rec.setAttribute("Дата окончания", period.getEndDate());
			rec.setAttribute("Просмотр", period.getPeriodId());
			rec.setAttribute("Редактирование", period.getPeriodId());
			rec.setAttribute("Отправить", period.getPeriodId());
			listGrid.addData(rec);
			number = getNextNumber();
		}
		listGrid.setWidth(600);
		listGrid.setHeight(224);
		listGrid.draw();
	}

	private int getNextNumber() {
		return counter++;
	}
}