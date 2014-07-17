package ua.nure.ostpc.malibu.shedule.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>Logout</code>.
 */
public interface LogoutServiceAsync {
	void logout(AsyncCallback<Void> callback) throws IllegalArgumentException;
}
