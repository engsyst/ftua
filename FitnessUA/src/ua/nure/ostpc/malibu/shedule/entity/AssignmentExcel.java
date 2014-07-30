package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Date;

public class AssignmentExcel implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int FIRST_HALF = 1;

	public static final int SECOND_HALF = 2;

	private long schedulePeriodId;
	private String clubTitle;
	private Date date;
	private int halfOfDay;
	private int QuantityOfPeople;
	private String name;
	private int colour;

	public AssignmentExcel() {
	}

	public AssignmentExcel(long schedulePeriodId, String clubTitle, Date date,
			int halfOfDay, String name, int colour) {
		this.schedulePeriodId = schedulePeriodId;
		this.clubTitle = clubTitle;
		this.date = date;
		this.halfOfDay = halfOfDay;
		this.name = name;
		this.colour = colour;
	}

	public String getClubTitle() {
		return clubTitle;
	}

	public void setClubTitle(String newClub) {
		this.clubTitle = newClub;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date newDate) {
		this.date = newDate;
	}

	public int getHalfOfDay() {
		return halfOfDay;
	}

	public void setHalfOfDay(int newHalfOfDay) {
		this.halfOfDay = newHalfOfDay;
	}

	public int getColour() {
		return colour;
	}

	public void setColour(int colour) {
		this.colour = colour;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSchedulePeriodId() {
		return schedulePeriodId;
	}

	public void setSchedulePeriodId(long assignmentexcelPeriodId) {
		this.schedulePeriodId = assignmentexcelPeriodId;
	}
	public int getQuantityOfPeople() {
		return QuantityOfPeople;
	}

	public void setQuantityOfPeople(int QuantityOfPeople) {
		this.QuantityOfPeople = QuantityOfPeople;
	}
	
}
