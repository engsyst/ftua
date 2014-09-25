package ua.nure.ostpc.malibu.shedule.server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import ua.nure.ostpc.malibu.shedule.dao.CategoryDAO;
import ua.nure.ostpc.malibu.shedule.dao.ClubDAO;
import ua.nure.ostpc.malibu.shedule.dao.ClubPrefDAO;
import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.HolidayDAO;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.dao.UserDAO;
import ua.nure.ostpc.malibu.shedule.dao.mssql.MSsqlClubDAO;
import ua.nure.ostpc.malibu.shedule.dao.mssql.MSsqlClubPrefDAO;
import ua.nure.ostpc.malibu.shedule.entity.AssignmentInfo;
import ua.nure.ostpc.malibu.shedule.entity.Category;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubPref;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Holiday;
import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.Role;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.service.NonclosedScheduleCacheService;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ScheduleManagerServiceImpl extends RemoteServiceServlet implements
		ScheduleManagerService, ScheduleDraftService, StartSettingService {
	private ScheduleDAO scheduleDAO;
	private NonclosedScheduleCacheService nonclosedScheduleCacheService;
	private static final Logger log = Logger
			.getLogger(ScheduleManagerServiceImpl.class);
	
	
	private CategoryDAO categoryDAO;
	private HolidayDAO holidayDAO;
	private UserDAO userDAO;
	
	private static final Logger logg = Logger.getLogger(StartSettingServiceImpl.class);
	private EmployeeDAO employeeDAO;
	private ClubDAO clubDAO;
	private ClubPrefDAO clubprefDAO;
	
	private static final Logger logger = Logger
			.getLogger(ScheduleDraftServiceImpl.class);
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
		if (employeeDAO == null) {
			logger.error("EmployeeDAO attribute is not exists.");
			throw new IllegalStateException(
					"EmployeeDAO attribute is not exists.");
		}
		clubDAO = (ClubDAO) servletContext.getAttribute(AppConstants.CLUB_DAO);
		categoryDAO = (CategoryDAO) servletContext.getAttribute(AppConstants.CATEGORY_DAO);
		holidayDAO = (HolidayDAO) servletContext.getAttribute(AppConstants.HOLIDAY_DAO);
		userDAO = (UserDAO) servletContext.getAttribute(AppConstants.USER_DAO);
		
		if (clubDAO == null) {
			logg.error("ClubDAO attribute is not exists.");
			throw new IllegalStateException("ClubDAO attribute is not exists.");
		}
		else if (employeeDAO == null) {
			logg.error("EmployeeDAO attribute is not exists.");
			throw new IllegalStateException("EmployeeDAO attribute is not exists.");
		}
		else if (categoryDAO == null) {
			logg.error("CategoryDAO attribute is not exists.");
			throw new IllegalStateException("CategoryDAO attribute is not exists.");
		}
		else if (userDAO == null) {
			logg.error("UserDAO attribute is not exists.");
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
		if (logger.isDebugEnabled()) {
			logger.debug("getEmployee method starts");
		}
		HttpServletRequest request = getThreadLocalRequest();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(AppConstants.USER);
		long employeeId = user.getEmployeeId();
		Employee employee = null;
		try {
			employee = employeeDAO.findEmployee(employeeId);
		} catch (SQLException e) {
			logger.error("Can not get employee!", e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Response was sent");
		}
		return employee;
	}

	@Override
	public Collection<Club> getClubes() throws IllegalArgumentException {
		clubDAO = (MSsqlClubDAO) DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getClubDAO();
		return clubDAO.getDependentClubs();
	}
	
	public List<ClubPref> getClubPref(long periodId) {
		clubprefDAO = (MSsqlClubPrefDAO) DAOFactory.getDAOFactory(
				DAOFactory.MSSQL).getClubPrefDAO();
		return clubprefDAO.getClubPrefsByPeriodId(periodId);
	}
	@Override
	public Map<Club, List<Employee>> getEmpToClub(long periodId) {
		clubDAO = (MSsqlClubDAO) DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getClubDAO();
		employeeDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getEmployeeDAO();

		Set<Club> clubList = new HashSet<Club>();
		Map<Club, List<Employee>> empToClub = new HashMap<Club, List<Employee>>();
		List<ClubPref> clubPrefs = getClubPref(periodId);
		Iterator<ClubPref> iter = clubPrefs.iterator();

		while (iter.hasNext()) {
			ClubPref clpr = iter.next();
			clubList.add(clubDAO.findClubById(clpr.getClubId()));
		}

		for (Club club : clubList) {
			Iterator<ClubPref> iterator = clubPrefs.iterator();
			List<Employee> empList = new ArrayList<Employee>();
			while (iterator.hasNext()) {
				ClubPref clpr = iterator.next();
				if (club.getClubId() == clpr.getClubId()) {
					try {
						empList.add(employeeDAO.findEmployee(clpr
								.getEmployeeId()));
					} catch (SQLException e) {
						e.printStackTrace();
					}
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
		if(malibuClubs == null)
			return new ArrayList<Club>();
		else
			return malibuClubs;
		
	}
	
	@Override
	public Collection<Employee> getEmployees() throws IllegalArgumentException {
		Collection<Employee> malibuEmployees = employeeDAO.getMalibuEmployees(); 
		if(malibuEmployees == null)
			return new ArrayList<Employee>();
		else
			return malibuEmployees;
	}

	@Override
	public Map<Long, Club> getDictionaryClub()
			throws IllegalArgumentException {
		Map<Long,Club> conformity = clubDAO.getConformity();
		if(conformity == null)
			return new HashMap<Long, Club>();
		else
			return conformity;
	}

	@Override
	public void setClubs(Collection<Club> clubsForInsert, Collection<Club> clubsForOnlyOurInsert,
			Collection<Club> clubsForUpdate, Collection<Club> clubsForDelete)
			throws IllegalArgumentException {
		for(Club elem : clubsForDelete){
			if(!clubDAO.removeClub(elem.getClubId())){
				throw new IllegalArgumentException("Произошла ошибка при удалении клуба " + elem.getTitle());
			}
		}
		
		for(Club elem : clubsForUpdate){
			if(!clubDAO.updateClub(elem)){
				throw new IllegalArgumentException("Произошла ошибка при обновлении клуба " + elem.getTitle());
			}
		}
		
		if(!clubDAO.insertClubs(clubsForOnlyOurInsert)){
			throw new IllegalArgumentException("Произошла ошибка при вставке клубов");
		}
		
		if(!clubDAO.insertClubsWithConformity(clubsForInsert)){
			throw new IllegalArgumentException("Произошла ошибка при вставке клубов");
		}
	}

	@Override
	public Collection<Club> getOnlyOurClubs() throws IllegalArgumentException {
		Collection<Club> ourClub = clubDAO.getOnlyOurClub();
		if(ourClub == null)
			return new ArrayList<Club>();
		else
			return ourClub;
	}

	@Override
	public Collection<Employee> getOnlyOurEmployees()
			throws IllegalArgumentException {
		Collection<Employee> ourEmployee = employeeDAO.getOnlyOurEmployees();
		if(ourEmployee == null)
			return new ArrayList<Employee>();
		else
			return ourEmployee;
	}

	@Override
	public Map<Long, Employee> getDictionaryEmployee()
			throws IllegalArgumentException {
		Map<Long, Employee> conformity = employeeDAO.getConformity();
		if(conformity == null)
			return new HashMap<Long, Employee>();
		else
			return conformity;
	}

	@Override
	public Map<Long, Collection<Boolean>> getRoleEmployee()
			throws IllegalArgumentException {
		Map<Long, Collection<Boolean>> roles = employeeDAO.getRolesForEmployee();
		if(roles == null)
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
			Map<Integer,Collection<Employee>> roleForInsertWithoutConformity)
			throws IllegalArgumentException {
		for(Employee elem : employeesForDelete){
			if(!employeeDAO.deleteEmployee(elem.getEmployeeId())){
				throw new IllegalArgumentException("Произошла ошибка при удалении сотрудника " + elem.getNameForSchedule());
			}
		}
		
		if(!employeeDAO.updateEmployees(employeesForUpdate)){
			throw new IllegalArgumentException("Произошла ошибка при обновлении сотрудников");
		}
		
		if(!employeeDAO.insertEmployees(employeesForOnlyOurInsert)){
			throw new IllegalArgumentException("Произошла ошибка при вставке сотрудников");
		}
		
		if(!employeeDAO.insertEmployeesWithConformity(employeesForInsert)){
			throw new IllegalArgumentException("Произошла ошибка при вставке сотрудников");
		}
		
		if(!employeeDAO.setRolesForEmployees(roleForInsert)){
			throw new IllegalArgumentException("Произошла ошибка при установки ролей сотрудникам");
		}
		
		if(!employeeDAO.deleteRolesForEmployees(roleForDelete)){
			throw new IllegalArgumentException("Произошла ошибка при удалении ролей сотрудников");
		}
		
		if(!employeeDAO.insertEmployeesWithConformityAndRoles(roleForInsertNew))
			throw new IllegalArgumentException("Произошла ошибка при установки ролей сотрудникам");
		
		if(!employeeDAO.insertEmployeesAndRoles(roleForInsertWithoutConformity))
			throw new IllegalArgumentException("Произошла ошибка при установки ролей сотрудникам");
	}

	@Override
	public Collection<Employee> getAllEmploee() throws IllegalArgumentException {
		Collection<Employee> employees = employeeDAO.getAllEmployee(); 
		if(employees == null)
			return new ArrayList<Employee>();
		else
			return employees;
	}

	@Override
	public Collection<Category> getCategories() throws IllegalArgumentException {
		Collection<Category> categories = categoryDAO.getCategoriesWithEmployees();
		if(categories == null)
			return new ArrayList<Category>();
		else
			return categories;
	}

	@Override
	public Map<Long, Collection<Employee>> getCategoriesDictionary()
			throws IllegalArgumentException {
		Collection<Category> categories = getCategories();
		HashMap<Long, Collection<Employee>> dictionaries = new HashMap<Long, Collection<Employee>>();
		for(Category c : categories){
			dictionaries.put(c.getCategoryId(), employeeDAO.findEmployees(c.getEmployeeIdList()));
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
		if(!categoryDAO.deleteCategory(categoriesForDelete))
			throw new IllegalArgumentException("Произошла ошибка при удалении категории");
		
		if(!categoryDAO.insertCategory(categoriesForInsert))
			throw new IllegalArgumentException("Произошла ошибка при создании категории");
		
		for(Category c : categories){
			if(employeeInCategoriesForDelete.containsKey(c.getCategoryId())){
				if(!categoryDAO.deleteEmployees(c.getCategoryId(), employeeInCategoriesForDelete.get(c.getCategoryId())))
					throw new IllegalArgumentException("Произошла ошибка при удалении сотрудников в категорию "
							+ c.getTitle());
			}
			if(employeeInCategoriesForInsert.containsKey(c.getCategoryId())){
				if(!categoryDAO.insertEmployees(c.getCategoryId(), employeeInCategoriesForInsert.get(c.getCategoryId())))
					throw new IllegalArgumentException("Произошла ошибка при добавлении сотрудников в категорию "
							+ c.getTitle());
			}
		}
	}

	@Override
	public Collection<Holiday> getHolidays() throws IllegalArgumentException {
		Collection<Holiday> holidays = holidayDAO.getHolidays();
		if(holidays == null)
			return new ArrayList<Holiday>();
		else
			return holidays;
	}

	@Override
	public void setHolidays(Collection<Holiday> holidaysForDelete,
			Collection<Holiday> holidaysForInsert)
			throws IllegalArgumentException {
		for(Holiday h : holidaysForDelete)
			if(!holidayDAO.removeHoliday(h.getHolidayid()))
				throw new IllegalArgumentException("Произошла ошибка при удалении выходного :"+ 
			DateTimeFormat.getFormat("dd.MM.yyyy").format(h.getDate()));
		if(!holidayDAO.insertHolidays(holidaysForInsert))
			throw new IllegalArgumentException("Произошла ошибка при вставке выходного");
	}

	@Override
	public Collection<Long> getEmployeeWithoutUser()
			throws IllegalArgumentException {
		Collection<Long> employees = userDAO.getEmployeeIdsWitoutUser();
		if(employees == null)
			return new ArrayList<Long>();
		else
			return employees;
	}

	@Override
	public void setUser(User user) throws IllegalArgumentException {
		if(userDAO.containsUser(user.getLogin())){
			throw new IllegalArgumentException("Такой логин уже существует");
		}
		if(!userDAO.insertUser(user))
			throw new IllegalArgumentException("Произошла ошибка при создании пользователя");
	}

	
}
