/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Description of Employee.
 * 
 * @author engsyst
 */
public class Employee implements Serializable, IsSerializable,
		Comparable<Employee> {
	private static final long serialVersionUID = 1L;

	public static final int MAX_DAYS = 7;

	private long employeeId;

	private String firstName;

	private String secondName;

	private String lastName;

	private long clubId;

	private long employeeGroupId;

	private Date birthday;

	private String address;

	private String passportNumber;

	private String idNumber;

	private String cellPhone;

	private String workPhone;

	private String homePhone;

	private String email;

	private String education;

	private String notes;

	private String passportIssuedBy;

	/**
	 * Min hours at week.
	 */
	private int minDays;

	/**
	 * Max hours at week.
	 */
	private int maxDays;

	public Employee() {
	}

	public Employee(String firstName, String sureName, String lastName,
			int minDays, int maxDays) {
		this.firstName = firstName;
		this.secondName = sureName;
		this.lastName = lastName;
		setEmpPrefs(minDays, maxDays);
	}

	/**
	 * Description of the method setEmpPrefs.
	 * 
	 * @param minDays
	 * @param maxDays
	 */
	public void setEmpPrefs(int minDays, int maxDays)
			throws IllegalArgumentException {
		if (minDays < 0 || maxDays <= 0 || maxDays > MAX_DAYS
				|| minDays > maxDays)
			throw new IllegalArgumentException(
					"Args: days at week out of range");
		this.minDays = minDays;
		this.maxDays = maxDays;
	}

	public long getClubId() {
		return this.clubId;
	}

	public void setClubId(long club_id) {
		this.clubId = club_id;
	}

	/**
	 * Returns employeeId.
	 * 
	 * @return employeeId
	 */
	public long getEmployeeId() {
		return employeeId;
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
	public String getSecondName() {
		return this.secondName;
	}

	/**
	 * Sets a value to attribute sureName.
	 * 
	 * @param newSureName
	 */
	public void setSecondName(String newSureName) {
		this.secondName = newSureName;
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
		return this.minDays;
	}

	/**
	 * Sets a value to attribute min. Set it after {@link maxDays}.
	 * 
	 * @param newMin
	 */
	public void setMinDays(int minDays) {
		if (minDays < 0 || minDays > this.maxDays)
			throw new IllegalArgumentException(
					"Args: days at week out of range");
		this.minDays = minDays;
	}

	/**
	 * Returns max.
	 * 
	 * @return max
	 */
	public int getMaxDays() {
		return this.maxDays;
	}

	/**
	 * Sets a value to attribute max. Set it before {@link minDays}.
	 * 
	 * @param maxDays
	 */
	public void setMaxDays(int maxDays) {
		if (maxDays < 0 || maxDays > MAX_DAYS || maxDays < this.minDays)
			throw new IllegalArgumentException(
					"Args: days at week out of range");
		this.maxDays = maxDays;
	}

	public void setMinAndMaxDays(int minDays, int maxDays) {
		if (minDays < 0 || minDays >= maxDays || maxDays < 0
				|| maxDays > MAX_DAYS)
			throw new IllegalArgumentException(
					"Args: days at week out of range");
		this.minDays = minDays;
		this.maxDays = maxDays;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime + (int) (employeeId ^ (employeeId >>> 32));
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

	public long getEmployeeGroupId() {
		return employeeGroupId;
	}

	public void setEmployeeGroupId(long employeeGroupId) {
		this.employeeGroupId = employeeGroupId;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String adress) {
		this.address = adress;
	}

	public String getPassportNumber() {
		return passportNumber;
	}

	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getPassportIssuedBy() {
		return passportIssuedBy;
	}

	public void setPassportIssuedBy(String passportIssuedBy) {
		this.passportIssuedBy = passportIssuedBy;
	}

}
