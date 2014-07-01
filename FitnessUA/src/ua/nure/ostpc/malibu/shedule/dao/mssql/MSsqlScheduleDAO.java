package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.dao.mapper.PeriodMapper;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;

public class MSsqlScheduleDAO implements ScheduleDAO {
	private static final Logger log = Logger.getLogger(MSsqlScheduleDAO.class);
	private static final String SQL__READ_PERIOD = "SELECT * FROM SchedulePeriod WHERE startDate<=? AND endDate>=?";

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
				period = PeriodMapper.unMapPeriod(rs);
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
	public Schedule readSchedule(Period period) {
		// TODO Auto-generated method stub
		return null;
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

}
