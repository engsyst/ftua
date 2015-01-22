package ua.nure.ostpc.malibu.shedule.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class DoDraftEvent extends GwtEvent<DoDraftHandler> {
	public static Type<DoDraftHandler> TYPE = new Type<DoDraftHandler>();
	
	private Long id;

	public DoDraftEvent(Long id) {
		if (id == null) 
			throw new IllegalStateException("id can not be a null");
		this.id = id;
	}
	
	public DoDraftEvent() {
	}
	
	@Override
	public Type<DoDraftHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DoDraftHandler handler) {
		handler.onDraft(this);
	}
	
	public long getId() {
		if (id == null) 
			throw new IllegalStateException("use DoDraftEvent(Long id) to store id to event");
		return id;
	}
}
