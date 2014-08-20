package ua.nure.ostpc.malibu.shedule.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("")
public interface StartSettingService extends RemoteService {
	Collection<Club> getClubs() throws IllegalArgumentException;

	Collection<Club> getOnlyOurClubs() throws IllegalArgumentException;

	Map<Long, Club> getDictionaryClub() throws IllegalArgumentException;

	void setClubs(Collection<Club> clubsForInsert,
			Collection<Club> clubsForOnlyOurInsert,
			Collection<Club> clubsForUpdate, Collection<Club> clubsForDelete)
			throws IllegalArgumentException;

	Collection<Employee> getEmployees() throws IllegalArgumentException;
	
	Collection<Employee> getOnlyOurEmployees() throws IllegalArgumentException;
	
	Map<Long, Employee> getDictionaryEmployee() throws IllegalArgumentException;
	
	Map<Long, Collection<Boolean>> getRoleEmployee() throws IllegalArgumentException;
	
	
	void setEmployees(Collection<Employee> employeesForInsert,
			Collection<Employee> employeesForOnlyOurInsert,
			Collection<Employee> employeesForUpdate, Collection<Employee> employeesForDelete,
			HashMap<Integer,Collection<Long>> roleForInsert, HashMap<Integer,Collection<Long>> roleForDelete,
			HashMap<Integer,Collection<Employee>> roleForInsertNew)
			throws IllegalArgumentException;

}
