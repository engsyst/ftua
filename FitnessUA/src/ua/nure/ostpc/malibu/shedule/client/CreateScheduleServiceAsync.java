package ua.nure.ostpc.malibu.shedule.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>CreateSchedule</code>.
 */
public interface CreateScheduleServiceAsync {
	void createSchedule(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;
}
