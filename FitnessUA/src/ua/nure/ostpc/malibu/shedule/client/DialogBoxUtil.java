package ua.nure.ostpc.malibu.shedule.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog box util.
 * 
 * @author Volodymyr_Semerkov
 *
 */
public class DialogBoxUtil {

	public static void callDialogBox(SimplePanel sp) {
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setWidth("100%");
		dialogBox.addStyleName("dialogBoxPosition");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Закрыть");
		closeButton.getElement().setId("closeButton");

		closeButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});

		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setSize("100%", "100%");

		Image closeImage = new Image("img/closeButton.png");
		closeImage.setAltText("Закрыть");
		closeImage.setStyleName("closeButton");
		closeImage.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});

		AbsolutePanel topPanel = new AbsolutePanel();
		topPanel.setStyleName("closePanel");
		topPanel.add(closeImage);
		verticalPanel.add(topPanel);

		verticalPanel.add(sp);
		verticalPanel.add(closeButton);
		dialogBox.add(verticalPanel);
		dialogBox.center();
	}
}
