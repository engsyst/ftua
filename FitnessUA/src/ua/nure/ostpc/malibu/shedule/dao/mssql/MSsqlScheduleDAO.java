package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import jxl.*;
import jxl.write.*;
import jxl.write.Number;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;

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

	public void pushToExcel(Period period) {
		Statement st = null;
		Connection con = MSsqlDAOFactory.getConnection();
		ArrayList<Long> clubs = new ArrayList<Long>();
		try {
			st = con.createStatement();
			java.sql.ResultSet resSet = st
					.executeQuery(String
							.format("select club_id from ClubPref where schedule_period_id = "
									+ period.getPeriodId() + ";"));
			while (resSet.next()) {
				clubs.add(resSet.getLong(MapperParameters.CLUB__ID));
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					throw e;
				}
			}
		}
		Locale local = new Locale("ru", "RU");
		java.util.Date StartDate = period.getStartDate();
		java.util.Date EndDate = period.getEndDate();
		int PeriodDuration = (int) period.getDuration();
		GregorianCalendar calenStart = new GregorianCalendar();
		GregorianCalendar calenEnd = new GregorianCalendar();
		GregorianCalendar calenCurrent = new GregorianCalendar();

		calenStart.setTime(StartDate);
		calenEnd.setTime(EndDate);
		calenCurrent.setTime(StartDate);
		calenEnd.add(Calendar.DATE, PeriodDuration);

		try {
			// Создаем книгу Excell
			SimpleDateFormat dateFormatter = new SimpleDateFormat();
			dateFormatter = new SimpleDateFormat("dd-MM-yy");

			String nameOfTheSheduleFile = "c:/temp/Shedule "
					+ dateFormatter.format(StartDate) + " to "
					+ dateFormatter.format(calenEnd.getTime()) + ".xls";
			WritableWorkbook wb = Workbook.createWorkbook(new File(
					nameOfTheSheduleFile));
			WritableSheet sheet = wb.createSheet("Лист 1", 0);
			sheet.addCell(new Label(0, 0, "Club_Id/Date"));
			sheet.setColumnView(0, 20);
			String ourDate = null;
			boolean first = false, second = false;
			for (int i = 1; calenCurrent.get(Calendar.DAY_OF_YEAR) != calenEnd
					.get(Calendar.DAY_OF_YEAR)
					|| calenCurrent.get(Calendar.YEAR) != calenEnd
							.get(Calendar.YEAR); i++) {
				ourDate = calenCurrent.getDisplayName(Calendar.DAY_OF_WEEK, 2,
						local)
						+ " "
						+ calenCurrent.get(Calendar.DAY_OF_MONTH)
						+ "  "
						+ calenCurrent.getDisplayName(Calendar.MONTH, 2, local)
						+ "  " + calenCurrent.get(Calendar.YEAR);

				sheet.setColumnView(i, 30);
				sheet.addCell(new Label(i, 0, ourDate));

				calenCurrent.add(Calendar.DATE, 1);
			}
			System.out.println("goods");

			for (int i = 0; i < clubs.size(); i++) {
				sheet.addCell(new Label(0, i + 1, clubs.get(i).toString()));
			}

			// Создаем шрифт для данных
			WritableFont font01Normal = new WritableFont(WritableFont.TIMES, 8,
					WritableFont.BOLD);
			// Создаем стиль ячейки для заголовка таблицы
			WritableCellFormat cellstyleTblHdr = new WritableCellFormat(
					font01Normal);
			cellstyleTblHdr.setAlignment(Alignment.CENTRE);
			cellstyleTblHdr.setWrap(true);
			cellstyleTblHdr.setBorder(Border.ALL, BorderLineStyle.MEDIUM);
			cellstyleTblHdr.setVerticalAlignment(VerticalAlignment.CENTRE);
			cellstyleTblHdr.setBackground(Colour.LIGHT_GREEN, Pattern.SOLID);
			// Создаем стиль ячейки для данных в таблице.
			WritableCellFormat cellstyleTblLeft = new WritableCellFormat(
					font01Normal);
			cellstyleTblLeft.setAlignment(Alignment.LEFT);
			cellstyleTblLeft.setWrap(true);
			cellstyleTblLeft.setBorder(Border.BOTTOM, BorderLineStyle.MEDIUM);
			cellstyleTblLeft.setBorder(Border.LEFT, BorderLineStyle.MEDIUM);
			cellstyleTblLeft.setBorder(Border.RIGHT, BorderLineStyle.MEDIUM);
			cellstyleTblLeft.setVerticalAlignment(VerticalAlignment.TOP);

			try {
				wb.write();
			} catch (SQLException e) {
				throw e;
			}
			try {
				wb.close();
			} catch (SQLException e) {
				throw e;
			}

		} catch (Exception e) {
			System.err.println(e);
		}
		System.out.println("good");

	}

}
