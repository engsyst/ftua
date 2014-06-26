package ua.nure.ostpc.malibu.shedule.dao;

import java.sql.SQLException;
import java.util.Collection;

import javax.sql.RowSet;

import ua.nure.ostpc.malibu.shedule.entity.EmpPrferences;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

//Interface that all EmployeeDAOs must support
public interface EmployeeDAO {
	public int insertEmployee(Employee emp) throws SQLException;
	
	public int insertEmployeeWithPrefs(Employee emp, EmpPrferences ep) throws SQLException;

	public boolean deleteEmployee(Employee emp);

	public Employee findEmployee();

	public boolean updateEmployee(Employee emp);

	public RowSet selectEmployeesRS();

	public Collection<Employee> selectEmployeesTO();
}
