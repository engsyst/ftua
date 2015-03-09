package ua.nure.ostpc.malibu.shedule.server;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
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
import ua.nure.ostpc.malibu.shedule.dao.DAOException;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.PreferenceDAO;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.dao.UserDAO;
import ua.nure.ostpc.malibu.shedule.entity.Category;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubPref;
import ua.nure.ostpc.malibu.shedule.entity.ClubSettingViewData;
import ua.nure.ostpc.malibu.shedule.entity.DraftViewData;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.EmployeeSettingsData;
import ua.nure.ostpc.malibu.shedule.entity.EmplyeeObjective;
import ua.nure.ostpc.malibu.shedule.entity.GenFlags;
import ua.nure.ostpc.malibu.shedule.entity.Holiday;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Preference;
import ua.nure.ostpc.malibu.shedule.entity.Right;
import ua.nure.ostpc.malibu.shedule.entity.Role;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;
import ua.nure.ostpc.malibu.shedule.entity.ScheduleViewData;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.entity.UserWithEmployee;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.security.Hashing;
import ua.nure.ostpc.malibu.shedule.service.ExcelService;
import ua.nure.ostpc.malibu.shedule.service.MailService;
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

		Map<String, String[]> params = request.getParameterMap();
		if (params.containsKey("download")) {
			try {
				long id = Long.parseLong(request.getParameter("id"));
				boolean isFull = Boolean.parseBoolean(request
						.getParameter("download"));
				Schedule s = scheduleDAO.getSchedule(id);
				Employee emp = null;
				if (!isFull)
					emp = employeeDAO.getScheduleEmployeeById(Long
							.parseLong(request.getParameter("empId")));
				byte[] xls = this.getExcel(s, isFull, emp);
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-Disposition",
						" attachment; filename=" + makeScheduleFileName(s, emp));
				response.setContentLength(xls.length);
				final int BUFFER = 1024 * 4;
				response.setBufferSize(BUFFER);
				ServletOutputStream o = response.getOutputStream();
				o.write(xls);
				o.close();

			} catch (Exception e) {
				log.error("Download shedule can not start");
			}
		} else {
			RequestDispatcher dispatcher = request
					.getRequestDispatcher(Path.PAGE__SCHEDULE_MANAGER);
			dispatcher.forward(request, response);
		}

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
		List<Role> ur = user.getRoles();
		System.err.println("ScheduleManagerServiceImpl.userRoles "
				+ (System.currentTimeMillis() - t1) + "ms");
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
		System.err.println("ScheduleManagerServiceImpl.getEmployee "
				+ (System.currentTimeMillis() - t1) + "ms");
		return employee;
	}

	@Override
	public Collection<Club> getClubes() throws IllegalArgumentException {
		long t1 = System.currentTimeMillis();
		List<Club> lc = clubDAO.getDependentClubs();
		System.err.println("ScheduleManagerServiceImpl.getClubes "
				+ (System.currentTimeMillis() - t1) + "ms");
		return lc;
	}

	@Override
	public ScheduleViewData getScheduleViewData(Long id)
			throws IllegalArgumentException {
		long t1 = System.currentTimeMillis();
		ScheduleViewData data = new ScheduleViewData();
		List<Employee> employeeList;
		List<Category> categoryList = getCategoriesWithEmployees();
		List<Employee> removedEmployeeList = employeeDAO
				.getRemovedScheduleEmployees();
		if (id != null) {
			Schedule schedule = scheduleDAO.getSchedule(id);
			data.setSchedule(schedule);
			employeeList = employeeDAO.getScheduleEmployeesForSchedule(id);
			List<Employee> unusedEmployeeList = new ArrayList<Employee>(
					removedEmployeeList);
			unusedEmployeeList.removeAll(employeeList);
			removeEmployeesFromCategoryList(categoryList, unusedEmployeeList);
		} else {
			employeeList = getScheduleEmployees();
			employeeList.removeAll(removedEmployeeList);
			removeEmployeesFromCategoryList(categoryList, removedEmployeeList);
		}
		data.setStartDate(getStartDate());
		data.setClubs(clubDAO.getDependentClubs());
		data.setEmployees(employeeList);
		data.setCategories(categoryList);
		data.setPrefs(getPreference());
		System.err.println("ScheduleManagerServiceImpl.getScheduleViewData "
				+ (System.currentTimeMillis() - t1) + "ms");
		return data;
	}

	private void removeEmployeesFromCategoryList(List<Category> categoryList,
			List<Employee> employeeList) {
		for (Category category : categoryList) {
			List<Long> employeeIdList = (List<Long>) category
					.getEmployeeIdList();
			for (Employee removedEmployee : employeeList) {
				employeeIdList.remove(removedEmployee.getEmployeeId());
			}
			category.setEmployeeIdList(employeeIdList);
		}
	}

	public List<ClubPref> getClubPref(long periodId) {
		long t1 = System.currentTimeMillis();
		List<ClubPref> cp = clubPrefDAO.getClubPrefsByPeriodId(periodId);
		System.err.println("ScheduleManagerServiceImpl.getClubPref "
				+ (System.currentTimeMillis() - t1) + "ms");
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
		System.err.println("ScheduleManagerServiceImpl.getEmpToClub "
				+ (System.currentTimeMillis() - t1) + "ms");
		return empToClub;
	}

	@Override
	public Schedule getScheduleById(long periodId) {
		long t1 = System.currentTimeMillis();
		Schedule schedule = nonclosedScheduleCacheService.getSchedule(periodId);
		if (schedule == null) {
			schedule = scheduleDAO.getSchedule(periodId);
		}
		System.err.println("ScheduleManagerServiceImpl.getScheduleById "
				+ (System.currentTimeMillis() - t1) + "ms");
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
		System.err.println("ScheduleManagerServiceImpl.updateShift "
				+ (System.currentTimeMillis() - t1) + "ms");
		return result;
	}

	@Override
	public Collection<Club> getMalibuClubs() throws IllegalArgumentException {
		long t1 = System.currentTimeMillis();
		Collection<Club> malibuClubs = clubDAO.getAllOuterClubs();
		if (malibuClubs == null)
			return new ArrayList<Club>();
		else
			System.err.println("ScheduleManagerServiceImpl.getClubs "
					+ (System.currentTimeMillis() - t1) + "ms");
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
		User user = getUserFromSession();
		for (Club club : clubsForDelete) {
			if (clubDAO.containsInSchedules(club.getClubId())) {
				club.setDeleted(true);
				try {
					clubDAO.updateClub(club);
				} catch (DAOException e) {
					throw new IllegalArgumentException(
							"Произошла ошибка при удалении клуба "
									+ club.getTitle());
				}
			} else {
				try {
					clubDAO.removeClub(club.getClubId());
					if (log.isInfoEnabled()) {
						if (user != null) {
							log.info("UserId: " + user.getUserId() + " Логин: "
									+ user.getLogin()
									+ " Действие: Настройка. Удалил клуб \""
									+ club.getTitle() + "\" (clubId="
									+ club.getClubId() + ") из таблицы Club.");
						}
					}
				} catch (Exception e) {
					log.error("Произошла ошибка при удалении клуба \""
							+ club.getTitle() + "\" (clubId="
							+ club.getClubId() + ") из таблицы Club.");
					throw new IllegalArgumentException(
							"Произошла ошибка при удалении клуба "
									+ club.getTitle());
				}
			}
		}

		for (Club club : clubsForUpdate) {
			try {
				clubDAO.updateClub(club);
				log.error("Произошла ошибка при обновлении клуба \""
						+ club.getTitle() + "\" (clubId=" + club.getClubId()
						+ ") в таблице Club.");
				if (log.isInfoEnabled()) {
					if (user != null) {
						log.info("UserId: " + user.getUserId() + " Логин: "
								+ user.getLogin()
								+ " Действие: Настройка. Обновил клуб \""
								+ club.getTitle() + "\" (clubId="
								+ club.getClubId() + ") в таблице Club.");
					}
				}
			} catch (DAOException e) {
				throw new IllegalArgumentException(
						"Произошла ошибка при обновлении клуба "
								+ club.getTitle());
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
					if (user != null) {
						log.info("UserId: " + user.getUserId() + " Логин: "
								+ user.getLogin()
								+ " Действие: Настройка. Добавил клуб \""
								+ club.getTitle() + "\" (clubId="
								+ club.getClubId() + ") в таблицу Club. "
								+ club.toString());
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
					if (user != null) {
						log.info("UserId: " + user.getUserId() + " Логин: "
								+ user.getLogin()
								+ " Действие: Настройка. Добавил клуб \""
								+ club.getTitle() + "\" (clubId="
								+ club.getClubId()
								+ ") в таблицах Club и ComplianceClub. "
								+ club.toString());
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
		Collection<Employee> ourEmployee = null;
		try {
			ourEmployee = employeeDAO.getOnlyOurEmployees();
		} catch (DAOException e) {
			log.error("Can not getOnlyOurEmployees", e);
			throw new IllegalArgumentException(
					"Невозможно получить данные с сервера", e);
		}
		if (ourEmployee == null) {
			return new ArrayList<Employee>();
		} else {
			return ourEmployee;
		}
	}

	@Override
	public Map<Long, Employee> getDictionaryEmployee()
			throws IllegalArgumentException {
		Map<Long, Employee> conformity = employeeDAO.getConformity();
		if (conformity == null) {
			return new HashMap<Long, Employee>();
		} else {
			return conformity;
		}
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
			if (employeeDAO.containsInSchedules(employee.getEmployeeId())) {
				employee.setDeleted(true);
				try {
					if (log.isInfoEnabled() && user != null) {
						log.info("UserId: "
								+ user.getUserId()
								+ " Логин: "
								+ user.getLogin()
								+ " Действие: Настройка. Обновил сотрудника с пометкой \"удалённый\" "
								+ employee.getNameForSchedule()
								+ " (employeeId=" + employee.getEmployeeId()
								+ ") из таблицы Employee.");
					}
					employeeDAO.updateEmployee(employee);
				} catch (Exception e) {
					log.error("Произошла ошибка при обновлении сотрудника с пометкой \"удалённый\" "
							+ employee.getNameForSchedule());
					throw new IllegalArgumentException(
							"Произошла ошибка при обновлении сотрудника с пометкой \"удалённый\" "
									+ employee.getNameForSchedule());
				}
			} else {
				try {
					employeeDAO.deleteEmployee(employee.getEmployeeId());
					if (log.isInfoEnabled() && user != null) {
						log.info("UserId: " + user.getUserId() + " Логин: "
								+ user.getLogin()
								+ " Действие: Настройка. Удалил сотрудника "
								+ employee.getNameForSchedule()
								+ " (employeeId=" + employee.getEmployeeId()
								+ ") из таблицы Employee.");
					}
				} catch (DAOException e) {
					log.error("Произошла ошибка при удалении сотрудника "
							+ employee.getNameForSchedule());
					throw new IllegalArgumentException(
							"Произошла ошибка при удалении сотрудника "
									+ employee.getShortName());
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

		List<Employee> employeeList = (List<Employee>) getOnlyOurEmployees();
		Map<Long, Employee> dictionaryEmployeesMap = getDictionaryEmployee();
		Iterator<Entry<Long, Employee>> iterator = dictionaryEmployeesMap
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Long, Employee> entry = iterator.next();
			employeeList.add(entry.getValue());
		}

		if (!roleForInsert.isEmpty()) {
			if (!employeeDAO.setRolesForEmployees(roleForInsert)) {
				log.error("Произошла ошибка при установке ролей сотрудникам");
				throw new IllegalArgumentException(
						"Произошла ошибка при установке ролей сотрудникам");
			} else {
				if (log.isInfoEnabled() && user != null
						&& roleForInsert != null) {
					Iterator<Entry<Integer, Collection<Long>>> it = roleForInsert
							.entrySet().iterator();
					while (it.hasNext()) {
						Entry<Integer, Collection<Long>> entry = it.next();
						if (entry.getValue() != null
								&& !entry.getValue().isEmpty()) {
							StringBuilder sb = new StringBuilder();
							sb.append("UserId: ");
							sb.append(user.getUserId());
							sb.append(" Логин: ");
							sb.append(user.getLogin());
							sb.append(" Действие: Настройка. Установил роль ");
							sb.append(getRightByEntryKey(entry.getKey()));
							sb.append(" для сотрудников ");
							for (Long employeeId : entry.getValue()) {
								for (Employee employee : employeeList) {
									if (employee.getEmployeeId() == employeeId) {
										sb.append("(");
										sb.append(employee.getNameForSchedule());
										sb.append("; employeeId=");
										sb.append(employeeId);
										sb.append(") ");
									}
								}
							}
							sb.append(" в таблице EmployeeUserRole.");
							log.info(sb);
						}
					}
				}
			}
		}

		if (!roleForDelete.isEmpty()) {
			if (!employeeDAO.deleteRolesForEmployees(roleForDelete)) {
				log.error("Произошла ошибка при удалении ролей сотрудников");
				throw new IllegalArgumentException(
						"Произошла ошибка при удалении ролей сотрудников");
			} else {
				if (log.isInfoEnabled() && user != null
						&& roleForDelete != null) {
					Iterator<Entry<Integer, Collection<Long>>> it = roleForDelete
							.entrySet().iterator();
					while (it.hasNext()) {
						Entry<Integer, Collection<Long>> entry = it.next();
						if (entry.getValue() != null
								&& !entry.getValue().isEmpty()) {
							StringBuilder sb = new StringBuilder();
							sb.append("UserId: ");
							sb.append(user.getUserId());
							sb.append(" Логин: ");
							sb.append(user.getLogin());
							sb.append(" Действие: Настройка. Удалил роль ");
							sb.append(getRightByEntryKey(entry.getKey()));
							sb.append(" для сотрудников ");
							for (Long employeeId : entry.getValue()) {
								for (Employee employee : employeeList) {
									if (employee.getEmployeeId() == employeeId) {
										sb.append("(");
										sb.append(employee.getNameForSchedule());
										sb.append("; employeeId=");
										sb.append(employeeId);
										sb.append(") ");
									}
								}
							}
							sb.append(" в таблице EmployeeUserRole.");
							log.info(sb.toString());
						}
					}
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
							StringBuilder sb = new StringBuilder();
							sb.append("UserId: ");
							sb.append(user.getUserId());
							sb.append(" Логин: ");
							sb.append(user.getLogin());
							sb.append(" Действие: Настройка. Добавил сотрудника ");
							sb.append(employee.getNameForSchedule());
							sb.append(" (employeeId=");
							sb.append(employee.getEmployeeId());
							sb.append(") и роль (");
							sb.append(getRightByEntryKey(entry.getKey()));
							sb.append(") в таблицы Employee, EmployeeUserRole и ComplianceEmployee.");
							log.info(sb);
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
			if (log.isInfoEnabled() && user != null
					&& roleForInsertWithoutConformity != null) {
				Iterator<Entry<Integer, Collection<Employee>>> it = roleForInsertWithoutConformity
						.entrySet().iterator();
				while (it.hasNext()) {
					Entry<Integer, Collection<Employee>> entry = it.next();
					Collection<Employee> employees = entry.getValue();
					if (employees != null) {
						for (Employee employee : employees) {
							StringBuilder sb = new StringBuilder();
							sb.append("UserId: ");
							sb.append(user.getUserId());
							sb.append(" Логин: ");
							sb.append(user.getLogin());
							sb.append(" Действие: Настройка. Добавил сотрудника ");
							sb.append(employee.getNameForSchedule());
							sb.append(" (employeeId=");
							sb.append(employee.getEmployeeId());
							sb.append(") и роль (");
							sb.append(getRightByEntryKey(entry.getKey()));
							sb.append(") в таблицы Employee и EmployeeUserRole.");
							log.info(sb);
						}
					}
				}
			}
		}
	}

	private Right getRightByEntryKey(int entryKey) {
		switch (entryKey) {
		case 1:
			return Right.ADMIN;
		case 2:
			return Right.RESPONSIBLE_PERSON;
		case 3:
			return Right.SUBSCRIBER;
		}
		return null;
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
		Collection<Holiday> holidays = preferenceDAO.getHolidays();
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
			if (!preferenceDAO.removeHoliday(holiday.getHolidayid())) {
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
		if (!preferenceDAO.insertHolidays(holidaysForInsert)) {
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
		try {
			userDAO.insertUser(newUser);
			User currentUser = getUserFromSession();
			if (log.isInfoEnabled()) {
				StringBuilder sb = new StringBuilder();
				sb.append("UserId: ");
				sb.append(currentUser.getUserId());
				sb.append(" Логин: ");
				sb.append(currentUser.getLogin());
				sb.append(" Действие: Добавил пользователя (Логин: ");
				sb.append(newUser.getLogin());
				sb.append(").");
				log.info(sb.toString());
			}
		} catch (DAOException e) {
			log.error("Произошла ошибка при добавлении пользователя");
			throw new IllegalArgumentException(
					"Произошла ошибка при добавлении пользователя");
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
	public List<Employee> getScheduleEmployees()
			throws IllegalArgumentException {
		List<Employee> scheduleEmployees = employeeDAO.getScheduleEmployees();
		if (scheduleEmployees == null) {
			scheduleEmployees = new ArrayList<Employee>();
		}
		return scheduleEmployees;
	}

	@Override
	public Preference getPreference() throws IllegalArgumentException {
		return preferenceDAO.getLastPreference();
	}

	@Override
	public List<Category> getCategoriesWithEmployees()
			throws IllegalArgumentException {
		List<Category> categoriesWithEmployees = categoryDAO
				.getCategoriesWithEmployees();
		if (categoriesWithEmployees == null) {
			categoriesWithEmployees = new ArrayList<Category>();
		}
		return categoriesWithEmployees;
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
		if (!preferenceDAO.updatePreference(preference)) {
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
		try {
			employeeDAO.updateEmployee(employee);
			User user = getUserFromSession();
			if (log.isInfoEnabled() && user != null) {
				log.info("UserId: " + user.getUserId() + " Логин: "
						+ user.getLogin()
						+ " Действие: Обновление личных данных о сотруднике "
						+ employee.getNameForSchedule() + " (employeeId="
						+ employee.getEmployeeId() + ").");
			}
		} catch (DAOException e) {
			log.error("Не удалось обновить личные данные сотрудника "
					+ employee.getNameForSchedule() + " (employeeId="
					+ employee.getEmployeeId() + ").");
			throw new IllegalArgumentException(
					"Не удалось обновить личные данные!");
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
					try {
						employeeDAO.updateEmployee(employee);
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
					} catch (Exception e) {
						log.error("Не удалось обновить личные данные сотрудника "
								+ employee.getNameForSchedule()
								+ " (employeeId="
								+ employee.getEmployeeId()
								+ ").");
						throw new IllegalArgumentException(
								"Не удалось обновить личные данные!");
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
		if (errorMap == null || errorMap.size() == 0) {
			errorMap = employeeDAO.checkEmployeeDataBeforeUpdate(email,
					cellPhone, employeeId);
			if (errorMap == null || errorMap.size() == 0) {
				Employee employee = employeeDAO
						.getScheduleEmployeeById(employeeId);
				if (employee != null) {
					employee.setEmail(email);
					employee.setCellPhone(cellPhone);
					try {
						employeeDAO.updateEmployee(employee);
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
					} catch (Exception e) {
						log.error("Не удалось обновить личные данные сотрудника "
								+ employee.getNameForSchedule()
								+ " (employeeId="
								+ employee.getEmployeeId()
								+ ").");
						throw new IllegalArgumentException(
								"Не удалось обновить личные данные!");
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
	public EmployeeUpdateResult changePassword(String oldPassword,
			String newPassword, long employeeId)
			throws IllegalArgumentException {
		EmployeeUpdateResult updateResult = new EmployeeUpdateResult();
		Map<String, String> errorMap = validator.validateChangePasswordData(
				oldPassword, newPassword);
		if (errorMap == null || errorMap.size() == 0) {
			User user = userDAO.getUserByEmployeeId(employeeId);
			if (user != null) {
				if (Hashing.salt(oldPassword, user.getLogin()).equals(
						user.getPassword())) {
					try {
						user.setPassword(Hashing.salt(newPassword,
								user.getLogin()));
						userDAO.updateUser(user);
						if (log.isInfoEnabled()) {
							Employee employee = employeeDAO
									.getScheduleEmployeeById(employeeId);
							User currentUser = getUserFromSession();
							log.info("UserId: "
									+ currentUser.getUserId()
									+ " Логин: "
									+ currentUser.getLogin()
									+ " Действие: Смена пароля входа в систему. Пароль успешно изменён. Пароль изменён для "
									+ employee.getNameForSchedule());
						}
						updateResult.setResult(true);
						Employee employee = employeeDAO
								.getScheduleEmployeeById(employeeId);
						updateResult.setEmployee(employee);
						return updateResult;
					} catch (DAOException e) {
						log.error("Не удалось сменить пароль входа в систему!");
						throw new IllegalArgumentException(
								"Не удалось сменить пароль входа в систему!");
					}
				} else {
					updateResult.setResult(false);
					errorMap = new LinkedHashMap<String, String>();
					errorMap.put(AppConstants.OLD_PASSWORD,
							AppConstants.PASSWORD_SERVER_ERROR);
					updateResult.setErrorMap(errorMap);
					return updateResult;
				}

			}
		}
		updateResult.setResult(false);
		updateResult.setErrorMap(errorMap);
		return updateResult;
	}

	@Override
	public EmployeeUpdateResult changeLoginAndPassword(String newLogin,
			String newPassword, long employeeId)
			throws IllegalArgumentException {
		EmployeeUpdateResult updateResult = new EmployeeUpdateResult();
		Map<String, String> errorMap = new LinkedHashMap<String, String>();
		User currentUser = getUserFromSession();
		List<Role> roles = currentUser.getRoles();
		boolean isResponsiblePerson = false;
		for (Role role : roles) {
			if (role.getRight() == Right.RESPONSIBLE_PERSON) {
				isResponsiblePerson = true;
				break;
			}
		}
		if (isResponsiblePerson) {
			String errorMessage = validator.validateSigninLogin(newLogin);
			if (errorMessage != null) {
				errorMap.put(AppConstants.LOGIN, errorMessage);
			}
			errorMessage = validator.validatePassword(newPassword);
			if (errorMessage != null) {
				errorMap.put(AppConstants.NEW_PASSWORD, errorMessage);
			}
			if (errorMap.size() == 0) {
				User user = userDAO.getUserByEmployeeId(employeeId);
				boolean isNewUser = false;
				if (user == null) {
					isNewUser = true;
					user = new User();
				}
				if (userDAO.containsOtherUserWithLogin(newLogin,
						user.getUserId())) {
					errorMap.put(AppConstants.LOGIN,
							AppConstants.LOGIN_SERVER_ERROR);
				} else {
					user.setLogin(newLogin);
					try {
						if (isNewUser) {
							user.setEmployeeId(employeeId);
							user.setPassword(newPassword);
							userDAO.insertUser(user);
						} else {
							user.setPassword(Hashing
									.salt(newPassword, newLogin));
							userDAO.updateUser(user);
						}
					} catch (DAOException e) {
						StringBuilder sb = new StringBuilder();
						sb.append("Не удалось ");
						if (isNewUser) {
							sb.append("добавить пользователя!");
						} else {
							sb.append("изменить логин и пароль входа в систему!");
						}
						log.error(sb.toString());
						throw new IllegalArgumentException(sb.toString());
					}
					if (log.isInfoEnabled()) {
						Employee employee = employeeDAO
								.getScheduleEmployeeById(employeeId);
						StringBuilder sb = new StringBuilder();
						sb.append("UserId: ");
						sb.append(currentUser.getUserId());
						sb.append(" Логин: ");
						sb.append(currentUser.getLogin());
						if (isNewUser) {
							sb.append(" Действие: Добавил пользователя для ");
						} else {
							sb.append(" Действие: Смена логина и пароля входа в систему. Логин и пароль успешно изменены для ");
						}
						sb.append(employee.getNameForSchedule());
						sb.append(" (Логин: ");
						sb.append(user.getLogin());
						sb.append(").");
						log.info(sb.toString());
					}
					updateResult.setResult(true);
					Employee employee = employeeDAO
							.getScheduleEmployeeById(employeeId);
					updateResult.setEmployee(employee);
					return updateResult;
				}

			}
		} else {
			updateResult.setResult(false);
			errorMap = new LinkedHashMap<String, String>();
			errorMap.put(AppConstants.NEW_PASSWORD,
					AppConstants.PASSWORD_INCORRECT_ROLE_ERROR);
			updateResult.setErrorMap(errorMap);
			return updateResult;
		}
		updateResult.setResult(false);
		updateResult.setErrorMap(errorMap);
		return updateResult;
	}

	@Override
	public EmployeeUpdateResult setPreference(int minDayNumber,
			int maxDayNumber, long employeeId) throws IllegalArgumentException {
		EmployeeUpdateResult updateResult = new EmployeeUpdateResult();
		Map<String, String> errorMap = validator.validateEmployeePrefs(
				minDayNumber, maxDayNumber);
		if (errorMap == null || errorMap.size() == 0) {
			Employee employee = employeeDAO.getScheduleEmployeeById(employeeId);
			if (employee != null) {
				employee.setMinAndMaxDays(minDayNumber, maxDayNumber);
				if (!employeeDAO.updateEmployeePrefs(employee)) {
					log.error("Произошла ошибка при сохранении предпочтений!");
					throw new IllegalArgumentException(
							"Произошла ошибка при сохранении предпочтений!");
				} else {
					User user = getUserFromSession();
					if (log.isInfoEnabled() && user != null) {
						log.info("UserId: "
								+ user.getUserId()
								+ " Логин: "
								+ user.getLogin()
								+ " Действие: Обновление данных о предпочтениях сотрудника "
								+ employee.getNameForSchedule()
								+ " (employeeId=" + employee.getEmployeeId()
								+ ").");
					}
					updateResult.setResult(true);
					employee = employeeDAO.getScheduleEmployeeById(employeeId);
					updateResult.setEmployee(employee);
					return updateResult;
				}
			}
		}
		updateResult.setResult(false);
		updateResult.setErrorMap(errorMap);
		return updateResult;
	}

	@Override
	public String getUser() throws IllegalArgumentException {
		User user = getUserFromSession();
		long employeeId = user.getEmployeeId();
		Employee employee = employeeDAO.findEmployee(employeeId);
		return employee.getFirstName() + " " + employee.getLastName();
	}

	@Override
	public DraftViewData getDraftView(long id) throws IllegalArgumentException {
		DraftViewData data = new DraftViewData();
		data.setEmployee(getEmployee());
		data.setMap(getEmpToClub(id));
		data.setSchedule(getScheduleById(id));
		return data;
	}

	@Override
	public UserWithEmployee getUserWithEmployee()
			throws IllegalArgumentException {
		User user = getUserFromSession();
		long employeeId = user.getEmployeeId();
		Employee employee = employeeDAO.findEmployee(employeeId);
		return new UserWithEmployee(user, employee);
	}

	@Override
	public User getUserByEmployeeId(long employeeId) {
		User user = userDAO.getUserByEmployeeId(employeeId);
		return user;
	}

	@Override
	public Schedule getCurrentSchedule() {
		return nonclosedScheduleCacheService.getCurrentSchedule();
	}

	@Override
	public long getFirstDraftPeriod() throws IllegalArgumentException {
		Date dateTime = new Date(System.currentTimeMillis());
		Period period = scheduleDAO.getFirstDraftPeriod(dateTime);
		if (period == null) {
			throw new IllegalArgumentException("Ближайший черновик не найден");
		}
		return period.getPeriodId();

	}

	@Override
	public Schedule generate(Schedule s) throws IllegalArgumentException {

		if (s.getStatus() != Schedule.Status.DRAFT)
			throw new IllegalArgumentException(
					"Данный график не имеет статус черновик");

		mode.setMode(GenFlags.ONLY_ONE_SHIFT, GenFlags.SCHEDULE_CAN_EMPTY,
				GenFlags.CHECK_MAX_HOURS_IN_WEEK,
				GenFlags.WEEKEND_AFTER_MAX_HOURS);
		// System.out.println("-- Shedule --\n" + s);

		// get all Employees to Schedule
		ArrayList<Employee> allEmps = (ArrayList<Employee>) employeeDAO
				.getScheduleEmployees();
		if (allEmps == null) {
			throw new IllegalArgumentException(
					"Не найдено ни одного сотрудника");
		}

		List<Employee> removedEmployeeList = employeeDAO
				.getRemovedScheduleEmployees();
		for (Employee removedEmployee : removedEmployeeList) {
			if (!s.containsEmployeeInShifts(removedEmployee.getEmployeeId())
					&& !s.containsEmployeeInClubPrefs(removedEmployee
							.getEmployeeId())) {
				allEmps.remove(removedEmployee);
			}
		}

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

		EmplyeeObjective emplyeeObjective = EmplyeeObjective.getInstance(min,
				max);

		s.generate(allEmps, prefs, emplyeeObjective);
		return s;
	}

	public void setEmployeeDAO(EmployeeDAO employeeDAO) {
		this.employeeDAO = employeeDAO;
	}

	// ======================================

	@Override
	public List<ClubSettingViewData> getAllClubs()
			throws IllegalArgumentException {
		try {
			return clubDAO.getAllClubs();
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Невозможно получить данные с сервера.");
		}
	}

	@Override
	public Club setClubIndependent(long id, boolean isIndepended) {
		try {
			return clubDAO.setClubIndependent(id, isIndepended);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Невозможно получить данные с сервера.");
		}
	}

	@Override
	public Club removeClub(long id) {
		try {
			return clubDAO.removeClub(id);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Невозможно получить данные с сервера.");
		}
	}

	@Override
	public Club importClub(Club club) {
		try {
			return clubDAO.importClub(club);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Невозможно получить данные с сервера.");
		}
	}

	@Override
	public List<EmployeeSettingsData> getEmployeeSettingsData() {
		try {
			return employeeDAO.getEmployeeSettingsData();
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Невозможно получить данные с сервера.");
		}
	}

	private byte[] getExcel(Schedule s, boolean full, Employee emp) {
		if (full)
			return ExcelService.scheduleAllToExcelConvert(s);
		else
			return ExcelService.scheduleUserToExcelConvert(s, emp);
	}

	private String makeScheduleFileName(Schedule s, Employee emp) {
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		String period = df.format(s.getPeriod().getStartDate()) + "-"
				+ df.format(s.getPeriod().getEndDate());
		String fName = "Schedule_" + period + (emp == null ? "_forAll" : "")
				+ ".xls";
		return fName;
	}

	@Override
	public void sendMail(long id, boolean full, boolean toAll, Long empId)
			throws IllegalArgumentException {
		String[] emails = null;
		Employee emp = null;
		Schedule s = scheduleDAO.getSchedule(id);
		if (toAll) {
			emails = (String[]) employeeDAO.getEmailListForSubscribers()
					.toArray();
		} else {
			emp = employeeDAO.getScheduleEmployeeById(empId);
			emails = new String[1];
			emails[0] = emp.getEmail();
		}

		String fName = makeScheduleFileName(s, emp);
		String theme = fName;
		byte[] xls = getExcel(s, full, emp);
		try {
			MailService.configure("mail.properties");
			MailService.sendMail(theme, "", xls, fName, emails);
		} catch (Exception e) {
			log.error("Can not send e-mail", e.getCause());
			throw new IllegalArgumentException("Невозможно отослать почту ", e);
		}
		User u = getUserFromSession();
		StringBuilder sb = new StringBuilder("UserId ");
		sb.append(u.getUserId());
		sb.append(" Логин: ");
		sb.append(u.getLogin());
		sb.append("  Действие: ");
		sb.append("Отправка почты.");
		sb.append(toAll ? " всем " : "себе ");
		sb.append(" PeriodId: ");
		sb.append(id);
		log.info(sb.toString());
	}

	@Override
	public Club getClub(Long id) {
		return clubDAO.findClubById(id);
	}

	@Override
	public Club setClub(Club club) throws IllegalArgumentException {
		try {
			return clubDAO.updateClub(club);
		} catch (DAOException e) {
			throw new IllegalArgumentException(e.getLocalizedMessage());
		}
	}

	@Override
	public Employee importEmployee(Employee employee) {
		try {
			// Set admin and subscriber
			List<Role> roles = employeeDAO.getRoles();
			for (int i = 0; i < roles.size(); i++) {
				Role r = roles.get(i);
				if (!Right.ADMIN.equals(r.getRight())
						&& !Right.SUBSCRIBER.equals(r.getRight())) {
					roles.remove(i);
				}
			}
			return employeeDAO.importEmployee(employee, roles);
		} catch (Exception e) {
			log.error("Can not importEmployee", e);
			throw new IllegalArgumentException(
					"Невозможно получить данные с сервера.", e);
		}
	}

	@Override
	public void removeEmployee(long id) {
		try {
			employeeDAO.deleteEmployee(id);
		} catch (DAOException e) {
			throw new IllegalArgumentException(
					"Невозможно получить данные с сервера.", e);
		}
	}

	@Override
	public long[] updateEmployeeRole(long empId, long roleId, boolean enable)
			throws IllegalArgumentException {
		try {
			if (enable)
				employeeDAO.insertEmployeeUserRole(empId, roleId);
			else
				employeeDAO.deleteEmployeeUserRole(empId, roleId);
			return new long[] { empId, roleId };
		} catch (DAOException e) {
			throw new IllegalArgumentException(
					"Невозможно обновить данные с сервере.", e);
		}
	}

}
