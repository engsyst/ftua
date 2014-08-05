package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DaySchedule implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;

	private Date date;
	private List<ClubDaySchedule> clubDaySchedules;

	public DaySchedule() {
	}

	public DaySchedule(Date date, List<ClubDaySchedule> clubDaySchedules) {
		this.date = date;
		this.clubDaySchedules = clubDaySchedules;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<ClubDaySchedule> getClubDaySchedules() {
		return clubDaySchedules;
	}

	public void setClubDaySchedules(List<ClubDaySchedule> clubDaySchedules) {
		this.clubDaySchedules = clubDaySchedules;
	}
}
