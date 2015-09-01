package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Excel employee.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class ExcelEmployee implements Serializable {
	private static final long serialVersionUID = 1L;

	private Employee employee;
	private List<Right> rights;

	public ExcelEmployee() {
	}

	public ExcelEmployee(Employee employee, List<Right> rights) {
		this.employee = employee;
		this.rights = rights;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public List<Right> getRights() {
		return rights;
	}

	public void setRights(List<Right> rights) {
		this.rights = rights;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + employee.hashCode();
		result = prime * result + rights.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExcelEmployee other = (ExcelEmployee) obj;
		if (!employee.equals(other.employee))
			return false;
		if (!rights.equals(other.rights))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ExcelEmployee [employee=");
		sb.append(employee);
		sb.append(", rights=");
		sb.append(Arrays.toString(rights.toArray()));
		sb.append("]");
		return sb.toString();
	}
}
