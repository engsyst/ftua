package ua.nure.ostpc.malibu.shedule.dao;

import java.sql.Connection;
import java.sql.SQLException;
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

	public List<Employee> findEmployeesByAssignmentId(Connection con,
			long assignmentId) throws SQLException;
}
