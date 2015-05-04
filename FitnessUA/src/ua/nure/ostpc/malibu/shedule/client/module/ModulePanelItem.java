package ua.nure.ostpc.malibu.shedule.client.module;

import ua.nure.ostpc.malibu.shedule.client.StyleConstants;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

public class ModulePanelItem {
	private FlexTable panel;
	private Image img;
	private InlineLabel label;

	public FlexTable getPanel() {
		return panel;
	}

	public Image getImg() {
		return img;
	}

	public InlineLabel getLabel() {
		return label;
	}

	public <H extends EventHandler> HandlerRegistration addHandler(H handler,
			GwtEvent.Type<H> type) {
		return panel.addHandler(handler, type);
	}

	public void addClickHandler(ClickHandler handler) {
		panel.addClickHandler(handler);
	}

	public <H extends EventHandler> ModulePanelItem(String text, String icon,
			Boolean enabled) { // , GwtEvent<H> event) {
		panel = new FlexTable();
		panel.insertRow(0);
		panel.addStyleName(StyleConstants.STYLE_MODULE_ITEM_PANEL);
		panel.insertCell(0, 0);
		panel.insertCell(0, 1);

		img = new Image(icon);
		img.addStyleName(StyleConstants.STYLE_MODULE_ITEM_ICON);
		panel.setWidget(0, 0, img);
		panel.getCellFormatter().addStyleName(0, 0,
				StyleConstants.STYLE_MODULE_ITEM_ICON_PANEL);

		label = new InlineLabel(text);
		label.addStyleName(StyleConstants.STYLE_MODULE_ITEM_LABEL);
		panel.setWidget(0, 1, label);
		panel.getCellFormatter().addStyleName(0, 1,
				StyleConstants.STYLE_MODULE_ITEM_LABEL_PANEL);
	}

	public void addStyleName(String style) {
		panel.addStyleName(style);
	}

	public void removeStyleName(String style) {
		panel.removeStyleName(style);
	}
}
