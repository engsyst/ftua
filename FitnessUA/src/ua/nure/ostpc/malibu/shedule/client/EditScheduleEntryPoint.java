package ua.nure.ostpc.malibu.shedule.client;

import ua.nure.ostpc.malibu.shedule.Path;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.FormPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class EditScheduleEntryPoint implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final SubmitButton logoutButton = new SubmitButton("Log out");
		logoutButton.addStyleName("sendButton");

		RootPanel rootPanel = RootPanel.get("сontainer");

		final Label errorLabel = new Label();
		RootPanel.get("errorLabel").add(errorLabel);

		final FormPanel logoutFormPanel = new FormPanel();
		logoutFormPanel.setSize("241px", "171px");
		logoutFormPanel.add(logoutButton);
		logoutFormPanel.setMethod(FormPanel.METHOD_POST);
		logoutFormPanel.setAction(Path.COMMAND__LOGOUT);

		logoutFormPanel.addSubmitHandler(new FormPanel.SubmitHandler() {

			@Override
			public void onSubmit(SubmitEvent event) {
				logoutButton.click();

			}
		});

		logoutFormPanel
				.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {

					@Override
					public void onSubmitComplete(SubmitCompleteEvent event) {
						Window.Location.replace(Path.COMMAND__LOGIN);

					}
				});

		rootPanel.add(logoutFormPanel, 79, 56);
	}
}
