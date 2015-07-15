package ua.nure.ostpc.malibu.shedule.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	public Period getFirstDraftPeriod(Date dateTime);

	public Period getPeriod(long periodId);

	public Period getLastPeriod(long periodId);

	public List<Period> getAllPeriods();

	public Map<Long, Status> getScheduleStatusMap();

	/**
	 * Return schedule by period id.
	 * 
	 * @param periodId
	 *            - Period id;
	 * @return Schedule
	 * @throws DAOException 
	 */
	public Schedule getSchedule(long periodId) throws DAOException;

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

	public long insertSchedule(Schedule schedule);

	public boolean updateSchedule(Schedule schedule);

	public Date getEndDateForLastPeriod();

	public Status getStatusByPeriodId(long periodId);

	public void removeSchedule(long id) throws DAOException;

	void updateScheduleStatus(long id, Status status) throws DAOException;
}
