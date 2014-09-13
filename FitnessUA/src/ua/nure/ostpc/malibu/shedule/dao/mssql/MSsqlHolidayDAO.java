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

import ua.nure.ostpc.malibu.shedule.dao.HolidayDAO;
import ua.nure.ostpc.malibu.shedule.entity.Holiday;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;

public class MSsqlHolidayDAO implements HolidayDAO {
	private static final Logger log = Logger.getLogger(MSsqlHolidayDAO.class);

	private static final String SQL__INSERT_HOLIDAY = "INSERT INTO Holidays (Date, Repeate) VALUES (?,?);";
	private static final String SQL__GET_HOLIDAYS = "SELECT * from Holidays;";
	private static final String SQL__REMOVE_HOLIDAY = "DELETE FROM Holidays WHERE HolidayId=?;";

	@Override
	public Boolean insertHolidays(Collection<Holiday> holidays) {
		Boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = insertHolidays(holidays, con);
			con.commit();
		} catch (SQLException e) {
			log.error("Can not insert holidays.", e);
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
			throw e;
		}
		return result;
	}

	public Collection<Holiday> getHolidays() {
		Connection con = null;
		Collection<Holiday> ourHolidays = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			ourHolidays = getHolidays(con);
		} catch (SQLException e) {
			log.error("Can not get holidays.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
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
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					throw e;
				}
			}
		}
		return holidays;
	}

	public Boolean removeHoliday(Long id) {
		Connection con = null;
		Boolean result = false;
		try {
			con = MSsqlDAOFactory.getConnection();
			removeHoliday(id, con);
			con.commit();
			result = true;
		} catch (SQLException e) {
			log.error("Can not remove holiday.", e);
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

	private void removeHoliday(long id, Connection con) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__REMOVE_HOLIDAY);
			pstmt.setLong(1, id);
			pstmt.executeUpdate();
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

	private void mapHolidayForInsert(Holiday holiday, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setDate(1, new Date(holiday.getDate().getTime()));
		pstmt.setInt(2, holiday.getRepeate());
	}

	private Holiday unMapHoliday(ResultSet rs) throws SQLException {
		Holiday holiday = new Holiday();
		holiday.setHolidayid(rs.getLong(MapperParameters.HOLIDAY__ID));
		holiday.setDate(rs.getDate(MapperParameters.HOLIDAY__DATE));
		holiday.setRepeate(rs.getInt(MapperParameters.HOLIDAY__REPEATE));
		return holiday;
	}
}
