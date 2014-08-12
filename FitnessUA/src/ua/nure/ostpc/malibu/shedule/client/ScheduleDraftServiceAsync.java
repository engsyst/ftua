package ua.nure.ostpc.malibu.shedule.client;

import java.util.Collection;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>SheduleDraft</code>.
 */
public interface ScheduleDraftServiceAsync {
	void getEmployee(AsyncCallback<Employee> callback)
			throws IllegalArgumentException;
	void getClubs (AsyncCallback<Collection<Club>> callback) throws IllegalArgumentException;
}
