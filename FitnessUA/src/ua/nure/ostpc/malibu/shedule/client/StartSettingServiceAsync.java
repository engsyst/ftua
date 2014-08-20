package ua.nure.ostpc.malibu.shedule.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>StartSettingService</code>.
 */
public interface StartSettingServiceAsync {
	void getClubs(AsyncCallback<Collection<Club>> callback)
			throws IllegalArgumentException;

	void getOnlyOurClubs(AsyncCallback<Collection<Club>> callback)
			throws IllegalArgumentException;

	void getDictionaryClub(AsyncCallback<Map<Long, Club>> callback)
			throws IllegalArgumentException;

	void setClubs(Collection<Club> clubsForInsert,
			Collection<Club> clubsForOnlyOurInsert,
			Collection<Club> clubsForUpdate, Collection<Club> clubsForDelete,
			AsyncCallback<Void> calback) throws IllegalArgumentException;

	void getEmployees(AsyncCallback<Collection<Employee>> callback)
			throws IllegalArgumentException;

	void getOnlyOurEmployees(AsyncCallback<Collection<Employee>> callback)
			throws IllegalArgumentException;
	
	void getDictionaryEmployee(AsyncCallback<Map<Long, Employee>> callback)
			throws IllegalArgumentException;
	
	void getRoleEmployee(AsyncCallback<Map<Long, Collection<Boolean>>> callback)
			throws IllegalArgumentException;
	
	void setEmployees(Collection<Employee> employeesForInsert,
			Collection<Employee> employeesForOnlyOurInsert,
			Collection<Employee> employeesForUpdate, Collection<Employee> employeesForDelete,
			HashMap<Integer,Collection<Long>> roleForInsert, HashMap<Integer,Collection<Long>> roleForDelete,
			HashMap<Integer,Collection<Employee>> roleForInsertNew, AsyncCallback<Void> calback)
			throws IllegalArgumentException;
}
