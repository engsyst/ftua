package ua.nure.ostpc.malibu.shedule.validator;

import java.util.LinkedHashMap;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

/**
 * Validator.
 *
 * @author Volodymyr_Semerkov
 */
public abstract class Validator {
	protected static String loginRegExp = "^[a-zA-Z]{3,25}$";
	protected static String passwordRegExp = "(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\n])(?=.*[A-Z])(?=.*[a-z]).*$";
	protected static String emailRegExp = "^([a-zA-Z0-9_\\.-]+)@([a-zA-Z0-9_\\.-]+)\\.([a-zA-Z\\.]{2,6})$";
	protected static String cellPhoneRegExp = "^[\\d]{10}$";
	protected static String nameRegExp = "^[а-яА-Я]{2,30}$";
	protected static String passportNumberRexExp = "^(([а-яА-Я]{2})\\d{6})$";
	protected static String idNumberRexExp = "^[\\d]{10}$";

	private static final String LOGIN_ERROR = "Логин должен содержать от 3 до 25 латинских символов!";
	private static final String LOGIN__PASSWORD_ERROR = "Пароль должен содержать минимум 8 символов!";
	private static final String SIGNIN__PASSWORD_ERROR = "Пароль должен содержать минимум 8 символов, символы верхнего и нижнего регистра, цифры и спецсимволы!";
	private static final String EMAIL_ERROR = "Некорректно указан адрес электронной почты!";
	private static final String CELL_PHONE_ERROR = "Номер телефона должен содержать 10 цифр!";
	private static final String LAST_NAME_ERROR = "Фамилия должна содержать от 2 до 30 букв!";
	private static final String FIRST_NAME_ERROR = "Имя должно содержать от 2 до 30 букв!";
	private static final String SECOND_NAME_ERROR = "Отчество должно содержать от 2 до 30 букв!";
	private static final String ADDRESS_ERROR = "Адрес должен содержать от 3 до 100 символов!";
	private static final String PASSPORT_NUMBER_ERROR = "Номер паспорта должен содержать 2 буквы и 6 цифр!";
	private static final String ID_NUMBER_ERROR = "Идентификационный код должен содержать 10 цифр!";
	private static final String BIRTHDAY_ERROR = "Некорректно указана дата рождения!";

	private PatternWrapper loginPattern;
	private PatternWrapper passwordPattern;
	private PatternWrapper emailPattern;
	private PatternWrapper cellPhonePattern;
	private PatternWrapper namePattern;
	private PatternWrapper passportPattern;
	private PatternWrapper idNumberPattern;

	public abstract boolean validateDate(String date, String datePattern);

	public void setLoginPattern(PatternWrapper loginPattern) {
		this.loginPattern = loginPattern;
	}

	public void setPasswordPattern(PatternWrapper passwordPattern) {
		this.passwordPattern = passwordPattern;
	}

	public void setEmailPattern(PatternWrapper emailPattern) {
		this.emailPattern = emailPattern;
	}

	public void setCellPhonePattern(PatternWrapper cellPhonePattern) {
		this.cellPhonePattern = cellPhonePattern;
	}

	public void setNamePattern(PatternWrapper namePattern) {
		this.namePattern = namePattern;
	}

	public void setPassportPattern(PatternWrapper passportPattern) {
		this.passportPattern = passportPattern;
	}

	public void setIdNumberPattern(PatternWrapper idNumberPattern) {
		this.idNumberPattern = idNumberPattern;
	}

	public boolean validateLoginPassword(String password) {
		boolean result = password != null;
		if (result) {
			result = password.trim().length() >= 8;
		}
		return result;
	}

	public boolean validateAddress(String address) {
		boolean result = address != null;
		if (result) {
			int addressLength = address.trim().length();
			result = addressLength >= 3 && addressLength <= 1000;
		}
		return result;
	}

