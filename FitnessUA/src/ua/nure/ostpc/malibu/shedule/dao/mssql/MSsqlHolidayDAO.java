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
	private static final Logger log = Logger.getLogger(MSsqlClubDAO.class);

	private static final String SQL__INSERT_HOLIDAY = "INSERT INTO Holidays (HolidayId, Date) VALUES (?, ?);";

	private static final String SQL__FIND_OUR_HOLIDAYS = "SELECT * from Holidays;";

	private static final String SQL__DELETE_HOLIDAY = "DELETE FROM Holidays WHERE HolidayId=?";

	@Override
	public Boolean insertHolidays(Collection<Holiday> holidays) {
		Boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = insertHolidays(holidays, con);
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
			con.commit();
		} catch (SQLException e) {
			throw e;
		}
		return result;
	}

	public Collection<Holiday> getOurHolidays() {
		Connection con = null;
		Collection<Holiday> ourHolidays = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			ourHolidays = getOurHolidays(con);
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
		return ourHolidays;
	}

	private Collection<Holiday> getOurHolidays(Connection con)
			throws SQLException {
		Statement stmt = null;
		Collection<Holiday> holidays = new ArrayList<Holiday>();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__FIND_OUR_HOLIDAYS);
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

	public Boolean deleteHoliday(Long id) {
		Connection con = null;
		Boolean result = false;
		try {
			con = MSsqlDAOFactory.getConnection();
			deleteHoliday(id, con);
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

	private void deleteHoliday(long id, Connection con) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__DELETE_HOLIDAY);
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

	private void mapHolidayForInsert(Holiday h, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setLong(1, h.getHolidayid());
		pstmt.setDate(2, new Date(h.getDate().getTime()));
	}

	private Holiday unMapHoliday(ResultSet rs) throws SQLException {
		Holiday h = new Holiday();
		h.setHolidayid(rs.getLong(MapperParameters.HOLIDAY__ID));
		h.setDate(rs.getDate(MapperParameters.HOLIDAY__DATE));
		return h;
	}
}
