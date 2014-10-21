package ua.nure.ostpc.malibu.shedule.shared;

import java.util.LinkedHashMap;
import java.util.Map;

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
