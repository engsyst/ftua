package ua.nure.ostpc.malibu.shedule.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Encoding filter.
 * 
 * @author Volodymyr_Semerkov
 */
public class EncodingFilter implements Filter {
	private static final Logger log = Logger.getLogger(EncodingFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.debug("Filter initialization starts");
		log.debug("Filter initialization finished");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		log.debug("Filter starts");
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpRequest.setCharacterEncoding("UTF-8");
		httpResponse.setCharacterEncoding("UTF-8");
		httpResponse.setHeader("Cache-Control", "no-cache, no-store");
		chain.doFilter(httpRequest, httpResponse);
		log.debug("Filter finished");
	}

	@Override
	public void destroy() {
		log.debug("Filter destroying starts");
		log.debug("Filter destroying finished");
	}
}
