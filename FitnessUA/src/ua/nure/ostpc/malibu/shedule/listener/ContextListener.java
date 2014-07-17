package ua.nure.ostpc.malibu.shedule.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.UserDAO;
import ua.nure.ostpc.malibu.shedule.dao.mssql.MSsqlUserDAO;
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
		UserDAO userDAO = new MSsqlUserDAO();
		servletContext.setAttribute(AppConstants.USER_DAO, userDAO);
		log.debug("UserDAO was created");
	}
}
