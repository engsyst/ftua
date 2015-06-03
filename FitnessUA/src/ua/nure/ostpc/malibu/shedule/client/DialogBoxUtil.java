package ua.nure.ostpc.malibu.shedule.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

	public static void callDialogBox(SimplePanel sp) {
		final DialogBox dialogBox = new DialogBox();
//		dialogBox.setWidth("100%");
//		dialogBox.addStyleName("dialogBoxPosition");
		dialogBox.setAnimationEnabled(true);

		VerticalPanel verticalPanel = new VerticalPanel();
//		verticalPanel.setSize("100%", "100%");

		Image closeImage = new Image("img/closeButton.png");
		closeImage.setAltText("Закрыть");
		closeImage.setStyleName("closeButton");
		closeImage.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});

		VerticalPanel topPanel = new VerticalPanel();
		topPanel.setStyleName("closePanel");
		topPanel.add(closeImage);
		verticalPanel.add(topPanel);

		verticalPanel.add(sp);
		dialogBox.add(verticalPanel);
		dialogBox.center();
	}
	
	public static void callDialogBox(String title, Panel panel) {
		final DialogBox dialogBox = new DialogBox();
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
		Label titleLabel = new Label(title);
		titleLabel.setStyleName("dialogBoxTitle");
		topPanel.add(titleLabel);
		verticalPanel.add(topPanel);

		verticalPanel.add(panel);
		dialogBox.add(verticalPanel);
		dialogBox.center();
	}

	/*public static void callEditingDialogBox(String title, Panel sp) {
		MyEventDialogBox dialogBox = new MyEventDialogBox();
		dialogBox.setAnimationEnabled(true);
		dialogBox.setAutoHideEnabled(true);
		dialogBox.setText(title);
		VerticalPanel panel = new VerticalPanel();
		panel.add(sp);
		dialogBox.add(panel);
		dialogBox.center();
	}*/

}
