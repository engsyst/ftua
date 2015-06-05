package ua.nure.ostpc.malibu.shedule.client.settings;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ControlWrapper extends HTML {

	public ControlWrapper(String html, Widget w) {
		super(html);
		this.setHTML("<div class=\"helpLabel\">" 
				+ html + "</div>"
				+ w.toString());

	}
}
