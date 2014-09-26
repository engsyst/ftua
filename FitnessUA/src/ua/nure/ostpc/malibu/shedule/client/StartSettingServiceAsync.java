package ua.nure.ostpc.malibu.shedule.client;

import java.util.Collection;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Category;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Holiday;
import ua.nure.ostpc.malibu.shedule.entity.User;

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

	void getMalibuEmployees(AsyncCallback<Collection<Employee>> callback)
			throws IllegalArgumentException;

	void getOnlyOurEmployees(AsyncCallback<Collection<Employee>> callback)
			throws IllegalArgumentException;
	
	void getDictionaryEmployee(AsyncCallback<Map<Long, Employee>> callback)
			throws IllegalArgumentException;
	
	void getRoleEmployee(AsyncCallback<Map<Long, Collection<Boolean>>> callback)
			throws IllegalArgumentException;
	
	void setEmployees(Collection<Employee> employeesForInsert,
			Collection<Employee> employeesForOnlyOurInsert,
			Collection<Employee> employeesForUpdate, Collection<Employee> employeesForDelete,
			Map<Integer,Collection<Long>> roleForInsert, Map<Integer,Collection<Long>> roleForDelete,
			Map<Integer,Collection<Employee>> roleForInsertNew,
			Map<Integer,Collection<Employee>> roleForInsertWithoutConformity, AsyncCallback<Void> calback)
			throws IllegalArgumentException;
	
	void getAllEmploee(AsyncCallback<Collection<Employee>> callback) throws IllegalArgumentException;
	
	void getCategories(AsyncCallback<Collection<Category>> callback) throws IllegalArgumentException;
	
	void getCategoriesDictionary(AsyncCallback<Map<Long, Collection<Employee>>> callback) throws IllegalArgumentException;
	
	void setCategory(Collection<Category> categories, Map<Long, Collection<Long>> employeeInCategoriesForDelete,
			Map<Long, Collection<Long>> employeeInCategoriesForInsert,
			Collection<Category> categoriesForDelete,
			Collection<Category> categoriesForInsert,
			AsyncCallback<Void> calback) throws IllegalArgumentException;

	void getHolidays(AsyncCallback<Collection<Holiday>> asyncCallback) throws IllegalArgumentException;
	
	void setHolidays(Collection<Holiday> holidaysForDelete,
			Collection<Holiday> holidaysForInsert,
			AsyncCallback<Void> calback) throws IllegalArgumentException;
	
	void getEmployeeWithoutUser (AsyncCallback<Collection<Long>> callback) throws IllegalArgumentException;
	
	void setUser(User user, AsyncCallback<Void> callback) throws IllegalArgumentException;
	
}
