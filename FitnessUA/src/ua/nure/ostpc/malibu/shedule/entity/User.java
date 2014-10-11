package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class User implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;

	private long userId;
	private long employeeId;
	private List<Role> roles;
	private String login;
	private String password;

	public User() {
	}

	public User(long userId, long employeeId, List<Role> roles, String login,
			String password) {
		this.userId = userId;
		this.employeeId = employeeId;
		this.roles = roles;
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

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
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
	public int hashCode() {
		return new Long(userId).hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("User [userId=");
		sb.append(userId);
		sb.append(", employeeId=");
		sb.append(employeeId);
		sb.append(", login=");
		sb.append(login);
		sb.append(", roles=");
		sb.append(Arrays.toString(roles.toArray()));
		sb.append("]");
		return sb.toString();
	}
}
