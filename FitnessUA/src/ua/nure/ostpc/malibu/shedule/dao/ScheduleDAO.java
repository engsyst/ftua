package ua.nure.ostpc.malibu.shedule.dao;

import java.sql.Date;
import java.sql.SQLException;
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
	 * @throws SQLException 
	 */
	public Period readPeriod(Date date) throws SQLException;

	/**
	 * 
	 * @param period
	 * @return
	 */
	public Schedule readSchedule(Period period);

	/**
	 * ������� ������ ����������. ���� ���� �������� � �������� �������, ��
	 * ���������� ���������� �� ���� ������. 
	 * ����������: ������ � ������ ��������� ����������� ����������.
	 * <p/>
	 * @param start
	 * @param end
	 * @return ������ ����������
	 */
	public Set<Schedule> readSchedules(Date start, Date end);

	public int insertSchedule(Schedule shedule);

	public boolean updateSchedule(Schedule shedule);

}
