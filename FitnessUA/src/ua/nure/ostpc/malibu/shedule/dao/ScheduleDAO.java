package ua.nure.ostpc.malibu.shedule.dao;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Set;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
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
	 * @throws SQLException
	 */
	public Schedule readSchedule(Period period);

	/**
	 * ������� ������ ����������. ���� ���� �������� � �������� �������, ��
	 * ���������� ���������� �� ���� ������. ����������: ������ � ������
	 * ��������� ����������� ����������.
	 * <p/>
	 * 
	 * @param start
	 * @param end
	 * @return ������ ����������
	 */
	public Set<Schedule> readSchedules(Date start, Date end);

	public int insertSchedule(Schedule shedule);

	public boolean updateSchedule(Schedule shedule);

	public Date readMaxEndDate();

	public void pushToExcel(Period period) throws SQLException, RowsExceededException, WriteException, IOException;
}
