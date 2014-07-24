package ua.nure.ostpc.malibu.shedule.server;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.Path;

@SuppressWarnings("serial")
public class LogoutServiceImpl extends HttpServlet {
	private static final Logger log = Logger.getLogger(LogoutServiceImpl.class);

	public LogoutServiceImpl() {
		super();
		if (log.isDebugEnabled()) {
			log.debug("Servlet creates");
		}
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (log.isDebugEnabled()) {
			log.debug("GET method starts");
		}
		RequestDispatcher dispatcher = request
				.getRequestDispatcher(Path.PAGE__LOGIN);
		dispatcher.forward(request, response);
		if (log.isDebugEnabled()) {
			log.debug("Response was sent");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (log.isDebugEnabled()) {
			log.debug("Logout method starts");
		}
		HttpSession session = request.getSession();
		session.invalidate();
	}
}
