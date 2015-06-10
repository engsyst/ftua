package ua.nure.ostpc.malibu.shedule.client.draft;

import java.util.ArrayList;
import java.util.List;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Shift;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class DraftShiftItem extends Composite implements 
				HasValueChangeHandlers<DraftShiftItem>, 
				HasEnabled {

	private WidgetList item;
	private Shift shift;
	int row, col, tab;

	public int getTab() {
		return tab;
	}

	public void setTab(int tab) {
		this.tab = tab;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public DraftShiftItem() {
		super();
		Image img = new Image("img/new_user.png");
		img.addStyleName("myImageAsButton");
		PushButton pb = new PushButton(img);
		item = new WidgetList(pb);
		pb.addClickHandler(addHandler);
		initWidget(item);
	}

	public DraftShiftItem(Shift data, int t, int r, int c) {
		this();
		tab = t;
		row = r;
		col = c;
		setShift(data);
	}

	protected ClickHandler removeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			Widget w = (Widget) event.getSource();
			shift.getEmployees().remove(item.indexOf(w));
			// Notify changes
			ValueChangeEvent.fire(DraftShiftItem.this, DraftShiftItem.this);
		}
	};

	private ClickHandler addHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (shift.getEmployees() == null)
				shift.setEmployees(new ArrayList<Employee>());
			if (shift.getQuantityOfEmployees() == shift.getEmployees().size())
				return;
			shift.getEmployees().add(AppState.employee);
			//			item.addItem(new ImageTextButton(new Image("img/close_10.png"), 
			//					AppState.employee.getShortName(), removeHandler));
			ValueChangeEvent.fire(DraftShiftItem.this, DraftShiftItem.this);
		}
	};

	public Shift getShift() {
		return shift;
	}

	public void setShift(Shift shift) {
		this.shift = shift;
		update();
	}

	private int addUiItem(String text) {
		return item.addItem(new ImageTextButton(new Image("img/close_10.png"), 
				text, removeHandler));
	}

	public void update() {
		item.removeAll();
		item.setAddEnabled(true);
		List<Employee> emps = this.shift.getEmployees();
		if (emps != null) {
			for (Employee e : emps) {
				int i = addUiItem(e.getShortName());
				if (AppState.employee.getEmployeeId() != e.getEmployeeId()) {
					((ImageTextButton) item.getWidget(i)).setEnabled(false);
				} else {
					item.setAddEnabled(false);
				}
			}
			if (shift.isFull())
				item.setAddEnabled(false);
				
			int j = emps.size();
			while (j < shift.getQuantityOfEmployees()) {
				item.addItem(new HTML("<div class=\"dsi-emptyItem\"></div>"));
				j++;
			}
		} else {
			int j = 0;
			while (j < shift.getQuantityOfEmployees()) {
				item.addItem(new HTML("<div class=\"dsi-emptyItem\"></div>"));
				j++;
			}
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<DraftShiftItem> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
		//		return AppState.eventBus.addHandler(ValueChangeEvent.getType(), handler);
	}

	@Override
	public void setEnabled(boolean enabled) {
		item.setEnabled(enabled);
	}

	@Override
	public boolean isEnabled() {
		return item.isEnabled();
	}

	public void setAddEnabled(boolean enabled) {
		item.setAddEnabled(enabled);
	}

	public boolean isAddEnabled() {
		return item.isAddEnabled();
	}

}
