package ua.nure.ostpc.malibu.shedule.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.client.draft.ScheduleDraftService;
import ua.nure.ostpc.malibu.shedule.client.draft.ScheduleDraftServiceAsync;
import ua.nure.ostpc.malibu.shedule.client.editor.ScheduleEditingService;
import ua.nure.ostpc.malibu.shedule.client.editor.ScheduleEditingServiceAsync;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.validator.ClientSideValidator;
import ua.nure.ostpc.malibu.shedule.validator.Validator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class AppState {
	public final static ScheduleManagerServiceAsync scheduleManagerService = GWT
			.create(ScheduleManagerService.class);
	public final static ScheduleDraftServiceAsync scheduleDraftService = GWT
			.create(ScheduleDraftService.class);
	public final static ScheduleEditingServiceAsync scheduleEditingService = GWT
			.create(ScheduleEditingService.class);
	public final static StartSettingServiceAsync startSettingsService = GWT
			.create(StartSettingService.class);
	public final static UserSettingServiceAsync userSettingService = GWT
			.create(UserSettingService.class);

	public static final HandlerManager eventBus = new HandlerManager(null);

	public static RootPanel userNamePanel = RootPanel.get("userNamePanel");
	public static RootPanel userIconPanel = RootPanel.get("userIconPanel");
	public static PopupPanel logoutPanel;
	public static RootPanel moduleItemsContainer = RootPanel
			.get("moduleItemsContainer");
	public static RootPanel moduleContentGrayPanel = RootPanel
			.get("moduleContentGrayPanel");
	public static RootPanel moduleContentContainer = RootPanel
			.get("moduleContentContainer");

	public static User user;
	public static Employee employee;
	public static Validator clientSideValidator = new ClientSideValidator();
	public static List<Period> periodList;
	// public static List<Club> clubs;
	// public static List<Employee> employees;
	// public static Preference preference;
	// private String currentStatus;
	// public static List<Role> roles = null;
	public static Boolean isResponsible = false;
	// public static long draftPeriodId;
	public static Set<Long> lockingPeriodIdSet = new HashSet<Long>();

}
