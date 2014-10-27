package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ua.nure.ostpc.malibu.shedule.Const;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author engsyst
 * 
 */
public class Shift implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;

	private long shiftId;
	private long scheduleClubDayId;
	private int shiftNumber;
	private int quantityOfEmployees;
	private List<Employee> employees;

	public Shift() {
	}

	public Shift(long shiftId, long scheduleClubDayId, int shiftNumber,
			int quantityOfEmployees, List<Employee> employees) {
		this.shiftId = shiftId;
		this.scheduleClubDayId = scheduleClubDayId;
		this.shiftNumber = shiftNumber;
		this.quantityOfEmployees = quantityOfEmployees;
		this.employees = employees;
	}

	public boolean isEmpty() {
		return (employees != null && employees.size() > 0);
	}

	/**
	 * @return true if {@link Shift} is full, otherwise return false
	 */
	public boolean isFull() {

		return (employees != null && employees.size() == quantityOfEmployees);
	}

	public long getShiftId() {
		return shiftId;
	}

	public void setShiftId(long shiftId) {
		this.shiftId = shiftId;
	}

	public long getScheduleClubDayId() {
		return scheduleClubDayId;
	}

	public void setScheduleClubDayId(long scheduleClubDayId) {
		this.scheduleClubDayId = scheduleClubDayId;
	}

	public int getShiftNumber() {
		return shiftNumber;
	}

	public void setShiftNumber(int shiftNumber) {
		this.shiftNumber = shiftNumber;
	}

	public int getQuantityOfEmployees() {
		return quantityOfEmployees;
	}

	public void setQuantityOfEmployees(int quantityOfEmployees) {
		this.quantityOfEmployees = quantityOfEmployees;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	@Override
	public int hashCode() {
		return new Long(shiftId).hashCode();
	}

	 @Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		switch (Const.TO_STRING_MODE) {
		case info:
			sb.append("Shift [Id=");
			sb.append(shiftId);
			sb.append(", No=");
			sb.append(shiftNumber);
			sb.append(", qOfEmps=");
			sb.append(quantityOfEmployees);
			sb.append(", EmpsCount=");
			sb.append((employees == null) ? 0 : employees.size());
			sb.append("]");
			break;
		case normal:
			sb.append("Shift [Id=");
			sb.append(shiftId);
			sb.append(", No=");
			sb.append(shiftNumber);
			sb.append(", qOfEmps=");
			sb.append(quantityOfEmployees);
			sb.append(", EmpsCount=");
			sb.append((employees == null) ? 0 : employees.size());
			sb.append(", emps=");
			sb.append(employees);
			sb.append("]");
			break;
		case debug:
			sb.append("Shift [Id=");
			sb.append(shiftId);
			sb.append(", No=");
			sb.append(shiftNumber);
			sb.append(", qOfEmps=");
			sb.append(quantityOfEmployees);
			sb.append(", ClubDayId=");
			sb.append(scheduleClubDayId);
			sb.append(", EmpsCount=");
			sb.append((employees == null) ? 0 : employees.size());
			sb.append(", emps=");
			sb.append(employees);
			sb.append("]");
			break;
		case fullInfo:
		case fullNormal:
		case fullDebug:
			sb.append("Shift [Id=");
			sb.append(shiftId);
			sb.append(", No=");
			sb.append(shiftNumber);
			sb.append(", qOfEmps=");
			sb.append(quantityOfEmployees);
			sb.append(", ClubDayId=");
			sb.append(scheduleClubDayId);
			sb.append(", EmpsCount=");
			sb.append((employees == null) ? 0 : employees.size());
			sb.append(", emps=");
			sb.append(employees);
			sb.append("]");
			break;
		default:
			break;
		}
		return sb.toString();
	}

	/**
	 * Fill employees from emps to {@link Shift} end <b>remove</b> their from
	 * emps
	 * 
	 * @param emps
	 * @return true if Shift is full
	 * @see {@link isFull}
	 */
	public boolean addEmployees(List<Employee> emps) {
		if (isFull())
			return true;
		if (employees == null)
			employees = new ArrayList<Employee>();
		int countToAdd = quantityOfEmployees
				- (employees == null ? 0 : employees.size());
		if (countToAdd >= emps.size()) {
			countToAdd = emps.size();
		}
		employees.addAll(emps.subList(0, countToAdd));
		for (Employee e : employees)
			e.incAssignment();
		emps.subList(0, countToAdd).clear();
		return isFull();
	}

}
