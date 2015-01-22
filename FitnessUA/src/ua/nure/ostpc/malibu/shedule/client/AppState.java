package ua.nure.ostpc.malibu.shedule.client;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Role;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

public class AppState {
	public final static ScheduleManagerServiceAsync scheduleManagerService = GWT
			.create(ScheduleManagerService.class);
	public final static ScheduleDraftServiceAsync scheduleDraft = GWT
			.create(ScheduleDraftService.class);
	public static final HandlerManager eventBus = new HandlerManager(null);

	public static String employeeName;
	public static List<Period> periodList;
	public static Map<Long, Status> scheduleStatusMap;
	// private String currentStatus;
	public static List<Role> roles = null;
	public static Boolean isResponsible = false;
	public static long draftPeriodId;
	public static Set<Long> lockingPeriodIdSet = new HashSet<Long>();

	public static void unlockSchedule() {
		// TODO Auto-generated method stub

	}

}
