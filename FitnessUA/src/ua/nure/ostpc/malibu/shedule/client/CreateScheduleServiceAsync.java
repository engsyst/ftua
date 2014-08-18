package ua.nure.ostpc.malibu.shedule.client;

import java.util.Date;
import java.util.List;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Preference;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>CreateSchedule</code>.
 */
public interface CreateScheduleServiceAsync {

	void getStartDate(AsyncCallback<Date> callback)
			throws IllegalArgumentException;

	void getDependentClubs(AsyncCallback<List<Club>> callback)
			throws IllegalArgumentException;

	void getEmployees(AsyncCallback<List<Employee>> callback)
			throws IllegalArgumentException;

	void getPreference(AsyncCallback<Preference> callback)
			throws IllegalArgumentException;
}
