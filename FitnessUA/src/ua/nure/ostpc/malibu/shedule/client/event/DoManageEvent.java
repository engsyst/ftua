package ua.nure.ostpc.malibu.shedule.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class DoManageEvent extends GwtEvent<DoManageHandler> {
	public static Type<DoManageHandler> TYPE = new Type<DoManageHandler>();
	
	public DoManageEvent() {
	}
	
	@Override
	public Type<DoManageHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DoManageHandler handler) {
		handler.onManage(this);
	}
}
