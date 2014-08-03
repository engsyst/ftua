package ua.nure.ostpc.malibu.shedule.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("")
public interface CreateScheduleService extends RemoteService {

	Date getStartDate() throws IllegalArgumentException;

	List<Club> getDependentClubs() throws IllegalArgumentException;

	Map<Long, List<Employee>> getEmployeesByClubsId(List<Long> clubsId)
			throws IllegalArgumentException;
}
