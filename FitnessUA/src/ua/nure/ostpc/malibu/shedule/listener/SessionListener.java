package ua.nure.ostpc.malibu.shedule.listener;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ua.nure.ostpc.malibu.shedule.entity.Period;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.service.NonclosedScheduleCacheService;
import ua.nure.ostpc.malibu.shedule.service.ScheduleEditEventService;

/**
 * Session listener.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class SessionListener implements HttpSessionListener {
	private static final Logger log = Logger.getLogger(SessionListener.class);

	private static DateFormat dateFormat = DateFormat.getDateInstance(
			DateFormat.LONG, new Locale("ru", "RU"));

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("Session created");
		}
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		ServletContext servletContext = session.getServletContext();
		ScheduleEditEventService scheduleEditEventService = (ScheduleEditEventService) servletContext
				.getAttribute(AppConstants.SCHEDULE_EDIT_EVENT_SERVICE);
		NonclosedScheduleCacheService nonclosedScheduleCacheService = (NonclosedScheduleCacheService) servletContext
				.getAttribute(AppConstants.NONCLOSED_SCHEDULE_CACHE_SERVICE);
		User user = (User) session.getAttribute(AppConstants.USER);
		if (user != null) {
			List<Long> periodIdList = scheduleEditEventService
					.removeEditEventsForUser(user.getUserId());
			if (periodIdList != null) {
				for (Long periodId : periodIdList) {
					nonclosedScheduleCacheService.unlockSchedule(periodId);
					if (log.isInfoEnabled()) {
						Period period = nonclosedScheduleCacheService
								.getSchedule(periodId).getPeriod();
						StringBuilder sb = new StringBuilder();
						sb.append("Разблокировал график работы: ");
						sb.append("(periodId=");
						sb.append(period.getPeriodId());
						sb.append(") ");
						sb.append("с ");
						sb.append(dateFormat.format(period.getStartDate()));
						sb.append(" до ");
						sb.append(dateFormat.format(period.getEndDate()));
						MDC.put(AppConstants.EMPLOYEE_ID, user.getEmployeeId());
						log.info("UserId: " + user.getUserId() + " Логин: "
								+ user.getLogin() + " Действие: "
								+ sb.toString());
						MDC.remove(AppConstants.EMPLOYEE_ID);
					}
				}
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Session destroyed");
		}
	}
}
