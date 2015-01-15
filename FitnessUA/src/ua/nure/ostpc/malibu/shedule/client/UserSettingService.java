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
	EmployeeUpdateResult changePassword(String oldPassword, String newPassword,
			long employeeId) throws IllegalArgumentException;

	Employee getCurrentEmployee() throws IllegalArgumentException;

	void updateEmployeeData(Employee emp) throws IllegalArgumentException;

	EmployeeUpdateResult setPreference(int minDayNumber, int maxDayNumber,
			long employeeId) throws IllegalArgumentException;

	EmployeeUpdateResult updateFullEmployeeProfile(
			Map<String, String> paramMap, long employeeId, String datePattern)
			throws IllegalArgumentException;

	EmployeeUpdateResult updateEmployeeProfile(String email, String cellPhone,
			long employeeId) throws IllegalArgumentException;

	Employee getScheduleEmployeeById(long employeeId)
			throws IllegalArgumentException;
}
