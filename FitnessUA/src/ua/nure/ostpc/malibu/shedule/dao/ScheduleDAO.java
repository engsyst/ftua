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

	public Period getPeriod(long periodId);

	/**
	 * Return schedule by period id.
	 * 
	 * @param periodId
	 *            - Period id;
	 * @return Schedule
	 */
	public Schedule getSchedule(long periodId);

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
	
	public Set<Schedule> getNotClosedSchedules();

	public int insertSchedule(Schedule shedule);

	public boolean updateSchedule(Schedule schedule);

	public Date getMaxEndDate();

	public Status getStatusByPeriodId(long periodId);

	public String pushToExcel(Period period) throws SQLException,
			RowsExceededException, WriteException, IOException;
}
