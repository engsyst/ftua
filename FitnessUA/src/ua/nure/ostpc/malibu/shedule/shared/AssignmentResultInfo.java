package ua.nure.ostpc.malibu.shedule.shared;

import java.io.Serializable;

import ua.nure.ostpc.malibu.shedule.entity.Schedule;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Assignment result info about employee is associated with user from session.
 * Used in schedule draft mode.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class AssignmentResultInfo implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;

	private Schedule schedule;
	private boolean updateResult;

	public AssignmentResultInfo() {

	}

	public AssignmentResultInfo(Schedule schedule, boolean updateResult) {
		this.schedule = schedule;
		this.updateResult = updateResult;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public boolean getUpdateResult() {
		return updateResult;
	}

	public void setUpdateResult(boolean updateResult) {
		this.updateResult = updateResult;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		else {
			AssignmentResultInfo otherAssignmentInfo = (AssignmentResultInfo) obj;
			if (this.schedule.equals(otherAssignmentInfo.schedule)) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public int hashCode() {
		return schedule.hashCode();
	}
}
