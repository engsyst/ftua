package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Holiday implements Serializable, IsSerializable, Comparable<Holiday>, Comparator<Holiday> {
	private static final long serialVersionUID = 1L;

	private Long holidayid;
	private Date date;
	private int repeate;

	public Holiday(Long id, Date dt, Integer repeate){
		this(id,dt);
		setRepeate(repeate);
	}
	
	public Holiday(Long id, Date dt) {
		setHolidayid(id);
		setDate(dt);
		setRepeate(0);
	}

	public Holiday() {
	}

	public Long getHolidayId() {
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
	
	public int getRepeate(){
		return repeate;
	}
	
	public void setRepeate(int repeate){
		this.repeate = repeate;
	}

	@Override
	public int compare(Holiday o1, Holiday o2) {
		return o1.getDate().compareTo(o2.getDate());
	}

	@Override
	public int compareTo(Holiday o) {
		return date.compareTo(o.getDate());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + repeate;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Holiday other = (Holiday) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (repeate != other.repeate)
			return false;
		return true;
	}

	public Object clone() {
		return new Holiday(holidayid.longValue(), (Date) date.clone(), repeate);
	}

}
