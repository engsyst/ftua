package ua.nure.ostpc.malibu.shedule.shared;

import java.util.LinkedHashMap;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Preference;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

import com.google.gwt.regexp.shared.RegExp;

/**
 * <p>
 * FieldVerifier validates that the name the user enters is valid.
 * </p>
 * <p>
 * This class is in the <code>shared</code> package because we use it in both
 * the client code and on the server. On the client, we verify that the name is
 * valid before sending an RPC request so the user doesn't have to wait for a
 * network round trip to get feedback. On the server, we verify that the name is
 * correct to ensure that the input is correct regardless of where the RPC
 * originates.
 * </p>
 * <p>
 * When creating a class that is used on both the client and the server, be sure
 * that all code is translatable and does not use native JavaScript. Code that
 * is not translatable (such as code that interacts with a database or the file
 * system) cannot be compiled into client-side JavaScript. Code that uses native
 * JavaScript (such as Widgets) cannot be run on the server.
 * </p>
 */
public class FieldVerifier {
	private static RegExp loginRegExp = RegExp.compile("^[a-zA-Z]{3,25}$");
	private static RegExp passwordRegExp = RegExp
			.compile("(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\n])(?=.*[A-Z])(?=.*[a-z]).*$");

	private static final String LOGIN_ERROR = "Логин должен содержать от 3 до 25 латинских символов!";
	private static final String LOGIN__PASSWORD_ERROR = "Пароль должен содержать минимум 8 символов!";
	private static final String SIGNIN__PASSWORD_ERROR = "Пароль должен содержать минимум 8 символов, символы верхнего и нижнего регистра, цифры и спецсимволы!";
	private static final String SHIFTS_NUMBER_ERROR = "Количество смен должно быть больше 0";
	private static final String WORK_HOURS_IN_DAY_ERROR = "Количество рабочих часов в дне должно быть не больше 24";
	private static final String WORK_HOURS_IN_WEEK_ERROR = "Количество рабочих часов в неделе должно быть не больше 'Количество рабочих часов в дне' * 7";
	private static final String WORK_CONTINUS_HOURS_ERROR = "Количество рабочих часов подряд это:\n"
			+ "'Количество рабочих дней' * ('Количество рабочих часов в дне' / 'Количество смен')";

	public static Map<String, String> validatePreferences(String shiftsNumber, 
			String workHoursInDay, String workHoursInWeek, String workContinusHours) {
		Map<String, String> paramErrors = new LinkedHashMap<String, String>();
		int sn = 0;
		int whd = 0;
		int whw = 0;
		int wch = 0;
		try {
			sn = Integer.parseInt(shiftsNumber);
			if (sn <= 0) throw new IllegalArgumentException();
		} catch (Exception e) {
			paramErrors.put(AppConstants.SHIFTS_NUMBER, SHIFTS_NUMBER_ERROR);
		}
		try {
			whd = Integer.parseInt(workHoursInDay);
			if (whd <= 0 || whd > 24) throw new IllegalArgumentException();
		} catch (Exception e) {
			paramErrors.put(AppConstants.WORK_HOURS_IN_DAY, WORK_HOURS_IN_DAY_ERROR);
		}
		try {
			whw = Integer.parseInt(workHoursInWeek);
			if (whw < 0 || whw > (whd * 7)) throw new IllegalArgumentException();
		} catch (Exception e) {
			paramErrors.put(AppConstants.WORK_HOURS_IN_WEEK, WORK_HOURS_IN_WEEK_ERROR);
		}
		try {
			wch = Integer.parseInt(workHoursInWeek);
			if (wch < 0 || wch > whd) throw new IllegalArgumentException();
		} catch (Exception e) {
			paramErrors.put(AppConstants.WORK_CONTINUS_HOURS, WORK_CONTINUS_HOURS_ERROR);
		}
		
		return paramErrors;
	}

	public static Map<String, String> validateLoginData(String login,
			String password) {
		Map<String, String> paramErrors = new LinkedHashMap<String, String>();
		if (!validateLogin(login)) {
			paramErrors.put(AppConstants.LOGIN, LOGIN_ERROR);
		}
		if (!validateLoginPassword(password)) {
			paramErrors.put(AppConstants.PASSWORD, LOGIN__PASSWORD_ERROR);
		}
		return paramErrors;
	}

	public static Map<String, String> validateSigninData(String login,
			String password) {
		Map<String, String> paramErrors = new LinkedHashMap<String, String>();
		if (!validateLogin(login)) {
			paramErrors.put(AppConstants.LOGIN, LOGIN_ERROR);
		}
		if (!validateSigninPassword(password)) {
			paramErrors.put(AppConstants.PASSWORD, SIGNIN__PASSWORD_ERROR);
		}
		return paramErrors;
	}

	public static String validateSigninLogin(String login) {
		if (!validateLogin(login)) {
			return LOGIN_ERROR;
		}
		return null;
	}

	public static String validatePassword(String password) {
		if (!validateSigninPassword(password)) {
			return SIGNIN__PASSWORD_ERROR;
		}
		return null;
	}

	public static boolean validateLogin(String login) {
		return checkStringValue(login, loginRegExp);
	}

	public static boolean validateLoginPassword(String password) {
		boolean result = password != null;
		if (result) {
			result = password.trim().length() >= 8;
		}
		return result;
	}

	public static boolean validateSigninPassword(String password) {
		return checkStringValue(password, passwordRegExp);
	}

	private static boolean checkStringValue(String value, RegExp regExp) {
		if (value != null) {
			return regExp.test(value);
		} else {
			return false;
		}
	}
}
