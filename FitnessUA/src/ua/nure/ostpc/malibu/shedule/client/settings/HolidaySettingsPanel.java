package ua.nure.ostpc.malibu.shedule.client.settings;

import com.google.gwt.user.client.ui.HorizontalPanel;

public class HolidaySettingsPanel extends HorizontalPanel {

	public HolidaySettingsPanel() {
		add(new HolidayPanel());
		add(new WeekendPanel());
		addStyleName("HolidaySettingsPanel");
	}
}
