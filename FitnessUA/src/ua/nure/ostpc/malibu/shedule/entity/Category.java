package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Category implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;

	private long categoryId;
	private String title;
	private List<Long> employeeIdList;

	public Category() {
	}

	public Category(long categoryId, String title, List<Long> employeeIdList) {
		this.categoryId = categoryId;
		this.title = title;
		this.employeeIdList = employeeIdList;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Collection<Long> getEmployeeIdList() {
		return employeeIdList;
	}

	public void setEmployeeIdList(List<Long> employeeIdList) {
		this.employeeIdList = employeeIdList;
	}

	@Override
	public int hashCode() {
		return new Long(categoryId).hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Category [categoryId=");
		sb.append(categoryId);
		sb.append(", title=");
		sb.append(title);
		sb.append("]");
		return sb.toString();
	}
}
