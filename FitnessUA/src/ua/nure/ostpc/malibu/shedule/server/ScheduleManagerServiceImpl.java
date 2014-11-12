package ua.nure.ostpc.malibu.shedule.server;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.Path;
import ua.nure.ostpc.malibu.shedule.client.ScheduleDraftService;
import ua.nure.ostpc.malibu.shedule.client.ScheduleManagerService;
import ua.nure.ostpc.malibu.shedule.client.StartSettingService;
import ua.nure.ostpc.malibu.shedule.client.UserSettingService;
import ua.nure.ostpc.malibu.shedule.client.panel.editing.ScheduleEditingService;
import ua.nure.ostpc.malibu.shedule.dao.CategoryDAO;
import ua.nure.ostpc.malibu.shedule.dao.ClubDAO;
import ua.nure.ostpc.malibu.shedule.dao.ClubPrefDAO;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.HolidayDAO;
import ua.nure.ostpc.malibu.shedule.dao.PreferenceDAO;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.dao.UserDAO;
import ua.nure.ostpc.malibu.shedule.entity.AssignmentInfo;
import ua.nure.ostpc.malibu.shedule.entity.Category;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubDaySchedule;
import ua.nure.ostpc.malibu.shedule.entity.ClubPref;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Holiday;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Preference;
import ua.nure.ostpc.malibu.shedule.entity.Right;
import ua.nure.ostpc.malibu.shedule.entity.Role;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;
import ua.nure.ostpc.malibu.shedule.entity.Shift;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.security.Hashing;
import ua.nure.ostpc.malibu.shedule.service.NonclosedScheduleCacheService;
import ua.nure.ostpc.malibu.shedule.service.ScheduleEditEventService;
import ua.nure.ostpc.malibu.shedule.shared.FieldVerifier;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ScheduleManagerServiceImpl extends RemoteServiceServlet implements
		ScheduleManagerService, ScheduleDraftService, StartSettingService,
		ScheduleEditingService, UserSettingService {
	private static final Logger log = Logger
			.getLogger(ScheduleManagerServiceImpl.class);

	private static DateFormat dateFormat = DateFormat.getDateInstance(
			DateFormat.LONG, new Locale("ru", "RU"));

	private NonclosedScheduleCacheService nonclosedScheduleCacheService;
	private ScheduleEditEventService scheduleEditEventService;
	private ScheduleDAO scheduleDAO;
	private CategoryDAO categoryDAO;
	private HolidayDAO holidayDAO;
	private UserDAO userDAO;
	private PreferenceDAO preferenceDAO;
	private EmployeeDAO employeeDAO;
	private ClubDAO clubDAO;
	private ClubPrefDAO clubPrefDAO;

	private GenFlags mode = GenFlags.DEFAULT;

	private enum GenFlags {
		ONLY_ONE_SHIFT(1), CAN_EMPTY(1 << 1), CHECK_MAX_DAYS(1 << 2), DEFAULT(
				ONLY_ONE_SHIFT, CAN_EMPTY, CHECK_MAX_DAYS), ;

		private int mode;

		GenFlags(int bit) {
			this.mode = (1 << bit);
		}

		GenFlags(GenFlags... flags) {
			mode = 0;
			for (GenFlags f : flags)
				this.mode |= f.getMask();
		}

		int getMask() {
			return mode;
		}

		public void setMode(GenFlags... flags) {
			mode = 0;
			for (GenFlags f : flags)
				this.mode |= f.getMask();
		}

		boolean isSet(GenFlags... flags) {
			for (GenFlags f : flags)
				if ((mode & f.mode) != f.mode)
					return false;
			return true;
		}
	}

	public ScheduleManagerServiceImpl() {
		super();
		if (log.isDebugEnabled()) {
			log.debug("Servlet creates");
		}
	}

	@Override
	public void init() {
		ServletContext servletContext = getServletContext();
		nonclosedScheduleCacheService = (NonclosedScheduleCacheService) servletContext
				.getAttribute(AppConstants.NONCLOSED_SCHEDULE_CACHE_SERVICE);
		scheduleEditEventService = (ScheduleEditEventService) servletContext
				.getAttribute(AppConstants.SCHEDULE_EDIT_EVENT_SERVICE);
		scheduleDAO = (ScheduleDAO) servletContext
				.getAttribute(AppConstants.SCHEDULE_DAO);
		employeeDAO = (EmployeeDAO) servletContext
				.getAttribute(AppConstants.EMPLOYEE_DAO);
		preferenceDAO = (PreferenceDAO) servletContext
				.getAttribute(AppConstants.PREFERENCE_DAO);
		clubDAO = (ClubDAO) servletContext.getAttribute(AppConstants.CLUB_DAO);
		clubPrefDAO = (ClubPrefDAO) servletContext
				.getAttribute(AppConstants.CLUB_PREF_DAO);
		categoryDAO = (CategoryDAO) servletContext
				.getAttribute(AppConstants.CATEGORY_DAO);
		holidayDAO = (HolidayDAO) servletContext
				.getAttribute(AppConstants.HOLIDAY_DAO);
		userDAO = (UserDAO) servletContext.getAttribute(AppConstants.USER_DAO);
		if (nonclosedScheduleCacheService == null) {
			log.error("NonclosedScheduleCacheService attribute is not exists.");
			throw new IllegalStateException(
					"NonclosedScheduleCacheService attribute is not exists.");
		}
		if (scheduleEditEventService == null) {
			log.error("ScheduleEditEventService attribute is not exists.");
			throw new IllegalStateException(
					"ScheduleEditEventService attribute is not exists.");
		}
		if (scheduleDAO == null) {
			log.error("ScheduleDAO attribute is not exists.");
			throw new IllegalStateException(
					"ScheduleDAO attribute is not exists.");
		}
		if (preferenceDAO == null) {
			log.error("PrefernceDAO attribute is not exists.");
			throw new IllegalStateException(
					"PreferenceDAO attribute is not exists.");
		}
		if (employeeDAO == null) {
			log.error("EmployeeDAO attribute is not exists.");
			throw new IllegalStateException(
					"EmployeeDAO attribute is not exists.");
		}
		if (clubDAO == null) {
			log.error("ClubDAO attribute is not exists.");
			throw new IllegalStateException("ClubDAO attribute is not exists.");
		} else if (employeeDAO == null) {
			log.error("EmployeeDAO attribute is not exists.");
			throw new IllegalStateException(
					"EmployeeDAO attribute is not exists.");
		} else if (categoryDAO == null) {
			log.error("CategoryDAO attribute is not exists.");
			throw new IllegalStateException(
					"CategoryDAO attribute is not exists.");
		} else if (userDAO == null) {
			log.error("UserDAO attribute is not exists.");
			throw new IllegalStateException("UserDAO attribute is not exists.");
		}
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (log.isDebugEnabled()) {
			log.debug("GET method starts");
		}
		RequestDispatcher dispatcher = request
				.getRequestDispatcher(Path.PAGE__SCHEDULE_MANAGER);
		dispatcher.forward(request, response);
		if (log.isDebugEnabled()) {
			log.debug("Response was sent");
		}
	}

	private User getUserFromSession() {
		HttpServletRequest request = getThreadLocalRequest();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(AppConstants.USER);
		return user;
	}

	@Override
	public List<Period> getAllPeriods() throws IllegalArgumentException {
		return scheduleDAO.getAllPeriods();
	}

	@Override
	public Map<Long, Status> getScheduleStatusMap()
			throws IllegalArgumentException {
		return scheduleDAO.getScheduleStatusMap();
	}

	@Override
	public boolean lockSchedule(Long periodId) throws IllegalArgumentException {
		boolean result = nonclosedScheduleCacheService.lockSchedule(periodId);
		User user = getUserFromSession();
		if (result) {
			scheduleEditEventService.addEditEvent(periodId, user.getUserId());
		}
		if (log.isInfoEnabled() && result && user != null) {
			Period period = getScheduleById(periodId).getPeriod();
			StringBuilder sb = new StringBuilder();
			sb.append("Заблокировал график работы: ");
			sb.append("(periodId=");
			sb.append(period.getPeriodId());
			sb.append(") ");
			sb.append("с ");
			sb.append(dateFormat.format(period.getStartDate()));
			sb.append(" до ");
			sb.append(dateFormat.format(period.getEndDate()));
			log.info("UserId: " + user.getUserId() + " Логин: "
					+ user.getLogin() + " Действие: " + sb.toString());
		}
		return result;
	}

	@Override
	public void unlockSchedule(Long periodId) throws IllegalArgumentException {
		nonclosedScheduleCacheService.unlockSchedule(periodId);
		scheduleEditEventService.removeEditEvent(periodId);
		if (log.isInfoEnabled()) {
			User user = getUserFromSession();
			if (user != null) {
				Period period = getScheduleById(periodId).getPeriod();
				StringBuilder sb = new StringBuilder();
				sb.append("Разблокировал график работы: ");
				sb.append("(periodId=");
				sb.append(period.getPeriodId());
				sb.append(") ");
				sb.append("с ");
				sb.append(dateFormat.format(period.getStartDate()));
				sb.append(" до ");
				sb.append(dateFormat.format(period.getEndDate()));
				log.info("UserId: " + user.getUserId() + " Логин: "
						+ user.getLogin() + " Действие: " + sb.toString());
			}
		}
	}

	@Override
	public List<Role> userRoles() throws IllegalArgumentException {
		User user = getUserFromSession();
		return user.getRoles();
	}

	@Override
	public Employee getEmployee() throws IllegalArgumentException {
		if (log.isDebugEnabled()) {
			log.debug("getEmployee method starts");
		}
		User user = getUserFromSession();
		long employeeId = user.getEmployeeId();
		Employee employee = employeeDAO.findEmployee(employeeId);
		return employee;
	}

	@Override
	public Collection<Club> getClubes() throws IllegalArgumentException {
		return clubDAO.getDependentClubs();
	}

	public List<ClubPref> getClubPref(long periodId) {
		return clubPrefDAO.getClubPrefsByPeriodId(periodId);
	}

	@Override
	public Map<Club, List<Employee>> getEmpToClub(long periodId) {
		Set<Club> clubList = new HashSet<Club>();
		Map<Club, List<Employee>> empToClub = new HashMap<Club, List<Employee>>();
		List<ClubPref> clubPrefs = getClubPref(periodId);

		if (clubPrefs != null) {
			Iterator<ClubPref> it = clubPrefs.iterator();
			while (it.hasNext()) {
				ClubPref clpr = it.next();
				clubList.add(clubDAO.findClubById(clpr.getClubId()));
			}
		}

		for (Club club : clubList) {
			Iterator<ClubPref> iterator = clubPrefs.iterator();
			List<Employee> empList = new ArrayList<Employee>();
			while (iterator.hasNext()) {
				ClubPref clpr = iterator.next();
				if (club.getClubId() == clpr.getClubId()) {
					empList.add(employeeDAO.findEmployee(clpr.getEmployeeId()));
				}
			}
			empToClub.put(club, empList);
		}
		return empToClub;
	}

	@Override
	public Schedule getScheduleById(long periodId) {
		return nonclosedScheduleCacheService.getSchedule(periodId);
	}

	@Override
	public boolean updateShift(AssignmentInfo assignmentInfo, Employee employee) {
		boolean result = nonclosedScheduleCacheService.updateShift(
				assignmentInfo, employee);
		if (log.isInfoEnabled() && result) {
			User user = getUserFromSession();
			if (user != null) {
				StringBuilder sb = new StringBuilder();
				sb.append(assignmentInfo.isAdded() == true ? "Добавил "
						: "Убрал ");
				sb.append("работника ");
				sb.append(employee.getNameForSchedule());
				sb.append(" в графике работы: ");
				sb.append("(periodId=");
				Period period = getScheduleById(assignmentInfo.getPeriodId())
						.getPeriod();
				sb.append(period.getPeriodId());
				sb.append(") ");
				sb.append("с ");
				sb.append(dateFormat.format(period.getStartDate()));
				sb.append(" до ");
				sb.append(dateFormat.format(period.getEndDate()));
				sb.append(" Дата: ");
				sb.append(dateFormat.format(assignmentInfo.getDate()));
				sb.append(" Клуб \"");
				sb.append(assignmentInfo.getClub().getTitle());
				sb.append("\" ");
				sb.append("Смена: ");
				sb.append(assignmentInfo.getRowNumber() + 1);
				sb.append(".");
				log.info("UserId: " + user.getUserId() + " Логин: "
						+ user.getLogin() + " Действие: " + sb.toString());
			}
		}
		return result;
	}

	@Override
	public Collection<Club> getClubs() throws IllegalArgumentException {
		Collection<Club> malibuClubs = clubDAO.getAllMalibuClubs();
		if (malibuClubs == null)
			return new ArrayList<Club>();
		else
			return malibuClubs;

	}

	@Override
	public Collection<Employee> getMalibuEmployees()
			throws IllegalArgumentException {
		Collection<Employee> malibuEmployees = employeeDAO.getMalibuEmployees();
		if (malibuEmployees == null)
			return new ArrayList<Employee>();
		else
			return malibuEmployees;
	}

	@Override
	public Map<Long, Club> getDictionaryClub() throws IllegalArgumentException {
		Map<Long, Club> conformity = clubDAO.getConformity();
		if (conformity == null)
			return new HashMap<Long, Club>();
		else
			return conformity;
	}

	@Override
	public void setClubs(Collection<Club> clubsForInsert,
			Collection<Club> clubsForOnlyOurInsert,
			Collection<Club> clubsForUpdate, Collection<Club> clubsForDelete)
			throws IllegalArgumentException {
		for (Club club : clubsForDelete) {
			if (!clubDAO.removeClub(club.getClubId())) {
				log.error("Произошла ошибка при удалении клуба \""
						+ club.getTitle() + "\" (clubId=" + club.getClubId()
						+ ") из таблицы Club.");
				throw new IllegalArgumentException(
						"Произошла ошибка при удалении клуба "
								+ club.getTitle());
			} else {
				if (log.isInfoEnabled()) {
					User user = getUserFromSession();
					if (user != null) {
						log.info("UserId: " + user.getUserId() + " Логин: "
								+ user.getLogin()
								+ " Действие: Настройка. Удалил клуб \""
								+ club.getTitle() + "\" (clubId="
								+ club.getClubId() + ") из таблицы Club.");
					}
				}
			}
		}

		for (Club club : clubsForUpdate) {
			if (!clubDAO.updateClub(club)) {
				log.error("Произошла ошибка при обновлении клуба \""
						+ club.getTitle() + "\" (clubId=" + club.getClubId()
						+ ") в таблице Club.");
				throw new IllegalArgumentException(
						"Произошла ошибка при обновлении клуба "
								+ club.getTitle());
			} else {
				if (log.isInfoEnabled()) {
					User user = getUserFromSession();
					if (user != null) {
						log.info("UserId: " + user.getUserId() + " Логин: "
								+ user.getLogin()
								+ " Действие: Настройка. Обновил клуб \""
								+ club.getTitle() + "\" (clubId="
								+ club.getClubId() + ") в таблице Club.");
					}
				}
			}
		}

		if (!clubDAO.insertClubs(clubsForOnlyOurInsert)) {
			for (Club club : clubsForOnlyOurInsert) {
				log.error("Произошла ошибка при добавлении клуба \""
						+ club.getTitle() + "\" (clubId=" + club.getClubId()
						+ ") в таблицу Club.");
			}
			throw new IllegalArgumentException(
					"Произошла ошибка при добавлении клубов");
		} else {
			if (log.isInfoEnabled() && clubsForOnlyOurInsert != null) {
				for (Club club : clubsForOnlyOurInsert) {
					User user = getUserFromSession();
					if (user != null) {
						log.info("UserId: " + user.getUserId() + " Логин: "
								+ user.getLogin()
								+ " Действие: Настройка. Добавил клуб \""
								+ club.getTitle() + "\" (clubId="
								+ club.getClubId() + ") в таблицу Club.");
					}
				}
			}
		}

		if (!clubDAO.insertClubsWithConformity(clubsForInsert)) {
			for (Club club : clubsForInsert) {
				log.error("Произошла ошибка при добавлении и согласовании клуба \""
						+ club.getTitle()
						+ "\" (clubId="
						+ club.getClubId()
						+ ") в таблицах Club и ComplianceClub.");
			}
			throw new IllegalArgumentException(
					"Произошла ошибка при вставке клубов");
		} else {
			if (log.isInfoEnabled() && clubsForInsert != null) {
				for (Club club : clubsForInsert) {
					User user = getUserFromSession();
					if (user != null) {
						log.info("UserId: "
								+ user.getUserId()
								+ " Логин: "
								+ user.getLogin()
								+ " Действие: Настройка. Добавил клуб и согласовал \""
								+ club.getTitle() + "\" (clubId="
								+ club.getClubId()
								+ ") в таблицах Club и ComplianceClub.");
					}
				}
			}
		}
	}

	@Override
	public Collection<Club> getOnlyOurClubs() throws IllegalArgumentException {
		Collection<Club> ourClub = clubDAO.getOnlyOurClub();
		if (ourClub == null)
			return new ArrayList<Club>();
		else
			return ourClub;
	}

	@Override
	public Collection<Employee> getOnlyOurEmployees()
			throws IllegalArgumentException {
		Collection<Employee> ourEmployee = employeeDAO.getOnlyOurEmployees();
		if (ourEmployee == null)
			return new ArrayList<Employee>();
		else
			return ourEmployee;
	}

	@Override
	public Map<Long, Employee> getDictionaryEmployee()
			throws IllegalArgumentException {
		Map<Long, Employee> conformity = employeeDAO.getConformity();
		if (conformity == null)
			return new HashMap<Long, Employee>();
		else
			return conformity;
	}

	@Override
	public Map<Long, Collection<Boolean>> getRoleEmployee()
			throws IllegalArgumentException {
		Map<Long, Collection<Boolean>> roles = employeeDAO
				.getRolesForEmployee();
		if (roles == null)
			return new HashMap<Long, Collection<Boolean>>();
		else
			return roles;
	}

	@Override
	public void setEmployees(Collection<Employee> employeesForInsert,
			Collection<Employee> employeesForOnlyOurInsert,
			Collection<Employee> employeesForUpdate,
			Collection<Employee> employeesForDelete,
			Map<Integer, Collection<Long>> roleForInsert,
			Map<Integer, Collection<Long>> roleForDelete,
			Map<Integer, Collection<Employee>> roleForInsertNew,
			Map<Integer, Collection<Employee>> roleForInsertWithoutConformity)
			throws IllegalArgumentException {
		User user = getUserFromSession();
		for (Employee employee : employeesForDelete) {
			if (!employeeDAO.deleteEmployee(employee.getEmployeeId())) {
				log.error("Произошла ошибка при удалении сотрудника "
						+ employee.getNameForSchedule());
				throw new IllegalArgumentException(
						"Произошла ошибка при удалении сотрудника "
								+ employee.getNameForSchedule());
			} else {
				if (log.isInfoEnabled() && user != null) {
					log.info("UserId: " + user.getUserId() + " Логин: "
							+ user.getLogin()
							+ " Действие: Настройка. Удалил сотрудника "
							+ employee.getNameForSchedule() + " (employeeId="
							+ employee.getEmployeeId()
							+ ") из таблицы Employee.");
				}
			}
		}

		if (!employeeDAO.updateEmployees(employeesForUpdate)) {
			log.error("Произошла ошибка при обновлении сотрудников");
			throw new IllegalArgumentException(
					"Произошла ошибка при обновлении сотрудников");
		} else {
			if (log.isInfoEnabled() && user != null
					&& employeesForUpdate != null) {
				for (Employee employee : employeesForUpdate) {
					log.info("UserId: "
							+ user.getUserId()
							+ " Логин: "
							+ user.getLogin()
							+ " Действие: Настройка. Обновил информацию о сотруднике "
							+ employee.getNameForSchedule() + " (employeeId="
							+ employee.getEmployeeId()
							+ ") в таблице Employee.");
				}
			}
		}

		if (!employeeDAO.insertEmployees(employeesForOnlyOurInsert)) {
			log.error("Произошла ошибка при вставке сотрудников");
			throw new IllegalArgumentException(
					"Произошла ошибка при вставке сотрудников");
		} else {
			if (log.isInfoEnabled() && user != null
					&& employeesForOnlyOurInsert != null) {
				for (Employee employee : employeesForOnlyOurInsert) {
					log.info("UserId: " + user.getUserId() + " Логин: "
							+ user.getLogin()
							+ " Действие: Настройка. Добавил сотрудника "
							+ employee.getNameForSchedule() + " (employeeId="
							+ employee.getEmployeeId()
							+ ") в таблицу Employee.");
				}
			}
		}

		if (!employeeDAO.insertEmployeesWithConformity(employeesForInsert)) {
			log.error("Произошла ошибка при вставке сотрудников");
			throw new IllegalArgumentException(
					"Произошла ошибка при вставке сотрудников");
		} else {
			if (log.isInfoEnabled() && user != null
					&& employeesForInsert != null) {
				for (Employee employee : employeesForInsert) {
					log.info("UserId: " + user.getUserId() + " Логин: "
							+ user.getLogin()
							+ " Действие: Настройка. Добавил сотрудника "
							+ employee.getNameForSchedule() + " (employeeId="
							+ employee.getEmployeeId()
							+ ") в таблицы Employee и ComplianceEmployee.");
				}
			}
		}

		if (!employeeDAO.setRolesForEmployees(roleForInsert)) {
			log.error("Произошла ошибка при установке ролей сотрудникам");
			throw new IllegalArgumentException(
					"Произошла ошибка при установке ролей сотрудникам");
		} else {
			if (log.isInfoEnabled() && user != null && roleForInsert != null) {
				Iterator<Entry<Integer, Collection<Long>>> it = roleForInsert
						.entrySet().iterator();
				while (it.hasNext()) {
					Entry<Integer, Collection<Long>> entry = it.next();
					log.info("UserId: "
							+ user.getUserId()
							+ " Логин: "
							+ user.getLogin()
							+ " Действие: Настройка. Установил для сотрудника (employeeId="
							+ entry.getKey() + ") роли (roleId="
							+ entry.getValue().toString()
							+ ") в таблице EmployeeUserRole.");
				}
			}
		}

		if (!employeeDAO.deleteRolesForEmployees(roleForDelete)) {
			log.error("Произошла ошибка при удалении ролей сотрудников");
			throw new IllegalArgumentException(
					"Произошла ошибка при удалении ролей сотрудников");
		} else {
			if (log.isInfoEnabled() && user != null && roleForDelete != null) {
				Iterator<Entry<Integer, Collection<Long>>> it = roleForDelete
						.entrySet().iterator();
				while (it.hasNext()) {
					Entry<Integer, Collection<Long>> entry = it.next();
					log.info("UserId: "
							+ user.getUserId()
							+ " Логин: "
							+ user.getLogin()
							+ " Действие: Настройка. Удалил для сотрудника (employeeId="
							+ entry.getKey() + ") роли (roleId="
							+ entry.getValue().toString()
							+ ") в таблице EmployeeUserRole.");
				}
			}
		}

		if (!employeeDAO
				.insertEmployeesWithConformityAndRoles(roleForInsertNew)) {
			log.error("Произошла ошибка при установке ролей сотрудникам");
			throw new IllegalArgumentException(
					"Произошла ошибка при установке ролей сотрудникам");
		} else {
			if (log.isInfoEnabled() && user != null && roleForInsertNew != null) {
				Iterator<Entry<Integer, Collection<Employee>>> it = roleForInsertNew
						.entrySet().iterator();
				while (it.hasNext()) {
					Entry<Integer, Collection<Employee>> entry = it.next();
					Collection<Employee> employees = entry.getValue();
					if (employees != null) {
						for (Employee employee : employees) {
							log.info("UserId: "
									+ user.getUserId()
									+ " Логин: "
									+ user.getLogin()
									+ " Действие: Настройка. Добавил сотрудника "
									+ employee.getNameForSchedule()
									+ " (employeeId="
									+ employee.getEmployeeId()
									+ ") и роль ("
									+ Right.values()[entry.getKey()]
									+ ") в таблицы Employee, EmployeeUserRole и ComplianceEmployee.");
						}
					}
				}
			}
		}

		if (!employeeDAO
				.insertEmployeesAndRoles(roleForInsertWithoutConformity)) {
			log.error("Произошла ошибка при установке ролей сотрудникам");
			throw new IllegalArgumentException(
					"Произошла ошибка при установке ролей сотрудникам");
		} else {
			if (log.isInfoEnabled() && user != null && roleForInsertNew != null) {
				Iterator<Entry<Integer, Collection<Employee>>> it = roleForInsertNew
						.entrySet().iterator();
				while (it.hasNext()) {
					Entry<Integer, Collection<Employee>> entry = it.next();
					Collection<Employee> employees = entry.getValue();
					if (employees != null) {
						for (Employee employee : employees) {
							log.info("UserId: "
									+ user.getUserId()
									+ " Логин: "
									+ user.getLogin()
									+ " Действие: Настройка. Добавил сотрудника "
									+ employee.getNameForSchedule()
									+ " (employeeId="
									+ employee.getEmployeeId()
									+ ") и роль ("
									+ Right.values()[entry.getKey()]
									+ ") в таблицы Employee и EmployeeUserRole.");
						}
					}
				}
			}
		}
	}

	@Override
	public Collection<Employee> getAllEmploee() throws IllegalArgumentException {
		Collection<Employee> employees = employeeDAO.getAllEmployee();
		if (employees == null)
			return new ArrayList<Employee>();
		else
			return employees;
	}

	@Override
	public Collection<Category> getCategories() throws IllegalArgumentException {
		Collection<Category> categories = categoryDAO
				.getCategoriesWithEmployees();
		if (categories == null)
			return new ArrayList<Category>();
		else
			return categories;
	}

	@Override
	public Map<Long, Collection<Employee>> getCategoriesDictionary()
			throws IllegalArgumentException {
		Collection<Category> categories = getCategories();
		HashMap<Long, Collection<Employee>> dictionaries = new HashMap<Long, Collection<Employee>>();
		for (Category c : categories) {
			dictionaries.put(c.getCategoryId(),
					employeeDAO.findEmployees(c.getEmployeeIdList()));
		}
		return dictionaries;
	}

	@Override
	public void setCategory(Collection<Category> categories,
			Map<Long, Collection<Long>> employeeInCategoriesForDelete,
			Map<Long, Collection<Long>> employeeInCategoriesForInsert,
			Collection<Category> categoriesForDelete,
			Collection<Category> categoriesForInsert)
			throws IllegalArgumentException {
		User user = getUserFromSession();
		if (!categoryDAO.deleteCategory(categoriesForDelete)) {
			log.error("Произошла ошибка при удалении категорий");
			throw new IllegalArgumentException(
					"Произошла ошибка при удалении категорий");
		} else {
			if (log.isInfoEnabled() && user != null
					&& categoriesForDelete != null) {
				for (Category category : categoriesForDelete) {
					log.info("UserId: " + user.getUserId() + " Логин: "
							+ user.getLogin()
							+ " Действие: Удалил категорию \""
							+ category.getTitle() + "\" (categoryId="
							+ category.getCategoryId() + ").");
				}
			}
		}

		if (!categoryDAO.insertCategory(categoriesForInsert)) {
			log.error("Произошла ошибка при добавлении категорий");
			throw new IllegalArgumentException(
					"Произошла ошибка при добавлении категорий");
		} else {
			if (log.isInfoEnabled() && user != null
					&& categoriesForInsert != null) {
				for (Category category : categoriesForInsert) {
					log.info("UserId: " + user.getUserId() + " Логин: "
							+ user.getLogin()
							+ " Действие: Добавил категорию \""
							+ category.getTitle() + "\".");
				}
			}
		}

		for (Category category : categories) {
			if (employeeInCategoriesForDelete.containsKey(category
					.getCategoryId())) {
				if (!categoryDAO.deleteEmployees(category.getCategoryId(),
						employeeInCategoriesForDelete.get(category
								.getCategoryId()))) {
					log.error("Произошла ошибка при удалении сотрудников в категории \""
							+ category.getTitle() + "\".");
					throw new IllegalArgumentException(
							"Произошла ошибка при удалении сотрудников в категории \""
									+ category.getTitle() + "\".");
				} else {
					if (log.isInfoEnabled() && user != null) {
						log.info("UserId: "
								+ user.getUserId()
								+ " Логин: "
								+ user.getLogin()
								+ " Действие: Удалил сотрудников в категории \""
								+ category.getTitle() + "\".");
					}
				}
			}
			if (employeeInCategoriesForInsert.containsKey(category
					.getCategoryId())) {
				if (!categoryDAO.insertEmployees(category.getCategoryId(),
						employeeInCategoriesForInsert.get(category
								.getCategoryId()))) {
					log.error("Произошла ошибка при добавлении сотрудников в категорию \""
							+ category.getTitle() + "\".");
					throw new IllegalArgumentException(
							"Произошла ошибка при добавлении сотрудников в категорию \""
									+ category.getTitle() + "\".");
				} else {
					if (log.isInfoEnabled() && user != null) {
						log.info("UserId: "
								+ user.getUserId()
								+ " Логин: "
								+ user.getLogin()
								+ " Действие: Добавил сотрудников в категории \""
								+ category.getTitle() + "\".");
					}
				}
			}
		}
	}

	@Override
	public Collection<Holiday> getHolidays() throws IllegalArgumentException {
		Collection<Holiday> holidays = holidayDAO.getHolidays();
		if (holidays == null)
			return new ArrayList<Holiday>();
		else
			return holidays;
	}

	@Override
	public void setHolidays(Collection<Holiday> holidaysForDelete,
			Collection<Holiday> holidaysForInsert)
			throws IllegalArgumentException {
		DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("dd.MM.yyyy");
		for (Holiday holiday : holidaysForDelete)
			if (!holidayDAO.removeHoliday(holiday.getHolidayid())) {
				log.error("Произошла ошибка при удалении выходного дня:"
						+ dateTimeFormat.format(holiday.getDate()));
				throw new IllegalArgumentException(
						"Произошла ошибка при удалении выходного дня:"
								+ dateTimeFormat.format(holiday.getDate()));
			} else {
				User user = getUserFromSession();
				if (log.isInfoEnabled() && user != null) {
					log.info("UserId: " + user.getUserId() + " Логин: "
							+ user.getLogin()
							+ " Действие: Удалил выходной день "
							+ dateTimeFormat.format(holiday.getDate())
							+ " (holidayId=" + holiday.getHolidayid() + ").");
				}
			}
		if (!holidayDAO.insertHolidays(holidaysForInsert)) {
			throw new IllegalArgumentException(
					"Произошла ошибка при добавлении выходных дней");
		} else {
			User user = getUserFromSession();
			if (log.isInfoEnabled() && user != null
					&& holidaysForInsert != null) {
				for (Holiday holiday : holidaysForInsert) {
					log.info("UserId: " + user.getUserId() + " Логин: "
							+ user.getLogin()
							+ " Действие: Добавил выходной день "
							+ dateTimeFormat.format(holiday.getDate())
							+ " (holidayId=" + holiday.getHolidayid() + ").");
				}
			}
		}
	}

	@Override
	public Collection<Long> getEmployeeWithoutUser()
			throws IllegalArgumentException {
		Collection<Long> employees = userDAO.getEmployeeIdsWitoutUser();
		if (employees == null)
			return new ArrayList<Long>();
		else
			return employees;
	}

	@Override
	public void setUser(User newUser) throws IllegalArgumentException {
		if (userDAO.containsUser(newUser.getLogin())) {
			throw new IllegalArgumentException("Такой логин уже существует");
		}
		if (!userDAO.insertUser(newUser)) {
			log.error("Произошла ошибка при создании пользователя");
			throw new IllegalArgumentException(
					"Произошла ошибка при создании пользователя");
		} else {
			User user = getUserFromSession();
			if (log.isInfoEnabled() && user != null) {
				log.info("UserId: " + user.getUserId() + " Логин: "
						+ user.getLogin()
						+ " Действие: Создал нового пользователя "
						+ newUser.toString());
			}
		}
	}

	/*
	 * CreateScheduleService begin.
	 */

	@Override
	public Date getStartDate() throws IllegalArgumentException {
		Date maxEndDate = scheduleDAO.getMaxEndDate();
		Date currentDate = new Date();
		if (maxEndDate == null) {
			maxEndDate = currentDate;
		}
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(maxEndDate);
		calendar.add(Calendar.DATE, 1);
		Date startDate = calendar.getTime();
		if (startDate.before(currentDate)) {
			startDate = currentDate;
		}
		return startDate;
	}

	@Override
	public List<Club> getDependentClubs() throws IllegalArgumentException {
		return clubDAO.getDependentClubs();
	}

	@Override
	public List<Employee> getEmployees() throws IllegalArgumentException {
		return employeeDAO.getScheduleEmployees();
	}

	@Override
	public Preference getPreference() throws IllegalArgumentException {
		return preferenceDAO.getLastPreference();
	}

	@Override
	public List<Category> getCategoriesWithEmployees()
			throws IllegalArgumentException {
		return categoryDAO.getCategoriesWithEmployees();
	}

	@Override
	public Schedule insertSchedule(Schedule schedule) {
		Schedule newSchedule = nonclosedScheduleCacheService
				.insertSchedule(schedule);
		User user = getUserFromSession();
		if (log.isInfoEnabled() && user != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("Создал новый график работы: ");
			sb.append("(periodId=");
			sb.append(newSchedule.getPeriod().getPeriodId());
			sb.append(") ");
			Period period = newSchedule.getPeriod();
			sb.append("с ");
			sb.append(dateFormat.format(period.getStartDate()));
			sb.append(" до ");
			sb.append(dateFormat.format(period.getEndDate()));
			log.info("UserId: " + user.getUserId() + " Логин: "
					+ user.getLogin() + " Действие: " + sb.toString());
		}
		return newSchedule;
	}

	@Override
	public Schedule updateSchedule(Schedule schedule) {
		Schedule newSchedule = nonclosedScheduleCacheService
				.updateSchedule(schedule);
		User user = getUserFromSession();
		if (log.isInfoEnabled() && user != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("Сохранил график работы: ");
			sb.append("(periodId=");
			sb.append(newSchedule.getPeriod().getPeriodId());
			sb.append(") ");
			Period period = newSchedule.getPeriod();
			sb.append("с ");
			sb.append(dateFormat.format(period.getStartDate()));
			sb.append(" до ");
			sb.append(dateFormat.format(period.getEndDate()));
			log.info("UserId: " + user.getUserId() + " Логин: "
					+ user.getLogin() + " Действие: " + sb.toString());
		}
		return newSchedule;
	}

	/*
	 * CreateScheduleService end.
	 */

	@Override
	public void setPreference(Preference preference)
			throws IllegalArgumentException {
		if (!preferenceDAO.updatePreference(preference.getWorkHoursInDay(),
				preference.getShiftsNumber())) {
			log.error("Произошла ошибка при обновлении информации о количестве смен и рабочих часов");
			throw new IllegalArgumentException(
					"Произошла ошибка при обновлении информации о количестве смен и рабочих часов");
		} else {
			User user = getUserFromSession();
			if (log.isInfoEnabled() && user != null) {
				log.info("UserId: "
						+ user.getUserId()
						+ " Логин: "
						+ user.getLogin()
						+ " Действие: Обновление информации о количестве смен и рабочих часов. Количество смен: "
						+ preference.getShiftsNumber()
						+ ". Количество рабочих часов: "
						+ preference.getWorkHoursInDay());
			}
		}
	}

	@Override
	public Employee getDataEmployee() throws IllegalArgumentException {
		try {
			User user = getUserFromSession();
			return employeeDAO.findEmployee(user.getEmployeeId());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void setDataEmployee(Employee employee)
			throws IllegalArgumentException {
		Collection<Employee> employees = new ArrayList<Employee>();
		employees.add(employee);
		if (!employeeDAO.updateEmployees(employees)) {
			log.error("Не удалось обновить личные данные сотрудника "
					+ employee.getNameForSchedule() + " (employeeId="
					+ employee.getEmployeeId() + ").");
			throw new IllegalArgumentException(
					"Не удалось обновить личные данные");
		} else {
			User user = getUserFromSession();
			if (log.isInfoEnabled() && user != null) {
				log.info("UserId: " + user.getUserId() + " Логин: "
						+ user.getLogin()
						+ " Действие: Обновление личных данных о сотруднике "
						+ employee.getNameForSchedule() + " (employeeId="
						+ employee.getEmployeeId() + ").");
			}
		}
	}

	@Override
	public void setPass(String oldPassword, String newPassword)
			throws IllegalArgumentException {
		User user = getUserFromSession();
		if (!Hashing.salt(oldPassword, user.getLogin()).equals(
				user.getPassword())) {
			throw new IllegalArgumentException(
					"Введен неверный старый пароль. Не удалось изменить пароль.");
		}
		String error = FieldVerifier.validatePassword(newPassword);
		if (error != null) {
			throw new IllegalArgumentException("Не удалось изменить пароль. "
					+ error);
		} else {
			user.setPassword(Hashing.salt(newPassword, user.getLogin()));
			userDAO.updateUser(user);
			if (log.isInfoEnabled()) {
				log.info("UserId: "
						+ user.getUserId()
						+ " Логин: "
						+ user.getLogin()
						+ " Действие: Смена пароля входа в систему. Пароль успешно изменён.");
			}

		}
	}

	@Override
	public void setPreference(Employee emp) throws IllegalArgumentException {
		Employee e = getDataEmployee();
		e.setMinAndMaxDays(emp.getMin(), emp.getMaxDays());
		if (!employeeDAO.updateEmployeePrefs(e)) {
			log.error("Произошла ошибка при сохранении предпочтений.");
			throw new IllegalArgumentException(
					"Произошла ошибка при сохранении предпочтений.");
		}
	}

	@Override
	public String getUser() throws IllegalArgumentException {
		User user = getUserFromSession();
		long employeeId = user.getEmployeeId();
		Employee employee = employeeDAO.findEmployee(employeeId);
		return employee.getFirstName() + " " + employee.getLastName();
	}

	@Override
	public Schedule getCurrentSchedule() {
		return nonclosedScheduleCacheService.getCurrentSchedule();
	}

	@Override
	public long getNearestPeriodId() throws IllegalArgumentException {
		java.sql.Date dateTime = new java.sql.Date(System.currentTimeMillis());
		Period period = scheduleDAO.getPeriod(dateTime);
//		if ()
		java.sql.Date date = (java.sql.Date) period.getEndDate();
		int count = 0;
		while (count < 30) {
			CalendarUtil.addDaysToDate(date, 1);
			period = scheduleDAO.getPeriod(date);
			if (period != null) {
				return period.getPeriodId();
			}
			count++;
			if (count == 30) {
				period = scheduleDAO.getLastPeriod(scheduleDAO.getPeriod(dateTime).getPeriodId());
				return period.getPeriodId();
			}
		}
		return -1;

	}

	private Set<Employee> getInvolvedInDate(List<ClubDaySchedule> daySchedules) {
		Set<Employee> clubDayEmps = new HashSet<Employee>();
		for (ClubDaySchedule c : daySchedules) {
			clubDayEmps.addAll(c.getEmployees());
		}
		return clubDayEmps;
	}

	private void sortEmpsByPriority(List<Employee> toSort,
			final List<Employee> prefered) {

		Comparator<Employee> comparator = new Comparator<Employee>() {
			@Override
			public int compare(Employee o1, Employee o2) {
				final boolean in1 = prefered.contains(o1);
				final boolean in2 = prefered.contains(o2);
				if ((in1 && in2) || (!in1 && !in2)) {
					return Integer.compare(
							o1.getMaxDays() - o2.getAssignment(),
							o2.getMaxDays() - o1.getAssignment());
					// (o1.getMaxDays() - o1.getMin()) / 2 - o1.getAssignment(),
					// (o2.getMaxDays() - o2.getMin()) / 2 -
					// o2.getAssignment());
				}
				return Boolean.compare(in2, in1);
			}
		};

		if (mode.isSet(GenFlags.CHECK_MAX_DAYS)) {
			ListIterator<Employee> eIter = toSort.listIterator();
			while (eIter.hasNext()) {
				Employee e = eIter.next();
				if (e.getMaxDays() <= e.getAssignment())
					eIter.remove();
			}
		}
		Collections.sort(toSort, comparator);
		// System.out.println(toSort);
	}

	private void moveEmpsToPreferredClub(Schedule s) {
		TreeSet<java.sql.Date> dates = new TreeSet<java.sql.Date>();
		dates.addAll(s.getDayScheduleMap().keySet());
		Iterator<java.sql.Date> dIter = dates.iterator();
		while (dIter.hasNext()) {
			java.sql.Date d = dIter.next();
			List<ClubDaySchedule> daySchedules = s.getDayScheduleMap().get(d);

			// By club
			ListIterator<ClubDaySchedule> cdsIter = daySchedules.listIterator();
			while (cdsIter.hasNext()) {
				// get next schedule of club at this date
				ClubDaySchedule clubDaySchedule = cdsIter.next();

				List<Shift> shifts = clubDaySchedule.getShifts();
				for (Shift sh : shifts) {
					sh.getEmployees();
				}
			}
		}

	}

	@Override
	public Schedule generate(Schedule s) throws IllegalArgumentException {
		if (s.getStatus() != Schedule.Status.DRAFT)
			throw new IllegalArgumentException(
					"Данный график не имеет статус черновик");

		// mode.setMode(GenFlags.CAN_EMPTY, GenFlags.ONLY_ONE_SHIFT);
		System.out.println("-- Shedule --\n" + s);

		// get all Employees to Schedule
		ArrayList<Employee> allEmps = (ArrayList<Employee>) employeeDAO
				.findEmployees(Right.ADMIN);
		if (allEmps == null)
			throw new IllegalArgumentException(
					"Не найдено ни одного сотрудника");

		// int allAssCount = s.getCountOfAllNeededAssignments();
		// if (allAssCount < allEmps.size())
		// // Включить режим 1 сотрудник на нескольких сменах подряд
		// throw new IllegalArgumentException("Не достаточно сотрудников");
		//
		//
		Set<Employee> involvedEmps = new HashSet<Employee>();

		// By date
		TreeSet<java.sql.Date> dates = new TreeSet<java.sql.Date>();
		dates.addAll(s.getDayScheduleMap().keySet());
		Iterator<java.sql.Date> dIter = dates.iterator();
		while (dIter.hasNext()) {
			java.sql.Date d = dIter.next();
			long diff = (d.getTime() - dates.first().getTime())
					/ (1000 * 60 * 60 * 24);
			if ((diff % 7) == 0) {
				for (Employee e : allEmps) {
					e.setAssignment(0);
				}
				s.recountAssignments(d);
			}
			List<ClubDaySchedule> daySchedules = s.getDayScheduleMap().get(d);
			s.sortClubsByPrefs(daySchedules, allEmps);
			/*
			 * // move preferred employees to their preferred club for
			 * (ClubDaySchedule cds1 : daySchedules) { for (ClubDaySchedule cds2
			 * : daySchedules) { if (cds1.getDate().equals(cds2.getDate()) &&
			 * cds1.getClub().equals(cds2.getClub())) continue; Shift sh1[] =
			 * cds1.getShifts().toArray(new Shift[0]); Shift sh2[] =
			 * cds2.getShifts().toArray(new Shift[0]); for (int i = 0; i <
			 * sh1.length; i++) { List<Employee> e1 = sh1[i].getEmployees(); if
			 * (e1 != null) { e1.removeAll(s.getPreferredEmps(e1,
			 * cds1.getClub())); } List<Employee> e2 =
			 * s.getPreferredEmps(sh2[i].getEmployees(), cds2.getClub()); } }
			 * 
			 * }
			 */
			// By club
			ListIterator<ClubDaySchedule> cdsIter = daySchedules.listIterator();
			while (cdsIter.hasNext()) {
				// get next schedule of club at this date
				ClubDaySchedule clubDaySchedule = cdsIter.next();
				System.out.println("-- ClubDaySchedule --\n" + clubDaySchedule);

				// get free Employees
				ArrayList<Employee> freeEmps = new ArrayList<Employee>(allEmps);
				involvedEmps = getInvolvedInDate(daySchedules);
				System.out.println("-- InvolvedEmps -- Size: "
						+ involvedEmps.size() + "\n" + involvedEmps);
				freeEmps.removeAll(involvedEmps);
				System.out.println("-- FreeEmps -- Size: " + freeEmps.size()
						+ "\n" + freeEmps);

				// check restrictions

				if (clubDaySchedule.isFull())
					continue;

				// Arrange by the objective function
				sortEmpsByPriority(freeEmps,
						s.getPreferredEmps(freeEmps, clubDaySchedule.getClub()));
				System.out.println("-- FreeEmps sorted before -- Size: "
						+ freeEmps.size() + "\n" + freeEmps);

				// if shifts in date not full and not enough free employees
				if (mode.isSet(GenFlags.CAN_EMPTY)) {
					if (!freeEmps.isEmpty())
						clubDaySchedule.assignEmployeesToShifts(freeEmps);
				} else {
					clubDaySchedule.assignEmployeesToShifts(freeEmps);
				}
				System.out.println("-- FreeEmps sorted after -- Size: "
						+ freeEmps.size() + "\n" + freeEmps);
			}
		}

		return s;
	}

	public void setEmployeeDAO(EmployeeDAO employeeDAO) {
		this.employeeDAO = employeeDAO;
	}
}
