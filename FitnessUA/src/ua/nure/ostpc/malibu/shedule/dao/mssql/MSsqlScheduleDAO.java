package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.dao.mapper.MapperParameters;
import ua.nure.ostpc.malibu.shedule.entity.Assignment;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;

public class MSsqlScheduleDAO implements ScheduleDAO {
	private static final Logger log = Logger.getLogger(MSsqlScheduleDAO.class);
	private static final String SQL__READ_PERIOD = "SELECT * FROM SchedulePeriod WHERE StartDate<=? AND EndDate>=?";
	private static final String SQL__FIND_PERIODS_BY_DATE = "SELECT * FROM SchedulePeriod WHERE StartDate>=? AND EndDate<=?;";
	private static final String SQL__INSERT_SCHEDULE = "INSERT INTO DaySchedule(dates, halfOfDay, users_id, club_id, shedule_period_id) "
			+ "VALUES(?, ?, ?, ?, ?)";

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
	public Schedule readSchedule(Period period) {
		Connection con = null;
		Schedule schedule = null;
		try {
			con = MSsqlDAOFactory.getConnection();
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
			schedules = readSchedules(start, end, con);
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
		return schedules;
	}

	private Set<Schedule> readSchedules(Date start, Date end, Connection con)
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
				Schedule schedule = readSchedule(period);
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
					log.error("Can not close statement", e);
				}
			}
		}
	}

	@Override
	public int insertSchedule(Schedule schedule) throws SQLException {
		Connection con = MSsqlDAOFactory.getConnection();
		PreparedStatement pstmt = null;
		int res = 0;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_SCHEDULE);
			// mapSchedule(schedule, pstmt);
			res = pstmt.executeBatch().length;
			con.commit();
		} catch (SQLException e) {
			log.error("Can not insert Schedule", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection", e);
			}
		}
		return res;
	}

	@Override
	public boolean updateSchedule(Schedule shedule) {
		// TODO Auto-generated method stub
		return false;
	}

	private Period unMapPeriod(ResultSet rs) throws SQLException {
		Period period = new Period(rs.getLong(MapperParameters.PERIOD__ID));
		period.setLastPriodId(rs
				.getLong(MapperParameters.PERIOD__LAST_PERIOD_ID));
		period.setPeriod(rs.getDate(MapperParameters.PERIOD__START_DATE),
				rs.getDate(MapperParameters.PERIOD__END_DATE));
		return period;
	}
}
