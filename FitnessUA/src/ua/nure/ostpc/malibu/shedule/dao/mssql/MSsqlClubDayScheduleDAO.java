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
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Shift;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;

public class MSsqlClubDayScheduleDAO implements ClubDayScheduleDAO {
	private static final Logger log = Logger
			.getLogger(MSsqlClubDayScheduleDAO.class);

	private static final String SQL__GET_CLUB_DAY_SCHEDULES_BY_DATE_AND_PERIOD_ID = "SELECT * FROM ScheduleClubDay WHERE Date=? AND SchedulePeriodId=?;";
	private static final String SQL__GET_CLUB_DAY_SCHEDULE_BY_ID = "SELECT * FROM ScheduleClubDay WHERE ScheduleClubDayId=?;";
	private static final String SQL__CONTAINS_CLUB_DAY_SCHEDULE_WITH_ID = "SELECT * FROM ScheduleClubDay WHERE ScheduleClubDayId=?;";
	private static final String SQL__INSERT_CLUB_DAY_SCHEDULE = "INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES(?, ?, ?, ?, ?);";
	private static final String SQL__UPDATE_CLUB_DAY_SCHEDULE = "UPDATE ScheduleClubDay SET Date=?, SchedulePeriodId=?, ClubId=?, ShiftsNumber=?, WorkHoursInDay=? WHERE ScheduleClubDayId=?;";
	private static final String SQL__REMOVE_CLUB_DAY_SCHEDULE = "DELETE FROM ScheduleClubDay WHERE ScheduleClubDayId=?;";

	private MSsqlClubDAO cludDAO = (MSsqlClubDAO) DAOFactory.getDAOFactory(
			DAOFactory.MSSQL).getClubDAO();
	private MSsqlShiftDAO shiftDAO = (MSsqlShiftDAO) DAOFactory.getDAOFactory(
			DAOFactory.MSSQL).getShiftDAO();

	@Override
	public List<ClubDaySchedule> getClubDaySchedulesByDateAndPeriodId(
			Date date, long periodId) {
		Connection con = null;
		List<ClubDaySchedule> clubDaySchedules = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			clubDaySchedules = getClubDaySchedulesByDateAndPeriodId(con, date,
					periodId);
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

	public List<ClubDaySchedule> getClubDaySchedulesByDateAndPeriodId(
			Connection con, Date date, long periodId) throws SQLException {
		PreparedStatement pstmt = null;
		List<ClubDaySchedule> clubDaySchedules = null;
		try {
			pstmt = con
					.prepareStatement(SQL__GET_CLUB_DAY_SCHEDULES_BY_DATE_AND_PERIOD_ID);
			pstmt.setDate(1, date);
			pstmt.setLong(2, periodId);
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

	@Override
	public ClubDaySchedule getClubDaySchedule(long clubDayScheduleId) {
		Connection con = null;
		ClubDaySchedule clubDaySchedule = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			clubDaySchedule = getClubDaySchedule(con, clubDayScheduleId);
		} catch (SQLException e) {
			log.error("Can not get club day schedule by id.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return clubDaySchedule;
	}

	private ClubDaySchedule getClubDaySchedule(Connection con,
			long clubDayScheduleId) throws SQLException {
		PreparedStatement pstmt = null;
		ClubDaySchedule clubDaySchedule = null;
		try {
			pstmt = con.prepareStatement(SQL__GET_CLUB_DAY_SCHEDULE_BY_ID);
			pstmt.setLong(1, clubDayScheduleId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				clubDaySchedule = unMapClubDaySchedule(rs);
			}
			return clubDaySchedule;
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

	@Override
	public boolean containsClubDaySchedule(long clubDayScheduleId) {
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			return containsClubDaySchedule(con, clubDayScheduleId);
		} catch (SQLException e) {
			log.error("Can not check club day schedule containing.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return false;
	}

	private boolean containsClubDaySchedule(Connection con,
			long clubDayScheduleId) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con
					.prepareStatement(SQL__CONTAINS_CLUB_DAY_SCHEDULE_WITH_ID);
			pstmt.setLong(1, clubDayScheduleId);
			ResultSet rs = pstmt.executeQuery();
			return rs.isBeforeFirst();
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

	@Override
	public boolean insertClubDaySchedule(ClubDaySchedule clubDaySchedule) {
		Connection con = null;
		boolean result = false;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = insertClubDaySchedule(con, clubDaySchedule);
			con.commit();
		} catch (SQLException e) {
			log.error("Can not insert club day schedule.", e);
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

	private boolean insertClubDaySchedule(Connection con,
			ClubDaySchedule clubDaySchedule) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_CLUB_DAY_SCHEDULE);
			mapClubDayScheduleForInsert(clubDaySchedule, pstmt);
			return pstmt.executeUpdate() != 0;
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

	@Override
	public boolean updateClubDaySchedule(ClubDaySchedule clubDaySchedule) {
		Connection con = null;
		boolean result = false;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = updateClubDaySchedule(con, clubDaySchedule);
			con.commit();
		} catch (SQLException e) {
			log.error("Can not update club day schedule.", e);
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

	private boolean updateClubDaySchedule(Connection con,
			ClubDaySchedule clubDaySchedule) throws SQLException {
		PreparedStatement pstmt = null;
		boolean result = true;
		try {
			pstmt = con.prepareStatement(SQL__UPDATE_CLUB_DAY_SCHEDULE);
			mapClubDayScheduleForUpdate(clubDaySchedule, pstmt);
			pstmt.executeUpdate();
			ClubDaySchedule oldClubDaySchedule = getClubDaySchedule(clubDaySchedule
					.getClubDayScheduleId());
			List<Shift> oldShifts = oldClubDaySchedule.getShifts();
			List<Shift> newShifts = clubDaySchedule.getShifts();
			for (Shift newShift : newShifts) {
				if (oldShifts.contains(newShift)) {
					shiftDAO.updateShift(newShift);
					oldShifts.remove(newShift);
				}
			}
			for (Shift oldShift : oldShifts) {

			}
			for (Shift newShift : newShifts) {

			}
		} catch (SQLException e) {
			result = false;
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
		return result;
	}

	@Override
	public boolean removeClubDaySchedule(ClubDaySchedule clubDaySchedule) {
		Connection con = null;
		boolean result = false;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = removeClubDaySchedule(con, clubDaySchedule);
			con.commit();
		} catch (SQLException e) {
			log.error("Can not remove club day schedule.", e);
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

	private boolean removeClubDaySchedule(Connection con,
			ClubDaySchedule clubDaySchedule) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__REMOVE_CLUB_DAY_SCHEDULE);
			pstmt.setLong(1, clubDaySchedule.getClubDayScheduleId());
			return pstmt.executeUpdate() != 0;
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

	private void mapClubDayScheduleForInsert(ClubDaySchedule clubDaySchedule,
			PreparedStatement pstmt) throws SQLException {
		pstmt.setDate(1, new Date(clubDaySchedule.getDate().getTime()));
		pstmt.setLong(2, clubDaySchedule.getSchedulePeriodId());
		pstmt.setLong(3, clubDaySchedule.getClub().getClubId());
		pstmt.setInt(4, clubDaySchedule.getShiftsNumber());
		pstmt.setInt(5, clubDaySchedule.getWorkHoursInDay());
	}

	private void mapClubDayScheduleForUpdate(ClubDaySchedule clubDaySchedule,
			PreparedStatement pstmt) throws SQLException {
		mapClubDayScheduleForInsert(clubDaySchedule, pstmt);
		pstmt.setLong(6, clubDaySchedule.getClubDayScheduleId());
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
