package ua.nure.ostpc.malibu.shedule.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScheduleManagerEntryPoint implements EntryPoint {
	private final ScheduleManagerServiceAsync scheduleManagerService = GWT
			.create(ScheduleManagerService.class);
	private final ScheduleDraftServiceAsync scheduleDraft = GWT
			.create(ScheduleDraftService.class);

	private String employeeName;
	private List<Period> periodList;
	private Map<Long, Status> scheduleStatusMap;
	// private String currentStatus;
	private List<Role> roles = null;
	private Boolean isResponsible = false;
	private long draftPeriodId;
	private Set<Long> lockingPeriodIdSet = new HashSet<Long>();
	private String currentPanelName;

	private AbsolutePanel mainPanel;
	private DockPanel logoutPanel = new DockPanel();

	private static Map<String, String> statusTranslationMap = new HashMap<String, String>();

	static {
		statusTranslationMap.put(Status.CLOSED.toString(), "Закрыт");
		statusTranslationMap.put(Status.CURRENT.toString(), "Текущий");
		statusTranslationMap.put(Status.DRAFT.toString(), "Черновик");
		statusTranslationMap.put(Status.FUTURE.toString(), "Будущий");
	}

	public void onModuleLoad() {
		setWindowCloseHandler();
		getAllPeriodsFromServer();
		getScheduleStatusMapFromServer();
		getEmployeeNameFromServer();
		getResponsible();
		Timer timer = new Timer() {
			private int count;

			@Override
			public void run() {
				if (count < 20) {
					if (periodList != null && scheduleStatusMap != null
							&& employeeName != null) {
						cancel();
						drawPage();
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

	private void setWindowCloseHandler() {
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

	private void getEmployeeNameFromServer() {
		scheduleDraft.getEmployee(new AsyncCallback<Employee>() {

			@Override
			public void onSuccess(Employee result) {
				employeeName = result.getFirstName() + " "
						+ result.getLastName();

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

	private void drawScheduleManagerTable() {
		final FlexTable scheduleManagerTable = new FlexTable();
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

		Collections.sort(periodList, new Comparator<Period>() {

			@Override
			public int compare(Period lhs, Period rhs) {
				return lhs.getStartDate().compareTo(rhs.getStartDate());
			}
		});

		for (final Period period : periodList) {
			long periodId = period.getPeriodId();
			scheduleManagerTable.insertRow(index);
			for (int i = 0; i < 7; i++) {
				scheduleManagerTable.insertCell(index, i);
			}
			scheduleManagerTable.setText(index, 0, String.valueOf(periodId));
			HorizontalPanel scheduleStatusPanel = new HorizontalPanel();
			Image scheduleStatusImage = new Image(GWT.getHostPageBaseURL()
					+ "img/" + scheduleStatusMap.get(period.getPeriodId())
					+ ".png");
			scheduleStatusImage.setStyleName("myImageAsButton");
			scheduleStatusImage.setTitle(String.valueOf(periodId));

			String scheduleStatusString = scheduleStatusMap.get(periodId)
					.toString();
			String scheduleStatus = statusTranslationMap
					.get(scheduleStatusString);

			scheduleStatusImage.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					Image image = (Image) event.getSource();
					String title = image.getTitle();
					String scheduleStatus = null;
					if (title != null) {
						long periodId = Long.parseLong(title);
						String scheduleStatusString = scheduleStatusMap.get(
								periodId).toString();
						scheduleStatus = statusTranslationMap
								.get(scheduleStatusString);
					}
					SC.say("Статус графика работ: " + scheduleStatus);
				}
			});

			scheduleStatusPanel.add(scheduleStatusImage);
			scheduleStatusPanel.add(new Label(scheduleStatus));
			scheduleManagerTable.setWidget(index, 1, scheduleStatusPanel);
			scheduleManagerTable.setText(index, 2, period.getStartDate()
					.toString());
			scheduleManagerTable.setText(index, 3, period.getEndDate()
					.toString());

			Image scheduleViewButton = new Image(GWT.getHostPageBaseURL()
					+ "img/view_icon.png");
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

			scheduleManagerTable.setWidget(index, 4, scheduleViewButton);

			Image scheduleEditButton = new Image(GWT.getHostPageBaseURL()
					+ "img/file_edit.png");
			scheduleEditButton.setTitle(String.valueOf(index));
			scheduleEditButton.setStyleName("myImageAsButton");

			scheduleEditButton.addClickHandler(new ClickHandler() {

				public void onClick(final ClickEvent event) {
					if (isResponsible) {
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

	private void drawPage() {
		RootPanel rootPanel = RootPanel.get("nameFieldContainer");
		rootPanel.setSize("100%", "100%");

		DockPanel globalPanel = new DockPanel();
		globalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		globalPanel.setSize("100%", "100%");
		rootPanel.add(globalPanel, 0, 0);

		drawGlobalTopPanel(globalPanel);
		drawModulePanel(globalPanel);
		drawControlPanel(globalPanel);
	}

	private void drawGlobalTopPanel(DockPanel globalPanel) {
		AbsolutePanel globalTopPanel = new AbsolutePanel();
		globalTopPanel.setStyleName("megaKostil");
		globalTopPanel.setSize("100%", "");
		globalPanel.add(globalTopPanel, DockPanel.NORTH);
		globalPanel.setCellHeight(globalTopPanel, "10%");
		globalPanel.setCellWidth(globalTopPanel, "auto");

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSize("100%", "100%");
		globalTopPanel.add(horizontalPanel, 0, 0);
		drawLogoPanel(horizontalPanel);
		drawTopPanel(horizontalPanel);
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

	private void drawTopPanel(HorizontalPanel horizontalPanel) {
		AbsolutePanel topPanel = new AbsolutePanel();
		topPanel.setStyleName("megaKostil");
		horizontalPanel.add(topPanel);
		horizontalPanel.setCellHorizontalAlignment(topPanel,
				HasHorizontalAlignment.ALIGN_RIGHT);
		topPanel.setSize("100%", "100%");

		drawUserPanel(topPanel);
	}

	private void drawUserPanel(AbsolutePanel topPanel) {
		HorizontalPanel userPanel = new HorizontalPanel();
		userPanel.setStyleName("megaKostil");
		userPanel.setSize("100%", "100%");
		topPanel.add(userPanel);

		DockPanel employeeNamePanel = new DockPanel();
		employeeNamePanel.setSize("100%", "100%");
		userPanel.add(employeeNamePanel);
		userPanel.setCellHeight(employeeNamePanel, "100%");
		userPanel.setCellWidth(employeeNamePanel, "90%");

		InlineLabel employeeNameLabel = new InlineLabel(employeeName);
		employeeNamePanel.add(employeeNameLabel, DockPanel.CENTER);
		employeeNamePanel.setCellVerticalAlignment(employeeNameLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		employeeNamePanel.setCellHorizontalAlignment(employeeNameLabel,
				HasHorizontalAlignment.ALIGN_RIGHT);

		AbsolutePanel userImagePanel = new AbsolutePanel();
		userImagePanel.setStyleName("megaKostil");
		userPanel.add(userImagePanel);
		userPanel.setCellHeight(userImagePanel, "100%");
		userPanel.setCellWidth(userImagePanel, "10%");
		userPanel.setCellVerticalAlignment(userImagePanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		userPanel.setCellHorizontalAlignment(userImagePanel,
				HasHorizontalAlignment.ALIGN_CENTER);
		userImagePanel.setSize("100%", "100%");

		Image userImage = new Image(GWT.getHostPageBaseURL() + "img/user.png");
		userImage.setStyleName("NapLogo");
		userImage.setSize("64px", "62px");
		userImagePanel.add(userImage);

		userImage.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!logoutPanel.isVisible())
					logoutPanel.setVisible(true);
				else
					logoutPanel.setVisible(false);
			}
		});

		drawLogoutPanel(userImagePanel);
	}

	private void drawLogoutPanel(AbsolutePanel userImagePanel) {
		logoutPanel.setStyleName("myBestUserPanel");
		logoutPanel.setSize("280px", "100px");
		logoutPanel.setVisible(false);
		userImagePanel.add(logoutPanel);

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
						DialogBoxUtil.callDialogBox(sp);
					}
				});
	}

	private void drawModulePanel(DockPanel globalPanel) {
		AbsolutePanel globalModulePanel = new AbsolutePanel();
		globalModulePanel.setStyleName("westPanelNap");
		globalModulePanel.setSize("100%", "100%");
		globalPanel.add(globalModulePanel, DockPanel.WEST);
		globalPanel.setCellHeight(globalModulePanel, "100%");
		globalPanel.setCellWidth(globalModulePanel, "18%");

		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setSize("100%", "100%");
		globalModulePanel.add(verticalPanel);

		VerticalPanel modulePanel = new VerticalPanel();
		modulePanel.setStyleName("gwt-StackPanelItem");
		modulePanel.setSize("100%", "100%");
		verticalPanel.add(modulePanel);
		verticalPanel.setCellHeight(modulePanel, "50%");
		verticalPanel.setCellWidth(modulePanel, "100%");

		drawCurrentScheduleViewModule(modulePanel);
		drawDraftModule(modulePanel);

		AbsolutePanel emptyPanel = new AbsolutePanel();
		emptyPanel.setStyleName("gwt-StackPanelItem");
		verticalPanel.add(emptyPanel);
		emptyPanel.setSize("100%", "100%");

		if (isResponsible) {
			drawManagerModule(modulePanel);
			drawCreateScheduleModule(modulePanel);
			drawSettingsModule(modulePanel);
		}
	}

	private void drawCurrentScheduleViewModule(VerticalPanel modulePanel) {
		AbsolutePanel currentScheduleViewGlobalPanel = new AbsolutePanel();
		currentScheduleViewGlobalPanel.setStyleName("horizontalPanelForLeft");
		currentScheduleViewGlobalPanel.setSize("100%", "100%");
		modulePanel.add(currentScheduleViewGlobalPanel);
		modulePanel.setCellHeight(currentScheduleViewGlobalPanel, "10%");
		modulePanel.setCellWidth(currentScheduleViewGlobalPanel, "100%");

		HorizontalPanel currentScheduleViewPanel = new HorizontalPanel();
		currentScheduleViewPanel.setStyleName("horizontalPanelForLeft");
		currentScheduleViewPanel
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		currentScheduleViewPanel.setSize("100%", "100%");
		currentScheduleViewGlobalPanel.add(currentScheduleViewPanel);

		Image currentScheduleViewImage = new Image(GWT.getHostPageBaseURL()
				+ "img/33.png");
		currentScheduleViewPanel.add(currentScheduleViewImage);
		currentScheduleViewPanel.setCellHorizontalAlignment(
				currentScheduleViewImage, HasHorizontalAlignment.ALIGN_CENTER);
		currentScheduleViewPanel
				.setCellHeight(currentScheduleViewImage, "100%");
		currentScheduleViewPanel.setCellWidth(currentScheduleViewImage, "30%");
		currentScheduleViewImage.setSize("32px", "32px");

		InlineLabel currentScheduleViewLabel = new InlineLabel(
				"Просмотр текущего");
		currentScheduleViewLabel.setStyleName("leftLabels");
		currentScheduleViewPanel.add(currentScheduleViewLabel);
		currentScheduleViewPanel.setCellHeight(currentScheduleViewLabel, "10%");
		currentScheduleViewPanel.setCellWidth(currentScheduleViewLabel, "100%");
		currentScheduleViewLabel.setSize("100%", "100%");

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
	}

	private void drawDraftModule(VerticalPanel modulePanel) {
		AbsolutePanel draftGlobalPanel = new AbsolutePanel();
		draftGlobalPanel.setStyleName("horizontalPanelForLeft");
		draftGlobalPanel.setSize("100%", "100%");
		modulePanel.add(draftGlobalPanel);
		modulePanel.setCellHeight(draftGlobalPanel, "10%");
		modulePanel.setCellWidth(draftGlobalPanel, "100%");

		HorizontalPanel draftPanel = new HorizontalPanel();
		draftPanel.setStyleName("horizontalPanelForLeft");
		draftPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		draftPanel.setSize("100%", "100%");
		draftGlobalPanel.add(draftPanel);

		Image draftImage = new Image(GWT.getHostPageBaseURL() + "img/47.png");
		draftPanel.add(draftImage);
		draftPanel.setCellHorizontalAlignment(draftImage,
				HasHorizontalAlignment.ALIGN_CENTER);
		draftPanel.setCellWidth(draftImage, "30%");
		draftImage.setSize("32px", "32px");

		InlineLabel draftLabel = new InlineLabel("Черновик");
		draftLabel.setStyleName("leftLabels");
		draftPanel.add(draftLabel);
		draftPanel.setCellHeight(draftLabel, "10%");
		draftPanel.setCellWidth(draftLabel, "100%");
		draftLabel.setSize("100%", "100%");

		draftPanel.sinkEvents(Event.ONCLICK);
		draftPanel.addHandler(new ClickHandler() {

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

	private void drawManagerModule(VerticalPanel modulePanel) {
		AbsolutePanel managerGlobalPanel = new AbsolutePanel();
		managerGlobalPanel.setStyleName("horizontalPanelForLeft");
		modulePanel.add(managerGlobalPanel);
		managerGlobalPanel.setSize("100%", "100%");
		modulePanel.setCellHeight(managerGlobalPanel, "10%");
		modulePanel.setCellWidth(managerGlobalPanel, "100%");

		HorizontalPanel managerPanel = new HorizontalPanel();
		managerPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		managerGlobalPanel.add(managerPanel);
		managerPanel.setSize("100%", "100%");

		Image managerImage = new Image(GWT.getHostPageBaseURL() + "img/91.png");
		managerPanel.add(managerImage);
		managerPanel.setCellHorizontalAlignment(managerImage,
				HasHorizontalAlignment.ALIGN_CENTER);
		managerPanel.setCellWidth(managerImage, "30%");
		managerImage.setSize("32px", "32px");

		InlineLabel managerLabel = new InlineLabel("Управление графиками работ");
		managerLabel.setStyleName("leftLabels");
		managerPanel.add(managerLabel);
		managerPanel.setCellHeight(managerLabel, "100%");
		managerPanel.setCellWidth(managerLabel, "100%");
		managerLabel.setSize("100%", "100%");

		managerPanel.sinkEvents(Event.ONCLICK);
		managerPanel.addHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				try {
					clearPanels();
					drawScheduleManagerTable();
				} catch (Exception ex) {
					drawScheduleManagerTable();
				}
			}

		}, ClickEvent.getType());
	}

	private void drawCreateScheduleModule(VerticalPanel modulePanel) {
		AbsolutePanel createScheduleGlobalPanel = new AbsolutePanel();
		createScheduleGlobalPanel.setStyleName("horizontalPanelForLeft");
		modulePanel.add(createScheduleGlobalPanel);
		createScheduleGlobalPanel.setSize("100%", "100%");
		modulePanel.setCellHeight(createScheduleGlobalPanel, "10%");
		modulePanel.setCellWidth(createScheduleGlobalPanel, "100%");

		HorizontalPanel createSchedulePanel = new HorizontalPanel();
		createSchedulePanel
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		createScheduleGlobalPanel.add(createSchedulePanel);
		createSchedulePanel.setSize("100%", "100%");

		Image createScheduleImage = new Image(GWT.getHostPageBaseURL()
				+ "img/15.png");
		createSchedulePanel.add(createScheduleImage);
		createSchedulePanel.setCellHorizontalAlignment(createScheduleImage,
				HasHorizontalAlignment.ALIGN_CENTER);
		createSchedulePanel.setCellWidth(createScheduleImage, "30%");
		createScheduleImage.setSize("32px", "32px");

		InlineLabel createScheduleLabel = new InlineLabel("Создать новый");
		createScheduleLabel.setStyleName("leftLabels");
		createSchedulePanel.add(createScheduleLabel);
		createScheduleLabel.setSize("100%", "100%");

		createSchedulePanel.sinkEvents(Event.ONCLICK);
		createSchedulePanel.addHandler(new ClickHandler() {

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
	}

	private void drawSettingsModule(VerticalPanel modulePanel) {
		AbsolutePanel settingsGlobalPanel = new AbsolutePanel();
		settingsGlobalPanel.setStyleName("horizontalPanelForLeft");
		modulePanel.add(settingsGlobalPanel);
		settingsGlobalPanel.setSize("100%", "100%");
		modulePanel.setCellHeight(settingsGlobalPanel, "10%");
		modulePanel.setCellWidth(settingsGlobalPanel, "100%");

		HorizontalPanel settingsPanel = new HorizontalPanel();
		settingsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		settingsGlobalPanel.add(settingsPanel);
		settingsPanel.setSize("100%", "100%");

		Image settingsImage = new Image(GWT.getHostPageBaseURL() + "img/44.png");
		settingsPanel.add(settingsImage);
		settingsPanel.setCellHorizontalAlignment(settingsImage,
				HasHorizontalAlignment.ALIGN_CENTER);
		settingsPanel.setCellWidth(settingsImage, "30%");
		settingsImage.setSize("32px", "32px");

		InlineLabel settingsLabel = new InlineLabel("Настройки");
		settingsLabel.setStyleName("leftLabels");
		settingsPanel.add(settingsLabel);
		settingsPanel.setCellHeight(settingsLabel, "100%");
		settingsLabel.setSize("100%", "100%");

		settingsPanel.sinkEvents(Event.ONCLICK);
		settingsPanel.addHandler(new ClickHandler() {

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

	private void drawControlPanel(DockPanel globalPanel) {
		AbsolutePanel controlPanel = new AbsolutePanel();
		globalPanel.add(controlPanel, DockPanel.CENTER);
		controlPanel.setSize("100%", "100%");

		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setStyleName("CentralPanelGraphique");
		controlPanel.add(verticalPanel);
		verticalPanel.setSize("100%", "100%");

		drawTopLinePanel(verticalPanel);
		drawMainPanel(verticalPanel);
	}

	private void drawTopLinePanel(VerticalPanel verticalPanel) {
		AbsolutePanel topLinePanel = new AbsolutePanel();
		topLinePanel.getElement().setId("topGreyLine");
		topLinePanel.setStyleName("topGreyLine");
		verticalPanel.add(topLinePanel);
		verticalPanel.setCellHeight(topLinePanel, "10%");
		verticalPanel.setCellWidth(topLinePanel, "100%");
	}

	private void drawMainPanel(VerticalPanel verticalPanel) {
		mainPanel = new AbsolutePanel();
		mainPanel.setStyleName("KutuzoffPanel");
		verticalPanel.add(mainPanel);
		verticalPanel.setCellHeight(mainPanel, "100%");
		verticalPanel.setCellWidth(mainPanel, "100%");
		mainPanel.setSize("100%", "100%");
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
		mainPanel.add(widget);
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
			final ScheduleEditingPanel editPanel = (ScheduleEditingPanel) mainPanel
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
		mainPanel.remove(0);
		currentPanelName = null;
	}

	private void clearTopLinePanel() {
		AbsolutePanel topLinePanel = RootPanel.get("topGreyLine");
		if (topLinePanel != null) {
			topLinePanel.clear();
		}
	}
}
