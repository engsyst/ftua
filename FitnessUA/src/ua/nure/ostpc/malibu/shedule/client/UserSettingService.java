package ua.nure.ostpc.malibu.shedule.client;

import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.shared.EmployeeUpdateResult;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("")
public interface UserSettingService extends RemoteService {
	void setPass(String oldPass, String newPass)
			throws IllegalArgumentException;

	Employee getCurrentEmployee() throws IllegalArgumentException;

	void updateEmployeeData(Employee emp) throws IllegalArgumentException;

	void setPreference(Employee emp) throws IllegalArgumentException;

	EmployeeUpdateResult updateEmployeeData(Map<String, String> paramMap,
			long employeeId, String datePattern)
			throws IllegalArgumentException;
}
