package ua.nure.ostpc.malibu.shedule.client.manage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.util.SC;

public class SendPopup {
	private InlineLabel sendMeShortLabel;
	private InlineLabel sendMeFullLabel;
	private InlineLabel sendToAllFullLabel;
	private InlineLabel saveShortLabel;
	private InlineLabel saveFullLabel;
	private long id;
	private PopupPanel popup;

	public SendPopup(boolean isResponsible) {
		popup = new PopupPanel();
		VerticalPanel panel = new VerticalPanel();
		if (isResponsible) {
			sendToAllFullLabel = new InlineLabel("Отправить всем полный график");
			panel.add(sendToAllFullLabel);
		}
		sendMeShortLabel = new InlineLabel("Отправить мне мой график");
		panel.add(sendMeShortLabel);
		sendMeFullLabel = new InlineLabel("Отправить мне полный график");
		panel.add(sendMeFullLabel);
		saveShortLabel = new InlineLabel("Скачать мой график");
		panel.add(saveShortLabel);
		saveFullLabel = new InlineLabel("Скачать полный график");
		panel.add(saveFullLabel);
		
		sendMeShortLabel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				sendSchedule(id, false, false);
			}

		});
		
		sendMeFullLabel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				sendSchedule(id, true, false);
			}
			
		});
		
		sendToAllFullLabel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				sendSchedule(id, true, true);
			}
			
		});
		
		saveShortLabel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				saveSchedule(id, false);
			}
			
		});
		
		saveFullLabel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				saveSchedule(id, true);
			}
			
		});

		popup.add(panel);
		popup.setAutoHideEnabled(true);
	}

	private void sendSchedule(long id, boolean full, boolean toAll) {
		// TODO Auto-generated method stub
		SC.say("sendSchedule" + id);
	}
	
	private void saveSchedule(long id, boolean full) {
		// TODO Auto-generated method stub
		SC.say("saveSchedule" + id);
	}
	
	public void show(long id, int left, int top) {
		this.id = id;
		popup.show();
		popup.setPopupPosition(left - popup.getElement().getOffsetWidth(), 
				top - popup.getElement().getOffsetHeight());
		popup.show();
	}

	public void hide() {
		popup.hide();
	}
}
