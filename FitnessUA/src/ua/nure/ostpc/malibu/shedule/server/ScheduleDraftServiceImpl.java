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
import ua.nure.ostpc.malibu.shedule.dao.ClubDAO;
import ua.nure.ostpc.malibu.shedule.dao.ClubPrefDAO;
import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.dao.mssql.MSsqlClubDAO;
import ua.nure.ostpc.malibu.shedule.dao.mssql.MSsqlClubPrefDAO;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubDaySchedule;
import ua.nure.ostpc.malibu.shedule.entity.ClubPref;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.InformationToSend;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Shift;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.service.NonclosedScheduleCacheService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ScheduleDraftServiceImpl extends RemoteServiceServlet implements
		ScheduleDraftService {
	private EmployeeDAO employeeDAO;
	private ClubDAO clubDAO;
	private ClubPrefDAO clubprefDAO;
	private ScheduleDAO scheduleDAO;
	private NonclosedScheduleCacheService nonclosedScheduleCacheService;
	private static final Logger log = Logger
			.getLogger(ScheduleDraftServiceImpl.class);

	public ScheduleDraftServiceImpl() {
		super();
		if (log.isDebugEnabled()) {
			log.debug("Servlet creates");
		}
	}

	@Override
	public void init() {
		ServletContext servletContext = getServletContext();
		employeeDAO = (EmployeeDAO) servletContext
				.getAttribute(AppConstants.EMPLOYEE_DAO);
		scheduleDAO = (ScheduleDAO) servletContext
				.getAttribute(AppConstants.SCHEDULE_DAO);
		nonclosedScheduleCacheService = (NonclosedScheduleCacheService) servletContext
				.getAttribute(AppConstants.NONCLOSED_SCHEDULE_CACHE_SERVICE);
		if (employeeDAO == null) {
			log.error("EmployeeDAO attribute is not exists.");
			throw new IllegalStateException(
					"EmployeeDAO attribute is not exists.");
		}
		if (scheduleDAO == null) {
			log.error("ScheduleDAO attribute is not exists.");
			throw new IllegalStateException(
					"ScheduleDAO attribute is not exists.");
		}
		if (nonclosedScheduleCacheService == null) {
			log.error("NonclosedScheduleCacheService attribute is not exists.");
			throw new IllegalStateException(
					"NonclosedScheduleCacheService attribute is not exists.");
		}
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (log.isDebugEnabled()) {
			log.debug("GET method starts");
		}
		RequestDispatcher dispatcher = request
				.getRequestDispatcher(Path.PAGE__SCHEDULE_DRAFT);
		dispatcher.forward(request, response);
		if (log.isDebugEnabled()) {
			log.debug("Response was sent");
		}
	}

	@Override
	public Employee getEmployee() {
		if (log.isDebugEnabled()) {
			log.debug("getEmployee method starts");
		}
		HttpServletRequest request = getThreadLocalRequest();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(AppConstants.USER);
		long employeeId = user.getEmployeeId();
		Employee employee = null;
		try {
			employee = employeeDAO.findEmployee(employeeId);
		} catch (SQLException e) {
			log.error("Can not get employee!", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("Response was sent");
		}
		return employee;
	}

	public Collection<Club> getClubs() {
		clubDAO = (MSsqlClubDAO) DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getClubDAO();
		return clubDAO.getDependentClubs();

	}

	public List<ClubPref> getClubPref(long periodId) {
		clubprefDAO = (MSsqlClubPrefDAO) DAOFactory.getDAOFactory(
				DAOFactory.MSSQL).getClubPrefDAO();
		return clubprefDAO.getClubPrefsByPeriodId(periodId);
	}

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

	public Schedule getScheduleById(long periodId) {
		return nonclosedScheduleCacheService.getSchedule(periodId);
	}

	public synchronized Integer setObjectToSend(InformationToSend inform,
			Employee employee) {
		Schedule schedule = getScheduleById(inform.getPeriodId());
		Map<java.sql.Date, List<ClubDaySchedule>> clubDayScheduleList = schedule
				.getDayScheduleMap();
		List<ClubDaySchedule> list = clubDayScheduleList.get(inform.getDate());
		Iterator<ClubDaySchedule> iterator = list.iterator();
		while (iterator.hasNext()) {
			ClubDaySchedule clubdayschedule = iterator.next();
			List<Shift> shiftList = clubdayschedule.getShifts();
			Iterator<Shift> shiftIterator = shiftList.iterator();
			int count = 0;
			while (shiftIterator.hasNext()) {
				if (count == inform.getShiftId()) {
					Shift shift = shiftIterator.next();
					List<Employee> lst = shift.getEmployees();
					if (inform.isAdded()) {
						if (lst.size() == shift.getQuantityOfEmployees()) {
							return 0;
						} else if (lst.size() < shift.getQuantityOfEmployees()) {
							return 1;
						}
					} else {
						Iterator<Employee> itera = lst.iterator();
						while (itera.hasNext()) {
							Employee emp = itera.next();
							if (emp.getEmployeeId() == employee.getEmployeeId()) {
//								lst.remove(emp);
//								nonclosedScheduleCacheService
//										.updateSchedule(schedule);
							}
						}
						return 3;
					}
				} else {
					shiftIterator.next();
					count++;
				}
			}
		}
		return 2;
	}
}
