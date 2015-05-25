package ua.nure.ostpc.malibu.shedule.client.draft;

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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class DraftShiftItem extends Composite implements HasValueChangeHandlers<Shift> {

	private WidgetList item;
	private Shift shift;
	
	public DraftShiftItem() {
		super();
		Image img = new Image("img/new_user.png");
		item = new WidgetList(img);
		img.addClickHandler(addHandler);
		initWidget(item);
	}
	
	public DraftShiftItem(Shift s) {
		this();
		setShift(s);
	}

	protected ClickHandler removeHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			Widget w = (Widget) event.getSource();
			shift.getEmployees().remove(item.removeItem(w));
			// Notify changes
			ValueChangeEvent.fire(DraftShiftItem.this, shift);
		}
	};
	
	private ClickHandler addHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if (shift.getQuantityOfEmployees() == shift.getEmployees().size())
				return;
			shift.getEmployees().add(AppState.employee);
			item.addItem(new ImageTextButton(new Image("img/close_10.png"), 
					AppState.employee.getShortName(), removeHandler));
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
		for (Employee e : this.shift.getEmployees()) {
			int i = addUiItem(e.getShortName());
			if (AppState.employee.getEmployeeId() != e.getEmployeeId()) {
				((ImageTextButton) item.getWidget(i)).setEnabled(false);
			}
		}
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Shift> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
//		return AppState.eventBus.addHandler(ValueChangeEvent.getType(), handler);
	}
	
}
