package ua.nure.ostpc.malibu.shedule.client.settings;

import java.util.ArrayList;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.client.LoadingImagePanel;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.shared.EmployeeUpdateResult;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.util.SC;

public class UserSettingSimplePanel extends SimplePanel {
	private ArrayList<VerticalPanel> settingPanelList;
	private ErrorLabel errorLabel;

	public UserSettingSimplePanel() {
		initPanel();
		setData();
	}

	public UserSettingSimplePanel(long employeeId) {
		initPanel();
		setData(employeeId);
	}

	private void initPanel() {
		VerticalPanel verticalPanel = new VerticalPanel();
		TabPanel tabPanel = new TabPanel();
		verticalPanel.add(tabPanel);
		errorLabel = new ErrorLabel();
		verticalPanel.add(errorLabel);
		setWidget(verticalPanel);
		settingPanelList = new ArrayList<VerticalPanel>();
		settingPanelList.add(new VerticalPanel());
		settingPanelList.add(new VerticalPanel());
		settingPanelList.add(new VerticalPanel());
		tabPanel.add(settingPanelList.get(0), "Личные данные", true);
		tabPanel.add(settingPanelList.get(1), "Предпочтения", true);
		tabPanel.add(settingPanelList.get(2), "Изменение пароля", true);
		tabPanel.selectTab(0);
	}

	private void setData() {
		LoadingImagePanel.start();
		AppState.userSettingService
				.getCurrentEmployee(new AsyncCallback<Employee>() {

					@Override
					public void onSuccess(Employee employee) {
						if (employee == null) {
							return;
						}
						createUserEmployeeProfilePanel(employee);
						createUserPrefPanel(employee);
						createUserChangePasswordPanel(employee);
						LoadingImagePanel.stop();
					}

					@Override
					public void onFailure(Throwable caught) {
						LoadingImagePanel.stop();
						setWidget(new Label(
								"Невозможно получить данные с сервера!"));
					}
				});
	}

	private void setData(long employeeId) {
		LoadingImagePanel.start();
		AppState.userSettingService.getScheduleEmployeeById(employeeId,
				new AsyncCallback<Employee>() {

					@Override
					public void onSuccess(Employee result) {
						if (result == null) {
							return;
						}
						createSettingEmployeeProfilePanel(result);
						createSettingChangePasswordPanel(result);
						createUserPrefPanel(result);
						LoadingImagePanel.stop();
					}

					@Override
					public void onFailure(Throwable caught) {
						LoadingImagePanel.stop();
						setWidget(new Label(
								"Невозможно получить данные с сервера!"));
					}
				});
	}

	private void createUserEmployeeProfilePanel(Employee employee) {
		UserEmployeeProfilePanel userEmployeeProfilePanel = new UserEmployeeProfilePanel(
				employee);
		settingPanelList.get(0).clear();
		settingPanelList.get(0).add(userEmployeeProfilePanel);
	}

	private void createSettingEmployeeProfilePanel(Employee employee) {
		SettingEmployeeProfilePanel settingEmployeeProfilePanel = new SettingEmployeeProfilePanel(
				employee);
		settingPanelList.get(0).clear();
		settingPanelList.get(0).add(settingEmployeeProfilePanel);
	}

	private void createUserPrefPanel(Employee employee) {
		UserPrefPanel userPrefPanel = new UserPrefPanel(employee);
		settingPanelList.get(1).clear();
		settingPanelList.get(1).add(userPrefPanel);
	}

	private void createUserChangePasswordPanel(Employee employee) {
		UserChangePasswordPanel userChangePasswordPanel = new UserChangePasswordPanel(
				employee);
		settingPanelList.get(2).clear();
		settingPanelList.get(2).add(userChangePasswordPanel);
	}

	private void createSettingChangePasswordPanel(Employee employee) {
		SettingChangePasswordPanel settingChangePasswordPanel = new SettingChangePasswordPanel(
				employee);
		settingPanelList.get(2).clear();
		settingPanelList.get(2).add(settingChangePasswordPanel);
	}

	private class SettingEmployeeProfilePanel extends ProfilePanel {

		private SettingEmployeeProfilePanel(Employee employee) {
			super(employee);
		}

