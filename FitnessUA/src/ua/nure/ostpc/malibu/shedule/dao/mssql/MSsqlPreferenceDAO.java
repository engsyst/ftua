package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.DAOException;
import ua.nure.ostpc.malibu.shedule.dao.PreferenceDAO;
import ua.nure.ostpc.malibu.shedule.entity.Holiday;
import ua.nure.ostpc.malibu.shedule.entity.Preference;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;

public class MSsqlPreferenceDAO implements PreferenceDAO {
	private static final Logger log = Logger
			.getLogger(MSsqlPreferenceDAO.class);

	private static final String SQL__GET_LAST_PREFERENCE = "SELECT * FROM Prefs "
			+ "WHERE PrefId IN (SELECT MAX(PrefId) FROM Prefs);";
	private static final String SQL__UPDATE_PREFERENCE = "UPDATE Prefs SET "
			+ "ShiftsNumber = ?, WorkHoursInDay = ?, WorkHoursInWeek = ?, WorkContinusHours = ?, GenerateMode = ?, Weekends = ? WHERE PrefId = ?;";

	private static final String SQL__INSERT_HOLIDAY = "INSERT INTO Holidays (Date, Repeate) VALUES (?,?);";
	private static final String SQL__GET_HOLIDAYS = "SELECT * from Holidays ORDER BY Date ASC;";
	private static final String SQL__DELETE_HOLIDAY = "DELETE FROM Holidays WHERE HolidayId=?;";
	private static final String SQL__GET_HOLIDAY_BY_ID = "SELECT * FROM Holidays WHERE HolidayId=?;";
	private static final String SQL__GET_WEEKENDS = "SELECT TOP 1 Weekends FROM Prefs";

	private static final String SQL__UPDATE_WEEKENDS = "UPDATE Prefs SET Weekends = ? WHERE PrefId = (SELECT TOP 1 PrefId FROM Prefs)";

