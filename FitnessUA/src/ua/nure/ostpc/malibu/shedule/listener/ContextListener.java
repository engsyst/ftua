package ua.nure.ostpc.malibu.shedule.listener;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.CategoryDAO;
import ua.nure.ostpc.malibu.shedule.dao.ClubDAO;
import ua.nure.ostpc.malibu.shedule.dao.ClubPrefDAO;
import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.PreferenceDAO;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.dao.ShiftDAO;
import ua.nure.ostpc.malibu.shedule.dao.UserDAO;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.service.ExcelEmployeeService;
import ua.nure.ostpc.malibu.shedule.service.NonclosedScheduleCacheService;
import ua.nure.ostpc.malibu.shedule.service.ScheduleEditEventService;
import ua.nure.ostpc.malibu.shedule.service.ScheduleSetManager;

/**
 * Context listener.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class ContextListener implements ServletContextListener {
	private static final Logger log = Logger.getLogger(ContextListener.class);
	private NonclosedScheduleCacheService nonclosedScheduleCacheService;
	private ScheduledExecutorService scheduler;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("Servlet context initialization starts");
		}
		ServletContext servletContext = event.getServletContext();
		// setDbProperties(servletContext);
		setClubDAOAttribute(servletContext);
		setPreferenceDAOAttribute(servletContext);
		setCategoryDAOAttribute(servletContext);
		setClubPrefDAOAttribute(servletContext);

		UserDAO userDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getUserDAO();
		EmployeeDAO employeeDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getEmployeeDAO();
		ScheduleDAO scheduleDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getScheduleDAO();
		ShiftDAO shiftDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getShiftDAO();

		setUserDAOAttribute(servletContext, userDAO);
		setEmployeeDAOAttribute(servletContext, employeeDAO);
		setScheduleDAOAttribute(servletContext, scheduleDAO);
		setShiftDAOAttribute(servletContext, shiftDAO);
		setNonclosedScheduleCacheService(servletContext, scheduleDAO, shiftDAO);

		// setMailServiceAttribute(servletContext);
		setScheduleEditEventServiceAttribute(servletContext);
		setExcelEmployeeServiceAttribute(servletContext, employeeDAO, userDAO);
		if (nonclosedScheduleCacheService != null) {
			scheduler = Executors.newScheduledThreadPool(1);
			scheduler.scheduleAtFixedRate(new ScheduleSetManager(
					nonclosedScheduleCacheService), 0, 15, TimeUnit.MINUTES);
		}
		if (log.isDebugEnabled()) {
			log.debug("Servlet context initialization finished");
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		nonclosedScheduleCacheService = null;
		scheduler.shutdownNow();
		scheduler = null;
		if (log.isDebugEnabled()) {
			log.debug("Servlet context destruction starts");
			log.debug("Servlet context destruction finished");
		}
	}

	private void setUserDAOAttribute(ServletContext servletContext,
			UserDAO userDAO) {
		servletContext.setAttribute(AppConstants.USER_DAO, userDAO);
		log.debug("UserDAO was created");
	}

	private void setClubDAOAttribute(ServletContext servletContext) {
		ClubDAO clubDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getClubDAO();
		servletContext.setAttribute(AppConstants.CLUB_DAO, clubDAO);
		log.debug("ClubDAO was created");
	}

	private void setEmployeeDAOAttribute(ServletContext servletContext,
			EmployeeDAO employeeDAO) {
		servletContext.setAttribute(AppConstants.EMPLOYEE_DAO, employeeDAO);
		log.debug("EmployeeDAO was created");
	}

	private void setScheduleDAOAttribute(ServletContext servletContext,
			ScheduleDAO scheduleDAO) {
		servletContext.setAttribute(AppConstants.SCHEDULE_DAO, scheduleDAO);
		log.debug("ScheduleDAO was created");
	}

	private void setShiftDAOAttribute(ServletContext servletContext,
			ShiftDAO shiftDAO) {
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

	private void setNonclosedScheduleCacheService(
			ServletContext servletContext, ScheduleDAO scheduleDAO,
			ShiftDAO shiftDAO) {
		Set<Schedule> scheduleSet = scheduleDAO.getNotClosedSchedules();
		if (scheduleSet == null) {
			scheduleSet = new TreeSet<Schedule>();
		}
		nonclosedScheduleCacheService = NonclosedScheduleCacheService
				.newInstance(scheduleSet, scheduleDAO, shiftDAO);
		servletContext.setAttribute(
				AppConstants.NONCLOSED_SCHEDULE_CACHE_SERVICE,
				nonclosedScheduleCacheService);
		log.debug("Nonclosed schedule cache service was created");
	}

	private void setScheduleEditEventServiceAttribute(
			ServletContext servletContext) {
		ScheduleEditEventService scheduleEditEventService = new ScheduleEditEventService();
		servletContext.setAttribute(AppConstants.SCHEDULE_EDIT_EVENT_SERVICE,
				scheduleEditEventService);
		log.debug("Schedule edit event service was created");
	}

	private void setExcelEmployeeServiceAttribute(
			ServletContext servletContext, EmployeeDAO employeeDAO,
			UserDAO userDAO) {
		ExcelEmployeeService excelEmployeeService = new ExcelEmployeeService(
				employeeDAO, userDAO);
		servletContext.setAttribute(AppConstants.EXCEL_EMPLOYEE_SERVICE,
				excelEmployeeService);
		log.debug("Excel employee service was created");
	}
}
