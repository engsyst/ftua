package ua.nure.ostpc.malibu.shedule.client;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubPref;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>SheduleDraft</code>.
 */
public interface ScheduleDraftServiceAsync {
	void getEmployee(AsyncCallback<Employee> callback)
			throws IllegalArgumentException;
	void getClubs (AsyncCallback<Collection<Club>> callback) throws IllegalArgumentException;
	void getEmpToClub(long periodId,AsyncCallback<Map<Club,List<Employee>>> callback);
}
