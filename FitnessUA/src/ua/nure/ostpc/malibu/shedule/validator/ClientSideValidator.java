package ua.nure.ostpc.malibu.shedule.validator;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.RegExp;

/**
 * Client side validator.
 * 
 * @author Volodymyr_Semerkov
 *
 */
public class ClientSideValidator extends Validator {

	public ClientSideValidator() {
		setLoginPattern(new ClientSidePatternWrapper(
				RegExp.compile(loginRegExp)));
		setPasswordPattern(new ClientSidePatternWrapper(
				RegExp.compile(passwordRegExp)));
		setEmailPattern(new ClientSidePatternWrapper(
				RegExp.compile(emailRegExp)));
		setCellPhonePattern(new ClientSidePatternWrapper(
				RegExp.compile(cellPhoneRegExp)));
		setNamePattern(new ClientSidePatternWrapper(RegExp.compile(nameRegExp)));
		setPassportPattern(new ClientSidePatternWrapper(
				RegExp.compile(passportNumberRexExp)));
		setIdNumberPattern(new ClientSidePatternWrapper(
				RegExp.compile(idNumberRexExp)));
	}

	@Override
	public boolean validateDate(String date, String datePattern) {
		boolean result = date != null && datePattern != null;
		if (result) {
			try {
				DateTimeFormat format = DateTimeFormat.getFormat(datePattern);
				format.parseStrict(date);
			} catch (IllegalArgumentException e) {
				return false;
			}
		}
		return result;
	}
}
