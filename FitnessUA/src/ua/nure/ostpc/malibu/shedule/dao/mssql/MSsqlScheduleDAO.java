package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.dao.mapper.MapperParameters;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;

public class MSsqlScheduleDAO implements ScheduleDAO {
	private static final Logger log = Logger.getLogger(MSsqlScheduleDAO.class);
	private static final String SQL__READ_PERIOD = "SELECT * FROM SchedulePeriod WHERE startDate<=? AND endDate>=?";
	private static final String SQL__READ_SCHEDULE = "SELECT * FROM DaySchedule WHERE schedule_period_id=?";

	@Override
	public Period readPeriod(Date date) throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		Period period = null;
		try {
			period = readPeriod(date, con);
		} catch (SQLException e) {
			log.error("Can not read Period", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection", e);
			}
		}
		return period;
	}

	private Period readPeriod(Date date, Connection con) throws SQLException {
		PreparedStatement pstmt = null;
		Period period = null;
		try {
			pstmt = con.prepareStatement(SQL__READ_PERIOD);
			pstmt.setDate(1, date);
			pstmt.setDate(2, date);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				period = unMapPeriod(rs);
			}
			return period;
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
	}

	@Override
	public Schedule readSchedule(Period period) throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		Schedule schedule = null;
		try {
			schedule = readSchedule(period, con);
		} catch (SQLException e) {
			log.error("Can not read Schedule", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection", e);
			}
		}
		return schedule;
	}

	private Schedule readSchedule(Period period, Connection con)
			throws SQLException {
		PreparedStatement pstmt = null;
		Schedule schedule = null;
		try {
			pstmt = con.prepareStatement(SQL__READ_SCHEDULE);
			pstmt.setLong(1, period.getPeriod_Id());
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				schedule = unMapShedule(rs);
				schedule.setPeriod(period);
			}
			return schedule;
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
	}

	@Override
	public Set<Schedule> readSchedules(Date start, Date end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int insertSchedule(Schedule shedule) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean updateSchedule(Schedule shedule) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private Schedule unMapShedule(ResultSet rs) throws SQLException {
		Schedule schedule = new Schedule();
		long scheduleId = rs.getLong(MapperParameters.SCHEDULE__ID);
		Date date = rs.getDate(MapperParameters.SCHEDULE__DATE);
		int halfOfDay = rs.getInt(MapperParameters.SCHEDULE__HALF_OF_DAY);
		long user_id = rs.getLong(MapperParameters.SCHEDULE__USER_ID);
		long clubId = rs.getLong(MapperParameters.SCHEDULE__CLUB_ID);
		// long periodId = rs.getLong(MapperParameters.SCHEDULE__PERIOD_ID);
		return schedule;
	}
	
	private Period unMapPeriod(ResultSet rs) throws SQLException {
		Period period = new Period(rs.getLong(MapperParameters.PERIOD__ID));
		period.setPeriod(rs.getDate(MapperParameters.PERIOD__START_DATE),
				rs.getDate(MapperParameters.PERIOD__END_DATE));
		period.setLastPriodId(rs
				.getLong(MapperParameters.PERIOD__LAST_PERIOD_ID));
		return period;
	}

}
