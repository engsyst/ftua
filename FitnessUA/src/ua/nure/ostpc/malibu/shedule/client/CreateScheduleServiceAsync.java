package ua.nure.ostpc.malibu.shedule.client;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>CreateSchedule</code>.
 */
public interface CreateScheduleServiceAsync {

	void getStartDate(AsyncCallback<Date> callback)
			throws IllegalArgumentException;

}
