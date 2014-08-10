package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private static final String SQL__UPDATE_CLUB = "UPDATE Club SET [Title]=?, [Cash]=?, [isIndependent]=? WHERE [ClubId]=?;";
	private static final String SQL__INSERT_CLUB = "INSERT INTO Club (Title, Cash, isIndependent) VALUES (?, ?, ?);";
	private static final String SQL__JOIN_CONFORMITY = "SELECT c1.ClubId, c1.Title, c1.Cash, c1.isIndependent, c2.OriginalClubId from Club c1 INNER JOIN ComplianceClub c2 "
			+ "ON c1.ClubId=c2.OurClubId";
	private static final String SQL__DELETE_CLUB = "DELETE FROM Club WHERE ClubId=?";
	private static final String SQL__INSERT_CLUB_TO_CONFORMITY = "INSERT INTO ComplianceClub (OriginalClubId, OurClubId) VALUES (?, "
			+ "(SELECT c.ClubId FROM Club c WHERE c.Title = ? and c.Cash = ?));";
	private static final String SQL__FIND_OUR_CLUBS = "SELECT * FROM Club c where c.ClubId not in (select c2.OurClubId from ComplianceClub c2)";

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
			pstmt = con.prepareStatement(SQL__UPDATE_CLUB);
			mapClubForUpdate(club, pstmt);
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
				Club club = unMapMalibuClub(rs);
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
			pstmt = con.prepareStatement(SQL__INSERT_CLUB);
			for (Club club : clubs) {
				mapClubForInsert(club, pstmt);
				pstmt.addBatch();
			}
			result = pstmt.executeBatch().length == clubs.size();
			con.commit();
		} catch (SQLException e) {
			throw e;
		}
		return result;
	}

	public boolean insertClubsWithConformity(Collection<Club> clubs) {
		boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = insertClubsWithConformity(clubs, con);
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

	private boolean insertClubsWithConformity(Collection<Club> clubs,
			Connection con) throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_CLUB);
			pstmt2 = con.prepareStatement(SQL__INSERT_CLUB_TO_CONFORMITY);
			for (Club club : clubs) {
				mapClubForInsert(club, pstmt);
				pstmt2.setLong(1, club.getClubId());
				pstmt2.setString(2, club.getTitle());
				pstmt2.setDouble(3, club.getCash());
				pstmt.addBatch();
				pstmt2.addBatch();
			}
			result = pstmt.executeBatch().length == clubs.size();
			result = pstmt2.executeBatch().length == clubs.size() && result;
			con.commit();
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
	public List<Club> getDependentClubs() {
		Connection con = null;
		List<Club> dependentClubs = null;
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

	private List<Club> getClubsByDependency(Connection con, boolean isDependent)
			throws SQLException {
		PreparedStatement pstmt = null;
		List<Club> dependentClubs = null;
		try {
			pstmt = con.prepareStatement(SQL__FIND_CLUBS_BY_DEPENDENCY);
			pstmt.setBoolean(1, !isDependent);
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

	public Map<Long, Club> getConformity() {
		Connection con = null;
		Map<Long, Club> dict = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			dict = getConformity(con);
		} catch (SQLException e) {
			log.error("Can not get conformity dictionary.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return dict;
	}

	private Map<Long, Club> getConformity(Connection con) throws SQLException {
		Statement stmt = null;
		Map<Long, Club> dict = new HashMap<Long, Club>();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__JOIN_CONFORMITY);
			while (rs.next()) {
				Club club = (new Club(rs.getLong(MapperParameters.CLUB__ID),
						rs.getString(MapperParameters.CLUB__TITLE),
						rs.getLong(MapperParameters.CLUB__CASH),
						rs.getBoolean(MapperParameters.CLUB__IS_INDEPENDENT)));
				dict.put(rs.getLong("OriginalClubId"), club);
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
		return dict;

	}

	@Override
	public Collection<Club> getOnlyOurClub() {
		Connection con = null;
		Collection<Club> ourClub = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			ourClub = getOnlyOurClub(con);
		} catch (SQLException e) {
			log.error("Can not get our clubs.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return ourClub;
	}

	private Collection<Club> getOnlyOurClub(Connection con) throws SQLException {
		Statement stmt = null;
		Collection<Club> clubs = new ArrayList<Club>();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__FIND_OUR_CLUBS);
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

	public Boolean deleteClub(long id) {
		Connection con = null;
		Boolean result = false;
		try {
			con = MSsqlDAOFactory.getConnection();
			deleteClub(id, con);
			result = true;
		} catch (SQLException e) {
			log.error("Can not delete club.", e);
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

	private void deleteClub(long id, Connection con) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__DELETE_CLUB);
			pstmt.setLong(1, id);
			pstmt.executeUpdate();
			con.commit();
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

	}

	private void mapClubForInsert(Club club, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setString(1, club.getTitle());
		pstmt.setDouble(2, club.getCash());
		pstmt.setBoolean(3, club.getIsIndependen());
	}

	private void mapClubForUpdate(Club club, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setString(1, club.getTitle());
		pstmt.setDouble(2, club.getCash());
		pstmt.setBoolean(3, club.getIsIndependen());
		pstmt.setLong(4, club.getClubId());
	}

	private Club unMapMalibuClub(ResultSet rs) throws SQLException {
		Club club = new Club();
		club.setClubId(rs.getLong(MapperParameters.CLUB__ID));
		club.setTitle(rs.getString(MapperParameters.CLUB__TITLE));
		club.setCash(rs.getDouble(MapperParameters.CLUB__CASH));
		return club;
	}

	private Club unMapClub(ResultSet rs) throws SQLException {
		Club club = unMapMalibuClub(rs);
		club.setIsIndependent(rs
				.getBoolean(MapperParameters.CLUB__IS_INDEPENDENT));
		return club;
	}
}
