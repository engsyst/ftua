package ua.nure.ostpc.malibu.shedule.client.draft;

import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.DraftViewData;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.shared.AssignmentInfo;
import ua.nure.ostpc.malibu.shedule.shared.AssignmentResultInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>SheduleDraft</code>.
 */
public interface ScheduleDraftServiceAsync {

	void getEmployee(AsyncCallback<Employee> callback)
			throws IllegalArgumentException;

	void getEmpToClub(long periodId,
			AsyncCallback<Map<Club, List<Employee>>> callback);

	void getScheduleById(long periodId, AsyncCallback<Schedule> callback);

	void updateShift(AssignmentInfo assignmentInfo,
			AsyncCallback<AssignmentResultInfo> callback);

	void getDraftView(long id, AsyncCallback<DraftViewData> callback)
			throws IllegalArgumentException;
}