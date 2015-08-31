package ua.nure.ostpc.malibu.shedule.client.editor;

import java.util.Date;
import java.util.List;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Preference;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("")
public interface ScheduleEditingService extends RemoteService {

	Date getStartDate() throws IllegalArgumentException;

	List<Club> getDependentClubs() throws IllegalArgumentException;

	List<Employee> getScheduleEmployees() throws IllegalArgumentException;

	Preference getPreference() throws IllegalArgumentException;

	/**
	 * Saves a new schedule to schedule cache and to database.
	 * 
	 * @param schedule
	 *            - The inserted schedule.
	 * @return Saved schedule. Equals null if the inserted schedule end date is
	 *         not after the end date for the last schedule in database.
	 */
	Schedule insertSchedule(Schedule schedule) throws IllegalArgumentException;

	Schedule updateSchedule(Schedule schedule) throws IllegalArgumentException;
}
