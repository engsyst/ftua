package ua.nure.ostpc.malibu.shedule.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleEditEventService {

	/**
	 * Schedule edit event map where key is schedule period id and value is list
	 * of identifiers of users which are editing current schedule in key now.
	 */
	private Map<Long, List<Long>> scheduleEditEventMap = new HashMap<Long, List<Long>>();

	public synchronized void addEditEvent(long periodId, long userId) {
		if (!scheduleEditEventMap.containsKey(periodId)) {
			List<Long> userIdList = new ArrayList<Long>();
			userIdList.add(userId);
			scheduleEditEventMap.put(periodId, userIdList);
		} else {
			scheduleEditEventMap.get(periodId).add(userId);
		}
	}

	public synchronized void removeEditEvent(long periodId, long userId) {
		if (!scheduleEditEventMap.containsKey(periodId)) {
			scheduleEditEventMap.get(periodId).remove(userId);
		}
	}
}
