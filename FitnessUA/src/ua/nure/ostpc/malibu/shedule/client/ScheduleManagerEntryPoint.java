package ua.nure.ostpc.malibu.shedule.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.Path;
import ua.nure.ostpc.malibu.shedule.client.panel.editing.ScheduleEditingPanel;
import ua.nure.ostpc.malibu.shedule.client.panel.editing.ScheduleEditingPanel.Mode;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Right;
import ua.nure.ostpc.malibu.shedule.entity.Role;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;

import com.smartgwt.client.util.SC;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
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
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
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
	public String currentStatus;
	private List<Role> roles = null;
	private Boolean isResponsible = false;
	private long draftPeriodId;
	private Set<Long> lockingPeriodIdSet = new HashSet<Long>();
	private String currentPanelName;

	private AbsolutePanel mainViewPanel;

	public void onModuleLoad() {

		Window.addCloseHandler(new CloseHandler<Window>() {

			@Override
			public void onClose(CloseEvent<Window> event) {
				for (Long periodId : lockingPeriodIdSet) {
					scheduleManagerService.unlockSchedule(periodId,
							new AsyncCallback<Void>() {

								@Override
								public void onFailure(Throwable caught) {
								}

								@Override
								public void onSuccess(Void result) {
								}

							});
				}
			}
		});

		getAllPeriodsFromServer();
		getScheduleStatusMapFromServer();
		getEmployeeSurname();
		getResponsible();
		Timer timer = new Timer() {
			private int count;

			@Override
			public void run() {
				if (count < 20) {
					if (periodList != null && scheduleStatusMap != null
							&& employee != null) {
						cancel();
						drawPrimaryPage();
						// drawPage();
					}
					count++;
				} else {
					SC.say("Проблемы с сервером, пожалуйста обратитесь к системному администратору.\n Код ошибки 1");
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
				SC.say("Проблемы с сервером, пожалуйста обратитесь к системному администратору \n Код ошибки 2");
			}
		});
	}

	private void getResponsible() {
		scheduleManagerService.userRoles(new AsyncCallback<List<Role>>() {

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
						SC.say("Проблемы с сервером, пожалуйста обратитесь к системному администратору \n Код ошибки 3");
					}
				});
	}

	private void drawPage() {

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
		for (int i = 0; i < 7; i++) {
			mainTable.getCellFormatter().setStyleName(0, i, "secondHeader");
			mainTable.getCellFormatter().setStyleName(0, i, "mainHeader");
		}

		int index = 1;

		for (final Period period : periodList) {
			mainTable.insertRow(index);
			for (int i = 0; i < 7; i++)
				mainTable.insertCell(index, i);
			mainTable.setText(index, 0, String.valueOf(index - 1));
			HorizontalPanel panel = new HorizontalPanel();
			Image button1 = new Image(GWT.getHostPageBaseURL() + "img/"
					+ scheduleStatusMap.get(period.getPeriodId()) + ".png");
			button1.setStyleName("myImageAsButton");
			button1.setTitle(String.valueOf(index));

			// button1.setIcon(GWT.getHostPageBaseURL()+"img/"
			// + scheduleStatusMap.get(period.getPeriodId()) + ".png");
			String string = scheduleStatusMap.get(period.getPeriodId())
					.toString();
			if (string.equals("DRAFT")) {
				currentStatus = "Черновик";
			} else if (string.equals("CURRENT")) {
				currentStatus = "Текущий";
			} else if (string.equals("CLOSED")) {
				currentStatus = "Закрыт";
			} else if (string.equals("FUTURE")) {
				currentStatus = "Будущий";
			}
			button1.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					SC.say("Статус графика работ: " + currentStatus);
				}
			});
			panel.add(button1);
			panel.add(new Label(currentStatus));
			mainTable.setWidget(index, 1, panel);
			mainTable.setText(index, 2, period.getStartDate().toString());
			mainTable.setText(index, 3, period.getEndDate().toString());

			Image scheduleViewButton = new Image(GWT.getHostPageBaseURL()
					+ "img/view_icon.png");
			// scheduleDisplayButton.setSize("18", "18");
			scheduleViewButton.setStyleName("myImageAsButton");
			scheduleViewButton.setTitle(String.valueOf(index));

			scheduleViewButton.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					clearPanels();
					ScheduleEditingPanel viewPanel = new ScheduleEditingPanel(
							Mode.VIEW, period.getPeriodId());
					addToMainViewPanel(viewPanel,
							ScheduleEditingPanel.class.getName());
				}

			});

			mainTable.setWidget(index, 4, scheduleViewButton);

			Image scheduleEditButton = new Image(GWT.getHostPageBaseURL()
					+ "img/file_edit.png");
			// scheduleEditButton.setSize("18", "18");
			scheduleEditButton.setTitle(String.valueOf(index));
			scheduleEditButton.setStyleName("myImageAsButton");

			scheduleEditButton.addClickHandler(new ClickHandler() {

				public void onClick(final ClickEvent event) {
					if (isResponsible == true) {

						scheduleManagerService.lockSchedule(
								period.getPeriodId(),
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
										SC.say("Невозможно пулучить данные с сервера!");
									}

								});

					} else {
						showDraft(mainTable, period.getPeriodId());
					}
				}
			});

			mainTable.setWidget(index, 5, scheduleEditButton);

			final Image button4 = new Image(GWT.getHostPageBaseURL()
					+ "img/mail_send.png");
			// button4.setSize("18", "18");
			button4.setStyleName("myImageAsButton");
			button4.setTitle(String.valueOf(index));
			// button4.setIcon(GWT.getHostPageBaseURL()+"img/mail_send.png");
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
		addToMainViewPanel(mainTable, ScheduleManagerEntryPoint.class.getName());
		// listGrid.draw();
	}

	private void drawLogoPanel(HorizontalPanel horizontalPanel) {
		AbsolutePanel logoPanel = new AbsolutePanel();
		logoPanel.setStyleName("NapLogo");
		horizontalPanel.add(logoPanel);
		logoPanel.setSize("100%", "100%");
		horizontalPanel.setCellHeight(logoPanel, "100%");
		horizontalPanel.setCellWidth(logoPanel, "18%");
		Image logoImage = new Image(GWT.getHostPageBaseURL() + "img/1_01.png");
		logoPanel.add(logoImage, 0, 0);
		logoImage.setSize("100%", "100%");
	}

	private void drawLogoutPanel(AbsolutePanel userPanel) {
		DockPanel logoutPanel = new DockPanel();
		logoutPanel.setSize("100%", "100%");
		userPanel.add(logoutPanel);

		final SubmitButton logoutButton = new SubmitButton("Выйти");
		logoutButton.setSize("100%", "100%");

		final FormPanel logoutFormPanel = new FormPanel();
		logoutFormPanel.setStyleName("logoutPanel");
		logoutFormPanel.setWidth("100px");
		logoutFormPanel.add(logoutButton);
		logoutFormPanel.setMethod(FormPanel.METHOD_POST);
		logoutFormPanel.setAction(GWT.getHostPageBaseURL()
				+ Path.COMMAND__LOGOUT);
		logoutPanel.add(logoutFormPanel, DockPanel.EAST);
		logoutPanel.setCellVerticalAlignment(logoutFormPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		logoutPanel.setCellHorizontalAlignment(logoutFormPanel,
				HasHorizontalAlignment.ALIGN_CENTER);

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
						Window.Location.replace(GWT.getHostPageBaseURL()
								+ Path.COMMAND__LOGIN);
					}
				});

		Button editProfileButton = new Button("Редактировать профиль");
		editProfileButton.setWidth("110px");
		logoutPanel.add(editProfileButton, DockPanel.WEST);
		logoutPanel.setCellVerticalAlignment(editProfileButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		logoutPanel.setCellHorizontalAlignment(editProfileButton,
				HasHorizontalAlignment.ALIGN_CENTER);

		editProfileButton
				.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {

					@Override
					public void onClick(
							com.google.gwt.event.dom.client.ClickEvent event) {
						UserSettingSimplePanel sp = new UserSettingSimplePanel();
						callDialogBox(sp);
					}
				});
	}

	private void drawPrimaryPage() {
		RootPanel rootPanel = RootPanel.get("nameFieldContainer");
		rootPanel.setSize("100%", "100%");

		DockPanel globalPanel = new DockPanel();
		globalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		globalPanel.setSize("100%", "100%");
		rootPanel.add(globalPanel, 0, 0);

		AbsolutePanel globalTopPanel = new AbsolutePanel();
		globalTopPanel.setStyleName("megaKostil");
		globalPanel.add(globalTopPanel, DockPanel.NORTH);
		globalTopPanel.setSize("100%", "");
		globalPanel.setCellHeight(globalTopPanel, "10%");
		globalPanel.setCellWidth(globalTopPanel, "auto");

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		globalTopPanel.add(horizontalPanel, 0, 0);
		horizontalPanel.setSize("100%", "100%");

		drawLogoPanel(horizontalPanel);

		AbsolutePanel topPanel = new AbsolutePanel();
		topPanel.setStyleName("megaKostil");
		horizontalPanel.add(topPanel);
		horizontalPanel.setCellHorizontalAlignment(topPanel,
				HasHorizontalAlignment.ALIGN_RIGHT);
		topPanel.setSize("100%", "100%");

		DockPanel dockPanel_1 = new DockPanel();
		topPanel.add(dockPanel_1);
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

		AbsolutePanel userImagePanel = new AbsolutePanel();
		userImagePanel.setStyleName("megaKostil");
		horizontalPanel_6.add(userImagePanel);
		horizontalPanel_6.setCellHeight(userImagePanel, "100%");
		horizontalPanel_6.setCellWidth(userImagePanel, "20%");
		horizontalPanel_6.setCellVerticalAlignment(userImagePanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_6.setCellHorizontalAlignment(userImagePanel,
				HasHorizontalAlignment.ALIGN_CENTER);
		userImagePanel.setSize("100%", "100%");

		Image userImage = new Image(GWT.getHostPageBaseURL() + "img/user.png");
		userImage.setStyleName("NapLogo");
		userImage.setSize("64px", "62px");
		userImagePanel.add(userImage);

		final AbsolutePanel userPanel = new AbsolutePanel();
		userPanel.setStyleName("myBestUserPanel");
		userPanel.setSize("280px", "100px");
		userPanel.setVisible(false);
		userImagePanel.add(userPanel);

		drawLogoutPanel(userPanel);

		AbsolutePanel absolutePanel_8 = new AbsolutePanel();
		dockPanel_1.add(absolutePanel_8, DockPanel.CENTER);
		dockPanel_1.setCellWidth(absolutePanel_8, "60%");
		absolutePanel_8.setSize("100%", "100%");

		AbsolutePanel absolutePanel_1 = new AbsolutePanel();
		absolutePanel_1.setStyleName("westPanelNap");
		globalPanel.add(absolutePanel_1, DockPanel.WEST);
		absolutePanel_1.setSize("100%", "100%");
		globalPanel.setCellHeight(absolutePanel_1, "100%");
		globalPanel.setCellWidth(absolutePanel_1, "18%");

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

		HorizontalPanel currentScheduleViewPanel = new HorizontalPanel();
		currentScheduleViewPanel.setStyleName("horizontalPanelForLeft");
		currentScheduleViewPanel
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		lookingNearest.add(currentScheduleViewPanel);
		currentScheduleViewPanel.setSize("100%", "100%");
		Image image_1 = new Image(GWT.getHostPageBaseURL() + "img/33.png");
		currentScheduleViewPanel.add(image_1);
		currentScheduleViewPanel.setCellHorizontalAlignment(image_1,
				HasHorizontalAlignment.ALIGN_CENTER);
		currentScheduleViewPanel.setCellHeight(image_1, "100%");
		currentScheduleViewPanel.setCellWidth(image_1, "30%");
		image_1.setSize("32px", "32px");

		InlineLabel inlineLabel_3 = new InlineLabel("Просмотр текущего");
		inlineLabel_3.setStyleName("leftLabels");
		currentScheduleViewPanel.add(inlineLabel_3);
		currentScheduleViewPanel.setCellHeight(inlineLabel_3, "10%");
		currentScheduleViewPanel.setCellWidth(inlineLabel_3, "100%");
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

		Image image_2 = new Image(GWT.getHostPageBaseURL() + "img/47.png");
		horizontalPanel_2.add(image_2);
		horizontalPanel_2.setCellHorizontalAlignment(image_2,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2.setCellWidth(image_2, "30%");
		image_2.setSize("32px", "32px");

		InlineLabel inlineLabel_2 = new InlineLabel("Черновик");
		inlineLabel_2.setStyleName("leftLabels");
		horizontalPanel_2.add(inlineLabel_2);
		horizontalPanel_2.setCellHeight(inlineLabel_2, "Черновик");
		inlineLabel_2.setSize("100%", "100%");
		AbsolutePanel absolutePanel_6 = new AbsolutePanel();
		absolutePanel_6.setStyleName("gwt-StackPanelItem");
		verticalPanel.add(absolutePanel_6);
		absolutePanel_6.setSize("100%", "100%");

		AbsolutePanel absolutePanel_2 = new AbsolutePanel();
		globalPanel.add(absolutePanel_2, DockPanel.CENTER);
		absolutePanel_2.setSize("100%", "100%");

		VerticalPanel verticalPanel_1 = new VerticalPanel();
		verticalPanel_1.setStyleName("CentralPanelGraphique");
		absolutePanel_2.add(verticalPanel_1);
		verticalPanel_1.setSize("100%", "100%");

		AbsolutePanel topLinePanel = new AbsolutePanel();
		verticalPanel_1.add(topLinePanel);
		verticalPanel_1.setCellHeight(topLinePanel, "10%");
		verticalPanel_1.setCellWidth(topLinePanel, "100%");
		topLinePanel.getElement().setId("topGreyLine");
		topLinePanel.setStyleName("topGreyLine");

		mainViewPanel = new AbsolutePanel();
		mainViewPanel.setStyleName("KutuzoffPanel");
		verticalPanel_1.add(mainViewPanel);
		verticalPanel_1.setCellHeight(mainViewPanel, "100%");
		verticalPanel_1.setCellWidth(mainViewPanel, "100%");
		mainViewPanel.setSize("100%", "100%");
		if (isResponsible) {
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

			Image image_3 = new Image(GWT.getHostPageBaseURL() + "img/91.png");
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

			Image image_4 = new Image(GWT.getHostPageBaseURL() + "img/15.png");
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

			Image image_5 = new Image(GWT.getHostPageBaseURL() + "img/44.png");
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
						clearPanels();
						drawPage();
					} catch (Exception ex) {
						drawPage();
					}
				}

			}, ClickEvent.getType());
			horizontalPanel_4.sinkEvents(Event.ONCLICK);
			horizontalPanel_4.addHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					try {
						clearPanels();
						ScheduleEditingPanel cpschdrft = new ScheduleEditingPanel();
						addToMainViewPanel(cpschdrft,
								ScheduleEditingPanel.class.getName());
					} catch (Exception ex) {
						ScheduleEditingPanel cpschdrft = new ScheduleEditingPanel();
						addToMainViewPanel(cpschdrft,
								ScheduleEditingPanel.class.getName());
					}
				}

			}, ClickEvent.getType());
			horizontalPanel_5.sinkEvents(Event.ONCLICK);
			horizontalPanel_5.addHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					try {
						clearPanels();
						StartSettingEntryPoint cpschdrft = new StartSettingEntryPoint();
						addToMainViewPanel(cpschdrft,
								StartSettingEntryPoint.class.getName());
					} catch (Exception ex) {
						StartSettingEntryPoint cpschdrft = new StartSettingEntryPoint();
						addToMainViewPanel(cpschdrft,
								StartSettingEntryPoint.class.getName());
					}
				}

			}, ClickEvent.getType());
		}

		userImage.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!userPanel.isVisible())
					userPanel.setVisible(true);
				else
					userPanel.setVisible(false);
			}
		});

		currentScheduleViewPanel.sinkEvents(Event.ONCLICK);
		currentScheduleViewPanel.addHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				clearPanels();
				scheduleManagerService
						.getCurrentSchedule(new AsyncCallback<Schedule>() {

							@Override
							public void onSuccess(Schedule result) {
								if (result != null) {
									ScheduleEditingPanel viewPanel = new ScheduleEditingPanel(
											Mode.VIEW, result.getPeriod()
													.getPeriodId());
									addToMainViewPanel(viewPanel,
											ScheduleEditingPanel.class
													.getName());
								} else {
									SC.warn("Текущего графика работы не существует!");
								}
							}

							@Override
							public void onFailure(Throwable caught) {
								SC.warn("Возникли проблемы с сервером, обратитесь к системному администратору");
							}
						});
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
								SC.say("Ближайшее расписание не составлено. \n Код ошибки 4");
							}
						});
				Timer timer = new Timer() {
					private int count;

					@Override
					public void run() {
						if (count < 20) {
							if (draftPeriodId != 0) {
								cancel();
								try {
									clearPanels();
									CopyOfScheduleDraft cpschdrft = new CopyOfScheduleDraft(
											draftPeriodId);
									addToMainViewPanel(cpschdrft,
											CopyOfScheduleDraft.class.getName());
								} catch (Exception ex) {
									CopyOfScheduleDraft cpschdrft = new CopyOfScheduleDraft(
											draftPeriodId);
									addToMainViewPanel(cpschdrft,
											CopyOfScheduleDraft.class.getName());
								}
							}
							count++;
						} else {
							SC.say("Проблемы с сервером, пожалуйста обратитесь к системному администратору \n Код ошибки 5");
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
		// dialogBox.setPopupPosition(498,53);
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
		dialogBox.center();

	}

	private void showDraft(FlexTable mainTable, long periodId) {
		try {
			SC.say("Запущен режим черновика");
			clearPanels();
			CopyOfScheduleDraft cpschdrft = new CopyOfScheduleDraft(periodId);
			addToMainViewPanel(cpschdrft, CopyOfScheduleDraft.class.getName());
		} catch (Exception ex) {
			SC.say("Запущен режим черновика");
			CopyOfScheduleDraft cpschdrft = new CopyOfScheduleDraft(periodId);
			addToMainViewPanel(cpschdrft, CopyOfScheduleDraft.class.getName());
		}
	}

	private void drawScheduleForResponsiblePerson(long periodId) {
		try {
			clearPanels();
			ScheduleEditingPanel editPanel = new ScheduleEditingPanel(
					Mode.EDITING, periodId);
			addToMainViewPanel(editPanel, ScheduleEditingPanel.class.getName());
			lockingPeriodIdSet.add(periodId);
		} catch (Exception ex) {
			SC.say("Ошибка отображения графика работы!");
		}
	}

	private void addToMainViewPanel(Widget widget, String panelName) {
		mainViewPanel.add(widget);
		this.currentPanelName = panelName;
	}

	private void clearPanels() {
		clearMainViewPanel();
		clearTopLinePanel();
	}

	private void clearMainViewPanel() {
		if (currentPanelName != null
				&& currentPanelName
						.equals(ScheduleEditingPanel.class.getName())) {
			final ScheduleEditingPanel editPanel = (ScheduleEditingPanel) mainViewPanel
					.getWidget(0);
			if (editPanel.getMode() == Mode.EDITING) {
				scheduleManagerService.unlockSchedule(editPanel.getPeriodId(),
						new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								lockingPeriodIdSet.remove(editPanel
										.getPeriodId());
							}

							@Override
							public void onFailure(Throwable caught) {
							}
						});
			}
		}
		mainViewPanel.remove(0);
		currentPanelName = null;
	}

	private void clearTopLinePanel() {
		AbsolutePanel topLinePanel = RootPanel.get("topGreyLine");
		if (topLinePanel != null) {
			topLinePanel.clear();
		}
	}
}
