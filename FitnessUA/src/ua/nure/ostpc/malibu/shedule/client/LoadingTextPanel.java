package ua.nure.ostpc.malibu.shedule.client;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class LoadingTextPanel {
	private static PopupPanel loadingPopupPanel = new PopupPanel(false, true);
	private static Label label = new Label();

	static {
		loadingPopupPanel.add(label);
		label.setStyleName("loadingLabelForText");
		loadingPopupPanel.setGlassEnabled(true);
		loadingPopupPanel.setStyleName("loadingPanel");
	}

	public static void start(String text) {
		label.setText(text != null ? text : "");
		loadingPopupPanel.center();
	}

	public static void stop() {
		label.setText("");
		loadingPopupPanel.hide();
	}
}
