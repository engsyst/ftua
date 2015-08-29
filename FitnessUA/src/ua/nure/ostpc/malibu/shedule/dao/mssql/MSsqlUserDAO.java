package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.DAOException;
import ua.nure.ostpc.malibu.shedule.dao.UserDAO;
import ua.nure.ostpc.malibu.shedule.entity.Right;
import ua.nure.ostpc.malibu.shedule.entity.Role;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;
import ua.nure.ostpc.malibu.shedule.security.Hashing;

public class MSsqlUserDAO implements UserDAO {
	private static final Logger log = Logger.getLogger(MSsqlUserDAO.class);

	private static final String SQL__CONTAINS_USER_WITH_LOGIN = "SELECT * FROM Client WHERE Login=?;";
	private static final String SQL__READ_USER_BY_LOGIN = "SELECT DISTINCT u.UserId, eur.EmployeeId, u.PwdHache, u.Login "
			+ "FROM Client u INNER JOIN EmployeeUserRole eur ON u.UserId=eur.UserId AND LOWER(u.Login)=LOWER(?);";
	private static final String SQL__READ_USER_BY_ID = "SELECT DISTINCT u.UserId, eur.EmployeeId, u.PwdHache, u.Login "
			+ "FROM Client u INNER JOIN EmployeeUserRole eur ON u.UserId=eur.UserId AND u.UserId=?;";
	private static final String SQL__READ_ROLES_BY_USER_ID = "SELECT r.RoleId, r.Rights, r.Title "
			+ "FROM Role r INNER JOIN EmployeeUserRole eur ON r.RoleId=eur.RoleId AND eur.UserId=?;";
	private static final String SQL__READ_ROLES_BY_EMPLOYEE_ID = "SELECT Role.RoleId, Role.Rights, Role.Title FROM Role "
			+ "INNER JOIN EmployeeUserRole ON Role.RoleId=EmployeeUserRole.RoleId AND EmployeeUserRole.EmployeeId=?;";
	private static final String SQL__GET_ALL_USERS = "SELECT DISTINCT u.*, eur.EmployeeId FROM Client u INNER JOIN EmployeeUserRole eur ON eur.UserId = u.UserId";
	private static final String SQL__INSERT_USER = "INSERT INTO Client (PwdHache, Login) VALUES (?, ?)";
	private static final String SQL__UPDATE_EMPLOYEE_USER_ROLE = "UPDATE EmployeeUserRole SET UserId = ? WHERE EmployeeId = ?";
	private static final String SQL__GET_EMPLOYEE_WITHOUT_USER = "select distinct t.EmployeeId from"
			+ " (SELECT e.EmployeeId FROM Employee e where e.EmployeeId not in"
			+ " (SELECT DISTINCT eur.EmployeeId FROM Client u INNER JOIN EmployeeUserRole eur ON eur.UserId = u.UserId)) t, EmployeeUserRole eur"
			+ " where eur.EmployeeId=t.EmployeeId and"
			+ " eur.RoleId not in (SELECT RoleId FROM Role where Rights = 2)";
	private static final String SQL__UPDATE_USER = "UPDATE Client SET Login=?, PwdHache=? WHERE UserId=?";
	private static final String SQL__GET_USER_BY_EMPLOYEE_ID = "SELECT DISTINCT Client.UserId, Employee.EmployeeId, Client.Login, Client.PwdHache FROM Employee "
			+ "INNER JOIN EmployeeUserRole ON Employee.EmployeeId=? AND EmployeeUserRole.EmployeeId=Employee.EmployeeId "
			+ "INNER JOIN Client ON EmployeeUserRole.UserId=Client.UserId;";
	private static final String SQL__GET_OTHER_USER_WITH_LOGIN = "SELECT * FROM Client WHERE Login=? AND UserId!=?;";

	private static final String SQL__GET_ROLE_BY_RIGHT = "SELECT * FROM Role WHERE RoleId = ?";

