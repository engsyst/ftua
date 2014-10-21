package ua.nure.ostpc.malibu.shedule.server;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.Path;
import ua.nure.ostpc.malibu.shedule.client.LoginService;
import ua.nure.ostpc.malibu.shedule.dao.UserDAO;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.security.Hashing;
import ua.nure.ostpc.malibu.shedule.shared.FieldVerifier;
import ua.nure.ostpc.malibu.shedule.shared.LoginInfo;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class LoginServiceImpl extends RemoteServiceServlet implements
		LoginService {
	private UserDAO userDAO;
	private static final Logger log = Logger.getLogger(LoginServiceImpl.class);

	public LoginServiceImpl() {
		super();
		if (log.isDebugEnabled()) {
			log.debug("Servlet creates");
		}
	}

	@Override
	public void init() {
		ServletContext servletContext = getServletContext();
		userDAO = (UserDAO) servletContext.getAttribute(AppConstants.USER_DAO);
		if (userDAO == null) {
			log.error("UserDAO attribute is not exists.");
			throw new IllegalStateException("UserDAO attribute is not exists.");
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
	public LoginInfo login(String login, String password) {
		if (log.isDebugEnabled()) {
			log.debug("Login method starts");
		}
		LoginInfo loginInfo;
		boolean result = true;
		Map<String, String> errors = FieldVerifier.validateLoginData(login,
				password);
		result = errors.size() == 0;
		if (result) {
			User user = userDAO.getUser(login);
			password = Hashing.salt(password, user.getLogin());
			if (user != null && user.getPassword().equals(password)) {
				HttpServletRequest request = getThreadLocalRequest();
				HttpSession session = request.getSession();
				session.setAttribute(AppConstants.USER, user);
			} else {
				errors.put(AppConstants.LOGIN,
						"Указан неверный логин или пароль!");
				result = false;
			}
		}
		loginInfo = new LoginInfo(result, errors);
		if (log.isDebugEnabled()) {
			log.debug("Response was sent");
		}
		return loginInfo;
	}
}
