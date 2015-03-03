package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class EmployeeSettingsData implements Serializable, IsSerializable {
	private Employee inEmployee;
	private Employee outEmployee;
	private User user;
	private List<Role> roles;
	private int row;

	public EmployeeSettingsData() {
		super();
	}
	
	public EmployeeSettingsData(Employee inEmployee, Employee outEmployee, User user) {
		super();
		this.inEmployee = inEmployee;
		this.outEmployee = outEmployee;
		this.user = user;
	}
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Employee getInEmployee() {
		return inEmployee;
	}

	public void setInEmployee(Employee inEmployee) {
		this.inEmployee = inEmployee;
	}

	public Employee getOutEmployee() {
		return outEmployee;
	}

	public void setOutEmployee(Employee outEmployee) {
		this.outEmployee = outEmployee;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((inEmployee == null) ? 0 : inEmployee.hashCode());
		result = prime * result
				+ ((outEmployee == null) ? 0 : outEmployee.hashCode());
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
		EmployeeSettingsData other = (EmployeeSettingsData) obj;
		if (inEmployee == null) {
			if (other.inEmployee != null)
				return false;
		} else if (!inEmployee.equals(other.inEmployee))
			return false;
		if (outEmployee == null) {
			if (other.outEmployee != null)
				return false;
		} else if (!outEmployee.equals(other.outEmployee))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmployeeSettingsData [");
		if (inEmployee != null) {
			builder.append("inEmployee=");
			builder.append(inEmployee);
			builder.append(", ");
		}
		if (outEmployee != null) {
			builder.append("outEmployee=");
			builder.append(outEmployee);
			builder.append(", ");
		}
		if (user != null) {
			builder.append("user=");
			builder.append(user);
		}
		builder.append("]");
		return builder.toString();
	}
}
