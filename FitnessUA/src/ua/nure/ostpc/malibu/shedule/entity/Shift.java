package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Shift implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;

	private long shiftId;
	private int shiftNumber;
	private int quantityOfEmployees;
	private List<Employee> employees;

	public Shift() {
	}

	public Shift(long shiftId, int shiftNumber, int quantityOfEmployees,
			List<Employee> employees) {
		this.shiftId = shiftId;
		this.shiftNumber = shiftNumber;
		this.quantityOfEmployees = quantityOfEmployees;
		this.employees = employees;
	}

	public long getShiftId() {
		return shiftId;
	}

	public void setShiftId(long shiftId) {
		this.shiftId = shiftId;
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
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Shift [shiftId=");
		sb.append(shiftId);
		sb.append(", shiftNumber=");
		sb.append(shiftNumber);
		sb.append(", quantityOfEmployees=");
		sb.append(quantityOfEmployees);
		sb.append("]");
		return sb.toString();
	}
}