		@Override
		protected void addHandlers() {
			getEditButton().addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					Map<String, String> paramMap = getFullEmployeeParamMap();
					Map<String, String> errorMap = AppState.clientSideValidator
							.validateFullEmployeeProfile(paramMap,
									getDatePattern());
					if (errorMap != null && errorMap.size() != 0) {
						setErrors(errorMap, errorLabel);
					} else {
						getEditButton().setEnabled(false);
						updateFullEmployeeProfile(paramMap, errorLabel);
					}
				}
			});
		}

	}

	private class UserEmployeeProfilePanel extends SettingEmployeeProfilePanel {

		private UserEmployeeProfilePanel(Employee employee) {
			super(employee);
		}

		@Override
		protected void initPanel() {
			super.initPanel();
			getLastNameTextBox().setEnabled(false);
			getFirstNameTextBox().setEnabled(false);
			getSecondNameTextBox().setEnabled(false);
			getAddressTextBox().setEnabled(false);
			getPassportNumberTextBox().setEnabled(false);
			getIdNumberTextBox().setEnabled(false);
			getBirthdayDateBox().setEnabled(false);
		}

		@Override
		protected void addHandlers() {
			getEditButton().addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					String email = getEmailTextBox().getValue();
					String cellPhone = getCellPhoneTextBox().getValue();
					Map<String, String> errorMap = AppState.clientSideValidator
							.validateEmployeeProfile(email, cellPhone);
					if (errorMap != null && errorMap.size() != 0) {
						setErrors(errorMap, errorLabel);
					} else {
						getEditButton().setEnabled(false);
						LoadingImagePanel.start();
						AppState.userSettingService.updateEmployeeProfile(
								email, cellPhone, getEmployeeId(),
								new AsyncCallback<EmployeeUpdateResult>() {

									@Override
									public void onSuccess(
											EmployeeUpdateResult updateResult) {
										if (updateResult != null) {
											if (updateResult.isResult()) {
												errorLabel
														.setText("Данные успешно сохранены!");
												getEditButton()
														.setEnabled(true);
												if (updateResult.getEmployee() != null) {
													createUserEmployeeProfilePanel(updateResult
															.getEmployee());
												}
											} else {
												if (updateResult.getErrorMap() != null) {
													setErrors(updateResult
															.getErrorMap(),
															errorLabel);
												}
												getEditButton()
														.setEnabled(true);
												getEditButton().setFocus(false);
											}
										}
										LoadingImagePanel.stop();
									}

									@Override
									public void onFailure(Throwable caught) {
										errorLabel.setText(caught.getMessage());
										getEditButton().setEnabled(true);
										getEditButton().setFocus(false);
										LoadingImagePanel.stop();
									}
								});
					}
				}
			});
		}

	}

	private class UserPrefPanel extends UserPanel {
		private TextBox minDayNumberTextBox;
		private TextBox maxDayNumberTextBox;

		private UserPrefPanel(Employee employee) {
			super(employee.getEmployeeId());
			initPanel();
			setEmployeeData(employee);
			addHandlers();
		}

		private void initPanel() {
			ArrayList<Label> labels = new ArrayList<Label>();
			Label minDayNumberLabel = new Label(
					"Минимальное кол-во рабочих дней в неделю:");
			labels.add(minDayNumberLabel);
			Label maxDayNumberLabel = new Label(
					"Максимальное кол-во рабочих дней в неделю:");
			labels.add(maxDayNumberLabel);

			ArrayList<Widget> paramControls = new ArrayList<Widget>();
			minDayNumberTextBox = new TextBox();
			paramControls.add(minDayNumberTextBox);
			maxDayNumberTextBox = new TextBox();
			paramControls.add(maxDayNumberTextBox);

			ArrayList<ErrorLabel> errorLabels = new ArrayList<ErrorLabel>();
			ErrorLabel minDayNumberErrorLabel = new ErrorLabel();
			errorLabels.add(minDayNumberErrorLabel);
			getErrorLabelMap().put(AppConstants.MIN_EMP_DAY_NUMBER,
					minDayNumberErrorLabel);
			ErrorLabel maxDayNumberErrorLabel = new ErrorLabel();
			errorLabels.add(maxDayNumberErrorLabel);
			getErrorLabelMap().put(AppConstants.MAX_EMP_DAY_NUMBER,
					maxDayNumberErrorLabel);
			initFlexTable(labels, paramControls, errorLabels);
			initEditButton();
		}

		private void setEmployeeData(Employee employee) {
			minDayNumberTextBox.setText(String.valueOf(employee.getMinDays()));
			maxDayNumberTextBox.setText(String.valueOf(employee.getMaxDays()));
		}

		private void addHandlers() {
			getEditButton().addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					String minDayNumberStr = minDayNumberTextBox.getValue();
					String maxDayNumberStr = maxDayNumberTextBox.getValue();
					Map<String, String> errorMap = AppState.clientSideValidator
							.validateEmployeePrefs(minDayNumberStr,
									maxDayNumberStr);
					if (errorMap != null && errorMap.size() != 0) {
						setErrors(errorMap, errorLabel);
					} else {
						getEditButton().setEnabled(false);
						int minDayNumber = Integer.parseInt(minDayNumberStr);
						int maxDayNumber = Integer.parseInt(maxDayNumberStr);
						LoadingImagePanel.start();
						AppState.userSettingService.setPreference(minDayNumber,
								maxDayNumber, getEmployeeId(),
								new AsyncCallback<EmployeeUpdateResult>() {

									@Override
									public void onSuccess(
											EmployeeUpdateResult updateResult) {
										if (updateResult != null) {
											if (updateResult.isResult()) {
												errorLabel
														.setText("Данные успешно сохранены!");
												getEditButton()
														.setEnabled(true);
												if (updateResult.getEmployee() != null) {
													createUserPrefPanel(updateResult
															.getEmployee());
												}
											} else {
												if (updateResult.getErrorMap() != null) {
													setErrors(updateResult
															.getErrorMap(),
															errorLabel);
												}
												getEditButton()
														.setEnabled(true);
												getEditButton().setFocus(false);
											}
										}
										LoadingImagePanel.stop();
									}

									@Override
									public void onFailure(Throwable caught) {
										errorLabel.setText(caught.getMessage());
										getEditButton().setEnabled(true);
										getEditButton().setFocus(false);
										LoadingImagePanel.stop();
									}
								});
					}
				}
			});
		}

	}

	private abstract class ChangePasswordPanel extends UserPanel {
		protected TextBox loginTextBox;
		protected PasswordTextBox oldPasswordTextBox;
		protected PasswordTextBox newPasswordTextBox;
		protected PasswordTextBox newPasswordRepeatTextBox;

		protected ArrayList<Label> labels = new ArrayList<Label>();
		protected ArrayList<Widget> paramControls = new ArrayList<Widget>();
		protected ArrayList<ErrorLabel> errorLabels = new ArrayList<ErrorLabel>();

		private ChangePasswordPanel(Employee employee) {
			super(employee.getEmployeeId());
			initPanel();
			addHandlers();
			setEmployeeData(employee);
		}

		protected abstract void addHandlers();

		protected abstract void clearFields();

		protected void initPanel() {
			initFlexTable(labels, paramControls, errorLabels);
			initEditButton();
		}

		protected abstract void setEmployeeData(Employee employee);

		protected void initLoginField() {
			Label loginLabel = new Label("Логин:");
			labels.add(loginLabel);
			loginTextBox = new TextBox();
			loginTextBox.setStyleName(new PasswordTextBox()
					.getStylePrimaryName());
			paramControls.add(loginTextBox);
			ErrorLabel loginErrorLabel = new ErrorLabel();
			errorLabels.add(loginErrorLabel);
			getErrorLabelMap().put(AppConstants.LOGIN, loginErrorLabel);
		}

		protected void initOldPasswordField() {
			Label oldPasswordLabel = new Label("Старый пароль:");
			labels.add(oldPasswordLabel);
			oldPasswordTextBox = new PasswordTextBox();
			paramControls.add(oldPasswordTextBox);
			ErrorLabel oldPasswordErrorLabel = new ErrorLabel();
			errorLabels.add(oldPasswordErrorLabel);
			getErrorLabelMap().put(AppConstants.OLD_PASSWORD,
					oldPasswordErrorLabel);
		}

		protected void initNewPasswordField() {
			Label newPasswordLabel = new Label("Новый пароль:");
			labels.add(newPasswordLabel);
			newPasswordTextBox = new PasswordTextBox();
			paramControls.add(newPasswordTextBox);
			ErrorLabel newPasswordErrorLabel = new ErrorLabel();
			errorLabels.add(newPasswordErrorLabel);
			getErrorLabelMap().put(AppConstants.NEW_PASSWORD,
					newPasswordErrorLabel);
		}

		protected void initNewPasswordRepeatField() {
			Label newPasswordRepeatLabel = new Label("Повтор нового пароля:");
			labels.add(newPasswordRepeatLabel);
			newPasswordRepeatTextBox = new PasswordTextBox();
			paramControls.add(newPasswordRepeatTextBox);
			ErrorLabel newPasswordRepeatErrorLabel = new ErrorLabel();
			errorLabels.add(newPasswordRepeatErrorLabel);
			getErrorLabelMap().put(AppConstants.NEW_PASSWORD_REPEAT,
					newPasswordRepeatErrorLabel);
		}

	}

	private class SettingChangePasswordPanel extends ChangePasswordPanel {

		private SettingChangePasswordPanel(Employee employee) {
			super(employee);
		}

		@Override
		protected void initPanel() {
			initLoginField();
			initNewPasswordField();
			initNewPasswordRepeatField();
			super.initPanel();
		}

		@Override
		protected void addHandlers() {
			getEditButton().addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					String newLogin = loginTextBox.getValue();
					String newPassword = newPasswordTextBox.getValue();
					String newPasswordRepeat = newPasswordRepeatTextBox
							.getValue();

					Map<String, String> errorMap = AppState.clientSideValidator
							.validateNewLoginAndPasswordData(newLogin,
									newPassword, newPasswordRepeat);
					if (errorMap != null && errorMap.size() != 0) {
						clearFields();
						setErrors(errorMap, errorLabel);
					} else {
						getEditButton().setEnabled(false);
						LoadingImagePanel.start();
						AppState.userSettingService.changeLoginAndPassword(
								newLogin, newPassword, getEmployeeId(),
								new AsyncCallback<EmployeeUpdateResult>() {

									@Override
									public void onSuccess(
											EmployeeUpdateResult updateResult) {
										if (updateResult != null) {
											if (updateResult.isResult()) {
												errorLabel
														.setText("Логин и пароль успешно изменены!");
												getEditButton()
														.setEnabled(true);
												if (updateResult.getEmployee() != null) {
													createSettingChangePasswordPanel(updateResult
															.getEmployee());
												}
											} else {
												if (updateResult.getErrorMap() != null) {
													setErrors(updateResult
															.getErrorMap(),
															errorLabel);
												}
												clearFields();
												getEditButton()
														.setEnabled(true);
												getEditButton().setFocus(false);
											}
										}
										LoadingImagePanel.stop();
									}

									@Override
									public void onFailure(Throwable caught) {
										errorLabel.setText(caught.getMessage());
										getEditButton().setEnabled(true);
										getEditButton().setFocus(false);
										LoadingImagePanel.stop();
									}
								});
					}
				}
			});
		}

		@Override
		protected void clearFields() {
			newPasswordTextBox.setValue("");
			newPasswordRepeatTextBox.setValue("");
		}

		@Override
		protected void setEmployeeData(Employee employee) {
			if (employee != null) {
				LoadingImagePanel.start();
				AppState.scheduleManagerService.getUserByEmployeeId(
						getEmployeeId(), new AsyncCallback<User>() {

							@Override
							public void onSuccess(User result) {
								if (result != null) {
									loginTextBox.setValue(result.getLogin());
								}
								LoadingImagePanel.stop();
							}

							@Override
							public void onFailure(Throwable caught) {
								LoadingImagePanel.stop();
								SC.warn("Невозможно получить данные о пользователе!");
							}
						});
			}
		}

	}

	private class UserChangePasswordPanel extends ChangePasswordPanel {

		private UserChangePasswordPanel(Employee employee) {
			super(employee);
		}

		@Override
		protected void initPanel() {
			initOldPasswordField();
			initNewPasswordField();
			initNewPasswordRepeatField();
			super.initPanel();
		}

		@Override
		protected void addHandlers() {
			getEditButton().addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					String oldPassword = oldPasswordTextBox.getValue();
					String newPassword = newPasswordTextBox.getValue();
					String newPasswordRepeat = newPasswordRepeatTextBox
							.getValue();

					Map<String, String> errorMap = AppState.clientSideValidator
							.validateChangePasswordData(oldPassword,
									newPassword, newPasswordRepeat);
					if (errorMap != null && errorMap.size() != 0) {
						clearFields();
						setErrors(errorMap, errorLabel);
					} else {
						getEditButton().setEnabled(false);
						LoadingImagePanel.start();
						AppState.userSettingService.changePassword(oldPassword,
								newPassword, getEmployeeId(),
								new AsyncCallback<EmployeeUpdateResult>() {

									@Override
									public void onSuccess(
											EmployeeUpdateResult updateResult) {
										if (updateResult != null) {
											if (updateResult.isResult()) {
												errorLabel
														.setText("Пароль успешно изменен!");
												getEditButton()
														.setEnabled(true);
												if (updateResult.getEmployee() != null) {
													createUserChangePasswordPanel(updateResult
															.getEmployee());
												}
											} else {
												if (updateResult.getErrorMap() != null) {
													setErrors(updateResult
															.getErrorMap(),
															errorLabel);
												}
												getEditButton()
														.setEnabled(true);
												getEditButton().setFocus(false);
											}
										}
										LoadingImagePanel.stop();
									}

									@Override
									public void onFailure(Throwable caught) {
										errorLabel.setText(caught.getMessage());
										getEditButton().setEnabled(true);
										getEditButton().setFocus(false);
										LoadingImagePanel.stop();
									}
								});
					}
				}
			});
		}

		@Override
		protected void clearFields() {
			oldPasswordTextBox.setValue("");
			newPasswordTextBox.setValue("");
			newPasswordRepeatTextBox.setValue("");
		}

		@Override
		protected void setEmployeeData(Employee employee) {
		}

	}

}
