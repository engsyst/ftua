package ua.nure.ostpc.malibu.shedule.client.manage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.client.LoadingPanel;
import ua.nure.ostpc.malibu.shedule.client.event.DoDraftEvent;
import ua.nure.ostpc.malibu.shedule.client.event.DoEditEvent;
import ua.nure.ostpc.malibu.shedule.client.event.DoViewEvent;
import ua.nure.ostpc.malibu.shedule.client.event.PeriodsUpdatedEvent;
import ua.nure.ostpc.malibu.shedule.client.event.PeriodsUpdatedHandler;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.smartgwt.client.util.SC;

public class ManagerModule extends Composite implements PeriodsUpdatedHandler {
	
	private static Map<Status, String> statusTranslationMap = new HashMap<Status, String>();
	private FlexTable table = new FlexTable();
	private static final int MAX_COLS = 8;

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
				drawTable();
				LoadingPanel.stop();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				LoadingPanel.stop();
				SC.say(caught.getMessage());
			}
		}); 
	}
	
	private long getIdFromEvent(ClickEvent event) {
		return Long.parseLong(event.getRelativeElement().getId().split("-")[1]);
	}
	
	private void drawHeader() {
		table.clear();
		table.insertRow(0);
		table.setStyleName("mainTable");
		for (int i = 0; i < MAX_COLS; i++) {
			table.insertCell(0, i);
		}
		table.setText(0, 0, "№");
		table.setText(0, 1, "Статус");
		table.setText(0, 2, "Дата начала");
		table.setText(0, 3, "Дата окончания");
		table.setText(0, 4, "Просмотр");
		table.setText(0, 5, "Редактирование");
		table.setText(0, 6, "Отправить");
		table.setText(0, 7, "Сохранить");
//		table.getFlexCellFormatter().setColSpan(0, 6, 4);
//		table.getFlexCellFormatter().setColSpan(0, 6, 3);
		for (int i = 0; i < MAX_COLS; i++) {
			table.getCellFormatter().setStyleName(0, i,
					"secondHeader");
			table.getCellFormatter().setStyleName(0, i,
					"mainHeader");
		}
	}
	
	private void drawTable() {
//		drawHeader();

		Collections.sort(AppState.periodList, new Comparator<Period>() {

			@Override
			public int compare(Period lhs, Period rhs) {
				return lhs.getStartDate().compareTo(rhs.getStartDate());
			}
		});

		int index = 1;
		int orderNumber = 0;
		for (Period period : AppState.periodList) {
			orderNumber++;
			
			final long periodId = period.getPeriodId();
			
			table.insertRow(index);
			for (int i = 0; i < MAX_COLS; i++) {
				table.insertCell(index, i);
			}
			
			int c = 0;
			// 0 column --> No
			table.setText(index, c++, String.valueOf(orderNumber));
			
			// 1 column --> Status
			final HorizontalPanel scheduleStatusPanel = new HorizontalPanel();
			final Image scheduleStatusImage = new Image(GWT.getHostPageBaseURL()
					+ "img/" + period.getStatus().toString()
					+ ".png");
			scheduleStatusImage.setStyleName("myImageAsButton");
//			scheduleStatusImage.setTitle(String.valueOf(periodId));

			String scheduleStatus = statusTranslationMap
					.get(period.getStatus());
			scheduleStatusPanel.add(scheduleStatusImage);
			scheduleStatusPanel.add(new Label(scheduleStatus));
			table.setWidget(index, c++, scheduleStatusPanel);
			
			// 2 column --> Start date
			table.setText(index, c++, period.getStartDate().toString());
			
			// 3 column --> End date
			table.setText(index, c++, period.getEndDate().toString());

			// 4 column --> View
			final Image scheduleViewButton = new Image(GWT.getHostPageBaseURL()
					+ "img/view_icon.png");
			scheduleViewButton.setStyleName("myImageAsButton");
			scheduleViewButton.setTitle("Просмотреть");
			scheduleViewButton.getElement().setId("view-" + periodId);

			scheduleViewButton.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					try {
						History.newItem(AppConstants.HISTORY_VIEW + "-" + getIdFromEvent(event));
//						AppState.eventBus.fireEvent(
//								new DoViewEvent(getIdFromEvent(event)));
					} catch (NumberFormatException e) {
						SC.say("Нет такого");
					}
				}
			});

			table.setWidget(index, c++, scheduleViewButton);

			// 5 column --> Edit
			final Image scheduleEditButton = new Image(GWT.getHostPageBaseURL()
					+ "img/file_edit.png");
			scheduleEditButton.setTitle(String.valueOf(index));
			scheduleEditButton.setStyleName("myImageAsButton");
			scheduleEditButton.getElement().setId("edit-" + periodId);
			scheduleEditButton.setTitle(
					AppState.isResponsible ? "Редактировать" : "Черновик");

			if (period.getStatus().equals(Status.CLOSED) 
					|| (!AppState.isResponsible && !period.getStatus().equals(Status.DRAFT))) {
				scheduleEditButton.setStyleDependentName("disabled", true);
				scheduleEditButton.setTitle("");
			} else {
				scheduleEditButton.addClickHandler(new ClickHandler() {
					
					public void onClick(final ClickEvent event) {
						final long id = getIdFromEvent(event);
						if (AppState.isResponsible) {
							AppState.scheduleManagerService.lockSchedule(id,
									new AsyncCallback<Boolean>() {
								
								@Override
								public void onSuccess(Boolean result) {
									if (result) {
										History.newItem(AppConstants.HISTORY_EDIT + "-" + id);
//										AppState.eventBus.fireEvent(new DoEditEvent(id));
									} else {
										SC.say("График работы редактируется или является закрытым!");
									}
								}
								
								@Override
								public void onFailure(Throwable caught) {
									SC.say(caught.getMessage());
								}
								
							});
							
						} else {
							History.newItem(AppConstants.HISTORY_DRAFT + "-" + id);
//							AppState.eventBus.fireEvent(new DoDraftEvent(id));
						}
					}
				});
			}

			table.setWidget(index, c++, scheduleEditButton);

			// 6 column --> Send to
			final SendButton sendImage = new SendButton(periodId);
			table.setWidget(index, c++, sendImage);
			
			// 7 column --> Send to
			final SaveButton saveImage = new SaveButton(periodId);
			table.setWidget(index, c++, saveImage);
		}
	}
	
	@Override
	public void onUpdate(PeriodsUpdatedEvent periodsUpdatedEvent) {
		drawTable();
	}
}
