package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Preference implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;

	private long preferenceId;
	private int shiftsNumber;
	private int workHoursInDay;
	private int workHoursInWeek = 50;
	private int workContinusHours = 30;

	public int getWorkHoursInWeek() {
		return workHoursInWeek;
	}

	public void setWorkHoursInWeek(int workHoursInWeek) {
		this.workHoursInWeek = workHoursInWeek;
	}

	public int getWorkContinusHours() {
		return workContinusHours;
	}

	public void setWorkContinusHours(int workContinusHours) {
		this.workContinusHours = workContinusHours;
	}

	public Preference() {
	}

	public Preference(long preferenceId, int shiftsNumber, int workHoursInDay) {
		this.preferenceId = preferenceId;
		this.shiftsNumber = shiftsNumber;
		this.workHoursInDay = workHoursInDay;
	}

	public long getPreferenceId() {
		return preferenceId;
	}

	public void setPreferenceId(long preferenceId) {
		this.preferenceId = preferenceId;
	}

	public int getShiftsNumber() {
		return shiftsNumber;
	}

	public void setShiftsNumber(int shiftsNumber) {
		this.shiftsNumber = shiftsNumber;
	}

	public int getWorkHoursInDay() {
		return workHoursInDay;
	}

	public void setWorkHoursInDay(int workHoursInDay) {
		this.workHoursInDay = workHoursInDay;
	}

	@Override
	public int hashCode() {
		return new Long(preferenceId).hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Preference [preferenceId=");
		sb.append(preferenceId);
		sb.append(", shiftsNumber=");
		sb.append(shiftsNumber);
		sb.append(", workHoursInDay=");
		sb.append(workHoursInDay);
		sb.append("]");
		return sb.toString();
	}
}
