package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.ClubDAO;
import ua.nure.ostpc.malibu.shedule.entity.Club;

public class MSsqlClubDAO implements ClubDAO {
	private static final Logger log = Logger.getLogger(MSsqlClubDAO.class);

	@Override
	public boolean updateClub(Club club) throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		boolean updateResult = false;
		try {
			updateResult = updateClub(con, club);
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("Can not update Club", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection", e);
			}
		}
		return updateResult;
	}

	public boolean updateClub(Connection con, Club club) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con
					.prepareStatement("UPDATE club c SET [title] = ? AND [isIndependent] = ? where [club_id] = ? ");
			pstmt.setString(1, club.getTitle());
			pstmt.setBoolean(2, club.getIsIndependen());
			pstmt.setLong(3, club.getClubId());
			int res = pstmt.executeUpdate();
			con.commit();
			return res != 0;
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
	public Collection<Club> selectClubs() throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		Collection<Club> resultClubSet = new ArrayList<Club>();
		try {
			resultClubSet = selectClubs(con);
		} catch (SQLException e) {
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

	public Collection<Club> selectClubs(Connection con) throws SQLException {
		Statement st = null;
		Collection<Club> resultClubSet = new ArrayList<Club>();
		try {
			st = con.createStatement();
			java.sql.ResultSet resSet = st
					.executeQuery(String
							.format("SELECT c.club_id,"
									+ "c.title, c.isIndependent * from Club c where c.isIndependent = true"));
			while (resSet.next()) {
				resultClubSet
						.add(new Club(resSet.getLong("club_id"), resSet
								.getString("title"), resSet
								.getBoolean("isIndependent")));
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					throw e;
				}
			}
		}
		return resultClubSet;
	}

}
