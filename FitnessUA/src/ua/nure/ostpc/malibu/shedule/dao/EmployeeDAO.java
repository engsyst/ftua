package ua.nure.ostpc.malibu.shedule.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.entity.Employee;

/**
 * Interface that all EmployeeDAOs must support
 * 
 * @author engsyst
 */
public interface EmployeeDAO {

	public int insertEmployeePrefs(Employee emp) throws SQLException;

	public Employee findEmployee(long empId) throws SQLException;

	public boolean updateEmployeePrefs(Employee emp) throws SQLException;

	public Set<Employee> selectEmployees(long groupId) throws SQLException;

	public Collection<Employee> findEmployeesByAssignmentId(long assignmentId);

	public Collection<Employee> findEmployeesByClubId(long clubId);

	public List<Employee> findEmployeesByShiftId(long shiftId);

	public Collection<Employee> getMalibuEmployees() throws SQLException;
}
