package ua.nure.ostpc.malibu.shedule.security.path;

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
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.Path;
import ua.nure.ostpc.malibu.shedule.entity.Right;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

/**
 * Security filter.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class SecurityFilter implements Filter {
	private SecurityManager securityManager;
	private static final Logger log = Logger.getLogger(SecurityFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (log.isDebugEnabled()) {
			log.debug("Filter initialization starts");
		}
		ServletContext servletContext = filterConfig.getServletContext();
		String xmlFileName = servletContext
				.getInitParameter(AppConstants.SECURITY_XML);
		boolean isValidate;
		try {
			isValidate = StAXParser.validate(xmlFileName);
		} catch (XMLStreamException e) {
			log.error("XML file validation error");
			throw new IllegalStateException("XML file validation error");
		}
		if (isValidate) {
			try {
				securityManager = new SecurityManager(
						StAXParser.parse(xmlFileName));
			} catch (XMLStreamException e) {
				log.error("XML file parsing error");
				throw new IllegalStateException("XML file parsing error");
			}
		} else {
			log.error("XML file is not valid");
			throw new IllegalStateException("XML file is not valid");
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
		Right right = user != null ? user.getRole().getRight() : Right.VISITOR;
		String pagePath = httpRequest.getServletPath();
		if (securityManager.accept(pagePath, right)) {
			chain.doFilter(httpRequest, httpResponse);
			return;
		} else {
			if (right == Right.VISITOR) {
				RequestDispatcher requestDispatcher = httpRequest
						.getRequestDispatcher(Path.PAGE__LOGIN);
				requestDispatcher.forward(httpRequest, httpResponse);
				return;
			} else {
				httpResponse.sendError(403);
				return;
			}
		}
	}

	@Override
	public void destroy() {
	}
}
