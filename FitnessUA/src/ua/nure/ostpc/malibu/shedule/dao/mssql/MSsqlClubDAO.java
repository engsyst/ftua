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
	private static final String SQL__FIND_CLUBS_BY_DEPENDENCY = "SELECT * FROM Club WHERE IsIndependent=?;";
	private static final String SQL__FIND_ALL_SCHEDULE_CLUBS = "SELECT * from Club;";
	private static final String SQL__FIND_ALL_MALIBU_CLUBS = "SELECT * from Clubs;";

	@Override
	public boolean updateClub(Club club) {
		Connection con = null;
		boolean updateResult = false;
		try {
			con = MSsqlDAOFactory.getConnection();
			updateResult = updateClub(con, club);
		} catch (SQLException e) {
			log.error("Can not update club.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return updateResult;
	}

	private boolean updateClub(Connection con, Club club) throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		try {
			pstmt = con
					.prepareStatement("UPDATE Club SET [Title]=?, [Cash]=? [isIndependent]=? where [ClubId] = ?;");
			pstmt.setString(1, club.getTitle());
			pstmt.setDouble(2, club.getCash());
			pstmt.setBoolean(3, club.getIsIndependen());
			pstmt.setLong(4, club.getClubId());
			int updatedRows = pstmt.executeUpdate();
			con.commit();
			result = updatedRows != 0;
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
		return result;
	}

	@Override
	public Club findClubById(long clubId) {
		Connection con = null;
		Club club = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			club = findClubById(con, clubId);
		} catch (SQLException e) {
			log.error("Can not find club.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return club;
	}

	private Club findClubById(Connection con, long clubId) throws SQLException {
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
	public Collection<Club> getAllScheduleClubs() {
		Connection con = null;
		Collection<Club> clubs = new ArrayList<Club>();
		try {
			con = MSsqlDAOFactory.getConnection();
			clubs = getAllScheduleClubs(con);
		} catch (SQLException e) {
			log.error("Can not get all schedule clubs.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return clubs;
	}

	private Collection<Club> getAllScheduleClubs(Connection con)
			throws SQLException {
		Statement stmt = null;
		Collection<Club> clubs = new ArrayList<Club>();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__FIND_ALL_SCHEDULE_CLUBS);
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
	public Collection<Club> getAllMalibuClubs() {
		Connection con = null;
		Collection<Club> clubs = new ArrayList<Club>();
		try {
			con = MSsqlDAOFactory.getConnection();
			clubs = getAllMalibuClubs(con);
		} catch (SQLException e) {
			log.error("Can not get all Malibu clubs.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return clubs;
	}

	private Collection<Club> getAllMalibuClubs(Connection con)
			throws SQLException {
		Statement stmt = null;
		Collection<Club> clubs = new ArrayList<Club>();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__FIND_ALL_MALIBU_CLUBS);
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

	public boolean insertClubs(Collection<Club> clubs) {
		boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = insertClubs(clubs, con);
		} catch (SQLException e) {
			log.error("Can not insert clubs.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return result;
	}

	private boolean insertClubs(Collection<Club> clubs, Connection con)
			throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		try {
			pstmt = con
					.prepareStatement("INSERT INTO Club (Title, Cash, isIndependent) VALUES (?, ?, ?);");
			for (Club club : clubs) {
				pstmt.setString(1, club.getTitle());
				pstmt.setDouble(2, club.getCash());
				pstmt.setBoolean(3, club.getIsIndependen());
				pstmt.addBatch();
			}
			result = pstmt.executeBatch().length == clubs.size();
		} catch (SQLException e) {
			throw e;
		}
		return result;
	}

	@Override
	public Collection<Club> getIndependentClubs() {
		Connection con = null;
		Collection<Club> dependentClubs = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			dependentClubs = getClubsByDependency(con, false);
		} catch (SQLException e) {
			log.error("Can not get independent clubs.", e);
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

	@Override
	public Collection<Club> getDependentClubs() {
		Connection con = null;
		Collection<Club> dependentClubs = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			dependentClubs = getClubsByDependency(con, true);
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

	private Collection<Club> getClubsByDependency(Connection con,
			boolean isDependent) throws SQLException {
		PreparedStatement pstmt = null;
		Collection<Club> dependentClubs = null;
		try {
			pstmt = con.prepareStatement(SQL__FIND_CLUBS_BY_DEPENDENCY);
			pstmt.setBoolean(1, isDependent);
			ResultSet rs = pstmt.executeQuery();
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
			if (pstmt != null) {
				try {
					pstmt.close();
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
