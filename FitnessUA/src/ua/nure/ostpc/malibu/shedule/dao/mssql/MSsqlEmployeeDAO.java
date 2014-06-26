package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

public class MSsqlEmployeeDAO implements EmployeeDAO {
	
	public int insertEmployeePrefs(Connection con, Employee emp) throws SQLException {
		Statement st = con.createStatement();
		return st.executeUpdate("....");
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
		}
		return updateResult;
	}

	@Override
	public Employee findEmployee(long empId) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateEmployeePrefs(Employee emp) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<Employee> selectEmployees(long groupId)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
}
