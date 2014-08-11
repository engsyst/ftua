package ua.nure.ostpc.malibu.shedule.entity;

import java.util.Date;

public class Holiday {
	private Long Holidayid;
	private Date date;
	
	public Holiday (Long id, Date dt) {
		setHolidayid(id);
		setDate(dt);
	}

	public Holiday() {
	}

	public Long getHolidayid() {
		return Holidayid;
	}

	public void setHolidayid(Long holidayid) {
		Holidayid = holidayid;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
