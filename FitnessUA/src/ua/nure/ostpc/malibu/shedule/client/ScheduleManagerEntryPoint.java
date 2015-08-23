package ua.nure.ostpc.malibu.shedule.client;

import java.util.HashSet;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.Path;
import ua.nure.ostpc.malibu.shedule.client.draft.DraftPanel;
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
import ua.nure.ostpc.malibu.shedule.client.settings.MenuSettingsPanel;
import ua.nure.ostpc.malibu.shedule.client.settings.UserSettingSimplePanel;
import ua.nure.ostpc.malibu.shedule.entity.Right;
import ua.nure.ostpc.malibu.shedule.entity.Role;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.UserWithEmployee;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.util.SC;

public class ScheduleManagerEntryPoint implements EntryPoint, DoViewHandler,
		DoDraftHandler, DoManageHandler, DoEditHandler, DoSettingsHandler,
		ValueChangeHandler<String> {
	private ModulePanelItem viewItem;
	private ModulePanelItem draftItem;
	private ModulePanelItem manageItem;
	private ModulePanelItem createNewItem;
	private ModulePanelItem settingsItem;

	private String currentPanelName;

	/**
	 * All widgets or view must implements this if need to inform user what
	 * widget have unsaved changes
	 * 
	 * @author engsyst
	 * 
	 */
	public interface HistoryChanged {
		/**
		 * 
		 * @return true if widget have unsaved changes
		 */
		public boolean hasUnsavedChanges();
	}

	private static Set<HistoryChanged> handlers = new HashSet<ScheduleManagerEntryPoint.HistoryChanged>();

	/**
	 * Notify all registered handlers what user go from current state
	 * 
	 * @return true if one of handlers return true
	 * @see HistoryChanged
	 * @see HistoryChanged#hasUnsavedChanges()
	 */
	private boolean notifyHistoryChange() {
		boolean state = false;
		for (HistoryChanged h : handlers) {
			try {
				if (h.hasUnsavedChanges())
					state = true;
			} catch (Exception e) {
			} // Nothing TO DO
		}
		return state;
	}

	public static boolean registerHistoryChangedHandler(HistoryChanged handler) {
		if (handler != null)
			return handlers.add(handler);
		return false;
	}

	public static boolean unregisterHistoryChangedHandler(HistoryChanged handler) {
		if (handler != null)
			return handlers.remove(handler);
		return false;
	}

	public void onModuleLoad() {
		// LoadingPanel.start();
		History.addValueChangeHandler(this);
		AppState.eventBus.addHandler(DoViewEvent.TYPE, this);
		AppState.eventBus.addHandler(DoDraftEvent.TYPE, this);
		AppState.eventBus.addHandler(DoManageEvent.TYPE, this);
		AppState.eventBus.addHandler(DoEditEvent.TYPE, this);
		AppState.eventBus.addHandler(DoSettingsEvent.TYPE, this);

		setWindowCloseHandler();
		// getAllPeriodsFromServer();
		// getScheduleStatusMapFromServer();
		// getEmployeeNameFromServer();
		getUserWithEmployee();
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

	private void getUserWithEmployee() {
		AppState.scheduleManagerService
				.getUserWithEmployee(new AsyncCallback<UserWithEmployee>() {

					@Override
					public void onSuccess(UserWithEmployee result) {
						if (result != null) {
							AppState.user = result.getUser();
							AppState.employee = result.getEmployee();
							for (Role role : AppState.user.getRoles()) {
								if (role.getRight() == Right.RESPONSIBLE_PERSON) {
									AppState.isResponsible = true;
								}
							}
							drawPage();
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						SC.say(caught.getMessage());
					}
				});
	}

	private void drawPage() {
		// AppState.userNamePanel = RootPanel.get("userNamePanel");
		InlineLabel userName = new InlineLabel(AppState.employee.getLastName()
				+ " " + AppState.employee.getFirstName());

		AppState.userNamePanel.add(userName);
		userName.addStyleName("userNameLabel");

		// AppState.userIconPanel = RootPanel.get("userIconPanel");
		Image userIcon = new Image(GWT.getHostPageBaseURL() + "img/user.png");
		AppState.userIconPanel.add(userIcon);
		userIcon.addStyleName("userIcon");
		userIcon.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Image icon = (Image) event.getSource();
				AppState.logoutPanel.setPopupPosition(
						icon.getAbsoluteLeft() - 220, icon.getAbsoluteTop()
								+ icon.getHeight());
				AppState.logoutPanel.show();
			}
		});

		// AppState.moduleItemsContainer =
		// RootPanel.get("moduleItemsContainer");
		AppState.moduleItemsContainer.add(drawModulePanel());
		// AppState.moduleContentGrayPanel =
		// RootPanel.get("moduleContentGrayPanel");
		// AppState.moduleContentContainer =
		// RootPanel.get("moduleContentContainer");
		drawLogoutPanel();
		History.fireCurrentHistoryState();
		// doManage();
	}

	private void drawLogoutPanel() {
		AppState.logoutPanel = new PopupPanel(true);
		DockPanel innerLogoutPanel = new DockPanel();
		innerLogoutPanel.setStyleName("myBestUserPanel");
		innerLogoutPanel.setSize("280px", "100px");

		final SubmitButton logoutButton = new SubmitButton("Выйти");
		logoutButton.setSize("100%", "100%");

		final FormPanel logoutFormPanel = new FormPanel();
		logoutFormPanel.setStyleName("logoutPanel");
		logoutFormPanel.setWidth("100px");
		logoutFormPanel.add(logoutButton);
		logoutFormPanel.setMethod(FormPanel.METHOD_POST);
		logoutFormPanel.setAction(GWT.getHostPageBaseURL()
				+ Path.COMMAND__LOGOUT);
		innerLogoutPanel.add(logoutFormPanel, DockPanel.EAST);
		innerLogoutPanel.setCellVerticalAlignment(logoutFormPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		innerLogoutPanel.setCellHorizontalAlignment(logoutFormPanel,
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
		innerLogoutPanel.add(editProfileButton, DockPanel.WEST);
		innerLogoutPanel.setCellVerticalAlignment(editProfileButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		innerLogoutPanel.setCellHorizontalAlignment(editProfileButton,
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

		AppState.logoutPanel.add(innerLogoutPanel);
	}

	private VerticalPanel drawModulePanel() {
		VerticalPanel modulePanel = new VerticalPanel();
		modulePanel.addStyleName(StyleConstants.STYLE_MODULE_PANEL);

		viewItem = new ModulePanelItem("Просмотр текущего",
				GWT.getHostPageBaseURL() + "img/33.png", true);
		viewItem.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// History.newItem(AppConstants.HISTORY_VIEW);
				doView(null);
			}
		});
		modulePanel.add(viewItem.getPanel());

		draftItem = new ModulePanelItem("Черновик", GWT.getHostPageBaseURL()
				+ "img/47.png", true);
		draftItem.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				doDraft(null);
			}
		});
		modulePanel.add(draftItem.getPanel());

		manageItem = new ModulePanelItem("Управление графиками работ",
				GWT.getHostPageBaseURL() + "img/91.png", true);
		manageItem.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				History.newItem(AppConstants.HISTORY_MANAGE);
				// doManage();
			}
		});
		modulePanel.add(manageItem.getPanel());

		if (AppState.isResponsible) {
			createNewItem = new ModulePanelItem("Создать новый",
					GWT.getHostPageBaseURL() + "img/15.png", true);
			modulePanel.add(createNewItem.getPanel());
			createNewItem.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					History.newItem(AppConstants.HISTORY_CREATE_NEW);
					// doNew();
				}
			});

			settingsItem = new ModulePanelItem("Настройки",
					GWT.getHostPageBaseURL() + "img/44.png", true);
			settingsItem.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					History.newItem(AppConstants.HISTORY_SETTINGS);
					// doSettings();
				}
			});
			modulePanel.add(settingsItem.getPanel());
		}

		return modulePanel;
	}

	private void clearPanels() {
		clearMainViewPanel();
		clearTopLinePanel();
		viewItem.removeStyleName(StyleConstants.STYLE_CURRENT_MODULE_ITEM_PANEL);
		draftItem
				.removeStyleName(StyleConstants.STYLE_CURRENT_MODULE_ITEM_PANEL);
		manageItem
				.removeStyleName(StyleConstants.STYLE_CURRENT_MODULE_ITEM_PANEL);
		if (AppState.isResponsible) {
			createNewItem
					.removeStyleName(StyleConstants.STYLE_CURRENT_MODULE_ITEM_PANEL);
			settingsItem
					.removeStyleName(StyleConstants.STYLE_CURRENT_MODULE_ITEM_PANEL);
		}
	}

	private void clearMainViewPanel() {
		if (currentPanelName != null
				&& currentPanelName
						.equals(ScheduleEditingPanel.class.getName())) {
			final ScheduleEditingPanel editPanel = (ScheduleEditingPanel) AppState.moduleContentContainer
					.getWidget(0);
			if (editPanel.getMode() == Mode.EDITING) {
				AppState.scheduleManagerService.unlockSchedule(
						editPanel.getPeriodId(), new AsyncCallback<Void>() {

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
		AppState.moduleContentContainer.clear();
		currentPanelName = null;
	}

	private void clearTopLinePanel() {
		AppState.moduleContentGrayPanel.clear();
	}

	private void doView(Long id) {
		if (id == null) {
			getCurrentSchedule();
		} else {
			if (AppState.moduleContentContainer == null)
				AppState.moduleContentContainer = RootPanel
						.get("moduleContentContainer");

			clearPanels();
			viewItem.addStyleName(StyleConstants.STYLE_CURRENT_MODULE_ITEM_PANEL);
			currentPanelName = ScheduleEditingPanel.class.getName();
			AppState.moduleContentContainer.add(new ScheduleEditingPanel(
					Mode.VIEW, id));
		}
	}

	private void getCurrentSchedule() {
		AppState.scheduleManagerService
				.getCurrentSchedule(new AsyncCallback<Schedule>() {

					@Override
					public void onSuccess(Schedule result) {
						if (result != null) {
							History.newItem(AppConstants.HISTORY_VIEW + "-"
									+ result.getPeriod().getPeriodId());
							// doView(result.getPeriod().getPeriodId());
						} else {
							SC.say("Текущего графика работ не существует!");
							History.newItem(AppConstants.HISTORY_MANAGE);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						SC.say(caught.getMessage());
					}
				});
	}

	@Override
	public void onView(DoViewEvent event) {
		doView(event.getId());
	}

	private void doDraft(Long id) {
		if (id == null) {
			getFirstDraftPeriodId();
		} else {
			if (AppState.moduleContentContainer == null)
				AppState.moduleContentContainer = RootPanel
						.get("moduleContentContainer");

			clearPanels();
			draftItem
					.addStyleName(StyleConstants.STYLE_CURRENT_MODULE_ITEM_PANEL);
			currentPanelName = DraftPanel.class.getName();
			AppState.moduleContentContainer.add(new DraftPanel(id));
			// currentPanelName = CopyOfScheduleDraft.class.getName();
			// AppState.moduleContentContainer.add(new CopyOfScheduleDraft(id));
		}
	}

	private void getFirstDraftPeriodId() {
		AppState.scheduleManagerService
				.getFirstDraftPeriod(new AsyncCallback<Long>() {

					@Override
					public void onSuccess(Long result) {
						if (result != null) {
							History.newItem(AppConstants.HISTORY_DRAFT + "-"
									+ result);
							// doDraft(result);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						SC.say(caught.getMessage());
					}
				});
	}

	@Override
	public void onDraft(DoDraftEvent event) {
		History.newItem(AppConstants.HISTORY_DRAFT + "-" + event.getId());
		// doDraft(event.getId());
	}

	public void doManage() {
		// History.newItem(AppConstants.HISTORY_MANAGE);
		if (AppState.moduleContentContainer == null)
			AppState.moduleContentContainer = RootPanel
					.get("moduleContentContainer");

		clearPanels();
		manageItem.addStyleName(StyleConstants.STYLE_CURRENT_MODULE_ITEM_PANEL);
		currentPanelName = ManagerModule.class.getName();
		ManagerModule mm = new ManagerModule();
		AppState.moduleContentContainer.add(mm);
	}

	@Override
	public void onManage(DoManageEvent event) {
		doManage();
	}

	public void doNew() {
		// History.newItem(AppConstants.HISTORY_CREATE_NEW);
		if (AppState.moduleContentContainer == null)
			AppState.moduleContentContainer = RootPanel
					.get("moduleContentContainer");

		clearPanels();
		createNewItem
				.addStyleName(StyleConstants.STYLE_CURRENT_MODULE_ITEM_PANEL);
		currentPanelName = ScheduleEditingPanel.class.getName();
		AppState.moduleContentContainer.add(new ScheduleEditingPanel());
	}

	@Override
	public void onEdit(DoEditEvent event) {
		// History.newItem(AppConstants.HISTORY_EDIT);
		if (AppState.moduleContentContainer == null)
			AppState.moduleContentContainer = RootPanel
					.get("moduleContentContainer");

		clearPanels();
		manageItem.addStyleName(StyleConstants.STYLE_CURRENT_MODULE_ITEM_PANEL);
		currentPanelName = ScheduleEditingPanel.class.getName();
		AppState.lockingPeriodIdSet.add(event.getId());
		AppState.moduleContentContainer.add(new ScheduleEditingPanel(
				Mode.EDITING, event.getId()));
	}

	@Override
	public void onSettings(DoSettingsEvent event) {
		doSettings(null);
	}

	public void doSettings(String token) {
		if (AppState.moduleContentContainer == null)
			AppState.moduleContentContainer = RootPanel
					.get("moduleContentContainer");

		int tabIndex = 0;
		try {
			tabIndex = Integer.parseInt(token);
		} catch (Exception e) {
		}
		clearPanels();
		settingsItem
				.addStyleName(StyleConstants.STYLE_CURRENT_MODULE_ITEM_PANEL);
		currentPanelName = MenuSettingsPanel.class.getName();
		AppState.moduleContentContainer.add(new MenuSettingsPanel(tabIndex));
	}

	public void doSettings() {
		if (AppState.moduleContentContainer == null)
			AppState.moduleContentContainer = RootPanel
					.get("moduleContentContainer");

		clearPanels();
		settingsItem
				.addStyleName(StyleConstants.STYLE_CURRENT_MODULE_ITEM_PANEL);
		currentPanelName = StartSettingEntryPoint.class.getName();
		AppState.moduleContentContainer.add(new StartSettingEntryPoint());
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if (notifyHistoryChange()) {
			return;
		}
		handlers.clear();
		LoadingImagePanel.start();
		String token = event.getValue();
		String[] tokens = new String[] { "", };
		if (token != null)
			tokens = token.split("-");

		if ("".equals(tokens[0])) {
			doView(null);
		} else if (AppConstants.HISTORY_MANAGE.equals(tokens[0])) {
			doManage();
		} else if (AppConstants.HISTORY_SETTINGS.equals(tokens[0])) {

			// TO Use MenuSettings uncomment 5 lines below and comment
			// doSettings()

			if (tokens.length > 1) {
				doSettings(tokens[1]);
			} else {
				doSettings(null);
			}
			// doSettings();
		} else if (AppConstants.HISTORY_CREATE_NEW.equals(tokens[0])) {
			doNew();
		} else if (AppConstants.HISTORY_VIEW.equals(tokens[0])) {
			try {
				doView(Long.parseLong(tokens[1]));
			} catch (Exception e) {
				doView(null);
			}
		} else if (AppConstants.HISTORY_DRAFT.equals(tokens[0])) {
			try {
				doDraft(Long.parseLong(tokens[1]));
			} catch (Exception e) {
				doDraft(null);
			}
		} else if (AppConstants.HISTORY_EDIT.equals(tokens[0])) {
			try {
				AppState.eventBus.fireEvent(new DoEditEvent(Long
						.parseLong(tokens[1])));
			} catch (Exception e) {
				AppState.eventBus.fireEvent(new DoEditEvent(null));
			}
		} else {
			History.newItem(AppConstants.HISTORY_MANAGE);
		}
		// else
		// History.fireCurrentHistoryState();
	}
}
