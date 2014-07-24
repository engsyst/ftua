package ua.nure.ostpc.malibu.shedule.client;

import java.io.IOException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("")
public interface LogoutService extends RemoteService {
	void logout() throws IllegalArgumentException, IOException;
}
