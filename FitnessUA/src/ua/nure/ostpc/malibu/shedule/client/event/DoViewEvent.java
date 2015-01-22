package ua.nure.ostpc.malibu.shedule.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class DoViewEvent extends GwtEvent<DoViewHandler> {
	public static Type<DoViewHandler> TYPE = new Type<DoViewHandler>();
	
	private Long id;

	public DoViewEvent(Long id) {
		if (id == null) 
			throw new IllegalStateException("id can not be a null");
		this.id = id;
	}
	
	public DoViewEvent() {
	}
	
	@Override
	public Type<DoViewHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DoViewHandler handler) {
		handler.onView(this);
	}
	
	public long getId() {
		if (id == null) 
			throw new IllegalStateException("use DoViewEvent(Long id) to store id to event");
		return id;
	}
}
