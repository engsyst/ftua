package ua.nure.ostpc.malibu.shedule.client;

import ua.nure.ostpc.malibu.shedule.Path;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class IndexEntryPoint implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final Button logoutButton = new Button("Logout");
		final Label errorLabel = new Label();

		// We can add style names to widgets
		logoutButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel rootPanel = RootPanel.get("nameFieldContainer");
		// RootPanel.get("sendButtonContainer").add(logoutButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);

		final FormPanel formPanel = new FormPanel();
		rootPanel.add(formPanel, 79, 56);
		formPanel.setSize("241px", "171px");
		formPanel.add(logoutButton);
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.setAction("/logout");

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				formPanel.submit();
				logoutButton.setEnabled(true);
				logoutButton.setFocus(true);
			}
		});

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				formPanel.submit();
				Window.Location.replace(Path.COMMAND__LOGIN);
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					// sendNameToServer();
				}
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		logoutButton.addClickHandler(handler);
	}
}
