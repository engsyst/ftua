package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.AssignmentDAO;
import ua.nure.ostpc.malibu.shedule.dao.ClubDAO;
import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.dao.UserDAO;

public class MSsqlDAOFactory extends DAOFactory {
	private static final Logger log = Logger.getLogger(MSsqlDAOFactory.class);

	@SuppressWarnings("unused")
	private static final String DRIVER = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
	private static final String DB_URL = "jdbc:sqlserver://localhost;instanceName=TEMPUS;database=FitnessUA;integratedSecurity=true";

	public MSsqlDAOFactory() {
	}

	/**
	 * method to create MSSQL connections
	 * 
	 * @return
	 * @throws SQLException
	 */
	protected static synchronized Connection getConnection()
			throws SQLException {
		Connection con = null;
		try {
			con = DriverManager.getConnection(DB_URL);
			con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			con.setAutoCommit(false);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return con;

		/*
		 * Connection con = null; try { Context initContext = new
		 * InitialContext(); Context envContext = (Context)
		 * initContext.lookup("java:/comp/env"); DataSource dataSource =
		 * (DataSource) envContext .lookup("jdbc/FitnessUA"); con =
		 * dataSource.getConnection(); } catch (NamingException ex) {
		 * log.error("Cannot obtain a connection from the pool", ex); }
		 * 
		 * return con;
		 */
		// if (con == null)
		// try {
		// con = DriverManager.getConnection(DB_URL);
		// con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		// con.setAutoCommit(false);
		// } catch (SQLException e) {
		// System.err.println(e.getMessage());
		// }
		/* return con; */
	}

	/*
	 * protected void commit(Connection con) { try { con.commit(); } catch
	 * (SQLException e) { try { con.rollback(); } catch (SQLException e1) {
	 * e1.printStackTrace(); System.err.println(e1.getMessage()); }
	 * System.err.println(e.getMessage()); } }
	 * 
	 * protected void close(Connection con) { try { con.close(); con = null; }
	 * catch (SQLException e) { System.err.println(e.getMessage()); } }
	 * 
	 * protected void commitAndClose(Connection con) { try { con.commit(); }
	 * catch (SQLException e) { System.err.println(e.getMessage()); } try {
	 * con.close(); con = null; } catch (SQLException e) {
	 * System.err.println(e.getMessage()); } }
	 */public EmployeeDAO getEmployeeDAO() {
		return new MSsqlEmployeeDAO();
	}

	@Override
	public ClubDAO getClubDAO() {
		return new MSsqlClubDAO();
	}

	@Override
	public AssignmentDAO getAssignmentDAO() {
		return new MSsqlAssignmentDAO();
	}

	@Override
	public ScheduleDAO getScheduleDAO() {
		return new MSsqlScheduleDAO();
	}

	@Override
	public UserDAO getUserDAO() {
		return new MSsqlUserDAO();
	}
}
