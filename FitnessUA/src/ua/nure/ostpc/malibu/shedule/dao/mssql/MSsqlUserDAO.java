package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.UserDAO;
import ua.nure.ostpc.malibu.shedule.dao.mapper.MapperParameters;
import ua.nure.ostpc.malibu.shedule.entity.Role;
import ua.nure.ostpc.malibu.shedule.entity.User;

public class MSsqlUserDAO implements UserDAO {
	private static final Logger log = Logger.getLogger(MSsqlUserDAO.class);
	private static final String SQL__FIND_USER_BY_LOGIN = "SELECT * FROM Users WHERE Login=?;";
	private static final String SQL__READ_USER_BY_LOGIN = "SELECT u.UserId, u.EmployeeId, u.Login, u.PwdHache, r.RoleId, r.Rights, r.Title "
			+ "FROM Users u INNER JOIN Role r ON r.RoleId=u.RoleId AND u.Login=?;";
	private static final String SQL__READ_USER_BY_ID = "SELECT u.UserId, u.EmployeeId, u.Login, u.PwdHache, r.RoleId, r.Rights, r.Title "
			+ "FROM Users u INNER JOIN Role r ON r.RoleId=u.RoleId AND u.UserId=?;";

	@Override
	public boolean containsUser(String login) {
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			return containsUser(con, login);
		} catch (SQLException e) {
			log.error("Can not check user containing", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection", e);
			}
		}
		return false;
	}

	private boolean containsUser(Connection con, String login)
			throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__FIND_USER_BY_LOGIN);
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
					log.error("Can not close statement", e);
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
			log.error("Can not get User", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection", e);
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
			}
			return user;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					log.error("Can not close statement", e);
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
			log.error("Can not get User", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection", e);
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
			}
			return user;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					log.error("Can not close statement", e);
				}
			}
		}
	}

	private User unMapUser(ResultSet rs) throws SQLException {
		User user = new User();
		user.setUserId(rs.getLong(MapperParameters.USER__ID));
		user.setEmployeeId(rs.getLong(MapperParameters.USER__EMPLOYEE_ID));
		user.setLogin(rs.getString(MapperParameters.USER__LOGIN));
		user.setPassword(rs.getString(MapperParameters.USER__PASSWORD));
		user.setRole(unMapRole(rs));
		return user;
	}

	private Role unMapRole(ResultSet rs) throws SQLException {
		Role role = new Role();
		role.setRoleId(rs.getLong(MapperParameters.ROLE__ID));
		role.setRights(rs.getInt(MapperParameters.ROLE__RIGHTS));
		role.setTitle(rs.getString(MapperParameters.ROLE__TITLE));
		return role;
	}
}
