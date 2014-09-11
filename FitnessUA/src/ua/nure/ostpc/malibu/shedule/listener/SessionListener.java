package ua.nure.ostpc.malibu.shedule.listener;

import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.entity.Schedule;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.service.ScheduleEditEventService;

/**
 * Session listener.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class SessionListener implements HttpSessionListener {
	private static final Logger log = Logger.getLogger(SessionListener.class);

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("Session created");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		ServletContext servletContext = session.getServletContext();
		ScheduleEditEventService scheduleEditEventService = (ScheduleEditEventService) servletContext
				.getAttribute(AppConstants.SCHEDULE_EDIT_EVENT_SERVICE);
		Set<Schedule> scheduleSet = (Set<Schedule>) servletContext
				.getAttribute(AppConstants.SCHEDULE_SET);
		User user = (User) session.getAttribute(AppConstants.USER);
		List<Long> periodIdList = scheduleEditEventService
				.removeEditEventsForUser(user.getUserId());
		if (periodIdList != null && !periodIdList.isEmpty())
			synchronized (scheduleSet) {
				for (Schedule schedule : scheduleSet) {
					for (Long periodId : periodIdList) {
						if (schedule.getPeriod().getPeriodId() == periodId) {
							schedule.setLocked(false);
						}
					}
				}
			}
		if (log.isDebugEnabled()) {
			log.debug("Session destroyed");
		}
	}
}
