package ua.nure.ostpc.malibu.shedule.client.panel.editing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.entity.Category;
import ua.nure.ostpc.malibu.shedule.entity.ClubPref;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ListBox;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;

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
	private static boolean hasChanges;

	/**
	 * Declares as true on select item on which value has been selected. False
	 * if on all certain select items value has been changed.
	 */
	private static boolean isValueChangePropagation;

	private long clubId;

	public ClubPrefSelectItem(Long clubId) {
		setOptionDataSource(ClubPrefDataSource.getInstance());
		setValueField(AppConstants.DATA_SOURCE_CLUB_PREF_ID);
		setDisplayField(AppConstants.DATA_SOURCE_CLUB_PREF_NAME);

		ListGrid pickListProperties = new ListGrid();
		pickListProperties.setShowFilterEditor(true);
		setPickListProperties(pickListProperties);

		ListGridField nameField = new ListGridField(
				AppConstants.DATA_SOURCE_CLUB_PREF_NAME);
		setPickListFields(nameField);

		setTextBoxStyle("item");
		setMultiple(true);
		setShowTitle(false);
		setMultipleAppearance(MultipleAppearance.PICKLIST);
		this.clubId = clubId;

		addChangedHandler(new ClubPrefChangedHandler());
	}

	public long getClubId() {
		return clubId;
	}

	public void setClubId(long clubId) {
		this.clubId = clubId;
	}

	public static void setCategoryList(List<Category> categoryList) {
		ClubPrefSelectItem.categoryList = categoryList;
	}

	public static List<Category> getCategoryList() {
		return categoryList;
	}

	public static boolean hasChanges() {
		return hasChanges;
	}

	public static void setHasChanges(boolean hasChanges) {
		ClubPrefSelectItem.hasChanges = hasChanges;
	}

	public static void addClubPrefSelectItem(
			ClubPrefSelectItem clubPrefSelectItem) {
		long clubId = clubPrefSelectItem.getClubId();
		if (!selectItemMap.containsKey(clubId)) {
			selectItemMap.put(clubId, new ArrayList<ClubPrefSelectItem>());
		}
		selectItemMap.get(clubId).add(clubPrefSelectItem);
	}

	public static LinkedHashMap<String, String> getValueMap(
			List<Employee> employeeList, List<Category> categoryList) {
		LinkedHashMap<String, String> categoryMap = new LinkedHashMap<String, String>();
		if (categoryList != null)
			for (Category category : categoryList) {
				categoryMap.put(String.valueOf(category.getCategoryId())
						+ AppConstants.CATEGORY_MARKER,
						"&lt" + category.getTitle() + "&gt");
			}
		LinkedHashMap<String, String> employeeMap = new LinkedHashMap<String, String>();
		if (employeeList != null)
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

	public static List<ClubPref> getClubPrefs() {
		List<ClubPref> clubPrefs = new ArrayList<ClubPref>();
		Iterator<Entry<Long, HashSet<String>>> it = prevValueSetMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<Long, HashSet<String>> entry = it.next();
			long clubId = entry.getKey();
			for (String idString : entry.getValue()) {
				if (idString.endsWith(AppConstants.EMPLOYEE_MARKER)) {
					long employeeId = Long.parseLong(idString.substring(0,
							idString.length() - 1));
					ClubPref clubPref = new ClubPref();
					clubPref.setClubId(clubId);
					clubPref.setEmployeeId(employeeId);
					clubPrefs.add(clubPref);
				}
			}
		}
		return clubPrefs;
	}

	public static void setClubPrefs(List<ClubPref> clubPrefs) {
		if (clubPrefs != null) {
			Set<Long> clubIdSet = new HashSet<Long>();
			for (ClubPref clubPref : clubPrefs) {
				long clubId = clubPref.getClubId();
				clubIdSet.add(clubId);
				if (!prevValueSetMap.containsKey(clubId)) {
					HashSet<String> valueSet = new HashSet<String>();
					prevValueSetMap.put(clubId, valueSet);
				}
				HashSet<String> valueSet = prevValueSetMap.get(clubId);
				valueSet.add(clubPref.getEmployeeId()
						+ AppConstants.EMPLOYEE_MARKER);
			}
			for (Long clubId : clubIdSet) {
				HashSet<String> valueSet = prevValueSetMap.get(clubId);
				correctCategoriesInValueSet(valueSet);
			}
			Iterator<Entry<Long, HashSet<String>>> it = prevValueSetMap
					.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Long, HashSet<String>> entry = it.next();
				long clubId = entry.getKey();
				HashSet<String> newValueSet = entry.getValue();
				setNewValueInAllItems(clubId, newValueSet);
			}
		}
	}

	public static void removeData() {
		selectItemMap = new HashMap<Long, List<ClubPrefSelectItem>>();
		prevValueSetMap = new HashMap<Long, HashSet<String>>();
		categoryList = new ArrayList<Category>();
	}

	private static void setNewValueInAllItems(long clubId,
			HashSet<String> newValueSet) {
		List<ClubPrefSelectItem> selectItemList = selectItemMap.get(clubId);
		if (selectItemList != null) {
			String[] newValueArray = newValueSet.toArray(new String[newValueSet
					.size()]);
			for (ClubPrefSelectItem selectItem : selectItemList) {
				selectItem.setValues(newValueArray);
				selectItem.fetchData();
			}
		}
	}

	private static void correctCategoriesInValueSet(Set<String> valueSet) {
		for (Category category : categoryList) {
			boolean result = true;
			if (!category.getEmployeeIdList().isEmpty()) {
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

	public static void disableAll() {
		Iterator<Entry<Long, List<ClubPrefSelectItem>>> it = selectItemMap
				.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Long, List<ClubPrefSelectItem>> entry = it.next();
			List<ClubPrefSelectItem> list = entry.getValue();
			if (list != null) {
				for (ClubPrefSelectItem clubPrefSelectItem : list) {
					clubPrefSelectItem.disable();
				}
			}
		}
	}

	public static void toView() {
		Iterator<Entry<Long, List<ClubPrefSelectItem>>> it = selectItemMap
				.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Long, List<ClubPrefSelectItem>> entry = it.next();
			List<ClubPrefSelectItem> list = entry.getValue();
			if (list != null) {
				for (ClubPrefSelectItem clubPrefSelectItem : list) {
					DynamicForm form = clubPrefSelectItem.getForm();
					AbsolutePanel panel = (AbsolutePanel) form.getParent();
					panel.remove(form);
					ListBox listBox = new ListBox();
					listBox.setStyleName("selectItem");
					HashSet<String> valueSet = prevValueSetMap.get(entry
							.getKey());
					if (valueSet != null) {
						for (String value : valueSet) {
							String itemStr = ClubPrefDataSource.getValueMap()
									.get(value);
							if (value.endsWith(AppConstants.CATEGORY_MARKER)) {
								itemStr = "<"
										+ itemStr.substring(3,
												itemStr.length() - 3) + ">";
							}
							listBox.addItem(itemStr);
						}
					}
					panel.add(listBox, 5, 20);
				}
			}
		}
	}

	private class ClubPrefChangedHandler implements ChangedHandler {

		@Override
		public void onChanged(ChangedEvent event) {
			if (!isValueChangePropagation) {
				Object eventValue = event.getValue();
				HashSet<String> newValueSet = processValue(eventValue);
				prevValueSetMap.put(clubId, newValueSet);
				isValueChangePropagation = true;
				setNewValueInAllItems(clubId, newValueSet);
				isValueChangePropagation = false;
				hasChanges = true;
			}
		}

		private HashSet<String> processValue(Object eventValue) {
			HashSet<String> prevValueSet = null;
			if (prevValueSetMap.containsKey(clubId)) {
				prevValueSet = prevValueSetMap.get(clubId);
			} else {
				prevValueSet = new HashSet<String>();
				prevValueSetMap.put(clubId, prevValueSet);
			}
			HashSet<String> valueSet = null;
			if (eventValue != null) {
				valueSet = new HashSet<String>(Arrays.asList(eventValue
						.toString().split(",")));
			} else {
				valueSet = new HashSet<String>();
			}
			HashSet<String> newValueSet = new HashSet<String>();
			if (valueSet.size() >= prevValueSet.size()) {
				valueSet.removeAll(prevValueSet);
				String newValue = valueSet.iterator().next();
				newValueSet.addAll(prevValueSet);
				newValueSet.add(newValue);
				addCategoryEmpToValueSet(newValueSet, new String[] { newValue });
			} else {
				prevValueSet.removeAll(valueSet);
				int difference = prevValueSet.size();
				if (difference != 1) {
					newValueSet.addAll(prevValueSet);
					newValueSet.addAll(valueSet);
					addCategoryEmpToValueSet(newValueSet,
							valueSet.toArray(new String[valueSet.size()]));
				} else {
					String oldValue = prevValueSet.iterator().next();
					newValueSet = valueSet;
					removeCategoryEmpFromValueSet(newValueSet,
							new String[] { oldValue });
				}
			}
			correctCategoriesInValueSet(newValueSet);
			return newValueSet;
		}

		private void addCategoryEmpToValueSet(Set<String> valueSet,
				String[] categoryContainedArray) {
			for (String value : categoryContainedArray) {
				if (value.endsWith(AppConstants.CATEGORY_MARKER)) {
					long categoryId = Long.valueOf(value.substring(0,
							value.length() - 1));
					for (Category category : categoryList) {
						if (category.getCategoryId() == categoryId) {
							for (Long employeeId : category.getEmployeeIdList()) {
								valueSet.add(employeeId
										+ AppConstants.EMPLOYEE_MARKER);
							}
						}
					}
				}
			}
		}

		private void removeCategoryEmpFromValueSet(Set<String> valueSet,
				String[] categoryContainedArray) {
			for (String value : categoryContainedArray) {
				if (value.endsWith(AppConstants.CATEGORY_MARKER)) {
					long categoryId = Long.valueOf(value.substring(0,
							value.length() - 1));
					for (Category category : categoryList) {
						if (category.getCategoryId() == categoryId) {
							for (Long employeeId : category.getEmployeeIdList()) {
								valueSet.remove(employeeId
										+ AppConstants.EMPLOYEE_MARKER);
							}
						}
					}
				}
			}
		}

	}

}
