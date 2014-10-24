package ua.nure.ostpc.malibu.shedule.client.panel.creation;

import java.util.Date;
import java.util.List;

import ua.nure.ostpc.malibu.shedule.entity.Category;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Preference;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;

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

	void getCategoriesWithEmployees(AsyncCallback<List<Category>> callback)
			throws IllegalArgumentException;

	void insertSchedule(Schedule schedule, AsyncCallback<Schedule> callback)
			throws IllegalArgumentException;
}
