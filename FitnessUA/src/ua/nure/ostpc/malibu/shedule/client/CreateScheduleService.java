package ua.nure.ostpc.malibu.shedule.client;

import java.util.Date;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("")
public interface CreateScheduleService extends RemoteService {

	Date getStartDate() throws IllegalArgumentException;

}
