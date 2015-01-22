
package ua.nure.ostpc.malibu.shedule.client;

import java.util.ArrayList;
import java.util.List;

import ua.nure.ostpc.malibu.shedule.Path;
import ua.nure.ostpc.malibu.shedule.client.event.DoDraftEvent;
import ua.nure.ostpc.malibu.shedule.client.event.DoDraftHandler;
import ua.nure.ostpc.malibu.shedule.client.event.DoEditEvent;
import ua.nure.ostpc.malibu.shedule.client.event.DoEditHandler;
import ua.nure.ostpc.malibu.shedule.client.event.DoManageEvent;
import ua.nure.ostpc.malibu.shedule.client.event.DoManageHandler;
import ua.nure.ostpc.malibu.shedule.client.event.DoSettingsEvent;
import ua.nure.ostpc.malibu.shedule.client.event.DoSettingsHandler;
import ua.nure.ostpc.malibu.shedule.client.event.DoViewEvent;
import ua.nure.ostpc.malibu.shedule.client.event.DoViewHandler;
import ua.nure.ostpc.malibu.shedule.client.manage.ManagerModule;
import ua.nure.ostpc.malibu.shedule.client.module.ModulePanelItem;
import ua.nure.ostpc.malibu.shedule.client.panel.editing.ScheduleEditingPanel;
import ua.nure.ostpc.malibu.shedule.client.panel.editing.ScheduleEditingPanel.Mode;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Right;
import ua.nure.ostpc.malibu.shedule.entity.Role;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;

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
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.util.SC;

