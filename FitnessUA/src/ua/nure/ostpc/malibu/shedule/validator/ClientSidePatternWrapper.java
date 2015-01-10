package ua.nure.ostpc.malibu.shedule.validator;

import com.google.gwt.regexp.shared.RegExp;

public class ClientSidePatternWrapper implements PatternWrapper {
	private RegExp pattern;

	public ClientSidePatternWrapper(RegExp pattern) {
		this.pattern = pattern;
	}

	@Override
	public boolean check(String value) {
		return pattern.test(value);
	}
}
