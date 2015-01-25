package ua.nure.ostpc.malibu.shedule.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class AppState {
	public final static ScheduleManagerServiceAsync scheduleManagerService = GWT
			.create(ScheduleManagerService.class);
	public final static ScheduleDraftServiceAsync scheduleDraft = GWT
			.create(ScheduleDraftService.class);
	public static final HandlerManager eventBus = new HandlerManager(null);

	public static RootPanel userNamePanel = RootPanel.get("userNamePanel");
	public static RootPanel userIconPanel = RootPanel.get("userIconPanel");
	public static DockPanel logoutPanel;
	public static RootPanel moduleItemsContainer = RootPanel.get("moduleItemsContainer");
	public static RootPanel moduleContentGrayPanel = RootPanel.get("moduleContentGrayPanel");
	public static RootPanel moduleContentContainer = RootPanel.get("moduleContentContainer");
	
	public static User user;
	public static Employee employee;
//	public static String employeeName;
	public static List<Period> periodList;
	// private String currentStatus;
//	public static List<Role> roles = null;
	public static Boolean isResponsible = false;
//	public static long draftPeriodId;
	public static Set<Long> lockingPeriodIdSet = new HashSet<Long>();

}
