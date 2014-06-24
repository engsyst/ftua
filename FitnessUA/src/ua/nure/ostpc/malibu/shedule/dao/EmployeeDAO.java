package ua.nure.ostpc.malibu.shedule.dao;

import java.util.Collection;

import javax.sql.RowSet;

import ua.nure.ostpc.malibu.shedule.entity.Employee;

//Interface that all EmployeeDAOs must support
public interface EmployeeDAO {
	public int insertEmployee(Employee emp);

	public boolean deleteEmployee(Employee emp);

	public Employee findEmployee();

	public boolean updateEmployee(Employee emp);

	public RowSet selectEmployeesRS();

	public Collection<Employee> selectEmployeesTO();
}
