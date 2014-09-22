package ua.nure.ostpc.malibu.shedule.shared;

import java.io.Serializable;
import java.util.Date;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Shift;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AssignmentInfo implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;

	private long periodId;
	private Club club;
	private Date date;
	private boolean isAdded;
	private int rowNumber;

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

	public int getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	public AssignmentInfo() {

	}

	public AssignmentInfo(Shift shift, long periodId, Club club, Date date,
			boolean isAdded) {
		this.periodId = periodId;
		this.club = club;
		this.isAdded = isAdded;
		this.date = date;
	}

	public boolean equals(AssignmentInfo object) {
		if (object == null || object.getClass() != this.getClass())
			return false;
		else {
			if (this.periodId == object.periodId
					&& this.club.getClubId() == object.club.getClubId()
					&& this.date.equals(date)) {
				return true;
			} else {
				return false;
			}
		}
	}
}
