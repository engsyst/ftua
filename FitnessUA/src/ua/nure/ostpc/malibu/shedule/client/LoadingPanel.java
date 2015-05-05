package ua.nure.ostpc.malibu.shedule.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

public class LoadingPanel {
	private static PopupPanel loadingPopupPanel = new PopupPanel(false, true);

	static {
		Image loadingImage = new Image(GWT.getHostPageBaseURL()
				+ "img/loader.gif");
		loadingPopupPanel.add(loadingImage);
		loadingPopupPanel.setGlassEnabled(true);
		loadingPopupPanel.addStyleName("loadingPanel");
	}

	public static void start() {
		loadingPopupPanel.center();
	}

	public static void stop() {
		loadingPopupPanel.hide();
	}
}
