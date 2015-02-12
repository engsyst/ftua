package ua.nure.ostpc.malibu.shedule.listener;

import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.CategoryDAO;
import ua.nure.ostpc.malibu.shedule.dao.ClubDAO;
import ua.nure.ostpc.malibu.shedule.dao.ClubPrefDAO;
import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.HolidayDAO;
import ua.nure.ostpc.malibu.shedule.dao.PreferenceDAO;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.dao.ShiftDAO;
import ua.nure.ostpc.malibu.shedule.dao.UserDAO;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.service.MailService;
import ua.nure.ostpc.malibu.shedule.service.NonclosedScheduleCacheService;
import ua.nure.ostpc.malibu.shedule.service.ScheduleEditEventService;

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
		setShiftDAOAttribute(servletContext);
		setPreferenceDAOAttribute(servletContext);
		setCategoryDAOAttribute(servletContext);
		setClubPrefDAOAttribute(servletContext);
		setNonclosedScheduleCacheService(servletContext);
//		setMailServiceAttribute(servletContext);
		setScheduleEditEventServiceAttribute(servletContext);
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

	private void setShiftDAOAttribute(ServletContext servletContext) {
		ShiftDAO shiftDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getShiftDAO();
		servletContext.setAttribute(AppConstants.SHIFT_DAO, shiftDAO);
		log.debug("ShiftDAO was created");
	}

	private void setPreferenceDAOAttribute(ServletContext servletContext) {
		PreferenceDAO preferenceDAO = DAOFactory
				.getDAOFactory(DAOFactory.MSSQL).getPreferenceDAO();
		servletContext.setAttribute(AppConstants.PREFERENCE_DAO, preferenceDAO);
		log.debug("PreferenceDAO was created");
	}

	private void setCategoryDAOAttribute(ServletContext servletContext) {
		CategoryDAO categoryDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getCategoryDAO();
		servletContext.setAttribute(AppConstants.CATEGORY_DAO, categoryDAO);
		log.debug("CategoryDAO was created");
	}

	private void setClubPrefDAOAttribute(ServletContext servletContext) {
		ClubPrefDAO clubPrefDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getClubPrefDAO();
		servletContext.setAttribute(AppConstants.CLUB_PREF_DAO, clubPrefDAO);
		log.debug("ClubPrefDAO was created");
	}

	private void setNonclosedScheduleCacheService(ServletContext servletContext) {
		ScheduleDAO scheduleDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getScheduleDAO();
		ShiftDAO shiftDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getShiftDAO();
		Set<Schedule> scheduleSet = scheduleDAO.getNotClosedSchedules();
		if (scheduleSet == null) {
			scheduleSet = new TreeSet<Schedule>();
		}
		NonclosedScheduleCacheService nonclosedScheduleCacheService = NonclosedScheduleCacheService
				.newInstance(scheduleSet, scheduleDAO, shiftDAO);
		servletContext.setAttribute(
				AppConstants.NONCLOSED_SCHEDULE_CACHE_SERVICE,
				nonclosedScheduleCacheService);
		log.debug("Nonclosed schedule cache service was created");
	}

//	private void setMailServiceAttribute(ServletContext servletContext) {
//		MailService mailService = new MailService();
//		servletContext.setAttribute(AppConstants.MAIL_SERVICE, mailService);
//		log.debug("Mail service created");
//	}

	private void setScheduleEditEventServiceAttribute(
			ServletContext servletContext) {
		ScheduleEditEventService scheduleEditEventService = new ScheduleEditEventService();
		servletContext.setAttribute(AppConstants.SCHEDULE_EDIT_EVENT_SERVICE,
				scheduleEditEventService);
		log.debug("Schedule edit event service created");
	}
}
