package ua.nure.ostpc.malibu.shedule.server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

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
import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.mssql.MSsqlClubDAO;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ScheduleDraftServiceImpl extends RemoteServiceServlet implements
		ScheduleDraftService {
	private EmployeeDAO employeeDAO;
	private ClubDAO clubDAO;
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
	public Collection<Club> getClubs()
	{
		clubDAO = (MSsqlClubDAO) DAOFactory.getDAOFactory(
				DAOFactory.MSSQL).getClubDAO();
		return clubDAO.getAllScheduleClubs();
		
	}
}
