package ua.nure.ostpc.malibu.shedule.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ChangeStyleEvent extends GwtEvent<ChangeStyleHandler> {
	public static Type<ChangeStyleHandler> TYPE = new Type<ChangeStyleHandler>();
	
	private String value;
	private boolean add;

	public ChangeStyleEvent(String value, boolean add) {
		this.value = value;
		this.add = add;
	}
	
	@Override
	public Type<ChangeStyleHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ChangeStyleHandler handler) {
		handler.onChangeStyle(this);
	}
	
	public String getValue() {
		if (value == null) 
			throw new IllegalStateException("use ChangeStyleEvent(Long value) to store value to event");
		return value;
	}

	public boolean add() {
		return add;
	}
}
