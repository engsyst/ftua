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
import ua.nure.ostpc.malibu.shedule.dao.DAOException;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubSettingViewData;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;

public class MSsqlClubDAO implements ClubDAO {
	private static final Logger log = Logger.getLogger(MSsqlClubDAO.class);

	private static final String SQL__FIND_CLUB_BY_ID = "SELECT * FROM Club WHERE ClubId=?;";
	private static final String SQL__FIND_CLUBS_BY_DEPENDENCY = "SELECT * FROM Club WHERE IsIndependent=?;";
	private static final String SQL__FIND_SCHEDULE_CLUBS = "SELECT * from Club WHERE IsIndependent=0 and IsDeleted=0;";
	private static final String SQL__FIND_ALL_OUR_CLUBS = "SELECT * from Clubs;";
	private static final String SQL__UPDATE_CLUB = "UPDATE Club SET Title=?, isIndependent=?, isDeleted=? WHERE ClubId=?;";
	private static final String SQL__INSERT_CLUB = "INSERT INTO Club (Title, isIndependent, isDeleted) VALUES (?, ?, ?);";
	private static final String SQL__JOIN_CONFORMITY = "SELECT c1.ClubId, c1.Title, c1.isIndependent, c1.isDeleted, c2.OriginalClubId from Club c1 INNER JOIN ComplianceClub c2 "
			+ "ON c1.ClubId=c2.OurClubId";
	private static final String SQL__DELETE_CLUB = "UPDATE Club SET IsDeleted = ? WHERE ClubId = ?";
	private static final String SQL__INSERT_CLUB_TO_CONFORMITY = "INSERT INTO ComplianceClub (OriginalClubId, OurClubId) VALUES (?, "
			+ "(SELECT c.ClubId FROM Club c WHERE c.Title = ?));";
	private static final String SQL__FIND_OUR_CLUBS = "SELECT * FROM Club c where c.ClubId not in (select c2.OurClubId from ComplianceClub c2)";
	private static final String SQL_FIND_SCHEDULE_PERIODS_BY_CLUB_ID = "SELECT DISTINCT ScheduleClubDay.SchedulePeriodId FROM ScheduleClubDay "
			+ "INNER JOIN Club ON Club.ClubId=ScheduleClubDay.ClubId AND Club.ClubId=?;";
	private static final String SQL__FIND_ALL_CLUBS = "SELECT    Club.ClubId as inId, Club.Title as inTitle, "
			+ "Club.IsIndependent as inIsIndependent, Club.IsDeleted as inIsDeleted, "
			+ "Clubs.ClubId as outId,  Clubs.Title as outTitle "
			+ "FROM Club "
			+ "FULL JOIN ComplianceClub ON Club.ClubId = ComplianceClub.OurClubID "
			+ "FULL JOIN Clubs ON ComplianceClub.OriginalClubId = Clubs.ClubId "
			+ " ORDER BY ISNULL(Club.Title, 'яяя'), inTitle ASC, outTitle ASC";

	private static final String SQL__CLUB_INDEPENDENT = "UPDATE Club SET IsIndependent=? WHERE ClubId=?";

	private static final String SQL__IMPORT_CLUB = "INSERT INTO Club (Title,IsIndependent,IsDeleted) "
			+ "VALUES (?, ?, ?);";
	private static final String SQL__SET_COMPLIANCE = "INSERT INTO ComplianceClub (OriginalClubId, OurClubID) "
			+ "VALUES (?, ?);";

	private static final String SQL__DELETE_COMPLIANCE = "DELETE FROM ComplianceClub WHERE OurClubID = ?";

	@Override
	public Club updateClub(Club club) throws DAOException {
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			club = updateClub(con, club);
		} catch (SQLException e) {
			log.error("Can not update club.", e);
			throw new DAOException("Невозможно добывить/изменить клуб");
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		return club;
	}

	private Club updateClub(Connection con, Club club) throws DAOException, SQLException {
		PreparedStatement pstmt = null;
		if (club.getClubId() != 0) { 
			pstmt = con.prepareStatement(SQL__UPDATE_CLUB);
			mapClubForUpdate(club, pstmt);
		} else {
			pstmt = con.prepareStatement(SQL__INSERT_CLUB, PreparedStatement.RETURN_GENERATED_KEYS);
			mapClubForInsert(club, pstmt);
			if (pstmt.executeUpdate() > 0) {
				ResultSet rs = pstmt.getGeneratedKeys();
				rs.next();
				club.setClubId(rs.getLong(1));
			} else {
				throw new DAOException("Can not insert club");
			}
		}
		MSsqlDAOFactory.closeStatement(pstmt);
		return club;
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

	public Club findClubById(Connection con, long clubId) throws SQLException {
		Club club = null;
		PreparedStatement pstmt = null;
		pstmt = con.prepareStatement(SQL__FIND_CLUB_BY_ID);
		pstmt.setLong(1, clubId);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			club = unMapClub(rs);
		}
		MSsqlDAOFactory.closeStatement(pstmt);
		return club;
	}