	@Override
	public boolean containsUser(String login) {
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			return containsUser(con, login);
		} catch (SQLException e) {
			log.error("Can not check user containing.", e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
		return false;
	}

	private boolean containsUser(Connection con, String login)
			throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__CONTAINS_USER_WITH_LOGIN);
			pstmt.setString(1, login);
			ResultSet rs = pstmt.executeQuery();
			return rs.isBeforeFirst();
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
	}

	@Override
	public User getUser(String login) {
		long t1 = System.currentTimeMillis();
		Connection con = null;
		User user = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			user = getUser(con, login);
		} catch (SQLException e) {
			log.error("Can not get user.", e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
		System.err.println("ScheduleManagerServiceImpl.userRoles "
				+ (System.currentTimeMillis() - t1) + "ms");
		return user;
	}

	private User getUser(Connection con, String login) throws SQLException {
		PreparedStatement pstmt = null;
		User user = null;
		try {
			pstmt = con.prepareStatement(SQL__READ_USER_BY_LOGIN);
			pstmt.setString(1, login);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				user = unMapUser(rs);
				List<Role> roles = getUserRoles(con, user.getUserId());
				user.setRoles(roles);
			}
			return user;
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
	public User getUser(long userId) {
		long t1 = System.currentTimeMillis();
		Connection con = null;
		User user = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			user = getUser(con, userId);
		} catch (SQLException e) {
			log.error("Can not get user.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		System.err.println("ScheduleManagerServiceImpl.userRoles "
				+ (System.currentTimeMillis() - t1) + "ms");
		return user;
	}

	private User getUser(Connection con, long userId) throws SQLException {
		PreparedStatement pstmt = null;
		User user = null;
		try {
			pstmt = con.prepareStatement(SQL__READ_USER_BY_ID);
			pstmt.setLong(1, userId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				user = unMapUser(rs);
				List<Role> roles = getUserRoles(con, user.getUserId());
				user.setRoles(roles);
			}
			return user;
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
	public List<Role> getUserRoles(long userId) {
		long t1 = System.currentTimeMillis();
		Connection con = null;
		List<Role> roles = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			roles = getUserRoles(con, userId);
		} catch (SQLException e) {
			log.error("Can not get user roles.", e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
		System.err.println("MSsqlUserDAO.getUserRoles "
				+ (System.currentTimeMillis() - t1) + "ms");
		return roles;
	}

	private List<Role> getUserRoles(Connection con, long userId)
			throws SQLException {
		PreparedStatement pstmt = null;
		List<Role> roles = null;
		pstmt = con.prepareStatement(SQL__READ_ROLES_BY_USER_ID);
		pstmt.setLong(1, userId);
		ResultSet rs = pstmt.executeQuery();
		if (rs.isBeforeFirst()) {
			roles = new ArrayList<Role>();
		}
		while (rs.next()) {
			Role role = unMapRole(rs);
			roles.add(role);
		}
		MSsqlDAOFactory.closeStatement(pstmt);
		return roles;
	}

	@Override
	public List<Role> getUserRolesByEmployeeId(long employeeId) {
		long t1 = System.currentTimeMillis();
		Connection con = null;
		List<Role> roles = new ArrayList<Role>();
		try {
			con = MSsqlDAOFactory.getConnection();
			roles = getUserRolesByEmployeeId(con, employeeId);
		} catch (SQLException e) {
			log.error("Can not get user roles by employee id.", e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
		System.err.println("MSsqlUserDAO.getUserRolesByEmployeeId "
				+ (System.currentTimeMillis() - t1) + "ms");
		return roles;
	}

	private List<Role> getUserRolesByEmployeeId(Connection con, long employeeId)
			throws SQLException {
		PreparedStatement pstmt = null;
		List<Role> roles = new ArrayList<Role>();
		pstmt = con.prepareStatement(SQL__READ_ROLES_BY_EMPLOYEE_ID);
		pstmt.setLong(1, employeeId);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			Role role = unMapRole(rs);
			roles.add(role);
		}
		MSsqlDAOFactory.closeStatement(pstmt);
		return roles;
	}

	public List<User> getAllUsers() {
		long t1 = System.currentTimeMillis();
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			return getAllUsers(con);
		} catch (SQLException e) {
			log.error("Can not get all users.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		System.err.println("ScheduleManagerServiceImpl.userRoles "
				+ (System.currentTimeMillis() - t1) + "ms");
		return null;
	}

	private List<User> getAllUsers(Connection con) throws SQLException {
		List<User> users = new ArrayList<User>();
		Statement stmt = null;
		User user = new User();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__GET_ALL_USERS);
			if (rs.next()) {
				user = unMapUser(rs);
				List<Role> roles = getUserRoles(con, user.getUserId());
				user.setRoles(roles);
				users.add(user);
			}
			return users;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					log.error("Can not close statement.", e);
				}
			}
		}
	}

	public List<Long> getEmployeeIdsWitoutUser() {
		long t1 = System.currentTimeMillis();
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			return getEmployeeIdsWitoutUser(con);
		} catch (SQLException e) {
			log.error("Can not get all users.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		System.err.println("ScheduleManagerServiceImpl.userRoles "
				+ (System.currentTimeMillis() - t1) + "ms");
		return null;
	}

	private List<Long> getEmployeeIdsWitoutUser(Connection con)
			throws SQLException {
		List<Long> employees = new ArrayList<Long>();
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__GET_EMPLOYEE_WITHOUT_USER);
			while (rs.next()) {
				employees.add(rs.getLong(MapperParameters.EMPLOYEE__ID));
			}
			return employees;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					log.error("Can not close statement.", e);
				}
			}
		}
	}

	@Override
	public boolean insertUser(User user) throws DAOException {
		boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = insertUser(con, user);
			if (result) {
				con.commit();
			} else {
				MSsqlDAOFactory.rollback(con);
			}
		} catch (SQLException e) {
			MSsqlDAOFactory.rollback(con);
			log.error("Can not insert user.", e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
		return result;
	}

	private boolean insertUser(Connection con, User user) throws SQLException {
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		boolean result = false;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_USER,
					Statement.RETURN_GENERATED_KEYS);
			mapUser(user, pstmt);
			if (pstmt.executeUpdate() != 1)
				return false;
			pstmt2 = con.prepareStatement(SQL__UPDATE_EMPLOYEE_USER_ROLE);
			ResultSet rs = pstmt.getGeneratedKeys();
			if (!rs.next()) {
				return false;
			}
			pstmt2.setLong(1, rs.getLong(1));
			pstmt2.setLong(2, user.getEmployeeId());
			pstmt2.executeUpdate();
			result = true;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
			MSsqlDAOFactory.closeStatement(pstmt2);
		}
		return result;
	}

	@Override
	public boolean updateUser(User user) throws DAOException {
		Connection con = null;
		boolean updateResult = false;
		try {
			con = MSsqlDAOFactory.getConnection();
			updateResult = updateUser(con, user);
			if (updateResult) {
				con.commit();
			} else {
				MSsqlDAOFactory.rollback(con);
			}
		} catch (SQLException e) {
			MSsqlDAOFactory.rollback(con);
			log.error("Can not update user.", e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
		return updateResult;
	}

	private boolean updateUser(Connection con, User user) throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__UPDATE_USER);
			mapUserForUpdate(user, pstmt);
			int updatedRows = pstmt.executeUpdate();
			result = updatedRows == 1;
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
		return result;
	}

	@Override
	public boolean containsOtherUserWithLogin(String login, long userId) {
		Connection con = null;
		boolean result = false;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = containsOtherUserWithLogin(con, login, userId);
		} catch (SQLException e) {
			log.error("Can not get other user with login.", e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
		return result;
	}

	private boolean containsOtherUserWithLogin(Connection con, String login,
			long userId) throws SQLException {
		PreparedStatement pstmt = null;
		boolean result = false;
		try {
			pstmt = con.prepareStatement(SQL__GET_OTHER_USER_WITH_LOGIN);
			pstmt.setString(1, login);
			pstmt.setLong(2, userId);
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
	public User getUserByEmployeeId(long employeeId) {
		Connection con = null;
		User user = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			user = getUserByEmployeeId(con, employeeId);
		} catch (SQLException e) {
			log.error("Can not get user by employee id.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return user;
	}

	private User getUserByEmployeeId(Connection con, long employeeId)
			throws SQLException {
		User user = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__GET_USER_BY_EMPLOYEE_ID);
			pstmt.setLong(1, employeeId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				user = unMapUser(rs);
				List<Role> roles = getUserRoles(con, user.getUserId());
				user.setRoles(roles);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
		return user;
	}

	private void mapUserForUpdate(User user, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setString(1, user.getLogin());
		pstmt.setString(2, user.getPassword());
		pstmt.setLong(3, user.getUserId());
	}

	private User unMapUser(ResultSet rs) throws SQLException {
		User user = new User();
		user.setUserId(rs.getLong(MapperParameters.USER__ID));
		user.setEmployeeId(rs.getLong(MapperParameters.USER__EMPLOYEE_ID));
		user.setLogin(rs.getString(MapperParameters.USER__LOGIN));
		user.setPassword(rs.getString(MapperParameters.USER__PASSWORD));
		return user;
	}

	private Role unMapRole(ResultSet rs) throws SQLException {
		Role role = new Role();
		role.setRoleId(rs.getLong(MapperParameters.ROLE__ID));
		role.setRight(Right.values()[rs.getInt(MapperParameters.ROLE__RIGHTS)]);
		role.setTitle(rs.getString(MapperParameters.ROLE__TITLE));
		return role;
	}

	private void mapUser(User user, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setString(1, Hashing.salt(user.getPassword(), user.getLogin()));
		pstmt.setString(2, user.getLogin());

	}

	@Override
	public Role getRole(Right r) throws DAOException {
		Role role;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			role = getRole(con, r);
		} catch (SQLException e) {
			log.error("Can not get role by right.", e);
			throw new DAOException("Can not get role by right.", e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
		return role;
	}

	public Role getRole(Connection con, Right r) throws SQLException {
		Role role = null;
		PreparedStatement pstmt = null;
		pstmt = con.prepareStatement(SQL__GET_ROLE_BY_RIGHT);
		pstmt.setLong(1, r.ordinal());
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			role = unMapRole(rs);
		}
		MSsqlDAOFactory.closeStatement(pstmt);
		return role;
	}

}
