package ua.nure.ostpc.malibu.shedule.client;

import ua.nure.ostpc.malibu.shedule.shared.LoginInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>Login</code>.
 */
public interface LoginServiceAsync {
	void login(String login, String password, AsyncCallback<LoginInfo> callback)
			throws IllegalArgumentException;
}
