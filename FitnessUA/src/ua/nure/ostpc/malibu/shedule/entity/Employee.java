/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import ua.nure.ostpc.malibu.shedule.Const;
import ua.nure.ostpc.malibu.shedule.excel.ExcelConstants;
import ua.nure.ostpc.malibu.shedule.excel.XlsField;
import ua.nure.ostpc.malibu.shedule.excel.XlsGetter;
import ua.nure.ostpc.malibu.shedule.excel.XlsSetter;

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
	@XlsField(name = ExcelConstants.EXCEL_FIELD_FIRST_NAME)
	private String firstName;
	@XlsField(name = ExcelConstants.EXCEL_FIELD_SECOND_NAME)
	private String secondName;
	@XlsField(name = ExcelConstants.EXCEL_FIELD_LAST_NAME)
	private String lastName;
	@XlsField(name = ExcelConstants.EXCEL_FIELD_BIRTHDAY)
	private Date birthday;
	@XlsField(name = ExcelConstants.EXCEL_FIELD_ADDRESS)
	private String address;
	@XlsField(name = ExcelConstants.EXCEL_FIELD_PASSPORT_NUMBER)
	private String passportNumber;
	@XlsField(name = ExcelConstants.EXCEL_FIELD_ID_NUMBER)
	private String idNumber;
	@XlsField(name = ExcelConstants.EXCEL_FIELD_CELL_PHONE)
	private String cellPhone;
	@XlsField(name = ExcelConstants.EXCEL_FIELD_WORK_PHONE)
	private String workPhone;
	@XlsField(name = ExcelConstants.EXCEL_FIELD_HOME_PHONE)
	private String homePhone;
	@XlsField(name = ExcelConstants.EXCEL_FIELD_EMAIL)
	private String email;
	@XlsField(name = ExcelConstants.EXCEL_FIELD_EDUCATION)
	private String education;
	@XlsField(name = ExcelConstants.EXCEL_FIELD_NOTES)
	private String notes;
	@XlsField(name = ExcelConstants.EXCEL_FIELD_PASSPORT_ISSUED_BY)
	private String passportIssuedBy;

	private boolean isDeleted;

	/**
	 * Min hours at week.
	 */
	@XlsField(name = ExcelConstants.EXCEL_FIELD_MIN_DAYS)
	private int minDays;

	/**
	 * Max hours at week.
	 */
	@XlsField(name = ExcelConstants.EXCEL_FIELD_MAX_DAYS)
	private int maxDays;

	/**
	 * Real assignments to current schedule
	 */
	private transient TreeMap<Date, Integer> assigns;

	private transient double objectiveValue;

	public double getObjectiveValue() {
		return objectiveValue;
	}

	public void setObjectiveValue(double objectiveValue) {
		this.objectiveValue = objectiveValue;
	}

	public static Comparator<Date> dateComparator = new Comparator<Date>() {

		@Override
		public int compare(Date o1, Date o2) {
			if (o1 == null)
				return -1;
			if (o2 == null)
				return 1;
			return o2.compareTo(o1);
		}
	};

	public Employee() {
	}

	public Employee(String firstName, String sureName, String lastName,
			int minDays, int maxDays) {
		this.firstName = firstName;
		this.secondName = sureName;
		this.lastName = lastName;
		setEmpPrefs(minDays, maxDays);
	}

	public String getShortName() {
		return lastName + " " + firstName.charAt(0) + "."
				+ secondName.charAt(0);
	}

	public int getAllAssignments() {
		if (assigns == null)
			return 0;
		int count = 0;
		Set<Entry<Date, Integer>> entries = assigns.entrySet();
		Iterator<Entry<Date, Integer>> it = entries.iterator();
		while (it.hasNext()) {
			Entry<Date, Integer> entry = (Entry<Date, Integer>) it.next();
			count += entry.getValue();
		}
		return count;
	}

	public int getLastAssignments(Date end) {
		if (assigns == null)
			return 0;
		int count = 0;
		Iterator<Entry<Date, Integer>> it = assigns.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Date, Integer> entry = (Map.Entry<Date, Integer>) it.next();
			if (entry.getKey().equals(end)) {
				count += entry.getValue();
				continue;
			}
			if (entry.getKey().before(end)) {
				if (entry.getValue() == 0)
					return count;
				count += entry.getValue();
			}
		}
		return count;
	}

	public int getAssignments(Date start, Date end) {
		if (assigns == null)
			return 0;
		if (start.compareTo(end) > 0)
			return 0;
		int count = 0;
		Set<Entry<Date, Integer>> entries = assigns.entrySet();
		Iterator<Entry<Date, Integer>> it = entries.iterator();
		while (it.hasNext()) {
			Entry<Date, Integer> entry = (Entry<Date, Integer>) it.next();
			if ((entry.getKey().after(start) || entry.getKey().equals(start))
					&& (entry.getKey().before(end) || entry.getKey()
							.equals(end)))
				count += entry.getValue();
		}
		return count;
	}

	public void clearAssignments() {
		if (assigns != null)
			assigns.clear();
	}

	/**
	 * Used only in schedule autogenerator
	 * 
	 * @param d
	 * @param count
	 * @return
	 */
	public int addAssignment(Date d, int count) {
		if (assigns == null)
			assigns = new TreeMap<Date, Integer>(dateComparator);
		Integer old = assigns.put(d, count);
		return old == null ? 0 : old;
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

	public long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(long newEmployeeId) {
		this.employeeId = newEmployeeId;
	}

	@XlsGetter(fieldName = ExcelConstants.EXCEL_FIELD_FIRST_NAME)
	public String getFirstName() {
		return this.firstName;
	}

	@XlsSetter(fieldName = ExcelConstants.EXCEL_FIELD_FIRST_NAME)
	public void setFirstName(String newFirstName) {
		this.firstName = newFirstName;
	}

	@XlsGetter(fieldName = ExcelConstants.EXCEL_FIELD_SECOND_NAME)
	public String getSecondName() {
		return this.secondName;
	}

	@XlsSetter(fieldName = ExcelConstants.EXCEL_FIELD_SECOND_NAME)
	public void setSecondName(String newSureName) {
		this.secondName = newSureName;
	}

	@XlsGetter(fieldName = ExcelConstants.EXCEL_FIELD_LAST_NAME)
	public String getLastName() {
		return this.lastName;
	}

	@XlsSetter(fieldName = ExcelConstants.EXCEL_FIELD_LAST_NAME)
	public void setLastName(String newLastName) {
		this.lastName = newLastName;
	}

	@XlsGetter(fieldName = ExcelConstants.EXCEL_FIELD_MIN_DAYS)
	public int getMinDays() {
		return this.minDays;
	}

	/**
	 * Sets a value to attribute min. Set it after {@link maxDays}.
	 * 
	 * @param newMin
	 */
	@XlsSetter(fieldName = ExcelConstants.EXCEL_FIELD_MIN_DAYS)
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
	@XlsGetter(fieldName = ExcelConstants.EXCEL_FIELD_MAX_DAYS)
	public int getMaxDays() {
		return this.maxDays;
	}

	/**
	 * Sets a value to attribute max. Set it before {@link minDays}.
	 * 
	 * @param maxDays
	 */
	@XlsSetter(fieldName = ExcelConstants.EXCEL_FIELD_MAX_DAYS)
	public void setMaxDays(int maxDays) {
		if (maxDays < 0 || maxDays > MAX_DAYS || maxDays < this.minDays)
			throw new IllegalArgumentException(
					"Args: days at week out of range");
		this.maxDays = maxDays;
	}

	public void setMinAndMaxDays(int minDays, int maxDays) {
		if (minDays < 0 || minDays > maxDays || maxDays < 0
				|| maxDays > MAX_DAYS)
			throw new IllegalArgumentException(
					"Args: days at week out of range");
		this.minDays = minDays;
		this.maxDays = maxDays;
	}

	@XlsGetter(fieldName = ExcelConstants.EXCEL_FIELD_BIRTHDAY)
	public Date getBirthday() {
		return birthday;
	}

	@XlsSetter(fieldName = ExcelConstants.EXCEL_FIELD_BIRTHDAY)
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	@XlsGetter(fieldName = ExcelConstants.EXCEL_FIELD_ADDRESS)
	public String getAddress() {
		return address;
	}

	@XlsSetter(fieldName = ExcelConstants.EXCEL_FIELD_ADDRESS)
	public void setAddress(String adress) {
		this.address = adress;
	}

	@XlsGetter(fieldName = ExcelConstants.EXCEL_FIELD_PASSPORT_NUMBER)
	public String getPassportNumber() {
		return passportNumber;
	}

	@XlsSetter(fieldName = ExcelConstants.EXCEL_FIELD_PASSPORT_NUMBER)
	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}

	@XlsGetter(fieldName = ExcelConstants.EXCEL_FIELD_ID_NUMBER)
	public String getIdNumber() {
		return idNumber;
	}

	@XlsSetter(fieldName = ExcelConstants.EXCEL_FIELD_ID_NUMBER)
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	@XlsGetter(fieldName = ExcelConstants.EXCEL_FIELD_CELL_PHONE)
	public String getCellPhone() {
		return cellPhone;
	}

	@XlsSetter(fieldName = ExcelConstants.EXCEL_FIELD_CELL_PHONE)
	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	@XlsGetter(fieldName = ExcelConstants.EXCEL_FIELD_WORK_PHONE)
	public String getWorkPhone() {
		return workPhone;
	}

	@XlsSetter(fieldName = ExcelConstants.EXCEL_FIELD_WORK_PHONE)
	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	@XlsGetter(fieldName = ExcelConstants.EXCEL_FIELD_HOME_PHONE)
	public String getHomePhone() {
		return homePhone;
	}

	@XlsSetter(fieldName = ExcelConstants.EXCEL_FIELD_HOME_PHONE)
	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	@XlsGetter(fieldName = ExcelConstants.EXCEL_FIELD_EMAIL)
	public String getEmail() {
		return email;
	}

	@XlsSetter(fieldName = ExcelConstants.EXCEL_FIELD_EMAIL)
	public void setEmail(String email) {
		this.email = email;
	}

	@XlsGetter(fieldName = ExcelConstants.EXCEL_FIELD_EDUCATION)
	public String getEducation() {
		return education;
	}

	@XlsSetter(fieldName = ExcelConstants.EXCEL_FIELD_EDUCATION)
	public void setEducation(String education) {
		this.education = education;
	}

	@XlsGetter(fieldName = ExcelConstants.EXCEL_FIELD_NOTES)
	public String getNotes() {
		return notes;
	}

	@XlsSetter(fieldName = ExcelConstants.EXCEL_FIELD_NOTES)
	public void setNotes(String notes) {
		this.notes = notes;
	}

	@XlsGetter(fieldName = ExcelConstants.EXCEL_FIELD_PASSPORT_ISSUED_BY)
	public String getPassportIssuedBy() {
		return passportIssuedBy;
	}

	@XlsSetter(fieldName = ExcelConstants.EXCEL_FIELD_PASSPORT_ISSUED_BY)
	public void setPassportIssuedBy(String passportIssuedBy) {
		this.passportIssuedBy = passportIssuedBy;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
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
		return employeeId == other.employeeId
				&& firstName.equals(other.firstName)
				&& secondName.equals(other.secondName)
				&& lastName.equals(other.lastName)
				&& birthday.equals(other.birthday)
				&& address.equals(other.address)
				&& passportNumber.equals(other.passportNumber)
				&& idNumber.equals(other.idNumber)
				&& cellPhone.equals(other.cellPhone)
				&& workPhone.equals(other.workPhone)
				&& homePhone.equals(other.homePhone)
				&& email.equals(other.email)
				&& education.equals(other.education)
				&& notes.equals(other.notes)
				&& passportIssuedBy.equals(other.passportIssuedBy);
	}

	@Override
	public int compareTo(Employee o) {
		return this.lastName.compareTo(o.lastName);
	}

	public String getNameForSchedule() {
		StringBuilder sb = new StringBuilder();
		sb.append(lastName);
		sb.append(" ");
		sb.append(firstName.charAt(0));
		sb.append(".");
		sb.append(secondName.charAt(0));
		sb.append(".");
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		switch (Const.TO_STRING_MODE) {
		case info:
			builder.append("Employee [Id=");
			builder.append(employeeId);
			builder.append(", Last name=");
			builder.append(lastName);
			builder.append("]");
			break;
		case normal:
		case debug:
			builder.append("Employee [Id=");
			builder.append(employeeId);
			builder.append(", min=");
			builder.append(minDays);
			builder.append(", max=");
			builder.append(maxDays);
			builder.append(", Last name=");
			builder.append(lastName);
			builder.append(", objective=");
			builder.append(objectiveValue);
			builder.append("]");
			break;
		case fullInfo:
		case fullNormal:
		case fullDebug:
			builder.append("Employee [Id=");
			builder.append(employeeId);
			builder.append(", minDays=");
			builder.append(minDays);
			builder.append(", maxDays=");
			builder.append(maxDays);
			builder.append(", lName=");
			builder.append(lastName);
			builder.append(", fName=");
			builder.append(firstName);
			builder.append(", sName=");
			builder.append(secondName);
			builder.append(", birthday=");
			builder.append(birthday);
			builder.append(", address=");
			builder.append(address);
			builder.append(", passp=");
			builder.append(passportNumber);
			builder.append(", INN=");
			builder.append(idNumber);
			builder.append(", cellPhone=");
			builder.append(cellPhone);
			builder.append(", workPhone=");
			builder.append(workPhone);
			builder.append(", homePhone=");
			builder.append(homePhone);
			builder.append(", email=");
			builder.append(email);
			builder.append(", education=");
			builder.append(education);
			builder.append(", notes=");
			builder.append(notes);
			builder.append(", passportIssuedBy=");
			builder.append(passportIssuedBy);
			builder.append(", isDeleted=");
			builder.append(isDeleted);
			builder.append("]");
			break;
		default:
			break;
		}
		return builder.toString();
	}

}
