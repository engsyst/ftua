package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;

public class MSsqlEmployeeDAO implements EmployeeDAO {
	private static final Logger log = Logger.getLogger(MSsqlEmployeeDAO.class);

	private static final String SQL__FIND_EMPLOYEES_BY_ASSIGNMENT_ID = "SELECT e.EmployeeId, "
			+ "e.Firstname, e.Secondname, e.Lastname, e.Birthday, e.Address, "
			+ "e.Passportint, e.Idint, e.CellPhone, e.WorkPhone, e.HomePhone, e.Email, e.Education, "
			+ "e.Notes, e.PassportIssuedBy, EmpPrefs.MinDays, EmpPrefs.MaxDays "
			+ "FROM Employee e "
			+ "INNER JOIN EmployeeToAssignment ON EmployeeToAssignment.EmployeeId=e.EmployeeId AND EmployeeToAssignment.AssignmentId=? "
			+ "INNER JOIN EmpPrefs ON EmpPrefs.EmployeeId=e.EmployeeId;";
	private static final String SQL__FIND_SCHEDULE_EMPLOYEES = "SELECT e.EmployeeId, "
			+ "e.Firstname, e.Secondname, e.Lastname, e.Birthday, e.Address, "
			+ "e.Passportint, e.Idint, e.CellPhone, e.WorkPhone, e.HomePhone, e.Email, e.Education, "
			+ "e.Notes, e.PassportIssuedBy, EmpPrefs.MinDays, EmpPrefs.MaxDays "
			+ "FROM Employee e "
			+ "INNER JOIN EmpPrefs ON EmpPrefs.EmployeeId=e.EmployeeId;";

	private static final String SQL__FIND_EMPLOYEES_BY_SHIFT_ID = "SELECT e.EmployeeId, "
			+ "e.Firstname, e.Secondname, e.Lastname, e.Birthday, e.Address, "
			+ "e.Passportint, e.Idint, e.CellPhone, e.WorkPhone, e.HomePhone, e.Email, e.Education, "
			+ "e.Notes, e.PassportIssuedBy, EmpPrefs.MinDays, EmpPrefs.MaxDays "
			+ "FROM Employee e "
			+ "INNER JOIN EmpPrefs ON EmpPrefs.EmployeeId=e.EmployeeId "
			+ "INNER JOIN Assignment ON Assignment.EmployeeId=e.EmployeeId AND Assignment.ShiftId=?;";
	
	private static final String SQL__DELETE_EMPLOYEE = "DELETE FROM Employee e WHERE e.EmployeeId = ?";
	
	private static final String SQL__FIND_OUR_EMPLOYEES = "SELECT * FROM Employee e where e.EmployeeId not in (select e2.OurEmployeeId from ComplianceEmployee e2)";
	
