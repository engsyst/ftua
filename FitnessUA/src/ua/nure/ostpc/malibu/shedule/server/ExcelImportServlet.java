package ua.nure.ostpc.malibu.shedule.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.service.ExcelEmployeeService;
import ua.nure.ostpc.malibu.shedule.shared.ExcelEmployeeInsertResult;

@SuppressWarnings("serial")
public class ExcelImportServlet extends HttpServlet {
	private static final Logger log = Logger
			.getLogger(ExcelImportServlet.class);

	private ExcelEmployeeService excelEmployeeService;

	public ExcelImportServlet() {
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
						ExcelEmployeeInsertResult result = excelEmployeeService
								.importFromExcel(fileItem.getInputStream());
						System.out.println(result);
					}
					break;
				}
			}
		} catch (Exception e) {
			// TODO
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
