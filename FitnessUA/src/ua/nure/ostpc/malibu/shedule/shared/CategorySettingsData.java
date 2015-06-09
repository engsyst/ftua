package ua.nure.ostpc.malibu.shedule.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Category;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CategorySettingsData implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;
	private List<Category> categoryList;
	private Map<Long, String> notRemovedEmployeeNameMap;
	private Map<Long, String> removedEmployeeNameMap;

	public CategorySettingsData() {
	}

	public CategorySettingsData(List<Category> categoryList,
			Map<Long, String> notRemovedEmployeeNameMap,
			Map<Long, String> removedEmployeeNameMap) {
		super();
		this.categoryList = categoryList;
		this.notRemovedEmployeeNameMap = notRemovedEmployeeNameMap;
		this.removedEmployeeNameMap = removedEmployeeNameMap;
	}

	public List<Category> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<Category> categoryList) {
		this.categoryList = categoryList;
	}

	public Map<Long, String> getNotRemovedEmployeeNameMap() {
		return notRemovedEmployeeNameMap;
	}

	public void setNotRemovedEmployeeNameMap(
			Map<Long, String> notRemovedEmployeeNameMap) {
		this.notRemovedEmployeeNameMap = notRemovedEmployeeNameMap;
	}

	public Map<Long, String> getRemovedEmployeeNameMap() {
		return removedEmployeeNameMap;
	}

	public void setRemovedEmployeeNameMap(
			Map<Long, String> removedEmployeeNameMap) {
		this.removedEmployeeNameMap = removedEmployeeNameMap;
	}

	public Map<Long, String> getAllEmployeeNameMap() {
		Map<Long, String> allEmployeeNameMap = new HashMap<Long, String>();
		allEmployeeNameMap.putAll(notRemovedEmployeeNameMap);
		allEmployeeNameMap.putAll(removedEmployeeNameMap);
		return allEmployeeNameMap;
	}
}
