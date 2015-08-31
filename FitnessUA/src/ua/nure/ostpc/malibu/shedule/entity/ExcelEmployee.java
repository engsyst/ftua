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
	private List<Role> roles;

	public ExcelEmployee() {
	}

	public ExcelEmployee(Employee employee, List<Role> roles) {
		this.employee = employee;
		this.roles = roles;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + employee.hashCode();
		result = prime * result + roles.hashCode();
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
		if (!roles.equals(other.roles))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ExcelEmployee [employee=");
		sb.append(employee);
		sb.append(", roles=");
		sb.append(Arrays.toString(roles.toArray()));
		sb.append("]");
		return sb.toString();
	}
}
