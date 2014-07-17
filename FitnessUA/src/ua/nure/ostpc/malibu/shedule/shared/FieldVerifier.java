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

	public static Map<String, String> validateLoginData(String login,
			String password) {
		Map<String, String> paramErrors = new LinkedHashMap<String, String>();
		if (!validateLogin(login)) {
			paramErrors.put(AppConstants.LOGIN,
					"Login must contains at least 3 latin characters!");
		}
		if (!validatePassword(password)) {
			paramErrors
					.put(AppConstants.PASSWORD,
							"Password must contains more than 8 characters, lower-case and upper-case characters, digits, wildcard characters!");
		}
		return paramErrors;
	}

	public static boolean validateLogin(String login) {
		return checkStringValue(login, loginRegExp);
	}

	public static boolean validatePassword(String password) {
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
