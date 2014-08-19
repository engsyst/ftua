package ua.nure.ostpc.malibu.shedule.entity;

import java.util.Date;

public class Holiday {
	private Long holidayid;
	private Date date;

	public Holiday(Long id, Date dt) {
		setHolidayid(id);
		setDate(dt);
	}

	public Holiday() {
	}

	public Long getHolidayid() {
		return holidayid;
	}

	public void setHolidayid(Long holidayid) {
		this.holidayid = holidayid;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
