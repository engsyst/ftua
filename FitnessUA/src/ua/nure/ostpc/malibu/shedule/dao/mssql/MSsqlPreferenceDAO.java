package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
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

	private Preference unMapPreference(ResultSet rs) throws SQLException {
		Preference preference = new Preference();
		preference.setPreferenceId(rs.getLong(MapperParameters.PREFERENCE__ID));
		preference.setShiftsNumber(rs
				.getInt(MapperParameters.PREFERENCE__SHIFTS_NUMBER));
		preference.setWorkHoursInDay(rs
				.getInt(MapperParameters.PREFERENCE__WORK_HOURS_IN_DAY));
		return preference;
	}
}
