package ua.nure.ostpc.malibu.shedule.client;

import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("")
public interface ScheduleManagerService extends RemoteService {

	List<Period> getAllPeriods() throws IllegalArgumentException;

	Map<Long, Status> getScheduleStatusMap() throws IllegalArgumentException;

	boolean lockSchedule(Long periodId) throws IllegalArgumentException;
}
