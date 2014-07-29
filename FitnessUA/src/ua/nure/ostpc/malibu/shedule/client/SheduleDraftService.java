package ua.nure.ostpc.malibu.shedule.client;

import ua.nure.ostpc.malibu.shedule.shared.LoginInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("")
public interface SheduleDraftService extends RemoteService {
	LoginInfo login(String login, String password)
			throws IllegalArgumentException;
}
