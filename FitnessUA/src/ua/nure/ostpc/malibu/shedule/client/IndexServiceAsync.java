package ua.nure.ostpc.malibu.shedule.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>IndexService</code>.
 */
public interface IndexServiceAsync {
	void index(AsyncCallback<Void> callback) throws IllegalArgumentException;
}
