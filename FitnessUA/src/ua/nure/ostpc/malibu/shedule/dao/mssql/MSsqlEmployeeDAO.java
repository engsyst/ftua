package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.DAOException;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.EmployeeSettingsData;
import ua.nure.ostpc.malibu.shedule.entity.ExcelEmployee;
import ua.nure.ostpc.malibu.shedule.entity.Right;
import ua.nure.ostpc.malibu.shedule.entity.Role;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;
import ua.nure.ostpc.malibu.shedule.shared.ExcelEmployeeInsertResult;

public class MSsqlEmployeeDAO implements EmployeeDAO {
	static final Logger log = Logger.getLogger(MSsqlEmployeeDAO.class);

	static final String SQL__FIND_EMPLOYEES_BY_ASSIGNMENT_ID = "SELECT e.EmployeeId, "
			+ "e.Firstname, e.Secondname, e.Lastname, e.Birthday, e.Address, "
			+ "e.Passportint, e.Idint, e.CellPhone, e.WorkPhone, e.HomePhone, e.Email, e.Education, "
			+ "e.Notes, e.PassportIssuedBy, e.IsDeleted, EmpPrefs.MinDays, EmpPrefs.MaxDays "
			+ "FROM Employee e "
			+ "INNER JOIN EmployeeToAssignment ON EmployeeToAssignment.EmployeeId=e.EmployeeId AND EmployeeToAssignment.AssignmentId=? "
			+ "INNER JOIN EmpPrefs ON EmpPrefs.EmployeeId=e.EmployeeId;";
	static final String SQL__FIND_EMPLOYEES_BY_SHIFT_ID = "SELECT e.EmployeeId, "
			+ "e.Firstname, e.Secondname, e.Lastname, e.Birthday, e.Address, "
			+ "e.Passportint, e.Idint, e.CellPhone, e.WorkPhone, e.HomePhone, e.Email, e.Education, "
			+ "e.Notes, e.PassportIssuedBy, e.IsDeleted, EmpPrefs.MinDays, EmpPrefs.MaxDays "
			+ "FROM Employee e "
			+ "INNER JOIN EmpPrefs ON EmpPrefs.EmployeeId=e.EmployeeId "
			+ "INNER JOIN Assignment ON Assignment.EmployeeId=e.EmployeeId AND Assignment.ShiftId=?;";
	static final String SQL__FIND_EMPLOYEES_BY_RIGHT = "select emps.*, ep.MinDays, ep.MaxDays  "
			+ "from Employee emps, EmployeeUserRole eur, Role r, EmpPrefs ep "
			+ "where emps.EmployeeId = eur.EmployeeId and eur.RoleId = r.RoleId and Ep.EmployeeId=emps.EmployeeId and r.Rights = ?";
	static final String SQL__DELETE_EMPLOYEE = "UPDATE Employee SET IsDeleted = 1 WHERE EmployeeId = ?";
	static final String SQL__FIND_OUR_EMPLOYEES = "SELECT Employee.*, "
			+ "COALESCE(EmpPrefs.MinDays, 0) as MinDays, COALESCE(EmpPrefs.MaxDays, 6) as MaxDays "
			+ "FROM Employee, EmpPrefs WHERE Employee.EmployeeId=EmpPrefs.EmployeeId AND Employee.EmployeeId "
			+ "NOT IN (SELECT ComplianceEmployee.OurEmployeeId from ComplianceEmployee)";
	static final String SQL__INSERT_EMPLOYEE = "INSERT INTO Employee ("
			+ "Firstname, Secondname, Lastname, Birthday, Address, "
			+ "Passportint, Idint, CellPhone, WorkPhone, HomePhone, Email, Education, "
			+ "Notes, PassportIssuedBy, IsDeleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	static final String SQL__INSERT_EMPLOYEE_PREFS = "INSERT INTO EmpPrefs (EmployeeId, MinDays, MaxDays) VALUES(?, ?, ?);";
	static final String SQL__INSERT_EMPLOYEE_TO_CONFORMITY = "INSERT INTO ComplianceEmployee "
			+ "(OriginalEmployeeId, OurEmployeeId) VALUES (?, " + "?);";
	static final String SQL__JOIN_CONFORMITY = "SELECT e1.*, e2.OriginalEmployeeId, "
			+ "0 as MinDays, 6 as MaxDays "
			+ "from Employee e1 INNER JOIN ComplianceEmployee e2 "
			+ "ON e1.EmployeeId=e2.OurEmployeeId";
	static final String SQL__ROLE_FOR_EMPLOYEES = "select eur.EmployeeId, r.Rights from EmployeeUserRole eur, Role r where eur.RoleId = r.RoleId";
	static final String SQL__FIND_ALL_EMPLOYEE_ID = "select EmployeeId from Employee";
	static final String SQL__FIND_ALL_EMPLOYEES = "SELECT Employee.*, "
			+ "COALESCE(EmpPrefs.MinDays, 2) as MinDays, COALESCE(EmpPrefs.MaxDays, 6) as MaxDays "
			+ "FROM Employee LEFT JOIN EmpPrefs on Employee.EmployeeId=EmpPrefs.EmployeeId "
			+ "ORDER BY Employee.Lastname";
	static final String SQL__INSERT_ROLE = "insert into EmployeeUserRole (RoleId, EmployeeId) "
			+ "values ((select r.RoleId from Role r where r.Rights=?), ?)";
	static final String SQL__DELETE_ROLE = "delete from EmployeeUserRole "
			+ "where RoleId=(select r.RoleId from Role r where r.Rights=?) and EmployeeId=?";
	static final String SQL__UPDATE_EMPLOYEE = "update Employee set Firstname = ?, "
			+ "Secondname = ?, Lastname = ?, Birthday = ?, Address = ?, "
			+ "Passportint = ?, Idint = ?, CellPhone = ?, WorkPhone = ?, "
			+ "HomePhone = ?, Email = ?, Education = ?, "
			+ "Notes = ?, PassportIssuedBy = ?, IsDeleted = ? where EmployeeId = ?";
	static final String SQL__FIND_EMAIL_LIST_FOR_ROLE = "SELECT Email FROM Employee e "
			+ "JOIN  EmployeeUserRole a ON e.Employeeid=a.Employeeid "
			+ "JOIN Role r on a.Roleid=r.Roleid where r.Rights=?;";
	static final String SQL__GET_OTHER_EMPLOYEE_WITH_EMAIL = "SELECT * FROM Employee WHERE Email=? AND EmployeeId!=?;";
	static final String SQL__GET_OTHER_EMPLOYEE_WITH_CELL_PHONE = "SELECT * FROM Employee WHERE CellPhone=? AND EmployeeId!=?;";
	static final String SQL__GET_OTHER_EMPLOYEE_WITH_PASSPORT_INT = "SELECT * FROM Employee WHERE Passportint=? AND EmployeeId!=?;";
	static final String SQL__GET_OTHER_EMPLOYEE_WITH_ID_INT = "SELECT * FROM Employee WHERE Idint=? AND EmployeeId!=?;";
	static final String SQL__FIND_SCHEDULE_EMPLOYEE_BY_ID = "SELECT e.EmployeeId, "
			+ "e.Firstname, e.Secondname, e.Lastname, e.Birthday, e.Address, "
			+ "e.Passportint, e.Idint, e.CellPhone, e.WorkPhone, e.HomePhone, e.Email, e.Education, "
			+ "e.Notes, e.PassportIssuedBy, e.IsDeleted, EmpPrefs.MinDays, EmpPrefs.MaxDays "
			+ "FROM Employee e "
			+ "INNER JOIN EmpPrefs ON EmpPrefs.EmployeeId=e.EmployeeId WHERE e.EmployeeId=?;";
	static final String SQL_FIND_ASSIGNMENTS_BY_EMPLOYEE_ID = "SELECT Assignment.AssignmentId FROM Assignment "
			+ "INNER JOIN Employee ON Employee.EmployeeId=Assignment.EmployeeId AND Employee.EmployeeId=?;";

