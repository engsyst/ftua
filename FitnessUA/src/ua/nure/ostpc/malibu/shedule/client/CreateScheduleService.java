package ua.nure.ostpc.malibu.shedule.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("createSchedule")
public interface CreateScheduleService extends RemoteService {
	String createSchedule(String name) throws IllegalArgumentException;
}
