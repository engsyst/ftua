package ua.nure.ostpc.malibu.shedule.client.editor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * ListBox with quantity of employees on shift.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class EmpOnShiftListBox extends ListBox {
	private static final int MAX_QUANTITY_OF_EMPLOYEES = 20;

	private static Map<Long, List<EmpOnShiftListBox>> listBoxMap = new HashMap<Long, List<EmpOnShiftListBox>>();
	private static Map<Date, List<ShiftItem>> dateShiftItemMap = new HashMap<Date, List<ShiftItem>>();
	private static Map<Long, Integer> prevValueMap = new HashMap<Long, Integer>();
	private static boolean hasChanges;

	private long clubId;

	public EmpOnShiftListBox(long clubId) {
		super(false);
		for (int i = 1; i <= MAX_QUANTITY_OF_EMPLOYEES; i++) {
			addItem(String.valueOf(i));
		}
		setSelectedIndex(0);
		addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				EmpOnShiftListBox empOnShiftListBox = (EmpOnShiftListBox) event
						.getSource();
				long clubId = empOnShiftListBox.getClubId();
				int selectedIndex = empOnShiftListBox.getSelectedIndex();
				int newValue = Integer.parseInt(empOnShiftListBox
						.getValue(selectedIndex));
				prevValueMap.put(clubId, newValue);
				List<EmpOnShiftListBox> listBoxList = listBoxMap.get(clubId);
				if (listBoxList != null) {
					for (EmpOnShiftListBox listBox : listBoxList) {
						listBox.setSelectedIndex(selectedIndex);
					}
				}
				Iterator<Entry<Date, List<ShiftItem>>> it = dateShiftItemMap
						.entrySet().iterator();
				while (it.hasNext()) {
					Entry<Date, List<ShiftItem>> entry = it.next();
					List<ShiftItem> shiftItemList = entry.getValue();
					if (shiftItemList != null) {
						for (ShiftItem shiftItem : shiftItemList) {
							if (shiftItem.getClubId() == clubId) {
								shiftItem.changeNumberOfEmployees(newValue);
							}
						}
					}
				}
				ScheduleEditingPanel.redraw();
				hasChanges = true;
			}
		});
		this.clubId = clubId;
	}

	public long getClubId() {
		return clubId;
	}

	public void setClubId(long clubId) {
		this.clubId = clubId;
	}

	public static boolean hasChanges() {
		return hasChanges;
	}

	public static void setHasChanges(boolean hasChanges) {
		EmpOnShiftListBox.hasChanges = hasChanges;
	}

	public static Map<Date, List<ShiftItem>> getDateShiftItemMap() {
		return dateShiftItemMap;
	}

	public static ShiftItem getShiftItem(Date date, long clubId, int shiftNumber) {
		List<ShiftItem> shiftItemList = dateShiftItemMap.get(date);
		for (ShiftItem shiftItem : shiftItemList) {
			if (shiftItem.getClubId() == clubId
					&& shiftItem.getShiftNumber() == shiftNumber) {
				return shiftItem;
			}
		}
		return null;
	}

	public static boolean updateShiftItem(ShiftItem item) {
		List<ShiftItem> shiftItemList = dateShiftItemMap.get(item.getDate());
		for (ShiftItem shiftItem : shiftItemList) {
			if (shiftItem.getClubId() == item.getClubId()
					&& shiftItem.getShiftNumber() == item.getShiftNumber()) {
				shiftItem = item;
				return true;
			}
		}
		return false;
	}

	public static int getEmployeesOnShift(long clubId) {
		int employeesOnShift = 1;
		if (prevValueMap.containsKey(clubId)) {
			employeesOnShift = prevValueMap.get(clubId);
		} else {
			prevValueMap.put(clubId, employeesOnShift);
		}
		return employeesOnShift;
	}

	public static void addEmpOnShiftListBox(EmpOnShiftListBox empOnShiftListBox) {
		long clubId = empOnShiftListBox.getClubId();
		if (!listBoxMap.containsKey(clubId)) {
			listBoxMap.put(clubId, new ArrayList<EmpOnShiftListBox>());
		}
		listBoxMap.get(clubId).add(empOnShiftListBox);
	}

	public static void addShiftItem(ShiftItem shiftItem) {
		Date date = shiftItem.getDate();
		if (!dateShiftItemMap.containsKey(date)) {
			dateShiftItemMap.put(date, new ArrayList<ShiftItem>());
		}
		dateShiftItemMap.get(date).add(shiftItem);
	}

	public static void setEmpOnShiftForClub(long clubId, int quantityOfEmployees) {
		List<EmpOnShiftListBox> listBoxList = listBoxMap.get(clubId);
		if (listBoxList != null) {
			prevValueMap.put(clubId, quantityOfEmployees);
			int selectedIndex = quantityOfEmployees - 1;
			for (EmpOnShiftListBox listBox : listBoxList) {
				listBox.setSelectedIndex(selectedIndex);
			}
		}
	}

	public static void removeData() {
		listBoxMap = new HashMap<Long, List<EmpOnShiftListBox>>();
		dateShiftItemMap = new HashMap<Date, List<ShiftItem>>();
		prevValueMap = new HashMap<Long, Integer>();
	}

	public static void disableAll() {
		Iterator<Entry<Long, List<EmpOnShiftListBox>>> it = listBoxMap
				.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Long, List<EmpOnShiftListBox>> entry = it.next();
			List<EmpOnShiftListBox> list = entry.getValue();
			if (list != null) {
				for (EmpOnShiftListBox empOnShiftListBox : list) {
					empOnShiftListBox.setEnabled(false);
				}
			}
		}
	}

	public static void disableElementsForCurrentSchedule() {
		disableAll();
		Iterator<Entry<Date, List<ShiftItem>>> it = dateShiftItemMap.entrySet()
				.iterator();
		Date currentDate = new Date();
		while (it.hasNext()) {
			Entry<Date, List<ShiftItem>> entry = it.next();
			if (entry.getKey().before(currentDate)) {
				List<ShiftItem> shiftItemList = entry.getValue();
				for (ShiftItem shiftItem : shiftItemList) {
					shiftItem.toView();
				}
			}
		}
	}

	public static void toView() {
		Iterator<Entry<Date, List<ShiftItem>>> it = dateShiftItemMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<Date, List<ShiftItem>> entry = it.next();
			List<ShiftItem> shiftItemList = entry.getValue();
			for (ShiftItem shiftItem : shiftItemList) {
				shiftItem.toView();
			}
		}
		Iterator<Entry<Long, List<EmpOnShiftListBox>>> iter = listBoxMap
				.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Long, List<EmpOnShiftListBox>> entry = iter.next();
			List<EmpOnShiftListBox> list = entry.getValue();
			if (list != null) {
				for (EmpOnShiftListBox empOnShiftListBox : list) {
					HorizontalPanel horizontalPanel = (HorizontalPanel) empOnShiftListBox
							.getParent();
					int widgetIndex = horizontalPanel
							.getWidgetIndex(empOnShiftListBox);
					horizontalPanel.remove(widgetIndex);
					horizontalPanel.insert(
							new Label(String.valueOf(prevValueMap
									.get(empOnShiftListBox.getClubId()))),
							widgetIndex);
				}
			}
		}
	}
}
