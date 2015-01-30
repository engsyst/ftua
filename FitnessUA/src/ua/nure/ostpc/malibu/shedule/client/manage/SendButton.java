package ua.nure.ostpc.malibu.shedule.client.manage;

import ua.nure.ostpc.malibu.shedule.client.AppState;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;

public class SendButton extends Image {
	
	private static SendPopup sendPopup;
	private long periodId;
	
	public SendButton(long id) {
		super(GWT.getHostPageBaseURL() + "img/mail_send.png");
		periodId = id;
		if (sendPopup == null)
			sendPopup = new SendPopup(AppState.isResponsible);
		setStyleName("myImageAsButton");
		getElement().setId("send-" + periodId);
		setTitle("Сохранить / Отправить по почте");
		
		addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				sendPopup.show(periodId, event.getClientX(), event.getClientY());
			}
		});
	}
}
