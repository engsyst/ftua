package ua.nure.ostsp.malibu.shedule.dao.mssql;

import java.sql.Connection;

import ua.nure.ostsp.malibu.shedule.dao.DAOFactory;
import ua.nure.ostsp.malibu.shedule.dao.EmployeeDAO;

public class MSsqlDAOFactory extends DAOFactory{
	  public static final String DRIVER=
			    "com.microsoft.jdbc.sqlserver.SQLServerDriver";
	  public static final String DBURL=
	    "jdbc:microsoft:sqlserver://localhost:1433, userName, password";
	  // method to create MSSQL connections
	  public static Connection createConnection() {
		return null;
	    // Use DRIVER and DBURL to create a connection
	    // Recommend connection pool implementation/usage
	  }
	  public EmployeeDAO getEmployeeDAO() {
	    // CloudscapeCustomerDAO implements CustomerDAO
	    return new MSsqlEmployeeDAO();
	  }
}
