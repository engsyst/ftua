/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * ClubDaySchedule.
 * 
 * @author engsyst
 */
public class ClubDaySchedule implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;

	private long clubDayScheduleId;
	private long schedulePeriodId;
	private Date date;
	private Club club;
	private int shiftsNumber;
	private int workHoursInDay;
	private List<Shift> shifts;

	public ClubDaySchedule() {
	}

	public ClubDaySchedule(long clubDayScheduleId, long schedulePeriodId,
			Date date, Club club, int shiftsNumber, int workHoursInDay,
			List<Shift> shifts) {
		this.clubDayScheduleId = clubDayScheduleId;
		this.schedulePeriodId = schedulePeriodId;
		this.date = date;
		this.club = club;
		this.shiftsNumber = shiftsNumber;
		this.workHoursInDay = workHoursInDay;
		this.shifts = shifts;
	}

	public long getClubDayScheduleId() {
		return clubDayScheduleId;
	}

	public void setClubDayScheduleId(long clubDayScheduleId) {
		this.clubDayScheduleId = clubDayScheduleId;
	}

	public long getSchedulePeriodId() {
		return schedulePeriodId;
	}

	public void setSchedulePeriodId(long schedulePeriodId) {
		this.schedulePeriodId = schedulePeriodId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Club getClub() {
		return club;
	}

	public void setClub(Club club) {
		this.club = club;
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

	public List<Shift> getShifts() {
		return shifts;
	}

	public void setShifts(List<Shift> shifts) {
		this.shifts = shifts;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ClubDaySchedule [clubDayScheduleId=");
		sb.append(clubDayScheduleId);
		sb.append(", schedulePeriodId=");
		sb.append(schedulePeriodId);
		sb.append(", clubId=");
		sb.append(club.getClubId());
		sb.append(", date=");
		sb.append(date);
		sb.append(", shiftsNumber=");
		sb.append(shiftsNumber);
		sb.append(", workHoursInDay=");
		sb.append(workHoursInDay);
		sb.append("]");
		return sb.toString();
	}
}
