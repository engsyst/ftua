/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostsp.malibu.shedule.entity;

import java.util.Date;
import ua.nure.ostsp.malibu.shedule.entity.Club;
import ua.nure.ostsp.malibu.shedule.entity.Employee;
// Start of user code (user defined imports)

// End of user code

/**
 * halfOfDay - половина дня<br />
 * 1 - первая половина<br />
 * 2 - вторая половина
 * 
 * @author engsyst
 */
public class Assignment {
	/**
	 * Description of the property club.
	 */
	private Club club = null;

	/**
	 * Description of the property employee.
	 */
	private Employee employee = null;

	/**
	 * Description of the property halfOfDay.
	 */
	private int halfOfDay = 0;

	/**
	 * Description of the property date.
	 */
	private Date date = new Date();

	// Start of user code (user defined attributes for Assignment)

	// End of user code

	/**
	 * The constructor.
	 */
	public Assignment() {
		// Start of user code constructor for Assignment)
		super();
		// End of user code
	}

	// Start of user code (user defined methods for Assignment)

	// End of user code
	/**
	 * Returns club.
	 * @return club 
	 */
	public Club getClub() {
		return this.club;
	}

	/**
	 * Sets a value to attribute club. 
	 * @param newClub 
	 */
	public void setClub(Club newClub) {
		this.club = newClub;
	}

	/**
	 * Returns employee.
	 * @return employee 
	 */
	public Employee getEmployee() {
		return this.employee;
	}

	/**
	 * Sets a value to attribute employee. 
	 * @param newEmployee 
	 */
	public void setEmployee(Employee newEmployee) {
		this.employee = newEmployee;
	}

	/**
	 * Returns halfOfDay.
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

}
