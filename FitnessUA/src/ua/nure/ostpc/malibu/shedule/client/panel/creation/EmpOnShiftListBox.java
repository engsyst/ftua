package ua.nure.ostpc.malibu.shedule.client.panel.creation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
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
	private static Map<Long, List<ShiftItem>> listShiftItemMap = new HashMap<Long, List<ShiftItem>>();
	private static AbsolutePanel schedulePanel;
	private static Map<Long, Integer> prevValueMap = new HashMap<Long, Integer>();

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
				List<ShiftItem> shiftItemList = listShiftItemMap.get(clubId);
				if (shiftItemList != null) {
					for (ShiftItem shiftItem : shiftItemList) {
						shiftItem.changeHeight(newValue);
					}
				}
				int tableHeight = schedulePanel.getWidget(0).getOffsetHeight();
				int height = 20;
				for (int i = 1; i < schedulePanel.getWidgetCount(); i++) {
					height += (tableHeight + 20);
					ScheduleWeekTable scheduleTable = (ScheduleWeekTable) schedulePanel
							.getWidget(i);
					schedulePanel.add(scheduleTable, 5, height);
				}
				height += 20;
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
		if (!listShiftItemMap.containsKey(clubId)) {
			listShiftItemMap.put(clubId, new ArrayList<ShiftItem>());
		}
		listShiftItemMap.get(clubId).add(shiftItem);
	}
}