	static final String SQL_FIND_IN_OUT_EMPLOYEES_USERS = "SELECT aewu.*, "
			+ "es.EmployeeId AS outEmployeeId, es.Firstname AS outFirstname, es.Secondname AS outSecondname, "
			+ "es.Lastname AS outLastname, es.Birthday AS outBirthday, es.Address AS outAddress, "
			+ "es.PassportNumber AS outPassportint, es.IdNumber AS outIdint, es.CellPhone AS outCellPhone, "
			+ "es.WorkPhone AS outWorkPhone, es.HomePhone AS outHomePhone, es.Email AS outEmail, "
			+ "es.Education AS outEducation, es.Notes AS outNotes, es.PassportIssuedBy AS outPassportIssuedBy "
			+ "FROM ActiveEmpWithUser AS aewu "
			+ "FULL OUTER JOIN ComplianceEmployee ON aewu.EmployeeId = ComplianceEmployee.OurEmployeeId "
			+ "FULL OUTER JOIN Employees as es ON ComplianceEmployee.OriginalEmployeeId = es.EmployeeId "
			+ "ORDER BY ISNULL(aewu.Lastname, 'яяя'), Lastname, EmployeeId";
	static final String SQL_FIND_ALL_IN_OUT_EMPLOYEES_USERS = "SELECT aewu.*, "
			+ "es.EmployeeId AS outEmployeeId, es.Firstname AS outFirstname, es.Secondname AS outSecondname, "
			+ "es.Lastname AS outLastname, es.Birthday AS outBirthday, es.Address AS outAddress, "
			+ "es.PassportNumber AS outPassportint, es.IdNumber AS outIdint, es.CellPhone AS outCellPhone, "
			+ "es.WorkPhone AS outWorkPhone, es.HomePhone AS outHomePhone, es.Email AS outEmail, "
			+ "es.Education AS outEducation, es.Notes AS outNotes, es.PassportIssuedBy AS outPassportIssuedBy "
			+ "FROM ActiveEmpWithUser AS aewu "
			+ "FULL OUTER JOIN ComplianceEmployee ON aewu.EmployeeId = ComplianceEmployee.OurEmployeeId "
			+ "FULL OUTER JOIN Employees as es ON ComplianceEmployee.OriginalEmployeeId = es.EmployeeId "
			+ "ORDER BY ISNULL(aewu.Lastname, 'яяя'), Lastname, EmployeeId";
	static final String SQL_FIND_EMPLOYEES_ROLES = "SELECT DISTINCT Employee.EmployeeId, Role.* FROM Employee "
			+ "INNER JOIN EmployeeUserRole ON Employee.EmployeeId = EmployeeUserRole.EmployeeId "
			+ "INNER JOIN Role ON EmployeeUserRole.RoleId = Role.RoleId "
			+ "WHERE Employee.IsDeleted = 0 " + "ORDER BY Employee.EmployeeId";

	static final String SQL__IMPORT_EMPLOYEE = "INSERT INTO Employee ("
			+ "Firstname, Secondname, Lastname, Birthday, Address, Passportint, Idint, "
			+ "CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy, IsDeleted) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; // 15
																		// fields

	static final String SQL__SET_COMPLIANCE = "INSERT INTO ComplianceEmployee "
			+ "(OriginalEmployeeId, OurEmployeeId) VALUES (?, ?)";

	static final String SQL__GET_ALL_EMPLOYEES_FOR_SCHEDULE = "SELECT DISTINCT emps.*, ep.MinDays, ep.MaxDays "
			+ "FROM Employee emps "
			+ "INNER JOIN EmployeeUserRole eur ON emps.EmployeeId = eur.EmployeeId "
			+ "INNER JOIN EmpPrefs ep ON ep.EmployeeId=emps.EmployeeId "
			+ "INNER JOIN ClubPrefs cp ON cp.EmployeeId=emps.EmployeeId "
			+ "INNER JOIN SchedulePeriod sp ON sp.SchedulePeriodId=? "
			+ "INNER JOIN ScheduleClubDay scd ON scd.SchedulePeriodId=sp.SchedulePeriodId "
			+ "INNER JOIN Shifts s ON s.ScheduleClubDayId=scd.ScheduleClubDayId "
			+ "INNER JOIN Assignment a ON a.ShiftId=s.ShiftId "
			+ "UNION "
			+ "SELECT DISTINCT emps.*, ep.MinDays, ep.MaxDays "
			+ "FROM Employee emps "
			+ "INNER JOIN EmployeeUserRole eur ON emps.EmployeeId = eur.EmployeeId AND  emps.IsDeleted=0 "
			+ "INNER JOIN Role r ON eur.RoleId = r.RoleId AND r.Rights=? "
			+ "INNER JOIN EmpPrefs ep ON ep.EmployeeId=emps.EmployeeId;";
	static final String SQL__GET_REMOVED_SCHEDULE_EMPLOYEES = "SELECT DISTINCT emps.*, ep.MinDays, ep.MaxDays "
			+ "FROM Employee emps "
			+ "INNER JOIN EmployeeUserRole eur ON emps.EmployeeId = eur.EmployeeId AND emps.IsDeleted=1 "
			+ "INNER JOIN Role r ON eur.RoleId = r.RoleId AND r.Rights=? "
			+ "INNER JOIN EmpPrefs ep ON ep.EmployeeId=emps.EmployeeId;";
	static final String SQL__FIND_ALL_NOT_DELETED_SCHEDULE_EMPLOYEES = "SELECT Employee.*, EmpPrefs.MinDays, EmpPrefs.MaxDays FROM Employee "
			+ "INNER JOIN EmpPrefs ON Employee.EmployeeId=EmpPrefs.EmployeeId AND Employee.IsDeleted=0 "
			+ "ORDER BY Employee.Lastname;";

	@Override
	public List<EmployeeSettingsData> getEmployeeSettingsData()
			throws DAOException {
		Connection con = null;
		List<EmployeeSettingsData> result = null;
		try {
			con = MSsqlDAOFactory.getConnection();
		} catch (SQLException e) {
			log.error("Can not select EmployeeSettingsData ", e);
			throw new DAOException("Can not select EmployeeSettingsData", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("Try Can not select EmployeeSettingsData.");
		}
		try {
			result = getEmployeeSettingsData(con);
		} catch (SQLException e) {
			log.error("Can not select EmployeeSettingsData ", e);
			throw new DAOException("Can not select EmployeeSettingsData", e);
		}
		MSsqlDAOFactory.close(con);
		return result;
	}

	public List<EmployeeSettingsData> getEmployeeSettingsData(Connection con)
			throws SQLException {
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(SQL_FIND_ALL_IN_OUT_EMPLOYEES_USERS);
		ArrayList<EmployeeSettingsData> result = new ArrayList<EmployeeSettingsData>();
		while (rs.next()) {
			result.add(unMapEmployeeSettingsData(rs));
		}
		rs.close();
		MSsqlDAOFactory.closeStatement(st);

		st = con.createStatement();
		rs = st.executeQuery(SQL_FIND_EMPLOYEES_ROLES);
		HashMap<Long, List<Role>> roles = new HashMap<Long, List<Role>>();
		while (rs.next()) {
			Long empId = rs.getLong(MapperParameters.EMPLOYEE__ID);
			List<Role> empRoles = roles.get(empId);
			if (empRoles == null) {
				empRoles = new ArrayList<Role>();
			}
			Role r = new Role(rs.getLong(MapperParameters.ROLE__ID),
					Right.values()[rs.getInt(MapperParameters.ROLE__RIGHTS)],
					rs.getString(MapperParameters.ROLE__TITLE));
			empRoles.add(r);
			roles.put(empId, empRoles);
		}
		rs.close();
		MSsqlDAOFactory.closeStatement(st);

		for (EmployeeSettingsData esd : result) {
			Employee e = esd.getInEmployee();
			if (e != null) {
				esd.setRoles(roles.get(esd.getInEmployee().getEmployeeId()));
			}
			User u = esd.getUser();
			if (u != null)
				u.setRoles(esd.getRoles());
		}
		return result;
	}

	EmployeeSettingsData unMapEmployeeSettingsData(ResultSet rs)
			throws SQLException {
		EmployeeSettingsData esd = new EmployeeSettingsData();
		Employee e = null;
		long id = rs.getLong(MapperParameters.EMPLOYEE__ID);
		if (id != 0) {
			e = unMapEmployee(rs, "");
			e.setDeleted(rs.getBoolean(MapperParameters.EMPLOYEE__IS_DELETED));
		}
		esd.setInEmployee(e);
		e = null;
		id = rs.getLong("out" + MapperParameters.EMPLOYEE__ID);
		if (id != 0) {
			e = unMapEmployee(rs, "out");
		}
		esd.setOutEmployee(e);
		User u = null;
		if (rs.getLong(MapperParameters.USER__ID) != 0) {
			u = new User(rs.getLong(MapperParameters.USER__ID),
					rs.getLong(MapperParameters.USER__EMPLOYEE_ID),
					new ArrayList<Role>(),
					rs.getString(MapperParameters.USER__LOGIN),
					rs.getString(MapperParameters.USER__PASSWORD));
			esd.setUser(u);
		}
		return esd;
	}

	@Override
	public boolean insertEmployeePrefs(Employee employee) throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		boolean result = false;
		try {
			if (log.isDebugEnabled()) {
				log.debug("Try insert employee preferences.");
			}
			result = insertEmployeePrefs(con, employee.getEmployeeId(),
					employee.getMinDays(), employee.getMaxDays());
		} catch (SQLException e) {
			log.error("Can not insert employee preferences " + e.getMessage());
		}
		MSsqlDAOFactory.commitAndClose(con);
		return result;
	}

