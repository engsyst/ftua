package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.AssignmentExcelDAO;
import ua.nure.ostpc.malibu.shedule.dao.CategoryDAO;
import ua.nure.ostpc.malibu.shedule.dao.ClubDAO;
import ua.nure.ostpc.malibu.shedule.dao.ClubDayScheduleDAO;
import ua.nure.ostpc.malibu.shedule.dao.ClubPrefDAO;
import ua.nure.ostpc.malibu.shedule.dao.DAOException;
import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.PreferenceDAO;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.dao.ShiftDAO;
import ua.nure.ostpc.malibu.shedule.dao.UserDAO;

public class MSsqlDAOFactory extends DAOFactory {
	private static final Logger log = Logger.getLogger(MSsqlDAOFactory.class);

	private static String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static String DB_URL = "jdbc:sqlserver://localhost:1433; database=FitnessUA; user=sa; password=admin;";
	
	public static String getDriver() {
		return DRIVER;
	}

	public static void setDriver(String dRIVER) {
		DRIVER = dRIVER;
	}

	public static String getDbUrl() {
		return DB_URL;
	}

	public static void setDbUrl(String dB_URL) {
		DB_URL = dB_URL;
	}

	public MSsqlDAOFactory() {
	}

	/**
	 * method to create MSSQL connections
	 * 
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
	static Connection getConnection() throws SQLException {
		Connection con = null;
		try {
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			
			DataSource ds = (DataSource)envContext.lookup("jdbc/sqlserver");
			con = ds.getConnection();
			con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			con.setAutoCommit(false);
		} catch (NamingException ex) {
			log.error("Cannot obtain a connection from the pool", ex);			
			throw new SQLException("Cannot obtain a connection from the pool", ex);
		}
		return con;
	}

//	protected static Connection getConnection()
//			throws SQLException {
//		Connection con = null;
//		try {
//			con = DriverManager.getConnection(DB_URL);
//			con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
//			con.setAutoCommit(false);
//		} catch (SQLException e) {
//			log.error("Can not get connection.", e);
//			throw e;
//		}
//		return con;
//
//	}

//	protected static synchronized Connection getConnection()
//			throws SQLException {
//		Connection con = null;
//		try {
//			con = DriverManager.getConnection(DB_URL);
//			con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
//			con.setAutoCommit(false);
//		} catch (SQLException e) {
//			log.error("Can not get connection.", e);
//			throw e;
//		}
//		return con;
//
//	}

	public static void roolback(Connection con) throws DAOException {
		if (con != null) {
			try {
				con.rollback();
			} catch (SQLException e) {
				log.error("Can not rollback transaction.", e);
				throw new DAOException("Can not rollback transaction", e);
			}
		}
	}

	static void commit(Connection con) {
		try {
			log.debug("Try commit transaction");
			con.commit();
		} catch (SQLException e) {
			try {
				log.error("Try rollback transaction");
				con.rollback();
			} catch (SQLException e1) {
				log.error("Can not rollback transaction # " + e1.getMessage());
			}
			log.error("Can not commit transaction # " + e.getMessage());
		}
	}

	static void close(Connection con) {
		try {
			log.debug("Try close connection");
			if (con != null) {
				con.close();
				con = null;
			}
		} catch (SQLException e) {
			log.error("Can not close connection # " + e.getMessage());
		}
	}

	static void closeStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				log.error(e.getLocalizedMessage(), e);
			}
		}
	}

	static void commitAndClose(Connection con) {
		commit(con);
		close(con);
	}

	public EmployeeDAO getEmployeeDAO() {
		return new MSsqlEmployeeDAO();
	}

	@Override
	public ClubDAO getClubDAO() {
		return new MSsqlClubDAO();
	}

	@Override
	public AssignmentExcelDAO getAssignmentExcelDAO() {
		return new MSsqlAssignmentExcelDAO();
	}

	@Override
	public ScheduleDAO getScheduleDAO() {
		return new MSsqlScheduleDAO();
	}

	@Override
	public UserDAO getUserDAO() {
		return new MSsqlUserDAO();
	}

	@Override
	public ShiftDAO getShiftDAO() {
		return new MSsqlShiftDAO();
	}

	@Override
	public ClubDayScheduleDAO getClubDayScheduleDAO() {
		return new MSsqlClubDayScheduleDAO();
	}

	@Override
	public ClubPrefDAO getClubPrefDAO() {
		return new MSsqlClubPrefDAO();
	}

	@Override
	public PreferenceDAO getPreferenceDAO() {
		return new MSsqlPreferenceDAO();
	}

	@Override
	public CategoryDAO getCategoryDAO() {
		return new MSsqlCategoryDAO();
	}

}
