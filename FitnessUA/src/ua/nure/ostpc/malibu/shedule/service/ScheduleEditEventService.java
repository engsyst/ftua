package ua.nure.ostpc.malibu.shedule.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * ScheduleEditEventService
 * 
 * @author Volodymyr_Semerkov
 * 
 */
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

	public synchronized List<Long> removeEditEventsForUser(long userId) {
		List<Long> periodIdList = new ArrayList<Long>();
		Iterator<Entry<Long, Long>> entryIterator = scheduleEditEventMap
				.entrySet().iterator();
		while (entryIterator.hasNext()) {
			Entry<Long, Long> entry = entryIterator.next();
			if (entry.getValue() == userId) {
				periodIdList.add(entry.getKey());
				entryIterator.remove();
			}
		}
		return periodIdList;
	}
}
