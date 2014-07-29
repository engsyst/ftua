package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.ClubDAO;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;

public class MSsqlClubDAO implements ClubDAO {
	private static final Logger log = Logger.getLogger(MSsqlClubDAO.class);

	private static final String SQL__FIND_CLUB_BY_ID = "SELECT * FROM Club WHERE ClubId=?;";

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
									+ "c.title, c.isIndependent,c.QuantityOfPeople * from Club c where c.isIndependent = true"));
			while (resSet.next()) {
				resultClubSet
						.add(new Club(
								resSet.getLong(MapperParameters.CLUB__ID),
								resSet.getString(MapperParameters.CLUB__TITLE),
								resSet.getDouble(MapperParameters.CLUB__CASH),
								resSet.getBoolean(MapperParameters.CLUB__IS_INDEPENDENT),
								resSet.getInt(MapperParameters.CLUB__QuantityOfPeople)));
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

	@Override
	public Club findClubById(Connection con, long clubId) throws SQLException {
		Club club = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__FIND_CLUB_BY_ID);
			pstmt.setLong(1, clubId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				club = unMapClub(rs);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					throw e;
				}
			}
		}
		return club;
	}

	@Override
	public Collection<Club> getMalibuClubs() throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		Collection<Club> resultClubSet = new ArrayList<Club>();
		try {
			resultClubSet = getMalibuClubs(con);
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

	public Collection<Club> getMalibuClubs(Connection con) throws SQLException {
		Statement st = null;
		Collection<Club> resultClubSet = new ArrayList<Club>();
		try {
			st = con.createStatement();
			java.sql.ResultSet resSet = st
					.executeQuery(String
							.format("SELECT c.clubid,"
									+ "c.Title, c.Cash from Clubs c"));
			while (resSet.next()) {
				resultClubSet
						.add(new Club(
								resSet.getLong("clubid"),
								resSet.getString(MapperParameters.CLUB__TITLE),
								resSet.getDouble(MapperParameters.CLUB__CASH),
								false,
								0));
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

	public Collection<Club> getOurClubs() throws SQLException {
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

	public Collection<Club> getOurClubs(Connection con) throws SQLException {
		Statement st = null;
		Collection<Club> resultClubSet = new ArrayList<Club>();
		try {
			st = con.createStatement();
			java.sql.ResultSet resSet = st
					.executeQuery(String
							.format("SELECT c.club_id,"
									+ "c.title, c.isIndependent,c.QuantityOfPeople from Club c"));
			while (resSet.next()) {
				resultClubSet
						.add(new Club(
								resSet.getLong(MapperParameters.CLUB__ID),
								resSet.getString(MapperParameters.CLUB__TITLE),
								resSet.getDouble(MapperParameters.CLUB__CASH),
								resSet.getBoolean(MapperParameters.CLUB__IS_INDEPENDENT),
								resSet.getInt(MapperParameters.CLUB__QuantityOfPeople)));
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

	public void insertClubs(Collection<Club> clubs) throws SQLException {
		Connection con = null;
		try	{
		con = MSsqlDAOFactory.getConnection();
		insertClubs(clubs, con);
		}
		catch (SQLException e) {
			throw e;
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Can not close connection # " + this.getClass()
					+ " # " + e.getMessage());
		}

	}
	public void insertClubs(Collection<Club> clubs, Connection con) throws SQLException {
		PreparedStatement ps = null;
		try {
		ps = con
				.prepareStatement("INSERT INTO Club (club_id, title, isIndependent, QuantityOfPeople) VALUES (?,?,?,?);");	
		}
		catch (SQLException e) {
			throw e;
		}
		for (Club c : clubs) {
			ps.setLong(1,c.getClubId());
			ps.setString(2, c.getTitle());
			ps.setBoolean(3, c.getIsIndependen());
			ps.setDouble(4, c.getQuantityOfPeople());
			ps.executeUpdate();
		}
	}
	
	private Club unMapClub(ResultSet rs) throws SQLException {
		Club club = new Club();
		club.setClubId(rs.getLong(MapperParameters.CLUB__ID));
		club.setTitle(rs.getString(MapperParameters.CLUB__TITLE));
		club.setCash(rs.getDouble(MapperParameters.CLUB__CASH));
		club.setIsIndependent(rs
				.getBoolean(MapperParameters.CLUB__IS_INDEPENDENT));
		club.setQuantityOfPeople(rs.getInt(MapperParameters.CLUB__QuantityOfPeople));
		return club;
	}

}
