package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.ClubPrefDAO;
import ua.nure.ostpc.malibu.shedule.entity.ClubPref;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;

public class MSsqlClubPrefDAO implements ClubPrefDAO {
	private static final Logger log = Logger.getLogger(MSsqlClubPrefDAO.class);

	private static final String SQL__GET_CLUB_PREFS_BY_PERIOD_ID = "SELECT * FROM ClubPrefs WHERE SchedulePeriodId=? Order By [ClubId]";
	private static final String SQL__CONTAINS_CLUB_PREF_WITH_ID = "SELECT * FROM ClubPrefs WHERE ClubPrefsId=?;";
	private static final String SQL__INSERT_CLUB_PREF = "INSERT INTO ClubPrefs(ClubId, SchedulePeriodId, EmployeeId) VALUES(?, ?, ?);";
	private static final String SQL__UPDATE_CLUB_PREF = "UPDATE ClubPrefs SET ClubId=?, SchedulePeriodId=?, EmployeeId=? WHERE ClubPrefsId=?;";
	private static final String SQL__DELETE_CLUB_PREF = "DELETE FROM ClubPrefs WHERE ClubPrefsId=?;";

	@Override
	public List<ClubPref> getClubPrefsByPeriodId(long periodId) {
		Connection con = null;
		List<ClubPref> clubPrefs = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			clubPrefs = getClubPrefsByPeriodId(con, periodId);
		} catch (SQLException e) {
			log.error("Can not get club preferences by period id.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return clubPrefs;
	}

	public List<ClubPref> getClubPrefsByPeriodId(Connection con, long periodId)
			throws SQLException {
		PreparedStatement pstmt = null;
		List<ClubPref> clubPrefs = null;
		try {
			pstmt = con.prepareStatement(SQL__GET_CLUB_PREFS_BY_PERIOD_ID);
			pstmt.setLong(1, periodId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.isBeforeFirst()) {
				clubPrefs = new ArrayList<ClubPref>();
			}
			while (rs.next()) {
				ClubPref clubPref = unMapClubPref(rs);
				clubPrefs.add(clubPref);
			}
			return clubPrefs;
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

	@Override
	public boolean containsClubPref(long clubPrefId) {
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			return containsClubPref(con, clubPrefId);
		} catch (SQLException e) {
			log.error("Can not check club preference containing.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return false;
	}

	private boolean containsClubPref(Connection con, long clubPrefId)
			throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__CONTAINS_CLUB_PREF_WITH_ID);
			pstmt.setLong(1, clubPrefId);
			ResultSet rs = pstmt.executeQuery();
			return rs.isBeforeFirst();
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

	@Override
	public boolean insertClubPref(ClubPref clubPref) {
		Connection con = null;
		boolean result = false;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = insertClubPref(con, clubPref);
			con.commit();
		} catch (SQLException e) {
			log.error("Can not insert club preference.", e);
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

	public boolean insertClubPref(Connection con, ClubPref clubPref)
			throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_CLUB_PREF);
			mapClubPrefForInsert(clubPref, pstmt);
			return pstmt.executeUpdate() != 0;
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

	@Override
	public boolean updateClubPref(ClubPref clubPref) {
		Connection con = null;
		boolean result = false;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = updateClubPref(con, clubPref);
			con.commit();
		} catch (SQLException e) {
			log.error("Can not update club preference.", e);
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

	public boolean updateClubPref(Connection con, ClubPref clubPref)
			throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__UPDATE_CLUB_PREF);
			mapClubPrefForUpdate(clubPref, pstmt);
			return pstmt.executeUpdate() != 0;
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

	@Override
	public boolean removeClubPref(ClubPref clubPref) {
		Connection con = null;
		boolean result = false;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = removeClubPref(con, clubPref);
			con.commit();
		} catch (SQLException e) {
			log.error("Can not remove club preference.", e);
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

	public boolean removeClubPref(Connection con, ClubPref clubPref)
			throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__DELETE_CLUB_PREF);
			pstmt.setLong(1, clubPref.getClubPrefId());
			return pstmt.executeUpdate() != 0;
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

	private void mapClubPrefForInsert(ClubPref clubPref, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setLong(1, clubPref.getClubId());
		pstmt.setLong(2, clubPref.getSchedulePeriodId());
		pstmt.setLong(3, clubPref.getEmployeeId());
	}

	private void mapClubPrefForUpdate(ClubPref clubPref, PreparedStatement pstmt)
			throws SQLException {
		mapClubPrefForInsert(clubPref, pstmt);
		pstmt.setLong(4, clubPref.getClubPrefId());
	}

	private ClubPref unMapClubPref(ResultSet rs) throws SQLException {
		ClubPref clubPref = new ClubPref();
		clubPref.setClubPrefId(rs.getLong(MapperParameters.CLUB_PREF__ID));
		clubPref.setClubId(rs.getLong(MapperParameters.CLUB_PREF__CLUB_ID));
		clubPref.setSchedulePeriodId(rs
				.getLong(MapperParameters.CLUB_PREF__SCHEDULE_PERIOD_ID));
		clubPref.setEmployeeId(rs
				.getLong(MapperParameters.CLUB_PREF__EMPLOYEE_ID));
		return clubPref;
	}
}
