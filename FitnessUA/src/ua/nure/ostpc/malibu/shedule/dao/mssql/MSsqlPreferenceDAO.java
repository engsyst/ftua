package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.PreferenceDAO;
import ua.nure.ostpc.malibu.shedule.entity.Preference;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;

public class MSsqlPreferenceDAO implements PreferenceDAO {
	private static final Logger log = Logger
			.getLogger(MSsqlPreferenceDAO.class);

	private static final String SQL__GET_LAST_PREFERENCE = "SELECT * FROM Prefs WHERE PrefId IN (SELECT MAX(PrefId) FROM Prefs);";
	private static final String SQL__UPDATE_PREFERENCE = "UPDATE Prefs SET ShiftsNumber = ?, WorkHoursInDay = ? WHERE PrefId = ?;";
	private static final String SQL__INSERT_PREFERENCE = "INSERT INTO Prefs (ShiftsNumber, WorkHoursInDay) VALUES (?, ?)";

	@Override
	public Preference getLastPreference() {
		Connection con = null;
		Preference preference = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			preference = getLastPreference(con);
		} catch (SQLException e) {
			log.error("Can not get last preference.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return preference;
	}

	private Preference getLastPreference(Connection con) throws SQLException {
		Statement stmt = null;
		Preference preference = null;
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__GET_LAST_PREFERENCE);
			if (rs.next()) {
				preference = unMapPreference(rs);
			}
			return preference;
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

	public boolean updatePreference(int hours, int shifts) {
		Connection con = null;
		Preference pref = null;
		boolean updateResult = false;
		try {
			pref = getLastPreference();
			con = MSsqlDAOFactory.getConnection();
			if (pref == null) {
				pref = new Preference();
				pref.setPreferenceId(1);
				pref.setShiftsNumber(shifts);
				pref.setWorkHoursInDay(hours);
				updateResult = insertPref(pref, con);
			} else {
				pref.setShiftsNumber(shifts);
				pref.setWorkHoursInDay(hours);
				updateResult = updatePreference(con, pref);
			}
		} catch (SQLException e) {
			log.error("Can not update pref.", e);
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

	private boolean updatePreference(Connection con, Preference pf)
			throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__UPDATE_PREFERENCE);
			mapPreference(pf, pstmt);
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

	private boolean insertPref(Preference pf, Connection con)
			throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_PREFERENCE);
			mapPreferenceForInsert(pf, pstmt);
			result = pstmt.executeBatch().length == 1;
			con.commit();
		} catch (SQLException e) {
			throw e;
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
		return preference;
	}

	private void mapPreferenceForInsert(Preference pf, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setLong(1, pf.getShiftsNumber());
		pstmt.setLong(2, pf.getWorkHoursInDay());

	}
	private void mapPreference(Preference pf, PreparedStatement pstmt)
			throws SQLException{
		mapPreferenceForInsert(pf, pstmt);
		pstmt.setLong(3, pf.getPreferenceId());
	}
}
