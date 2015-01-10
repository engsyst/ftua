package ua.nure.ostpc.malibu.shedule.client;

import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.shared.EmployeeUpdateResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>StartSettingService</code>.
 */
public interface UserSettingServiceAsync {

	void setPass(String oldPass, String newPass, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void getCurrentEmployee(AsyncCallback<Employee> callback)
			throws IllegalArgumentException;

	void updateEmployeeData(Employee emp, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void setPreference(Employee emp, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void updateEmployeeData(Map<String, String> paramMap, long employeeId,
			String datePattern, AsyncCallback<EmployeeUpdateResult> callback)
			throws IllegalArgumentException;
}
