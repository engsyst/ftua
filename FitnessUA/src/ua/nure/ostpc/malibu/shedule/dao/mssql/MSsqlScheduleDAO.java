package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.entity.ClubDaySchedule;
import ua.nure.ostpc.malibu.shedule.entity.ClubPref;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;

public class MSsqlScheduleDAO implements ScheduleDAO {
	private static final Logger log = Logger.getLogger(MSsqlScheduleDAO.class);

	private static final String SQL__GET_PERIOD_BY_DATE = "SELECT * FROM SchedulePeriod WHERE StartDate<=? AND EndDate>=?";
	private static final String SQL__GET_FIRST_DRAFT_PERIOD = "SELECT TOP 1 * FROM SchedulePeriod WHERE StartDate>? AND Status=0 ORDER BY SchedulePeriod.StartDate";
	private static final String SQL__GET_PERIOD_BY_ID = "SELECT * FROM SchedulePeriod WHERE SchedulePeriodId=?";
	private static final String SQL__GET_ALL_PERIODS = "SELECT * FROM SchedulePeriod;";
	private static final String SQL__GET_ALL_SCHEDULE_STATUSES = "SELECT SchedulePeriodId, Status FROM SchedulePeriod;";
	private static final String SQL__FIND_PERIODS_BY_DATE = "SELECT * FROM SchedulePeriod WHERE StartDate>=? AND EndDate<=?;";
	private static final String SQL__FIND_NOT_CLOSED_PERIODS = "SELECT * FROM SchedulePeriod WHERE Status<>?;";
	private static final String SQL__INSERT_SCHEDULE = "INSERT INTO SchedulePeriod(StartDate, EndDate, LastPeriodId, Status) "
			+ "VALUES(?, ?, (SELECT MAX(SchedulePeriodId) FROM SchedulePeriod), ?);";
	private static final String SQL__UPDATE_SCHEDULE = "UPDATE SchedulePeriod SET LastPeriodId=?, StartDate=?, EndDate=?, Status=? "
			+ "WHERE SchedulePeriodId=?;";
	private static final String SQL__UPDATE_SCHEDULE_WITHOUT_LAST_PERIOD = "UPDATE SchedulePeriod SET StartDate=?, EndDate=?, Status=? "
			+ "WHERE SchedulePeriodId=?;";
	private static final String SQL__GET_MAX_END_DATE = "SELECT MAX(EndDate) AS EndDate FROM SchedulePeriod;";
	private static final String SQL__FIND_STATUS_BY_PEDIOD_ID = "SELECT Status FROM SchedulePeriod WHERE SchedulePeriodId=?;";
	private static final String SQL__GET_PERIOD_BY_LAST_PERIOD_ID = "SELECT * FROM SchedulePeriod WHERE LastPeriodId=?";

	private MSsqlClubDayScheduleDAO clubDayScheduleDAO = new MSsqlClubDayScheduleDAO();
	private MSsqlClubPrefDAO clubPrefDAO = new MSsqlClubPrefDAO();

