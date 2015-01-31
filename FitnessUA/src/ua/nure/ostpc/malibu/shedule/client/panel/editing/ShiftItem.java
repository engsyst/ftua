package ua.nure.ostpc.malibu.shedule.client.panel.editing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.FlexTable;
import com.smartgwt.client.types.MultiComboBoxLayoutStyle;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.MultiComboBoxItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Shift item.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class ShiftItem extends MultiComboBoxItem {
	private static final int RECORD_HEIGHT = 30;

	private static LinkedHashMap<String, String> employeeMap;
	private static Map<Date, HashMap<Long, Integer>> numberOfShiftsMap = new HashMap<Date, HashMap<Long, Integer>>();
	private static Map<Date, HashMap<Long, Integer>> workHoursInDayMap = new HashMap<Date, HashMap<Long, Integer>>();

	private Date date;
	private long clubId;
	private int shiftNumber;
	private HLayout shiftLayout;

	private HashSet<String> prevValueSet;

	@SuppressWarnings("rawtypes")
	private LinkedHashMap valueMap;

	public ShiftItem(Date date, long clubId, int shiftNumber,
			int employeesOnShift, LinkedHashMap<String, String> employeeMap) {
		this.date = new Date(date.getTime());
		this.clubId = clubId;
		this.shiftNumber = shiftNumber;
		setValueMap(employeeMap);
		setLayoutStyle(MultiComboBoxLayoutStyle.VERTICAL);
		setShowTitle(false);
		DynamicForm shiftForm = new DynamicForm();
		setWidth("80px");
		shiftForm.setItems(this);
		this.shiftLayout = new HLayout();
		shiftLayout.setStyleName("shiftItem");
		shiftLayout.addChild(shiftForm);
		changeNumberOfEmployees(employeesOnShift);
		this.prevValueSet = new HashSet<String>();

		addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				Map<Date, List<ShiftItem>> dateShiftItemMap = EmpOnShiftListBox
						.getDateShiftItemMap();
				ShiftItem shiftItem = (ShiftItem) event.getSource();
				long clubId = shiftItem.getClubId();
				Date date = shiftItem.getDate();
				List<ShiftItem> shiftItemList = new ArrayList<ShiftItem>(
						dateShiftItemMap.get(date));
				HashSet<String> valueSet = new HashSet<String>(
						Arrays.asList(shiftItem.getValues()));
				HashSet<String> newValueSet = null;

				if (valueSet.size() > prevValueSet.size()) {

					// ADD EMPLOYEE ID

					valueSet.removeAll(prevValueSet);
					String newValue = valueSet.iterator().next();

					int employeesOnShift = EmpOnShiftListBox
							.getEmployeesOnShift(clubId);
					if (prevValueSet.size() == employeesOnShift) {
						shiftItem.setValues(prevValueSet.toArray());
						SC.warn("Максимальное количество человек на смене: "
								+ employeesOnShift, new BooleanCallback() {

							@Override
							public void execute(Boolean value) {
							}
						});
						return;
					}

					newValueSet = prevValueSet;
					newValueSet.add(newValue);

					// Set flags relative to current shift (shiftItem) day.

					// dayFlag is true if some of shifts with clubId contains
					// newValue yet.
					boolean dayFlag = false;
					boolean[] dayShiftsFlags = new boolean[getNumberOfShifts(
							date, clubId)];
					dayShiftsFlags[shiftItem.getShiftNumber() - 1] = true;
					shiftItemList.remove(shiftItem);
					Iterator<ShiftItem> it = shiftItemList.iterator();
					while (it.hasNext()) {
						ShiftItem item = it.next();
						if (item.getClubId() == shiftItem.getClubId()
								&& item.getPrevValueSet().contains(newValue)) {
							dayFlag = true;
							dayShiftsFlags[item.getShiftNumber() - 1] = true;
							it.remove();
						}
					}

					// Don't remove employee id in neighboring shift in current
					// shift day.

					it = shiftItemList.iterator();
					while (it.hasNext()) {
						ShiftItem item = it.next();
						boolean fl = false;
						if (item.getClubId() == shiftItem.getClubId()) {
							int itemShiftNumber = item.getShiftNumber();
							if (itemShiftNumber == 1
									&& dayShiftsFlags[itemShiftNumber] == true) {
								fl = true;
							}
							if (!fl
									&& (itemShiftNumber == dayShiftsFlags.length && dayShiftsFlags[itemShiftNumber - 2] == true)) {
								fl = true;
							}
							if (!fl
									&& (dayShiftsFlags[itemShiftNumber - 2] == true || dayShiftsFlags[itemShiftNumber] == true)) {
								fl = true;
							}
							if (fl) {
								item.addToValueMap(newValue);
								it.remove();
							}
						}
					}

					it = shiftItemList.iterator();
					while (it.hasNext()) {
						ShiftItem item = it.next();
						if (dayFlag
								&& item.getClubId() != shiftItem.getClubId()) {
							it.remove();
						} else {

							// Remove employee id from others clubs.

							item.removeFromValueMap(newValue);
						}
					}
				} else {
					if (valueSet.size() < prevValueSet.size()) {

						// REMOVE EMPLOYEE ID

						prevValueSet.removeAll(valueSet);
						String oldValue = prevValueSet.iterator().next();
						newValueSet = valueSet;

						// Set flags relative to current shift (shiftItem) day.

						// dayFlag is true if some of shifts with clubId
						// contains
						// newValue yet.
						boolean dayFlag = false;
						boolean[] dayShiftsFlags = new boolean[getNumberOfShifts(
								date, clubId)];
						shiftItemList.remove(shiftItem);
						Iterator<ShiftItem> it = shiftItemList.iterator();
						while (it.hasNext()) {
							ShiftItem item = it.next();
							if (item.getClubId() == shiftItem.getClubId()
									&& item.getPrevValueSet()
											.contains(oldValue)) {
								dayFlag = true;
								dayShiftsFlags[item.getShiftNumber() - 1] = true;
							}
						}

						if (dayFlag) {

							// Remove employee id in shift in current
							// shift day below current position if shift have
							// employee
							// id above current position.

							boolean continuityFlag = false;
							for (int i = 0; i < dayShiftsFlags.length; i++) {
								if (dayShiftsFlags[i] == true) {
									if (i == 0) {
										continuityFlag = true;
									} else {
										if (dayShiftsFlags[i - 1] == false
												&& continuityFlag == true) {
											dayShiftsFlags[i] = false;
										} else {
											continuityFlag = true;
										}
									}
								}
							}

							// Don't remove employee id in neighboring shift in
							// current
							// shift day.

							it = shiftItemList.iterator();
							while (it.hasNext()) {
								ShiftItem item = it.next();
								boolean fl = false;
								if (item.getClubId() == shiftItem.getClubId()) {
									int itemShiftNumber = item.getShiftNumber();
									if (itemShiftNumber == 1
											&& dayShiftsFlags[itemShiftNumber] == true) {
										fl = true;
									}
									if (!fl
											&& (itemShiftNumber == dayShiftsFlags.length && dayShiftsFlags[itemShiftNumber - 2] == true)) {
										fl = true;
									}
									if (!fl
											&& (dayShiftsFlags[itemShiftNumber - 2] == true || dayShiftsFlags[itemShiftNumber] == true)) {
										fl = true;
									}
									if (fl) {
										item.addToValueMap(oldValue);
										it.remove();
									}
								}
							}
						}

						it = shiftItemList.iterator();
						while (it.hasNext()) {
							ShiftItem item = it.next();
							if (dayFlag) {
								if (item.getClubId() != shiftItem.getClubId()) {
									it.remove();
								} else {
									if (item.getValues() != null
											&& item.getValues().length != 0) {
										List<String> itemValuesList = new ArrayList<String>(
												Arrays.asList(item.getValues()));
										itemValuesList.remove(oldValue);
										if (itemValuesList.size() != 0) {
											item.setValues(itemValuesList
													.toArray());
										} else {
											item.setValues(new Object[0]);
										}
									}
									item.removeFromValueMap(oldValue);
								}
							} else {

								// Add employee id to others clubs.
								if (oldValue != null) {
									item.addToValueMap(oldValue);
								}
							}
						}

					} else {
						newValueSet = prevValueSet;
					}
				}
				prevValueSet = newValueSet;
			}
		});
	}

	public Date getDate() {
		return date;
	}

	public long getClubId() {
		return clubId;
	}

	public int getShiftNumber() {
		return shiftNumber;
	}

	public HLayout getShiftLayout() {
		return shiftLayout;
	}

	public HashSet<String> getPrevValueSet() {
		return prevValueSet;
	}

	public void changeNumberOfEmployees(int employeesOnShift) {
		shiftLayout.setHeight((employeesOnShift + 1) * RECORD_HEIGHT);
		if (getValues().length > employeesOnShift) {
			String[] oldEmployeesId = getValues();
			int n = oldEmployeesId.length;
			int diff = n - employeesOnShift;
			Object[] newEmployeesId = new String[n - diff];
			for (int i = diff; i < n; i++) {
				newEmployeesId[i - diff] = oldEmployeesId[i];
			}
			this.setValues(newEmployeesId);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setValueMap(LinkedHashMap valueMap) {
		this.valueMap = new LinkedHashMap<String, String>(valueMap);
		super.setValueMap(this.valueMap);
	}

	@SuppressWarnings("unchecked")
	public void addToValueMap(String employeeId) {
		String employeeName = ShiftItem.employeeMap.get(employeeId);
		valueMap.put(employeeId, employeeName);
		super.setValueMap(this.valueMap);
	}

	public void removeFromValueMap(String employeeId) {
		valueMap.remove(employeeId);
		super.setValueMap(this.valueMap);
	}

	public static void setEmployeeMap(LinkedHashMap<String, String> employeeMap) {
		ShiftItem.employeeMap = employeeMap;
	}

	public static void setNumberOfShifts(Date date, long clubId,
			int numberOfShifts) {
		if (numberOfShiftsMap.containsKey(date)) {
			HashMap<Long, Integer> clubNumberOfShiftsMap = numberOfShiftsMap
					.get(date);
			clubNumberOfShiftsMap.put(clubId, numberOfShifts);
			numberOfShiftsMap.put(date, clubNumberOfShiftsMap);
		} else {
			HashMap<Long, Integer> clubNumberOfShiftsMap = new HashMap<Long, Integer>();
			clubNumberOfShiftsMap.put(clubId, numberOfShifts);
			numberOfShiftsMap.put(date, clubNumberOfShiftsMap);
		}
	}

	public static int getNumberOfShifts(Date date, long clubId) {
		int numberOfShifts = 0;
		HashMap<Long, Integer> clubNumberOfShiftsMap = numberOfShiftsMap
				.get(date);
		if (clubNumberOfShiftsMap != null
				&& clubNumberOfShiftsMap.containsKey(clubId)) {
			numberOfShifts = clubNumberOfShiftsMap.get(clubId);
		}
		return numberOfShifts;
	}

	public static void setWorkHoursInDay(Date date, long clubId,
			int workHoursInDay) {
		if (workHoursInDayMap.containsKey(date)) {
			HashMap<Long, Integer> clubWorkHoursInDayMap = workHoursInDayMap
					.get(date);
			clubWorkHoursInDayMap.put(clubId, workHoursInDay);
			workHoursInDayMap.put(date, clubWorkHoursInDayMap);
		} else {
			HashMap<Long, Integer> clubWorkHoursInDayMap = new HashMap<Long, Integer>();
			clubWorkHoursInDayMap.put(clubId, workHoursInDay);
			workHoursInDayMap.put(date, clubWorkHoursInDayMap);
		}
	}

	public static int getWorkHoursInDay(Date date, long clubId) {
		int workHoursInDay = 0;
		HashMap<Long, Integer> clubWorkHoursInDayMap = workHoursInDayMap
				.get(date);
		if (clubWorkHoursInDayMap != null
				&& clubWorkHoursInDayMap.containsKey(clubId)) {
			workHoursInDay = clubWorkHoursInDayMap.get(clubId);
		}
		return workHoursInDay;
	}

	public void toView() {
		TextCell textCell = new TextCell();
		CellList<String> cellList = new CellList<String>(textCell);
		List<String> list = new ArrayList<String>();
		for (String id : prevValueSet) {
			list.add((String) valueMap.get(id));
		}
		cellList.setRowData(0, list);
		cellList.setStyleName("cellList");
		FlexTable shiftsTable = (FlexTable) shiftLayout.getParent();
		shiftsTable.remove(shiftLayout);
		shiftLayout = new HLayout();
		shiftLayout.setStyleName("shiftItem");
		shiftLayout.addChild(cellList);
		shiftsTable.setWidget(shiftNumber - 1, 0, shiftLayout);
	}
}
