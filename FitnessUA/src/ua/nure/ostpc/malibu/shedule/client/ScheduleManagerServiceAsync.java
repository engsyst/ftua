package ua.nure.ostpc.malibu.shedule.client;

import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Role;
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

	void lockSchedule(Long periodId, AsyncCallback<Boolean> callback)
			throws IllegalArgumentException;

	void userRoles(AsyncCallback<List<Role>> callback)
			throws IllegalArgumentException;
	void getUser(AsyncCallback<String> callback) throws IllegalArgumentException;

}
