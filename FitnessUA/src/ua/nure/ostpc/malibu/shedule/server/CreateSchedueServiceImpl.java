package ua.nure.ostpc.malibu.shedule.server;

import ua.nure.ostpc.malibu.shedule.client.CreateScheduleService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CreateSchedueServiceImpl extends RemoteServiceServlet implements
		CreateScheduleService {

	public String createSchedule(String input) throws IllegalArgumentException {
		return "Schedule";
	}
}
