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
	private static final int RECORD_HEIGHT = 30;

	private Date date;
	private long clubId;
	private int shiftNumber;
	private HLayout shiftLayout;

	public ShiftItem(Date date, long clubId, int shiftNumber,
			int employeesOnShift, LinkedHashMap<String, String> employeeMap) {
		super();
		this.date = new Date(date.getTime());
		this.clubId = clubId;
		this.shiftNumber = shiftNumber;
		setValueMap(employeeMap);
		setLayoutStyle(MultiComboBoxLayoutStyle.VERTICAL);
		setShowTitle(false);
		DynamicForm shiftForm = new DynamicForm();
		shiftForm.setItems(this);
		this.shiftLayout = new HLayout();
		shiftLayout.setStyleName("shiftItem");
		shiftLayout.addChild(shiftForm);
		changeNumberOfEmployees(employeesOnShift);
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
}
