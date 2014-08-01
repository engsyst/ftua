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
	private static final String SQL__FIND_DEPENDENT_CLUBS = "SELECT * FROM Club WHERE IsIndependent=0;";

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
					.prepareStatement("UPDATE club c SET [title] = ? AND [isIndependent] = ? where [club_id] = ?;s");
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
			resultClubSet = getIndependentClubs(con);
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

	public Collection<Club> getIndependentClubs(Connection con)
			throws SQLException {
		Statement stmt = null;
		Collection<Club> clubs = new ArrayList<Club>();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(String
					.format("SELECT * from Club where isIndependent=1;"));
			while (rs.next()) {
				Club club = unMapClub(rs);
				clubs.add(club);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					throw e;
				}
			}
		}
		return clubs;
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
	public Collection<Club> getAllClubs() throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		Collection<Club> clubs = new ArrayList<Club>();
		try {
			clubs = getAllClubs(con);
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
		return clubs;
	}

	public Collection<Club> getAllClubs(Connection con) throws SQLException {
		Statement stmt = null;
		Collection<Club> clubs = new ArrayList<Club>();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(String
					.format("SELECT * from Clubs;"));
			while (rs.next()) {
				Club club = unMapClub(rs);
				clubs.add(club);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					throw e;
				}
			}
		}
		return clubs;
	}

	public Collection<Club> getOurClubs() throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		Collection<Club> resultClubSet = new ArrayList<Club>();
		try {
			resultClubSet = getIndependentClubs(con);
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
		Statement stmt = null;
		Collection<Club> clubs = new ArrayList<Club>();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(String
					.format("SELECT * from Club"));
			while (rs.next()) {
				Club club = unMapClub(rs);
				clubs.add(club);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					throw e;
				}
			}
		}
		return clubs;
	}

	public void insertClubs(Collection<Club> clubs) throws SQLException {
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			insertClubs(clubs, con);
		} catch (SQLException e) {
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

	public void insertClubs(Collection<Club> clubs, Connection con)
			throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con
					.prepareStatement("INSERT INTO Club (Title, Cash, isIndependent) VALUES (?, ?, ?);");
		} catch (SQLException e) {
			throw e;
		}
		for (Club club : clubs) {
			pstmt.setString(1, club.getTitle());
			pstmt.setDouble(2, club.getCash());
			pstmt.setBoolean(3, club.getIsIndependen());
			pstmt.addBatch();
		}
		pstmt.executeBatch();
	}

	@Override
	public Collection<Club> getDependentClubs() {
		Connection con = null;
		Collection<Club> dependentClubs = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			dependentClubs = getDependentClubs(con);
		} catch (SQLException e) {
			log.error("Can not get dependent clubs.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return dependentClubs;
	}

	private Collection<Club> getDependentClubs(Connection con)
			throws SQLException {
		Statement stmt = null;
		Collection<Club> dependentClubs = null;
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__FIND_DEPENDENT_CLUBS);
			if (rs.isBeforeFirst()) {
				dependentClubs = new ArrayList<Club>();
			}
			while (rs.next()) {
				Club club = unMapClub(rs);
				dependentClubs.add(club);
			}
			return dependentClubs;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					log.error("Can not close statement.", e);
				}
			}
		}
	}

	private Club unMapClub(ResultSet rs) throws SQLException {
		Club club = new Club();
		club.setClubId(rs.getLong(MapperParameters.CLUB__ID));
		club.setTitle(rs.getString(MapperParameters.CLUB__TITLE));
		club.setCash(rs.getDouble(MapperParameters.CLUB__CASH));
		club.setIsIndependent(rs
				.getBoolean(MapperParameters.CLUB__IS_INDEPENDENT));
		return club;
	}

}
