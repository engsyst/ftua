package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class UserWithEmployee implements Serializable, IsSerializable {
	private User user;
	private Employee emp;
	
	public Employee getEmp() {
		return emp;
	}

	public UserWithEmployee() {
	}

	public UserWithEmployee(User user, Employee emp) {
		super();
		this.user = user;
		this.emp = emp;
	}
	
	public void setEmp(Employee emp) {
		this.emp = emp;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}

	public Employee getEmployee() {
		return emp;
	}

}
