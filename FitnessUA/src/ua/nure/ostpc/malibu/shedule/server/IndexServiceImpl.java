package ua.nure.ostpc.malibu.shedule.server;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.Path;
import ua.nure.ostpc.malibu.shedule.client.IndexService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class IndexServiceImpl extends RemoteServiceServlet implements
		IndexService {
	private static final Logger log = Logger.getLogger(IndexServiceImpl.class);

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (log.isDebugEnabled()) {
			log.debug("GET method starts");
		}
		RequestDispatcher dispatcher = request
				.getRequestDispatcher(Path.PAGE__INDEX);
		dispatcher.forward(request, response);
		if (log.isDebugEnabled()) {
			log.debug("Response was sent");
		}
	}

	@Override
	public void index() throws IllegalArgumentException {
	}
}
