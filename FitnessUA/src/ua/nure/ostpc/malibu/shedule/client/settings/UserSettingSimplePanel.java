package ua.nure.ostpc.malibu.shedule.client.settings;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.client.UserSettingService;
import ua.nure.ostpc.malibu.shedule.client.UserSettingServiceAsync;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.shared.EmployeeUpdateResult;
import ua.nure.ostpc.malibu.shedule.validator.ClientSideValidator;
import ua.nure.ostpc.malibu.shedule.validator.Validator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.smartgwt.client.util.SC;

public class UserSettingSimplePanel extends SimplePanel {
	private final UserSettingServiceAsync userSettingService = GWT
			.create(UserSettingService.class);

	private ArrayList<VerticalPanel> settingPanelList;
	private ErrorLabel errorLabel;
	private Validator validator = new ClientSideValidator();

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
		userSettingService.getCurrentEmployee(new AsyncCallback<Employee>() {

			@Override
			public void onSuccess(Employee employee) {
				if (employee == null) {
					return;
				}
				createUserEmployeeProfilePanel(employee);
				createUserPrefPanel(employee);
				createUserChangePasswordPanel(employee);
			}

			@Override
			public void onFailure(Throwable caught) {
				setWidget(new Label("Невозможно получить данные с сервера!"));
			}
		});
	}

	private void setData(long employeeId) {
		userSettingService.getScheduleEmployeeById(employeeId,
				new AsyncCallback<Employee>() {

					@Override
					public void onSuccess(Employee result) {
						if (result == null) {
							return;
						}
						createSettingEmployeeProfilePanel(result);
						createSettingChangePasswordPanel(result);
						createUserPrefPanel(result);
					}

					@Override
					public void onFailure(Throwable caught) {
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

	private class SettingEmployeeProfilePanel extends UserPanel {
		protected TextBox emailTextBox;
		protected TextBox cellPhoneTextBox;
		protected TextBox lastNameTextBox;
		protected TextBox firstNameTextBox;
		protected TextBox secondNameTextBox;
		protected TextBox addressTextBox;
		protected TextBox passportNumberTextBox;
		protected TextBox idNumberTextBox;
		protected DateBox birthdayDateBox;

		private String datePattern = "dd.MM.yyyy";

		private SettingEmployeeProfilePanel(Employee employee) {
			super(employee.getEmployeeId());
			initPanel();
			setEmployeeData(employee);
			addHandlers();
		}

		protected void initPanel() {
			ArrayList<Label> labels = new ArrayList<Label>();
			Label emailLabel = new Label("Email:");
			labels.add(emailLabel);
			Label cellPhoneLabel = new Label("Мобильный телефон:");
			labels.add(cellPhoneLabel);
			Label lastNameLabel = new Label("Фамилия:");
			labels.add(lastNameLabel);
			Label firstNameLabel = new Label("Имя:");
			labels.add(firstNameLabel);
			Label secondNameLabel = new Label("Отчество:");
			labels.add(secondNameLabel);
			Label addressLabel = new Label("Адрес:");
			labels.add(addressLabel);
			Label passportNumberLabel = new Label("Номер паспорта:");
			labels.add(passportNumberLabel);
			Label idNumberLabel = new Label("Идентификационный код:");
			labels.add(idNumberLabel);
			Label birthdayLabel = new Label("Дата рождения: ");
			labels.add(birthdayLabel);

			ArrayList<Widget> paramControls = new ArrayList<Widget>();
			emailTextBox = new TextBox();
			paramControls.add(emailTextBox);
			cellPhoneTextBox = new TextBox();
			paramControls.add(cellPhoneTextBox);
			lastNameTextBox = new TextBox();
			paramControls.add(lastNameTextBox);
			firstNameTextBox = new TextBox();
			paramControls.add(firstNameTextBox);
			secondNameTextBox = new TextBox();
			paramControls.add(secondNameTextBox);
			addressTextBox = new TextBox();
			paramControls.add(addressTextBox);
			passportNumberTextBox = new TextBox();
			paramControls.add(passportNumberTextBox);
			idNumberTextBox = new TextBox();
			paramControls.add(idNumberTextBox);
			birthdayDateBox = new DateBox();
			birthdayDateBox.getTextBox().setStyleName(
					new TextBox().getStylePrimaryName());
			DateTimeFormat format = DateTimeFormat.getFormat(datePattern);
			birthdayDateBox.setFormat(new DateBox.DefaultFormat(format));
			paramControls.add(birthdayDateBox);

			ArrayList<ErrorLabel> errorLabels = new ArrayList<ErrorLabel>();
			ErrorLabel emailErrorLabel = new ErrorLabel();
			errorLabels.add(emailErrorLabel);
			getErrorLabelMap().put(AppConstants.EMAIL, emailErrorLabel);
			ErrorLabel cellPhoneErrorLabel = new ErrorLabel();
			errorLabels.add(cellPhoneErrorLabel);
			getErrorLabelMap()
					.put(AppConstants.CELL_PHONE, cellPhoneErrorLabel);
			ErrorLabel lastNameErrorLabel = new ErrorLabel();
			errorLabels.add(lastNameErrorLabel);
			getErrorLabelMap().put(AppConstants.LAST_NAME, lastNameErrorLabel);
			ErrorLabel firstNameErrorLabel = new ErrorLabel();
			errorLabels.add(firstNameErrorLabel);
			getErrorLabelMap()
					.put(AppConstants.FIRST_NAME, firstNameErrorLabel);
			ErrorLabel secondNameErrorLabel = new ErrorLabel();
			errorLabels.add(secondNameErrorLabel);
			getErrorLabelMap().put(AppConstants.SECOND_NAME,
					secondNameErrorLabel);
			ErrorLabel addressErrorLabel = new ErrorLabel();
			errorLabels.add(addressErrorLabel);
			getErrorLabelMap().put(AppConstants.ADDRESS, addressErrorLabel);
			ErrorLabel passportNumberErrorLabel = new ErrorLabel();
			errorLabels.add(passportNumberErrorLabel);
			getErrorLabelMap().put(AppConstants.PASSPORT_NUMBER,
					passportNumberErrorLabel);
			ErrorLabel idNumberErrorLabel = new ErrorLabel();
			errorLabels.add(idNumberErrorLabel);
			getErrorLabelMap().put(AppConstants.ID_NUMBER, idNumberErrorLabel);
			ErrorLabel birthdayErrorLabel = new ErrorLabel();
			errorLabels.add(birthdayErrorLabel);
			getErrorLabelMap().put(AppConstants.BIRTHDAY, birthdayErrorLabel);
			initFlexTable(labels, paramControls, errorLabels);
			initEditButton();
		}

		private void setEmployeeData(Employee employee) {
			if (employee != null) {
				emailTextBox.setValue(employee.getEmail());
				cellPhoneTextBox.setValue(employee.getCellPhone());
				lastNameTextBox.setValue(employee.getLastName());
				firstNameTextBox.setValue(employee.getFirstName());
				secondNameTextBox.setValue(employee.getSecondName());
				addressTextBox.setValue(employee.getAddress());
				passportNumberTextBox.setValue(employee.getPassportNumber());
				idNumberTextBox.setValue(employee.getIdNumber());
				birthdayDateBox.setValue(employee.getBirthday());
			}
		}

		protected void addHandlers() {
			getEditButton().addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					Map<String, String> paramMap = new LinkedHashMap<String, String>();
					paramMap.put(AppConstants.EMAIL, emailTextBox.getValue());
					paramMap.put(AppConstants.CELL_PHONE,
							cellPhoneTextBox.getValue());
					paramMap.put(AppConstants.LAST_NAME,
							lastNameTextBox.getValue());
					paramMap.put(AppConstants.FIRST_NAME,
							firstNameTextBox.getValue());
					paramMap.put(AppConstants.SECOND_NAME,
							secondNameTextBox.getValue());
					paramMap.put(AppConstants.ADDRESS,
							addressTextBox.getValue());
					paramMap.put(AppConstants.PASSPORT_NUMBER,
							passportNumberTextBox.getValue());
					paramMap.put(AppConstants.ID_NUMBER,
							idNumberTextBox.getValue());
					paramMap.put(AppConstants.BIRTHDAY, birthdayDateBox
							.getTextBox().getText());
					Map<String, String> errorMap = validator
							.validateFullEmployeeProfile(paramMap, datePattern);
					if (errorMap != null && errorMap.size() != 0) {
						setErrors(errorMap, errorLabel);
					} else {
						getEditButton().setEnabled(false);
						userSettingService.updateFullEmployeeProfile(paramMap,
								getEmployeeId(), datePattern,
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
													createSettingEmployeeProfilePanel(updateResult
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
									}

									@Override
									public void onFailure(Throwable caught) {
										errorLabel.setText(caught.getMessage());
										getEditButton().setEnabled(true);
										getEditButton().setFocus(false);
									}
								});
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
			lastNameTextBox.setEnabled(false);
			firstNameTextBox.setEnabled(false);
			secondNameTextBox.setEnabled(false);
			addressTextBox.setEnabled(false);
			passportNumberTextBox.setEnabled(false);
			idNumberTextBox.setEnabled(false);
			birthdayDateBox.setEnabled(false);
		}

		@Override
		protected void addHandlers() {
			getEditButton().addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					String email = emailTextBox.getValue();
					String cellPhone = cellPhoneTextBox.getValue();
					Map<String, String> errorMap = validator
							.validateEmployeeProfile(email, cellPhone);
					if (errorMap != null && errorMap.size() != 0) {
						setErrors(errorMap, errorLabel);
					} else {
						getEditButton().setEnabled(false);
						userSettingService.updateEmployeeProfile(email,
								cellPhone, getEmployeeId(),
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
									}

									@Override
									public void onFailure(Throwable caught) {
										errorLabel.setText(caught.getMessage());
										getEditButton().setEnabled(true);
										getEditButton().setFocus(false);
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
					Map<String, String> errorMap = validator
							.validateEmployeePrefs(minDayNumberStr,
									maxDayNumberStr);
					if (errorMap != null && errorMap.size() != 0) {
						setErrors(errorMap, errorLabel);
					} else {
						getEditButton().setEnabled(false);
						int minDayNumber = Integer.parseInt(minDayNumberStr);
						int maxDayNumber = Integer.parseInt(maxDayNumberStr);
						userSettingService.setPreference(minDayNumber,
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
									}

									@Override
									public void onFailure(Throwable caught) {
										errorLabel.setText(caught.getMessage());
										getEditButton().setEnabled(true);
										getEditButton().setFocus(false);
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

					Map<String, String> errorMap = validator
							.validateNewLoginAndPasswordData(newLogin,
									newPassword, newPasswordRepeat);
					if (errorMap != null && errorMap.size() != 0) {
						clearFields();
						setErrors(errorMap, errorLabel);
					} else {
						getEditButton().setEnabled(false);
						userSettingService.changeLoginAndPassword(newLogin,
								newPassword, getEmployeeId(),
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
									}

									@Override
									public void onFailure(Throwable caught) {
										errorLabel.setText(caught.getMessage());
										getEditButton().setEnabled(true);
										getEditButton().setFocus(false);
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
				AppState.scheduleManagerService.getUserByEmployeeId(
						getEmployeeId(), new AsyncCallback<User>() {

							@Override
							public void onSuccess(User result) {
								if (result != null) {
									loginTextBox.setValue(result.getLogin());
								}
							}

							@Override
							public void onFailure(Throwable caught) {
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

					Map<String, String> errorMap = validator
							.validateChangePasswordData(oldPassword,
									newPassword, newPasswordRepeat);
					if (errorMap != null && errorMap.size() != 0) {
						clearFields();
						setErrors(errorMap, errorLabel);
					} else {
						getEditButton().setEnabled(false);
						userSettingService.changePassword(oldPassword,
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
									}

									@Override
									public void onFailure(Throwable caught) {
										errorLabel.setText(caught.getMessage());
										getEditButton().setEnabled(true);
										getEditButton().setFocus(false);
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
