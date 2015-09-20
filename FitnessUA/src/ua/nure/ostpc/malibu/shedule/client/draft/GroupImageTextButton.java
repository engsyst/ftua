package ua.nure.ostpc.malibu.shedule.client.draft;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.client.event.ChangeStyleEvent;
import ua.nure.ostpc.malibu.shedule.client.event.ChangeStyleHandler;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Image;

public class GroupImageTextButton extends ImageTextButton implements ChangeStyleHandler, MouseOverHandler, MouseOutHandler {

	public GroupImageTextButton(Image upImage, String text) {
		super(upImage, text);
		AppState.eventBus.addHandler(ChangeStyleEvent.TYPE, this);
		addMouseOverHandler(this);
		addMouseOutHandler(this);
	}

	public GroupImageTextButton(Image upImage, String text, ClickHandler handler) {
		super(upImage, text, handler);
		AppState.eventBus.addHandler(ChangeStyleEvent.TYPE, this);
		addMouseOverHandler(this);
		addMouseOutHandler(this);
	}

	@Override
	public void onChangeStyle(ChangeStyleEvent event) {
		if (getText().equals(event.getValue())) {
				setStyleDependentName("down", event.add());
				setStyleDependentName("hovering", event.add());
		}
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		AppState.eventBus.fireEvent(new ChangeStyleEvent(getText(), false));
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		AppState.eventBus.fireEvent(new ChangeStyleEvent(getText(), true));
	}
}
