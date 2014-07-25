package ua.nure.ostpc.malibu.shedule.client;

import java.util.Collection;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("start_setting")
public interface StartSettingService extends RemoteService {
	Collection<Club> getClubs() throws IllegalArgumentException;
	void setClubs(Collection<Club> clubs) throws IllegalArgumentException;
	
	Collection<Employee> getEmployees() throws IllegalArgumentException;
	void setEmployees(Collection<Employee> admins,Collection<Employee> responsiblePersons,
			Collection<Employee> other) throws IllegalArgumentException;
}
