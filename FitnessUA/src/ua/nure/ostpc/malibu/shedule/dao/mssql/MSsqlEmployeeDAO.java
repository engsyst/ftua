package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.mapper.MapperParameters;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

public class MSsqlEmployeeDAO implements EmployeeDAO {
	private static final String SQL__FIND_EMPLOYEES_BY_ASSIGNMENT_ID = "SELECT e.EmployeeId, e.ClubId, "
			+ "e.EmployeeGroupId, e.Firstname, e.Secondname, e.Lastname, e.Birthday, e.Address, "
			+ "e.PassportNumber, e.IdNumber, e.CellPhone, e.WorkPhone, e.HomePhone, e.Email, e.Education, "
			+ "e.Notes, e.PassportIssuedBy, EmpPrefs.MinDays, EmpPrefs.MaxDays "
			+ "FROM Employees e "
			+ "JOIN EmployeeToAssignment ON EmployeeToAssignment.EmployeeId=e.EmployeeId AND EmployeeToAssignment.AssignmentId=? "
			+ "JOIN EmpPrefs ON EmpPrefs.EmployeeId=e.EmployeeId;";

	public int insertEmployeePrefs(Connection con, Employee emp)
			throws SQLException {
		Statement st = null;
		int res = 0;
		try {
			st = con.createStatement();
			res = st.executeUpdate(String
					.format("insert into EmpPrefs(EmployeeId,MinDays,MaxDays) values(%1$d,%2$d,%3$d)",
							emp.getEmployeeId(), emp.getMin(), emp.getMaxDays()));
		} catch (SQLException e) {
			throw e;
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
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

	public Employee findEmployee(Connection con, long empId)
			throws SQLException {
		Employee emp = null;
		Statement st = null;
		try {
			st = con.createStatement();
			java.sql.ResultSet resSet = st
					.executeQuery(String
							.format("select e.EmployeeId,"
									+ "e.ClubId, e.EmployeeGroupId,e.Firstname,e.Secondname,"
									+ "e.Lastname,e.Birthday,e.Address,e.PassportNumber,e.IdNumber,e.CellPhone,"
									+ "e.WorkPhone.e.HomePhone,e.Email,e.Education,e.Notes,e.PassportIssuedBy"
									+ " from Employees e join EmpPrefs p "
									+ "on e.EmployeeId=p.employee_id where e.employee_id=%d",
									empId));
			emp = new Employee();
			emp.setAddress(resSet.getString("Address"));
			emp.setBirthday(resSet.getDate("Birthday"));
			emp.setCellPhone(resSet.getString("CellPhone"));
			emp.setClubId(resSet.getLong("ClubId"));
			emp.setEducation(resSet.getString("Education"));
			emp.setEmail(resSet.getString("Email"));
			emp.setEmployeeGroupId(resSet.getLong("EmployeeGroupId"));
			emp.setEmployeeId(resSet.getLong("EmployeeId"));
			emp.setFirstName(resSet.getString("Firstname"));
			emp.setSecondName(resSet.getString("Secondname"));
			emp.setLastName(resSet.getString("Lastname"));
			emp.setHomePhone(resSet.getString("HomePhone"));
			emp.setIdNumber(resSet.getString("IdNumber"));
			emp.setMaxDays(resSet.getInt("MaxDays"));
			emp.setMinDays(resSet.getInt("MinDays"));
			emp.setNotes(resSet.getString("Notes"));
			emp.setPassportIssuedBy(resSet.getString("PassportIssuedBy"));
			emp.setPassportNumber(resSet.getString("PassportNumber"));
			emp.setWorkPhone(resSet.getString("WorkPhone"));
		} catch (SQLException e) {
			throw e;
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
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
		try {
			emp = findEmployee(con, empId);
		} catch (SQLException e) {
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

	public boolean updateEmployeePrefs(Connection con, Employee emp)
			throws SQLException {
		Statement st = null;
		int res = 0;
		try {
			st = con.createStatement();
			res = st.executeUpdate(String.format(
					"update EmpPrefs set MinDays = %1$d,"
							+ " %2$d = MaxDays where employee_id=%3$d",
					emp.getMin(), emp.getMaxDays(), emp.getEmployeeId()));
		} catch (SQLException e) {
			throw e;
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
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
		try {
			st = con.createStatement();
			java.sql.ResultSet resSet = st
					.executeQuery(String
							.format("select e.EmployeeId,"
									+ "e.ClubId, e.EmployeeGroupId,e.Firstname,e.Secondname,"
									+ "e.Lastname,e.Birthday,e.Address,e.PassportNumber,e.IdNumber,e.CellPhone,"
									+ "e.WorkPhone.e.HomePhone,e.Email,e.Education,e.Notes,e.PassportIssuedBy"
									+ " from Employees e join EmpPrefs p on e.employee_id=p.employee_id where e.group_id=%d",
									groupId));
			while (resSet.next()) {
				Employee emp = new Employee();
				emp.setAddress(resSet.getString("Address"));
				emp.setBirthday(resSet.getDate("Birthday"));
				emp.setCellPhone(resSet.getString("CellPhone"));
				emp.setClubId(resSet.getLong("ClubId"));
				emp.setEducation(resSet.getString("Education"));
				emp.setEmail(resSet.getString("Email"));
				emp.setEmployeeGroupId(resSet.getLong("EmployeeGroupId"));
				emp.setEmployeeId(resSet.getLong("EmployeeId"));
				emp.setFirstName(resSet.getString("Firstname"));
				emp.setSecondName(resSet.getString("Secondname"));
				emp.setLastName(resSet.getString("Lastname"));
				emp.setHomePhone(resSet.getString("HomePhone"));
				emp.setIdNumber(resSet.getString("IdNumber"));
				emp.setMaxDays(resSet.getInt("MaxDays"));
				emp.setMinDays(resSet.getInt("MinDays"));
				emp.setNotes(resSet.getString("Notes"));
				emp.setPassportIssuedBy(resSet.getString("PassportIssuedBy"));
				emp.setPassportNumber(resSet.getString("PassportNumber"));
				emp.setWorkPhone(resSet.getString("WorkPhone"));
				resultEmployeeSet.add(emp);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					throw e;
				}
			}
		}
		return resultEmployeeSet;
	}

	@Override
	public Set<Employee> selectEmployees(long groupId) throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		Set<Employee> resultEmployeeSet = new java.util.HashSet<Employee>();
		try {
			resultEmployeeSet = selectEmployees(con, groupId);
		} catch (SQLException e) {
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

	public List<Employee> findEmployeesByAssignmentId(Connection con,
			long assignmentId) throws SQLException {
		List<Employee> employees = new ArrayList<Employee>();
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__FIND_EMPLOYEES_BY_ASSIGNMENT_ID);
			pstmt.setLong(1, assignmentId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Employee employee = unMapEmployee(rs);
				employees.add(employee);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					throw e;
				}
			}
		}
		return employees;
	}

	private Employee unMapEmployee(ResultSet rs) throws SQLException {
		Employee employee = new Employee();
		employee.setEmployeeId(rs.getLong(MapperParameters.EMPLOYEE__ID));
		employee.setFirstName(rs
				.getString(MapperParameters.EMPLOYEE__FIRSTNAME));
		employee.setSecondName(rs
				.getString(MapperParameters.EMPLOYEE__SECONDNAME));
		employee.setLastName(rs.getString(MapperParameters.EMPLOYEE__LASTNAME));
		employee.setClubId(rs.getLong(MapperParameters.EMPLOYEE__CLUB_ID));
		employee.setEmployeeGroupId(rs
				.getLong(MapperParameters.EMPLOYEE__GROUP_ID));
		employee.setBirthday(rs.getDate(MapperParameters.EMPLOYEE__BIRTHDAY));
		employee.setAddress(rs.getString(MapperParameters.EMPLOYEE__ADDRESS));
		employee.setPassportNumber(rs
				.getString(MapperParameters.EMPLOYEE__PASSPORT_NUMBER));
		employee.setIdNumber(rs.getString(MapperParameters.EMPLOYEE__ID_NUMBER));
		employee.setCellPhone(rs
				.getString(MapperParameters.EMPLOYEE__CELL_PHONE));
		employee.setWorkPhone(rs
				.getString(MapperParameters.EMPLOYEE__WORK_PHONE));
		employee.setHomePhone(rs
				.getString(MapperParameters.EMPLOYEE__HOME_PHONE));
		employee.setEmail(rs.getString(MapperParameters.EMPLOYEE__EMAIL));
		employee.setEducation(rs
				.getString(MapperParameters.EMPLOYEE__EDUCATION));
		employee.setNotes(rs.getString(MapperParameters.EMPLOYEE__NOTES));
		employee.setPassportIssuedBy(rs
				.getString(MapperParameters.EMPLOYEE__PASSPORT_ISSUED_BY));
		employee.setMinAndMaxDays(
				rs.getInt(MapperParameters.EMPLOYEE__MIN_DAYS),
				rs.getInt(MapperParameters.EMPLOYEE__MAX_DAYS));
		return employee;
	}
}
