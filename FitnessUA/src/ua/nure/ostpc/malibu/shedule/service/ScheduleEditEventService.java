package ua.nure.ostpc.malibu.shedule.service;

import java.util.HashMap;
import java.util.Map;

public class ScheduleEditEventService {

	/**
	 * Schedule edit event map where key is schedule period id and value is id
	 * (user id) of responsible person which is editing current schedule now.
	 */
	private Map<Long, Long> scheduleEditEventMap = new HashMap<Long, Long>();

	public synchronized boolean addEditEvent(long periodId, long userId) {
		if (!scheduleEditEventMap.containsKey(periodId)) {
			return false;
		} else {
			scheduleEditEventMap.put(periodId, userId);
			return true;
		}
	}

	public synchronized void removeEditEvent(long periodId) {
		scheduleEditEventMap.remove(periodId);
	}
}
