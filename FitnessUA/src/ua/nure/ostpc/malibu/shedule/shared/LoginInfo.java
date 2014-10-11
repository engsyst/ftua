package ua.nure.ostpc.malibu.shedule.shared;

import java.io.Serializable;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class LoginInfo implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;
	private boolean result;
	private Map<String, String> errors;

	public LoginInfo() {
	}

	public LoginInfo(boolean result, Map<String, String> errors) {
		this.result = result;
		this.errors = errors;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
	}
}
