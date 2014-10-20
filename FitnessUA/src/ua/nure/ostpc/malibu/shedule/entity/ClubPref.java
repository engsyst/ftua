package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;

import ua.nure.ostpc.malibu.shedule.Const;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ClubPref implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;

	private long clubPrefId;
	private long clubId;
	private long schedulePeriodId;
	private long employeeId;

	public ClubPref() {
	}

	public ClubPref(long clubPrefId, long clubId, long schedulePeriodId,
			long employeeId) {
		super();
		this.clubPrefId = clubPrefId;
		this.clubId = clubId;
		this.schedulePeriodId = schedulePeriodId;
		this.employeeId = employeeId;
	}

	public long getClubPrefId() {
		return clubPrefId;
	}

	public void setClubPrefId(long clubPrefId) {
		this.clubPrefId = clubPrefId;
	}

	public long getClubId() {
		return clubId;
	}

	public void setClubId(long clubId) {
		this.clubId = clubId;
	}

	public long getSchedulePeriodId() {
		return schedulePeriodId;
	}

	public void setSchedulePeriodId(long schedulePeriodId) {
		this.schedulePeriodId = schedulePeriodId;
	}

	public long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

	@Override
	public int hashCode() {
		return new Long(clubPrefId).hashCode();
	}

//	@Override
	public String toString(int m) {
		StringBuilder builder = new StringBuilder();
		switch (Const.TO_STRING_MODE) {
		case info:
		case normal:
		case debug:
		case fullInfo:
		case fullNormal:
		case fullDebug:
			builder.append("ClubPref [Id=");
			builder.append(clubPrefId);
			builder.append(", clubId=");
			builder.append(clubId);
			builder.append(", schedulePeriodId=");
			builder.append(schedulePeriodId);
			builder.append(", employeeId=");
			builder.append(employeeId);
			builder.append("]");
			break;
		default:
			break;
		
		}
		return builder.toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		return builder.toString();
	}
}
