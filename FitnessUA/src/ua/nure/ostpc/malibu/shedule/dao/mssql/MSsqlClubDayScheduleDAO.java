package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.ClubDayScheduleDAO;
import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.entity.ClubDaySchedule;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;

public class MSsqlClubDayScheduleDAO implements ClubDayScheduleDAO {
	private static final Logger log = Logger
			.getLogger(MSsqlClubDayScheduleDAO.class);

	private static final String SQL__GET_CLUB_DAY_SCHEDULES_BY_DATE = "SELECT * FROM ScheduleClubDay WHERE Date=?;";

	private MSsqlClubDAO cludDAO = (MSsqlClubDAO) DAOFactory.getDAOFactory(
			DAOFactory.MSSQL).getClubDAO();
	private MSsqlShiftDAO shiftDAO = (MSsqlShiftDAO) DAOFactory.getDAOFactory(
			DAOFactory.MSSQL).getShiftDAO();

	@Override
	public List<ClubDaySchedule> getClubDaySchedulesByDate(Date date) {
		Connection con = null;
		List<ClubDaySchedule> clubDaySchedules = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			clubDaySchedules = getClubDaySchedulesByDate(con, date);
		} catch (SQLException e) {
			log.error("Can not get club day schedules by date.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return clubDaySchedules;
	}

	public List<ClubDaySchedule> getClubDaySchedulesByDate(Connection con,
			Date date) throws SQLException {
		PreparedStatement pstmt = null;
		List<ClubDaySchedule> clubDaySchedules = null;
		try {
			pstmt = con.prepareStatement(SQL__GET_CLUB_DAY_SCHEDULES_BY_DATE);
			pstmt.setDate(1, date);
			ResultSet rs = pstmt.executeQuery();
			if (rs.isBeforeFirst()) {
				clubDaySchedules = new ArrayList<ClubDaySchedule>();
			}
			while (rs.next()) {
				ClubDaySchedule clubDaySchedule = unMapClubDaySchedule(rs);
				long clubId = rs
						.getLong(MapperParameters.CLUB_DAY_SCHEDULE__CLUB_ID);
				clubDaySchedule.setClub(cludDAO.findClubById(con, clubId));
				clubDaySchedule.setShifts(shiftDAO
						.getShiftsByScheduleClubDayId(con,
								clubDaySchedule.getClubDayScheduleId()));
				clubDaySchedules.add(clubDaySchedule);
			}
			return clubDaySchedules;
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

	private ClubDaySchedule unMapClubDaySchedule(ResultSet rs)
			throws SQLException {
		ClubDaySchedule clubDaySchedule = new ClubDaySchedule();
		clubDaySchedule.setClubDayScheduleId(rs
				.getLong(MapperParameters.CLUB_DAY_SCHEDULE__ID));
		clubDaySchedule.setDate(rs
				.getDate(MapperParameters.CLUB_DAY_SCHEDULE__DATE));
		clubDaySchedule
				.setSchedulePeriodId(rs
						.getLong(MapperParameters.CLUB_DAY_SCHEDULE__SCHEDULE_PERIOD_ID));
		clubDaySchedule.setShiftsNumber(rs
				.getInt(MapperParameters.CLUB_DAY_SCHEDULE__SHIFTS_NUMBER));
		clubDaySchedule.setWorkHoursInDay(rs
				.getInt(MapperParameters.CLUB_DAY_WORK_HOURS_IN_DAY));
		return clubDaySchedule;
	}
}
