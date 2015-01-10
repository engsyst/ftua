package ua.nure.ostpc.malibu.shedule.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * Server side validator.
 * 
 * @author Volodymyr_Semerkov
 *
 */
public class ServerSideValidator extends Validator {

	public ServerSideValidator() {
		setLoginPattern(new ServerSidePatternWrapper(
				Pattern.compile(loginRegExp)));
		setPasswordPattern(new ServerSidePatternWrapper(
				Pattern.compile(passwordRegExp)));
		setEmailPattern(new ServerSidePatternWrapper(
				Pattern.compile(emailRegExp)));
		setCellPhonePattern(new ServerSidePatternWrapper(
				Pattern.compile(cellPhoneRegExp)));
		setNamePattern(new ServerSidePatternWrapper(Pattern.compile(nameRegExp)));
		setPassportPattern(new ServerSidePatternWrapper(
				Pattern.compile(passportNumberRexExp)));
		setIdNumberPattern(new ServerSidePatternWrapper(
				Pattern.compile(idNumberRexExp)));
	}

	@Override
	public boolean validateDate(String date, String datePattern) {
		boolean result = date != null && datePattern != null;
		if (result) {
			try {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						datePattern);
				simpleDateFormat.setLenient(false);
				simpleDateFormat.parse(date);
			} catch (ParseException e) {
				return false;
			}
		}
		return result;
	}
}
