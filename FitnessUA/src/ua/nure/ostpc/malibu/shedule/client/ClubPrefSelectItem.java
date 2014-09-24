package ua.nure.ostpc.malibu.shedule.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.entity.Category;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;

/**
 * Club preference select item.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class ClubPrefSelectItem extends SelectItem {
	private static Map<Long, List<ClubPrefSelectItem>> selectItemMap = new HashMap<Long, List<ClubPrefSelectItem>>();
	private static Map<Long, HashSet<String>> prevValueSetMap = new HashMap<Long, HashSet<String>>();
	private static List<Category> categoryList = new ArrayList<Category>();

	public ClubPrefSelectItem(Long clubId,
			LinkedHashMap<String, String> valueMap) {
		setTextBoxStyle("item");
		setMultiple(true);
		setShowTitle(false);
		setMultipleAppearance(MultipleAppearance.PICKLIST);
		setValueMap(valueMap);
		setTitle(String.valueOf(clubId));
		addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				ClubPrefSelectItem clubPrefSelectItem = (ClubPrefSelectItem) event
						.getSource();
				long clubId = Long.parseLong(clubPrefSelectItem.getTitle());
				HashSet<String> prevValueSet = null;
				if (prevValueSetMap.containsKey(clubId)) {
					prevValueSet = prevValueSetMap.get(clubId);
				} else {
					prevValueSet = new HashSet<String>();
					prevValueSetMap.put(clubId, prevValueSet);
				}
				HashSet<String> valueSet = null;
				if (event.getValue() != null) {
					valueSet = new HashSet<String>(Arrays.asList(event
							.getValue().toString().split(",")));
				} else {
					valueSet = new HashSet<String>();
				}
				HashSet<String> newValueSet = null;
				if (valueSet.size() > prevValueSet.size()) {
					valueSet.removeAll(prevValueSet);
					String newValue = valueSet.iterator().next();
					newValueSet = prevValueSet;
					newValueSet.add(newValue);
					if (newValue.endsWith(AppConstants.CATEGORY_MARKER)) {
						long categoryId = Long.parseLong(newValue.substring(0,
								newValue.length() - 1));
						for (Category category : categoryList) {
							if (category.getCategoryId() == categoryId) {
								for (Long employeeId : category
										.getEmployeeIdList()) {
									newValueSet.add(employeeId
											+ AppConstants.EMPLOYEE_MARKER);
								}
							}
						}
					}
				} else {
					prevValueSet.removeAll(valueSet);
					String oldValue = prevValueSet.iterator().next();
					newValueSet = valueSet;
					if (oldValue.endsWith(AppConstants.CATEGORY_MARKER)) {
						long categoryId = Long.parseLong(oldValue.substring(0,
								oldValue.length() - 1));
						for (Category category : categoryList) {
							if (category.getCategoryId() == categoryId) {
								for (Long employeeId : category
										.getEmployeeIdList()) {
									newValueSet.remove(employeeId
											+ AppConstants.EMPLOYEE_MARKER);
								}
							}
						}
					}
				}
				correctValueSet(newValueSet);
				prevValueSet = newValueSet;
				List<ClubPrefSelectItem> selectItemList = selectItemMap
						.get(clubId);
				if (selectItemList != null) {
					String[] newValueArray = new String[newValueSet.size()];
					int i = 0;
					for (String value : newValueSet) {
						newValueArray[i] = value;
						i++;
					}
					for (ClubPrefSelectItem selectItem : selectItemList) {
						selectItem.setValues(newValueArray);
					}
				}
			}
		});
	}

	public static List<Category> getCategoryList() {
		return categoryList;
	}

	public static void setCategoryList(List<Category> categoryList) {
		ClubPrefSelectItem.categoryList = categoryList;
	}

	public static void addClubPrefSelectItem(
			ClubPrefSelectItem clubPrefSelectItem) {
		long clubId = Long.parseLong(clubPrefSelectItem.getTitle());
		if (!selectItemMap.containsKey(clubId)) {
			selectItemMap.put(clubId, new ArrayList<ClubPrefSelectItem>());
		}
		selectItemMap.get(clubId).add(clubPrefSelectItem);
	}

	public static LinkedHashMap<String, String> getValueMap(
			List<Employee> employeeList, List<Category> categoryList) {
		LinkedHashMap<String, String> categoryMap = new LinkedHashMap<String, String>();
		for (Category category : categoryList) {
			categoryMap.put(String.valueOf(category.getCategoryId())
					+ AppConstants.CATEGORY_MARKER, "<" + category.getTitle()
					+ ">");
		}
		LinkedHashMap<String, String> employeeMap = new LinkedHashMap<String, String>();
		for (Employee employee : employeeList) {
			employeeMap.put(String.valueOf(employee.getEmployeeId())
					+ AppConstants.EMPLOYEE_MARKER,
					employee.getNameForSchedule());
		}
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		valueMap.putAll(categoryMap);
		valueMap.putAll(employeeMap);
		return valueMap;
	}

	private static void correctValueSet(Set<String> valueSet) {
		for (Category category : categoryList) {
			boolean result = true;
			for (Long employeeId : category.getEmployeeIdList()) {
				if (!valueSet.contains(employeeId
						+ AppConstants.EMPLOYEE_MARKER)) {
					result = false;
					break;
				}
			}
			if (result) {
				valueSet.add(category.getCategoryId()
						+ AppConstants.CATEGORY_MARKER);
			} else {
				valueSet.remove(category.getCategoryId()
						+ AppConstants.CATEGORY_MARKER);
			}
		}
	}
}
