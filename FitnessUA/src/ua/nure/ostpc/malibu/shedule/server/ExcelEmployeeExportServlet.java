package ua.nure.ostpc.malibu.shedule.server;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.service.ExcelEmployeeService;

@SuppressWarnings("serial")
public class ExcelEmployeeExportServlet extends HttpServlet {
	private static final Logger log = Logger
			.getLogger(ExcelEmployeeExportServlet.class);

	private ExcelEmployeeService excelEmployeeService;

	public ExcelEmployeeExportServlet() {
		if (log.isDebugEnabled()) {
			log.debug("Servlet created");
		}
	}

	@Override
	public void init() {
		ServletContext servletContext = getServletContext();
		excelEmployeeService = (ExcelEmployeeService) servletContext
				.getAttribute(AppConstants.EXCEL_EMPLOYEE_SERVICE);
		if (excelEmployeeService == null) {
			log.error("ExcelEmployeeService attribute does not exist.");
			throw new IllegalStateException(
					"ExcelEmployeeService attribute does not exist.");
		}
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (log.isDebugEnabled()) {
			log.debug("GET method started");
		}
	}
}
