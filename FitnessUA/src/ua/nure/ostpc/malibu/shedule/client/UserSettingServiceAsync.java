package ua.nure.ostpc.malibu.shedule.client;

import java.util.Collection;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Category;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Holiday;
import ua.nure.ostpc.malibu.shedule.entity.Preference;
import ua.nure.ostpc.malibu.shedule.entity.User;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>StartSettingService</code>.
 */
public interface UserSettingServiceAsync {
	
	void getDataUser(AsyncCallback<User> callback) throws IllegalArgumentException;
	
	void getDataEmployee(AsyncCallback<Employee> callback) throws IllegalArgumentException;
	
	void setDataUser(String oldPass, User user, AsyncCallback<Void> callback) throws IllegalArgumentException;
	
	void setDataEmployee(Employee emp, AsyncCallback<Void> callback) throws IllegalArgumentException;
}
