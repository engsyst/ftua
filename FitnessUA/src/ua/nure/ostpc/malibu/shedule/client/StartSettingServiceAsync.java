package ua.nure.ostpc.malibu.shedule.client;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Category;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubSettingViewData;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.EmployeeSettingsData;
import ua.nure.ostpc.malibu.shedule.entity.Holiday;
import ua.nure.ostpc.malibu.shedule.entity.Preference;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.shared.CategorySettingsData;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>StartSettingService</code>.
 */
public interface StartSettingServiceAsync {
	void getMalibuClubs(AsyncCallback<Collection<Club>> callback)
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
			Collection<Employee> employeesForUpdate,
			Collection<Employee> employeesForDelete,
			Map<Integer, Collection<Long>> roleForInsert,
			Map<Integer, Collection<Long>> roleForDelete,
			Map<Integer, Collection<Employee>> roleForInsertNew,
			Map<Integer, Collection<Employee>> roleForInsertWithoutConformity,
			AsyncCallback<Void> calback) throws IllegalArgumentException;

	void getAllEmployees(AsyncCallback<Collection<Employee>> callback)
			throws IllegalArgumentException;

	void getCategorySettingsData(AsyncCallback<CategorySettingsData> callback)
			throws IllegalArgumentException;

	void insertCategory(Category category, AsyncCallback<Category> callback)
			throws IllegalArgumentException;

	void updateCategory(Category category, AsyncCallback<Category> callback)
			throws IllegalArgumentException;

	void removeCategory(long categoryId, AsyncCallback<Boolean> callback)
			throws IllegalArgumentException;

	void getAllCategories(AsyncCallback<Collection<Category>> callback)
			throws IllegalArgumentException;

	void getCategoriesWithEmployees(AsyncCallback<Collection<Category>> callback)
			throws IllegalArgumentException;

	void getCategoriesDictionary(
			AsyncCallback<Map<Long, Collection<Employee>>> callback)
			throws IllegalArgumentException;

	void setCategory(Collection<Category> categories,
			Map<Long, Collection<Long>> employeeInCategoriesForDelete,
			Map<Long, Collection<Long>> employeeInCategoriesForInsert,
			Collection<Category> categoriesForDelete,
			Collection<Category> categoriesForInsert,
			AsyncCallback<Void> calback) throws IllegalArgumentException;

	void getHolidays(AsyncCallback<Collection<Holiday>> asyncCallback)
			throws IllegalArgumentException;

	void setHolidays(Collection<Holiday> holidaysForDelete,
			Collection<Holiday> holidaysForInsert, AsyncCallback<Void> calback)
			throws IllegalArgumentException;

	void getEmployeeWithoutUser(AsyncCallback<Collection<Long>> callback)
			throws IllegalArgumentException;

	void setUser(User user, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void getPreference(AsyncCallback<Preference> callback)
			throws IllegalArgumentException;

	void setPreference(Preference pref, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	// ================================

	public void getAllClubs(AsyncCallback<List<ClubSettingViewData>> callback)
			throws IllegalArgumentException;

	void setClubIndependent(long id, boolean isIndepended,
			AsyncCallback<Club> callback) throws IllegalArgumentException;

	void removeClub(long id, AsyncCallback<Club> callback)
			throws IllegalArgumentException;

	void importClub(Club innerClub, AsyncCallback<Club> callback);

	void getClub(Long club, AsyncCallback<Club> callback);

	void setClub(Club club, AsyncCallback<Club> callback);

	void getEmployeeSettingsData(
			AsyncCallback<List<EmployeeSettingsData>> callback);

	void importEmployee(Employee outEmployee,
			AsyncCallback<Employee> asyncCallback);

	void removeEmployee(long employeeId, AsyncCallback<Void> asyncCallback);

	void updateEmployeeRole(long empId, long roleId, boolean enable,
			AsyncCallback<long[]> asyncCallback)
			throws IllegalArgumentException;
	
	void updateHoliday(Holiday holiday, AsyncCallback<Long> asyncCallback) 
			throws IllegalArgumentException;
}
