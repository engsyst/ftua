package ua.nure.ostpc.malibu.shedule.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Right;

/**
 * Interface that all EmployeeDAOs must support
 * 
 * @author engsyst
 */
public interface EmployeeDAO {

	public int insertEmployeePrefs(Employee emp) throws SQLException;

	public Employee findEmployee(long empId);

	public boolean updateEmployeePrefs(Employee emp);

	public Collection<Employee> findEmployeesByAssignmentId(long assignmentId);

	public List<Employee> getScheduleEmployees();

	public List<Employee> getEmployeesByShiftId(long shiftId);

	public Collection<Employee> getMalibuEmployees();

	public Map<Long, Employee> getConformity();

	public boolean containsInSchedules(long employeeId);

	public boolean deleteEmployee(long id);

	public Collection<Employee> getOnlyOurEmployees();

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

	public boolean insertEmployees(Collection<Employee> emps);

	public boolean updateEmployee(Employee employee);

	public boolean updateEmployees(Collection<Employee> emps);

	public Collection<Employee> getAllEmployee();

	public Collection<Employee> findEmployees(Collection<Long> ids);

	public Collection<Employee> findEmployees(Right right);

	public List<String> getEmailListForSubscribers();

	public Map<String, String> checkEmployeeDataBeforeUpdate(
			Map<String, String> paramMap, long employeeId);

	public Map<String, String> checkEmployeeDataBeforeUpdate(String email,
			String cellPhone, long employeeId);

	public Employee getScheduleEmployeeById(long employeeId);
}
