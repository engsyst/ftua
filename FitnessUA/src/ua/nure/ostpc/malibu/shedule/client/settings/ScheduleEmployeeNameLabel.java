package ua.nure.ostpc.malibu.shedule.client.settings;

import ua.nure.ostpc.malibu.shedule.client.DialogBoxUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.InlineLabel;

public class ScheduleEmployeeNameLabel extends InlineLabel {
	private long employeeId;

	public ScheduleEmployeeNameLabel(String nameForSchedule, long employeeId) {
		this.employeeId = employeeId;
		setText(nameForSchedule);
		setStyleName("cursor");

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ScheduleEmployeeNameLabel employeeNameLabel = (ScheduleEmployeeNameLabel) event
						.getSource();
				long employeeId = employeeNameLabel.getEmployeeId();
				UserSettingSimplePanel userSettingSimplePanel = new UserSettingSimplePanel(
						employeeId);
				DialogBoxUtil.callDialogBox(userSettingSimplePanel);
			}
		});
	}

	public long getEmployeeId() {
		return employeeId;
	}
}
