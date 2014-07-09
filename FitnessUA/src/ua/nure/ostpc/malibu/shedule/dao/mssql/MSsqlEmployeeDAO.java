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
			 res = st.executeUpdate(String.format("insert into EmpPrefs(EmployeeId,MinDays,MaxDays) values(%1$d,%2$d,%3$d)",emp.getEmployeeId(),emp.getMin(),emp.getMax()));
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
			java.sql.ResultSet resSet = st.executeQuery(String.format("select e.EmployeeId,"
					+ "e.ClubId, e.EmployeeGroupId,e.EmpEmployeegroupid,e.Firstname,e.Secondname,"
					+ "e.Lastname,e.Birthday,e.Address,e.PassportNumber,e.IdNumber,e.CellPhone,"
					+ "e.WorkPhone.e.HomePhone,e.Email,e.Education,e.Notes,e.PassportIssuedBy"
					+" from Employees e join EmpPrefs p "
					+ "on e.EmployeeId=p.employee_id where e.employee_id=%d",empId));
			emp = new Employee();
			emp.setAdress(resSet.getString("Address"));
			emp.setBirthday(resSet.getDate("Birthday"));
			emp.setCellPhone(resSet.getString("CellPhone"));
			emp.setClubId(resSet.getLong("ClubId"));
			emp.setEducation(resSet.getString("Education"));
			emp.setEmail(resSet.getString("Email"));
			emp.setEmpEmployeegroupid(resSet.getLong("EmpEmployeegroupid"));
			emp.setEmployeeGroupId(resSet.getLong("EmployeeGroupId"));
			emp.setEmployeeId(resSet.getLong("EmployeeId"));
			emp.setFirstName(resSet.getString("Firstname"));
			emp.setSureName(resSet.getString("Secondname"));
			emp.setLastName(resSet.getString("Lastname"));
			emp.setHomePhone(resSet.getString("HomePhone"));
			emp.setIdNumber(resSet.getString("IdNumber"));
			emp.setMax(resSet.getInt("MaxDays"));
			emp.setMin(resSet.getInt("MinDays"));
			emp.setNotes(resSet.getString("Notes"));
			emp.setPassportIssuedBy(resSet.getString("PassportIssuedBy"));
			emp.setPassportNumber(resSet.getString("PassportNumber"));
			emp.setWorkPhone(resSet.getString("WorkPhone"));
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
			 res = st.executeUpdate(String.format("update EmpPrefs set MinDays = %1$d,"
			 		+ " %2$d = MaxDays where employee_id=%3$d",emp.getMin(),
			 		emp.getMax(),emp.getEmployeeId()));
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
			java.sql.ResultSet resSet = st.executeQuery(String.format("select e.EmployeeId,"
					+ "e.ClubId, e.EmployeeGroupId,e.EmpEmployeegroupid,e.Firstname,e.Secondname,"
					+ "e.Lastname,e.Birthday,e.Address,e.PassportNumber,e.IdNumber,e.CellPhone,"
					+ "e.WorkPhone.e.HomePhone,e.Email,e.Education,e.Notes,e.PassportIssuedBy"
					+" from Employees e join EmpPrefs p on e.employee_id=p.employee_id where e.group_id=%d", groupId));			
			while (resSet.next()) {
				Employee emp = new Employee();
				emp.setAdress(resSet.getString("Address"));
				emp.setBirthday(resSet.getDate("Birthday"));
				emp.setCellPhone(resSet.getString("CellPhone"));
				emp.setClubId(resSet.getLong("ClubId"));
				emp.setEducation(resSet.getString("Education"));
				emp.setEmail(resSet.getString("Email"));
				emp.setEmpEmployeegroupid(resSet.getLong("EmpEmployeegroupid"));
				emp.setEmployeeGroupId(resSet.getLong("EmployeeGroupId"));
				emp.setEmployeeId(resSet.getLong("EmployeeId"));
				emp.setFirstName(resSet.getString("Firstname"));
				emp.setSureName(resSet.getString("Secondname"));
				emp.setLastName(resSet.getString("Lastname"));
				emp.setHomePhone(resSet.getString("HomePhone"));
				emp.setIdNumber(resSet.getString("IdNumber"));
				emp.setMax(resSet.getInt("MaxDays"));
				emp.setMin(resSet.getInt("MinDays"));
				emp.setNotes(resSet.getString("Notes"));
				emp.setPassportIssuedBy(resSet.getString("PassportIssuedBy"));
				emp.setPassportNumber(resSet.getString("PassportNumber"));
				emp.setWorkPhone(resSet.getString("WorkPhone"));
				resultEmployeeSet.add(emp);
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
