package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Right;
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

	private static final String SQL__DELETE_EMPLOYEE = "DELETE FROM Employee WHERE EmployeeId = ?";

	private static final String SQL__FIND_OUR_EMPLOYEES = "SELECT * FROM Employee e inner join EmpPrefs p "
			+ "on e.EmployeeId=p.EmployeeId where e.EmployeeId not in (select e2.OurEmployeeId from ComplianceEmployee e2)";

	private static final String SQL__INSERT_EMPLOYEE = "INSERT INTO Employee ("
			+ "Firstname, Secondname, Lastname, Birthday, Address, "
			+ "Passportint, Idint, CellPhone, WorkPhone, HomePhone, Email, Education, "
			+ "Notes, PassportIssuedBy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String SQL__INSERT_EMPLOYEE_TO_CONFORMITY = "INSERT INTO ComplianceEmployee (OriginalEmployeeId, OurEmployeeId) VALUES (?, "
			+ "?);";

	private static final String SQL__JOIN_CONFORMITY = "SELECT e1.*, e2.OriginalEmployeeId, 0 as MinDays, 7 as MaxDays from Employee e1 INNER JOIN ComplianceEmployee e2 "
			+ "ON e1.EmployeeId=e2.OurEmployeeId";

	private static final String SQL__ROLE_FOR_EMPLOYEES = "select eur.EmployeeId, r.Rights from EmployeeUserRole eur, Role r where eur.RoleId = r.RoleId";

	private static final String SQL__FIND_ALL_EMPLOYEE_ID = "select EmployeeId from Employee";

	private static final String SQL__FIND_ALL_EMPLOYEE = "select e.*,0 as MinDays, 7 as MaxDays from Employee e order by LastName";

	private static final String SQL__INSERT_ROLE = "insert into EmployeeUserRole (RoleId, EmployeeId) values ((select r.RoleId from Role r where r.Rights=?), ?)";

	private static final String SQL__DELETE_ROLE = "delete from EmployeeUserRole where RoleId=(select r.RoleId from Role r where r.Rights=?) and EmployeeId=?";

	private static final String SQL__UPDATE_EMPLOYEE = "update Employee set Firstname = ?, Secondname = ?, Lastname = ?, Birthday = ?, Address = ?, "
			+ "Passportint = ?, Idint = ?, CellPhone = ?, WorkPhone = ?, HomePhone = ?, Email = ?, Education = ?, "
			+ "Notes = ?, PassportIssuedBy = ? where EmployeeId = ?";

	private static final String SQL__FIND_EMAIL_LIST_FOR_ROLE = "SELECT Email FROM Employee e "
			+ "JOIN  EmployeeUserRole a ON e.Employeeid=a.Employeeid "
			+ "JOIN Role r on a.Roleid=r.Roleid where r.Rights=?;";

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
	public Collection<Employee> getMalibuEmployees() {
		Connection con = null;
		Collection<Employee> resultEmpSet = new ArrayList<Employee>();
		try {
			con = MSsqlDAOFactory.getConnection();
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
					.format("SELECT * from Employees e"));
			while (resSet.next()) {
				Employee emp = unMapEmployee(resSet);
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

	private Collection<Employee> getOnlyOurEmployees(Connection con)
			throws SQLException {
		Statement stmt = null;
		Collection<Employee> ourEmployees = new ArrayList<Employee>();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__FIND_OUR_EMPLOYEES);
			while (rs.next()) {
				Employee emp = unMapScheduleEmployee(rs);
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

	public boolean insertEmployeesWithConformity(Collection<Employee> employees) {
		boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = insertEmployeesWithConformity(employees, con);
		} catch (SQLException e) {
			log.error("Can not insert employees.", e);
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
			pstmt = con.prepareStatement(SQL__INSERT_EMPLOYEE,
					Statement.RETURN_GENERATED_KEYS);
			pstmt2 = con.prepareStatement(SQL__INSERT_EMPLOYEE_TO_CONFORMITY);
			for (Employee emp : emps) {
				mapEmployeeForInsert(emp, pstmt);
				if (pstmt.executeUpdate() != 1)
					return false;
				ResultSet rs = pstmt.getGeneratedKeys();
				long newId = 0;
				while (rs.next())
					newId = rs.getLong(1);
				pstmt2.setLong(1, emp.getEmployeeId());
				pstmt2.setLong(2, newId);
				pstmt2.addBatch();
			}
			result = pstmt2.executeBatch().length == emps.size();
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
		emp.setIdNumber(rs.getString("IdNumber"));
		emp.setNotes(rs.getString(MapperParameters.EMPLOYEE__NOTES));
		emp.setPassportIssuedBy(rs
				.getString(MapperParameters.EMPLOYEE__PASSPORT_ISSUED_BY));
		emp.setPassportNumber(rs.getString("PassportNumber"));
		emp.setSecondName(rs.getString(MapperParameters.EMPLOYEE__SECONDNAME));
		emp.setWorkPhone(rs.getString(MapperParameters.EMPLOYEE__WORK_PHONE));
		return emp;
	}

	private void mapEmployeeForInsert(Employee emp, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setString(1, emp.getFirstName());
		pstmt.setString(2, emp.getSecondName());
		pstmt.setString(3, emp.getLastName());
		pstmt.setDate(4, new Date(emp.getBirthday().getTime()));
		pstmt.setString(5, emp.getAddress());
		pstmt.setString(6, emp.getPassportNumber());
		pstmt.setString(7, emp.getIdNumber());
		pstmt.setString(8, emp.getCellPhone());
		pstmt.setString(9, emp.getWorkPhone() == null ? "" : emp.getWorkPhone());
		pstmt.setString(10,
				emp.getHomePhone() == null ? "" : emp.getHomePhone());
		pstmt.setString(11, emp.getEmail());
		pstmt.setString(12,
				emp.getEducation() == null ? "" : emp.getEducation());
		pstmt.setString(13, emp.getNotes() == null ? "" : emp.getNotes());
		pstmt.setString(
				14,
				emp.getPassportIssuedBy() == null ? "" : emp
						.getPassportIssuedBy());
	}

	@Override
	public Map<Long, Employee> getConformity() {
		Connection con = null;
		Map<Long, Employee> dict = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			dict = getConformity(con);
		} catch (SQLException e) {
			log.error("Can not get conformity dictionary.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return dict;
	}

	private Map<Long, Employee> getConformity(Connection con)
			throws SQLException {
		Statement stmt = null;
		Map<Long, Employee> dict = new HashMap<Long, Employee>();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__JOIN_CONFORMITY);
			while (rs.next()) {
				Employee employee = unMapScheduleEmployee(rs);
				dict.put(rs.getLong("OriginalEmployeeId"), employee);
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
		return dict;

	}

	@Override
	public Map<Long, Collection<Boolean>> getRolesForEmployee() {
		Connection con = null;
		Map<Long, Collection<Boolean>> roles = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			roles = getRolesForEmployee(con);
		} catch (SQLException e) {
			log.error("Can not get conformity dictionary.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return roles;
	}

	private Map<Long, Collection<Boolean>> getRolesForEmployee(Connection con)
			throws SQLException {
		Statement stmt = null;
		Map<Long, Collection<Boolean>> roles = new HashMap<Long, Collection<Boolean>>();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__FIND_ALL_EMPLOYEE_ID);
			while (rs.next()) {
				ArrayList<Boolean> r = new ArrayList<Boolean>();
				for (int i = 0; i < 3; i++)
					r.add(false);
				roles.put(rs.getLong(MapperParameters.EMPLOYEE__ID), r);
			}
			rs = stmt.executeQuery(SQL__ROLE_FOR_EMPLOYEES);
			while (rs.next()) {
				long id = rs.getLong(MapperParameters.EMPLOYEE__ID);
				if (roles.containsKey(id)) {
					switch (rs.getInt(MapperParameters.ROLE__RIGHTS)) {
					case 0:
						((ArrayList<Boolean>) roles.get(id)).set(1, true);
						break;
					case 1:
						((ArrayList<Boolean>) roles.get(id)).set(0, true);
						break;
					case 2:
						((ArrayList<Boolean>) roles.get(id)).set(2, true);
						break;
					}
				} else {
					ArrayList<Boolean> r = new ArrayList<Boolean>();
					for (int i = 0; i < 3; i++)
						r.add(false);
					switch (rs.getInt(MapperParameters.ROLE__RIGHTS)) {
					case 0:
						r.set(1, true);
						break;
					case 1:
						r.set(0, true);
						break;
					case 2:
						r.set(2, true);
						break;
					}
					roles.put(id, r);
				}
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
		return roles;
	}

	@Override
	public boolean setRolesForEmployees(
			Map<Integer, Collection<Long>> roleForInsert) {
		return workWithRoles(SQL__INSERT_ROLE, roleForInsert);
	}

	@Override
	public boolean deleteRolesForEmployees(
			Map<Integer, Collection<Long>> roleForDelete) {
		return workWithRoles(SQL__DELETE_ROLE, roleForDelete);
	}

	private boolean workWithRoles(String sql,
			Map<Integer, Collection<Long>> roles) {
		boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = workWithRoles(sql, roles, con);
		} catch (SQLException e) {
			log.error("Can not insert roles.", e);
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

	private boolean workWithRoles(String sql,
			Map<Integer, Collection<Long>> roles, Connection con)
			throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		try {
			int size = 0;
			pstmt = con.prepareStatement(sql);
			int right;
			for (int i = 1; i <= 3; i++) {
				switch (i) {
				case 2:
					right = 0;
					break;
				case 3:
					right = 2;
					break;
				default:
					right = i;
					break;
				}
				for (long j : roles.get(i)) {
					pstmt.setInt(1, right);
					pstmt.setLong(2, j);
					pstmt.addBatch();
				}
				size += roles.get(i).size();
			}
			result = pstmt.executeBatch().length == size;
			con.commit();
		} catch (SQLException e) {
			throw e;
		}
		return result;
	}

	@Override
	public boolean insertEmployeesWithConformityAndRoles(
			Map<Integer, Collection<Employee>> roleForInsert) {
		boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = insertEmployeesWithConformityAndRoles(roleForInsert, con);
		} catch (SQLException e) {
			log.error("Can not insert employees.", e);
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

	private boolean insertEmployeesWithConformityAndRoles(
			Map<Integer, Collection<Employee>> roleForInsert, Connection con)
			throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_EMPLOYEE,
					Statement.RETURN_GENERATED_KEYS);
			pstmt2 = con.prepareStatement(SQL__INSERT_EMPLOYEE_TO_CONFORMITY);
			int size = 0;
			Map<Integer, Collection<Long>> roles = new HashMap<Integer, Collection<Long>>();
			for (int i = 1; i <= 3; i++) {
				roles.put(i, new ArrayList<Long>());
				for (Employee emp : roleForInsert.get(i)) {
					mapEmployeeForInsert(emp, pstmt);
					if (pstmt.executeUpdate() != 1)
						return false;
					ResultSet rs = pstmt.getGeneratedKeys();
					long newId = 0;
					while (rs.next())
						newId = rs.getLong(1);
					pstmt2.setLong(1, emp.getEmployeeId());
					pstmt2.setLong(2, newId);
					roles.get(i).add(newId);
					pstmt2.addBatch();
				}
				size += roleForInsert.get(i).size();
			}
			result = pstmt2.executeBatch().length == size;
			con.commit();
			if (result)
				result = workWithRoles(SQL__INSERT_ROLE, roles, con);
		} catch (SQLException e) {
			throw e;
		}
		return result;
	}

	@Override
	public boolean insertEmployeesAndRoles(
			Map<Integer, Collection<Employee>> roleForInsert) {
		boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = insertEmployeesAndRoles(roleForInsert, con);
		} catch (SQLException e) {
			log.error("Can not insert employees.", e);
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

	private boolean insertEmployeesAndRoles(
			Map<Integer, Collection<Employee>> roleForInsert, Connection con)
			throws SQLException {
		boolean result = false;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_EMPLOYEE,
					Statement.RETURN_GENERATED_KEYS);
			Map<Integer, Collection<Long>> roles = new HashMap<Integer, Collection<Long>>();
			for (int i = 1; i <= 3; i++) {
				roles.put(i, new ArrayList<Long>());
				for (Employee emp : roleForInsert.get(i)) {
					mapEmployeeForInsert(emp, pstmt);
					if (pstmt.executeUpdate() != 1)
						return false;
					ResultSet rs = pstmt.getGeneratedKeys();
					long newId = 0;
					while (rs.next())
						newId = rs.getLong(1);
					roles.get(i).add(newId);
				}
			}
			con.commit();
			if (result)
				result = workWithRoles(SQL__INSERT_ROLE, roles, con);
		} catch (SQLException e) {
			throw e;
		}
		return result;
	}

	@Override
	public boolean insertEmployees(Collection<Employee> emps) {
		boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = insertEmployees(emps, con);
		} catch (SQLException e) {
			log.error("Can not insert employees.", e);
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

	private boolean insertEmployees(Collection<Employee> emps, Connection con)
			throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_EMPLOYEE);
			for (Employee emp : emps) {
				mapEmployeeForInsert(emp, pstmt);
				pstmt.addBatch();
			}
			result = pstmt.executeBatch().length == emps.size();
			con.commit();
		} catch (SQLException e) {
			throw e;
		}
		return result;
	}

	@Override
	public boolean updateEmployees(Collection<Employee> emps) {
		boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = updateEmployees(emps, con);
		} catch (SQLException e) {
			log.error("Can not insert employees.", e);
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

	private boolean updateEmployees(Collection<Employee> emps, Connection con)
			throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__UPDATE_EMPLOYEE);
			for (Employee emp : emps) {
				mapEmployeeForInsert(emp, pstmt);
				pstmt.setLong(15, emp.getEmployeeId());
				pstmt.addBatch();
			}
			result = pstmt.executeBatch().length == emps.size();
			con.commit();
		} catch (SQLException e) {
			throw e;
		}
		return result;
	}

	@Override
	public Collection<Employee> getAllEmployee() {
		Connection con = null;
		Collection<Employee> resultEmpSet = new ArrayList<Employee>();
		try {
			con = MSsqlDAOFactory.getConnection();
			resultEmpSet = getAllEmployee(con);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not find employees # " + this.getClass()
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

	private Collection<Employee> getAllEmployee(Connection con)
			throws SQLException {
		Statement st = null;
		Collection<Employee> resultEmpSet = new ArrayList<Employee>();
		try {
			st = con.createStatement();
			java.sql.ResultSet resSet = st.executeQuery(SQL__FIND_ALL_EMPLOYEE);
			while (resSet.next()) {
				Employee emp = unMapScheduleEmployee(resSet);
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

	@Override
	public Collection<Employee> findEmployees(Collection<Long> ids) {
		Connection con = null;
		Collection<Employee> resultEmpSet = new ArrayList<Employee>();
		try {
			con = MSsqlDAOFactory.getConnection();
			resultEmpSet = findEmployees(ids, con);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not find employees # " + this.getClass()
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

	private Collection<Employee> findEmployees(Collection<Long> ids,
			Connection con) throws SQLException {
		Collection<Employee> resultEmpSet = new ArrayList<Employee>();
		try {
			for (long id : ids) {
				resultEmpSet.add(findEmployee(con, id));
			}
		} catch (SQLException e) {
			throw e;
		}
		return resultEmpSet;
	}

	@Override
	public List<String> getEmailListForSubscribers() {
		Connection con = null;
		ArrayList<String> emailList = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			emailList = getEmailListForSubscribers(con);
		} catch (SQLException e) {
			log.error("Can not get email list.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return emailList;
	}

	private ArrayList<String> getEmailListForSubscribers(Connection con)
			throws SQLException {
		PreparedStatement pstmt = null;
		ArrayList<String> emailList = null;
		try {
			pstmt = con.prepareStatement(SQL__FIND_EMAIL_LIST_FOR_ROLE);
			pstmt.setInt(1, Right.SUBSCRIBER.ordinal());
			ResultSet rs = pstmt.executeQuery();
			if (rs.isBeforeFirst()) {
				emailList = new ArrayList<String>();
			}
			while (rs.next()) {
				emailList.add(rs.getString(MapperParameters.EMPLOYEE__EMAIL));
			}
			return emailList;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					log.error("Can not close statement.", e);
				}
			}
		}
	}
}
