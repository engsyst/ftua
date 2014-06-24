/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;

// Start of user code (user defined imports)

// End of user code

/**
 * Description of Employee.
 * 
 * @author engsyst
 */
public class Employee implements Serializable {
	/**
	 * Description of the property employeeId.
	 */
	private long employeeId = 0L;

	/**
	 * Description of the property firstName.
	 */
	private String firstName = "";

	/**
	 * Description of the property sureName.
	 */
	private String sureName = "";

	/**
	 * Description of the property lastName.
	 */
	private String lastName = "";

	/**
	 * Description of the property min.
	 */
	private int min = 0;

	/**
	 * Description of the property max.
	 */
	private int max = 0;

	// Start of user code (user defined attributes for Employee)

	// End of user code

	/**
	 * The constructor.
	 */
	public Employee() {
		// Start of user code constructor for Employee)
		super();
		// End of user code
	}

	/**
	 * Description of the method setEmpPrefs.
	 * @param min 
	 * @param max 
	 */
	public void setEmpPrefs(int min, int max) {
		// Start of user code for method setEmpPrefs
		// End of user code
	}

	// Start of user code (user defined methods for Employee)

	// End of user code
	/**
	 * Returns employeeId.
	 * @return employeeId 
	 */
	public long getEmployeeId() {
		return this.employeeId;
	}

	/**
	 * Sets a value to attribute employeeId. 
	 * @param newEmployeeId 
	 */
	public void setEmployeeId(long newEmployeeId) {
		this.employeeId = newEmployeeId;
	}

	/**
	 * Returns firstName.
	 * @return firstName 
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * Sets a value to attribute firstName. 
	 * @param newFirstName 
	 */
	public void setFirstName(String newFirstName) {
		this.firstName = newFirstName;
	}

	/**
	 * Returns sureName.
	 * @return sureName 
	 */
	public String getSureName() {
		return this.sureName;
	}

	/**
	 * Sets a value to attribute sureName. 
	 * @param newSureName 
	 */
	public void setSureName(String newSureName) {
		this.sureName = newSureName;
	}

	/**
	 * Returns lastName.
	 * @return lastName 
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * Sets a value to attribute lastName. 
	 * @param newLastName 
	 */
	public void setLastName(String newLastName) {
		this.lastName = newLastName;
	}

	/**
	 * Returns min.
	 * @return min 
	 */
	public int getMin() {
		return this.min;
	}

	/**
	 * Sets a value to attribute min. 
	 * @param newMin 
	 */
	public void setMin(int newMin) {
		this.min = newMin;
	}

	/**
	 * Returns max.
	 * @return max 
	 */
	public int getMax() {
		return this.max;
	}

	/**
	 * Sets a value to attribute max. 
	 * @param newMax 
	 */
	public void setMax(int newMax) {
		this.max = newMax;
	}

}