	private boolean insertEmployeePrefs(Connection con, long employeeId,
			int minDayNumber, int maxDayNumber) throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_EMPLOYEE_PREFS);
			pstmt.setLong(1, employeeId);
			pstmt.setInt(2, minDayNumber);
			pstmt.setInt(3, maxDayNumber);
			result = pstmt.executeUpdate() == 1;
		} catch (SQLException e) {
			throw e;
		}
		return result;
	}

	public Employee findEmployee(Connection con, long employeeId)
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
									+ "e.WorkPhone,e.HomePhone,e.Email,e.Education,e.Notes,e.PassportIssuedBy, e.isDeleted, p.MaxDays, p.MinDays"
									+ " from Employee e left join EmpPrefs p "
									+ "on e.EmployeeId=p.EmployeeId where e.EmployeeId=%d",
									employeeId));
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
	public Employee findEmployee(long employeeId) {
		Connection con = null;
		Employee employee = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try findEmployee with id: " + employeeId);
			con = MSsqlDAOFactory.getConnection();
			employee = findEmployee(con, employeeId);
		} catch (SQLException e) {
			log.error("Can not find Employee", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return employee;
	}

	@Override
	public boolean updateEmployeePrefs(Employee employee) {
		Connection con = null;
		boolean updateResult = false;
		try {
			if (log.isDebugEnabled())
				log.debug("Try updateEmployeePrefs with id: "
						+ employee.getEmployeeId());
			con = MSsqlDAOFactory.getConnection();
			updateResult = updateEmployeePrefs(con, employee.getEmployeeId(),
					employee.getMinDays(), employee.getMaxDays());
		} catch (SQLException e) {
			log.error("Can not update Employee # " + this.getClass() + " # "
					+ e.getMessage());
		}
		MSsqlDAOFactory.commitAndClose(con);
		return updateResult;
	}

	boolean updateEmployeePrefs(Connection con, long employeeId,
			int minDayNumber, int maxDayNumber) throws SQLException {
		Statement st = null;
		boolean result = false;
		st = con.createStatement();
		ResultSet rs = st.executeQuery(String.format(
				"select * from EmpPrefs where EmployeeId=%1$d", employeeId));
		if (!rs.next()) {
			return insertEmployeePrefs(con, employeeId, minDayNumber,
					maxDayNumber);
		}
		result = st.executeUpdate(String.format(
				"update EmpPrefs set MinDays = %1$d,"
						+ " MaxDays = %2$d where EmployeeId=%3$d",
				minDayNumber, maxDayNumber, employeeId)) == 1;
		return result;
	}

	@Override
	public Collection<Employee> findEmployeesByAssignmentId(long assignmentId) {
		Connection con = null;
		Collection<Employee> employees = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try findEmployeesByAssignmentId with id: "
						+ assignmentId);
			con = MSsqlDAOFactory.getConnection();
			employees = findEmployeesByAssignmentId(con, assignmentId);
		} catch (SQLException e) {
			log.error("Can not findEmployeesByAssignmentId # ", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return employees;
	}

	Collection<Employee> findEmployeesByAssignmentId(Connection con,
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
	public List<Employee> getAllAdminScheduleEmployees() throws DAOException {
		Connection con = null;
		List<Employee> employees = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try get all schedule employees with admin role.");
			con = MSsqlDAOFactory.getConnection();
			employees = (List<Employee>) findEmployees(Right.ADMIN, con);
		} catch (SQLException e) {
			log.error("Can not get all schedule employees with admin role: ", e);
			throw new DAOException(
					"Can not get all schedule employees with admin role: ", e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
		return employees;
	}

	@Override
	public List<Employee> getEmployeesByShiftId(long shiftId)
			throws DAOException {
		Connection con = null;
		List<Employee> employees = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try getEmployeesByShiftId with id: " + shiftId);
			con = MSsqlDAOFactory.getConnection();
			employees = getEmployeesByShiftId(con, shiftId);
		} catch (SQLException e) {
			log.error("Can not getEmployeesByShiftId # ", e);
			throw new DAOException("Can not getEmployeesByShiftId # ", e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
		return employees;
	}

	protected List<Employee> getEmployeesByShiftId(Connection con, long shiftId)
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

	Employee unMapScheduleEmployee(ResultSet rs) throws SQLException {
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
		employee.setDeleted(rs
				.getBoolean(MapperParameters.EMPLOYEE__IS_DELETED));
		int min = rs.getInt(MapperParameters.EMPLOYEE__MIN_DAYS), max = rs
				.getInt(MapperParameters.EMPLOYEE__MAX_DAYS);
		if (max != 0) {
			employee.setMinAndMaxDays(min, max);
		}
		return employee;
	}

	@Override
	public Collection<Employee> getMalibuEmployees() {
		Connection con = null;
		Collection<Employee> resultEmpSet = new ArrayList<Employee>();
		try {
			if (log.isDebugEnabled())
				log.debug("Try getMalibuEmployees");
			con = MSsqlDAOFactory.getConnection();
			resultEmpSet = getMalibuEmployees(con);
		} catch (SQLException e) {
			log.error("Can not getMalibuEmployees # ", e);
			return null;
		}
		MSsqlDAOFactory.commitAndClose(con);
		return resultEmpSet;
	}

	Collection<Employee> getMalibuEmployees(Connection con) throws SQLException {
		Statement st = null;
		Collection<Employee> resultEmpSet = new ArrayList<Employee>();
		try {
			st = con.createStatement();
			java.sql.ResultSet resSet = st.executeQuery(String
					.format("SELECT * from Employees e"));
			while (resSet.next()) {
				Employee emp = unMapEmployees(resSet);
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
	public boolean containsInSchedules(long employeeId) {
		Connection con = null;
		boolean result = false;
		try {
			if (log.isDebugEnabled())
				log.debug("Try check employee in schedules.");
			con = MSsqlDAOFactory.getConnection();
			result = containsInSchedules(con, employeeId);
		} catch (SQLException e) {
			log.error("Can not check employee in schedules. ", e);
			return false;
		}
		MSsqlDAOFactory.commitAndClose(con);
		return result;
	}

	boolean containsInSchedules(Connection con, long employeeId)
			throws SQLException {
		PreparedStatement pstmt = null;
		boolean result = false;
		try {
			pstmt = con.prepareStatement(SQL_FIND_ASSIGNMENTS_BY_EMPLOYEE_ID);
			pstmt.setLong(1, employeeId);
			ResultSet rs = pstmt.executeQuery();
			result = rs.isBeforeFirst();
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
		return result;
	}

	@Override
	public void deleteEmployee(long id) throws DAOException {
		Connection con = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try deleteEmployee with id: " + id);
			con = MSsqlDAOFactory.getConnection();
			deleteEmployee(id, con);
			con.commit();
		} catch (SQLException e) {
			MSsqlDAOFactory.rollback(con);
			log.error("Can not delete employee.", e);
			throw new DAOException("Невозможно удалить сотрудника", e);
		}
		MSsqlDAOFactory.close(con);
	}

	void deleteEmployee(long id, Connection con) throws SQLException {
		PreparedStatement pstmt = con.prepareStatement(SQL__DELETE_EMPLOYEE);
		pstmt.setLong(1, id);
		pstmt.executeUpdate();
		pstmt.close();
	}

	@Override
	public Collection<Employee> getOnlyOurEmployees() throws DAOException {
		Connection con = null;
		Collection<Employee> ourEmployees = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try getOnlyOurEmployees");
			con = MSsqlDAOFactory.getConnection();
			ourEmployees = getOnlyOurEmployees(con);
		} catch (SQLException e) {
			log.error("Can not getOnlyOurEmployees.", e);
			throw new DAOException("Can not getOnlyOurEmployees.", e);
		}
		MSsqlDAOFactory.close(con);
		return ourEmployees;
	}

	Collection<Employee> getOnlyOurEmployees(Connection con)
			throws SQLException {
		Statement stmt = null;
		Collection<Employee> ourEmployees = new ArrayList<Employee>();
		stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(SQL__FIND_OUR_EMPLOYEES);
		while (rs.next()) {
			Employee emp = unMapScheduleEmployee(rs);
			ourEmployees.add(emp);
		}
		MSsqlDAOFactory.closeStatement(stmt);
		return ourEmployees;
	}

	public boolean insertEmployeesWithConformity(Collection<Employee> employees) {
		boolean result = false;
		Connection con = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try insertEmployeesWithConformity (count : "
						+ employees.size() + ")");
			con = MSsqlDAOFactory.getConnection();
			result = insertEmployeesWithConformity(employees, con);
		} catch (SQLException e) {
			log.error("Can not insert employees.", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return result;
	}

	boolean insertEmployeesWithConformity(Collection<Employee> emps,
			Connection con) throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_EMPLOYEE,
					Statement.RETURN_GENERATED_KEYS);
			pstmt2 = con.prepareStatement(SQL__INSERT_EMPLOYEE_TO_CONFORMITY);
			for (Employee emp : emps) {
				if (emp.getMinDays() == 0 && emp.getMaxDays() == 0) {
					emp.setMinAndMaxDays(AppConstants.EMP_PREF_MIN_DAY_NUMBER,
							AppConstants.EMP_PREF_MAX_DAY_NUMBER);
				}
				mapEmployeeForInsert(emp, pstmt);
				if (pstmt.executeUpdate() != 1) {
					return false;
				}
				ResultSet rs = pstmt.getGeneratedKeys();
				long newEmployeeId = 0;
				if (rs.next()) {
					newEmployeeId = rs.getLong(1);
					insertEmployeePrefs(con, newEmployeeId, emp.getMinDays(),
							emp.getMaxDays());
				}
				pstmt2.setLong(1, emp.getEmployeeId());
				pstmt2.setLong(2, newEmployeeId);
				pstmt2.addBatch();
			}
			result = pstmt2.executeBatch().length == emps.size();
			con.commit();
		} catch (SQLException e) {
			throw e;
		}
		return result;
	}

	Employee unMapEmployees(ResultSet rs) throws SQLException {
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

	Employee unMapEmployee(ResultSet rs, String prefix) throws SQLException {
		prefix = prefix == null ? "" : prefix;
		StringBuffer sb = new StringBuffer(prefix);
		Employee emp = new Employee();
		emp.setFirstName(rs.getString(sb.append(
				MapperParameters.EMPLOYEE__FIRSTNAME).toString()));
		sb.setLength(prefix.length());
		emp.setLastName(rs.getString(sb.append(
				MapperParameters.EMPLOYEE__LASTNAME).toString()));
		sb.setLength(prefix.length());
		emp.setAddress(rs.getString(sb.append(
				MapperParameters.EMPLOYEE__ADDRESS).toString()));
		sb.setLength(prefix.length());
		emp.setBirthday(rs.getDate(sb.append(
				MapperParameters.EMPLOYEE__BIRTHDAY).toString()));
		sb.setLength(prefix.length());
		emp.setCellPhone(rs.getString(sb.append(
				MapperParameters.EMPLOYEE__CELL_PHONE).toString()));
		sb.setLength(prefix.length());
		emp.setEducation(rs.getString(sb.append(
				MapperParameters.EMPLOYEE__EDUCATION).toString()));
		sb.setLength(prefix.length());
		emp.setEmail(rs.getString(sb.append(MapperParameters.EMPLOYEE__EMAIL)
				.toString()));
		sb.setLength(prefix.length());
		emp.setEmployeeId(rs.getLong(sb.append(MapperParameters.EMPLOYEE__ID)
				.toString()));
		sb.setLength(prefix.length());
		emp.setHomePhone(rs.getString(sb.append(
				MapperParameters.EMPLOYEE__HOME_PHONE).toString()));
		sb.setLength(prefix.length());
		emp.setIdNumber(rs.getString(sb.append(
				MapperParameters.EMPLOYEE__ID_NUMBER).toString()));
		sb.setLength(prefix.length());
		emp.setNotes(rs.getString(sb.append(MapperParameters.EMPLOYEE__NOTES)
				.toString()));
		sb.setLength(prefix.length());
		emp.setPassportIssuedBy(rs.getString(sb.append(
				MapperParameters.EMPLOYEE__PASSPORT_ISSUED_BY).toString()));
		sb.setLength(prefix.length());
		emp.setPassportNumber(rs.getString(sb.append(
				MapperParameters.EMPLOYEE__PASSPORT_NUMBER).toString()));
		sb.setLength(prefix.length());
		emp.setSecondName(rs.getString(sb.append(
				MapperParameters.EMPLOYEE__SECONDNAME).toString()));
		sb.setLength(prefix.length());
		emp.setWorkPhone(rs.getString(sb.append(
				MapperParameters.EMPLOYEE__WORK_PHONE).toString()));
		return emp;
	}

	void mapEmployeeForInsert(Employee emp, PreparedStatement pstmt)
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
		pstmt.setBoolean(15, emp.isDeleted());
	}

	@Override
	public Map<Long, Employee> getConformity() {
		Connection con = null;
		Map<Long, Employee> dict = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try getConformity");
			con = MSsqlDAOFactory.getConnection();
			dict = getConformity(con);
		} catch (SQLException e) {
			log.error("Can not get conformity dictionary.", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return dict;
	}

	Map<Long, Employee> getConformity(Connection con) throws SQLException {
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
			if (log.isDebugEnabled())
				log.debug("Try getRolesForEmployee");
			con = MSsqlDAOFactory.getConnection();
			roles = getRolesForEmployee(con);
		} catch (SQLException e) {
			log.error("Can not get conformity dictionary.", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return roles;
	}

	Map<Long, Collection<Boolean>> getRolesForEmployee(Connection con)
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

	boolean workWithRoles(String sql, Map<Integer, Collection<Long>> roles) {
		boolean result = false;
		Connection con = null;
		try {
			if (log.isDebugEnabled())
				log.debug(sql);
			con = MSsqlDAOFactory.getConnection();
			result = workWithRoles(sql, roles, con);
		} catch (SQLException e) {
			log.error("Can not insert roles.", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return result;
	}

	boolean workWithRoles(String sql, Map<Integer, Collection<Long>> roles,
			Connection con) throws SQLException {
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
			if (log.isDebugEnabled())
				log.debug("Try insertEmployeesWithConformityAndRoles");
			con = MSsqlDAOFactory.getConnection();
			result = insertEmployeesWithConformityAndRoles(roleForInsert, con);
		} catch (SQLException e) {
			log.error("Can not insert employees.", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return result;
	}

	boolean insertEmployeesWithConformityAndRoles(
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
			HashMap<Long, Long> insertedEmployee = new HashMap<Long, Long>();
			for (int i = 1; i <= 3; i++) {
				roles.put(i, new ArrayList<Long>());
				for (Employee emp : roleForInsert.get(i)) {
					if (emp.getMinDays() == 0 && emp.getMaxDays() == 0) {
						emp.setMinAndMaxDays(
								AppConstants.EMP_PREF_MIN_DAY_NUMBER,
								AppConstants.EMP_PREF_MAX_DAY_NUMBER);
					}
					long newEmployeeId = 0;
					if (!insertedEmployee.containsKey(emp.getEmployeeId())) {
						mapEmployeeForInsert(emp, pstmt);
						if (pstmt.executeUpdate() != 1) {
							return false;
						}
						ResultSet rs = pstmt.getGeneratedKeys();
						if (rs.next()) {
							newEmployeeId = rs.getLong(1);
							insertedEmployee.put(emp.getEmployeeId(),
									newEmployeeId);
							insertEmployeePrefs(con, newEmployeeId,
									emp.getMinDays(), emp.getMaxDays());
						}
					} else {
						newEmployeeId = insertedEmployee.get(emp
								.getEmployeeId());
					}
					pstmt2.setLong(1, emp.getEmployeeId());
					pstmt2.setLong(2, newEmployeeId);
					roles.get(i).add(newEmployeeId);
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
			if (log.isDebugEnabled())
				log.debug("Try insertEmployeesAndRoles (count: "
						+ roleForInsert.size());
			con = MSsqlDAOFactory.getConnection();
			result = insertEmployeesAndRoles(roleForInsert, con);
		} catch (SQLException e) {
			log.error("Can not insert employees.", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return result;
	}

	boolean insertEmployeesAndRoles(
			Map<Integer, Collection<Employee>> roleForInsert, Connection con)
			throws SQLException {
		boolean result = true;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_EMPLOYEE,
					Statement.RETURN_GENERATED_KEYS);
			Map<Integer, Collection<Long>> roles = new HashMap<Integer, Collection<Long>>();
			HashMap<Long, Long> insertedEmployee = new HashMap<Long, Long>();
			for (int i = 1; i <= 3; i++) {
				roles.put(i, new ArrayList<Long>());
				for (Employee emp : roleForInsert.get(i)) {
					if (emp.getMinDays() == 0 && emp.getMaxDays() == 0) {
						emp.setMinAndMaxDays(
								AppConstants.EMP_PREF_MIN_DAY_NUMBER,
								AppConstants.EMP_PREF_MAX_DAY_NUMBER);
					}
					long newEmployeeId = 0;
					if (!insertedEmployee.containsKey(emp.getEmployeeId())) {
						mapEmployeeForInsert(emp, pstmt);
						if (pstmt.executeUpdate() != 1) {
							return false;
						}
						ResultSet rs = pstmt.getGeneratedKeys();
						if (rs.next()) {
							newEmployeeId = rs.getLong(1);
							insertEmployeePrefs(con, newEmployeeId,
									emp.getMinDays(), emp.getMaxDays());
						}
						insertedEmployee
								.put(emp.getEmployeeId(), newEmployeeId);
					} else
						newEmployeeId = insertedEmployee.get(emp
								.getEmployeeId());
					roles.get(i).add(newEmployeeId);
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
	public long insertEmployee(Employee employee) throws DAOException {
		long employeeId = 0;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			employeeId = insertEmployee(con, employee);
		} catch (SQLException e) {
			log.error("Can not insert employee.", e);
			MSsqlDAOFactory.rollback(con);
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		return employeeId;
	}

	private long insertEmployee(Connection con, Employee employee)
			throws SQLException {
		long employeeId = 0;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_EMPLOYEE,
					Statement.RETURN_GENERATED_KEYS);
			if (employee.getMinDays() == 0 && employee.getMaxDays() == 0) {
				employee.setMinAndMaxDays(AppConstants.EMP_PREF_MIN_DAY_NUMBER,
						AppConstants.EMP_PREF_MAX_DAY_NUMBER);
			}
			mapEmployeeForInsert(employee, pstmt);
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				employeeId = rs.getLong(1);
				insertEmployeePrefs(con, employeeId, employee.getMinDays(),
						employee.getMaxDays());
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
		return employeeId;
	}

	@Override
	public boolean insertEmployees(Collection<Employee> emps) {
		boolean result = false;
		Connection con = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try insertEmployees (count: " + emps.size() + ")");
			con = MSsqlDAOFactory.getConnection();
			result = insertEmployees(emps, con);
		} catch (SQLException e) {
			log.error("Can not insertEmployees", e);
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		return result;
	}

	private boolean insertEmployees(Collection<Employee> emps, Connection con)
			throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_EMPLOYEE,
					Statement.RETURN_GENERATED_KEYS);
			for (Employee emp : emps) {
				if (emp.getMinDays() == 0 && emp.getMaxDays() == 0) {
					emp.setMinAndMaxDays(AppConstants.EMP_PREF_MIN_DAY_NUMBER,
							AppConstants.EMP_PREF_MAX_DAY_NUMBER);
				}
				mapEmployeeForInsert(emp, pstmt);
				if (pstmt.executeUpdate() != 1) {
					return false;
				}
				ResultSet rs = pstmt.getGeneratedKeys();
				if (rs.next()) {
					long newEmployeeId = rs.getLong(1);
					insertEmployeePrefs(con, newEmployeeId, emp.getMinDays(),
							emp.getMaxDays());
				}
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
		return true;
	}

	@Override
	public ExcelEmployeeInsertResult insertExcelEmployees(
			List<ExcelEmployee> excelEmployeeList) throws DAOException {
		ExcelEmployeeInsertResult insertResult = null;
		Connection con = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try insert employees from Excel document:  (count: "
						+ excelEmployeeList.size() + ").");
			con = MSsqlDAOFactory.getConnection();
			insertResult = insertExcelEmployees(con, excelEmployeeList);
		} catch (SQLException e) {
			insertResult = new ExcelEmployeeInsertResult(false);
			MSsqlDAOFactory.rollback(con);
			log.error("Can not insert employees from Excel document: ", e);
		} finally {
			if (insertResult.isInsertResult()) {
				MSsqlDAOFactory.commitAndClose(con);
			} else {
				MSsqlDAOFactory.rollback(con);
				log.error("Can not insert employees from Excel document. Input data are not valid!");
			}
		}
		return insertResult;
	}

	private ExcelEmployeeInsertResult insertExcelEmployees(Connection con,
			List<ExcelEmployee> excelEmployeeList) throws SQLException {
		boolean res = true;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_EMPLOYEE,
					Statement.RETURN_GENERATED_KEYS);
			for (int i = 0; i < excelEmployeeList.size(); i++) {
				Employee employee = excelEmployeeList.get(i).getEmployee();
				List<Right> rightList = excelEmployeeList.get(i).getRights();
				Map<String, String> paramMap = new LinkedHashMap<String, String>();
				paramMap.put(AppConstants.EMAIL, employee.getEmail());
				paramMap.put(AppConstants.CELL_PHONE, employee.getCellPhone());
				paramMap.put(AppConstants.PASSPORT_NUMBER,
						employee.getPassportNumber());
				paramMap.put(AppConstants.ID_NUMBER, employee.getIdNumber());
				Map<String, String> errorMap = checkEmployeeDataBeforeUpdate(
						paramMap, 0, con);
				if (errorMap.size() == 0) {
					if (employee.getMinDays() == 0
							&& employee.getMaxDays() == 0) {
						employee.setMinAndMaxDays(
								AppConstants.EMP_PREF_MIN_DAY_NUMBER,
								AppConstants.EMP_PREF_MAX_DAY_NUMBER);
					}
					mapEmployeeForInsert(employee, pstmt);
					res = res && pstmt.executeUpdate() == 1;
					ResultSet rs = pstmt.getGeneratedKeys();
					if (rs.next()) {
						long newEmployeeId = rs.getLong(1);
						res = res
								&& insertEmployeePrefs(con, newEmployeeId,
										employee.getMinDays(),
										employee.getMaxDays());
						for (Right right : rightList) {
							res = res && insertRight(con, newEmployeeId, right);
						}
						if (!rightList.isEmpty()) {
							deleteRight(con, newEmployeeId, Right.VISITOR);
						}
					}
				} else {
					return new ExcelEmployeeInsertResult(false, i + 1, errorMap);
				}
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
		return new ExcelEmployeeInsertResult(res);
	}

	private boolean insertRight(Connection con, long employeeId, Right right)
			throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_ROLE);
			pstmt.setInt(1, right.ordinal());
			pstmt.setLong(2, employeeId);
			return pstmt.executeUpdate() == 1;
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
	}

	private boolean deleteRight(Connection con, long employeeId, Right right)
			throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__DELETE_ROLE);
			pstmt.setInt(1, right.ordinal());
			pstmt.setLong(2, employeeId);
			return pstmt.executeUpdate() == 1;
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
	}

	@Override
	public boolean updateEmployee(Employee employee) throws DAOException {
		boolean result = false;
		Connection con = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try update employee!");
			con = MSsqlDAOFactory.getConnection();
			result = updateEmployee(employee, con);
			con.commit();
		} catch (SQLException e) {
			MSsqlDAOFactory.rollback(con);
			log.error("Can not update employee!", e);
		}
		MSsqlDAOFactory.close(con);
		return result;
	}

	boolean updateEmployee(Employee employee, Connection con)
			throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		pstmt = con.prepareStatement(SQL__UPDATE_EMPLOYEE);
		mapEmployeeForInsert(employee, pstmt);
		pstmt.setLong(16, employee.getEmployeeId());
		result = pstmt.executeUpdate() == 1;
		pstmt.close();
		return result;
	}

	@Override
	public boolean updateEmployees(Collection<Employee> emps) {
		boolean result = false;
		Connection con = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try update employees!");
			con = MSsqlDAOFactory.getConnection();
			result = updateEmployees(emps, con);
		} catch (SQLException e) {
			log.error("Can not update employees!", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return result;
	}

	boolean updateEmployees(Collection<Employee> emps, Connection con)
			throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		pstmt = con.prepareStatement(SQL__UPDATE_EMPLOYEE);
		for (Employee emp : emps) {
			mapEmployeeForInsert(emp, pstmt);
			pstmt.setLong(16, emp.getEmployeeId());
			pstmt.addBatch();
		}
		result = pstmt.executeBatch().length == emps.size();
		con.commit();
		return result;
	}

	@Override
	public Collection<Employee> getAllEmployees() {
		Connection con = null;
		Collection<Employee> employeeList = new ArrayList<Employee>();
		try {
			if (log.isDebugEnabled())
				log.debug("Try get all employees");
			con = MSsqlDAOFactory.getConnection();
			employeeList = getAllEmployees(con);
		} catch (SQLException e) {
			log.error("Can not get all employees # " + e.getMessage());
		} finally {
			MSsqlDAOFactory.close(con);
		}
		return employeeList;
	}

	Collection<Employee> getAllEmployees(Connection con) throws SQLException {
		Statement stmt = null;
		Collection<Employee> employeeList = new ArrayList<Employee>();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__FIND_ALL_EMPLOYEES);
			while (rs.next()) {
				Employee employee = unMapScheduleEmployee(rs);
				employeeList.add(employee);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(stmt);
		}
		return employeeList;
	}

	@Override
	public Collection<Employee> findEmployees(Collection<Long> ids) {
		Connection con = null;
		Collection<Employee> resultEmpSet = new ArrayList<Employee>();
		try {
			if (log.isDebugEnabled())
				log.debug("Try findEmployees (count: " + ids.size() + ")");
			con = MSsqlDAOFactory.getConnection();
			resultEmpSet = findEmployees(ids, con);
		} catch (SQLException e) {
			log.error("Can not find employees ", e);
			return null;
		}
		MSsqlDAOFactory.commitAndClose(con);
		return resultEmpSet;
	}

	Collection<Employee> findEmployees(Collection<Long> ids, Connection con)
			throws SQLException {
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
	public Collection<Employee> findEmployees(Right right) {
		Connection con = null;
		Collection<Employee> resultEmpSet = new ArrayList<Employee>();
		try {
			if (log.isDebugEnabled())
				log.debug("Try findEmployees with rights: " + right);
			con = MSsqlDAOFactory.getConnection();
			resultEmpSet = findEmployees(right, con);
		} catch (SQLException e) {
			log.error("Can not find employees ", e);
			return null;
		}
		MSsqlDAOFactory.commitAndClose(con);
		return resultEmpSet;
	}

	Collection<Employee> findEmployees(Right right, Connection con)
			throws SQLException {
		PreparedStatement pstmt = null;
		ArrayList<Employee> emps = null;
		try {
			pstmt = con.prepareStatement(SQL__FIND_EMPLOYEES_BY_RIGHT);
			pstmt.setInt(1, right.ordinal());
			ResultSet rs = pstmt.executeQuery();
			if (rs.isBeforeFirst()) {
				emps = new ArrayList<Employee>();
			}
			while (rs.next()) {
				Employee emp = unMapScheduleEmployee(rs);
				emps.add(emp);
			}
			return emps;
		} catch (SQLException e) {
			log.error("Statement.", e);
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

	@Override
	public List<Employee> getAllEmployeesForSchedule(long scheduleId) {
		Connection con = null;
		List<Employee> employeeList = new ArrayList<Employee>();
		try {
			if (log.isDebugEnabled())
				log.debug("Try get schedule employees for schedule id="
						+ scheduleId);
			con = MSsqlDAOFactory.getConnection();
			employeeList = getAllEmployeesForSchedule(con, scheduleId);
		} catch (SQLException e) {
			log.error("Can not get schedule employees for schedule id="
					+ scheduleId, e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
		return employeeList;
	}

	List<Employee> getAllEmployeesForSchedule(Connection con,
			long scheduleId) throws SQLException {
		PreparedStatement pstmt = null;
		List<Employee> employeeList = new ArrayList<Employee>();
		try {
			pstmt = con
					.prepareStatement(SQL__GET_ALL_EMPLOYEES_FOR_SCHEDULE);
			pstmt.setLong(1, scheduleId);
			pstmt.setInt(2, Right.ADMIN.ordinal());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Employee employee = unMapScheduleEmployee(rs);
				employeeList.add(employee);
			}
			return employeeList;
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
	}

	@Override
	public List<Employee> getRemovedScheduleEmployees() {
		Connection con = null;
		List<Employee> employeeList = new ArrayList<Employee>();
		try {
			if (log.isDebugEnabled())
				log.debug("Try get removed schedule employees.");
			con = MSsqlDAOFactory.getConnection();
			employeeList = getRemovedScheduleEmployees(con);
		} catch (SQLException e) {
			log.error("Can not get removed schedule employees", e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
		return employeeList;
	}

	List<Employee> getRemovedScheduleEmployees(Connection con)
			throws SQLException {
		PreparedStatement pstmt = null;
		List<Employee> employeeList = new ArrayList<Employee>();
		try {
			pstmt = con.prepareStatement(SQL__GET_REMOVED_SCHEDULE_EMPLOYEES);
			pstmt.setInt(1, Right.ADMIN.ordinal());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Employee employee = unMapScheduleEmployee(rs);
				employeeList.add(employee);
			}
			return employeeList;
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
	}

	@Override
	public List<String> getEmailListForSubscribers() {
		Connection con = null;
		ArrayList<String> emailList = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try getEmailListForSubscribers");
			con = MSsqlDAOFactory.getConnection();
			emailList = getEmailListForSubscribers(con);
		} catch (SQLException e) {
			log.error("Can not get email list.", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return emailList;
	}

	ArrayList<String> getEmailListForSubscribers(Connection con)
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

	@Override
	public Map<String, String> checkEmployeeDataBeforeUpdate(
			Map<String, String> paramMap, long employeeId) {
		Connection con = null;
		Map<String, String> paramErrors = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try check employee data before update.");
			con = MSsqlDAOFactory.getConnection();
			paramErrors = checkEmployeeDataBeforeUpdate(paramMap, employeeId,
					con);
		} catch (SQLException e) {
			log.error("Can not check employee data before update.", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return paramErrors;
	}

	private Map<String, String> checkEmployeeDataBeforeUpdate(
			Map<String, String> paramMap, long employeeId, Connection con)
			throws SQLException {
		String email = paramMap.get(AppConstants.EMAIL);
		String cellPhone = paramMap.get(AppConstants.CELL_PHONE);
		Map<String, String> paramErrors = checkEmployeeDataBeforeUpdate(email,
				cellPhone, employeeId, con);
		try {
			if (containsOtherEmployeeWithPassportNumber(
					paramMap.get(AppConstants.PASSPORT_NUMBER), employeeId, con)) {
				paramErrors.put(AppConstants.PASSPORT_NUMBER,
						AppConstants.PASSPORT_NUMBER_SERVER_ERROR);
			}
			if (containsOtherEmployeeWithIdNumber(
					paramMap.get(AppConstants.ID_NUMBER), employeeId, con)) {
				paramErrors.put(AppConstants.ID_NUMBER,
						AppConstants.ID_NUMBER_SERVER_ERROR);
			}
			return paramErrors;
		} catch (SQLException e) {
			throw e;
		}
	}

	@Override
	public Map<String, String> checkEmployeeDataBeforeUpdate(String email,
			String cellPhone, long employeeId) {
		Connection con = null;
		Map<String, String> paramErrors = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try check employee data before update.");
			con = MSsqlDAOFactory.getConnection();
			paramErrors = checkEmployeeDataBeforeUpdate(email, cellPhone,
					employeeId, con);
		} catch (SQLException e) {
			log.error("Can not check employee data before update.", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return paramErrors;
	}

	Map<String, String> checkEmployeeDataBeforeUpdate(String email,
			String cellPhone, long employeeId, Connection con)
			throws SQLException {
		Map<String, String> paramErrors = new LinkedHashMap<String, String>();
		try {
			if (containsOtherEmployeeWithEmail(email, employeeId, con)) {
				paramErrors.put(AppConstants.EMAIL,
						AppConstants.EMAIL_SERVER_ERROR);
			}
			if (containsOtherEmployeeWithCellPhone(cellPhone, employeeId, con)) {
				paramErrors.put(AppConstants.CELL_PHONE,
						AppConstants.CELL_PHONE_SERVER_ERROR);
			}
			return paramErrors;
		} catch (SQLException e) {
			throw e;
		}
	}

	boolean containsOtherEmployeeWithEmail(String email, long employeeId,
			Connection con) throws SQLException {
		PreparedStatement pstmt = null;
		boolean result = false;
		try {
			pstmt = con.prepareStatement(SQL__GET_OTHER_EMPLOYEE_WITH_EMAIL);
			pstmt.setString(1, email);
			pstmt.setLong(2, employeeId);
			ResultSet rs = pstmt.executeQuery();
			result = rs.isBeforeFirst();
			return result;
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
	}

	boolean containsOtherEmployeeWithCellPhone(String cellPhone,
			long employeeId, Connection con) throws SQLException {
		PreparedStatement pstmt = null;
		boolean result = false;
		try {
			pstmt = con
					.prepareStatement(SQL__GET_OTHER_EMPLOYEE_WITH_CELL_PHONE);
			pstmt.setString(1, cellPhone);
			pstmt.setLong(2, employeeId);
			ResultSet rs = pstmt.executeQuery();
			result = rs.isBeforeFirst();
			return result;
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
	}

	boolean containsOtherEmployeeWithPassportNumber(String passportNumber,
			long employeeId, Connection con) throws SQLException {
		PreparedStatement pstmt = null;
		boolean result = false;
		try {
			pstmt = con
					.prepareStatement(SQL__GET_OTHER_EMPLOYEE_WITH_PASSPORT_INT);
			pstmt.setString(1, passportNumber);
			pstmt.setLong(2, employeeId);
			ResultSet rs = pstmt.executeQuery();
			result = rs.isBeforeFirst();
			return result;
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
	}

	boolean containsOtherEmployeeWithIdNumber(String idNumber, long employeeId,
			Connection con) throws SQLException {
		PreparedStatement pstmt = null;
		boolean result = false;
		try {
			pstmt = con.prepareStatement(SQL__GET_OTHER_EMPLOYEE_WITH_ID_INT);
			pstmt.setString(1, idNumber);
			pstmt.setLong(2, employeeId);
			ResultSet rs = pstmt.executeQuery();
			result = rs.isBeforeFirst();
			return result;
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
	}

	@Override
	public Employee getScheduleEmployeeById(long employeeId) {
		Connection con = null;
		Employee employee = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try get schedule employee by id: " + employeeId);
			con = MSsqlDAOFactory.getConnection();
			employee = getScheduleEmployeeById(con, employeeId);
		} catch (SQLException e) {
			log.error("Can not get schedule employee by id: " + employeeId
					+ "!", e);
		}
		MSsqlDAOFactory.close(con);
		return employee;
	}

	Employee getScheduleEmployeeById(Connection con, long employeeId)
			throws SQLException {
		Employee employee = null;
		PreparedStatement pstmt = con
				.prepareStatement(SQL__FIND_SCHEDULE_EMPLOYEE_BY_ID);
		pstmt.setLong(1, employeeId);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			employee = unMapScheduleEmployee(rs);
		}
		MSsqlDAOFactory.closeStatement(pstmt);
		return employee;
	}

	@Override
	public Employee importEmployee(Employee employee, List<Role> roles)
			throws DAOException {
		Connection con = null;
		Employee result = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try importEemployee with id: "
						+ employee.getEmployeeId());
			con = MSsqlDAOFactory.getConnection();
			long id = importEmployee(employee, roles, con);
			result = findEmployee(con, id);
			con.commit();
		} catch (SQLException e) {
			MSsqlDAOFactory.rollback(con);
			log.error("Can not import club.", e);
			throw new DAOException("Ошибка при импорте клуба", e);
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		return result;
	}

	long importEmployee(Employee employee, List<Role> roles, Connection con)
			throws SQLException {
		PreparedStatement pstmt = con.prepareStatement(SQL__IMPORT_EMPLOYEE,
				PreparedStatement.RETURN_GENERATED_KEYS);
		mapEmployeeForInsert(employee, pstmt);

		pstmt.executeUpdate();
		ResultSet rs = pstmt.getGeneratedKeys();
		rs.next();
		long id = rs.getLong(1);
		rs.close();
		pstmt = con.prepareStatement(SQL__SET_COMPLIANCE);
		pstmt.setLong(1, employee.getEmployeeId()); // original
		pstmt.setLong(2, id); // our
		pstmt.executeUpdate();
		pstmt.close();
		insertEmployeeRoles(id, roles, con);
		int minDayNumber = employee.getMinDays();
		int maxDayNumber = employee.getMaxDays();
		if (minDayNumber == 0 && maxDayNumber == 0) {
			minDayNumber = AppConstants.EMP_PREF_MIN_DAY_NUMBER;
			maxDayNumber = AppConstants.EMP_PREF_MAX_DAY_NUMBER;
		}
		insertEmployeePrefs(con, id, minDayNumber, maxDayNumber);
		return id;
	}

	void insertEmployeeRoles(long empId, List<Role> roles, Connection con)
			throws SQLException {
		assert roles == null : "Roles can not be a null";
		long userId = getUserIdByEmployeeId(empId, con);
		for (Role role : roles) {
			insertEmployeeUserRole(empId, userId, role.getRoleId(), con);
		}
	}

	public static final String SQL__GET_USERID_BY_EMPID = "SELECT DISTINCT "
			+ "UserId FROM EmployeeUserRole WHERE EmployeeId = %1$d";

	long getUserIdByEmployeeId(long id, Connection con) throws SQLException {
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(String.format(
				SQL__GET_USERID_BY_EMPID, id));
		long userId = rs.next() ? rs.getLong("UserId") : 0;
		rs.close();
		stmt.close();
		return userId;
	}

	@Override
	public List<Role> getRoles() throws DAOException {
		Connection con = null;
		List<Role> result = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try getRoles ");
			con = MSsqlDAOFactory.getConnection();
			result = getRoles(con);
			con.commit();
		} catch (SQLException e) {
			MSsqlDAOFactory.rollback(con);
			log.error("Can not getRoles.", e);
			throw new DAOException("Ошибка при получении ролей", e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
		return result;
	}

	List<Role> getRoles(Connection con) throws SQLException {
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM ROLE");
		List<Role> roles = new ArrayList<Role>();
		while (rs.next()) {
			roles.add(new Role(rs.getLong(MapperParameters.ROLE__ID), Right
					.values()[rs.getInt(MapperParameters.ROLE__RIGHTS)], rs
					.getString(MapperParameters.ROLE__TITLE)));
		}
		MSsqlDAOFactory.closeStatement(stmt);
		return roles;
	}

	@Override
	public void insertEmployeeUserRole(long empId, long roleId)
			throws DAOException {
		Connection con = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try deleteEmployeeUserRole of employee with id: "
						+ empId);
			con = MSsqlDAOFactory.getConnection();
			Long uId = getUserIdByEmployeeId(empId, con);
			insertEmployeeUserRole(empId, uId, roleId, con);
			if (existEmployeeUserRole(empId, Right.VISITOR, con))
				deleteEmployeeUserRole(empId, getVisitorRoleId(con), con);
			con.commit();
		} catch (SQLException e) {
			MSsqlDAOFactory.rollback(con);
			log.error("Can not updateRole.", e);
			throw new DAOException(
					"Ошибка при обновлении роли сотрудника с id " + empId, e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
	}

	public static final String SQL__GET_VISITOR_ROLE_ID = "SELECT RoleID FROM Role WHERE Rights = %1$d";

	long getVisitorRoleId(Connection con) throws SQLException {
		Statement st = con.createStatement();
		String query = String.format(SQL__GET_VISITOR_ROLE_ID,
				Right.VISITOR.ordinal());
		ResultSet rs = st.executeQuery(query);
		rs.next();
		int id = rs.getInt(1);
		rs.close();
		st.close();
		return id;
	}

	@Override
	public void deleteEmployeeUserRole(long empId, long roleId)
			throws DAOException {
		Connection con = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try deleteEmployeeUserRole of employee with id: "
						+ empId);
			con = MSsqlDAOFactory.getConnection();
			Long uId = getUserIdByEmployeeId(empId, con);
			deleteEmployeeUserRole(empId, roleId, con);
			if (!existEmployeeUserRole(empId, null, con)) {
				insertEmployeeUserRole(empId, uId, getVisitorRoleId(con), con);
			}
			con.commit();
		} catch (SQLException e) {
			MSsqlDAOFactory.rollback(con);
			log.error("Can not updateRole.", e);
			throw new DAOException(
					"Ошибка при обновлении роли сотрудника с id " + empId, e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
	}

	public static final String SQL__INSERT_EUR = "INSERT EmployeeUserRole "
			+ "(EmployeeId, UserId, RoleId) VALUES(?,?,?)";

	/**
	 * 
	 * @param empId
	 * @param uId
	 *            can be a null or zero
	 * @param roleId
	 * @param con
	 * @throws SQLException
	 */
	private void insertEmployeeUserRole(long empId, Long uId, long roleId,
			Connection con) throws SQLException {
		PreparedStatement pstmt = null;
		pstmt = con.prepareStatement(SQL__INSERT_EUR);
		pstmt.setLong(1, empId);
		if (uId == null || uId == 0)
			pstmt.setNull(2, Types.NULL);
		else
			pstmt.setLong(2, uId);
		pstmt.setLong(3, roleId);
		pstmt.executeUpdate();
		pstmt.close();
	}

	public static final String SQL__DELETE_EUR = "DELETE FROM EmployeeUserRole "
			+ "WHERE EmployeeUserRoleId = ?";

	void deleteEmployeeUserRole(long id, Connection con) throws SQLException,
			DAOException {
		PreparedStatement pstmt = null;
		pstmt = con.prepareStatement(SQL__DELETE_EUR);
		pstmt.setLong(1, id);
		pstmt.executeUpdate();
		pstmt.close();
	}

	public static final String SQL__GET_EUR_BY_EMPID = "SELECT count(*) "
			+ "FROM EmployeeUserRole WHERE EmployeeId = %1$d";
	public static final String SQL__GET_EUR_BY_EMPID_AND_RIGHT = "SELECT count(*) "
			+ "FROM EmployeeUserRole, dbo.Role "
			+ "WHERE EmployeeId = %1$d AND Role.RoleId IN (SELECT RoleId FROM Role WHERE Rights = %2$d)";

	boolean existEmployeeUserRole(long empId, Right right, Connection con)
			throws SQLException {
		Statement st = con.createStatement();
		ResultSet rs = right == null ? st.executeQuery(String.format(
				SQL__GET_EUR_BY_EMPID, empId)) : st.executeQuery(String.format(
				SQL__GET_EUR_BY_EMPID_AND_RIGHT, empId, right.ordinal()));
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		st.close();
		return count > 0;
	}

	public static final String SQL__DELETE_EUR_BY_EID_RID = "DELETE FROM EmployeeUserRole "
			+ "WHERE EmployeeId = ? AND RoleId = ?";

	void deleteEmployeeUserRole(long empId, long roleId, Connection con)
			throws SQLException, DAOException {
		PreparedStatement pstmt = null;
		pstmt = con.prepareStatement(SQL__DELETE_EUR_BY_EID_RID);
		pstmt.setLong(1, empId);
		pstmt.setLong(2, roleId);
		pstmt.executeUpdate();
		pstmt.close();
	}

	@Override
	public List<Employee> getAllNotDeletedScheduleEmployees() {
		Connection con = null;
		List<Employee> employeeList = new ArrayList<Employee>();
		try {
			if (log.isDebugEnabled()) {
				log.debug("Try get all not deleted schedule employees.");
			}
			con = MSsqlDAOFactory.getConnection();
			employeeList = getAllNotDeletedScheduleEmployees(con);
		} catch (SQLException e) {
			log.error("Can not get all not deleted schedule employees: "
					+ e.getMessage());
		} finally {
			MSsqlDAOFactory.close(con);
		}
		return employeeList;
	}

	private List<Employee> getAllNotDeletedScheduleEmployees(Connection con)
			throws SQLException {
		Statement stmt = null;
		List<Employee> employeeList = new ArrayList<Employee>();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt
					.executeQuery(SQL__FIND_ALL_NOT_DELETED_SCHEDULE_EMPLOYEES);
			while (rs.next()) {
				Employee employee = unMapScheduleEmployee(rs);
				employeeList.add(employee);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(stmt);
		}
		return employeeList;
	}

}
