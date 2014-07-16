package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;

public class Role implements Serializable {
	private static final long serialVersionUID = 1L;

	private long roleId;
	private int rights;
	private String title;

	public Role() {
	}

	public Role(long roleId, int rights, String title) {
		this.roleId = roleId;
		this.rights = rights;
		this.title = title;
	}

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public int getRights() {
		return rights;
	}

	public void setRights(int rights) {
		this.rights = rights;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Role [roleId=");
		sb.append(roleId);
		sb.append(", title=");
		sb.append(title);
		sb.append("]");
		return sb.toString();
	}

}
