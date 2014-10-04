package ua.nure.ostpc.malibu.shedule.client.panel.creation;

import java.util.Date;
import java.util.LinkedHashMap;

import com.smartgwt.client.types.MultiComboBoxLayoutStyle;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.MultiComboBoxItem;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Shift item.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class ShiftItem extends MultiComboBoxItem {
	private Date date;
	private long clubId;
	private int shiftNumber;
	private HLayout shiftLayout;

	public ShiftItem(Date date, long clubId, int shiftNumber,
			LinkedHashMap<String, String> employeeMap) {
		this.date = new Date(date.getTime());
		this.clubId = clubId;
		this.shiftNumber = shiftNumber;
		setValueMap(employeeMap);
		setLayoutStyle(MultiComboBoxLayoutStyle.VERTICAL);
		setShowTitle(false);
		setWidth("103px");
		DynamicForm shiftForm = new DynamicForm();
		shiftForm.setItems(this);
		this.shiftLayout = new HLayout();
		shiftLayout.addChild(shiftForm);
		int employeesOnShift = EmpOnShiftListBox.getEmployeesOnShift(clubId);
		shiftLayout.setHeight((employeesOnShift + 1) * 30);
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
}
