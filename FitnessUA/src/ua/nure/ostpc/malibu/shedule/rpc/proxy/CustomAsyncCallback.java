package ua.nure.ostpc.malibu.shedule.rpc.proxy;

import ua.nure.ostpc.malibu.shedule.Path;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;

/**
 * AsyncCallback for redirecting user to login page when session expired.
 * 
 * @author Volodymyr_Semerkov
 *
 * @param <T>
 */
public class CustomAsyncCallback<T> implements AsyncCallback<T> {

	private final AsyncCallback<T> asyncCallback;

	public CustomAsyncCallback(AsyncCallback<T> asyncCallback) {
		this.asyncCallback = asyncCallback;
	}

	@Override
	public void onFailure(Throwable caught) {
		if (caught instanceof StatusCodeException
				&& ((StatusCodeException) caught).getStatusCode() == 401) {
			Window.Location.replace(GWT.getHostPageBaseURL()
					+ Path.COMMAND__LOGIN);
			return;
		}
		asyncCallback.onFailure(caught);
	}

	@Override
	public void onSuccess(T result) {
		asyncCallback.onSuccess(result);
	}
}
