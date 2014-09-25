package ua.nure.ostpc.malibu.shedule.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
}
