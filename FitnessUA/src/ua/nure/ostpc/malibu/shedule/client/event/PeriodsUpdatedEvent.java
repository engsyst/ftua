package ua.nure.ostpc.malibu.shedule.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class PeriodsUpdatedEvent extends GwtEvent<PeriodsUpdatedHandler> {
	public static Type<PeriodsUpdatedHandler> TYPE = new Type<PeriodsUpdatedHandler>();
	
	@Override
	public Type<PeriodsUpdatedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PeriodsUpdatedHandler handler) {
		handler.onUpdate(this);
	}
	
}
