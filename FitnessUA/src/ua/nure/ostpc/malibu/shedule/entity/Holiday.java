package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Holiday implements Serializable, IsSerializable{
	private static final long serialVersionUID = 1L;
	
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
