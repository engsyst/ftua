package ua.nure.ostpc.malibu.shedule.client.manage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.client.event.DoDraftEvent;
import ua.nure.ostpc.malibu.shedule.client.event.DoEditEvent;
import ua.nure.ostpc.malibu.shedule.client.event.DoViewEvent;
import ua.nure.ostpc.malibu.shedule.client.event.PeriodsUpdatedEvent;
import ua.nure.ostpc.malibu.shedule.client.event.PeriodsUpdatedHandler;
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

public class ManagerModule extends Composite implements PeriodsUpdatedHandler {
	
	private static Map<Status, String> statusTranslationMap = new HashMap<Status, String>();
	private static FlexTable table = new FlexTable();

	static {
		statusTranslationMap.put(Status.CLOSED, "Закрыт");
		statusTranslationMap.put(Status.CURRENT, "Текущий");
		statusTranslationMap.put(Status.DRAFT, "Черновик");
		statusTranslationMap.put(Status.FUTURE, "Будущий");
	}
	
	public ManagerModule() {
		AppState.eventBus.addHandler(PeriodsUpdatedEvent.TYPE, this);
		getAllPeriods();
		drawHeader();
		initWidget(table);
	}

//	public void setData(List<Period> data) {
//		assert data != null : "data cannot be a null";
//		AppState.periodList = data;
//	}
	
	private  void getAllPeriods() {
		AppState.scheduleManagerService
		.getAllPeriods(new AsyncCallback<List<Period>>() {
			
			@Override
			public void onSuccess(List<Period> result) {
				if (AppState.periodList == null) 
					AppState.periodList = new ArrayList<Period>();
				AppState.periodList.clear();
				if (result != null)
					AppState.periodList.addAll(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				SC.say(caught.getMessage());
			}
		}); 
	}
	
	private long getIdFromEvent(ClickEvent event) {
		String elementId = event.getRelativeElement().getId();
		return Long.parseLong(elementId.split("-")[1]);
	}
	
	private void drawHeader() {
		table.clear();
		table.insertRow(0);
		table.setStyleName("mainTable");
		for (int i = 0; i < 7; i++) {
			table.insertCell(0, i);
		}
		table.setText(0, 0, "№");
		table.setText(0, 1, "Статус");
		table.setText(0, 2, "Дата начала");
		table.setText(0, 3, "Дата окончания");
		table.setText(0, 4, "Просмотр");
		table.setText(0, 5, "Редактирование");
		table.setText(0, 6, "Отправить");
		for (int i = 0; i < 7; i++) {
			table.getCellFormatter().setStyleName(0, i,
					"secondHeader");
			table.getCellFormatter().setStyleName(0, i,
					"mainHeader");
		}
	}
	
	private void drawTable() {
		drawHeader();

		Collections.sort(AppState.periodList, new Comparator<Period>() {

			@Override
			public int compare(Period lhs, Period rhs) {
				return lhs.getStartDate().compareTo(rhs.getStartDate());
			}
		});

		int index = 1;
		for (final Period period : AppState.periodList) {
			
			final long periodId = period.getPeriodId();
			
			table.insertRow(index);
			for (int i = 0; i < 7; i++) {
				table.insertCell(index, i);
			}
			
			// 0 column --> No
			table.setText(index, 0, String.valueOf(period.getPeriodId()));
			
			// 1 column --> Status
			HorizontalPanel scheduleStatusPanel = new HorizontalPanel();
			Image scheduleStatusImage = new Image(GWT.getHostPageBaseURL()
					+ "img/" + statusTranslationMap.get(period.getStatus().toString())
					+ ".png");
			scheduleStatusImage.setStyleName("myImageAsButton");
			scheduleStatusImage.setTitle(String.valueOf(periodId));

			String scheduleStatus = statusTranslationMap
					.get(period.getStatus());
			scheduleStatusPanel.add(scheduleStatusImage);
			scheduleStatusPanel.add(new Label(scheduleStatus));
			table.setWidget(index, 1, scheduleStatusPanel);
			
			// 2 column --> Start date
			table.setText(index, 2, period.getStartDate().toString());
			
			// 3 column --> End date
			table.setText(index, 3, period.getEndDate().toString());

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
								new DoViewEvent(getIdFromEvent(event)));
					} catch (NumberFormatException e) {
						SC.say("Нет такого");
					}
				}
			});

			table.setWidget(index, 4, scheduleViewButton);

			// 5 column --> Edit
			Image scheduleEditButton = new Image(GWT.getHostPageBaseURL()
					+ "img/file_edit.png");
			scheduleEditButton.setTitle(String.valueOf(index));
			scheduleEditButton.setStyleName("myImageAsButton");
			scheduleEditButton.getElement().setId("edit-" + period.getPeriodId());

			scheduleEditButton.addClickHandler(new ClickHandler() {

				public void onClick(final ClickEvent event) {
					long id = getIdFromEvent(event);
					if (AppState.isResponsible) {
						AppState.scheduleManagerService.lockSchedule(id,
								new AsyncCallback<Boolean>() {

									@Override
									public void onSuccess(Boolean result) {
										if (result) {
											AppState.eventBus.fireEvent(new DoEditEvent(periodId));
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
						AppState.eventBus.fireEvent(new DoDraftEvent(periodId));
					}
				}
			});

			table.setWidget(index, 5, scheduleEditButton);

			// 6 column --> Edit
			final Image scheduleSendImage = new Image(GWT.getHostPageBaseURL()
					+ "img/mail_send.png");
			scheduleSendImage.setStyleName("myImageAsButton");
			scheduleSendImage.setTitle(String.valueOf(index));

			scheduleSendImage.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					SC.say("График отправлен");
					SC.say(Integer.toString(table.getRowCount()
							- table.getCellForEvent(event)
									.getRowIndex()));
				}
			});

			table.setWidget(index, 6, scheduleSendImage);
		}
	}

	@Override
	public void onUpdate(PeriodsUpdatedEvent periodsUpdatedEvent) {
		drawTable();
	}
}
