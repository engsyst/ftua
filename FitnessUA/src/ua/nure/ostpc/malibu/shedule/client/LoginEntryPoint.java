package ua.nure.ostpc.malibu.shedule.client;

import java.util.Map;

import ua.nure.ostpc.malibu.shedule.Path;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.shared.FieldVerifier;
import ua.nure.ostpc.malibu.shedule.shared.LoginInfo;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
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
		RootPanel rootPanel = RootPanel.get("loginPanel");
		rootPanel.setStyleName("loginPanel");

		final Label titleLabel = new Label();
		titleLabel.setStyleName("title");
		titleLabel.setText("Login");
		rootPanel.add(titleLabel, 70, 10);

		final Label errorLabel = new Label();
		errorLabel.setStyleName("errorLabel");
		errorLabel.setSize("290px", "1px");
		rootPanel.add(errorLabel, -45, 70);

		AbsolutePanel absolutePanel = new AbsolutePanel();
		rootPanel.add(absolutePanel, 0, 5);
		absolutePanel.setSize("207px", "300px");

		final TextBox loginField = new TextBox();
		loginField.setMaxLength(50);
		loginField.getElement().setAttribute("placeholder", "Login");
		absolutePanel.add(loginField, 12, 151);
		loginField.setSize("173px", "18px");
		loginField.setFocus(true);

		final PasswordTextBox passwordField = new PasswordTextBox();
		passwordField.setMaxLength(50);
		passwordField.getElement().setAttribute("placeholder", "Password");
		absolutePanel.add(passwordField, 12, 195);
		passwordField.setSize("173px", "16px");

		final Button loginButton = new Button();
		loginButton.setText("Login");
		absolutePanel.add(loginButton, 60, 240);
		loginButton.setSize("84px", "40px");

		class LoginHandler implements ClickHandler, KeyUpHandler {

			public void onClick(ClickEvent event) {
				login();
			}

			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					login();
				}
			}

			private void login() {
				errorLabel.setText("");
				String login = loginField.getText();
				String password = passwordField.getText();
				Map<String, String> errors = FieldVerifier.validateLoginData(
						login, password);
				if (errors.size() != 0) {
					errorLabel.setText(errorMapToString(errors));
					loginField.setFocus(true);
					return;
				}
				loginButton.setEnabled(false);
				loginService.login(login, password,
						new AsyncCallback<LoginInfo>() {
							public void onFailure(Throwable caught) {
								errorLabel.setText(AppConstants.SERVER_ERROR);
								passwordField.setText("");
								passwordField.setFocus(true);
							}

							public void onSuccess(LoginInfo loginInfo) {
								if (loginInfo.isResult()) {
									if (!Window.Location.getPath().contains(
											Path.COMMAND__LOGIN)
											&& !Window.Location
													.getPath()
													.contains(
															Path.COMMAND__LOGOUT)) {
										Window.Location.reload();
									} else {
										Window.Location
												.replace(Path.COMMAND__SCHEDULE_MANAGER);
									}
								} else {
									errorLabel
											.setText(errorMapToString(loginInfo
													.getErrors()));
									passwordField.setText("");
									passwordField.setFocus(true);
								}
							}
						});
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
		}

		LoginHandler loginHandler = new LoginHandler();
		loginButton.addClickHandler(loginHandler);
		loginButton.addKeyUpHandler(loginHandler);
		loginField.addKeyUpHandler(loginHandler);
		passwordField.addKeyUpHandler(loginHandler);
	}
}
