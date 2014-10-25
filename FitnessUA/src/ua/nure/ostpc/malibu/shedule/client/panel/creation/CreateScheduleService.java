package ua.nure.ostpc.malibu.shedule.client.panel.creation;

import java.util.Date;
import java.util.List;

import ua.nure.ostpc.malibu.shedule.entity.Category;
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
public interface CreateScheduleService extends RemoteService {

	Date getStartDate() throws IllegalArgumentException;

	List<Club> getDependentClubs() throws IllegalArgumentException;

	List<Employee> getEmployees() throws IllegalArgumentException;

	Preference getPreference() throws IllegalArgumentException;

	List<Category> getCategoriesWithEmployees() throws IllegalArgumentException;

	Schedule insertSchedule(Schedule schedule) throws IllegalArgumentException;
}
