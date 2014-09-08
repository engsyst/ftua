package ua.nure.ostpc.malibu.shedule.listener;

import java.util.Date;
import java.util.Iterator;
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
import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.PreferenceDAO;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.dao.UserDAO;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.Schedule.Status;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.service.MailService;

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
		setPreferenceDAOAttribute(servletContext);
		setCategoryDAOAttribute(servletContext);
		setScheduleSet(servletContext);
		setMailServiceAttribute(servletContext);
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

	private void setScheduleSet(ServletContext servletContext) {
		ScheduleDAO scheduleDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getScheduleDAO();
		Set<Schedule> scheduleSet = scheduleDAO.getNotClosedSchedules();
		if (scheduleSet == null) {
			scheduleSet = new TreeSet<Schedule>();
		}
		servletContext.setAttribute(AppConstants.SCHEDULE_SET, scheduleSet);
		log.debug("schedule set was created");
		ScheduledExecutorService scheduler = Executors
				.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(new ScheduleSetManager(scheduleSet,
				scheduleDAO), 0, 1, TimeUnit.DAYS);
	}

	private void setMailServiceAttribute(ServletContext servletContext) {
		EmployeeDAO employeeDAO = DAOFactory.getDAOFactory(DAOFactory.MSSQL)
				.getEmployeeDAO();
		MailService mailService = new MailService(employeeDAO);
		servletContext.setAttribute(AppConstants.MAIL_SERVICE, mailService);
		log.debug("Mail service created");
	}

	private class ScheduleSetManager implements Runnable {
		private Set<Schedule> scheduleSet;
		private ScheduleDAO scheduleDAO;

		public ScheduleSetManager(Set<Schedule> scheduleSet,
				ScheduleDAO scheduleDAO) {
			this.scheduleSet = scheduleSet;
			this.scheduleDAO = scheduleDAO;
		}

		@Override
		public void run() {
			synchronized (scheduleSet) {
				Iterator<Schedule> it = scheduleSet.iterator();
				while (it.hasNext()) {
					Schedule schedule = it.next();
					if (schedule.getPeriod().getEndDate().before(new Date())
							&& !schedule.isLocked()) {
						it.remove();
						if (log.isDebugEnabled()) {
							log.debug("Schedule deleted from servlet context. "
									+ schedule);
						}
						schedule.setStatus(Status.CLOSED);
						scheduleDAO.updateSchedule(schedule);
						if (log.isDebugEnabled()) {
							log.debug("Schedule statud updated. " + schedule);
						}
					}
				}
			}
		}
	}
}
