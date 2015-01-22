package ua.nure.ostpc.malibu.shedule.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class DoSettingsEvent extends GwtEvent<DoSettingsHandler> {
	public static Type<DoSettingsHandler> TYPE = new Type<DoSettingsHandler>();
	
	@Override
	public Type<DoSettingsHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DoSettingsHandler handler) {
		handler.onSettings(this);
	}
}
