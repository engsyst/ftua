package ua.nure.ostpc.malibu.shedule.client.manage;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.client.ScheduleManagerEntryPoint;
import ua.nure.ostpc.malibu.shedule.client.event.DoManageEvent;
import ua.nure.ostpc.malibu.shedule.client.event.DoManageHandler;
import ua.nure.ostpc.malibu.shedule.client.event.DoViewEvent;
import ua.nure.ostpc.malibu.shedule.client.panel.editing.ScheduleEditingPanel;
import ua.nure.ostpc.malibu.shedule.client.panel.editing.ScheduleEditingPanel.Mode;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.smartgwt.client.util.SC;

public class ManagerModule extends Composite {
	
	private static Map<String, String> statusTranslationMap = new HashMap<String, String>();
	private static FlexTable scheduleManagerTable = new FlexTable();

	static {
		statusTranslationMap.put(Status.CLOSED.toString(), "Закрыт");
		statusTranslationMap.put(Status.CURRENT.toString(), "Текущий");
		statusTranslationMap.put(Status.DRAFT.toString(), "Черновик");
		statusTranslationMap.put(Status.FUTURE.toString(), "Будущий");
	}
	
	private static ManagerModule module;
	
	private ManagerModule() {
	}

	public static synchronized FlexTable getPanel () {
		if (module == null) 
			new ManagerModule();
		module.getScheduleStatusMapFromServer();
		return scheduleManagerTable;
	}
	
	private  void getScheduleStatusMapFromServer() {
		AppState.scheduleManagerService
		.getScheduleStatusMap(new AsyncCallback<Map<Long, Status>>() {

			@Override
			public void onSuccess(Map<Long, Status> result) {
				if (result != null) {
					AppState.scheduleStatusMap.clear();
					AppState.scheduleStatusMap.putAll(result);
				} else {
					AppState.scheduleStatusMap = 
							new HashMap<Long, Status>();
				}
				drawScheduleManagerTable();
			}

			@Override
			public void onFailure(Throwable caught) {
				SC.say("Проблемы с сервером, "
						+ "пожалуйста обратитесь к "
						+ "системному администратору \n"
						+ "Код ошибки 3");
			}
		});
	}
	
	private long getIdFromEvent(ClickEvent event) {
		String elementId = event.getRelativeElement().getId();
		return Long.parseLong(elementId.split("-")[1]);
	}
	
	private void drawScheduleManagerTable() {
		scheduleManagerTable.clear();
		scheduleManagerTable = new FlexTable();
		scheduleManagerTable.insertRow(0);
		scheduleManagerTable.setStyleName("mainTable");
		for (int i = 0; i < 7; i++) {
			scheduleManagerTable.insertCell(0, i);
		}
		scheduleManagerTable.setText(0, 0, "№");
		scheduleManagerTable.setText(0, 1, "Статус");
		scheduleManagerTable.setText(0, 2, "Дата начала");
		scheduleManagerTable.setText(0, 3, "Дата окончания");
		scheduleManagerTable.setText(0, 4, "Просмотр");
		scheduleManagerTable.setText(0, 5, "Редактирование");
		scheduleManagerTable.setText(0, 6, "Отправить");
		for (int i = 0; i < 7; i++) {
			scheduleManagerTable.getCellFormatter().setStyleName(0, i,
					"secondHeader");
			scheduleManagerTable.getCellFormatter().setStyleName(0, i,
					"mainHeader");
		}
		int index = 1;

		Collections.sort(AppState.periodList, new Comparator<Period>() {

			@Override
			public int compare(Period lhs, Period rhs) {
				return lhs.getStartDate().compareTo(rhs.getStartDate());
			}
		});

		for (Period period : AppState.periodList) {
			
			long periodId = period.getPeriodId();
			
			scheduleManagerTable.insertRow(index);
			for (int i = 0; i < 7; i++) {
				scheduleManagerTable.insertCell(index, i);
			}
			
			// 0 column --> No
			scheduleManagerTable.setText(index, 0, String.valueOf(period.getPeriodId()));
			
			// 1 column --> Status
			HorizontalPanel scheduleStatusPanel = new HorizontalPanel();
			Image scheduleStatusImage = new Image(GWT.getHostPageBaseURL()
					+ "img/" + AppState.scheduleStatusMap.get(periodId)
					+ ".png");
			scheduleStatusImage.setStyleName("myImageAsButton");
			scheduleStatusImage.setTitle(String.valueOf(periodId));

			String scheduleStatusString = AppState.scheduleStatusMap.get(periodId)
					.toString();
			String scheduleStatus = statusTranslationMap
					.get(scheduleStatusString);
			scheduleStatusPanel.add(scheduleStatusImage);
			scheduleStatusPanel.add(new Label(scheduleStatus));
			scheduleManagerTable.setWidget(index, 1, scheduleStatusPanel);
			
			// 2 column --> Start date
			scheduleManagerTable.setText(index, 2, period.getStartDate().toString());
			
			// 3 column --> End date
			scheduleManagerTable.setText(index, 3, period.getEndDate().toString());

			// 4 column --> View
			Image scheduleViewButton = new Image(GWT.getHostPageBaseURL()
					+ "img/view_icon.png");
			scheduleViewButton.setStyleName("myImageAsButton");
			scheduleViewButton.setTitle(period.getStartDate().toString() 
					+ " - " + period.getEndDate().toString());
			scheduleViewButton.getElement().setId("view-" + period.getPeriodId());

			scheduleViewButton.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					try {
						AppState.eventBus.fireEvent(
								new DoViewEvent(module.getIdFromEvent(event)));
					} catch (NumberFormatException e) {
						SC.say("Нет такого");
					}
				}
			});

			scheduleManagerTable.setWidget(index, 4, scheduleViewButton);

			// 5 column --> Edit
			Image scheduleEditButton = new Image(GWT.getHostPageBaseURL()
					+ "img/file_edit.png");
			scheduleEditButton.setTitle(String.valueOf(index));
			scheduleEditButton.setStyleName("myImageAsButton");
			scheduleEditButton.getElement().setId("edit-" + period.getPeriodId());

			scheduleEditButton.addClickHandler(new ClickHandler() {

				public void onClick(final ClickEvent event) {
					long id = module.getIdFromEvent(event);
					if (AppState.isResponsible) {
						AppState.scheduleManagerService.lockSchedule(id,
								new AsyncCallback<Boolean>() {

									@Override
									public void onSuccess(Boolean result) {
										if (result) {
											drawScheduleForResponsiblePerson(period
													.getPeriodId());
										} else {
											SC.say("График работы редактируется или является закрытым!");
										}
									}

									@Override
									public void onFailure(Throwable caught) {
										SC.say("Невозможно получить данные с сервера!");
									}

								});

					} else {
						showDraft(scheduleManagerTable, period.getPeriodId());
					}
				}
			});

			scheduleManagerTable.setWidget(index, 5, scheduleEditButton);

			final Image scheduleSendImage = new Image(GWT.getHostPageBaseURL()
					+ "img/mail_send.png");
			scheduleSendImage.setStyleName("myImageAsButton");
			scheduleSendImage.setTitle(String.valueOf(index));

			scheduleSendImage.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					SC.say("График отправлен");
					SC.say(Integer.toString(scheduleManagerTable.getRowCount()
							- scheduleManagerTable.getCellForEvent(event)
									.getRowIndex()));
				}
			});

			scheduleManagerTable.setWidget(index, 6, scheduleSendImage);

		}
		addToMainViewPanel(scheduleManagerTable,
				ScheduleManagerEntryPoint.class.getName());
	}


	}
}
