package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.util.Collection;

import ua.nure.ostpc.malibu.shedule.dao.ClubDAO;
import ua.nure.ostpc.malibu.shedule.entity.Club;

public class MSsqlClubDAO implements ClubDAO {
	@Override
	public boolean updateClub(Club club) throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		boolean updateResult = false;
		try {
			updateResult = updateClub(con, ast);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not update Club # " + this.getClass()
					+ " # " + e.getMessage());
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not close connection # " + this.getClass()
					+ " # " + e.getMessage());
		}
		return updateResult;
	}

	public boolean updateClub(Connection con, Club c) throws SQLException {
		Statement st = null;
		int res = 0;
		try{
			 st = con.createStatement();
			 //Здесь должен пойти некоторый запрос на обновление клуба
			 //res = st.executeUpdate(...);
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

	public Collection<Club> selectClubs() throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		ollection<Club> resultClubSet = new Collection<Club>();
		try{
			resultClubSet = selectClubs(con);
		}
		catch(SQLException e){
			e.printStackTrace();
			System.err.println("Can not find Clubs # " + this.getClass()
					+ " # " + e.getMessage());
			return null;
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not close connection # " + this.getClass()
					+ " # " + e.getMessage());
		}
		return resultClubSet;
	}
	@Override
	public Collection<Club> selectClubs(Connection con) throws SQLException {
		Statement st = null;
		Collection<Club> resultClubSet = new Collection<Club>();
		try{
			st = con.createStatement();
			java.sql.ResultSet resSet = st.executeQuery(String.format("select c.club_id,"
			+ "c.title, c.isIndependent * from Club c "));
			while (resSet.next()) {
				resultClubSet.add(new Club(resSet.getLong("club_id"),resSet.getString("title"),resSet.getBoolean("isIndependent")));
			}
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
		return resultClubSet;
	}

}
