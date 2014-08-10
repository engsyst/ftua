package ua.nure.ostpc.malibu.shedule.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.Path;
import ua.nure.ostpc.malibu.shedule.client.CreateScheduleService;
import ua.nure.ostpc.malibu.shedule.dao.ClubDAO;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CreateSchedueServiceImpl extends RemoteServiceServlet implements
		CreateScheduleService {
	private ScheduleDAO scheduleDAO;
	private ClubDAO clubDAO;
	private EmployeeDAO employeeDAO;
	private static final Logger log = Logger
			.getLogger(CreateSchedueServiceImpl.class);

	public CreateSchedueServiceImpl() {
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
		clubDAO = (ClubDAO) servletContext.getAttribute(AppConstants.CLUB_DAO);
		employeeDAO = (EmployeeDAO) servletContext
				.getAttribute(AppConstants.EMPLOYEE_DAO);
		if (scheduleDAO == null) {
			log.error("ScheduleDAO attribute is not exists.");
			throw new IllegalStateException(
					"ScheduleDAO attribute is not exists.");
		}
		if (clubDAO == null) {
			log.error("ClubDAO attribute is not exists.");
			throw new IllegalStateException("ClubDAO attribute is not exists.");
		}
		if (employeeDAO == null) {
			log.error("EmployeeDAO attribute is not exists.");
			throw new IllegalStateException(
					"EmployeeDAO attribute is not exists.");
		}
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (log.isDebugEnabled()) {
			log.debug("GET method starts");
		}
		RequestDispatcher dispatcher = request
				.getRequestDispatcher(Path.PAGE__CREATE_SCHEDULE);
		dispatcher.forward(request, response);
		if (log.isDebugEnabled()) {
			log.debug("Response was sent");
		}
	}

	@Override
	public Date getStartDate() throws IllegalArgumentException {
		Date maxEndDate = scheduleDAO.getMaxEndDate();
		if (maxEndDate == null) {
			maxEndDate = new Date();
		}
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(maxEndDate);
		calendar.add(Calendar.DATE, 1);
		Date startDate = calendar.getTime();
		return startDate;
	}

	@Override
	public List<Club> getDependentClubs() throws IllegalArgumentException {
		List<Club> dependentClubs = new ArrayList<Club>();
		List<Club> clubCollection = clubDAO.getDependentClubs();
		if (clubCollection != null) {
			dependentClubs.addAll(clubDAO.getDependentClubs());
		}
		return dependentClubs;
	}

	@Override
	public Map<Long, List<Employee>> getEmployeesByClubsId(List<Long> clubsId)
			throws IllegalArgumentException {
		Map<Long, List<Employee>> employeesByClubs = new HashMap<Long, List<Employee>>();
		if (clubsId != null) {
			for (Long clubId : clubsId) {
				Collection<Employee> employeeCollection = employeeDAO
						.findEmployeesByClubId(clubId);
				List<Employee> employeesInClub = new ArrayList<Employee>();
				if (employeeCollection != null) {
					employeesInClub.addAll(employeeCollection);
				}
				employeesByClubs.put(clubId, employeesInClub);
			}
		}
		return employeesByClubs;
	}
}
