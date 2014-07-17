package ua.nure.ostpc.malibu.shedule.client;

import java.util.Map;

import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.shared.FieldVerifier;
import ua.nure.ostpc.malibu.shedule.shared.LoginInfo;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class LoginEntryPoint implements EntryPoint {

	/**
	 * Create a remote service proxy to talk to the server-side Login service.
	 */
	private final LoginServiceAsync loginService = GWT
			.create(LoginService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final Label errorLabel = new Label();
		errorLabel.setStyleName("errorLabel");

		RootPanel rootPanel = RootPanel.get("nameFieldContainer");
		rootPanel.setStyleName("mainPanel");
		rootPanel.add(errorLabel, -45, -40);
		errorLabel.setSize("290px", "1px");

		AbsolutePanel absolutePanel = new AbsolutePanel();
		rootPanel.add(absolutePanel, 0, 15);
		absolutePanel.setSize("207px", "150px");

		final TextBox loginField = new TextBox();
		loginField.setMaxLength(50);
		loginField.getElement().setAttribute("placeholder", AppConstants.LOGIN);
		absolutePanel.add(loginField, 12, 21);
		loginField.setSize("173px", "18px");

		final PasswordTextBox passwordField = new PasswordTextBox();
		passwordField.setMaxLength(50);
		passwordField.getElement().setAttribute("placeholder",
				AppConstants.PASSWORD);
		absolutePanel.add(passwordField, 12, 60);
		passwordField.setSize("173px", "16px");

		final Button loginButton = new Button();
		loginButton.setText("Login");
		absolutePanel.add(loginButton, 60, 100);
		loginButton.setSize("84px", "40px");

		loginButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				login();
			}

			private void login() {
				errorLabel.setText("");
				String login = loginField.getText();
				String password = passwordField.getText();
				Map<String, String> errors = FieldVerifier.validateLoginData(
						login, password);
				if (errors.size() != 0) {
					errorLabel.setText(errorMapToString(errors));
					return;
				}
				loginButton.setEnabled(false);
				loginService.login(login, password,
						new AsyncCallback<LoginInfo>() {
							public void onFailure(Throwable caught) {
								errorLabel.setText(AppConstants.SERVER_ERROR);
							}

							public void onSuccess(LoginInfo loginInfo) {
								if (loginInfo.isResult()) {
									Window.Location
											.replace("createSchedule.html");
								} else {
									errorLabel
											.setText(errorMapToString(loginInfo
													.getErrors()));
								}
							}
						});
				passwordField.setText("");
				loginButton.setFocus(false);
				loginButton.setEnabled(true);
			}

			private String errorMapToString(Map<String, String> errors) {
				StringBuilder errorMessage = new StringBuilder();
				String loginError = errors.get(AppConstants.LOGIN);
				String passwordError = errors.get(AppConstants.PASSWORD);
				if (loginError != null) {
					errorMessage.append(loginError);
				}
				if (passwordError != null) {
					if (errorMessage.length() != 0) {
						errorMessage.append("\n");
						errorMessage.append(passwordError);
					} else {
						errorMessage.append(passwordError);
					}
				}
				return errorMessage.toString();
			}
		});
	}
}
