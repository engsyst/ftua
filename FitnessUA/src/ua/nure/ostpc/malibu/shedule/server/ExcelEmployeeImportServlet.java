package ua.nure.ostpc.malibu.shedule.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import ua.nure.ostpc.malibu.shedule.entity.User;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.service.ExcelEmployeeService;
import ua.nure.ostpc.malibu.shedule.shared.ExcelEmployeeInsertResult;

@SuppressWarnings("serial")
public class ExcelEmployeeImportServlet extends HttpServlet {
	private static final Logger log = Logger
			.getLogger(ExcelEmployeeImportServlet.class);

	private ExcelEmployeeService excelEmployeeService;

	public ExcelEmployeeImportServlet() {
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
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (log.isDebugEnabled()) {
			log.debug("POST method started");
		}
		ExcelEmployeeInsertResult result = null;
		FileItemFactory fileItemFactory = new DiskFileItemFactory();
		ServletFileUpload servletFileUpload = new ServletFileUpload(
				fileItemFactory);
		try {
			List<FileItem> fileItemList = servletFileUpload
					.parseRequest(request);
			for (FileItem fileItem : fileItemList) {
				if (!fileItem.isFormField()
						&& fileItem.getFieldName().equals(
								AppConstants.EXCEL_FILE)) {
					if (checkFileAttributes(fileItem.getName(),
							fileItem.getContentType(), fileItem.getSize())) {
						result = excelEmployeeService.importFromExcel(fileItem
								.getInputStream());
					}
					break;
				}
			}
		} catch (Exception e) {
			log.error("Cannot import employees from Excel file!", e);
		} finally {
			if (result == null) {
				result = new ExcelEmployeeInsertResult(false);
			}
			if (log.isDebugEnabled()) {
				log.debug("result --> " + result);
			}
			JSONObject resultJson = new JSONObject();
			try {
				resultJson.put(AppConstants.EXCEL_JSON_RESULT,
						result.isInsertResult());
				resultJson.put(AppConstants.EXCEL_JSON_ROW_NUMBER,
						result.getRowNumber());
				resultJson.put(AppConstants.EXCEL_JSON_ERROR_MAP,
						result.getErrorMap());
			} catch (JSONException e) {
				log.error("Cannot send JSON!", e);
			}
			response.setContentType(AppConstants.JSON_CONTENT_TYPE);
			PrintWriter writer = response.getWriter();
			writer.write(resultJson.toString());
			writer.flush();
			if (log.isInfoEnabled() && result.isInsertResult()) {
				HttpSession session = request.getSession();
				User user = (User) session.getAttribute(AppConstants.USER);
				log.info("UserId: "
						+ user.getUserId()
						+ " Логин: "
						+ user.getLogin()
						+ " Действие: Выполнил импорт списка сотрудников из Excel-файла.");
			}
		}
	}

	private boolean checkFileAttributes(String fileName, String contentType,
			long fileSize) {
		boolean result = checkFileName(fileName);
		result = result && checkFileExtension(fileName);
		result = result && checkContentType(contentType);
		result = result && checkFileSize(fileSize);
		return result;
	}

	private boolean checkFileName(String fileName) {
		return fileName != null ? true : false;
	}

	private boolean checkFileExtension(String fileName) {
		String fileExtension = "";
		if (fileName != null) {
			fileExtension = FilenameUtils.getExtension(fileName);
		}
		return fileExtension
				.equalsIgnoreCase(AppConstants.EXCEL_FILE_EXTENSION);
	}

	private boolean checkContentType(String contentType) {
		return AppConstants.EXCEL_FILE_CONTENT_TYPE.equals(contentType);
	}

	private boolean checkFileSize(long fileSize) {
		return fileSize != 0
				&& fileSize <= AppConstants.EXCEL_FILE_MAX_SIZE_MB * 1024 * 1024 ? true
				: false;
	}
}
