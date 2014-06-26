/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;

/**
 * Description of Employee.
 * 
 * @author engsyst
 */
public class Employee implements Serializable, Comparable<Employee> {

	public static final int MAX_DAYS = 7;

	private long employeeId;

	private String firstName;

	private String sureName;

	private String lastName;

	/**
	 * Min hours at week.
	 */
	private int min = 0;

	/**
	 * Max hours at week.
	 */
	private int max = 0;

	public Employee() {
		super();
	}

	public Employee(String firstName, String sureName, String lastName,
			int min, int max) {
		super();
		this.firstName = firstName;
		this.sureName = sureName;
		this.lastName = lastName;
		setEmpPrefs(min, max);
	}

	/**
	 * Description of the method setEmpPrefs.
	 * 
	 * @param min
	 * @param max
	 */
	public void setEmpPrefs(int min, int max) throws IllegalArgumentException {
		if (min < 0 || max <= 0 || max > MAX_DAYS || min > max)
			throw new IllegalArgumentException(
					"Args: days at week out of range");
		this.min = min;
		this.max = max;
	}

	/**
	 * Returns employeeId.
	 * 
	 * @return employeeId
	 */
	public long getEmployeeId() {
		return this.employeeId;
	}

	/**
	 * Sets a value to attribute employeeId.
	 * 
	 * @param newEmployeeId
	 */
	public void setEmployeeId(long newEmployeeId) {
		this.employeeId = newEmployeeId;
	}

	/**
	 * Returns firstName.
	 * 
	 * @return firstName
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * Sets a value to attribute firstName.
	 * 
	 * @param newFirstName
	 */
	public void setFirstName(String newFirstName) {
		this.firstName = newFirstName;
	}

	/**
	 * Returns sureName.
	 * 
	 * @return sureName
	 */
	public String getSureName() {
		return this.sureName;
	}

	/**
	 * Sets a value to attribute sureName.
	 * 
	 * @param newSureName
	 */
	public void setSureName(String newSureName) {
		this.sureName = newSureName;
	}

	/**
	 * Returns lastName.
	 * 
	 * @return lastName
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * Sets a value to attribute lastName.
	 * 
	 * @param newLastName
	 */
	public void setLastName(String newLastName) {
		this.lastName = newLastName;
	}

	/**
	 * Returns min.
	 * 
	 * @return min
	 */
	public int getMin() {
		return this.min;
	}

	/**
	 * Sets a value to attribute min. Set it after {@link max}.
	 * 
	 * @param newMin
	 */
	public void setMin(int min) {
		if (min < 0 || min > this.max)
			throw new IllegalArgumentException(
					"Args: days at week out of range");
		this.min = min;
	}

	/**
	 * Returns max.
	 * 
	 * @return max
	 */
	public int getMax() {
		return this.max;
	}

	/**
	 * Sets a value to attribute max. Set it before {@link min}.
	 * 
	 * @param max
	 */
	public void setMax(int max) {
		if (max < 0 || max > MAX_DAYS || max < this.min)
			throw new IllegalArgumentException(
					"Args: days at week out of range");
		this.max = max;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime  + (int) (employeeId ^ (employeeId >>> 32));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Employee))
			return false;
		Employee other = (Employee) obj;
		if (employeeId != other.employeeId)
			return false;
		return true;
	}

	@Override
	public int compareTo(Employee o) {
		return this.lastName.compareTo(o.lastName);
	}

}
