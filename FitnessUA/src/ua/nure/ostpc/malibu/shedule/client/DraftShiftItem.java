package ua.nure.ostpc.malibu.shedule.client;

import ua.nure.ostpc.malibu.shedule.entity.Employee;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DraftShiftItem extends Composite {
	private VerticalPanel vp = new VerticalPanel();
	private FlexTable tab;
	private int empCount;
	private Item[] items;
	private String id;
	private int row;
	private int col;

	private class Item {
		String id;
		boolean enabled;
		Label label;
		CheckBox checkBox;

		public Item() {
			this(true, " ", false);
		}

		public Item(boolean enabled, String label, boolean checked) {
			setEnabled(enabled);
			this.label = new Label(label);
			this.checkBox = new CheckBox();
			setChecked(checked);
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
			checkBox.setEnabled(enabled);
		}

		public Label getLabel() {
			return label;
		}

		public void setLabel(Label label) {
			this.label = label;
		}

		public boolean isChecked() {
			return checkBox.getValue();
		}

		public void setChecked(boolean checked) {
			this.checkBox.getValue();
		}

		public void setId(long employeeId, String shiftId) {
			this.id = employeeId + "-" + shiftId;
		}
	}

	public DraftShiftItem() {
		this(1, 0, 0);
	}

	public DraftShiftItem(int empCount, int row, int col) {
		this.row = row;
		this.col = col;
		setId(row, col);
		this.empCount = empCount;
		items = new Item[empCount];
		tab = new FlexTable();
		for (int i = 0; i < empCount; i++) {
			addItem(i, false, " ", false);
		}

		initWidget(vp);
	}

	private void setId(int row, int col) {
		id = row + "-" + col;
	}

	private void addItem(int position, boolean enabled, String label, boolean checked) {
		items[position] = new Item(enabled, label, checked);
		int row = tab.insertRow(position);
		tab.addCell(row);
		tab.addCell(row);
		tab.setWidget(row, 0, items[position].label);
		tab.setWidget(row, 0, items[position].checkBox);
	}
	
	public void setItem(Employee emp, int shift, boolean enabled) {
		items[shift].setId(emp.getEmployeeId(), this.id);
	}
}