public class ScheduleManagerEntryPoint implements EntryPoint, 
				DoViewHandler, DoDraftHandler, DoManageHandler, 
				DoEditHandler, DoSettingsHandler {
	private String currentPanelName;

	private AbsolutePanel mainPanel;
	private DockPanel logoutPanel = new DockPanel();
	private static RootPanel moduleContent;

	//private SessionInvalidationDetector sessionInvalidationDetector = new SessionInvalidationDetector();

	public void onModuleLoad() {
		AppState.eventBus.addHandler(DoViewEvent.TYPE, this);
		AppState.eventBus.addHandler(DoDraftEvent.TYPE, this);
		AppState.eventBus.addHandler(DoManageEvent.TYPE, this);
		AppState.eventBus.addHandler(DoEditEvent.TYPE, this);
		AppState.eventBus.addHandler(DoSettingsEvent.TYPE, this);

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
					if (AppState.periodList != null && AppState.scheduleStatusMap != null
							&& AppState.employeeName != null) {
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
				for (Long periodId : AppState.lockingPeriodIdSet) {
					AppState.scheduleManagerService.unlockSchedule(periodId,
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
		AppState.scheduleManagerService.getAllPeriods(new AsyncCallback<List<Period>>() {

			@Override
			public void onSuccess(List<Period> result) {
				if (result != null) {
					AppState.periodList = result;
				} else {
					AppState.periodList = new ArrayList<Period>();
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				SC.say("Проблемы с сервером, пожалуйста обратитесь к системному администратору \n Код ошибки 2");
			}
		});
	}

	private void getResponsible() {
		AppState.scheduleManagerService.userRoles(new AsyncCallback<List<Role>>() {

			@Override
			public void onFailure(Throwable caught) {

			}

			@Override
			public void onSuccess(List<Role> result) {
				AppState.roles = result;
				for (Role role : AppState.roles) {
					if (role.getRight() == Right.RESPONSIBLE_PERSON) {
						AppState.isResponsible = true;
					}

				}
			}
		});
	}

	private void getEmployeeNameFromServer() {
		AppState.scheduleDraft.getEmployee(new AsyncCallback<Employee>() {

			@Override
			public void onSuccess(Employee result) {
				AppState.employeeName = result.getFirstName() + " "
						+ result.getLastName();
			}

			@Override
			public void onFailure(Throwable caught) {
				SC.say(caught.getMessage());
			}
		});
	}

	private void drawPage() {
		RootPanel rootPanel = RootPanel.get("nameFieldContainer");
		rootPanel.setSize("100%", "100%");

		DockPanel globalPanel = new DockPanel();
		globalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		globalPanel.setSize("100%", "100%");
		rootPanel.add(globalPanel, 0, 0);

		drawGlobalTopPanel(globalPanel);
		drawModulePanel();
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

		InlineLabel employeeNameLabel = new InlineLabel(AppState.employeeName);
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

	private void drawModulePanel() {
//		VerticalPanel globalModulePanel = new VerticalPanel();
//		globalModulePanel.setStyleName("westPanelNap");
//		globalModulePanel.setSize("100%", "100%");
//		globalPanel.add(globalModulePanel, DockPanel.WEST);
//		globalPanel.setCellHeight(globalModulePanel, "100%");
//		globalPanel.setCellWidth(globalModulePanel, "18%");

		RootPanel globalModulePanel = RootPanel.get("moduleItemsContainer");
		VerticalPanel modulePanel = new VerticalPanel();
		modulePanel.setSize("100%", "100%");
		globalModulePanel.add(modulePanel);
		
		ModulePanelItem item = new ModulePanelItem("Просмотр текущего", 
				GWT.getHostPageBaseURL()+ "img/33.png", 
				true);
		modulePanel.add(item.getPanel());
		item.addHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doView();
			}
		}, ClickEvent.getType());

		item = new ModulePanelItem("Черновик", 
				GWT.getHostPageBaseURL()+ "img/47.png", 
				true);
		modulePanel.add(item.getPanel());
		item.addHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doDraft();
			}
		}, ClickEvent.getType());
		
		item = new ModulePanelItem("Управление графиками работ", 
				GWT.getHostPageBaseURL()+ "img/91.png", 
				true);
		modulePanel.add(item.getPanel());
		item.addHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doManage();
			}
		}, ClickEvent.getType());
		
		if (AppState.isResponsible) {
			item = new ModulePanelItem("Создать новый",
					GWT.getHostPageBaseURL() + "img/15.png", true);
			modulePanel.add(item.getPanel());
			item.addHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					doNew();
				}
			}, ClickEvent.getType());

			item = new ModulePanelItem("Настройки", 
					GWT.getHostPageBaseURL()+ "img/44.png", true);
			modulePanel.add(item.getPanel());
			item.addHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					doSettings();
				}
			}, ClickEvent.getType());

		}

		SimplePanel emptyPanel = new SimplePanel();
		emptyPanel.setStyleName("gwt-StackPanelItem");
		modulePanel.add(emptyPanel);
		emptyPanel.addStyleName("");

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
			AppState.lockingPeriodIdSet.add(periodId);
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
				AppState.scheduleManagerService.unlockSchedule(editPanel.getPeriodId(),
						new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								AppState.lockingPeriodIdSet.remove(editPanel
										.getPeriodId());
							}

							@Override
							public void onFailure(Throwable caught) {
							}
						});
			}
		}
		mainPanel.clear();
		currentPanelName = null;
	}

	private void clearTopLinePanel() {
		AbsolutePanel topLinePanel = RootPanel.get("topGreyLine");
		if (topLinePanel != null) {
			topLinePanel.clear();
		}
	}

	private class SessionInvalidationDetector {
		private boolean isContains;

		private SessionInvalidationDetector() {
			isContains = true;
			Timer timer = new Timer() {

				@Override
				public void run() {
					checkUserInSession();
					if (!isContains) {
						SC.warn("Сессия завершена!");
						cancel();
						Window.Location.replace(GWT.getHostPageBaseURL()
								+ Path.COMMAND__LOGIN);
					}
				}
			};
			timer.scheduleRepeating(11000);
		}

		private void checkUserInSession() {
			AppState.scheduleManagerService
					.containsUserInSession(new AsyncCallback<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {
							isContains = result;
						}

						@Override
						public void onFailure(Throwable caught) {
							isContains = false;
							SC.warn("Невозможно получить информацию о сессии с сервера!");
						}
					});
		}

	}

	private void doView() {
		getCurrentSchedule();
	}
	
	private void doView(Long id) {
		if (id == null) {
			getCurrentSchedule();
		} else {
			if (moduleContent == null) 
				moduleContent = RootPanel.get("moduleContentContainer");
			
			clearMainViewPanel();
			moduleContent.add(new ScheduleEditingPanel(Mode.VIEW, id));
		}
	}
	
	private void getCurrentSchedule() {
		AppState.scheduleManagerService.getCurrentSchedule(
				new AsyncCallback<Schedule>() {

					@Override
					public void onSuccess(Schedule result) {
						if (result != null) {
							doView(result.getPeriod().getPeriodId());
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						SC.say("");
					}
		});
	}

	@Override
	public void onView(DoViewEvent event) {
		doView(event.getId());
	}
	
	private void doDraft() {
		getCurrentSchedule();
	}
	
	private void doDraft(Long id) {
		if (id == null) {
			getFirstDraftPeriodId();
		} else {
			if (moduleContent == null) 
				moduleContent = RootPanel.get("moduleContentContainer");
			
			clearMainViewPanel();
			moduleContent.add(new ScheduleEditingPanel(Mode.VIEW, id));
		}
	}
	
	private void getFirstDraftPeriodId() {
		AppState.scheduleManagerService.getFirstDraftPeriod(
				new AsyncCallback<Long>() {
					
					@Override
					public void onSuccess(Long result) {
						if (result != null) {
							doDraft(result);
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						SC.say("");
					}
				});
	}
	
	@Override
	public void onDraft(DoDraftEvent event) {
		doDraft(event.getId());
	}
	
	public void doManage() {
		if (moduleContent == null) 
			moduleContent = RootPanel.get("moduleContentContainer");
		
		clearMainViewPanel();
		moduleContent.add(ManagerModule.getPanel());
	}
	
	@Override
	public void onManage(DoManageEvent event) {
		doManage();
	}

	public void doNew() {
		if (moduleContent == null) 
			moduleContent = RootPanel.get("moduleContentContainer");
		
		clearMainViewPanel();
		moduleContent.add(new ScheduleEditingPanel());
	}

	@Override
	public void onEdit(DoEditEvent event) {
		if (moduleContent == null) 
			moduleContent = RootPanel.get("moduleContentContainer");
		
		clearMainViewPanel();
		moduleContent.add(new ScheduleEditingPanel(Mode.EDITING, event.getId()));
	}

	public void doSettings() {
		if (moduleContent == null) 
			moduleContent = RootPanel.get("moduleContentContainer");
		
		clearMainViewPanel();
		moduleContent.add(new StartSettingEntryPoint());
	}
	
	@Override
	public void onSettings(DoSettingsEvent event) {
		doSettings();
	}
}
