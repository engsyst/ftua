package ua.nure.ostpc.malibu.shedule.server;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import ua.nure.ostpc.malibu.shedule.entity.Category;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubDaySchedule;
import ua.nure.ostpc.malibu.shedule.entity.ClubPref;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.EmplyeeObjective;
import ua.nure.ostpc.malibu.shedule.entity.GenFlags;
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
import ua.nure.ostpc.malibu.shedule.shared.AssignmentInfo;
import ua.nure.ostpc.malibu.shedule.shared.EmployeeUpdateResult;
import ua.nure.ostpc.malibu.shedule.validator.ServerSideValidator;
import ua.nure.ostpc.malibu.shedule.validator.Validator;

import com.google.gwt.i18n.client.DateTimeFormat;
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
	private Validator validator;

	private GenFlags mode = GenFlags.DEFAULT;

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
		validator = new ServerSideValidator();
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
		long t1 = System.currentTimeMillis();
		User user = getUserFromSession();
		List<Role> ur	=	user.getRoles();
		System.err.println("ScheduleManagerServiceImpl.userRoles " + (System.currentTimeMillis() - t1) + "ms");
		return ur;
	}

	@Override
	public Employee getEmployee() throws IllegalArgumentException {
		long t1 = System.currentTimeMillis();

		if (log.isDebugEnabled()) {
			log.debug("getEmployee method starts");
		}
		User user = getUserFromSession();
		long employeeId = user.getEmployeeId();
		Employee employee = employeeDAO.findEmployee(employeeId);
		System.err.println("ScheduleManagerServiceImpl.getEmployee " + (System.currentTimeMillis() - t1) + "ms");
		return employee;
	}

	@Override
	public Collection<Club> getClubes() throws IllegalArgumentException {
		long t1 = System.currentTimeMillis();
		List<Club> lc = clubDAO.getDependentClubs();
		System.err.println("ScheduleManagerServiceImpl.getClubes " + (System.currentTimeMillis() - t1) + "ms");
		return lc; 
	}

	public List<ClubPref> getClubPref(long periodId) {
		long t1 = System.currentTimeMillis();
		List<ClubPref> cp = 
				clubPrefDAO.getClubPrefsByPeriodId(periodId);
		System.err.println("ScheduleManagerServiceImpl.getClubPref " + (System.currentTimeMillis() - t1) + "ms");
		return cp; 
	}

	@Override
	public Map<Club, List<Employee>> getEmpToClub(long periodId) {
		long t1 = System.currentTimeMillis();
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
		System.err.println("ScheduleManagerServiceImpl.getEmpToClub " + (System.currentTimeMillis() - t1) + "ms");
		return empToClub;
	}

	@Override
	public Schedule getScheduleById(long periodId) {
		long t1 = System.currentTimeMillis();
		Schedule schedule = nonclosedScheduleCacheService.getSchedule(periodId);
		if (schedule == null) {
			schedule = scheduleDAO.getSchedule(periodId);
		}
		System.err.println("ScheduleManagerServiceImpl.getScheduleById " + (System.currentTimeMillis() - t1) + "ms");
		return schedule;
	}

	@Override
	public boolean updateShift(AssignmentInfo assignmentInfo, Employee employee) {
		long t1 = System.currentTimeMillis();
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
		System.err.println("ScheduleManagerServiceImpl.updateShift " + (System.currentTimeMillis() - t1) + "ms");
		return result;
	}

	@Override
	public Collection<Club> getClubs() throws IllegalArgumentException {
		long t1 = System.currentTimeMillis();
		Collection<Club> malibuClubs = clubDAO.getAllMalibuClubs();
		if (malibuClubs == null)
			return new ArrayList<Club>();
		else
			System.err.println("ScheduleManagerServiceImpl.getClubs " + (System.currentTimeMillis() - t1) + "ms");
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
	public Employee getCurrentEmployee() throws IllegalArgumentException {
		User user = getUserFromSession();
		Employee employee = null;
		if (user != null) {
			employee = employeeDAO.findEmployee(user.getEmployeeId());
		}
		return employee;
	}

	@Override
	public void updateEmployeeData(Employee employee)
			throws IllegalArgumentException {
		if (!employeeDAO.updateEmployee(employee)) {
			log.error("Не удалось обновить личные данные сотрудника "
					+ employee.getNameForSchedule() + " (employeeId="
					+ employee.getEmployeeId() + ").");
			throw new IllegalArgumentException(
					"Не удалось обновить личные данные!");
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
	public EmployeeUpdateResult updateFullEmployeeProfile(
			Map<String, String> paramMap, long employeeId, String datePattern)
			throws IllegalArgumentException {
		EmployeeUpdateResult updateResult = new EmployeeUpdateResult();
		Map<String, String> errorMap = validator.validateFullEmployeeProfile(
				paramMap, datePattern);
		if (errorMap.size() == 0) {
			errorMap = employeeDAO.checkEmployeeDataBeforeUpdate(paramMap,
					employeeId);
			if (errorMap == null || errorMap.size() == 0) {
				Employee employee = employeeDAO
						.getScheduleEmployeeById(employeeId);
				if (employee != null) {
					employee.setEmail(paramMap.get(AppConstants.EMAIL));
					employee.setCellPhone(paramMap.get(AppConstants.CELL_PHONE));
					employee.setLastName(paramMap.get(AppConstants.LAST_NAME));
					employee.setFirstName(paramMap.get(AppConstants.FIRST_NAME));
					employee.setSecondName(paramMap
							.get(AppConstants.SECOND_NAME));
					employee.setAddress(paramMap.get(AppConstants.ADDRESS));
					employee.setPassportNumber(paramMap
							.get(AppConstants.PASSPORT_NUMBER));
					employee.setIdNumber(paramMap.get(AppConstants.ID_NUMBER));
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
							datePattern);
					simpleDateFormat.setLenient(false);
					try {
						Date birthday = simpleDateFormat.parse(paramMap
								.get(AppConstants.BIRTHDAY));
						employee.setBirthday(birthday);
					} catch (ParseException e) {
						throw new IllegalArgumentException(
								"Не удалось обновить личные данные!");
					}
					if (!employeeDAO.updateEmployee(employee)) {
						log.error("Не удалось обновить личные данные сотрудника "
								+ employee.getNameForSchedule()
								+ " (employeeId="
								+ employee.getEmployeeId()
								+ ").");
						throw new IllegalArgumentException(
								"Не удалось обновить личные данные!");
					} else {
						User user = getUserFromSession();
						if (log.isInfoEnabled() && user != null) {
							log.info("UserId: "
									+ user.getUserId()
									+ " Логин: "
									+ user.getLogin()
									+ " Действие: Обновление личных данных о сотруднике "
									+ employee.getNameForSchedule()
									+ " (employeeId="
									+ employee.getEmployeeId() + ").");
						}
						updateResult.setResult(true);
						employee = employeeDAO
								.getScheduleEmployeeById(employeeId);
						updateResult.setEmployee(employee);
						return updateResult;
					}
				}
			}
		}
		updateResult.setResult(false);
		updateResult.setErrorMap(errorMap);
		return updateResult;
	}

	@Override
	public EmployeeUpdateResult updateEmployeeProfile(String email,
			String cellPhone, long employeeId) throws IllegalArgumentException {
		EmployeeUpdateResult updateResult = new EmployeeUpdateResult();
		Map<String, String> errorMap = validator.validateEmployeeProfile(email,
				cellPhone);
		if (errorMap.size() == 0) {
			errorMap = employeeDAO.checkEmployeeDataBeforeUpdate(email,
					cellPhone, employeeId);
			if (errorMap == null || errorMap.size() == 0) {
				Employee employee = employeeDAO
						.getScheduleEmployeeById(employeeId);
				if (employee != null) {
					employee.setEmail(email);
					employee.setCellPhone(cellPhone);
					if (!employeeDAO.updateEmployee(employee)) {
						log.error("Не удалось обновить личные данные сотрудника "
								+ employee.getNameForSchedule()
								+ " (employeeId="
								+ employee.getEmployeeId()
								+ ").");
						throw new IllegalArgumentException(
								"Не удалось обновить личные данные!");
					} else {
						User user = getUserFromSession();
						if (log.isInfoEnabled() && user != null) {
							log.info("UserId: "
									+ user.getUserId()
									+ " Логин: "
									+ user.getLogin()
									+ " Действие: Обновление личных данных о сотруднике "
									+ employee.getNameForSchedule()
									+ " (employeeId="
									+ employee.getEmployeeId() + ").");
						}
						updateResult.setResult(true);
						employee = employeeDAO
								.getScheduleEmployeeById(employeeId);
						updateResult.setEmployee(employee);
						return updateResult;
					}
				}
			}
		}
		updateResult.setResult(false);
		updateResult.setErrorMap(errorMap);
		return updateResult;
	}

	@Override
	public Employee getScheduleEmployeeById(long employeeId)
			throws IllegalArgumentException {
		return employeeDAO.getScheduleEmployeeById(employeeId);
	}

	@Override
	public boolean containsUserInSession() throws IllegalArgumentException {
		return getUserFromSession() != null;
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
		String error = validator.validatePassword(newPassword);
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
		Employee e = getCurrentEmployee();
		e.setMinAndMaxDays(emp.getMinDays(), emp.getMaxDays());
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
	public long getFirstDraftPeriod() throws IllegalArgumentException {
		java.sql.Date dateTime = new java.sql.Date(System.currentTimeMillis());
		Period period = scheduleDAO.getFirstDraftPeriod(dateTime);
		if (period == null) {
			throw new IllegalArgumentException("Ближайший черновик не найден");
		}
		return period.getPeriodId();

	}

	@Override
	public Schedule generate(Schedule s) throws IllegalArgumentException {
		
		// TODO refactor schedule. Move real generate code to Schedule
		if (s.getStatus() != Schedule.Status.DRAFT)
			throw new IllegalArgumentException(
					"Данный график не имеет статус черновик");

		mode.setMode(GenFlags.ONLY_ONE_SHIFT, GenFlags.SCHEDULE_CAN_EMPTY, GenFlags.CHECK_MAX_HOURS_IN_WEEK, GenFlags.WEEKEND_AFTER_MAX_HOURS);
//		System.out.println("-- Shedule --\n" + s);

		// get all Employees to Schedule
		ArrayList<Employee> allEmps = 
				(ArrayList<Employee>) employeeDAO.findEmployees(Right.ADMIN);
		if (allEmps == null)
			throw new IllegalArgumentException("Не найдено ни одного сотрудника");
		
		for (Employee e : allEmps) {
			e.clearAssignments();
		}
		
		// TODO set schedule preferences into schedule
		Preference prefs = preferenceDAO.getLastPreference();
		
		// max of maxDays
		int max = 0;
		// max of minDays
		int min = 0;
		for (Employee employee : allEmps) {
			if (employee.getMaxDays() > max)
				max = employee.getMaxDays();
			if (employee.getMinDays() > min)
				min = employee.getMinDays();
		}
		
		EmplyeeObjective emplyeeObjective = EmplyeeObjective.getInstance(min, max);
		
		s.generate(allEmps, prefs, emplyeeObjective);
		return s;
	}

	public void setEmployeeDAO(EmployeeDAO employeeDAO) {
		this.employeeDAO = employeeDAO;
	}
}