	@Override
	public Preference getLastPreference() {
		Connection con = null;
		Preference pref = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			pref = getLastPreference(con);
			pref.getHolidays().clear();
			pref.getHolidays().addAll(getHolidays(con));
		} catch (SQLException e) {
			log.error("Can not get last preference.", e);
		}
		MSsqlDAOFactory.close(con);
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
			pref.getHolidays().addAll(getHolidays(con));
			return pref;
		} catch (SQLException e) {
			log.error("Can not getLastPreference.", e);
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(stmt);
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
			MSsqlDAOFactory.closeStatement(pstmt);
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
		preference
				.setWeekends(rs.getInt(MapperParameters.PREFERENCE__WEEKENDS));
		return preference;
	}

	private void mapPreference(Preference pf, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setInt(1, pf.getShiftsNumber());
		pstmt.setInt(2, pf.getWorkHoursInDay());
		pstmt.setInt(3, pf.getWorkHoursInWeek());
		pstmt.setInt(4, pf.getWorkContinusHours());
		pstmt.setInt(5, pf.getMode());
		pstmt.setInt(6, pf.getWeekendsAsInt());
		pstmt.setLong(7, pf.getPreferenceId());
	}

	@Override
	public long insertHoliday(Holiday holiday) throws DAOException {
		long holidayId = 0;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			holidayId = insertHoliday(con, holiday);
		} catch (SQLException e) {
			log.error("Can not insert holiday.", e);
			MSsqlDAOFactory.roolback(con);
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		return holidayId;
	}

	private long insertHoliday(Connection con, Holiday holiday)
			throws SQLException {
		long holidayId = 0;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_HOLIDAY,
					Statement.RETURN_GENERATED_KEYS);
			mapHolidayForInsert(holiday, pstmt);
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				holidayId = rs.getLong(1);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
		return holidayId;
	}

	@Override
	public Boolean insertHolidays(Collection<Holiday> holidays) {
		Boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = insertHolidays(holidays, con);
		} catch (SQLException e) {
			log.error("Can not insert holidays.", e);
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		return result;
	}

	private boolean insertHolidays(Collection<Holiday> holidays, Connection con)
			throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_HOLIDAY);
			for (Holiday h : holidays) {
				mapHolidayForInsert(h, pstmt);
				pstmt.addBatch();
			}
			result = pstmt.executeBatch().length == holidays.size();
		} catch (SQLException e) {
			log.error("Can not insertHolidays", e);
			throw e;
		}
		return result;
	}

	@Override
	public Collection<Holiday> getHolidays() {
		Connection con = null;
		Collection<Holiday> ourHolidays = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			ourHolidays = getHolidays(con);
		} catch (SQLException e) {
			log.error("Can not get holidays.", e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
		return ourHolidays;
	}

	private Collection<Holiday> getHolidays(Connection con) throws SQLException {
		Statement stmt = null;
		Collection<Holiday> holidays = new ArrayList<Holiday>();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__GET_HOLIDAYS);
			while (rs.next()) {
				Holiday h = unMapHoliday(rs);
				holidays.add(h);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(stmt);
		}
		return holidays;
	}

	@Override
	public Holiday getHolidayById(long holidayId) {
		Connection con = null;
		Holiday holiday = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			holiday = getHolidayById(con, holidayId);
		} catch (SQLException e) {
			log.error("Can not get holiday by id.", e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
		return holiday;
	}

	private Holiday getHolidayById(Connection con, long holidayId)
			throws SQLException {
		PreparedStatement pstmt = null;
		Holiday holiday = null;
		try {
			pstmt = con.prepareStatement(SQL__GET_HOLIDAY_BY_ID);
			pstmt.setLong(1, holidayId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				holiday = unMapHoliday(rs);
			}
			return holiday;
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
	}

	@Override
	public boolean deleteHoliday(long holidayId) throws DAOException {
		boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = deleteHoliday(con, holidayId);
		} catch (SQLException e) {
			log.error("Can not delete holiday.", e);
			MSsqlDAOFactory.roolback(con);
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		return result;
	}

	private boolean deleteHoliday(Connection con, long holidayId)
			throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__DELETE_HOLIDAY);
			pstmt.setLong(1, holidayId);
			result = pstmt.executeUpdate() == 1;
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
		return result;
	}

	private void mapHolidayForInsert(Holiday holiday, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setDate(1, new Date(holiday.getDate().getTime()));
		pstmt.setInt(2, holiday.getRepeate());
	}

	private Holiday unMapHoliday(ResultSet rs) throws SQLException {
		Holiday holiday = new Holiday();
		holiday.setHolidayId(rs.getLong(MapperParameters.HOLIDAY__ID));
		holiday.setDate(rs.getDate(MapperParameters.HOLIDAY__DATE));
		holiday.setRepeate(rs.getInt(MapperParameters.HOLIDAY__REPEATE));
		return holiday;
	}

	@Override
	public int getWeekends() throws DAOException {
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			return getWeekends(con);
		} catch (SQLException e) {
			log.error("Can not get weekends.", e);
			throw new DAOException("Can not get weekends.", e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
	}

	private int getWeekends(Connection con) throws SQLException {
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__GET_WEEKENDS);
			rs.next();
			return (int) rs.getLong("Weekends");
		} finally {
			MSsqlDAOFactory.closeStatement(stmt);
		}
	}

	@Override
	public void updateWeekends(int weekends) throws DAOException {
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			updateWeekends(con, weekends);
			con.commit();
		} catch (SQLException e) {
			log.error("Can not update weekends.", e);
			MSsqlDAOFactory.roolback(con);
			throw new DAOException("Can not update weekends.", e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
	}

	private void updateWeekends(Connection con, int weekends)
			throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__UPDATE_WEEKENDS);
			pstmt.setLong(1, weekends);
			pstmt.executeUpdate();
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
	}

}
