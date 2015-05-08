package ua.nure.ostpc.malibu.shedule.client.manage;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;

public class PopupButton extends Image {

	protected PopupMenu menu;

	public PopupButton(String id, String url, PopupMenu popup) {
		super(url);
		getElement().setId(id);
		if (popup == null)
			popup = new PopupMenu();
		this.menu = popup;
		getElement().setId(id);
		menu.setAutoHideEnabled(true);

		addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				PopupButton.this.menu.show();
				PopupButton.this.menu.setPopupPosition(
						event.getClientX() - PopupButton.this.menu.getElement().getOffsetWidth(), 
						event.getClientY() - PopupButton.this.menu.getElement().getOffsetHeight());
				PopupButton.this.menu.show();
			}
		});
	}
	
	public PopupMenu getPopup() {
		return menu;
	}

	public void setPopup(PopupMenu popup) {
		this.menu = popup;
	}
	
	public void addPopupItem(String text, ScheduledCommand cmd) {
		menu.addPopupItem(text, cmd);
	}
}
