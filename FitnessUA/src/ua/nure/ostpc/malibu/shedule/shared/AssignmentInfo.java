package ua.nure.ostpc.malibu.shedule.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Assignment info about employee is associated with user from session. Used in
 * schedule draft mode.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class AssignmentInfo implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;

	private long periodId;
	private long shiftId;
	private boolean isAdded;

	public AssignmentInfo() {

	}

	public AssignmentInfo(long periodId, long shiftId, boolean isAdded) {
		this.periodId = periodId;
		this.shiftId = shiftId;
		this.isAdded = isAdded;
	}

	public long getPeriodId() {
		return periodId;
	}

	public void setPeriodId(long peiodId) {
		this.periodId = peiodId;
	}

	public long getShiftId() {
		return shiftId;
	}

	public void setShiftId(long shiftId) {
		this.shiftId = shiftId;
	}

	public boolean isAdded() {
		return isAdded;
	}

	public void setAdded(boolean isAdded) {
		this.isAdded = isAdded;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		else {
			AssignmentInfo otherAssignmentInfo = (AssignmentInfo) obj;
			if (this.periodId == otherAssignmentInfo.periodId
					&& this.shiftId == otherAssignmentInfo.shiftId) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = result * prime + Long.valueOf(periodId).hashCode();
		result = result * prime + Long.valueOf(shiftId).hashCode();
		return result;
	}
}
