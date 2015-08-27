package ua.nure.ostpc.malibu.shedule.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.DAOException;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.dao.ShiftDAO;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;
import ua.nure.ostpc.malibu.shedule.entity.Shift;
import ua.nure.ostpc.malibu.shedule.shared.AssignmentInfo;
import ua.nure.ostpc.malibu.shedule.shared.AssignmentResultInfo;
import ua.nure.ostpc.malibu.shedule.shared.OperationCallException;

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

	private static NonclosedScheduleCacheService nc;

	private volatile Set<Schedule> scheduleSet;
	private ScheduleDAO scheduleDAO;
	private ShiftDAO shiftDAO;

	private NonclosedScheduleCacheService(Set<Schedule> scheduleSet,
			ScheduleDAO scheduleDAO, ShiftDAO shiftDAO) {
		this.scheduleSet = scheduleSet;
		this.scheduleDAO = scheduleDAO;
		this.shiftDAO = shiftDAO;
	}

	public static synchronized NonclosedScheduleCacheService newInstance(
			Set<Schedule> scheduleSet, ScheduleDAO scheduleDAO,
			ShiftDAO shiftDAO) {
		if (nc == null)
			nc = new NonclosedScheduleCacheService(scheduleSet, scheduleDAO,
					shiftDAO);
		return nc;
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

	public synchronized Schedule updateSchedule(Schedule schedule) {
		scheduleDAO.updateSchedule(schedule);
		long periodId = schedule.getPeriod().getPeriodId();
		try {
			schedule = scheduleDAO.getSchedule(periodId);
		} catch (DAOException e) {
			throw new IllegalArgumentException(e);
		}
		Schedule s = schedule;
		for (Iterator<Schedule> iter = scheduleSet.iterator(); iter.hasNext();) {
			Schedule type = iter.next();
			if (type.getPeriod().getPeriodId() == schedule.getPeriod()
					.getPeriodId()) {
				s = type;
				break;
			}
		}
		scheduleSet.remove(schedule);
		schedule.setLocked(s.isLocked());
		scheduleSet.add(schedule);
		return schedule;
	}

	public synchronized Schedule insertSchedule(Schedule schedule) {
		long periodId = scheduleDAO.insertSchedule(schedule);
		try {
			schedule = scheduleDAO.getSchedule(periodId);
		} catch (DAOException e) {
			throw new IllegalArgumentException(e);
		}
		scheduleSet.add(schedule);
		return schedule;
	}

	public synchronized AssignmentResultInfo updateShift(
			AssignmentInfo assignmentInfo, Employee employee)
			throws DAOException {
		boolean updateResult = false;
		Shift shift = shiftDAO.getShift(assignmentInfo.getShiftId());
		List<Employee> employeeList = shift.getEmployees();
		if (employeeList == null) {
			employeeList = new ArrayList<Employee>();
			shift.setEmployees(employeeList);
		}
		if (assignmentInfo.isAdded()) {
			if (employeeList.size() < shift.getQuantityOfEmployees()) {
				shift.getEmployees().add(employee);
				updateResult = shiftDAO.updateShift(shift);
			}
		} else {
			Iterator<Employee> it = employeeList.listIterator();
			while (it.hasNext()) {
				Employee emp = it.next();
				if (emp.getEmployeeId() == employee.getEmployeeId()) {
					it.remove();
					updateResult = shiftDAO.updateShift(shift);
					break;
				}
			}
		}
		Schedule schedule = scheduleDAO.getSchedule(assignmentInfo
				.getPeriodId());
		scheduleSet.remove(schedule);
		scheduleSet.add(schedule);
		AssignmentResultInfo assignmentResultInfo = new AssignmentResultInfo(
				schedule, updateResult);
		return assignmentResultInfo;
	}

	public synchronized Schedule getCurrentSchedule() {
		for (Schedule schedule : scheduleSet) {
			if (schedule.getStatus() == Status.CURRENT) {
				return schedule;
			}
		}
		return null;
	}

	synchronized void removeClosedSchedules() {
		Set<Schedule> closedScheduleSet = new TreeSet<Schedule>();
		Iterator<Schedule> it = scheduleSet.iterator();
		while (it.hasNext()) {
			Schedule schedule = it.next();
			Date currentDate = getCurrentDate();
			if (schedule.getPeriod().getEndDate().before(currentDate)
					&& !schedule.isLocked()) {
				closedScheduleSet.add(schedule);
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
		scheduleSet.removeAll(closedScheduleSet);
	}

	public synchronized void removeSchedule(long id) throws DAOException,
			OperationCallException {
		Schedule s = getSchedule(id);
		if (!(s.getPeriod().getStatus() == Status.DRAFT || s.getPeriod()
				.getStatus() == Status.FUTURE))
			throw new OperationCallException(
					"Данный график не имеет статус 'Черновик'");

		if (s.getPeriod().getPeriodId() != getLastSchedule().getPeriod()
				.getPeriodId())
			throw new OperationCallException(
					"Вы не можете удалить не последний график");

		scheduleDAO.removeSchedule(id);
		Iterator<Schedule> it = scheduleSet.iterator();
		while (it.hasNext()) {
			if (it.next().getPeriod().getPeriodId() == id) {
				it.remove();
				break;
			}
		}
	}

	synchronized void setCurrentStatus() {
		for (Schedule schedule : scheduleSet) {
			Date currentDate = getCurrentDate();
			if ((schedule.getPeriod().getStartDate().before(currentDate) || schedule
					.getPeriod().getStartDate().equals(currentDate))
					&& (schedule.getPeriod().getEndDate().after(currentDate) || schedule
							.getPeriod().getEndDate().equals(currentDate))
					&& !schedule.isLocked()) {
				if (log.isDebugEnabled()) {
					log.debug("Schedule has current status. " + schedule);
				}
				schedule.setStatus(Status.CURRENT);
				scheduleDAO.updateSchedule(schedule);
				if (log.isDebugEnabled()) {
					log.debug("Schedule status updated. " + schedule);
				}
			}
		}
	}

	/**
	 * Returns current date.
	 * 
	 * @return date without hours, minutes, seconds, milliseconds.
	 */
	private Date getCurrentDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		return calendar.getTime();
	}

	public synchronized void updateDraftFutureSchedulesInCache() {
		Schedule[] schedules = scheduleSet.toArray(new Schedule[0]);
		for (int i = 0; i < schedules.length; i++) {
			try {
				if (schedules[i].getStatus().equals(Status.DRAFT)
						|| schedules[i].getStatus().equals(Status.FUTURE)) {
					scheduleSet.remove(schedules[i]);
					Schedule s = scheduleDAO.getSchedule(schedules[i]
							.getPeriod().getPeriodId());
					s.setLocked(schedules[i].isLocked());
					scheduleSet.add(s);
				}
			} catch (DAOException e) {
				log.error(e);
				throw new IllegalStateException(
						"Ошибка обновления кеша графиков работ");
			}

		}
	}

	public void changeScheduleStatus(Status newStatus, long id)
			throws IllegalArgumentException, OperationCallException {
		Schedule s = getSchedule(id);
		try {
			if (!(s.getStatus().equals(Status.DRAFT) || s.getStatus().equals(
					Status.FUTURE))) {
				throw new OperationCallException(
						"График работ не имеет статус 'Черновик' или 'К исполнению'");
			}
			scheduleDAO.updateScheduleStatus(id, newStatus);
			s.setStatus(newStatus);
		} catch (DAOException e) {
			throw new IllegalStateException(
					"Ошибка обновления кеша графиков работ");
		}

	}

	public Schedule getLastSchedule() {
		return ((TreeSet<Schedule>) scheduleSet).last();
	}
}
