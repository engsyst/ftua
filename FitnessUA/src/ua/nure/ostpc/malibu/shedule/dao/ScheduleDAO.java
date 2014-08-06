package ua.nure.ostpc.malibu.shedule.dao;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Set;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;

public interface ScheduleDAO {

	/**
	 * Return period if date inside period dates. Otherwise return null.
	 * 
	 * @param date
	 * @return Period if date inside period dates, otherwise null.
	 */
	public Period getPeriod(Date date);

	/**
	 * 
	 * @param period
	 * @return
	 * @throws SQLException
	 */
	public Schedule getSchedule(Period period);

	/**
	 * Return set of schedules between startDate and endDate.
	 * 
	 * @param startDate
	 *            - Start date;
	 * @param endDate
	 *            - End date.
	 * @return Set of schedules between startDate and endDate.
	 */
	public Set<Schedule> getSchedules(Date startDate, Date endDate);

	public int insertSchedule(Schedule shedule);

	public boolean updateSchedule(Schedule shedule);

	public Date getMaxEndDate();

	public Status getStatusByPeriodId(long periodId);

	public int getShiftsNumberByPeriodId(long periodId);

	public int getWorkHoursInDayByPeriodId(long periodId);

	public String pushToExcel(Period period) throws SQLException,
			RowsExceededException, WriteException, IOException;
}
