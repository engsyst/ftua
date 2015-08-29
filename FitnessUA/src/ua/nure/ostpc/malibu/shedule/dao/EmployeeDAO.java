package ua.nure.ostpc.malibu.shedule.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.EmployeeSettingsData;
import ua.nure.ostpc.malibu.shedule.entity.Right;
import ua.nure.ostpc.malibu.shedule.entity.Role;

/**
 * Interface that all EmployeeDAOs must support
 * 
 * @author engsyst
 */
public interface EmployeeDAO {

	public boolean insertEmployeePrefs(Employee employee) throws SQLException;

	public Employee findEmployee(long empId);

	public boolean updateEmployeePrefs(Employee emp);

	public Collection<Employee> findEmployeesByAssignmentId(long assignmentId);

	public List<Employee> getAllAdminScheduleEmployees();

	public List<Employee> getEmployeesByShiftId(long shiftId);

	public Collection<Employee> getMalibuEmployees();

	public Map<Long, Employee> getConformity();

	public boolean containsInSchedules(long employeeId);

	public void deleteEmployee(long id) throws DAOException;

	public Collection<Employee> getOnlyOurEmployees() throws DAOException;

	public boolean insertEmployeesWithConformity(Collection<Employee> emps);

	public boolean insertEmployeesWithConformityAndRoles(
			Map<Integer, Collection<Employee>> roleForInsert);

	public boolean insertEmployeesAndRoles(
			Map<Integer, Collection<Employee>> roleForInsert);

	public Map<Long, Collection<Boolean>> getRolesForEmployee();

	public boolean setRolesForEmployees(
			Map<Integer, Collection<Long>> roleForInsert);

	public boolean deleteRolesForEmployees(
			Map<Integer, Collection<Long>> roleForDelete);

	public long insertEmployee(Employee employee) throws DAOException;

	public boolean insertEmployees(Collection<Employee> emps);

	public boolean updateEmployee(Employee employee) throws DAOException;

	public boolean updateEmployees(Collection<Employee> emps);

	public Collection<Employee> getAllEmployees();

	public Collection<Employee> findEmployees(Collection<Long> ids);

	public Collection<Employee> findEmployees(Right right);

	public List<Employee> getScheduleEmployeesForSchedule(long scheduleId);

	public List<Employee> getRemovedScheduleEmployees();

	public List<String> getEmailListForSubscribers();

	public Map<String, String> checkEmployeeDataBeforeUpdate(
			Map<String, String> paramMap, long employeeId);

	public Map<String, String> checkEmployeeDataBeforeUpdate(String email,
			String cellPhone, long employeeId);

	public Employee getScheduleEmployeeById(long employeeId);

	public List<EmployeeSettingsData> getEmployeeSettingsData()
			throws DAOException;

	public Employee importEmployee(Employee employee, List<Role> roles)
			throws DAOException;

	public List<Role> getRoles() throws DAOException;

	public void deleteEmployeeUserRole(long empId, long roleId)
			throws DAOException;

	void insertEmployeeUserRole(long empId, long roleId) throws DAOException;

	public List<Employee> getAllNotDeletedScheduleEmployees();
}