	@Override
	public Period getPeriod(Date date) {
		Connection con = null;
		Period period = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try getPeriod by date: " + date);
			con = MSsqlDAOFactory.getConnection();
			period = getPeriod(con, date);
		} catch (SQLException e) {
			log.error("Can not get period by date.", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return period;
	}

	private Period getPeriod(Connection con, Date date) throws SQLException {
		PreparedStatement pstmt = null;
		Period period = null;
		try {
			pstmt = con.prepareStatement(SQL__GET_PERIOD_BY_DATE);
			pstmt.setDate(1, new java.sql.Date(date.getTime()));
			pstmt.setDate(2, new java.sql.Date(date.getTime()));
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				period = unMapPeriod(rs);
			}
			return period;
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
	public Period getFirstDraftPeriod(Date date) {
		Connection con = null;
		Period period = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try getPeriod by date: " + date);
			con = MSsqlDAOFactory.getConnection();
			period = getFirstDraftPeriod(con, date);
		} catch (SQLException e) {
			log.error("Can not get period by date.", e);
			try {
				con.rollback();
			} catch (SQLException e1) {
				log.error("Can not rollback get period by date.", e);
			}
		}
		MSsqlDAOFactory.commitAndClose(con);
		return period;
	}

	private Period getFirstDraftPeriod(Connection con, Date date)
			throws SQLException {
		PreparedStatement pstmt = null;
		Period period = null;
		try {
			pstmt = con.prepareStatement(SQL__GET_FIRST_DRAFT_PERIOD);
			pstmt.setDate(1, new java.sql.Date(date.getTime()));
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				period = unMapPeriod(rs);
			}
			return period;
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
	public Period getPeriod(long periodId) {
		Connection con = null;
		Period period = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try getPeriod by id: " + periodId);
			con = MSsqlDAOFactory.getConnection();
			period = getPeriod(con, periodId);
		} catch (SQLException e) {
			log.error("Can not getPeriod by id: " + periodId, e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return period;
	}

	private Period getPeriod(Connection con, long periodId) throws SQLException {
		PreparedStatement pstmt = null;
		Period period = null;
		try {
			pstmt = con.prepareStatement(SQL__GET_PERIOD_BY_ID);
			pstmt.setLong(1, periodId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				period = unMapPeriod(rs);
			}
			return period;
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

	public Period getLastPeriod(long periodId) {
		Connection con = null;
		Period period = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try getLastPeriod by id: " + periodId);
			con = MSsqlDAOFactory.getConnection();
			period = getLastPeriod(con, periodId);
		} catch (SQLException e) {
			log.error("Can not get period by id.", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return period;
	}

	private Period getLastPeriod(Connection con, long periodId)
			throws SQLException {
		PreparedStatement pstmt = null;
		Period period = null;
		try {
			pstmt = con.prepareStatement(SQL__GET_PERIOD_BY_LAST_PERIOD_ID);
			pstmt.setLong(1, periodId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				period = unMapPeriod(rs);
			}
			return period;
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
	public List<Period> getAllPeriods() {
		Connection con = null;
		List<Period> periodList = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try getAllPeriods");
			con = MSsqlDAOFactory.getConnection();
			periodList = getAllPeriods(con);
		} catch (SQLException e) {
			log.error("Can not get all periods.", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return periodList;
	}

	private List<Period> getAllPeriods(Connection con) throws SQLException {
		PreparedStatement pstmt = null;
		List<Period> periodList = null;
		try {
			pstmt = con.prepareStatement(SQL__GET_ALL_PERIODS);
			ResultSet rs = pstmt.executeQuery();
			if (rs.isBeforeFirst()) {
				periodList = new ArrayList<Period>();
			}
			while (rs.next()) {
				Period period = unMapPeriod(rs);
				periodList.add(period);
			}
			return periodList;
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
	public Map<Long, Status> getScheduleStatusMap() {
		Connection con = null;
		Map<Long, Status> scheduleStatusMap = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try getScheduleStatusMap");
			con = MSsqlDAOFactory.getConnection();
			scheduleStatusMap = getScheduleStatusMap(con);
		} catch (SQLException e) {
			log.error("Can not get schedule status map.", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return scheduleStatusMap;
	}

	private Map<Long, Status> getScheduleStatusMap(Connection con)
			throws SQLException {
		PreparedStatement pstmt = null;
		Map<Long, Status> scheduleStatusMap = null;
		try {
			pstmt = con.prepareStatement(SQL__GET_ALL_SCHEDULE_STATUSES);
			ResultSet rs = pstmt.executeQuery();
			if (rs.isBeforeFirst()) {
				scheduleStatusMap = new HashMap<Long, Status>();
			}
			while (rs.next()) {
				long periodId = rs.getLong(MapperParameters.PERIOD__ID);
				Status status = Status.values()[rs
						.getInt(MapperParameters.PERIOD__STATUS)];
				scheduleStatusMap.put(periodId, status);
			}
			return scheduleStatusMap;
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
	public Schedule getSchedule(long periodId) {
		Connection con = null;
		Schedule schedule = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try getSchedule by id: " + periodId);
			con = MSsqlDAOFactory.getConnection();
			schedule = getSchedule(con, periodId);
		} catch (SQLException e) {
			log.error("Can not get schedule.", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return schedule;
	}

	private Schedule getSchedule(Connection con, long periodId)
			throws SQLException {
		Schedule schedule = null;
		Period period = getPeriod(con, periodId);
		if (period != null) {
			Status status = getStatusByPeriodId(con, periodId);
			List<ClubPref> clubPrefs = clubPrefDAO.getClubPrefsByPeriodId(con,
					periodId);
			Map<Date, List<ClubDaySchedule>> dayScheduleMap = new HashMap<Date, List<ClubDaySchedule>>();
			GregorianCalendar currentDateCalendar = new GregorianCalendar();
			currentDateCalendar.setTime(period.getStartDate());
			while (currentDateCalendar.getTimeInMillis() <= period.getEndDate()
					.getTime()) {
				List<ClubDaySchedule> clubDaySchedules = clubDayScheduleDAO
						.getClubDaySchedulesByDateAndPeriodId(
								con,
								new java.sql.Date(currentDateCalendar
										.getTimeInMillis()), periodId);
				dayScheduleMap.put(
						new Date(currentDateCalendar.getTimeInMillis()),
						clubDaySchedules);
				currentDateCalendar.add(GregorianCalendar.DAY_OF_YEAR, 1);
			}
			schedule = new Schedule(period, status, dayScheduleMap, clubPrefs);
		}
		return schedule;
	}

	@Override
	public Set<Schedule> getSchedules(Date startDate, Date endDate) {
		Connection con = null;
		Set<Schedule> schedules = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try getSchedules by dates: start - " + startDate
						+ ", end - " + endDate);
			con = MSsqlDAOFactory.getConnection();
			schedules = getSchedules(con, startDate, endDate);
		} catch (SQLException e) {
			log.error("Can not get schedules.", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return schedules;
	}

	private Set<Schedule> getSchedules(Connection con, Date start, Date end)
			throws SQLException {
		PreparedStatement pstmt = null;
		Set<Schedule> schedules = null;
		try {
			pstmt = con.prepareStatement(SQL__FIND_PERIODS_BY_DATE);
			pstmt.setDate(1, new java.sql.Date(start.getTime()));
			pstmt.setDate(2, new java.sql.Date(end.getTime()));
			ResultSet rs = pstmt.executeQuery();
			if (rs.isBeforeFirst()) {
				schedules = new TreeSet<Schedule>();
			}
			while (rs.next()) {
				Period period = unMapPeriod(rs);
				Schedule schedule = getSchedule(con, period.getPeriodId());
				if (schedule != null) {
					schedules.add(schedule);
				}
			}
			return schedules;
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
	public Set<Schedule> getNotClosedSchedules() {
		Connection con = null;
		Set<Schedule> schedules = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try getNotClosedSchedules");
			con = MSsqlDAOFactory.getConnection();
			schedules = getNotClosedSchedules(con);
		} catch (SQLException e) {
			log.error("Can not get schedules.", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return schedules;
	}

	private Set<Schedule> getNotClosedSchedules(Connection con)
			throws SQLException {
		PreparedStatement pstmt = null;
		Set<Schedule> schedules = null;
		try {
			pstmt = con.prepareStatement(SQL__FIND_NOT_CLOSED_PERIODS);
			pstmt.setInt(1, Schedule.Status.CLOSED.ordinal());
			ResultSet rs = pstmt.executeQuery();
			if (rs.isBeforeFirst()) {
				schedules = new TreeSet<Schedule>();
			}
			while (rs.next()) {
				Period period = unMapPeriod(rs);
				Schedule schedule = getSchedule(con, period.getPeriodId());
				if (schedule != null) {
					schedules.add(schedule);
				}
			}
			return schedules;
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
	public long insertSchedule(Schedule schedule) {
		Connection con = null;
		long periodId = 0;
		try {
			if (log.isDebugEnabled())
				log.debug("Try insertSchedule");
			con = MSsqlDAOFactory.getConnection();
			periodId = insertSchedule(con, schedule);
			con.commit();
		} catch (SQLException e) {
			log.error("Can not insert schedule.", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return periodId;
	}

	private long insertSchedule(Connection con, Schedule schedule)
			throws SQLException {
		PreparedStatement pstmt = null;
		long periodId = 0;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_SCHEDULE,
					Statement.RETURN_GENERATED_KEYS);
			mapScheduleForInsert(schedule, pstmt);
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs != null && rs.next()) {
				periodId = rs.getLong(1);
			}
			List<ClubPref> clubPrefList = schedule.getClubPrefs();
			if (clubPrefList != null) {
				for (ClubPref clubPref : clubPrefList) {
					clubPref.setSchedulePeriodId(periodId);
					clubPrefDAO.insertClubPref(con, clubPref);
				}
			}
			Iterator<Entry<Date, List<ClubDaySchedule>>> it = schedule
					.getDayScheduleMap().entrySet().iterator();
			while (it.hasNext()) {
				Entry<Date, List<ClubDaySchedule>> entry = it.next();
				for (ClubDaySchedule clubDaySchedule : entry.getValue()) {
					clubDaySchedule.setSchedulePeriodId(periodId);
					clubDayScheduleDAO.insertClubDaySchedule(con,
							clubDaySchedule);
				}
			}
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					log.error("Can not close statement.", e);
				}
			}
		}
		return periodId;
	}

	@Override
	public boolean updateSchedule(Schedule schedule) {
		Connection con = null;
		boolean result = true;
		try {
			if (log.isDebugEnabled())
				log.debug("Try updateSchedule id: "
						+ schedule.getPeriod().getPeriodId());
			con = MSsqlDAOFactory.getConnection();
			updateSchedule(con, schedule);
			con.commit();
		} catch (SQLException e) {
			log.error("Can not update schedule.", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return result;
	}

	private boolean updateSchedule(Connection con, Schedule schedule)
			throws SQLException {
		PreparedStatement pstmt = null;
		boolean result = true;
		long periodId = schedule.getPeriod().getPeriodId();
		try {
			if (schedule.getPeriod().getLastPeriodId() != 0) {
				pstmt = con.prepareStatement(SQL__UPDATE_SCHEDULE);
				mapScheduleForUpdate(schedule, pstmt);
			} else {
				pstmt = con
						.prepareStatement(SQL__UPDATE_SCHEDULE_WITHOUT_LAST_PERIOD);
				mapScheduleForUpdateWithoutLastPeriod(schedule, pstmt);
			}
			pstmt.executeUpdate();
			Schedule oldSchedule = getSchedule(con, schedule.getPeriod()
					.getPeriodId());
			List<ClubPref> oldClubPrefs = oldSchedule.getClubPrefs();
			List<ClubPref> clubPrefs = schedule.getClubPrefs();
			if (clubPrefs != null)
				for (ClubPref clubPref : clubPrefs) {
					if (oldClubPrefs != null
							&& oldClubPrefs.contains(clubPref.getClubPrefId())) {
						clubPrefDAO.updateClubPref(con, clubPref);
						oldClubPrefs.remove(clubPref);
					} else {
						clubPref.setSchedulePeriodId(periodId);
						clubPrefDAO.insertClubPref(con, clubPref);
					}
				}
			if (oldClubPrefs != null)
				for (ClubPref oldClubPref : oldClubPrefs) {
					clubPrefDAO.removeClubPref(con, oldClubPref);
				}
			Map<Date, List<ClubDaySchedule>> dayScheduleMap = schedule
					.getDayScheduleMap();
			Map<Date, List<ClubDaySchedule>> oldDayScheduleMap = oldSchedule
					.getDayScheduleMap();
			for (Date date : dayScheduleMap.keySet()) {
				List<ClubDaySchedule> clubDayScheduleList;
				if (oldDayScheduleMap.containsKey(date)) {
					clubDayScheduleList = dayScheduleMap.get(date);
					List<ClubDaySchedule> oldClubDayScheduleList = oldDayScheduleMap
							.get(date);
					if (clubDayScheduleList != null
							&& oldClubDayScheduleList != null) {
						for (ClubDaySchedule clubDaySchedule : clubDayScheduleList) {
							if (oldClubDayScheduleList
									.contains(clubDaySchedule)) {
								clubDayScheduleDAO.updateClubDaySchedule(con,
										clubDaySchedule);
								oldClubDayScheduleList.remove(clubDaySchedule);
							} else {
								clubDaySchedule.setSchedulePeriodId(periodId);
								clubDayScheduleDAO.insertClubDaySchedule(con,
										clubDaySchedule);
							}
						}
						for (ClubDaySchedule oldClubDaySchedule : oldClubDayScheduleList) {
							clubDayScheduleDAO.removeClubDaySchedule(con,
									oldClubDaySchedule);
						}
					}
					oldDayScheduleMap.remove(date);
				} else {
					clubDayScheduleList = dayScheduleMap.get(date);
					for (ClubDaySchedule clubDaySchedule : clubDayScheduleList) {
						clubDaySchedule.setSchedulePeriodId(periodId);
						clubDayScheduleDAO.insertClubDaySchedule(con,
								clubDaySchedule);
					}
				}
			}
			for (Date date : oldDayScheduleMap.keySet()) {
				List<ClubDaySchedule> oldClubDayScheduleList = oldDayScheduleMap
						.get(date);
				if (oldClubDayScheduleList != null) {
					for (ClubDaySchedule oldClubDaySchedule : oldClubDayScheduleList) {
						clubDayScheduleDAO.insertClubDaySchedule(con,
								oldClubDaySchedule);
					}
				}
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
	public Date getMaxEndDate() {
		Connection con = null;
		Date maxEndDate = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try getMaxEndDate");
			con = MSsqlDAOFactory.getConnection();
			maxEndDate = getMaxEndDate(con);
			if (log.isDebugEnabled())
				log.debug("MaxEndDate" + maxEndDate);
		} catch (SQLException e) {
			log.error("Can not get max end date.", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return maxEndDate;
	}

	private Date getMaxEndDate(Connection con) throws SQLException {
		Statement stmt = null;
		Date maxEndDate = null;
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__GET_MAX_END_DATE);
			if (rs.next()) {
				maxEndDate = rs.getDate(MapperParameters.PERIOD__END_DATE);
			}
			return maxEndDate;
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

	@Override
	public Status getStatusByPeriodId(long periodId) {
		Connection con = null;
		Status status = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Try getStatusByPeriodId with id: " + periodId);
			con = MSsqlDAOFactory.getConnection();
			status = getStatusByPeriodId(con, periodId);
		} catch (SQLException e) {
			log.error("Can not get status.", e);
		}
		MSsqlDAOFactory.commitAndClose(con);
		return status;
	}

	private Status getStatusByPeriodId(Connection con, long periodId)
			throws SQLException {
		Status status = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__FIND_STATUS_BY_PEDIOD_ID);
			pstmt.setLong(1, periodId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				status = Status.values()[rs
						.getInt(MapperParameters.PERIOD__STATUS)];
			}
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					throw e;
				}
			}
		}
		return status;
	}

	private void mapScheduleForInsert(Schedule schedule, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setDate(1, new java.sql.Date(schedule.getPeriod().getStartDate()
				.getTime()));
		pstmt.setDate(2, new java.sql.Date(schedule.getPeriod().getEndDate()
				.getTime()));
		pstmt.setLong(3, schedule.getStatus().ordinal());
	}

	private void mapScheduleForUpdate(Schedule schedule, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setLong(1, schedule.getPeriod().getLastPeriodId());
		pstmt.setDate(2, new java.sql.Date(schedule.getPeriod().getStartDate()
				.getTime()));
		pstmt.setDate(3, new java.sql.Date(schedule.getPeriod().getEndDate()
				.getTime()));
		pstmt.setLong(4, schedule.getStatus().ordinal());
		pstmt.setLong(5, schedule.getPeriod().getPeriodId());
	}

	private void mapScheduleForUpdateWithoutLastPeriod(Schedule schedule,
			PreparedStatement pstmt) throws SQLException {
		pstmt.setDate(1, new java.sql.Date(schedule.getPeriod().getStartDate()
				.getTime()));
		pstmt.setDate(2, new java.sql.Date(schedule.getPeriod().getEndDate()
				.getTime()));
		pstmt.setLong(3, schedule.getStatus().ordinal());
		pstmt.setLong(4, schedule.getPeriod().getPeriodId());
	}

	private Period unMapPeriod(ResultSet rs) throws SQLException {
		Period period = new Period(rs.getLong(MapperParameters.PERIOD__ID));
		period.setLastPriodId(rs
				.getLong(MapperParameters.PERIOD__LAST_PERIOD_ID));
		period.setPeriod(rs.getDate(MapperParameters.PERIOD__START_DATE),
				rs.getDate(MapperParameters.PERIOD__END_DATE));
		period.setStatus(Status.values()[rs
				.getInt(MapperParameters.PERIOD__STATUS)]);
		return period;
	}

}
