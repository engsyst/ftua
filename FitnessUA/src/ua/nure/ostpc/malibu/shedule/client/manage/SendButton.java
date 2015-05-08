package ua.nure.ostpc.malibu.shedule.client.manage;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.client.LoadingPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;

public class SendButton extends PopupButton {

	private long periodId;
	private ScheduledCommand sendCmd = new ScheduledCommand() {
		
		@Override
		public void execute() {
			sendSchedule(periodId, false, false);
		}
	};

	private ScheduledCommand sendFullCmd = new ScheduledCommand() {
		
		@Override
		public void execute() {
			sendSchedule(periodId, true, false);
		}
	};
	
	private ScheduledCommand sendFullToAllCmd = new ScheduledCommand() {
		
		@Override
		public void execute() {
			sendSchedule(periodId, true, true);
		}
	};
	
	public SendButton(long id) {
		super(String.valueOf(id), "img/mail_send.png", null);
		setStyleName("myImageAsButton");
		periodId = id;
		getElement().setId("send-" + periodId);
		setTitle("Отправить");
		addPopupItem("Отправить мне мой график", sendCmd);
		addPopupItem("Отправить мне полный график", sendFullCmd);
		if (AppState.isResponsible)
			addPopupItem("Отправить всем полный график", sendFullToAllCmd);
	}

	private void sendSchedule(long id, boolean full, boolean toAll) {
		LoadingPanel.start();
		AppState.scheduleManagerService.sendMail(id, full, toAll, 
				toAll ? null : AppState.employee.getEmployeeId(), 
						new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				LoadingPanel.stop();
				SC.say("Письмо с графиком работ отослано");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				LoadingPanel.stop();
				SC.say("Невозможно отослать почту");
			}
		});
	}
}
