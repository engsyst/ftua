package ua.nure.ostpc.malibu.shedule.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.ClubDAO;
import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.dao.UserDAO;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

/**
 * Context listener.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class ContextListener implements ServletContextListener {
	private static final Logger log = Logger.getLogger(ContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("Servlet context initialization starts");
		}
		ServletContext servletContext = event.getServletContext();
		setUserDAOAttribute(servletContext);
		setClubDAOAttribute(servletContext);
		setEmployeeDAOAttribute(servletContext);
		setScheduleDAOAttribute(servletContext);
		if (log.isDebugEnabled()) {
			log.debug("Servlet context initialization finished");
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (log.isDebugEnabled()) {
			log.debug("Servlet context destruction starts");
			log.debug("Servlet context destruction finished");
		}
	}

	private void setUserDAOAttribute(ServletContext servletContext) {
		UserDAO userDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getUserDAO();
		servletContext.setAttribute(AppConstants.USER_DAO, userDAO);
		log.debug("UserDAO was created");
	}

	private void setClubDAOAttribute(ServletContext servletContext) {
		ClubDAO clubDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getClubDAO();
		servletContext.setAttribute(AppConstants.CLUB_DAO, clubDAO);
		log.debug("ClubDAO was created");
	}

	private void setEmployeeDAOAttribute(ServletContext servletContext) {
		EmployeeDAO employeeDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getEmployeeDAO();
		servletContext.setAttribute(AppConstants.EMPLOYEE_DAO, employeeDAO);
		log.debug("EmployeeDAO was created");
	}

	private void setScheduleDAOAttribute(ServletContext servletContext) {
		ScheduleDAO scheduleDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getScheduleDAO();
		servletContext.setAttribute(AppConstants.SCHEDULE_DAO, scheduleDAO);
		log.debug("ScheduleDAO was created");
	}
}
