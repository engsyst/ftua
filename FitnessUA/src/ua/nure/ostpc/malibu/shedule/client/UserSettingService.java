package ua.nure.ostpc.malibu.shedule.client;

import ua.nure.ostpc.malibu.shedule.entity.Employee;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("")
public interface UserSettingService extends RemoteService {
	void setPass(String oldPass, String newPass)
			throws IllegalArgumentException;

	Employee getDataEmployee() throws IllegalArgumentException;

	void setDataEmployee(Employee emp) throws IllegalArgumentException;

	void setPreference(Employee emp) throws IllegalArgumentException;
}
