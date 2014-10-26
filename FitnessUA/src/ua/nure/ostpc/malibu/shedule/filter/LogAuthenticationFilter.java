package ua.nure.ostpc.malibu.shedule.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

/**
 * Log authentication filter.
 * 
 * @author Volodymyr_Semerkov
 */
public class LogAuthenticationFilter implements Filter {

	private static final Logger log = Logger
			.getLogger(LogAuthenticationFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (log.isDebugEnabled()) {
			log.debug("Filter initialization starts");
			log.debug("Filter initialization finished");
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		log.debug("Filter starts");
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession();
		User user = (User) session.getAttribute(AppConstants.USER);
		long employeeId = 0;
		if (user != null) {
			employeeId = user.getEmployeeId();
		}
		MDC.put(AppConstants.EMPLOYEE_ID, employeeId);
		chain.doFilter(request, response);
		MDC.remove(AppConstants.EMPLOYEE_ID);
		log.debug("Filter finished");
	}

	@Override
	public void destroy() {
		log.debug("Filter destroying starts");
		log.debug("Filter destroying finished");
	}
}
