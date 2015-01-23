package ua.nure.ostpc.malibu.shedule.client.module;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.client.StyleConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

public class ModulePanelItem {
	private HorizontalPanel panel;
	private Image img;
	private InlineLabel label;
	
	public HorizontalPanel getPanel() {
		return panel;
	}

	public Image getImg() {
		return img;
	}

	public InlineLabel getLabel() {
		return label;
	}
	
	public <H extends EventHandler> HandlerRegistration addHandler(H handler, GwtEvent.Type<H> type) {
		return panel.addHandler(handler, type);
	}

	public <H extends EventHandler> ModulePanelItem(String text, String icon, 
			Boolean enabled) { //, GwtEvent<H> event) {
		panel = new HorizontalPanel();
		panel.addStyleName(StyleConstants.STYLE_MODULE_ITEM_PANEL);
		img = new Image(icon);
		panel.add(img);
		img.addStyleName(StyleConstants.STYLE_MODULE_ITEM_ICON);
		
		// TODO Set width
		panel.setCellWidth(img, new Integer(img.getWidth() + 10).toString());
		
		label = new InlineLabel(text);
		panel.add(label);
		img.setTitle(text);
		label.addStyleName(StyleConstants.STYLE_MODULE_ITEM_LABEL);
		panel.setCellVerticalAlignment(label, HasVerticalAlignment.ALIGN_MIDDLE);
//		AppState.eventBus.fireEvent(event);
	}

}
