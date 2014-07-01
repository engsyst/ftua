package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.util.Set;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import ua.nure.ostpc.malibu.shedule.dao.AssignmentDAO;
import ua.nure.ostpc.malibu.shedule.entity.Assignment;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

public class MSsqlAssignmentDAO implements AssignmentDAO {

	@Override
	 	public int insertAssignment(Assignment ast) {
	 		// TODO Auto-generated method stub
	 	return 0;
	 	public int insertAssignment(Connection con, Assignment ast) throws SQLException
	 	{
	 		Statement st = null;
	 	int res = 0;
	 		try{
	 			 st = con.createStatement();
	 			 res = st.executeUpdate(String.format("insert into Assignment(day_shedule_id,day,halfOfDay,user_id,club_id) "
	 			 		+ "values(%1$d,%2$d,%3$d,%4$d,%5%d)", ast.getAssignment_Id(),ast.getDate(), ast.getHalfOfDay(), ast.getEmployee().getEmployeeId(), 
	 			 		ast.getClub().getClubId()));
	 			 		
	 		}
	 		catch(SQLException e){
	 			throw e;
	 		}
	 		finally{
	 			if(st!=null){
	 				try{
	 					st.close();
	 				}
	 				catch(SQLException e){
	 					throw e;
	 				}
	 			}
	 		}
	 		return res;
	  	}
	 	
	 	public int insertAssignment(Assignment ast) throws SQLException
	 	 	{
	 			Connection con = MSsqlDAOFactory.getConnection();
	 			int insertResult = 0;
	 			try {
	 				updateResult = insertAssignment(con, ast);
	 			} catch (SQLException e) {
	 	 			e.printStackTrace();
	 	 			System.err.println("Can not update any Assignment # " + this.getClass()
	 	 					+ " # " + e.getMessage());
	 	 		}
	 	 		try {
	 	 			con.close();
	 	 		} catch (SQLException e) {
	 	 			e.printStackTrace();
	 	 			System.err.println("Can not close any connection # " + this.getClass()
	 	 					+ " # " + e.getMessage());
	 	 		}
	 	 		return insertResult;
	 	 	}

	@Override
	public boolean deleteAssignment(Assignment ast) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean deleteAssignment(Connection con, Assignment ast) throws SQLException
	{
	 			Statement st = null;
	 			int res = 0;
	 			try{
	 				 st = con.createStatement();
	 				 res = st.executeUpdate(String.format("delete * from DayShedule where day_shedule_id = %1%d",ast.getAssignment_Id()));
	 			}
	 			catch(SQLException e){
	 				throw e;
	 			}
	 			finally{
	 				if(st!=null){
	 					try{
	 						st.close();
	 					}
	 					catch(SQLException e){
	 						throw e;
	 					}
	 				}
	 			}
	 			if (res == 0)
	 			return false;
	 			else 
	 				return true;
	 		}
	public boolean deleteAssignment(Assignment ast) throws SQLException
	{
		Connection con=MSsqlDAOFactory.getConnection();
		boolean deleteResult = false;
			try {
				deleteResult = deleteAssignment(con, ast);
			} catch (SQLException e) {
	 			e.printStackTrace();
	 			System.err.println("Can not delete any Assignment # " + this.getClass()
	 					+ " # " + e.getMessage());
	 		}
	 		try {
	 			con.close();
	 		} catch (SQLException e) {
	 			e.printStackTrace();
	 			System.err.println("Can not close any connection # " + this.getClass()
	 					+ " # " + e.getMessage());
	 		}
	 		return deleteResult;
	}

	@Override
	public Assignment findAssignment(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateAssignment(Assignment ast) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean updateAssignment(Connection con, Assignment ast) throws SQLException
	 	{
	 		Statement st = null;
	 		int res = 0;
	 		try{
	 			 st = con.createStatement();
	 			 res = st.executeUpdate(String.format("update DayShedule set date = %2$d, halfOfDay =%3$d,"
	 		 		+ "user_id=%4$d, club_id=%5$d  where day_shedule_id=%1$d",ast.getAssignment_Id(),ast.getDate(),
	 			 		ast.getHalfOfDay(),ast.getEmployee().getEmployeeId(),ast.getClub().getClubId()));
	 		}
	 		catch(SQLException e){
	 			throw e;
	 		}
	 		finally{
	 			if(st!=null){
	 			try{
	 					st.close();
	 				}
	 				catch(SQLException e){
	 					throw e;
	 				}
	 			}
	 		}
	 		if (res == 0)
	 		return false;
	 		   else 
	 			return true;
	 	}
	public boolean updateAssignment(Assignment ast) throws SQLException
	{
		Connection con=MSsqlDAOFactory.getConnection();
		boolean updateResult = false;
			try {
				updateResult = updateAssignment(con, ast);
			} catch (SQLException e) {
	 			e.printStackTrace();
	 			System.err.println("Can not update any Assignment # " + this.getClass()
	 					+ " # " + e.getMessage());
	 		}
	 		try {
	 			con.close();
	 		} catch (SQLException e) {
	 			e.printStackTrace();
	 			System.err.println("Can not close any connection # " + this.getClass()
	 					+ " # " + e.getMessage());
	 		}
	 		return updateResult;
	}

	@Override
	public Set<Assignment> selectAssignments(Period period) {
		// TODO Auto-generated method stub
		return null;
	}

}
