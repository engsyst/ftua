package ua.nure.ostpc.malibu.shedule.client.panel.creation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * ListBox with quantity of employees on shift.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class EmpOnShiftListBox extends ListBox {
	private static final int MAX_QUANTITY_OF_EMPLOYEES = 20;

	private static Map<Long, List<EmpOnShiftListBox>> listBoxMap = new HashMap<Long, List<EmpOnShiftListBox>>();
	private static Map<Long, List<ShiftItem>> clubShiftItemMap = new HashMap<Long, List<ShiftItem>>();
	private static Map<Date, List<ShiftItem>> dateShiftItemMap = new HashMap<Date, List<ShiftItem>>();
	private static Map<Long, Integer> prevValueMap = new HashMap<Long, Integer>();
	private static AbsolutePanel schedulePanel;

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
				List<ShiftItem> shiftItemList = clubShiftItemMap.get(clubId);
				if (shiftItemList != null) {
					for (ShiftItem shiftItem : shiftItemList) {
						shiftItem.changeNumberOfEmployees(newValue);
					}
				}
				List<Widget> widgets = new ArrayList<Widget>();
				for (int i = 0; i < schedulePanel.getWidgetCount(); i++) {
					widgets.add(schedulePanel.getWidget(i));
				}
				int tableHeight = schedulePanel.getWidget(0).getOffsetHeight();
				schedulePanel.clear();
				int height = 20;
				for (Widget widget : widgets) {
					schedulePanel.add(widget, 5, height);
					height += (tableHeight + 20);
				}
				schedulePanel.setHeight(height + "px");
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

	public static void setSchedulePanel(AbsolutePanel schedulePanel) {
		EmpOnShiftListBox.schedulePanel = schedulePanel;
	}

	public static Map<Long, List<ShiftItem>> getClubShiftItemMap() {
		return clubShiftItemMap;
	}

	public static Map<Date, List<ShiftItem>> getDateShiftItemMap() {
		return dateShiftItemMap;
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
		long clubId = shiftItem.getClubId();
		if (!clubShiftItemMap.containsKey(clubId)) {
			clubShiftItemMap.put(clubId, new ArrayList<ShiftItem>());
		}
		clubShiftItemMap.get(clubId).add(shiftItem);
		Date date = shiftItem.getDate();
		if (!dateShiftItemMap.containsKey(date)) {
			dateShiftItemMap.put(date, new ArrayList<ShiftItem>());
		}
		dateShiftItemMap.get(date).add(shiftItem);
	}

	public static void removeData() {
		listBoxMap = new HashMap<Long, List<EmpOnShiftListBox>>();
		clubShiftItemMap = new HashMap<Long, List<ShiftItem>>();
		dateShiftItemMap = new HashMap<Date, List<ShiftItem>>();
		prevValueMap = new HashMap<Long, Integer>();
	}
}
