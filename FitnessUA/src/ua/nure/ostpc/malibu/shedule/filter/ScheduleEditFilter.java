package ua.nure.ostpc.malibu.shedule.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.Path;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.service.NonclosedScheduleCacheService;
import ua.nure.ostpc.malibu.shedule.service.ScheduleEditEventService;

/**
 * Schedule edit filter.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class ScheduleEditFilter implements Filter {
	private ScheduleEditEventService scheduleEditEventService;
	private NonclosedScheduleCacheService nonclosedScheduleCacheService;

	private static final Logger log = Logger
			.getLogger(ScheduleEditFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (log.isDebugEnabled()) {
			log.debug("Filter initialization starts");
		}
		ServletContext servletContext = filterConfig.getServletContext();
		scheduleEditEventService = (ScheduleEditEventService) servletContext
				.getAttribute(AppConstants.SCHEDULE_EDIT_EVENT_SERVICE);
		nonclosedScheduleCacheService = (NonclosedScheduleCacheService) servletContext
				.getAttribute(AppConstants.NONCLOSED_SCHEDULE_CACHE_SERVICE);
		if (scheduleEditEventService == null) {
			log.error("ScheduleEditEventService attribute is not exists.");
			throw new IllegalStateException(
					"ScheduleEditEventService attribute is not exists.");
		}
		if (nonclosedScheduleCacheService == null) {
			log.error("NonclosedScheduleCacheService attribute is not exists.");
			throw new IllegalStateException(
					"NonclosedScheduleCacheService attribute is not exists.");
		}
		if (log.isDebugEnabled()) {
			log.debug("Filter initialization finished");
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		User user = (User) httpRequest.getSession().getAttribute(
				AppConstants.USER);
		try {
			long periodId = Long.parseLong(request
					.getParameter(AppConstants.PERIOD_ID));
			if (scheduleEditEventService.addEditEvent(periodId,
					user.getUserId())) {
				nonclosedScheduleCacheService.lockSchedule(periodId);
				chain.doFilter(httpRequest, httpResponse);
				return;
			} else {
				httpResponse.sendError(403);
				return;
			}
		} catch (NumberFormatException | NullPointerException e) {
			RequestDispatcher requestDispatcher = httpRequest
					.getRequestDispatcher(Path.PAGE__SCHEDULE_MANAGER);
			requestDispatcher.forward(httpRequest, httpResponse);
			return;
		}
	}

	@Override
	public void destroy() {
		log.debug("Filter destroying starts");
		log.debug("Filter destroying finished");
	}
}
