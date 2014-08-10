package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jxl.*;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;
import jxl.format.Colour;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.AssignmentExcelDAO;
import ua.nure.ostpc.malibu.shedule.dao.ClubDAO;
import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.entity.Assignment;
import ua.nure.ostpc.malibu.shedule.entity.AssignmentExcel;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubDaySchedule;
import ua.nure.ostpc.malibu.shedule.entity.DaySchedule;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;

public class MSsqlScheduleDAO implements ScheduleDAO {
	private static final Logger log = Logger.getLogger(MSsqlScheduleDAO.class);

	private static final String SQL__GET_PERIOD_BY_DATE = "SELECT * FROM SchedulePeriod WHERE StartDate<=? AND EndDate>=?";
	private static final String SQL__GET_PERIOD_BY_ID = "SELECT * FROM SchedulePeriod WHERE SchedulePeriodId=?";
	private static final String SQL__FIND_PERIODS_BY_DATE = "SELECT * FROM SchedulePeriod WHERE StartDate>=? AND EndDate<=?;";
	private static final String SQL__INSERT_SCHEDULE = "INSERT INTO SchedulePeriod(StartDate, EndDate, LastPeriodId, Status, ShiftsNumber, WorkHoursInDay) "
			+ "VALUES(?, ?, (SELECT MAX(SchedulePeriodId) FROM SchedulePeriod), ?, ?, ?);";
	private static final String SQL__UPDATE_SCHEDULE = "UPDATE SchedulePeriod SET LastPeriodId=?, StartDate=?, EndDate=?, Status=? ShiftsNumber=?, WorkHoursInDay=? "
			+ "WHERE SchedulePeriodId=?;";
	private static final String SQL__GET_MAX_END_DATE = "SELECT MAX(EndDate) AS EndDate FROM SchedulePeriod;";
	private static final String SQL__FIND_STATUS_BY_PEDIOD_ID = "SELECT Status FROM SchedulePeriod WHERE SchedulePeriodId=?;";

	private MSsqlAssignmentDAO assignmentDAO = (MSsqlAssignmentDAO) DAOFactory
			.getDAOFactory(DAOFactory.MSSQL).getAssignmentDAO();
	private MSsqlClubDAO clubDAO = (MSsqlClubDAO) DAOFactory.getDAOFactory(
			DAOFactory.MSSQL).getClubDAO();
	private MSsqlEmployeeDAO employeeDAO = (MSsqlEmployeeDAO) DAOFactory
			.getDAOFactory(DAOFactory.MSSQL).getEmployeeDAO();
	private MSsqlAssignmentExcelDAO assignmentExcelDAO = (MSsqlAssignmentExcelDAO) DAOFactory
			.getDAOFactory(DAOFactory.MSSQL).getAssignmentExcelDAO();
	private MSsqlShiftDAO shiftDAO = (MSsqlShiftDAO) DAOFactory.getDAOFactory(
			DAOFactory.MSSQL).getShiftDAO();

