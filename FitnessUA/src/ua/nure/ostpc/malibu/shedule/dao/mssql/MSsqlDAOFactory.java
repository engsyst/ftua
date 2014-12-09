package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.AssignmentExcelDAO;
import ua.nure.ostpc.malibu.shedule.dao.CategoryDAO;
import ua.nure.ostpc.malibu.shedule.dao.ClubDAO;
import ua.nure.ostpc.malibu.shedule.dao.ClubDayScheduleDAO;
import ua.nure.ostpc.malibu.shedule.dao.ClubPrefDAO;
import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.HolidayDAO;
import ua.nure.ostpc.malibu.shedule.dao.PreferenceDAO;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.dao.ShiftDAO;
import ua.nure.ostpc.malibu.shedule.dao.UserDAO;

public class MSsqlDAOFactory extends DAOFactory {
	private static final Logger log = Logger.getLogger(MSsqlDAOFactory.class);

	private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static final String DB_URL = "jdbc:sqlserver://localhost; instanceName=SQLEXPRESS; database=FitnessUA; user=sa; password=master;";

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
			Class.forName(DRIVER);
			con = DriverManager.getConnection(DB_URL);
			con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			con.setAutoCommit(false);
		} catch (SQLException e) {
			log.error("Can not get connection.", e);
		} catch (ClassNotFoundException e) {
			log.error("Can not load driver.", e);
		}
		return con;

	}

	protected static void commit(Connection con) {
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

	protected static void close(Connection con) {
		try {
			log.debug("Try close connection");
			con.close();
			con = null;
		} catch (SQLException e) {
			log.error("Can not close connection # " + e.getMessage());
		}
	}

	protected static void commitAndClose(Connection con) {
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

	@Override
	public HolidayDAO getHolidayDAO() {
		return new MSsqlHolidayDAO();
	}
}
