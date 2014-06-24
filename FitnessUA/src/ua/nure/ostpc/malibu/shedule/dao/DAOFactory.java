package ua.nure.ostpc.malibu.shedule.dao;

import ua.nure.ostpc.malibu.shedule.dao.mssql.MSsqlDAOFactory;

public abstract class DAOFactory {

	  // List of DAO types supported by the factory
	  public static final int MSSQL = 1;

	  // There will be a method for each DAO that can be 
	  // created. The concrete factories will have to 
	  // implement these methods.
	  public abstract EmployeeDAO getEmployeeDAO();


	  public static DAOFactory getDAOFactory(
	      int whichFactory) {
	  
	    switch (whichFactory) {
	      case MSSQL: 
	          return new MSsqlDAOFactory();
	      default           : 
	          return null;
	    }
	  }
}
