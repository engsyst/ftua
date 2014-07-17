package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.entity.Assignment;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;

public class MSsqlScheduleDAO implements ScheduleDAO {
	private static final Logger log = Logger.getLogger(MSsqlScheduleDAO.class);
	private static final String SQL__READ_PERIOD = "SELECT * FROM SchedulePeriod WHERE StartDate<=? AND EndDate>=?";
	private static final String SQL__FIND_PERIODS_BY_DATE = "SELECT * FROM SchedulePeriod WHERE StartDate>=? AND EndDate<=?;";
	private static final String SQL__INSERT_PERIOD = "INSERT INTO SchedulePeriod(StartDate, EndDate, LastPeriodId) "
			+ "VALUES(?, ?, (SELECT MAX(SchedulePeriodId) FROM SchedulePeriod));";
	private static final String SQL__UPDATE_PERIOD = "UPDATE SchedulePeriod SET LastPeriodId=?, StartDate=?, EndDate=? "
			+ "WHERE SchedulePeriodId=?;";

	private MSsqlAssignmentDAO assignmentDAO = (MSsqlAssignmentDAO) DAOFactory
			.getDAOFactory(DAOFactory.MSSQL).getAssignmentDAO();
	private MSsqlClubDAO clubDAO = (MSsqlClubDAO) DAOFactory.getDAOFactory(
			DAOFactory.MSSQL).getClubDAO();
	private MSsqlEmployeeDAO employeeDAO = (MSsqlEmployeeDAO) DAOFactory
			.getDAOFactory(DAOFactory.MSSQL).getEmployeeDAO();

	@Override
	public Period readPeriod(Date date) {
		Connection con = null;
		Period period = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			period = readPeriod(con, date);
		} catch (SQLException e) {
			log.error("Can not read period.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return period;
	}

	private Period readPeriod(Connection con, Date date) throws SQLException {
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
					log.error("Can not close statement.", e);
				}
			}
		}
	}

	@Override
	public Schedule readSchedule(Period period) {
		Connection con = null;
		Schedule schedule = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			schedule = readSchedule(con, period);
		} catch (SQLException e) {
			log.error("Can not read Schedule.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return schedule;
	}

	private Schedule readSchedule(Connection con, Period period)
			throws SQLException {
		Schedule schedule = null;
		try {
			Set<Assignment> assignments = new TreeSet<Assignment>();
			List<Assignment> assignmentsForPeriod = assignmentDAO
					.findAssignmenstByPeriodId(con, period.getPeriodId());
			for (Assignment assignmentForPeriod : assignmentsForPeriod) {
				Club club = clubDAO.findClubById(con,
						assignmentForPeriod.getClubId());
				List<Employee> employees = employeeDAO
						.findEmployeesByAssignmentId(con,
								assignmentForPeriod.getAssignmentId());
				for (Employee employee : employees) {
					Assignment assignment = new Assignment(
							assignmentForPeriod.getAssignmentId(), period,
							club, assignmentForPeriod.getDate(),
							assignmentForPeriod.getHalfOfDay(), employee);
					assignments.add(assignment);
				}
			}
			if (assignments.size() != 0) {
				schedule = new Schedule(period, assignments);
			}
			return schedule;
		} catch (SQLException e) {
			throw e;
		}
	}

	@Override
	public Set<Schedule> readSchedules(Date start, Date end) {
		Connection con = null;
		Set<Schedule> schedules = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			schedules = readSchedules(con, start, end);
		} catch (SQLException e) {
			log.error("Can not read schedules.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return schedules;
	}

	private Set<Schedule> readSchedules(Connection con, Date start, Date end)
			throws SQLException {
		PreparedStatement pstmt = null;
		Set<Schedule> schedules = null;
		try {
			pstmt = con.prepareStatement(SQL__FIND_PERIODS_BY_DATE);
			pstmt.setDate(1, start);
			pstmt.setDate(2, end);
			ResultSet rs = pstmt.executeQuery();
			if (rs.isBeforeFirst()) {
				schedules = new TreeSet<Schedule>();
			}
			while (rs.next()) {
				Period period = unMapPeriod(rs);
				Schedule schedule = readSchedule(con, period);
				if (schedule != null) {
					schedules.add(schedule);
				}
			}
			return schedules;
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
	public int insertSchedule(Schedule schedule) {
		Connection con = null;
		int res = 0;
		try {
			con = MSsqlDAOFactory.getConnection();
			insertPeriod(con, schedule.getPeriod());
			Set<Assignment> assignments = schedule.getAssignments();
			Iterator<Assignment> it = assignments.iterator();
			if (it.hasNext()) {
				Assignment assignment = it.next();
				res += assignmentDAO.insertAssignment(con, assignment);
			}
			con.commit();
		} catch (SQLException e) {
			log.error("Can not insert schedule.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return res;
	}

	private int insertPeriod(Connection con, Period period) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_PERIOD);
			mapPeriodForInsert(period, pstmt);
			return pstmt.executeUpdate();
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
	public boolean updateSchedule(Schedule schedule) {
		Connection con = null;
		boolean res = true;
		try {
			con = MSsqlDAOFactory.getConnection();
			updatePeriod(con, schedule.getPeriod());
			Set<Assignment> assignments = schedule.getAssignments();
			Iterator<Assignment> it = assignments.iterator();
			if (it.hasNext()) {
				Assignment assignment = it.next();
				res = res && assignmentDAO.updateAssignment(con, assignment);
			}
			con.commit();
		} catch (SQLException e) {
			log.error("Can not update schedule.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return res;
	}

	private boolean updatePeriod(Connection con, Period period)
			throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__UPDATE_PERIOD);
			mapPeriodForUpdate(period, pstmt);
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

	private void mapPeriodForInsert(Period period, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setDate(1, new Date(period.getStartDate().getTime()));
		pstmt.setDate(2, new Date(period.getEndDate().getTime()));
	}

	private void mapPeriodForUpdate(Period period, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setLong(1, period.getLastPeriodId());
		pstmt.setDate(2, new Date(period.getStartDate().getTime()));
		pstmt.setDate(3, new Date(period.getEndDate().getTime()));
		pstmt.setLong(4, period.getPeriodId());
	}

	private Period unMapPeriod(ResultSet rs) throws SQLException {
		Period period = new Period(rs.getLong(MapperParameters.PERIOD__ID));
		period.setLastPriodId(rs
				.getLong(MapperParameters.PERIOD__LAST_PERIOD_ID));
		period.setPeriod(rs.getDate(MapperParameters.PERIOD__START_DATE),
				rs.getDate(MapperParameters.PERIOD__END_DATE));
		return period;
	}

	public void pushToExcel(Schedule schedule) {
		// to do;
	}
}
