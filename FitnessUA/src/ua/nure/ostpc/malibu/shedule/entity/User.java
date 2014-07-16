package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	private long userId;
	private long employeeId;
	private Role role;
	private String login;
	private String password;

	public User() {
	}

	public User(long userId, long employeeId, Role role, String login,
			String password) {
		this.userId = userId;
		this.employeeId = employeeId;
		this.role = role;
		this.login = login;
		this.password = password;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("User [userId=");
		sb.append(userId);
		sb.append(", employeeId=");
		sb.append(employeeId);
		sb.append(", roleId=");
		sb.append(role.getRoleId());
		sb.append(", login=");
		sb.append(login);
		sb.append("]");
		return sb.toString();
	}
}
