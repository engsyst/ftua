package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;

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
			emp.setAddress(resSet.getString(MapperParameters.EMPLOYEE__ADDRESS));
			emp.setBirthday(resSet.getDate(MapperParameters.EMPLOYEE__BIRTHDAY));
			emp.setCellPhone(resSet
					.getString(MapperParameters.EMPLOYEE__CELL_PHONE));
			emp.setClubId(resSet.getLong(MapperParameters.EMPLOYEE__CLUB_ID));
			emp.setEducation(resSet
					.getString(MapperParameters.EMPLOYEE__EDUCATION));
			emp.setEmail(resSet.getString(MapperParameters.EMPLOYEE__EMAIL));
			emp.setEmployeeGroupId(resSet
					.getLong(MapperParameters.EMPLOYEE__GROUP_ID));
			emp.setEmployeeId(resSet.getLong(MapperParameters.EMPLOYEE__ID));
			emp.setFirstName(resSet
					.getString(MapperParameters.EMPLOYEE__FIRSTNAME));
			emp.setSecondName(resSet
					.getString(MapperParameters.EMPLOYEE__SECONDNAME));
			emp.setLastName(resSet
					.getString(MapperParameters.EMPLOYEE__LASTNAME));
			emp.setHomePhone(resSet
					.getString(MapperParameters.EMPLOYEE__HOME_PHONE));
			emp.setIdNumber(resSet
					.getString(MapperParameters.EMPLOYEE__ID_NUMBER));
			emp.setMaxDays(resSet.getInt(MapperParameters.EMPLOYEE__MAX_DAYS));
			emp.setMinDays(resSet.getInt(MapperParameters.EMPLOYEE__MIN_DAYS));
			emp.setNotes(resSet.getString(MapperParameters.EMPLOYEE__NOTES));
			emp.setPassportIssuedBy(resSet
					.getString(MapperParameters.EMPLOYEE__PASSPORT_ISSUED_BY));
			emp.setPassportNumber(resSet
					.getString(MapperParameters.EMPLOYEE__PASSPORT_NUMBER));
			emp.setWorkPhone(resSet
					.getString(MapperParameters.EMPLOYEE__WORK_PHONE));
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
				emp.setAddress(resSet
						.getString(MapperParameters.EMPLOYEE__ADDRESS));
				emp.setBirthday(resSet
						.getDate(MapperParameters.EMPLOYEE__BIRTHDAY));
				emp.setCellPhone(resSet
						.getString(MapperParameters.EMPLOYEE__CELL_PHONE));
				emp.setClubId(resSet
						.getLong(MapperParameters.EMPLOYEE__CLUB_ID));
				emp.setEducation(resSet
						.getString(MapperParameters.EMPLOYEE__EDUCATION));
				emp.setEmail(resSet.getString(MapperParameters.EMPLOYEE__EMAIL));
				emp.setEmployeeGroupId(resSet
						.getLong(MapperParameters.EMPLOYEE__GROUP_ID));
				emp.setEmployeeId(resSet.getLong(MapperParameters.EMPLOYEE__ID));
				emp.setFirstName(resSet
						.getString(MapperParameters.EMPLOYEE__FIRSTNAME));
				emp.setSecondName(resSet
						.getString(MapperParameters.EMPLOYEE__SECONDNAME));
				emp.setLastName(resSet
						.getString(MapperParameters.EMPLOYEE__LASTNAME));
				emp.setHomePhone(resSet
						.getString(MapperParameters.EMPLOYEE__HOME_PHONE));
				emp.setIdNumber(resSet
						.getString(MapperParameters.EMPLOYEE__ID_NUMBER));
				emp.setMaxDays(resSet
						.getInt(MapperParameters.EMPLOYEE__MAX_DAYS));
				emp.setMinDays(resSet
						.getInt(MapperParameters.EMPLOYEE__MIN_DAYS));
				emp.setNotes(resSet.getString(MapperParameters.EMPLOYEE__NOTES));
				emp.setPassportIssuedBy(resSet
						.getString(MapperParameters.EMPLOYEE__PASSPORT_ISSUED_BY));
				emp.setPassportNumber(resSet
						.getString(MapperParameters.EMPLOYEE__PASSPORT_NUMBER));
				emp.setWorkPhone(resSet
						.getString(MapperParameters.EMPLOYEE__WORK_PHONE));
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

	@Override
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

	@Override
	public Collection<Employee> getMalibuEmployees() throws SQLException {
		// ������� � ����� ���� ��� � ������?
		Connection con = MSsqlDAOFactory.getConnection();
		Collection<Employee> resultEmpSet = new ArrayList<Employee>();
		try {
			resultEmpSet = getMalibuEmployees(con);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not find Clubs # " + this.getClass()
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
		return resultEmpSet;
	}

	public Collection<Employee> getMalibuEmployees(Connection con)
			throws SQLException {
		Statement st = null;
		Collection<Employee> resultEmpSet = new ArrayList<Employee>();
		try {
			st = con.createStatement();
			java.sql.ResultSet resSet = st.executeQuery(String
					.format("SELECT e.EmployeeId, e.Firstname,"
							+ "e.Secondname, e.Lastname from Employees e"));
			while (resSet.next()) {
				Employee emp = new Employee(resSet.getString("Firstname"),
						resSet.getString("Secondname"), resSet
						.getString("Lastname"), 0, 7);
				emp.setEmployeeId(resSet.getLong("EmployeeId"));
				resultEmpSet.add(emp);
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
		return resultEmpSet;
	}

	public void pushToExcel(Schedule schedule) {
		// to do ;

	}

}
