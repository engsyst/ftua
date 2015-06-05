package ua.nure.ostpc.malibu.shedule.client.settings;

import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.shared.DirectionEstimator;
import com.google.gwt.user.client.ui.Label;

public class HelpLabel extends Label {
	
	public HelpLabel() {
		super();
		setStyleName("helpLabel");
	}

	public HelpLabel(Element element) {
		super(element);
		setStyleName("helpLabel");
	}

	public HelpLabel(String text, boolean wordWrap) {
		super(text, wordWrap);
		setStyleName("helpLabel");
	}

	public HelpLabel(String text, Direction dir) {
		super(text, dir);
		setStyleName("helpLabel");
	}

	public HelpLabel(String text, DirectionEstimator directionEstimator) {
		super(text, directionEstimator);
		setStyleName("helpLabel");
	}

	public HelpLabel(String text) {
		super(text);
		setStyleName("helpLabel");
	}

}
