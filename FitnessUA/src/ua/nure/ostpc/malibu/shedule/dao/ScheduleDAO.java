package ua.nure.ostpc.malibu.shedule.dao;

import java.util.Date;

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
	 * @return Period from table Period if date inside period dates, otherwise null
	 */
	public Period readPeriod(Date date);

	/**
	 * 
	 * @param period
	 * @return
	 */
	public Schedule readSchedule(Period period);
	
	public int insertSchedule(Schedule shedule);
	
	public boolean updateSchedule(Schedule shedule);
	

}