	private static final String SQL__INSERT_EMPLOYEE = "INSERT INTO Employee (EmployeeId, "
			+ "Firstname, Secondname, Lastname, Birthday, Address, "
			+ "Passportint, Idint, CellPhone, WorkPhone, HomePhone, Email, Education, "
			+ "Notes, PassportIssuedBy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	
	//По какому признаку соединять?
	private static final String SQL__INSERT_EMPLOYEE_TO_CONFORMITY = "INSERT INTO ComplianceEmployee (OriginalEmployeeId, OurEmployeeId) VALUES (?, "
			+ "(SELECT e.EmployeeId FROM Employee e WHERE e.Lastname = ?));";


	
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
		Employee employee = null;
		Statement st = null;
		try {
			st = con.createStatement();
			ResultSet rs = st
					.executeQuery(String
							.format("select e.EmployeeId,"
									+ "e.Firstname,e.Secondname,"
									+ "e.Lastname,e.Birthday, e.[Address], e.Passportint,e.Idint,e.CellPhone,"
									+ "e.WorkPhone,e.HomePhone,e.Email,e.Education,e.Notes,e.PassportIssuedBy, p.MaxDays, p.MinDays"
									+ " from Employee e inner join EmpPrefs p "
									+ "on e.EmployeeId=p.EmployeeId where e.EmployeeId=%d",
									empId));
			if (rs.next()) {
				employee = unMapScheduleEmployee(rs);
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
		return employee;
	}

	@Override
	public Employee findEmployee(long employeeId) throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		Employee emp = null;
		try {
			emp = findEmployee(con, employeeId);
		} catch (SQLException e) {
			log.error("Can not find Employee", e);
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
	public boolean updateEmployeePrefs(Employee employee) throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		boolean updateResult = false;
		try {
			updateResult = updateEmployeePrefs(con, employee);
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
							+ " %2$d = MaxDays where EmployeeId=%3$d",
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

	@Override
	public Collection<Employee> findEmployeesByAssignmentId(long assignmentId) {
		Connection con = null;
		Collection<Employee> employees = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			employees = findEmployeesByAssignmentId(con, assignmentId);
		} catch (SQLException e) {
			log.error("Can not find employees by assignment id.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return employees;
	}

	private Collection<Employee> findEmployeesByAssignmentId(Connection con,
			long assignmentId) throws SQLException {
		Collection<Employee> employees = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__FIND_EMPLOYEES_BY_ASSIGNMENT_ID);
			pstmt.setLong(1, assignmentId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.isBeforeFirst()) {
				employees = new ArrayList<Employee>();
			}
			while (rs.next()) {
				Employee employee = unMapScheduleEmployee(rs);
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

	@Override
	public List<Employee> getScheduleEmployees() {
		Connection con = null;
		List<Employee> employees = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			employees = getScheduleEmployees(con);
		} catch (SQLException e) {
			log.error("Can not find schedule employees.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return employees;
	}

	private List<Employee> getScheduleEmployees(Connection con)
			throws SQLException {
		List<Employee> employees = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__FIND_SCHEDULE_EMPLOYEES);
			ResultSet rs = pstmt.executeQuery();
			if (rs.isBeforeFirst()) {
				employees = new ArrayList<Employee>();
			}
			while (rs.next()) {
				Employee employee = unMapScheduleEmployee(rs);
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

	@Override
	public List<Employee> getEmployeesByShiftId(long shiftId) {
		Connection con = null;
		List<Employee> employees = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			employees = getEmployeesByShiftId(con, shiftId);
		} catch (SQLException e) {
			log.error("Can not find employees by shift id.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return employees;
	}

	public List<Employee> getEmployeesByShiftId(Connection con, long shiftId)
			throws SQLException {
		List<Employee> employees = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__FIND_EMPLOYEES_BY_SHIFT_ID);
			pstmt.setLong(1, shiftId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.isBeforeFirst()) {
				employees = new ArrayList<Employee>();
			}
			while (rs.next()) {
				Employee employee = unMapScheduleEmployee(rs);
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

	private Employee unMapScheduleEmployee(ResultSet rs) throws SQLException {
		Employee employee = new Employee();
		employee.setEmployeeId(rs.getLong(MapperParameters.EMPLOYEE__ID));
		employee.setFirstName(rs
				.getString(MapperParameters.EMPLOYEE__FIRSTNAME));
		employee.setSecondName(rs
				.getString(MapperParameters.EMPLOYEE__SECONDNAME));
		employee.setLastName(rs.getString(MapperParameters.EMPLOYEE__LASTNAME));
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
		Connection con = MSsqlDAOFactory.getConnection();
		Collection<Employee> resultEmpSet = new ArrayList<Employee>();
		try {
			resultEmpSet = getMalibuEmployees(con);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not find Malibu employees # "
					+ this.getClass() + " # " + e.getMessage());
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
				Employee emp = new Employee(
						resSet.getString(MapperParameters.EMPLOYEE__FIRSTNAME),
						resSet.getString(MapperParameters.EMPLOYEE__SECONDNAME),
						resSet.getString(MapperParameters.EMPLOYEE__LASTNAME),
						0, 7);
				emp.setEmployeeId(resSet.getLong(MapperParameters.EMPLOYEE__ID));
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
	
	public Boolean deleteEmployee(long id) {
		Connection con = null;
		Boolean result = false;
		try {
			con = MSsqlDAOFactory.getConnection();
			deleteEmployee(id, con);
			result = true;
		} catch (SQLException e) {
			log.error("Can not delete employee.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return result;
	}

	private void deleteEmployee(long id, Connection con) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__DELETE_EMPLOYEE);
			pstmt.setLong(1, id);
			pstmt.executeUpdate();
			con.commit();
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

	}

	public Collection<Employee> getOnlyOurEmployees() {
		Connection con = null;
		Collection<Employee> ourEmployees = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			ourEmployees = getOnlyOurEmployees(con);
		} catch (SQLException e) {
			log.error("Can not get our clubs.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return ourEmployees;
	}

	private Collection<Employee> getOnlyOurEmployees(Connection con) throws SQLException {
		Statement stmt = null;
		Collection<Employee> ourEmployees = new ArrayList<Employee>();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__FIND_OUR_EMPLOYEES);
			while (rs.next()) {
				Employee emp = unMapEmployee(rs);
				ourEmployees.add(emp);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					throw e;
				}
			}
		}
		return ourEmployees;
	}
	
	public boolean insertEmployeesWithConformity(Collection<Employee> clubs) {
		boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = insertEmployeesWithConformity(clubs, con);
		} catch (SQLException e) {
			log.error("Can not insert clubs.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return result;
	}

	private boolean insertEmployeesWithConformity(Collection<Employee> emps,
			Connection con) throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_EMPLOYEE);
			pstmt2 = con.prepareStatement(SQL__INSERT_EMPLOYEE_TO_CONFORMITY);
			for (Employee emp : emps) {
				mapEmployeeForInsert(emp, pstmt);
				/* По какому признаку соединять?
				pstmt2.setLong(1, club.getClubId());
				pstmt2.setString(2, club.getTitle());
				*/
				pstmt.addBatch();
				pstmt2.addBatch();
			}
			result = pstmt.executeBatch().length == emps.size();
			result = pstmt2.executeBatch().length == emps.size() && result;
			con.commit();
		} catch (SQLException e) {
			throw e;
		}
		return result;
	}


	public void pushToExcel(Schedule schedule) {
		// to do ;

	}
	
	
	private Employee unMapEmployee(ResultSet rs) throws SQLException {
		Employee emp = new Employee();
		emp.setFirstName(rs.getString(MapperParameters.EMPLOYEE__FIRSTNAME));
		emp.setLastName(rs.getString(MapperParameters.EMPLOYEE__LASTNAME));
		emp.setAddress(rs.getString(MapperParameters.EMPLOYEE__ADDRESS));
		emp.setBirthday(rs.getDate(MapperParameters.EMPLOYEE__BIRTHDAY));
		emp.setCellPhone(rs.getString(MapperParameters.EMPLOYEE__CELL_PHONE));
		emp.setEducation(rs.getString(MapperParameters.EMPLOYEE__EDUCATION));
		emp.setEmail(rs.getString(MapperParameters.EMPLOYEE__EMAIL));
		emp.setEmployeeId(rs.getLong(MapperParameters.EMPLOYEE__ID));
		emp.setHomePhone(rs.getString(MapperParameters.EMPLOYEE__HOME_PHONE));
		emp.setEmpPrefs(rs.getInt(MapperParameters.EMPLOYEE__MIN_DAYS), rs.getInt(MapperParameters.EMPLOYEE__MAX_DAYS));;
		emp.setIdNumber(rs.getString(MapperParameters.EMPLOYEE__ID_NUMBER));
		emp.setNotes(rs.getString(MapperParameters.EMPLOYEE__NOTES));
		emp.setPassportIssuedBy(rs.getString(MapperParameters.EMPLOYEE__PASSPORT_ISSUED_BY));
		emp.setPassportNumber(rs.getString(MapperParameters.EMPLOYEE__PASSPORT_NUMBER));
		emp.setSecondName(rs.getString(MapperParameters.EMPLOYEE__SECONDNAME));
		emp.setWorkPhone(rs.getString(MapperParameters.EMPLOYEE__WORK_PHONE));
		return emp;
	}
	
	private void mapEmployeeForInsert(Employee emp, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setLong(1, emp.getEmployeeId());
		pstmt.setString(2, emp.getFirstName());
		pstmt.setString(3, emp.getSecondName());
		pstmt.setString(4, emp.getLastName());
		pstmt.setDate(5, new Date(emp.getBirthday().getTime()));
		pstmt.setString(6, emp.getAddress());
		pstmt.setString(7, emp.getPassportNumber());
		pstmt.setString(8, emp.getIdNumber());
		pstmt.setString(9, emp.getCellPhone());
		pstmt.setString(10, emp.getWorkPhone());
		pstmt.setString(11, emp.getHomePhone());
		pstmt.setString(12, emp.getEmail());
		pstmt.setString(13, emp.getEducation());
		pstmt.setString(14, emp.getNotes());
		pstmt.setString(15, emp.getPassportIssuedBy());
	}

}
