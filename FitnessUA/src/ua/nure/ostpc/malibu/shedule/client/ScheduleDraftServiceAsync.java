package ua.nure.ostpc.malibu.shedule.client;

import ua.nure.ostpc.malibu.shedule.entity.Employee;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>SheduleDraft</code>.
 */
public interface ScheduleDraftServiceAsync {
	void getEmployee(AsyncCallback<Employee> callback)
			throws IllegalArgumentException;
}
