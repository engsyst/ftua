package ua.nure.ostpc.malibu.shedule.server;

import ua.nure.ostpc.malibu.shedule.client.CreateScheduleService;
import ua.nure.ostpc.malibu.shedule.shared.FieldVerifier;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CreateSchedueServiceImpl extends RemoteServiceServlet implements
		CreateScheduleService {

	public String createSchedule(String input) throws IllegalArgumentException {
		if (!FieldVerifier.isValidName(input)) {
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}
		return "Schedule";
	}
}
