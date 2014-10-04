package ua.nure.ostpc.malibu.shedule.client;

import java.util.Collection;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Category;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Holiday;
import ua.nure.ostpc.malibu.shedule.entity.Preference;
import ua.nure.ostpc.malibu.shedule.entity.User;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("")
public interface UserSettingService extends RemoteService {
	User getDataUser() throws IllegalArgumentException;
	Employee getDataEmployee() throws IllegalArgumentException;
	void setDataUser(String oldPass, User user) throws IllegalArgumentException;
	void setDataEmployee(Employee emp) throws IllegalArgumentException;
}
