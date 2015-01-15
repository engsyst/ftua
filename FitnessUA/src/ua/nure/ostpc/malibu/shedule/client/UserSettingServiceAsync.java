package ua.nure.ostpc.malibu.shedule.client;

import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.shared.EmployeeUpdateResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>StartSettingService</code>.
 */
public interface UserSettingServiceAsync {

	void changePassword(String oldPassword, String newPassword,
			long employeeId, AsyncCallback<EmployeeUpdateResult> callback)
			throws IllegalArgumentException;

	void getCurrentEmployee(AsyncCallback<Employee> callback)
			throws IllegalArgumentException;

	void updateEmployeeData(Employee emp, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void setPreference(int minDayNumber, int maxDayNumber, long employeeId,
			AsyncCallback<EmployeeUpdateResult> callback)
			throws IllegalArgumentException;

	void updateFullEmployeeProfile(Map<String, String> paramMap,
			long employeeId, String datePattern,
			AsyncCallback<EmployeeUpdateResult> callback)
			throws IllegalArgumentException;

	void updateEmployeeProfile(String email, String cellPhone, long employeeId,
			AsyncCallback<EmployeeUpdateResult> callback)
			throws IllegalArgumentException;

	void getScheduleEmployeeById(long employeeId,
			AsyncCallback<Employee> callback) throws IllegalArgumentException;
}
