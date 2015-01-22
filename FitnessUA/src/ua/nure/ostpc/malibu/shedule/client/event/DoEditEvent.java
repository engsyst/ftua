package ua.nure.ostpc.malibu.shedule.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class DoEditEvent extends GwtEvent<DoEditHandler> {
	public static Type<DoEditHandler> TYPE = new Type<DoEditHandler>();
	
	private Long id;

	public DoEditEvent(Long id) {
		if (id == null) 
			throw new IllegalStateException("id can not be a null");
		this.id = id;
	}
	
	public DoEditEvent() {
	}
	
	@Override
	public Type<DoEditHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DoEditHandler handler) {
		handler.onEdit(this);
	}
	
	public long getId() {
		if (id == null) 
			throw new IllegalStateException("use DoEditEvent(Long id) to store id to event");
		return id;
	}
}
