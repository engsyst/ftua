package ua.nure.ostpc.malibu.shedule.server;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
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
		try {
			byte[] excelByteArray = excelEmployeeService.exportToExcel();
			response.setContentType(AppConstants.EXCEL_FILE_CONTENT_TYPE);
			response.setHeader("Content-Disposition", " attachment; filename="
					+ ExcelEmployeeService.makeNameForExport() + ".xls");
			response.setContentLength(excelByteArray.length);
			final int BUFFER = 1024 * 4;
			response.setBufferSize(BUFFER);
			ServletOutputStream servletOutputStream = response
					.getOutputStream();
			servletOutputStream.write(excelByteArray);
			servletOutputStream.flush();
			servletOutputStream.close();
		} catch (Exception e) {
			log.error("Download can not be started!");
		}
		if (log.isDebugEnabled()) {
			log.debug("Response was sent");
		}
	}
}
