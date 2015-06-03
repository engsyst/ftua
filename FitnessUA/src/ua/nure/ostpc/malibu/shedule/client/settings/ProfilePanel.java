package ua.nure.ostpc.malibu.shedule.client.settings;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.client.LoadingPanel;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.shared.EmployeeUpdateResult;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public abstract class ProfilePanel extends UserPanel {
	private TextBox emailTextBox;
	private TextBox cellPhoneTextBox;
	private TextBox lastNameTextBox;
	private TextBox firstNameTextBox;
	private TextBox secondNameTextBox;
	private TextBox addressTextBox;
	private TextBox passportNumberTextBox;
	private TextBox idNumberTextBox;
	private DateBox birthdayDateBox;

	private String datePattern = "dd.MM.yyyy";

	public ProfilePanel() {
		initPanel();
		addHandlers();
	}

	public ProfilePanel(Employee employee) {
		super(employee.getEmployeeId());
		initPanel();
		setEmployeeData(employee);
		addHandlers();
	}

	protected TextBox getEmailTextBox() {
		return emailTextBox;
	}

	protected TextBox getCellPhoneTextBox() {
		return cellPhoneTextBox;
	}

	protected TextBox getLastNameTextBox() {
		return lastNameTextBox;
	}

	protected TextBox getFirstNameTextBox() {
		return firstNameTextBox;
	}

	protected TextBox getSecondNameTextBox() {
		return secondNameTextBox;
	}

	protected TextBox getAddressTextBox() {
		return addressTextBox;
	}

	protected TextBox getPassportNumberTextBox() {
		return passportNumberTextBox;
	}

	protected TextBox getIdNumberTextBox() {
		return idNumberTextBox;
	}

	protected DateBox getBirthdayDateBox() {
		return birthdayDateBox;
	}

	protected String getDatePattern() {
		return datePattern;
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
		birthdayDateBox.getDatePicker().setYearArrowsVisible(true);
		birthdayDateBox.getDatePicker().setYearAndMonthDropdownVisible(true);
		paramControls.add(birthdayDateBox);

		ArrayList<ErrorLabel> errorLabels = new ArrayList<ErrorLabel>();
		ErrorLabel emailErrorLabel = new ErrorLabel();
		errorLabels.add(emailErrorLabel);
		getErrorLabelMap().put(AppConstants.EMAIL, emailErrorLabel);
		ErrorLabel cellPhoneErrorLabel = new ErrorLabel();
		errorLabels.add(cellPhoneErrorLabel);
		getErrorLabelMap().put(AppConstants.CELL_PHONE, cellPhoneErrorLabel);
		ErrorLabel lastNameErrorLabel = new ErrorLabel();
		errorLabels.add(lastNameErrorLabel);
		getErrorLabelMap().put(AppConstants.LAST_NAME, lastNameErrorLabel);
		ErrorLabel firstNameErrorLabel = new ErrorLabel();
		errorLabels.add(firstNameErrorLabel);
		getErrorLabelMap().put(AppConstants.FIRST_NAME, firstNameErrorLabel);
		ErrorLabel secondNameErrorLabel = new ErrorLabel();
		errorLabels.add(secondNameErrorLabel);
		getErrorLabelMap().put(AppConstants.SECOND_NAME, secondNameErrorLabel);
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

	protected void setEmployeeData(Employee employee) {
		if (employee != null) {
			setEmployeeId(employee.getEmployeeId());
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

	protected abstract void addHandlers();

	protected Map<String, String> getFullEmployeeParamMap() {
		Map<String, String> paramMap = new LinkedHashMap<String, String>();
		paramMap.put(AppConstants.EMAIL, emailTextBox.getValue());
		paramMap.put(AppConstants.CELL_PHONE, cellPhoneTextBox.getValue());
		paramMap.put(AppConstants.LAST_NAME, lastNameTextBox.getValue());
		paramMap.put(AppConstants.FIRST_NAME, firstNameTextBox.getValue());
		paramMap.put(AppConstants.SECOND_NAME, secondNameTextBox.getValue());
		paramMap.put(AppConstants.ADDRESS, addressTextBox.getValue());
		paramMap.put(AppConstants.PASSPORT_NUMBER,
				passportNumberTextBox.getValue());
		paramMap.put(AppConstants.ID_NUMBER, idNumberTextBox.getValue());
		paramMap.put(AppConstants.BIRTHDAY, birthdayDateBox.getTextBox()
				.getText());
		return paramMap;
	}

	protected void insertFullEmployeeProfile(Map<String, String> paramMap,
			final ErrorLabel errorLabel) {
		LoadingPanel.start();
		AppState.userSettingService.insertFullEmployeeProfile(paramMap,
				datePattern, new AsyncCallback<EmployeeUpdateResult>() {

					@Override
					public void onSuccess(EmployeeUpdateResult updateResult) {
						if (updateResult != null) {
							if (updateResult.isResult()) {
								errorLabel.setText("Данные успешно сохранены!");
								getEditButton().setEnabled(true);
								if (updateResult.getEmployee() != null) {
									clearErrorLabelMap();
									setEmployeeData(updateResult.getEmployee());
								}
							} else {
								if (updateResult.getErrorMap() != null) {
									setErrors(updateResult.getErrorMap(),
											errorLabel);
								}
								getEditButton().setEnabled(true);
								getEditButton().setFocus(false);
							}
						}
						LoadingPanel.stop();
					}

					@Override
					public void onFailure(Throwable caught) {
						errorLabel.setText(caught.getMessage());
						getEditButton().setEnabled(true);
						getEditButton().setFocus(false);
						LoadingPanel.stop();
					}
				});
	}

	protected void updateFullEmployeeProfile(Map<String, String> paramMap,
			final ErrorLabel errorLabel) {
		LoadingPanel.start();
		AppState.userSettingService.updateFullEmployeeProfile(paramMap,
				getEmployeeId(), datePattern,
				new AsyncCallback<EmployeeUpdateResult>() {

					@Override
					public void onSuccess(EmployeeUpdateResult updateResult) {
						if (updateResult != null) {
							if (updateResult.isResult()) {
								errorLabel.setText("Данные успешно сохранены!");
								getEditButton().setEnabled(true);
								if (updateResult.getEmployee() != null) {
									clearErrorLabelMap();
									setEmployeeData(updateResult.getEmployee());
								}
							} else {
								if (updateResult.getErrorMap() != null) {
									setErrors(updateResult.getErrorMap(),
											errorLabel);
								}
								getEditButton().setEnabled(true);
								getEditButton().setFocus(false);
							}
						}
						LoadingPanel.stop();
					}

					@Override
					public void onFailure(Throwable caught) {
						errorLabel.setText(caught.getMessage());
						getEditButton().setEnabled(true);
						getEditButton().setFocus(false);
						LoadingPanel.stop();
					}
				});
	}
}
