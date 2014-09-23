package ua.nure.ostpc.malibu.shedule.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Category;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

import com.google.gwt.user.client.Window;
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

				if (valueList.size() > prevValueList.size()) {
					valueList.removeAll(prevValueList);
					String newValue=valueList.get(0);
					
				}else{
					prevValueList.removeAll(valueList);
					String oldValue= prevValueList.get(0);
					
				}

				ClubPrefSelectItem clubPrefSelectItem = (ClubPrefSelectItem) event
						.getSource();
				long clubId = Long.parseLong(clubPrefSelectItem.getTitle());
				List<ClubPrefSelectItem> selectItemList = selectItemMap
						.get(clubId);
				if (selectItemList != null) {
					for (ClubPrefSelectItem selectItem : selectItemList) {
						selectItem.setValues("1e", "2e");
					}
				}
			}
		});
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
			categoryMap.put(String.valueOf(category.getCategoryId()) + "c", "<"
					+ category.getTitle() + ">");
		}
		LinkedHashMap<String, String> employeeMap = new LinkedHashMap<String, String>();
		for (Employee employee : employeeList) {
			employeeMap.put(String.valueOf(employee.getEmployeeId()) + "e",
					employee.getNameForSchedule());
		}
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		valueMap.putAll(categoryMap);
		valueMap.putAll(employeeMap);
		return valueMap;
	}
}
