package ua.nure.ostpc.malibu.shedule.client.manage;

import ua.nure.ostpc.malibu.shedule.client.AppState;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;

public class SaveButton extends PopupButton {

	private long periodId;
	private ScheduledCommand saveCmd = new ScheduledCommand() {
		
		@Override
		public void execute() {
			saveSchedule(periodId, false);
		}
	};

	private ScheduledCommand saveFullCmd = new ScheduledCommand() {
		
		@Override
		public void execute() {
			saveSchedule(periodId, true);
		}
	};
	
	public SaveButton(long id) {
		super(String.valueOf(id), "img/mail_send.png", null);
		setStyleName("myImageAsButton");
		periodId = id;
		getElement().setId("send-" + periodId);
		setTitle("Скачать");
		addPopupItem("Скачать мой график", saveCmd);
		addPopupItem("Скачать полный график", saveFullCmd);
	}

	private void saveSchedule(long id, boolean full) {
		String url = GWT.getModuleBaseURL() + "?download=" + full + "&id=" + id 
				+ (full ? "" : "&empId=" + AppState.employee.getEmployeeId());
		Window.open(url, "_blank", "");
	}
}
