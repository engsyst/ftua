package ua.nure.ostpc.malibu.shedule.client;

import ua.nure.ostpc.malibu.shedule.entity.Employee;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("")
public interface ScheduleDraftService extends RemoteService {
	Employee getEmployee() throws IllegalArgumentException;
}