	@Override
	public Collection<Club> getAllScheduleClubs() {
		long t1 = System.currentTimeMillis();
		Connection con = null;
		Collection<Club> clubs = new ArrayList<Club>();
		try {
			con = MSsqlDAOFactory.getConnection();
			clubs = getAllScheduleClubs(con);
		} catch (SQLException e) {
			log.error("Can not get all schedule clubs.", e);
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		System.err.println("ScheduleManagerServiceImpl.userRoles "
				+ (System.currentTimeMillis() - t1) + "ms");
		return clubs;
	}

	private Collection<Club> getAllScheduleClubs(Connection con)
			throws SQLException {
		Statement stmt = null;
		Collection<Club> clubs = new ArrayList<Club>();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__FIND_ALL_OUR_CLUBS);
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
	public Collection<Club> getAllOuterClubs() {
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
			ResultSet rs = stmt.executeQuery(SQL__FIND_ALL_OUR_CLUBS);
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

	public List<Club> getClubsByDependency(Connection con, boolean isDependent)
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
						rs.getBoolean(MapperParameters.CLUB__IS_INDEPENDENT),
						rs.getBoolean(MapperParameters.CLUB__IS_DELETED)));
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

	@Override
	public boolean containsInSchedules(long clubId) {
		Connection con = null;
		boolean result = false;
		try {
			if (log.isDebugEnabled())
				log.debug("Try check club in schedules.");
			con = MSsqlDAOFactory.getConnection();
			result = containsInSchedules(con, clubId);
		} catch (SQLException e) {
			log.error("Can not check club in schedules. ", e);
			return false;
		}
		MSsqlDAOFactory.commitAndClose(con);
		return result;
	}

