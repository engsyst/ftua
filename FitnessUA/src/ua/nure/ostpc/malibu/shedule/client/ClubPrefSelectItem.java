package ua.nure.ostpc.malibu.shedule.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	private static List<String> prevValueList = new ArrayList<String>();
	private static List<Category> categoryList = new ArrayList<Category>();

	public ClubPrefSelectItem(Long clubId,
			LinkedHashMap<String, String> valueMap) {
		super(AppConstants.KEY);
		setTextBoxStyle("item");
		setMultiple(true);
		setShowTitle(false);
		setMultipleAppearance(MultipleAppearance.PICKLIST);
		setValueMap(valueMap);
		setTitle(String.valueOf(clubId));
		addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				List<String> valueList = new ArrayList<String>(
						Arrays.asList(event.getValue().toString().split(",")));
				List<String> newValueList = null;
				if (valueList.size() > prevValueList.size()) {
					valueList.removeAll(prevValueList);
					String newValue = valueList.get(0);
					newValueList = prevValueList;
					newValueList.add(newValue);
					if (newValue.endsWith(AppConstants.CATEGORY_MARKER)) {
						long categoryId = Long.parseLong(newValue.substring(0,
								newValue.length() - 1));
						for (Category category : categoryList) {
							if (category.getCategoryId() == categoryId) {
								for (Long employeeId : category
										.getEmployeeIdList()) {
									newValueList.add(employeeId
											+ AppConstants.EMPLOYEE_MARKER);
								}
							}
						}
					}
				} else {
					prevValueList.removeAll(valueList);
					String oldValue = prevValueList.get(0);
					newValueList = valueList;
					if (oldValue.endsWith(AppConstants.CATEGORY_MARKER)) {
						long categoryId = Long.parseLong(oldValue.substring(0,
								oldValue.length() - 1));
						for (Category category : categoryList) {
							if (category.getCategoryId() == categoryId) {
								for (Long employeeId : category
										.getEmployeeIdList()) {
									newValueList.remove(employeeId
											+ AppConstants.EMPLOYEE_MARKER);
								}
							}
						}
					}
				}
				correctValueList(newValueList);
				prevValueList = newValueList;
				ClubPrefSelectItem clubPrefSelectItem = (ClubPrefSelectItem) event
						.getSource();
				long clubId = Long.parseLong(clubPrefSelectItem.getTitle());
				List<ClubPrefSelectItem> selectItemList = selectItemMap
						.get(clubId);
				if (selectItemList != null) {
					for (ClubPrefSelectItem selectItem : selectItemList) {
						selectItem.setValues(newValueList
								.toArray(new String[] {}));
					}
				}
			}
		});
	}

	private static void correctValueList(List<String> valueList) {
		// for(String)
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
}
