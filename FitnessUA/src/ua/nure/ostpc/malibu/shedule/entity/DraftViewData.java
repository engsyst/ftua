package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class DraftViewData implements IsSerializable, Serializable {
	private Employee emp;
	private Map<Club, List<Employee>> map;
	private Schedule schedule;

	public Employee getEmployee() {
		return emp;
	}

	public void setEmployee(Employee emp) {
		this.emp = emp;
	}

	public Map<Club, List<Employee>> getMap() {
		return map;
	}

	public void setMap(Map<Club, List<Employee>> map) {
		this.map = map;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

}
