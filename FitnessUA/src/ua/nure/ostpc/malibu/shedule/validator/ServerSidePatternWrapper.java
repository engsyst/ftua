package ua.nure.ostpc.malibu.shedule.validator;

import java.util.regex.Pattern;

public class ServerSidePatternWrapper implements PatternWrapper {
	private Pattern pattern;

	public ServerSidePatternWrapper(Pattern pattern) {
		this.pattern = pattern;
	}

	@Override
	public boolean check(String value) {
		return pattern.matcher(value).lookingAt();
	}
}
