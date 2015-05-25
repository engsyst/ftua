package ua.nure.ostpc.malibu.shedule.client;

import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.DraftViewData;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Shift;
import ua.nure.ostpc.malibu.shedule.shared.AssignmentInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("")
public interface ScheduleDraftService extends RemoteService {

	Employee getEmployee() throws IllegalArgumentException;

	Map<Club, List<Employee>> getEmpToClub(long periodId);

	Schedule getScheduleById(long periodId);

	boolean updateShift(AssignmentInfo inform, Employee employee);

	Schedule updateShift(Shift shift, Long periodId);
	
	DraftViewData getDraftView(long id) throws IllegalArgumentException;

}
