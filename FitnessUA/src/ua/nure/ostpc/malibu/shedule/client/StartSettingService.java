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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("")
public interface StartSettingService extends RemoteService {
	Collection<Club> getMalibuClubs() throws IllegalArgumentException;

	Collection<Club> getOnlyOurClubs() throws IllegalArgumentException;

	Map<Long, Club> getDictionaryClub() throws IllegalArgumentException;

	void setClubs(Collection<Club> clubsForInsert,
			Collection<Club> clubsForOnlyOurInsert,
			Collection<Club> clubsForUpdate, Collection<Club> clubsForDelete)
			throws IllegalArgumentException;

	Collection<Employee> getMalibuEmployees() throws IllegalArgumentException;

	Collection<Employee> getOnlyOurEmployees() throws IllegalArgumentException;

	Map<Long, Employee> getDictionaryEmployee() throws IllegalArgumentException;

	Map<Long, Collection<Boolean>> getRoleEmployee()
			throws IllegalArgumentException;

	void setEmployees(Collection<Employee> employeesForInsert,
			Collection<Employee> employeesForOnlyOurInsert,
			Collection<Employee> employeesForUpdate,
			Collection<Employee> employeesForDelete,
			Map<Integer, Collection<Long>> roleForInsert,
			Map<Integer, Collection<Long>> roleForDelete,
			Map<Integer, Collection<Employee>> roleForInsertNew,
			Map<Integer, Collection<Employee>> roleForInsertWithoutConformity)
			throws IllegalArgumentException;

	Collection<Employee> getAllEmployees() throws IllegalArgumentException;

	Collection<Category> getAllCategories() throws IllegalArgumentException;

	CategorySettingsData getCategorySettingsData()
			throws IllegalArgumentException;

	Category insertCategory(Category category) throws IllegalArgumentException;

	Category updateCategory(Category category) throws IllegalArgumentException;

	boolean removeCategory(long categoryId) throws IllegalArgumentException;

	Collection<Category> getCategoriesWithEmployees()
			throws IllegalArgumentException;

	Map<Long, Collection<Employee>> getCategoriesDictionary()
			throws IllegalArgumentException;

	void setCategory(Collection<Category> categories,
			Map<Long, Collection<Long>> employeeInCategoriesForDelete,
			Map<Long, Collection<Long>> employeeInCategoriesForInsert,
			Collection<Category> categoriesForDelete,
			Collection<Category> categoriesForInsert)
			throws IllegalArgumentException;

	Collection<Holiday> getHolidays() throws IllegalArgumentException;

	Holiday insertHoliday(Holiday holiday) throws IllegalArgumentException;

	boolean removeHoliday(long holidayId) throws IllegalArgumentException;

	void setHolidays(Collection<Holiday> holidaysForDelete,
			Collection<Holiday> holidaysForInsert)
			throws IllegalArgumentException;

	Collection<Long> getEmployeeWithoutUser() throws IllegalArgumentException;

	void setUser(User user) throws IllegalArgumentException;

	Preference getPreference() throws IllegalArgumentException;

	void setPreference(Preference pref) throws IllegalArgumentException;

	// ================================

	public List<ClubSettingViewData> getAllClubs()
			throws IllegalArgumentException;

	public Club setClubIndependent(long id, boolean isIndepended);

	public Club removeClub(long id);

	public Club importClub(Club club);

	public Club getClub(Long id);

	public Club setClub(Club club);

	public List<EmployeeSettingsData> getEmployeeSettingsData();

	public Employee importEmployee(Employee employee);

	public void removeEmployee(long id);

	public long[] updateEmployeeRole(long empId, int right, boolean enable)
			throws IllegalArgumentException;
	
	public long updateHoliday(Holiday holiday)
			throws IllegalArgumentException;

	public boolean[] getWeekends() throws IllegalArgumentException;
	
	public void updateWeekends(boolean[] weekends)
			throws IllegalArgumentException;
	
}
