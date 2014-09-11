package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.UserDAO;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Right;
import ua.nure.ostpc.malibu.shedule.entity.Role;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;

public class MSsqlUserDAO implements UserDAO {
	private static final Logger log = Logger.getLogger(MSsqlUserDAO.class);

	private static final String SQL__CONTAINS_USER_WITH_LOGIN = "SELECT * FROM Users WHERE Login=?;";
	private static final String SQL__READ_USER_BY_LOGIN = "SELECT DISTINCT u.UserId, eur.EmployeeId, u.PwdHache, u.Login "
			+ "FROM Users u INNER JOIN EmployeeUserRole eur ON u.UserId=eur.UserId AND u.Login=?;";
	private static final String SQL__READ_USER_BY_ID = "SELECT DISTINCT u.UserId, eur.EmployeeId, u.PwdHache, u.Login "
			+ "FROM Users u INNER JOIN EmployeeUserRole eur ON u.UserId=eur.UserId AND u.UserId=?;";
	private static final String SQL__READ_ROLES_BY_USER_ID = "SELECT r.RoleId, r.Rights, r.Title "
			+ "FROM Role r INNER JOIN EmployeeUserRole eur ON r.RoleId=eur.RoleId AND eur.UserId=?;";
	private static final String SQL__GET_ALL_USERS = "SELECT * FROM Users ";
	private static final String SQL__INSERT_USER= "INSERT INTO User ("
			+ "Firstname, Secondname, Lastname, Birthday, Address, "
			+ "Passportint, Idint, CellPhone, WorkPhone, HomePhone, Email, Education, "
			+ "Notes, PassportIssuedBy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	
	@Override
	public boolean containsUser(String login) {
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			return containsUser(con, login);
		} catch (SQLException e) {
			log.error("Can not check user containing.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
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
	public User getUser(String login) {
		Connection con = null;
		User user = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			user = getUser(con, login);
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
				List<Role> roles = getUserRoles(user.getUserId());
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
				List<Role> roles = getUserRoles(user.getUserId());
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
		Connection con = null;
		List<Role> roles = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			roles = getUserRoles(con, userId);
		} catch (SQLException e) {
			log.error("Can not get user roles.", e);
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

	private List<Role> getUserRoles(Connection con, long userId)
			throws SQLException {
		PreparedStatement pstmt = null;
		List<Role> roles = null;
		try {
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
			return roles;
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
	
	public List<User> getAllUsers() {
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
		return null;
	}

	private List<User> getAllUsers(Connection con)
			throws SQLException {
		List<User> users = null;
		Statement stmt = null;
		User user = new User();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__GET_ALL_USERS);
			if (rs.next()) {
				user = unMapUser(rs);
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
	
	public List<Long> getUserEmployeeIds() {
		List<Long> ids = null;
		List<User> users = getAllUsers();
		for (User u : users) {
			ids.add(u.getEmployeeId());
		}
		return ids;
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

	@Override
	public boolean insertUser(User user) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean insertUser(User user, Connection con) {
		// TODO Auto-generated method stub
		return false;
	}

}
