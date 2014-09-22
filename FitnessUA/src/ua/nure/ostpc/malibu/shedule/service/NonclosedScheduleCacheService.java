package ua.nure.ostpc.malibu.shedule.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.dao.ShiftDAO;
import ua.nure.ostpc.malibu.shedule.entity.ClubDaySchedule;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;
import ua.nure.ostpc.malibu.shedule.entity.Shift;
import ua.nure.ostpc.malibu.shedule.shared.AssignmentInfo;

/**
 * The <code>NonclosedScheduleCacheService</code> class contains set of
 * nonclosed schedules.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class NonclosedScheduleCacheService {
	private static final Logger log = Logger
			.getLogger(NonclosedScheduleCacheService.class);

	private Set<Schedule> scheduleSet;
	private ScheduleDAO scheduleDAO;
	private ShiftDAO shiftDAO;

	public NonclosedScheduleCacheService(Set<Schedule> scheduleSet,
			ScheduleDAO scheduleDAO, ShiftDAO shiftDAO) {
		this.scheduleSet = scheduleSet;
		this.scheduleDAO = scheduleDAO;
		this.shiftDAO = shiftDAO;
		ScheduledExecutorService scheduler = Executors
				.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(new ScheduleSetManager(this), 0, 1,
				TimeUnit.DAYS);
	}

	public synchronized boolean lockSchedule(long periodId) {
		for (Schedule schedule : scheduleSet) {
			if (schedule.getPeriod().getPeriodId() == periodId
					&& !schedule.isLocked()) {
				schedule.setLocked(true);
				return true;
			}
		}
		return false;
	}

	public synchronized void unlockSchedule(long periodId) {
		for (Schedule schedule : scheduleSet) {
			if (schedule.getPeriod().getPeriodId() == periodId) {
				schedule.setLocked(false);
			}
		}
	}

	public synchronized Schedule getSchedule(long periodId) {
		for (Schedule schedule : scheduleSet) {
			if (schedule.getPeriod().getPeriodId() == periodId) {
				return schedule;
			}
		}
		return null;
	}

	public synchronized void updateSchedule(Schedule schedule) {
		scheduleSet.add(schedule);
		scheduleDAO.updateSchedule(schedule);
	}

	public synchronized void insertSchedule(Schedule schedule) {
		long periodId = scheduleDAO.insertSchedule(schedule);
		schedule = scheduleDAO.getSchedule(periodId);
		scheduleSet.add(schedule);
	}

	public synchronized boolean updateShift(AssignmentInfo inform,
			Employee employee) {
		Schedule schedule = getSchedule(inform.getPeriodId());
		List<ClubDaySchedule> clubDayScheduleList = schedule
				.getDayScheduleMap().get(inform.getDate());
		for (ClubDaySchedule clubDaySchedule : clubDayScheduleList) {
			List<Shift> shiftList = clubDaySchedule.getShifts();
			int count = 0;
			for (Shift shift : shiftList) {
				if (count == inform.getRowNumber()) {
					List<Employee> employeeList = shift.getEmployees();
					if (employeeList == null) {
						employeeList = new ArrayList<Employee>();
						shift.setEmployees(employeeList);
					}
					if (inform.isAdded()) {
						if (employeeList.size() < shift
								.getQuantityOfEmployees()) {
							shift.getEmployees().add(employee);
							shiftDAO.updateShift(shift);
							return true;
						} else {
							return false;
						}
					} else {
						Iterator<Employee> it = employeeList.iterator();
						while (it.hasNext()) {
							Employee emp = it.next();
							if (emp.getEmployeeId() == employee.getEmployeeId()) {
								it.remove();
								shiftDAO.updateShift(shift);
								return true;
							}
						}
						return false;
					}
				}
				count++;
			}
		}
		return false;
	}

	private synchronized void removeClosedSchedules() {
		Iterator<Schedule> it = scheduleSet.iterator();
		while (it.hasNext()) {
			Schedule schedule = it.next();
			if (schedule.getPeriod().getEndDate().before(new Date())
					&& !schedule.isLocked()) {
				it.remove();
				if (log.isDebugEnabled()) {
					log.debug("Schedule deleted. " + schedule);
				}
				schedule.setStatus(Status.CLOSED);
				scheduleDAO.updateSchedule(schedule);
				if (log.isDebugEnabled()) {
					log.debug("Schedule status updated. " + schedule);
				}
			}
		}
	}

	private class ScheduleSetManager implements Runnable {

		private NonclosedScheduleCacheService nonclosedScheduleCacheService;

		public ScheduleSetManager(
				NonclosedScheduleCacheService nonclosedScheduleCacheService) {
			this.nonclosedScheduleCacheService = nonclosedScheduleCacheService;
		}

		@Override
		public void run() {
			nonclosedScheduleCacheService.removeClosedSchedules();
		}
	}
}
