package ua.nure.ostpc.malibu.shedule.shared;

import java.io.Serializable;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Employee;

import com.google.gwt.user.client.rpc.IsSerializable;

public class EmployeeUpdateResult implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;
	private boolean result;
	private Employee employee;
	private Map<String, String> errorMap;

	public EmployeeUpdateResult() {
	}

	public EmployeeUpdateResult(boolean result, Employee employee,
			Map<String, String> paramErrors) {
		this.result = result;
		this.employee = employee;
		this.errorMap = paramErrors;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Map<String, String> getErrorMap() {
		return errorMap;
	}

	public void setErrorMap(Map<String, String> errorMap) {
		this.errorMap = errorMap;
	}
}
