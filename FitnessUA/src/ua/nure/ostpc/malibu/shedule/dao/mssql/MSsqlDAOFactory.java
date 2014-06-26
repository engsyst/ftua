package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import ua.nure.ostpc.malibu.shedule.dao.AssignmentDAO;
import ua.nure.ostpc.malibu.shedule.dao.ClubDAO;
import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;

public class MSsqlDAOFactory extends DAOFactory {
	private static final String DRIVER = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
	private static final String DB_URL = "jdbc:microsoft:sqlserver://localhost:1433, userName, password";

	private static Connection con;

	public MSsqlDAOFactory() {
	}

	/**
	 * method to create MSSQL connections
	 * 
	 * @return
	 * @throws SQLException
	 */
	protected static synchronized Connection getConnection() throws SQLException {
		if (con == null)
			try {
				con = DriverManager.getConnection(DB_URL);
				con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
				con.setAutoCommit(false);
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		return con;
	}

/*	protected void commit(Connection con) {
		try {
			con.commit();
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.err.println(e1.getMessage());
			}
			System.err.println(e.getMessage());
		}
	}

	protected void close(Connection con) {
		try {
			con.close();
			con = null;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}

	protected void commitAndClose(Connection con) {
		try {
			con.commit();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		try {
			con.close();
			con = null;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}

*/	public EmployeeDAO getEmployeeDAO() {
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
}
