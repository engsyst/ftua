package ua.nure.ostpc.malibu.shedule.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>CreateSchedule</code>.
 */
public interface CreateScheduleServiceAsync {

	void getStartDate(AsyncCallback<Date> callback)
			throws IllegalArgumentException;

	void getDependentClubs(AsyncCallback<List<Club>> callback)
			throws IllegalArgumentException;

	void getEmployeesByClubsId(List<Long> clubsId,
			AsyncCallback<Map<Long, List<Employee>>> callback)
			throws IllegalArgumentException;
}
