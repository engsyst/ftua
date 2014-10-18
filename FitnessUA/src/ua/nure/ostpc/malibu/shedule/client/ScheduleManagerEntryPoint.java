package ua.nure.ostpc.malibu.shedule.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.Path;
import ua.nure.ostpc.malibu.shedule.client.panel.creation.CreateScheduleEntryPoint;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Right;
import ua.nure.ostpc.malibu.shedule.entity.Role;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;

import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScheduleManagerEntryPoint implements EntryPoint {

	private final ScheduleManagerServiceAsync scheduleManagerService = GWT
			.create(ScheduleManagerService.class);
	private final ScheduleDraftServiceAsync scheduleDraft = GWT
			.create(ScheduleDraftService.class);
	public String employee;
	public Employee emp;
	private List<Period> periodList;
	private Map<Long, Status> scheduleStatusMap;
	List<Role> roles = null;
	private int counter = 1;
	private Boolean isResponsible = false;
	long draftPeriodId;
	boolean innerResult;
	ListGrid listGrid = new ListGrid();

	public void onModuleLoad() {
		getAllPeriodsFromServer();
		getScheduleStatusMapFromServer();
		getEmployeeSurname();
		getResponsible();
		Timer timer = new Timer() {
			private int count;

			@Override
			public void run() {
				if (count < 10) {
					if (periodList != null && scheduleStatusMap != null
							&& employee != null) {
						cancel();
						drawPrimaryPage();
						// drawPage();
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
private void getResponsible() {
	scheduleManagerService
	.userRoles(new AsyncCallback<List<Role>>() {

		@Override
		public void onFailure(Throwable caught) {

		}

		@Override
		public void onSuccess(List<Role> result) {
			roles = result;
			for (Role role : roles) {
				if (role.getRight() == Right.RESPONSIBLE_PERSON) {
					isResponsible = true;
				}

			}
		}
	});
}
	private void getEmployeeSurname() {
		scheduleDraft.getEmployee(new AsyncCallback<Employee>() {

			@Override
			public void onSuccess(Employee result) {
				employee = result.getFirstName() + " " + result.getLastName();

			}

			@Override
			public void onFailure(Throwable caught) {

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

	private void drawPage(final AbsolutePanel absolutePanel) {

		final FlexTable mainTable = new FlexTable();
		mainTable.insertRow(0);
		mainTable.setStyleName("mainTable");
		for (int i = 0; i < 7; i++)
			mainTable.insertCell(0, i);
		mainTable.setText(0, 0, "№");
		mainTable.setText(0, 1, "Статус");
		mainTable.setText(0, 2, "Дата начала");
		mainTable.setText(0, 3, "Дата окончания");
		mainTable.setText(0, 4, "Просмотр");
		mainTable.setText(0, 5, "Редактирование");
		mainTable.setText(0, 6, "Отправить");
		for(int i=0;i<7;i++)
			mainTable.getCellFormatter().setStyleName(0, i, "secondHeader");

		int index = 1;

		for (final Period period : periodList) {
			mainTable.insertRow(index);
			for (int i = 0; i < 7; i++)
				mainTable.insertCell(index, i);
			mainTable.setText(index, 0, String.valueOf(index - 1));
			HorizontalPanel panel = new HorizontalPanel();
			Image button1 = new Image("/img/"
					+ scheduleStatusMap.get(period.getPeriodId()) + ".png");
			button1.setStyleName("myBestManagerImage");
			button1.setTitle(String.valueOf(index));

			// button1.setIcon("/img/"
			// + scheduleStatusMap.get(period.getPeriodId()) + ".png");
			button1.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					SC.say("Статус графика работ: "
							+ scheduleStatusMap.get(period.getPeriodId()));
				}
			});
			panel.add(button1);
			panel.add(new Label(scheduleStatusMap.get(period.getPeriodId())
					.toString()));
			mainTable.setWidget(index, 1, panel);
			mainTable.setText(index, 2, period.getStartDate().toString());
			mainTable.setText(index, 3, period.getEndDate().toString());

			Image button2 = new Image("/img/view_icon.png");
			button2.setSize("18", "18");
			button2.setStyleName("myBestManagerImage");
			button2.setTitle(String.valueOf(index));
			// button2.setIcon("/img/view_icon.png");
			button2.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					try {
						absolutePanel.remove(0);
						LookingNearest cpschdrft = new LookingNearest(mainTable
								.getRowCount()
								- mainTable.getCellForEvent(event)
										.getRowIndex());
						absolutePanel.add(cpschdrft);
					} catch (Exception ex) {
						LookingNearest cpschdrft = new LookingNearest(mainTable
								.getRowCount()
								- mainTable.getCellForEvent(event)
										.getRowIndex());
						absolutePanel.add(cpschdrft);
					}
				}
			});
			mainTable.setWidget(index, 4, button2);

			Image button3 = new Image("/img/file_edit.png");
			button3.setSize("18", "18");
			button3.setTitle(String.valueOf(index));
			button3.setStyleName("myBestManagerImage");
			// button3.setIcon("/img/file_edit.png");
			button3.addClickHandler(new ClickHandler() {
				public void onClick(final ClickEvent event) {
					if (isResponsible == true) {
						long periodId = period.getPeriodId();
						scheduleManagerService.lockSchedule(
								periodId,
								new AsyncCallback<Boolean>() {

									@Override
									public void onSuccess(
											Boolean result) {
										innerResult = result;
									}

									@Override
									public void onFailure(
											Throwable caught) {
										SC.say("ошибка!!");
									}
								});
						if (innerResult) {
							showDraft(absolutePanel,mainTable,event);
						}
						else {
							showDraft(absolutePanel,mainTable,event);
						}
					} 
					else {
						showDraft(absolutePanel,mainTable,event);
					}
				}
			});
			mainTable.setWidget(index, 5, button3);

			final Image button4 = new Image("/img/mail_send.png");
			button4.setSize("18", "18");
			button4.setStyleName("myBestManagerImage");
			button4.setTitle(String.valueOf(index));
			// button4.setIcon("/img/mail_send.png");
			button4.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					SC.say("График отправлен");
					SC.say(Integer.toString(mainTable.getRowCount()
							- mainTable.getCellForEvent(event).getRowIndex()));
				}
			});
			mainTable.setWidget(index, 6, button4);

		}
		// listGrid.setWidth(600);
		// listGrid.setHeight(224);
		absolutePanel.add(mainTable);
		// listGrid.draw();
	}

	private int getNextNumber() {
		return counter++;
	}

	@SuppressWarnings("deprecation")
	private void drawPrimaryPage() {
		RootPanel rootPanel = RootPanel.get("nameFieldContainer");
		rootPanel.setSize("100%", "100%");

		DockPanel dockPanel = new DockPanel();
		rootPanel.add(dockPanel, 0, 0);
		dockPanel.setSize("100%", "100%");

		AbsolutePanel absolutePanel = new AbsolutePanel();
		absolutePanel.setStyleName("megaKostil");
		dockPanel.add(absolutePanel, DockPanel.NORTH);
		absolutePanel.setSize("100%", "100%");
		dockPanel.setCellHeight(absolutePanel, "10%");
		dockPanel.setCellWidth(absolutePanel, "auto");

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		absolutePanel.add(horizontalPanel, 0, 0);
		horizontalPanel.setSize("100%", "100%");

		AbsolutePanel absolutePanel_3 = new AbsolutePanel();
		absolutePanel_3.setStyleName("NapLogo");
		horizontalPanel.add(absolutePanel_3);
		absolutePanel_3.setSize("100%", "100%");
		horizontalPanel.setCellHeight(absolutePanel_3, "100%");
		horizontalPanel.setCellWidth(absolutePanel_3, "18%");

		Image image = new Image("/img/1_01.png");
		absolutePanel_3.add(image, 0, 0);
		image.setSize("100%", "100%");

		AbsolutePanel absolutePanel_4 = new AbsolutePanel();
		absolutePanel_4.setStyleName("megaKostil");
		horizontalPanel.add(absolutePanel_4);
		horizontalPanel.setCellHorizontalAlignment(absolutePanel_4,
				HasHorizontalAlignment.ALIGN_RIGHT);
		absolutePanel_4.setSize("100%", "100%");

		DockPanel dockPanel_1 = new DockPanel();
		absolutePanel_4.add(dockPanel_1);
		dockPanel_1.setSize("100%", "100%");

		AbsolutePanel absolutePanel_7 = new AbsolutePanel();
		absolutePanel_7.setStyleName("megaKostil");
		dockPanel_1.add(absolutePanel_7, DockPanel.EAST);
		absolutePanel_7.setSize("100%", "100%");

		HorizontalPanel horizontalPanel_6 = new HorizontalPanel();
		absolutePanel_7.add(horizontalPanel_6);
		horizontalPanel_6.setSize("100%", "100%");

		DockPanel dockPanel_2 = new DockPanel();
		horizontalPanel_6.add(dockPanel_2);
		dockPanel_2.setSize("100%", "100%");
		horizontalPanel_6.setCellHeight(dockPanel_2, "100%");
		horizontalPanel_6.setCellWidth(dockPanel_2, "80%");

		InlineLabel nlnlblNewInlinelabel_1 = new InlineLabel(employee);
		dockPanel_2.add(nlnlblNewInlinelabel_1, DockPanel.CENTER);
		dockPanel_2.setCellVerticalAlignment(nlnlblNewInlinelabel_1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		dockPanel_2.setCellHorizontalAlignment(nlnlblNewInlinelabel_1,
				HasHorizontalAlignment.ALIGN_RIGHT);

		AbsolutePanel absolutePanel_10 = new AbsolutePanel();
		absolutePanel_10.setStyleName("megaKostil");
		horizontalPanel_6.add(absolutePanel_10);
		horizontalPanel_6.setCellHeight(absolutePanel_10, "100%");
		horizontalPanel_6.setCellWidth(absolutePanel_10, "20%");
		horizontalPanel_6.setCellVerticalAlignment(absolutePanel_10,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_6.setCellHorizontalAlignment(absolutePanel_10,
				HasHorizontalAlignment.ALIGN_CENTER);
		absolutePanel_10.setSize("100%", "100%");

		Image image_6 = new Image("/img/user.png");
		image_6.setStyleName("NapLogo");
		absolutePanel_10.add(image_6);
		image_6.setSize("64px", "62px");

		final AbsolutePanel absolutePanel_9 = new AbsolutePanel();
		absolutePanel_9.setStyleName("myBestUserPanel");
		absolutePanel_10.add(absolutePanel_9);
		absolutePanel_9.setSize("280px", "100px");
		absolutePanel_9.setVisible(false);

		DockPanel dockPanel_3 = new DockPanel();
		absolutePanel_9.add(dockPanel_3);
		dockPanel_3.setSize("100%", "100%");

		AbsolutePanel absolutePanel_12 = new AbsolutePanel();
		dockPanel_3.add(absolutePanel_12, DockPanel.NORTH);

		DockPanel dockPanel_4 = new DockPanel();
		dockPanel_3.add(dockPanel_4, DockPanel.CENTER);
		dockPanel_4.setSize("100%", "100%");
		
		final SubmitButton logoutButton = new SubmitButton("Выйти");
		logoutButton.setSize("100%", "100%");

		final FormPanel logoutFormPanel = new FormPanel();
		logoutFormPanel.setStyleName("logoutPanel");
		logoutFormPanel.setWidth("100px");
		dockPanel_4.add(logoutFormPanel, DockPanel.EAST);
		dockPanel_4.setCellVerticalAlignment(logoutFormPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		dockPanel_4.setCellHorizontalAlignment(logoutFormPanel,
				HasHorizontalAlignment.ALIGN_CENTER);
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

//		FormPanel logoutFormPanel = new FormPanel();
//		dockPanel_4.add(logoutFormPanel, DockPanel.EAST);
//		logoutFormPanel.setWidth("100px");
//		logoutFormPanel.setMethod(FormPanel.METHOD_POST);
//		logoutFormPanel.setAction(Path.COMMAND__LOGOUT);
//		dockPanel_4.setCellVerticalAlignment(logoutFormPanel,
//				HasVerticalAlignment.ALIGN_MIDDLE);
//		dockPanel_4.setCellHorizontalAlignment(logoutFormPanel,
//				HasHorizontalAlignment.ALIGN_CENTER);
//		final Button exitButton = new Button("Выйти");
//		logoutFormPanel.setWidget(exitButton);
//		exitButton.setSize("100%", "100%");
//		logoutFormPanel.addSubmitHandler(new FormPanel.SubmitHandler() {
//
//			@Override
//			public void onSubmit(SubmitEvent event) {
//				exitButton.click();
//			}
//		});
//
//		logoutFormPanel
//				.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
//
//					@Override
//					public void onSubmitComplete(SubmitCompleteEvent event) {
//						Window.Location.replace(Path.COMMAND__LOGIN);
//					}
//				});
		Button setProfile = new Button("Редактировать профиль");
		dockPanel_4.add(setProfile, DockPanel.WEST);
		setProfile.setWidth("110px");

		dockPanel_4.setCellVerticalAlignment(setProfile,
				HasVerticalAlignment.ALIGN_MIDDLE);
		dockPanel_4.setCellHorizontalAlignment(setProfile,
				HasHorizontalAlignment.ALIGN_CENTER);
		AbsolutePanel absolutePanel_8 = new AbsolutePanel();
		dockPanel_1.add(absolutePanel_8, DockPanel.CENTER);
		dockPanel_1.setCellWidth(absolutePanel_8, "60%");
		absolutePanel_8.setSize("100%", "100%");

		AbsolutePanel absolutePanel_1 = new AbsolutePanel();
		absolutePanel_1.setStyleName("westPanelNap");
		dockPanel.add(absolutePanel_1, DockPanel.WEST);
		absolutePanel_1.setSize("100%", "100%");
		dockPanel.setCellHeight(absolutePanel_1, "100%");
		dockPanel.setCellWidth(absolutePanel_1, "18%");

		VerticalPanel verticalPanel = new VerticalPanel();
		absolutePanel_1.add(verticalPanel);
		verticalPanel.setSize("100%", "100%");

		AbsolutePanel absolutePanel_5 = new AbsolutePanel();
		absolutePanel_5.setStyleName("gwt-StackPanelItem");
		verticalPanel.add(absolutePanel_5);
		absolutePanel_5.setSize("100%", "100%");
		verticalPanel.setCellHeight(absolutePanel_5, "50%");
		verticalPanel.setCellWidth(absolutePanel_5, "100%");

		VerticalPanel verticalPanel_2 = new VerticalPanel();
		absolutePanel_5.add(verticalPanel_2, 0, 0);
		verticalPanel_2.setSize("100%", "100%");

		AbsolutePanel lookingNearest = new AbsolutePanel();
		lookingNearest.setStyleName("horizontalPanelForLeft");
		verticalPanel_2.add(lookingNearest);
		lookingNearest.setSize("100%", "100%");
		verticalPanel_2.setCellHeight(lookingNearest, "10%");
		verticalPanel_2.setCellWidth(lookingNearest, "100%");

		HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
		horizontalPanel_1.setStyleName("horizontalPanelForLeft");
		horizontalPanel_1
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		lookingNearest.add(horizontalPanel_1);
		horizontalPanel_1.setSize("100%", "100%");
		Image image_1 = new Image("/img/33.png");
		horizontalPanel_1.add(image_1);
		horizontalPanel_1.setCellHorizontalAlignment(image_1,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_1.setCellHeight(image_1, "100%");
		horizontalPanel_1.setCellWidth(image_1, "30%");
		image_1.setSize("32px", "32px");

		InlineLabel inlineLabel_3 = new InlineLabel("Просмотр текущего");
		inlineLabel_3.setStyleName("leftLabels");
		horizontalPanel_1.add(inlineLabel_3);
		horizontalPanel_1.setCellHeight(inlineLabel_3, "10%");
		horizontalPanel_1.setCellWidth(inlineLabel_3, "100%");
		inlineLabel_3.setSize("100%", "100%");

		AbsolutePanel Draft = new AbsolutePanel();
		Draft.setStyleName("horizontalPanelForLeft");
		verticalPanel_2.add(Draft);
		Draft.setSize("100%", "100%");
		verticalPanel_2.setCellHeight(Draft, "10%");
		verticalPanel_2.setCellWidth(Draft, "100%");

		HorizontalPanel horizontalPanel_2 = new HorizontalPanel();
		horizontalPanel_2.setStyleName("horizontalPanelForLeft");
		horizontalPanel_2
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		Draft.add(horizontalPanel_2);
		horizontalPanel_2.setSize("100%", "100%");

		Image image_2 = new Image("/img/47.png");
		horizontalPanel_2.add(image_2);
		horizontalPanel_2.setCellHorizontalAlignment(image_2,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2.setCellWidth(image_2, "30%");
		image_2.setSize("32px", "32px");

		InlineLabel inlineLabel_2 = new InlineLabel("Редактировать ближайший");
		inlineLabel_2.setStyleName("leftLabels");
		horizontalPanel_2.add(inlineLabel_2);
		horizontalPanel_2.setCellHeight(inlineLabel_2,
				"Редактировать ближайший");
		inlineLabel_2.setSize("100%", "100%");
		AbsolutePanel absolutePanel_6 = new AbsolutePanel();
		absolutePanel_6.setStyleName("gwt-StackPanelItem");
		verticalPanel.add(absolutePanel_6);
		absolutePanel_6.setSize("100%", "100%");

		AbsolutePanel absolutePanel_2 = new AbsolutePanel();
		dockPanel.add(absolutePanel_2, DockPanel.CENTER);
		absolutePanel_2.setSize("100%", "100%");

		VerticalPanel verticalPanel_1 = new VerticalPanel();
		verticalPanel_1.setStyleName("CentralPanelGraphique");
		absolutePanel_2.add(verticalPanel_1);
		verticalPanel_1.setSize("100%", "100%");

		AbsolutePanel absolutePanel_14 = new AbsolutePanel();
		verticalPanel_1.add(absolutePanel_14);
		absolutePanel_14.setSize("100%", "100%");
		verticalPanel_1.setCellHeight(absolutePanel_14, "10%");
		verticalPanel_1.setCellWidth(absolutePanel_14, "100%");

		final AbsolutePanel MainAbsolutePanel = new AbsolutePanel();
		MainAbsolutePanel.setStyleName("KutuzoffPanel");
		verticalPanel_1.add(MainAbsolutePanel);
		verticalPanel_1.setCellHeight(MainAbsolutePanel, "100%");
		verticalPanel_1.setCellWidth(MainAbsolutePanel, "100%");
		MainAbsolutePanel.setSize("100%", "100%");
		if(isResponsible) {
		AbsolutePanel Manager = new AbsolutePanel();
		Manager.setStyleName("horizontalPanelForLeft");
		verticalPanel_2.add(Manager);
		Manager.setSize("100%", "100%");
		verticalPanel_2.setCellHeight(Manager, "10%");
		verticalPanel_2.setCellWidth(Manager, "100%");

		HorizontalPanel horizontalPanel_3 = new HorizontalPanel();
		horizontalPanel_3
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		Manager.add(horizontalPanel_3);
		horizontalPanel_3.setSize("100%", "100%");

		Image image_3 = new Image("/img/91.png");
		horizontalPanel_3.add(image_3);
		horizontalPanel_3.setCellHorizontalAlignment(image_3,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_3.setCellWidth(image_3, "30%");
		image_3.setSize("32px", "32px");

		InlineLabel inlineLabel_1 = new InlineLabel(
				"Управление графиками работ");
		inlineLabel_1.setStyleName("leftLabels");
		horizontalPanel_3.add(inlineLabel_1);
		horizontalPanel_3.setCellHeight(inlineLabel_1, "100%");
		horizontalPanel_3.setCellWidth(inlineLabel_1, "100%");
		inlineLabel_1.setSize("100%", "100%");

		AbsolutePanel CreateSchedule = new AbsolutePanel();
		CreateSchedule.setStyleName("horizontalPanelForLeft");
		verticalPanel_2.add(CreateSchedule);
		CreateSchedule.setSize("100%", "100%");
		verticalPanel_2.setCellHeight(CreateSchedule, "10%");
		verticalPanel_2.setCellWidth(CreateSchedule, "100%");

		HorizontalPanel horizontalPanel_4 = new HorizontalPanel();
		horizontalPanel_4
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		CreateSchedule.add(horizontalPanel_4);
		horizontalPanel_4.setSize("100%", "100%");

		Image image_4 = new Image("/img/15.png");
		horizontalPanel_4.add(image_4);
		horizontalPanel_4.setCellHorizontalAlignment(image_4,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_4.setCellWidth(image_4, "30%");
		image_4.setSize("32px", "32px");

		InlineLabel inlineLabel = new InlineLabel("Создать новый");
		inlineLabel.setStyleName("leftLabels");
		horizontalPanel_4.add(inlineLabel);
		inlineLabel.setSize("100%", "100%");

		AbsolutePanel StartSettings = new AbsolutePanel();
		StartSettings.setStyleName("horizontalPanelForLeft");
		verticalPanel_2.add(StartSettings);
		StartSettings.setSize("100%", "100%");
		verticalPanel_2.setCellHeight(StartSettings, "10%");
		verticalPanel_2.setCellWidth(StartSettings, "100%");

		HorizontalPanel horizontalPanel_5 = new HorizontalPanel();
		horizontalPanel_5
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		StartSettings.add(horizontalPanel_5);
		horizontalPanel_5.setSize("100%", "100%");

		Image image_5 = new Image("/img/44.png");
		horizontalPanel_5.add(image_5);
		horizontalPanel_5.setCellHorizontalAlignment(image_5,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_5.setCellWidth(image_5, "30%");
		image_5.setSize("32px", "32px");

		InlineLabel nlnlblNewInlinelabel = new InlineLabel("Настройки");
		nlnlblNewInlinelabel.setStyleName("leftLabels");
		horizontalPanel_5.add(nlnlblNewInlinelabel);
		horizontalPanel_5.setCellHeight(nlnlblNewInlinelabel, "100%");
		nlnlblNewInlinelabel.setSize("100%", "100%");
		horizontalPanel_3.sinkEvents(Event.ONCLICK);
		horizontalPanel_3.addHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				try {
					MainAbsolutePanel.remove(0);
					drawPage(MainAbsolutePanel);
				} catch (Exception ex) {
					drawPage(MainAbsolutePanel);
				}
			}

		}, ClickEvent.getType());
		horizontalPanel_4.sinkEvents(Event.ONCLICK);
		horizontalPanel_4.addHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				try {
					MainAbsolutePanel.remove(0);
					CreateScheduleEntryPoint cpschdrft = new CreateScheduleEntryPoint();
					MainAbsolutePanel.add(cpschdrft);
				} catch (Exception ex) {
					CreateScheduleEntryPoint cpschdrft = new CreateScheduleEntryPoint();
					MainAbsolutePanel.add(cpschdrft);
				}
			}

		}, ClickEvent.getType());
		horizontalPanel_5.sinkEvents(Event.ONCLICK);
		horizontalPanel_5.addHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				try {
					MainAbsolutePanel.remove(0);
					StartSettingEntryPoint cpschdrft = new StartSettingEntryPoint();
					MainAbsolutePanel.add(cpschdrft);
				} catch (Exception ex) {
					StartSettingEntryPoint cpschdrft = new StartSettingEntryPoint();
					MainAbsolutePanel.add(cpschdrft);
				}
			}

		}, ClickEvent.getType());
		}
		

		image_6.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!absolutePanel_9.isVisible())
					absolutePanel_9.setVisible(true);
				else
					absolutePanel_9.setVisible(false);
			}
		});
		setProfile
				.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {

					@Override
					public void onClick(
							com.google.gwt.event.dom.client.ClickEvent event) {
						UserSettingSimplePanel sp = new UserSettingSimplePanel();
						callDialogBox(sp);
					}
				});
		horizontalPanel_1.sinkEvents(Event.ONCLICK);
		horizontalPanel_1.addHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				try {
					MainAbsolutePanel.remove(0);
					LookingNearest cpschdrft = new LookingNearest();
					MainAbsolutePanel.add(cpschdrft);
				} catch (Exception ex) {
					LookingNearest cpschdrft = new LookingNearest();
					MainAbsolutePanel.add(cpschdrft);
				}
			}

		}, ClickEvent.getType());
		horizontalPanel_2.sinkEvents(Event.ONCLICK);
		horizontalPanel_2.addHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {

				scheduleManagerService
						.getNearestPeriodId(new AsyncCallback<Long>() {

							@Override
							public void onSuccess(Long result) {
								draftPeriodId = result;
							}

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Ближайшее расписание не составлено");
							}
						});
				Timer timer = new Timer() {
					private int count;

					@Override
					public void run() {
						if (count < 10) {
							if (draftPeriodId != 0) {
								cancel();
								try {
									MainAbsolutePanel.remove(0);
									CopyOfScheduleDraft cpschdrft = new CopyOfScheduleDraft(
											draftPeriodId);
									MainAbsolutePanel.add(cpschdrft);
								} catch (Exception ex) {
									CopyOfScheduleDraft cpschdrft = new CopyOfScheduleDraft(
											draftPeriodId);
									MainAbsolutePanel.add(cpschdrft);
								}
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

		}, ClickEvent.getType());
		

	}

	private void callDialogBox(SimplePanel sp) {
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setWidth("100%");
		dialogBox.setPopupPosition(498,53);
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Закрыть");
		closeButton.getElement().setId("closeButton");
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});
		VerticalPanel vp = new VerticalPanel();
		vp.setSize("100%", "100%");
		vp.add(sp);
		vp.add(closeButton);
		dialogBox.add(vp);
		dialogBox.show();

	}
	private void showDraft(AbsolutePanel absolutePanel, FlexTable mainTable, ClickEvent event) {
		try {
			SC.say("Запущен режим черновика");
			absolutePanel.remove(0);
			CopyOfScheduleDraft cpschdrft = new CopyOfScheduleDraft(
					mainTable.getRowCount()
					- mainTable.getCellForEvent(event).getRowIndex());
			absolutePanel.add(cpschdrft);
		} catch (Exception ex) {
			SC.say("Запущен режим черновика");
			CopyOfScheduleDraft cpschdrft = new CopyOfScheduleDraft(
					mainTable.getRowCount()
					- mainTable.getCellForEvent(event).getRowIndex());
			absolutePanel.add(cpschdrft);
		}
	}
}