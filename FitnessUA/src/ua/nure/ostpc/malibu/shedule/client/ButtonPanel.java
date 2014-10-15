package ua.nure.ostpc.malibu.shedule.client;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class ButtonPanel extends SimplePanel {
	public ButtonPanel(ClickHandler[] clicks, String[] names) {
		HorizontalPanel root = new HorizontalPanel();
		root.setSpacing(5);
		for(int i = 0;i<clicks.length; i++){
			root.add(new Button(names[i], clicks[i]));
		}
		add(root);
		root.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
	}
}
