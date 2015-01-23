package ua.nure.ostpc.malibu.shedule.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface PeriodsUpdatedHandler extends EventHandler {
	void onUpdate(PeriodsUpdatedEvent periodsUpdatedEvent);
}
