package ua.nure.ostpc.malibu.shedule.entity;


import java.util.Date;

public class AssignmentExcel {
	private static final long serialVersionUID = 1L;

	
	public static final int FIRST_HALF = 1;

	public static final int SECOND_HALF = 2;

	private String SchedulePeriodId;
	private String clubTitle;
	private Date date;
	private int halfOfDay = 0;
	private String name ;
	private String colour;
	

	public AssignmentExcel() {
	}

	public AssignmentExcel( String clubTitle, Date date,	int halfOfDay, String name,String colour) {
		
		this.clubTitle = clubTitle;
		this.date = date;
		this.halfOfDay = halfOfDay;
		this.name=name;
		this.colour=colour;
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

	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public long getSchedulePeriodId() {
		return SchedulePeriodId;
	}

	public void setSchedulePeriodId(String assignmentexcelPeriodId) {
		this.SchedulePeriodId = assignmentexcelPeriodId;
	}

	
	
	
}


	

	
