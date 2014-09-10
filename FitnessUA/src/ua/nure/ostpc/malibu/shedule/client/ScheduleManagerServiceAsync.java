package ua.nure.ostpc.malibu.shedule.client;

import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ScheduleManagerServiceAsync {

	void getAllPeriods(AsyncCallback<List<Period>> callback)
			throws IllegalArgumentException;

	void getScheduleStatusMap(AsyncCallback<Map<Long, Status>> callback)
			throws IllegalArgumentException;
}