	public Map<String, String> validateLoginData(String login, String password) {
		Map<String, String> paramErrors = new LinkedHashMap<String, String>();
		if (!validateLogin(login)) {
			paramErrors.put(AppConstants.LOGIN, LOGIN_ERROR);
		}
		if (!validateLoginPassword(password)) {
			paramErrors.put(AppConstants.PASSWORD, LOGIN__PASSWORD_ERROR);
		}
		return paramErrors;
	}

	public Map<String, String> validateSigninData(String login, String password) {
		Map<String, String> paramErrors = new LinkedHashMap<String, String>();
		if (!validateLogin(login)) {
			paramErrors.put(AppConstants.LOGIN, LOGIN_ERROR);
		}
		if (!validateSigninPassword(password)) {
			paramErrors.put(AppConstants.PASSWORD, SIGNIN__PASSWORD_ERROR);
		}
		return paramErrors;
	}

	public Map<String, String> validateEmployeePersonalData(
			Map<String, String> paramMap, String datePattern) {
		Map<String, String> paramErrors = new LinkedHashMap<String, String>();
		if (!validateEmail(paramMap.get(AppConstants.EMAIL))) {
			paramErrors.put(AppConstants.EMAIL, EMAIL_ERROR);
		}
		if (!validateCellPhone(paramMap.get(AppConstants.CELL_PHONE))) {
			paramErrors.put(AppConstants.CELL_PHONE, CELL_PHONE_ERROR);
		}
		if (!validateName(paramMap.get(AppConstants.LAST_NAME))) {
			paramErrors.put(AppConstants.LAST_NAME, LAST_NAME_ERROR);
		}
		if (!validateName(paramMap.get(AppConstants.FIRST_NAME))) {
			paramErrors.put(AppConstants.FIRST_NAME, FIRST_NAME_ERROR);
		}
		if (!validateName(paramMap.get(AppConstants.SECOND_NAME))) {
			paramErrors.put(AppConstants.SECOND_NAME, SECOND_NAME_ERROR);
		}
		if (!validateAddress(paramMap.get(AppConstants.ADDRESS))) {
			paramErrors.put(AppConstants.ADDRESS, ADDRESS_ERROR);
		}
		if (!validatePassportNumber(paramMap.get(AppConstants.PASSPORT_NUMBER))) {
			paramErrors
					.put(AppConstants.PASSPORT_NUMBER, PASSPORT_NUMBER_ERROR);
		}
		if (!validateIdNumber(paramMap.get(AppConstants.ID_NUMBER))) {
			paramErrors.put(AppConstants.ID_NUMBER, ID_NUMBER_ERROR);
		}
		if (!validateDate(paramMap.get(AppConstants.BIRTHDAY), datePattern)) {
			paramErrors.put(AppConstants.BIRTHDAY, BIRTHDAY_ERROR);
		}
		return paramErrors;
	}

	public String validateSigninLogin(String login) {
		if (!validateLogin(login)) {
			return LOGIN_ERROR;
		}
		return null;
	}

	public String validatePassword(String password) {
		if (!validateSigninPassword(password)) {
			return SIGNIN__PASSWORD_ERROR;
		}
		return null;
	}

	public boolean validateLogin(String login) {
		return checkStringValue(login, loginPattern);
	}

	public boolean validateSigninPassword(String password) {
		return checkStringValue(password, passwordPattern);
	}

	public boolean validateEmail(String email) {
		return checkStringValue(email, emailPattern);
	}

	public boolean validateCellPhone(String cellPhone) {
		return checkStringValue(cellPhone, cellPhonePattern);
	}

	public boolean validateName(String name) {
		return checkStringValue(name, namePattern);
	}

	public boolean validatePassportNumber(String passportNumber) {
		return checkStringValue(passportNumber, passportPattern);
	}

	public boolean validateIdNumber(String idNumber) {
		return checkStringValue(idNumber, idNumberPattern);
	}

	private boolean checkStringValue(String value, PatternWrapper pattern) {
		if (value != null || pattern == null) {
			return pattern.check(value);
		}
		return false;
	}
}
