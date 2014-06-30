package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

public class MSsqlEmployeeDAO implements EmployeeDAO {
	
	public int insertEmployeePrefs(Connection con, Employee emp) throws SQLException {
		Statement st = null;
		int res = 0;
		try{
			 st = con.createStatement();
			 res = st.executeUpdate(String.format("insert into EmployeePref(min,max,employee_id) values(%1$d,%2$d,%3$d)",emp.getMin(),emp.getMax(),emp.getEmployeeId()));
		}
		catch(SQLException e){
			throw e;
		}
		finally{
			if(st!=null){
				try{
					st.close();
				}
				catch(SQLException e){
					throw e;
				}
			}
		}
		return res;
	}

	@Override
	public int insertEmployeePrefs(Employee emp) throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		int updateResult = 0;
		try {
			updateResult = insertEmployeePrefs(con, emp);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not update Employee # " + this.getClass()
					+ " # " + e.getMessage());
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not close connection # " + this.getClass()
					+ " # " + e.getMessage());
		}
		return updateResult;
	}

	public Employee findEmployee(Connection con, long empId) throws SQLException {
		Employee emp = null;
		Statement st = null;
		try{
			st = con.createStatement();
			java.sql.ResultSet resSet = st.executeQuery(String.format("select e.name, e.surName, e.threadName, p.min, p.max from Employees e join EmployeePref p on e.employee_id=p.employee_id where e.employee_id=%d",empId));
			emp = new Employee(resSet.getString("name"), resSet.getString("surName"),
					resSet.getString("threadName"), resSet.getByte("min"),
					resSet.getByte("max"));
		}
		catch(SQLException e){
			throw e;
		}
		finally{
			if(st!=null){
				try{
					st.close();
				}
				catch(SQLException e){
					throw e;
				}
			}
		}
		return emp;
	}
	
	@Override
	public Employee findEmployee(long empId) throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		Employee emp = null;
		try{
			emp = findEmployee(con, empId);
		}
		catch(SQLException e){
			e.printStackTrace();
			System.err.println("Can not find Employee # " + this.getClass()
					+ " # " + e.getMessage());
			return null;
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not close connection # " + this.getClass()
					+ " # " + e.getMessage());
		}
		return emp;
	}

	@Override
	public boolean updateEmployeePrefs(Employee emp) throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		boolean updateResult = false;
		try {
			updateResult = updateEmployeePrefs(con, emp);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not update Employee # " + this.getClass()
					+ " # " + e.getMessage());
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not close connection # " + this.getClass()
					+ " # " + e.getMessage());
		}
		return updateResult;
	}

	public boolean updateEmployeePrefs(Connection con,Employee emp)
		throws SQLException {
		Statement st = null;
		int res = 0;
		try{
			 st = con.createStatement();
			 res = st.executeUpdate(String.format("update EmployeePref set min = %1$d, %2$d = max where employee_id=%3$d",emp.getMin(),emp.getMax(),emp.getEmployeeId()));
		}
		catch(SQLException e){
			throw e;
		}
		finally{
			if(st!=null){
				try{
					st.close();
				}
				catch(SQLException e){
					throw e;
				}
			}
		}
		if (res == 0)
			return false;
		else 
			return true;
	}
	
	public Set<Employee> selectEmployees(Connection con, long groupId)
			throws SQLException {
		Statement st = null;
		Set<Employee> resultEmployeeSet = new java.util.HashSet<Employee>();
		try{
			st = con.createStatement();
			java.sql.ResultSet resSet = st.executeQuery(String.format("select e.name, e.surName, e.threadName, p.min, p.max from Employees e join EmployeePref p on e.employee_id=p.employee_id where e.group_id=%d", groupId));
			while (resSet.next()) {
				resultEmployeeSet.add(new Employee(resSet.getString("name"), resSet.getString("surName"),
					resSet.getString("threadName"), resSet.getByte("min"),
					resSet.getByte("max")));
			}
		}
		catch(SQLException e){
			throw e;
		}
		finally{
			if(st!=null){
				try{
					st.close();
				}
				catch(SQLException e){
					throw e;
				}
			}
		}
		return resultEmployeeSet;
	}
	
	@Override
	public Set<Employee> selectEmployees(long groupId)
			throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		Set<Employee> resultEmployeeSet = new java.util.HashSet<Employee>();
		try{
			resultEmployeeSet = selectEmployees(con, groupId);
		}
		catch(SQLException e){
			e.printStackTrace();
			System.err.println("Can not find Employees # " + this.getClass()
					+ " # " + e.getMessage());
			return null;
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not close connection # " + this.getClass()
					+ " # " + e.getMessage());
		}
		return resultEmployeeSet;
	}
}
