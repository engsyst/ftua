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
import org.apache.log4j.MDC;

import ua.nure.ostpc.malibu.shedule.Path;
import ua.nure.ostpc.malibu.shedule.client.LoginService;
import ua.nure.ostpc.malibu.shedule.dao.UserDAO;
import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.security.Hashing;
import ua.nure.ostpc.malibu.shedule.shared.LoginInfo;
import ua.nure.ostpc.malibu.shedule.validator.ServerSideValidator;
import ua.nure.ostpc.malibu.shedule.validator.Validator;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class LoginServiceImpl extends RemoteServiceServlet implements
		LoginService {
	private static final Logger log = Logger.getLogger(LoginServiceImpl.class);

	private UserDAO userDAO;
	private Validator validator;

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
		validator = new ServerSideValidator();
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
		Map<String, String> errors = validator.validateLoginData(login,
				password);
		result = errors.size() == 0;
		if (result) {
			User user = userDAO.getUser(login);
			if (user != null
					&& user.getPassword().equals(
							Hashing.salt(password, user.getLogin()))) {
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
		if (result && log.isInfoEnabled()) {
			HttpServletRequest request = getThreadLocalRequest();
			HttpSession session = request.getSession();
			User user = (User) session.getAttribute(AppConstants.USER);
			MDC.put(AppConstants.EMPLOYEE_ID, user.getEmployeeId());
			log.info("UserId: " + user.getUserId() + " Логин: "
					+ user.getLogin() + " Действие: Вход.");
		}
		if (log.isDebugEnabled()) {
			log.debug("Response was sent");
		}
		return loginInfo;
	}
}
