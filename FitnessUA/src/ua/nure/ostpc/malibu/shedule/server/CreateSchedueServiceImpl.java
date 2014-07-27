package ua.nure.ostpc.malibu.shedule.server;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.client.CreateScheduleService;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CreateSchedueServiceImpl extends RemoteServiceServlet implements
		CreateScheduleService {
	private ScheduleDAO scheduleDAO;
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
		if (scheduleDAO == null) {
			log.error("ScheduleDAO attribute is not exists.");
			throw new IllegalStateException(
					"ScheduleDAO attribute is not exists.");
		}
	}

	@Override
	public Date getStartDate() throws IllegalArgumentException {
		Date maxEndDate = scheduleDAO.readMaxEndDate();
		if (maxEndDate == null) {
			maxEndDate = new Date();
		}
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(maxEndDate);
		calendar.add(Calendar.DATE, 1);
		Date startDate = calendar.getTime();
		return startDate;
	}
}
