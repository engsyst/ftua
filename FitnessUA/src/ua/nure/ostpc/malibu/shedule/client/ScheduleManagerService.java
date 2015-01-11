package ua.nure.ostpc.malibu.shedule.client;

import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Role;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
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

	void unlockSchedule(Long periodId) throws IllegalArgumentException;

	List<Role> userRoles() throws IllegalArgumentException;

	String getUser() throws IllegalArgumentException;

	Schedule getCurrentSchedule() throws IllegalArgumentException;

	long getFirstDraftPeriod() throws IllegalArgumentException;

	Schedule getScheduleById(long periodId);

	Schedule generate(Schedule schedule) throws IllegalArgumentException;
}
