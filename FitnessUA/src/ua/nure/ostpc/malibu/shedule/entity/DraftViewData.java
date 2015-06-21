package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class DraftViewData implements IsSerializable, Serializable {
	private Employee emp;
	private Map<Club, List<Employee>> clubPrefs;
	private Schedule schedule;
	private Map<Long, HashSet<String>> prefSetMap;

	public Employee getEmployee() {
		return emp;
	}

	public void setEmployee(Employee emp) {
		this.emp = emp;
	}

	public Map<Club, List<Employee>> getClubPrefs() {
		return clubPrefs;
	}

	public void setClubPrefs(Map<Club, List<Employee>> cp) {
		this.clubPrefs = cp;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public Map<Long, HashSet<String>> getPrefSetMap() {
		return prefSetMap;
	}

	public void setPrefSetMap(Map<Long, HashSet<String>> prefSetMap) {
		this.prefSetMap = prefSetMap;
	}
}
