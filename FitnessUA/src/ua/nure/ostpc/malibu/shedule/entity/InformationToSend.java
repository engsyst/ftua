package ua.nure.ostpc.malibu.shedule.entity;

import java.util.Date;

public class InformationToSend {

	private Shift shift;

	private long periodId;

	private Club club;

	private Date date;

	private boolean isAdded;

	public Shift getShift() {
		return shift;
	}

	public void setShift(Shift shift) {
		this.shift = shift;
	}

	public long getPeriodId() {
		return periodId;
	}

	public void setPeriodId(long periodId) {
		this.periodId = periodId;
	}

	public Club getClub() {
		return club;
	}

	public void setClub(Club club) {
		this.club = club;
	}

	public boolean isAdded() {
		return isAdded;
	}

	public void setAdded(boolean isAdded) {
		this.isAdded = isAdded;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public InformationToSend() {

	}

	public InformationToSend(Shift shift, long periodId, Club club, Date date,
			boolean isAdded) {
		this.shift = shift;
		this.periodId = periodId;
		this.club = club;
		this.isAdded = isAdded;
		this.date = date;
	}

	public boolean equals(InformationToSend object) {
		if (object == null || object.getClass() != this.getClass())
			return false;
		else {
			if (this.shift.getShiftId() == object.shift.getShiftId()
					&& this.periodId == object.periodId
					&& this.club.getClubId() == object.club.getClubId()
					&& this.date.equals(date)) {
				return true;
			} else {
				return false;
			}
		}
	}
}
