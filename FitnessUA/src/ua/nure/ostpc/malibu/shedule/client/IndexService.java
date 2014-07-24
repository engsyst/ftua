package ua.nure.ostpc.malibu.shedule.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("")
public interface IndexService extends RemoteService {
	void index() throws IllegalArgumentException;
}
