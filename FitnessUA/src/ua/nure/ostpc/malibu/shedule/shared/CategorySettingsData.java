package ua.nure.ostpc.malibu.shedule.shared;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Category;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CategorySettingsData implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;
	private List<Category> categoryList;
	private Map<Long, String> employeeNameMap;

	public CategorySettingsData() {
	}

	public CategorySettingsData(List<Category> categoryList,
			Map<Long, String> employeeNameMap) {
		super();
		this.categoryList = categoryList;
		this.employeeNameMap = employeeNameMap;
	}

	public List<Category> getCategoryList() {
		return categoryList;
	}

	public Map<Long, String> getEmployeeNameMap() {
		return employeeNameMap;
	}

	public void setCategoryList(List<Category> categoryList) {
		this.categoryList = categoryList;
	}

	public void setEmployeeNameMap(Map<Long, String> employeeNameMap) {
		this.employeeNameMap = employeeNameMap;
	}
}
