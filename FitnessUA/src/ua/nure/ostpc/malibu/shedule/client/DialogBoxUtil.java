package ua.nure.ostpc.malibu.shedule.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog box util.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class DialogBoxUtil {

	public static void callDialogBox(SimplePanel simplePanel) {
		callDialogBox(null, simplePanel);
	}

	public static void callDialogBox(String title, Panel panel) {
		final DialogBox dialogBox = new DialogBox();
		dialogBox.addStyleName("dialogBoxPosition");
		dialogBox.setAnimationEnabled(true);

		VerticalPanel verticalPanel = new VerticalPanel();

		Image closeImage = new Image("img/closeButton.png");
		closeImage.setAltText("Закрыть");
		closeImage.setTitle("Закрыть");
		closeImage.setStyleName("closeButton");
		closeImage.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});

		VerticalPanel topPanel = new VerticalPanel();
		topPanel.setStyleName("closePanel");
		topPanel.add(closeImage);
		if (title != null) {
			Label titleLabel = new Label(title);
			titleLabel.setStyleName("dialogBoxTitle");
			topPanel.add(titleLabel);
		}
		verticalPanel.add(topPanel);

		verticalPanel.add(panel);
		dialogBox.add(verticalPanel);

		int left = (Window.getClientWidth() - dialogBox.getOffsetWidth()) >> 1;
		int top = (Window.getClientHeight() - dialogBox.getOffsetHeight()) >> 1;
		dialogBox.setPopupPosition(left - 200, top - 200);
		dialogBox.show();
	}
}
