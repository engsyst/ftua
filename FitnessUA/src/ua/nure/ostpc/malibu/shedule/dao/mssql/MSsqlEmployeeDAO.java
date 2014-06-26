package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import javax.sql.RowSet;

import org.apache.tomcat.dbcp.pool.impl.StackObjectPool;

import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.entity.EmpPrferences;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

public class MSsqlEmployeeDAO implements EmployeeDAO {

	@Override
	public Employee findEmployee() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RowSet selectEmployeesRS() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int insertEmployee(Employee emp) {
		Connection con = MSsqlDAOFactory.createConnection();
		int updateResult = 0;
		try {
			updateResult = insertEmployee(con, emp);
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

	public int insertEmployee(Connection con, Employee emp) throws SQLException {
		Statement st = con.createStatement();
		return st.executeUpdate("....");
	}

	@Override
	public boolean deleteEmployee(Employee emp) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateEmployee(Employee emp) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Employee> selectEmployeesTO() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int insertEmployeeWithPrefs(Employee emp, EmpPrferences ep)
			throws SQLException {
		Connection con = MSsqlDAOFactory.createConnection();
		insertEmployeeWithPrefs(con, emp, ep);
		con.commit();
		con.close();
		return 0;
	}

	public int insertEmployeeWithPrefs(Connection con, Employee emp,
			EmpPrferences ep) throws SQLException {
		insertEmployee(con, emp);
		Statement st = con.createStatement();
		st.executeUpdate("");
		return 0;
	}

}
