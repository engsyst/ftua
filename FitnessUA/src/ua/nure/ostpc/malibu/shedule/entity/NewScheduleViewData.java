package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class NewScheduleViewData implements Serializable, IsSerializable {
	private Date startDate;
	private List<Club> clubs;
	private List<Employee> emps;
	private Preference prefs;
	private List<Category> categories;
	
	public NewScheduleViewData(Date startDate, List<Club> clubs,
			List<Employee> emps, Preference prefs, List<Category> categories) {
		super();
		this.startDate = startDate;
		this.clubs = clubs;
		this.emps = emps;
		this.prefs = prefs;
		this.categories = categories;
	}

	public NewScheduleViewData() {
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public List<Club> getClubs() {
		return clubs;
	}

	public void setClubs(List<Club> clubs) {
		this.clubs = clubs;
	}

	public List<Employee> getEmployees() {
		return emps;
	}

	public void setEmployees(List<Employee> emps) {
		this.emps = emps;
	}

	public Preference getPrefs() {
		return prefs;
	}

	public void setPrefs(Preference prefs) {
		this.prefs = prefs;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
}
