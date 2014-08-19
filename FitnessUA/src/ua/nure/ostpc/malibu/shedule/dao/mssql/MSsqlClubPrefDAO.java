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

	private static final String SQL__GET_CLUB_PREFS_BY_PERIOD_ID = "SELECT * FROM [FitnessUA].[dbo].[ClubPrefs] WHERE SchedulePeriodId=? Order By [ClubId]";

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