	@Override
	public Period getPeriod(Date date) {
		Connection con = null;
		Period period = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			period = getPeriod(con, date);
		} catch (SQLException e) {
			log.error("Can not get period by date.", e);
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

	private Period getPeriod(Connection con, Date date) throws SQLException {
		PreparedStatement pstmt = null;
		Period period = null;
		try {
			pstmt = con.prepareStatement(SQL__GET_PERIOD_BY_DATE);
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
	public Period getPeriod(long periodId) {
		Connection con = null;
		Period period = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			period = getPeriod(con, periodId);
		} catch (SQLException e) {
			log.error("Can not get period by id.", e);
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
	public Schedule getSchedule(long periodId) {
		Connection con = null;
		Schedule schedule = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			schedule = getSchedule(con, periodId);
		} catch (SQLException e) {
			log.error("Can not get schedule.", e);
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

	private Schedule getSchedule(Connection con, long periodId)
			throws SQLException {
		Schedule schedule = null;
		Period period = getPeriod(con, periodId);
		if (period != null) {
			Status status = getStatusByPeriodId(periodId);
			Map<Date, DaySchedule> dayScheduleMap = new HashMap<Date, DaySchedule>();
			List<Club> clubs = clubDAO.getClubsByDependency(con, true);
			GregorianCalendar currentDateCalendar = new GregorianCalendar();
			currentDateCalendar.setTime(period.getStartDate());
			while (currentDateCalendar.getTimeInMillis() <= period.getEndDate()
					.getTime()) {

				currentDateCalendar.add(GregorianCalendar.DAY_OF_YEAR, 1);
			}

		}
		return schedule;
	}

	@Override
	public Set<Schedule> getSchedules(Date startDate, Date endDate) {
		Connection con = null;
		Set<Schedule> schedules = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			schedules = getSchedules(con, startDate, endDate);
		} catch (SQLException e) {
			log.error("Can not get schedules.", e);
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

	private Set<Schedule> getSchedules(Connection con, Date start, Date end)
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
				Schedule schedule = getSchedule(con, period);
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
			insertSchedule(con, schedule);
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

	private int insertSchedule(Connection con, Schedule schedule)
			throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_SCHEDULE);
			mapScheduleForInsert(schedule, pstmt);
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
		boolean result = true;
		try {
			con = MSsqlDAOFactory.getConnection();
			updateSchedule(con, schedule);
			Set<Assignment> assignments = schedule.getAssignments();
			Iterator<Assignment> it = assignments.iterator();
			if (it.hasNext()) {
				Assignment assignment = it.next();
				result = result
						&& assignmentDAO.updateAssignment(con, assignment);
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
		return result;
	}

	private boolean updateSchedule(Connection con, Schedule schedule)
			throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__UPDATE_SCHEDULE);
			mapScheduleForUpdate(schedule, pstmt);
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
	public Date getMaxEndDate() {
		Connection con = null;
		Date maxEndDate = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			maxEndDate = getMaxEndDate(con);
		} catch (SQLException e) {
			log.error("Can not get max end date.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
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
			con = MSsqlDAOFactory.getConnection();
			status = getStatusByPeriodId(con, periodId);
		} catch (SQLException e) {
			log.error("Can not get status.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
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
		return status;
	}

	private void mapScheduleForInsert(Schedule schedule, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setDate(1,
				new Date(schedule.getPeriod().getStartDate().getTime()));
		pstmt.setDate(2, new Date(schedule.getPeriod().getEndDate().getTime()));
		pstmt.setLong(3, schedule.getStatus().ordinal());
		pstmt.setLong(4, schedule.getShiftsNumber());
		pstmt.setLong(5, schedule.getWorkHoursInDay());
	}

	private void mapScheduleForUpdate(Schedule schedule, PreparedStatement pstmt)
			throws SQLException {
		pstmt.setLong(1, schedule.getPeriod().getLastPeriodId());
		pstmt.setDate(2,
				new Date(schedule.getPeriod().getStartDate().getTime()));
		pstmt.setDate(3, new Date(schedule.getPeriod().getEndDate().getTime()));
		pstmt.setLong(4, schedule.getStatus().ordinal());
		pstmt.setLong(5, schedule.getShiftsNumber());
		pstmt.setLong(6, schedule.getWorkHoursInDay());
		pstmt.setLong(7, schedule.getPeriod().getPeriodId());
	}

	private Period unMapPeriod(ResultSet rs) throws SQLException {
		Period period = new Period(rs.getLong(MapperParameters.PERIOD__ID));
		period.setLastPriodId(rs
				.getLong(MapperParameters.PERIOD__LAST_PERIOD_ID));
		period.setPeriod(rs.getDate(MapperParameters.PERIOD__START_DATE),
				rs.getDate(MapperParameters.PERIOD__END_DATE));
		return period;
	}

	@SuppressWarnings({ "unchecked" })
	public String pushToExcel(Period period) throws SQLException,
			RowsExceededException, WriteException, IOException {
		Statement st = null;
		Connection con = null;

		@SuppressWarnings("rawtypes")
		Set<Club> clubs = new HashSet();
		try {
			con = MSsqlDAOFactory.getConnection();
			st = con.createStatement();
			java.sql.ResultSet resSet = st
					.executeQuery(String
							.format("SELECT DISTINCT cl.Title  , cl.QuantityOfPeople from [Assignment] ass , Club cl where ass.ClubId=cl.ClubId and  SchedulePeriodId =  "
									+ period.getPeriodId() + ";"));
			while (resSet.next()) {
				Club tempClub = new Club();
				tempClub.setTitle((resSet.getString("Title")));
				tempClub.setQuantityOfPeople(resSet.getInt("QuantityOfPeople"));
				clubs.add(tempClub);
			}
		} catch (SQLException e) {
			log.error("Can not select club id.", e);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					log.error("Can not close statement.", e);
				}
			}
		}
		Locale local = new Locale("ru", "RU");
		java.util.Date StartDate = period.getStartDate();
		java.util.Date EndDate = period.getEndDate();
		int PeriodDuration = period.getDurationDays();
		GregorianCalendar calenStart = new GregorianCalendar();
		GregorianCalendar calenEnd = new GregorianCalendar();
		GregorianCalendar calenCurrent = new GregorianCalendar();

		calenStart.setTime(StartDate);
		calenEnd.setTime(EndDate);
		calenCurrent.setTime(StartDate);

		// creating Excell
		SimpleDateFormat dateFormatter = new SimpleDateFormat();
		dateFormatter = new SimpleDateFormat("dd-MM-yy");

		String nameOfTheSheduleFile = "c:/temp/Shedule "
				+ dateFormatter.format(StartDate) + " to "
				+ dateFormatter.format(calenEnd.getTime()) + ".xls";
		WritableWorkbook wb = Workbook.createWorkbook(new File(
				nameOfTheSheduleFile));
		WritableSheet sheet = wb.createSheet("list  1", 0);
		String ourDate = null;

		for (int i = 1; i <= (PeriodDuration + 1); i++) {

			ourDate = calenCurrent.getDisplayName(Calendar.DAY_OF_WEEK, 2,
					local)
					+ " "
					+ calenCurrent.get(Calendar.DAY_OF_MONTH)
					+ "  "
					+ calenCurrent.getDisplayName(Calendar.MONTH, 2, local)
					+ "  " + calenCurrent.get(Calendar.YEAR);

			sheet.setColumnView(i + 1, 30);
			sheet.addCell(new Label(i + 1, 0, ourDate));
			calenCurrent.add(Calendar.DATE, 1);

		}
		System.out.println("goods");
		Set<AssignmentExcel> assignmentExcel = assignmentExcelDAO
				.selectAssignmentsExcel(period);
		Iterator<AssignmentExcel> iterAssExcel = assignmentExcel.iterator();

		Iterator<Club> iterClubs = clubs.iterator();
		// modul of writing classes and halfsOfDays sells
		for (int i = 0, j = 0; i < clubs.size(); i++) {
			Club clbs = iterClubs.next();
			for (int y = j + 1; y <= (j + 2 * clbs.getQuantityOfPeople()); y++) {
				sheet.addCell(new Label(0, y, clbs.getTitle()));
			}
			for (int y = j + 1; y <= (j + clbs.getQuantityOfPeople()); y++) {
				sheet.addCell(new Label(1, y, "first half"));
			}
			for (int y = 1 + j + clbs.getQuantityOfPeople(); y <= (j + 2 * clbs
					.getQuantityOfPeople()); y++) {
				sheet.addCell(new Label(1, y, "second"));
			}
			j += 2 * clbs.getQuantityOfPeople();

		}

		calenCurrent.setTime(StartDate);

		SimpleDateFormat dateFormatter1 = new SimpleDateFormat("yyyy-MM-dd");
		while (iterAssExcel.hasNext()) {
			AssignmentExcel assignment = iterAssExcel.next();
			int columnNumber = 0;
			int rownNumber = 0;

			for (int i = 1; i <= (PeriodDuration + 1); i++) {
				if (dateFormatter1.format(assignment.getDate()).equals(
						dateFormatter1.format(calenCurrent.getTime()))) {
					columnNumber = (i + 1);
					break;

				}
				calenCurrent.add(Calendar.DATE, 1);
			}
			calenCurrent.setTime(StartDate);

			for (int j = 1; j < sheet.getRows() + 2; j++) {

				if (assignment.getClubTitle().equals(
						sheet.getCell(0, j).getContents())) {
					// counter of coming into "if"

					rownNumber = j;
					if (assignment.getHalfOfDay() == 1) {
						for (int k = 0; k < assignment.getQuantityOfPeople(); k++) {
							if (sheet.getCell(columnNumber, rownNumber + k)
									.getContents().isEmpty()) {
								sheet.addCell(new Label(columnNumber,
										rownNumber + k, assignment.getName(),
										getCellFormat(new Colour(assignment
												.getColour(), "2323", 1, 1, 1) {
										})));
								// sheet.addCell(new Label(columnNumber,
								// rownNumber+k,assignment.getName(),getCellFormat(new
								// Colour(0x19,"2323",1,1,1){})));

								break;
							}
						}
						break;
					} else {
						rownNumber += assignment.getQuantityOfPeople();
						for (int k = 0; k < assignment.getQuantityOfPeople(); k++) {
							if (sheet.getCell(columnNumber, rownNumber + k)
									.getContents().isEmpty()) {
								// sheet.addCell(new Label(columnNumber,
								// rownNumber+k,assignment.getName(),getCellFormat((new
								// Colour(assignment.getColour(),"2323",1,1,1){}))));
								sheet.addCell(new Label(columnNumber,
										rownNumber + k, assignment.getName(),
										getCellFormat((Colour.BRIGHT_GREEN))));
								break;
							}
						}
						break;
					}
				}

			}

		}

		sheet.removeColumn(0);
		sheet.removeColumn(0);
		sheet.insertColumn(0);
		sheet.insertColumn(0);
		sheet.addCell(new Label(0, 0, "Имя клуба / Дата "));
		sheet.addCell(new Label(1, 0, "Половина дня"));
		sheet.setColumnView(0, 20);
		sheet.setColumnView(1, 20);

		Iterator<Club> iterClubs1 = clubs.iterator();
		for (int i = 0, j = 0; i < clubs.size(); i++) {
			Club clbs = iterClubs1.next();

			sheet.mergeCells(0, 1 + j, 0, j + 2 * clbs.getQuantityOfPeople());
			sheet.addCell(new Label(0, 1 + j, clbs.getTitle()));
			sheet.mergeCells(1, 1 + j, 1, j + clbs.getQuantityOfPeople());
			sheet.addCell(new Label(1, 1 + j, "Первая"));
			sheet.mergeCells(1, 1 + j + clbs.getQuantityOfPeople(), 1, j + 2
					* clbs.getQuantityOfPeople());
			sheet.addCell(new Label(1, 1 + j + clbs.getQuantityOfPeople(),
					"Вторая"));
			j += 2 * clbs.getQuantityOfPeople();
		}

		// end

		wb.write();
		wb.close();
		return nameOfTheSheduleFile;
	}

	private static WritableCellFormat getCellFormat(Colour colour)
			throws WriteException {
		WritableFont cellFont = new WritableFont(WritableFont.TIMES, 12);
		WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
		cellFormat.setBackground(colour);
		return cellFormat;
	}
}
