/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Date;

public class Assignment implements Serializable {
	private Period period = null;
	/**
	 * Description of the property club.
	 */
	private Club club = null;

	/**
	 * Description of the property employee.
	 */
	private Employee employee = null;

	/**
	 * halfOfDay - <br />
	 * 1 - first half of the day<br />
	 * 2 - second half of the day
	 * <p/>
	 * Use final fields (@link FIRST_HALF}, (@link SECOND_HALF} 
	 * 0 - reserved
	 */
	private int halfOfDay = 0;
	
	/**
	 * See: {@link halfOfDay}
	 */
	public static final int FIRST_HALF = 1;

	/**
	 * See: {@link halfOfDay}
	 */
	public static final int SECOND_HALF = 2;

	/**
	 * Description of the property date.
	 */
	private Date date = new Date();
	
	/**
	 * Description of the property assignment_id.
	 */
	private long Assignment_Id = 0;
	
	private long SchedulePeriodId =0;
	
	private long ClubId;

	public Assignment() {
		super();
	}
	public Assignment (long day_shedule_id,Date date, int HalfOfDay, long Employee_Id, long Club_Id, long shedule_period_id)
	{
		this.Assignment_Id=day_shedule_id;
		this.halfOfDay = HalfOfDay;
		this.date = date;
		this.employee.setEmployeeId(Employee_Id);
		this.club.setClubId(Club_Id);
		this.period.setPeriod_Id(shedule_period_id);
		
	}
	/**
	 * Returns club. See: {@link Club}
	 * @return club 
	 */
	public Club getClub() {
		return this.club;
	}

	/**
	 * Sets a value to attribute club. See: {@link Club}
	 * @param newClub 
	 */
	public void setClub(Club newClub) {
		this.club = newClub;
	}

	/**
	 * Returns employee. See: {@link Employee}
	 * @return employee 
	 */
	public Employee getEmployee() {
		return this.employee;
	}

	/**
	 * Sets a value to attribute employee. See: {@link Employee}
	 * @param newEmployee 
	 */
	public void setEmployee(Employee newEmployee) {
		this.employee = newEmployee;
	}

	/**
	 * Returns halfOfDay. See: {@link halfOfDay}
	 * @return halfOfDay 
	 */
	public int getHalfOfDay() {
		return this.halfOfDay;
	}

	/**
	 * Sets a value to attribute halfOfDay. 
	 * @param newHalfOfDay 
	 */
	public void setHalfOfDay(int newHalfOfDay) {
		this.halfOfDay = newHalfOfDay;
	}

	/**
	 * Returns date.
	 * @return date 
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * Sets a value to attribute date. 
	 * @param newDate 
	 */
	public void setDate(Date newDate) {
		this.date = newDate;
	}
	
	public long getAssignment_Id ()
	{
		return this.Assignment_Id;
	}
	
	public void setAssignment_Id (long id)
	{
		this.Assignment_Id = id;
	}
	public long getSchedulePeriodId() {
		return SchedulePeriodId;
	}
	public void setSchedulePeriodId(long schedulePeriodId) {
		SchedulePeriodId = schedulePeriodId;
	}
	public long getClubId() {
		return ClubId;
	}
	public void setClubId(long clubId) {
		ClubId = clubId;
	}
}
