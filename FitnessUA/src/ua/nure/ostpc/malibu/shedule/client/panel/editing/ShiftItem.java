package ua.nure.ostpc.malibu.shedule.client.panel.editing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.CellList;
import com.smartgwt.client.types.MultiComboBoxLayoutStyle;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
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

					boolean dayFlag = false;
					boolean[] dayShiftsFlags = new boolean[EmpOnShiftListBox
							.getEmployeesOnShift(clubId)];
					dayShiftsFlags[shiftItem.getShiftNumber() - 1] = true;
					shiftItemList.remove(shiftItem);
					Iterator<ShiftItem> it = shiftItemList.iterator();
					while (it.hasNext()) {
						ShiftItem item = it.next();
						if (item.getClubId() == shiftItem.getClubId()
								&& item.getPrevValueSet().contains(newValue)) {
							dayFlag = true;
							dayShiftsFlags[item.getShiftNumber()] = true;
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
									&& dayShiftsFlags[itemShiftNumber + 1] == true) {
								fl = true;
							}
							if (itemShiftNumber == dayShiftsFlags.length - 1
									&& dayShiftsFlags[itemShiftNumber - 1] == true) {
								fl = true;
							}
							if (dayShiftsFlags[itemShiftNumber - 1] == true
									|| dayShiftsFlags[itemShiftNumber + 1] == true) {
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

					// REMOVE EMPLOYEE ID

					prevValueSet.removeAll(valueSet);
					String oldValue = null;
					if (!prevValueSet.isEmpty()) {
						oldValue = prevValueSet.iterator().next();
					}
					newValueSet = valueSet;

					// Set flags relative to current shift (shiftItem) day.

					boolean dayFlag = false;
					boolean[] dayShiftsFlags = new boolean[EmpOnShiftListBox
							.getEmployeesOnShift(clubId)];
					shiftItemList.remove(shiftItem);
					Iterator<ShiftItem> it = shiftItemList.iterator();
					while (it.hasNext()) {
						ShiftItem item = it.next();
						if (item.getClubId() == shiftItem.getClubId()
								&& item.getPrevValueSet().contains(oldValue)) {
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
										&& dayShiftsFlags[itemShiftNumber + 1] == true) {
									fl = true;
								}
								if (itemShiftNumber == dayShiftsFlags.length - 1
										&& dayShiftsFlags[itemShiftNumber - 1] == true) {
									fl = true;
								}
								if (dayShiftsFlags[itemShiftNumber - 1] == true
										|| dayShiftsFlags[itemShiftNumber + 1] == true) {
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
										item.setValues(itemValuesList.toArray());
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

	public void toView() {
		TextCell textCell = new TextCell();
		CellList<String> cellList = new CellList<String>(textCell);
		cellList.setRowCount(prevValueSet.size(), true);
		List<String> list = new ArrayList<String>();
		for (String id : prevValueSet) {
			list.add((String) valueMap.get(id));
		}
		cellList.setRowData(0, list);
		Canvas[] canvas = shiftLayout.getChildren();
		for (Canvas canv : canvas) {
			shiftLayout.removeChild(canv);
		}
		shiftLayout.clear();
		cellList.setStyleName("cellList");
		shiftLayout.addChild(cellList);
	}
}