	private boolean containsInSchedules(Connection con, long clubId)
			throws SQLException {
		PreparedStatement pstmt = null;
		boolean result = false;
		try {
			pstmt = con.prepareStatement(SQL_FIND_SCHEDULE_PERIODS_BY_CLUB_ID);
			pstmt.setLong(1, clubId);
			ResultSet rs = pstmt.executeQuery();
			result = rs.isBeforeFirst();
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
		return result;
	}

	private void mapClubForInsert(Club club, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setString(1, club.getTitle());
		pstmt.setBoolean(2, club.isIndependent());
		pstmt.setBoolean(3, club.isDeleted());
	}

	private void mapClubForUpdate(Club club, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setString(1, club.getTitle());
		pstmt.setBoolean(2, club.isIndependent());
		pstmt.setBoolean(3, club.isDeleted());
		pstmt.setLong(4, club.getClubId());
	}

	private Club unMapMalibuClub(ResultSet rs) throws SQLException {
		Club club = new Club();
		club.setClubId(rs.getLong(MapperParameters.CLUB__ID));
		club.setTitle(rs.getString(MapperParameters.CLUB__TITLE));
		return club;
	}

	private Club unMapClub(ResultSet rs) throws SQLException {
		Club club = new Club();
		club.setClubId(rs.getLong(MapperParameters.CLUB__ID));
		club.setTitle(rs.getString(MapperParameters.CLUB__TITLE));
		club.setIndependent(rs
				.getBoolean(MapperParameters.CLUB__IS_INDEPENDENT));
		club.setDeleted(rs.getBoolean(MapperParameters.CLUB__IS_DELETED));
		return club;
	}

	@Override
	public List<Club> getScheduleClubs() {
		long t1 = System.currentTimeMillis();
		Connection con = null;
		List<Club> clubs = new ArrayList<Club>();
		try {
			con = MSsqlDAOFactory.getConnection();
			clubs = getScheduleClubs(con);
		} catch (SQLException e) {
			log.error("Can not get all schedule clubs.", e);
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		System.err.println("ScheduleManagerServiceImpl.userRoles "
				+ (System.currentTimeMillis() - t1) + "ms");
		return clubs;
	}

	private List<Club> getScheduleClubs(Connection con) throws SQLException {
		Statement stmt = null;
		List<Club> clubs = new ArrayList<Club>();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__FIND_SCHEDULE_CLUBS);
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

	// ================================

	@Override
	public List<ClubSettingViewData> getAllClubs() throws Exception {
		long t1 = System.currentTimeMillis();
		Connection con = null;
		List<ClubSettingViewData> clubs = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			clubs = getAllClubs(con);
		} catch (SQLException e) {
			log.error("Can not get all clubs.", e);
			throw e;
		} finally {
			MSsqlDAOFactory.close(con);
		}
		System.err.println("MSsqlClubDAO.getAllClubs "
				+ (System.currentTimeMillis() - t1) + "ms");
		return clubs;
	}

	private List<ClubSettingViewData> getAllClubs(Connection con)
			throws Exception {
		Statement stmt = null;
		List<ClubSettingViewData> clubs = new ArrayList<ClubSettingViewData>();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__FIND_ALL_CLUBS);
			while (rs.next()) {
				clubs.add(unMapClubSettingsViewData(rs));
			}
		} catch (SQLException e) {
			log.error("Can not get all clubs.", e);
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(stmt);
		}
		return clubs;
	}

	private ClubSettingViewData unMapClubSettingsViewData(ResultSet rs)
			throws SQLException {
		ClubSettingViewData cvd = new ClubSettingViewData();
		Club c = null;
		String s = rs.getString("inTitle");
		if (s != null) {
			c = new Club();
			c.setClubId(rs.getLong("inId"));
			c.setTitle(rs.getString("inTitle"));
			c.setIndependent(rs.getBoolean("inIsIndependent"));
			c.setDeleted(rs.getBoolean("inIsDeleted"));
		}
		cvd.setInner(c);
		c = null;
		s = rs.getString("outTitle");
		if (s != null) {
			c = new Club();
			c.setTitle(s);
			c.setClubId(rs.getLong("outId"));
		}
		cvd.setOuter(c);
		return cvd;
	}

	@Override
	public Club setClubIndependent(long id, boolean isIndepended)
			throws DAOException {
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			setClubIndependent(id, isIndepended, con);
			return findClubById(con, id);
		} catch (SQLException e) {
			log.error("Can not get all clubs.", e);
			throw new DAOException();
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
	}

	private void setClubIndependent(long id, boolean isIndependent,
			Connection con) throws SQLException {
		PreparedStatement pstmt = null;
		pstmt = con.prepareStatement(SQL__CLUB_INDEPENDENT);
		pstmt.setBoolean(1, isIndependent);
		pstmt.setLong(2, id);
		pstmt.executeUpdate();
		MSsqlDAOFactory.closeStatement(pstmt);
	}

	@Override
	public Club importClub(Club club) throws DAOException {
		Connection con = null;
		Club result = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			long id = importClub(club, con);
			result = findClubById(con, id);
		} catch (SQLException e) {
			log.error("Can not delete club.", e);
			throw new DAOException("Ошибка при удалении клуба", e.getCause());
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		return result;
	}

	private long importClub(Club club, Connection con) throws SQLException {
		PreparedStatement pstmt = null;
		pstmt = con.prepareStatement(SQL__IMPORT_CLUB,
				PreparedStatement.RETURN_GENERATED_KEYS);
		pstmt.setString(1, club.getTitle());
		pstmt.setBoolean(2, false);
		pstmt.setBoolean(3, false);
		pstmt.executeUpdate();
		ResultSet rs = pstmt.getGeneratedKeys();
		rs.next();
		long id = rs.getLong(1);
		pstmt = con.prepareStatement(SQL__SET_COMPLIANCE);
		pstmt.setLong(1, club.getClubId());
		pstmt.setLong(2, id);
		pstmt.executeUpdate();
		MSsqlDAOFactory.closeStatement(pstmt);
		return id;
	}

	@Override
	public Club removeClub(long id) throws DAOException {
		Connection con = null;
		Club result = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			removeClub(id, con);
			result = findClubById(con, id);
		} catch (SQLException e) {
			log.error("Can not delete club.", e);
			throw new DAOException("Ошибка при удалении клуба", e.getCause());
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		return result;
	}

	private void removeClub(long id, Connection con) throws SQLException {
		PreparedStatement pstmt = null;
		pstmt = con.prepareStatement(SQL__DELETE_CLUB);
		pstmt.setBoolean(1, true);
		pstmt.setLong(2, id);
		pstmt.executeUpdate();
		pstmt = con.prepareStatement(SQL__DELETE_COMPLIANCE);
		pstmt.setLong(1, id);
		pstmt.executeUpdate();
		MSsqlDAOFactory.closeStatement(pstmt);
	}

}
