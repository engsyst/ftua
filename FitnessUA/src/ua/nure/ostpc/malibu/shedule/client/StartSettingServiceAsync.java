package ua.nure.ostpc.malibu.shedule.client;

import java.util.Collection;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>StartSettingService</code>.
 */
public interface StartSettingServiceAsync {
	void getClubs(AsyncCallback<Collection<Club>> callback)
			throws IllegalArgumentException;

	void getOnlyOurClubs(AsyncCallback<Collection<Club>> callback)
			throws IllegalArgumentException;

	void getDictionaryClub(AsyncCallback<Map<Long, Club>> callback)
			throws IllegalArgumentException;

	void setClubs(Collection<Club> clubsForInsert,
			Collection<Club> clubsForOnlyOurInsert,
			Collection<Club> clubsForUpdate, Collection<Club> clubsForDelete,
			AsyncCallback<Void> calback) throws IllegalArgumentException;

	void getEmployees(AsyncCallback<Collection<Employee>> callback)
			throws IllegalArgumentException;

	void setEmployees(Collection<Employee> admins,
			Collection<Employee> responsiblePersons,
			Collection<Employee> other, AsyncCallback<Void> calback)
			throws IllegalArgumentException;
}
