/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Date;

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
	
	private long ClubId;
	
	private long EmployeeGroupId;
	
	private long EmpEmployeegroupid;
	
	private Date Birthday;
	
	private String Adress;
	
	private String PassportNumber;
	
	private String IdNumber;
	
	private String CellPhone;
	
	private String WorkPhone;
	
	private String HomePhone;
	
	private String Email;
	
	private String Education;
	
	private String Notes;
	
	private String PassportIssuedBy;
	 

	/**
	 * Min hours at week.
	 */
	private int MinDays = 0;

	/**
	 * Max hours at week.
	 */
	private int MaxDays = 0;

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
		this.MinDays = min;
		this.MaxDays = max;
	}
	
	public long getClubId()
	{
		return this.ClubId;
	}
	public void setClubId(long club_id)
	{
		this.ClubId = club_id;
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
		return this.MinDays;
	}

	/**
	 * Sets a value to attribute min. Set it after {@link max}.
	 * 
	 * @param newMin
	 */
	public void setMin(int min) {
		if (min < 0 || min > this.MaxDays)
			throw new IllegalArgumentException(
					"Args: days at week out of range");
		this.MinDays = min;
	}

	/**
	 * Returns max.
	 * 
	 * @return max
	 */
	public int getMax() {
		return this.MaxDays;
	}

	/**
	 * Sets a value to attribute max. Set it before {@link min}.
	 * 
	 * @param max
	 */
	public void setMax(int max) {
		if (max < 0 || max > MAX_DAYS || max < this.MinDays)
			throw new IllegalArgumentException(
					"Args: days at week out of range");
		this.MaxDays = max;
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

	public long getEmployeeGroupId() {
		return EmployeeGroupId;
	}

	public void setEmployeeGroupId(long employeeGroupId) {
		EmployeeGroupId = employeeGroupId;
	}

	public long getEmpEmployeegroupid() {
		return EmpEmployeegroupid;
	}

	public void setEmpEmployeegroupid(long empEmployeegroupid) {
		EmpEmployeegroupid = empEmployeegroupid;
	}

	public Date getBirthday() {
		return Birthday;
	}

	public void setBirthday(Date birthday) {
		Birthday = birthday;
	}

	public String getAdress() {
		return Adress;
	}

	public void setAdress(String adress) {
		Adress = adress;
	}

	public String getPassportNumber() {
		return PassportNumber;
	}

	public void setPassportNumber(String passportNumber) {
		PassportNumber = passportNumber;
	}

	public String getIdNumber() {
		return IdNumber;
	}

	public void setIdNumber(String idNumber) {
		IdNumber = idNumber;
	}

	public String getCellPhone() {
		return CellPhone;
	}

	public void setCellPhone(String cellPhone) {
		CellPhone = cellPhone;
	}

	public String getWorkPhone() {
		return WorkPhone;
	}

	public void setWorkPhone(String workPhone) {
		WorkPhone = workPhone;
	}

	public String getHomePhone() {
		return HomePhone;
	}

	public void setHomePhone(String homePhone) {
		HomePhone = homePhone;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getEducation() {
		return Education;
	}

	public void setEducation(String education) {
		Education = education;
	}

	public String getNotes() {
		return Notes;
	}

	public void setNotes(String notes) {
		Notes = notes;
	}

	public String getPassportIssuedBy() {
		return PassportIssuedBy;
	}

	public void setPassportIssuedBy(String passportIssuedBy) {
		PassportIssuedBy = passportIssuedBy;
	}

	

}
