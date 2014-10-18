package ua.nure.ostpc.malibu.shedule.server;

import java.io.IOException;
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
import java.util.Map;
import java.util.Set;

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
import ua.nure.ostpc.malibu.shedule.client.panel.creation.CreateScheduleService;
import ua.nure.ostpc.malibu.shedule.client.panel.editing.EditScheduleService;
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
import ua.nure.ostpc.malibu.shedule.entity.Role;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.security.Hashing;
import ua.nure.ostpc.malibu.shedule.service.NonclosedScheduleCacheService;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ScheduleManagerServiceImpl extends RemoteServiceServlet implements
		ScheduleManagerService, ScheduleDraftService, StartSettingService,
		CreateScheduleService, UserSettingService, EditScheduleService {
	private ScheduleDAO scheduleDAO;
	private NonclosedScheduleCacheService nonclosedScheduleCacheService;
	private static final Logger log = Logger
			.getLogger(ScheduleManagerServiceImpl.class);

	private CategoryDAO categoryDAO;
	private HolidayDAO holidayDAO;
	private UserDAO userDAO;
	private PreferenceDAO preferenceDAO;
	private EmployeeDAO employeeDAO;
	private ClubDAO clubDAO;
	private ClubPrefDAO clubPrefDAO;

	public ScheduleManagerServiceImpl() {
		super();
		if (log.isDebugEnabled()) {
			log.debug("Servlet creates");
		}
	}

	@Override
	public void init() {
		ServletContext servletContext = getServletContext();
		scheduleDAO = (ScheduleDAO) servletContext
				.getAttribute(AppConstants.SCHEDULE_DAO);
		nonclosedScheduleCacheService = (NonclosedScheduleCacheService) servletContext
				.getAttribute(AppConstants.NONCLOSED_SCHEDULE_CACHE_SERVICE);
		if (nonclosedScheduleCacheService == null) {
			log.error("NonclosedScheduleCacheService attribute is not exists.");
			throw new IllegalStateException(
					"NonclosedScheduleCacheService attribute is not exists.");
		}
		if (scheduleDAO == null) {
			log.error("ScheduleDAO attribute is not exists.");
			throw new IllegalStateException(
					"ScheduleDAO attribute is not exists.");
		}
		employeeDAO = (EmployeeDAO) servletContext
				.getAttribute(AppConstants.EMPLOYEE_DAO);
		preferenceDAO = (PreferenceDAO) servletContext
				.getAttribute(AppConstants.PREFERENCE_DAO);
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
		clubDAO = (ClubDAO) servletContext.getAttribute(AppConstants.CLUB_DAO);
		clubPrefDAO = (ClubPrefDAO) servletContext.getAttribute(AppConstants.CLUB_PREF_DAO);
		categoryDAO = (CategoryDAO) servletContext
				.getAttribute(AppConstants.CATEGORY_DAO);
		holidayDAO = (HolidayDAO) servletContext
				.getAttribute(AppConstants.HOLIDAY_DAO);
		userDAO = (UserDAO) servletContext.getAttribute(AppConstants.USER_DAO);

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
		return nonclosedScheduleCacheService.lockSchedule(periodId);
	}

	@Override
	public List<Role> userRoles() throws IllegalArgumentException {
		HttpSession session = getThreadLocalRequest().getSession();
		User user = (User) session.getAttribute(AppConstants.USER);
		return user.getRoles();
	}

	@Override
	public Employee getEmployee() throws IllegalArgumentException {
		if (log.isDebugEnabled()) {
			log.debug("getEmployee method starts");
		}
		HttpServletRequest request = getThreadLocalRequest();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(AppConstants.USER);
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
	public boolean updateShift(AssignmentInfo inform, Employee employee) {
		return nonclosedScheduleCacheService.updateShift(inform, employee);
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
		for (Club elem : clubsForDelete) {
			if (!clubDAO.removeClub(elem.getClubId())) {
				throw new IllegalArgumentException(
						"Произошла ошибка при удалении клуба "
								+ elem.getTitle());
			}
		}

		for (Club elem : clubsForUpdate) {
			if (!clubDAO.updateClub(elem)) {
				throw new IllegalArgumentException(
						"Произошла ошибка при обновлении клуба "
								+ elem.getTitle());
			}
		}

		if (!clubDAO.insertClubs(clubsForOnlyOurInsert)) {
			throw new IllegalArgumentException(
					"Произошла ошибка при вставке клубов");
		}

		if (!clubDAO.insertClubsWithConformity(clubsForInsert)) {
			throw new IllegalArgumentException(
					"Произошла ошибка при вставке клубов");
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
		for (Employee elem : employeesForDelete) {
			if (!employeeDAO.deleteEmployee(elem.getEmployeeId())) {
				throw new IllegalArgumentException(
						"Произошла ошибка при удалении сотрудника "
								+ elem.getNameForSchedule());
			}
		}

		if (!employeeDAO.updateEmployees(employeesForUpdate)) {
			throw new IllegalArgumentException(
					"Произошла ошибка при обновлении сотрудников");
		}

		if (!employeeDAO.insertEmployees(employeesForOnlyOurInsert)) {
			throw new IllegalArgumentException(
					"Произошла ошибка при вставке сотрудников");
		}

		if (!employeeDAO.insertEmployeesWithConformity(employeesForInsert)) {
			throw new IllegalArgumentException(
					"Произошла ошибка при вставке сотрудников");
		}

		if (!employeeDAO.setRolesForEmployees(roleForInsert)) {
			throw new IllegalArgumentException(
					"Произошла ошибка при установки ролей сотрудникам");
		}

		if (!employeeDAO.deleteRolesForEmployees(roleForDelete)) {
			throw new IllegalArgumentException(
					"Произошла ошибка при удалении ролей сотрудников");
		}

		if (!employeeDAO
				.insertEmployeesWithConformityAndRoles(roleForInsertNew))
			throw new IllegalArgumentException(
					"Произошла ошибка при установки ролей сотрудникам");

		if (!employeeDAO
				.insertEmployeesAndRoles(roleForInsertWithoutConformity))
			throw new IllegalArgumentException(
					"Произошла ошибка при установки ролей сотрудникам");
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
		if (!categoryDAO.deleteCategory(categoriesForDelete))
			throw new IllegalArgumentException(
					"Произошла ошибка при удалении категории");

		if (!categoryDAO.insertCategory(categoriesForInsert))
			throw new IllegalArgumentException(
					"Произошла ошибка при создании категории");

		for (Category c : categories) {
			if (employeeInCategoriesForDelete.containsKey(c.getCategoryId())) {
				if (!categoryDAO.deleteEmployees(c.getCategoryId(),
						employeeInCategoriesForDelete.get(c.getCategoryId())))
					throw new IllegalArgumentException(
							"Произошла ошибка при удалении сотрудников в категорию "
									+ c.getTitle());
			}
			if (employeeInCategoriesForInsert.containsKey(c.getCategoryId())) {
				if (!categoryDAO.insertEmployees(c.getCategoryId(),
						employeeInCategoriesForInsert.get(c.getCategoryId())))
					throw new IllegalArgumentException(
							"Произошла ошибка при добавлении сотрудников в категорию "
									+ c.getTitle());
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
		for (Holiday h : holidaysForDelete)
			if (!holidayDAO.removeHoliday(h.getHolidayid()))
				throw new IllegalArgumentException(
						"Произошла ошибка при удалении выходного :"
								+ DateTimeFormat.getFormat("dd.MM.yyyy")
										.format(h.getDate()));
		if (!holidayDAO.insertHolidays(holidaysForInsert))
			throw new IllegalArgumentException(
					"Произошла ошибка при вставке выходного");
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
	public void setUser(User user) throws IllegalArgumentException {
		if (userDAO.containsUser(user.getLogin())) {
			throw new IllegalArgumentException("Такой логин уже существует");
		}
		if (!userDAO.insertUser(user))
			throw new IllegalArgumentException(
					"Произошла ошибка при создании пользователя");
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
	public void insertSchedule(Schedule schedule) {
		nonclosedScheduleCacheService.insertSchedule(schedule);
	}

	/*
	 * CreateScheduleService end.
	 */

	@Override
	public void setPreference(Preference pref) throws IllegalArgumentException {
		if (!preferenceDAO.updatePreference(pref.getWorkHoursInDay(),
				pref.getShiftsNumber()))
			throw new IllegalArgumentException(
					"Произошла ошибка при сохранении смены");

	}

	@Override
	public Employee getDataEmployee() throws IllegalArgumentException {

		HttpSession session = getThreadLocalRequest().getSession();
		try {
			User user = (User) session.getAttribute(AppConstants.USER);
			return employeeDAO.findEmployee(user.getEmployeeId());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void setDataEmployee(Employee emp) throws IllegalArgumentException {
		Collection<Employee> emps = new ArrayList<Employee>();
		emps.add(emp);
		if (!employeeDAO.updateEmployees(emps))
			throw new IllegalArgumentException(
					"Не удалось обновить личные данные");

	}

	@Override
	public void setPass(String oldPass, String newPass)
			throws IllegalArgumentException {
		HttpSession session = getThreadLocalRequest().getSession();
		User oldUser = (User) session.getAttribute(AppConstants.USER);
		if (!Hashing.salt(oldPass, oldUser.getLogin()).equals(
				oldUser.getPassword()))
			throw new IllegalArgumentException(Hashing.salt(oldPass,
					oldUser.getLogin())
					+ " - "
					+ oldUser.getPassword()
					+ " Введен неверный старый пароль.");
		oldUser.setPassword(newPass);
		throw new IllegalArgumentException("Неудалось изменить пароль.");
	}

	@Override
	public void setPreference(Employee emp) throws IllegalArgumentException {
		Employee e = getDataEmployee();
		e.setMinAndMaxDays(emp.getMin(), emp.getMaxDays());
		if (!employeeDAO.updateEmployeePrefs(e))
			throw new IllegalArgumentException(
					"Произошла ошибка при сохранении предпочтений.");
	}

	@Override
	public String getUser() throws IllegalArgumentException {
		if (log.isDebugEnabled()) {
			log.debug("getEmployee method starts");
		}
		HttpServletRequest request = getThreadLocalRequest();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(AppConstants.USER);
		long employeeId = user.getEmployeeId();
		Employee employee = employeeDAO.findEmployee(employeeId);
		return employee.getFirstName() + " " + employee.getLastName();
	}

	public Schedule getCurrentSchedule(java.sql.Date date) {
		return nonclosedScheduleCacheService.getCurrentSchedule();
	}

	@Override
	public long getNearestPeriodId() throws IllegalArgumentException {
		java.sql.Date dateTime = new java.sql.Date(System.currentTimeMillis());
		Period period = scheduleDAO.getPeriod(dateTime);
		java.sql.Date date = (java.sql.Date) period.getEndDate();
		CalendarUtil.addDaysToDate(date, 1);
		Period newPeriod = null;
		int count = 30;
		while (newPeriod == null || count < 30) {
			newPeriod = scheduleDAO.getPeriod(date);
			CalendarUtil.addDaysToDate(date, 1);
			count++;
		}
		if (newPeriod != null) {
			return newPeriod.getPeriodId();
		} else {
			return -1;
		}

	}

	private Set<Employee> getInvolvedInDate(List<ClubDaySchedule> daySchedules) {
		Set<Employee> clubDayEmps = new HashSet<Employee>();
		for (ClubDaySchedule c : daySchedules) {
			clubDayEmps.addAll(c.getEmployees());
		}
		return clubDayEmps;
	}

	/**
	 * @param club
	 * @param emps
	 * @return list of preferred employees for club
	 */
	private List<Employee> getPreferredEmps(List<Employee> emps, Club club,
			List<ClubPref> clubPrefs) {
		List<Employee> re = new ArrayList<Employee>();
		for (ClubPref cp : clubPrefs) {
			for (Employee e : emps)
				if (cp.getEmployeeId() == e.getEmployeeId()) {
					re.add(e);
					break;
				}
		}
		return re;
	}

	private void sortByPriority(List<Employee> toSort,
			final List<Employee> prefered) {

		Comparator<Employee> comparator = new Comparator<Employee>() {
			@Override
			public int compare(Employee o1, Employee o2) {
				final boolean in1 = prefered.contains(o1);
				final boolean in2 = prefered.contains(o2);
				if ((in1 && in2) || (!in1 && !in2)) {
					return Integer.compare(
							o1.getMaxDays() - o1.getAssignment(),
							o2.getMaxDays() - o2.getAssignment());
				}
				return Boolean.compare(in1, in2);
			}
		};
		Collections.sort(toSort, comparator);
		System.out.println(toSort);
	}

	@Override
	public Schedule generate(Schedule s) throws IllegalArgumentException {
		if (s.getStatus() != Schedule.Status.DRAFT)
			return s;
		s.recountAssignments();
		System.out.println("-- Shedule --\n" + s);

		// get all Employees

		ArrayList<Employee> allEmps = (ArrayList<Employee>) employeeDAO
				.getAllEmployee();
		if (allEmps == null)
			return s;

		Set<Employee> involvedEmps = new HashSet<Employee>();

		// By date
		Set<java.sql.Date> dates = s.getDayScheduleMap().keySet();
		Iterator<java.sql.Date> dIter = dates.iterator();
		while (dIter.hasNext()) {
			List<ClubDaySchedule> daySchedules = s.getDayScheduleMap().get(
					dIter.next());
			involvedEmps = getInvolvedInDate(daySchedules);
			System.out.println("-- InvolvedEmps --\n" + involvedEmps);

			// By club
			ListIterator<ClubDaySchedule> cdsIter = daySchedules.listIterator();
			while (cdsIter.hasNext()) {
				// get next schedule of club at this date
				ClubDaySchedule clubDaySchedule = cdsIter.next();
				System.out.println("-- ClubDaySchedule --\n" + clubDaySchedule);
				System.out.println("-- Club --\n" + clubDaySchedule.getClub());

				// get free Employees
				@SuppressWarnings("unchecked")
				ArrayList<Employee> freeEmps = (ArrayList<Employee>) allEmps
						.clone();
				freeEmps.removeAll(involvedEmps);
				System.out.println("-- FreeEmps --\n" + freeEmps);
				if (clubDaySchedule.isFull())
					continue;
				sortByPriority(
						freeEmps,
						getPreferredEmps(freeEmps, clubDaySchedule.getClub(),
								s.getClubPrefs()));
				System.out.println("-- FreeEmps sorted --\n" + freeEmps);

				// if shifts in date not full and not enough free employees
				if (!clubDaySchedule.assignEmployeesToShifts(freeEmps)
						&& freeEmps.isEmpty())
					return s;
				System.out.println("-- FreeEmps sorted --\n" + freeEmps);
			}
		}

		return s;
	}

}
