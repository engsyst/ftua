package ua.nure.ostpc.malibu.shedule.client.editor;

import java.util.Date;
import java.util.List;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Preference;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>CreateSchedule</code>.
 */
public interface ScheduleEditingServiceAsync {

	void getStartDate(AsyncCallback<Date> callback)
			throws IllegalArgumentException;

	void getDependentClubs(AsyncCallback<List<Club>> callback)
			throws IllegalArgumentException;

	void getScheduleEmployees(AsyncCallback<List<Employee>> callback)
			throws IllegalArgumentException;

	void getPreference(AsyncCallback<Preference> callback)
			throws IllegalArgumentException;

	/**
	 * Saves a new schedule to schedule cache and to database.
	 * 
	 * @param schedule
	 *            - The inserted schedule.
	 * @return Saved schedule. Equals null if the inserted schedule end date is
	 *         not after the end date for the last schedule in database.
	 */
	void insertSchedule(Schedule schedule, AsyncCallback<Schedule> callback)
			throws IllegalArgumentException;

	void updateSchedule(Schedule schedule, AsyncCallback<Schedule> callback)
			throws IllegalArgumentException;
}