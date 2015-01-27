package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.PreferenceDAO;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Preference;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;

public class MSsqlPreferenceDAO implements PreferenceDAO {
	private static final Logger log = Logger
			.getLogger(MSsqlPreferenceDAO.class);

	private static final String SQL__GET_LAST_PREFERENCE = "SELECT * FROM Prefs "
			+ "WHERE PrefId IN (SELECT MAX(PrefId) FROM Prefs);";
	private static final String SQL__UPDATE_PREFERENCE = "UPDATE Prefs SET "
			+ "ShiftsNumber = ?, WorkHoursInDay = ?, WorkHoursInWeek = ?, WorkContinusHours = ?, GenerateMode = ? WHERE PrefId = ?;";

	@Override
	public Preference getLastPreference() {
		Connection con = null;
		Preference pref = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			pref = getLastPreference(con);
		} catch (SQLException e) {
			log.error("Can not get last preference.", e);
		} 
		MSsqlDAOFactory.commitAndClose(con);
		return pref;
	}

	private Preference getLastPreference(Connection con) throws SQLException {
		Statement stmt = null;
		Preference pref = null;
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__GET_LAST_PREFERENCE);
			if (rs.next()) {
				pref = unMapPreference(rs);
			} else {
				throw new IllegalStateException("Preferances not found");
			}
			return pref;
		} catch (SQLException e) {
			log.error("Can not getLastPreference.", e);
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

	public boolean updatePreference(Preference pf) {
		if (pf == null) 
			throw new IllegalArgumentException("Preference can not be a null");
		Connection con = null;
		Preference pref = null;
		boolean updateResult = false;
		try {
			con = MSsqlDAOFactory.getConnection();
			pref = getLastPreference(con);
			pf.setPreferenceId(pref.getPreferenceId());
			updateResult = updatePreference(con, pf);
		} catch (SQLException e) {
			log.error("Can not update pref.", e);
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		return updateResult;
	}

	private boolean updatePreference(Connection con, Preference pf)
			throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__UPDATE_PREFERENCE);
			mapPreference(pf, pstmt);
			int updatedRows = pstmt.executeUpdate();
			result = updatedRows != 0;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					log.error("updatePreference: Can not close statement", e);
				}
			}
		}
		return result;
	}

	private Preference unMapPreference(ResultSet rs) throws SQLException {
		Preference preference = new Preference();
		preference.setPreferenceId(rs.getLong(MapperParameters.PREFERENCE__ID));
		preference.setShiftsNumber(rs
				.getInt(MapperParameters.PREFERENCE__SHIFTS_NUMBER));
		preference.setWorkHoursInDay(rs
				.getInt(MapperParameters.PREFERENCE__WORK_HOURS_IN_DAY));
		preference.setWorkHoursInWeek(rs
				.getInt(MapperParameters.PREFERENCE__WORK_HOURS_IN_WEEK));
		preference.setWorkContinusHours(rs
				.getInt(MapperParameters.PREFERENCE__WORK_CONTINUS_HOURS));
		preference.setMode(rs
				.getInt(MapperParameters.PREFERENCE__GENERATE_MODE));
		return preference;
	}

	private void mapPreference(Preference pf, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setInt(1, pf.getShiftsNumber());
		pstmt.setInt(2, pf.getWorkHoursInDay());
		pstmt.setInt(3, pf.getWorkHoursInWeek());
		pstmt.setInt(4, pf.getWorkContinusHours());
		pstmt.setInt(5, pf.getMode());
		pstmt.setLong(6, pf.getPreferenceId());
	}
}
