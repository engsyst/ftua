package ua.nure.ostpc.malibu.shedule.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.shared.EmployeeUpdateResult;
import ua.nure.ostpc.malibu.shedule.validator.ClientSideValidator;
import ua.nure.ostpc.malibu.shedule.validator.Validator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class UserSettingSimplePanel extends SimplePanel {
	private final UserSettingServiceAsync userSettingService = GWT
			.create(UserSettingService.class);

	private ArrayList<AbsolutePanel> settingPanelList;
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
		settingPanelList = new ArrayList<AbsolutePanel>();
		settingPanelList.add(new AbsolutePanel());
		settingPanelList.add(new AbsolutePanel());
		settingPanelList.add(new AbsolutePanel());
		tabPanel.add(settingPanelList.get(0), "Личные данные", true);
		tabPanel.add(settingPanelList.get(1), "Предпочтения", true);
		tabPanel.add(settingPanelList.get(2), "Изменение пароля", true);
		tabPanel.selectTab(0);
	}

	private void setData() {
		userSettingService.getCurrentEmployee(new AsyncCallback<Employee>() {

			@Override
			public void onSuccess(Employee result) {
				if (result == null) {
					return;
				}
				createUserEmployeeProfilePanel(result);
				createUserPanel();
				createPrefPanel(result);
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
						createUserPanel();
						createPrefPanel(result);
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

	private boolean fieldsIsEmpty(ArrayList<Widget> textBoxs) {
		for (int i = 0; i < textBoxs.size(); i++) {
			if ("TextBox".equals(textBoxs.get(i).getClass().getSimpleName())
					|| "PasswordTextBox".equals(textBoxs.get(i).getClass()
							.getSimpleName()))
				if (((TextBox) textBoxs.get(i)).getValue() == null
						|| ((TextBox) textBoxs.get(i)).getValue().isEmpty())
					return true;
			if ("DateBox".equals(textBoxs.get(i).getClass().getSimpleName()))
				if (((DateBox) textBoxs.get(i)).getValue() == null)
					return true;
		}
		return false;
	}

	private void createPrefPanel(Employee emp) {
		final AbsolutePanel absPanel = new AbsolutePanel();

		FlexTable table = new FlexTable();
		table.setBorderWidth(0);

		ArrayList<Label> labelsNotNull = new ArrayList<Label>();
		labelsNotNull.add(new Label("Минимальное:"));
		labelsNotNull.add(new Label("Максимальное:"));

		final ArrayList<Widget> textBoxs = new ArrayList<Widget>();
		textBoxs.add(new TextBox());
		textBoxs.add(new TextBox());

		((TextBox) textBoxs.get(0)).setText(String.valueOf(emp.getMinDays()));
		((TextBox) textBoxs.get(1)).setText(String.valueOf(emp.getMaxDays()));

		final Button editButton = new Button("Изменить");
		editButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (fieldsIsEmpty(textBoxs)) {
					errorLabel.setText("Вы заполнили не все поля");
					return;
				}
				int min, max;
				try {
					min = Integer.parseInt(((TextBox) (textBoxs.get(0)))
							.getValue());
					max = Integer.parseInt(((TextBox) (textBoxs.get(1)))
							.getValue());
					if (min < 0 || max < 0 || min > 7 || max > 7)
						throw new Exception();
				} catch (Exception e) {
					errorLabel
							.setText("Данные должны быть положительными числами меньше или равными 7!");
					return;
				}
				if (min >= max)
					errorLabel
							.setText("Минимальное колчисество должно быть меньше максимального!");
				else {
					final Employee e = new Employee();
					try {
						e.setMinAndMaxDays(min, max);
					} catch (Exception exc) {
						errorLabel.setText(exc.getMessage());
					}
					editButton.setEnabled(false);
					userSettingService.setPreference(e,
							new AsyncCallback<Void>() {

								@Override
								public void onSuccess(Void result) {
									editButton.setEnabled(true);
									errorLabel
											.setText("Данные успешно обновлены.");
									createPrefPanel(e);
								}

								@Override
								public void onFailure(Throwable caught) {
									errorLabel.setText(caught.getMessage());
									editButton.setEnabled(true);
								}
							});
				}
			}
		});

		absPanel.add(new Label("Количество рабчих дней:"));
		for (int i = 0; i < labelsNotNull.size(); i++) {
			table.insertRow(i);
			table.insertCell(i, 0);
			table.setWidget(i, 0, labelsNotNull.get(i));
			table.insertCell(i, 1);
			table.setWidget(i, 1, textBoxs.get(i));
		}
		absPanel.add(table);
		absPanel.add(editButton);
		settingPanelList.get(1).clear();
		settingPanelList.get(1).add(absPanel);
	}

	private void createUserPanel() {
		final AbsolutePanel absPanel = new AbsolutePanel();

		FlexTable table = new FlexTable();
		table.setBorderWidth(0);

		ArrayList<Label> labelsNotNull = new ArrayList<Label>();
		labelsNotNull.add(new Label("Старый пароль:"));
		labelsNotNull.add(new Label("Новый пароль:"));
		labelsNotNull.add(new Label("Повторите новый пароль:"));

		final ArrayList<Widget> textBoxs = new ArrayList<Widget>();
		textBoxs.add(new PasswordTextBox());
		textBoxs.add(new PasswordTextBox());
		textBoxs.add(new PasswordTextBox());

		final Button addButton = new Button("Изменить");
		addButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (fieldsIsEmpty(textBoxs)) {
					errorLabel.setText("Вы заполнили не все поля");
					((PasswordTextBox) (textBoxs.get(0))).setValue("");
					((PasswordTextBox) (textBoxs.get(1))).setValue("");
					((PasswordTextBox) (textBoxs.get(2))).setValue("");
				} else if (!((PasswordTextBox) (textBoxs.get(1))).getValue()
						.equals(((PasswordTextBox) (textBoxs.get(2)))
								.getValue())) {
					errorLabel.setText("Пароли не совпадают");
					((PasswordTextBox) (textBoxs.get(1))).setValue("");
					((PasswordTextBox) (textBoxs.get(2))).setValue("");
				} else if (!validator
						.validateSigninPassword(((PasswordTextBox) textBoxs
								.get(1)).getValue())) {
					((PasswordTextBox) (textBoxs.get(1))).setValue("");
					((PasswordTextBox) (textBoxs.get(2))).setValue("");
					errorLabel
							.setText("Password must contains at least 8 characters, lower-case and upper-case characters, digits, wildcard characters!");
				} else {
					addButton.setEnabled(false);
					userSettingService.setPass(
							((PasswordTextBox) textBoxs.get(0)).getValue(),
							((PasswordTextBox) textBoxs.get(1)).getValue(),
							new AsyncCallback<Void>() {

								@Override
								public void onSuccess(Void result) {
									addButton.setEnabled(true);
									errorLabel
											.setText("Пароль успешно изменен!");
									createUserPanel();
								}

								@Override
								public void onFailure(Throwable caught) {
									errorLabel.setText(caught.getMessage());
									addButton.setEnabled(true);
								}
							});
				}
			}
		});

		for (int i = 0; i < labelsNotNull.size(); i++) {
			table.insertRow(i);
			table.insertCell(i, 0);
			table.setWidget(i, 0, labelsNotNull.get(i));
			table.insertCell(i, 1);
			table.setWidget(i, 1, textBoxs.get(i));
		}
		absPanel.add(table);
		absPanel.add(addButton);
		settingPanelList.get(2).clear();
		settingPanelList.get(2).add(absPanel);
	}

	private class SettingEmployeeProfilePanel extends AbsolutePanel {
		protected TextBox emailTextBox;
		protected TextBox cellPhoneTextBox;
		protected TextBox lastNameTextBox;
		protected TextBox firstNameTextBox;
		protected TextBox secondNameTextBox;
		protected TextBox addressTextBox;
		protected TextBox passportNumberTextBox;
		protected TextBox idNumberTextBox;
		protected DateBox birthdayDateBox;
		protected Button editButton;
		protected long employeeId;

		private Map<String, ErrorLabel> errorLabelMap;
		private String datePattern = "dd.MM.yyyy";

		private SettingEmployeeProfilePanel(Employee employee) {
			this.employeeId = employee.getEmployeeId();
			initPanel();
			setEmployeeData(employee);
			addHandlers();
		}

		protected void initPanel() {
			FlexTable flexTable = new FlexTable();
			flexTable.setBorderWidth(0);

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

			ArrayList<Label> errorLabels = new ArrayList<Label>();
			errorLabelMap = new LinkedHashMap<String, ErrorLabel>();
			ErrorLabel emailErrorLabel = new ErrorLabel();
			errorLabels.add(emailErrorLabel);
			errorLabelMap.put(AppConstants.EMAIL, emailErrorLabel);
			ErrorLabel cellPhoneErrorLabel = new ErrorLabel();
			errorLabels.add(cellPhoneErrorLabel);
			errorLabelMap.put(AppConstants.CELL_PHONE, cellPhoneErrorLabel);
			ErrorLabel lastNameErrorLabel = new ErrorLabel();
			errorLabels.add(lastNameErrorLabel);
			errorLabelMap.put(AppConstants.LAST_NAME, lastNameErrorLabel);
			ErrorLabel firstNameErrorLabel = new ErrorLabel();
			errorLabels.add(firstNameErrorLabel);
			errorLabelMap.put(AppConstants.FIRST_NAME, firstNameErrorLabel);
			ErrorLabel secondNameErrorLabel = new ErrorLabel();
			errorLabels.add(secondNameErrorLabel);
			errorLabelMap.put(AppConstants.SECOND_NAME, secondNameErrorLabel);
			ErrorLabel addressErrorLabel = new ErrorLabel();
			errorLabels.add(addressErrorLabel);
			errorLabelMap.put(AppConstants.ADDRESS, addressErrorLabel);
			ErrorLabel passportNumberErrorLabel = new ErrorLabel();
			errorLabels.add(passportNumberErrorLabel);
			errorLabelMap.put(AppConstants.PASSPORT_NUMBER,
					passportNumberErrorLabel);
			ErrorLabel idNumberErrorLabel = new ErrorLabel();
			errorLabels.add(idNumberErrorLabel);
			errorLabelMap.put(AppConstants.ID_NUMBER, idNumberErrorLabel);
			ErrorLabel birthdayErrorLabel = new ErrorLabel();
			errorLabels.add(birthdayErrorLabel);
			errorLabelMap.put(AppConstants.BIRTHDAY, birthdayErrorLabel);
			for (int i = 0; i < labels.size(); i++) {
				flexTable.insertRow(i);
				flexTable.insertCell(i, 0);
				flexTable.setWidget(i, 0, labels.get(i));
				flexTable.insertCell(i, 1);
				flexTable.setWidget(i, 1, paramControls.get(i));
				flexTable.insertCell(i, 2);
				flexTable.setWidget(i, 2, errorLabels.get(i));
			}
			add(flexTable);
			editButton = new Button("Изменить");
			add(editButton);
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
			editButton.addClickHandler(new ClickHandler() {

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
						setErrors(errorMap);
					} else {
						editButton.setEnabled(false);
						userSettingService.updateFullEmployeeProfile(paramMap,
								employeeId, datePattern,
								new AsyncCallback<EmployeeUpdateResult>() {

									@Override
									public void onSuccess(
											EmployeeUpdateResult updateResult) {
										if (updateResult != null) {
											if (updateResult.isResult()) {
												errorLabel
														.setText("Данные успешно сохранены!");
												editButton.setEnabled(true);
												if (updateResult.getEmployee() != null) {
													createSettingEmployeeProfilePanel(updateResult
															.getEmployee());
												}
											} else {
												if (updateResult.getErrorMap() != null) {
													setErrors(updateResult
															.getErrorMap());
												}
												editButton.setEnabled(true);
											}
										}
									}

									@Override
									public void onFailure(Throwable caught) {
										errorLabel.setText(caught.getMessage());
										editButton.setEnabled(true);
									}
								});
					}
				}
			});
		}

		protected void setErrors(Map<String, String> errorMap) {
			Iterator<Entry<String, ErrorLabel>> it = errorLabelMap.entrySet()
					.iterator();
			while (it.hasNext()) {
				Entry<String, ErrorLabel> entry = it.next();
				String key = entry.getKey();
				ErrorLabel errorLabel = entry.getValue();
				String errorMessage = errorMap.get(key);
				errorMessage = errorMessage != null ? errorMessage : "";
				errorLabel.setText(errorMessage);
			}
			errorLabel.setText("Не все поля заполнены корректно!");
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
			editButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					String email = emailTextBox.getValue();
					String cellPhone = cellPhoneTextBox.getValue();
					Map<String, String> errorMap = validator
							.validateEmployeeProfile(email, cellPhone);
					if (errorMap != null && errorMap.size() != 0) {
						setErrors(errorMap);
					} else {
						editButton.setEnabled(false);
						userSettingService.updateEmployeeProfile(email,
								cellPhone, employeeId,
								new AsyncCallback<EmployeeUpdateResult>() {

									@Override
									public void onSuccess(
											EmployeeUpdateResult updateResult) {
										if (updateResult != null) {
											if (updateResult.isResult()) {
												errorLabel
														.setText("Данные успешно сохранены!");
												editButton.setEnabled(true);
												if (updateResult.getEmployee() != null) {
													createUserEmployeeProfilePanel(updateResult
															.getEmployee());
												}
											} else {
												if (updateResult.getErrorMap() != null) {
													setErrors(updateResult
															.getErrorMap());
												}
												editButton.setEnabled(true);
											}
										}
									}

									@Override
									public void onFailure(Throwable caught) {
										errorLabel.setText(caught.getMessage());
										editButton.setEnabled(true);
									}
								});
					}
				}
			});
		}

	}

	private class ErrorLabel extends Label {

		private ErrorLabel() {
			super();
			setStyleName("serverResponseLabelError");
		}

	}

}
