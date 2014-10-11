package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;

public class Role implements Serializable {
	private static final long serialVersionUID = 1L;

	private long roleId;
	private Right right;
	private String title;

	public Role() {
	}

	public Role(long roleId, Right right, String title) {
		this.roleId = roleId;
		this.right = right;
		this.title = title;
	}

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public Right getRight() {
		return right;
	}

	public void setRight(Right right) {
		this.right = right;
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
		sb.append(", right=");
		sb.append(right);
		sb.append(", title=");
		sb.append(title);
		sb.append("]");
		return sb.toString();
	}

}
