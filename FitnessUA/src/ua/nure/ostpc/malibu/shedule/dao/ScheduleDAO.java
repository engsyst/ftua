package ua.nure.ostpc.malibu.shedule.dao;

import java.util.Date;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;

/**
 * Use UTF8 encoding in project properties
 * 
 * @author engsyst
 * 
 */

public interface ScheduleDAO {
	/**
	 * Create instance of Period from table Period if date inside period dates.
	 * Otherwise return null.
	 * 
	 * @param date
	 * @return Period from table Period if date inside period dates, otherwise
	 *         null
	 */
	public Period readPeriod(Date date);

	/**
	 * 
	 * @param period
	 * @return
	 */
	public Schedule readSchedule(Period period);

	/**
	 * —оздает список расписаний. ≈сли дата попадает в середину периода, то
	 * выбираетс€ расписание на весь период. 
	 * —ортировка: первым в списке последнее добавленное расписание.
	 * <p/>
	 * @param start
	 * @param end
	 * @return —писок расписаний
	 */
	public Set<Schedule> readSchedules(Date start, Date end);

	public int insertSchedule(Schedule shedule);

	public boolean updateSchedule(Schedule shedule);

}
