package ua.nure.ostpc.malibu.shedule.service;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.UserDAO;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.ExcelEmployee;
import ua.nure.ostpc.malibu.shedule.entity.Right;
import ua.nure.ostpc.malibu.shedule.excel.ExcelEmployeeBuilder;
import ua.nure.ostpc.malibu.shedule.excel.ExcelEmployeeWriter;
import ua.nure.ostpc.malibu.shedule.excel.ExcelNameContainer;
import ua.nure.ostpc.malibu.shedule.excel.XlsReader;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.shared.ExcelEmployeeInsertResult;
import ua.nure.ostpc.malibu.shedule.validator.ServerSideValidator;
import ua.nure.ostpc.malibu.shedule.validator.Validator;

/**
 * Service for export schedule employees to Excel document and import employees
 * from Excel document to database.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class ExcelEmployeeService {
	private static final Logger log = Logger
			.getLogger(ExcelEmployeeService.class);

	private EmployeeDAO employeeDAO;
	private UserDAO userDAO;

	public ExcelEmployeeService(EmployeeDAO employeeDAO, UserDAO userDAO) {
		this.employeeDAO = employeeDAO;
		this.userDAO = userDAO;
	}

	public static String makeNameForExport() {
		DateFormat df = new SimpleDateFormat(AppConstants.PATTERN_dd_MM_yyyy);
		String name = "Employees_export_for_" + df.format(new Date());
		return name;
	}

	public byte[] exportToExcel() {
		List<ExcelEmployee> excelEmployeeList = new ArrayList<ExcelEmployee>();
		List<Employee> employeeList = employeeDAO
				.getAllNotDeletedScheduleEmployees();
		for (Employee employee : employeeList) {
			List<Right> rightList = userDAO.getUserRightsByEmployeeId(employee
					.getEmployeeId());
			ExcelEmployee excelEmployee = new ExcelEmployee(employee, rightList);
			excelEmployeeList.add(excelEmployee);
		}
		ExcelEmployeeWriter excelEmployeeWriter = new ExcelEmployeeWriter();
		return excelEmployeeWriter.toExcel(excelEmployeeList);
	}

	public ExcelEmployeeInsertResult importFromExcel(InputStream inputStream) {
		ExcelEmployeeInsertResult insertResult;
		String[] columnArray = ExcelNameContainer.getColumnTitleArray();
		XlsReader reader = new XlsReader(inputStream);
		List<ExcelEmployee> excelEmployeeList;
		try {
			excelEmployeeList = reader.read(columnArray,
					new ExcelEmployeeBuilder<ExcelEmployee>());
			String errorMsg = validateExcelEmployeeList(excelEmployeeList);
			if (errorMsg.isEmpty()) {
				insertResult = employeeDAO
						.insertExcelEmployees(excelEmployeeList);
			} else {
				Map<String, String> errorMap = new LinkedHashMap<String, String>();
				errorMap.put(AppConstants.ERROR, errorMsg);
				insertResult = new ExcelEmployeeInsertResult(false, errorMap);
			}
		} catch (Exception e) {
			insertResult = new ExcelEmployeeInsertResult(false);
			log.error("Cannot import data from excel file!", e);
		}
		return insertResult;
	}

	/**
	 * This method validates {@code ExcelEmployee} list.
	 * 
	 * @param excelEmployeeList
	 *            - {@code ExcelEmployee} list for validation.
	 * @return Error message string. String is empty if there are no errors.
	 */
	private String validateExcelEmployeeList(
			List<ExcelEmployee> excelEmployeeList) {
		StringBuilder sb = new StringBuilder();
		Validator validator = new ServerSideValidator();
		for (int i = 0; i < excelEmployeeList.size(); i++) {
			Map<String, String> paramErrors = validator
					.validateExcelEmployeeProfile(excelEmployeeList.get(i)
							.toParamMapForValidation());
			if (!paramErrors.isEmpty()) {
				sb.append("Строка ");
				sb.append(i + 1);
				sb.append(" - ");
				for (String errorMsg : paramErrors.values()) {
					sb.append(errorMsg);
					sb.append(" ");
				}
				sb.append("\n");
			}
		}
		return sb.toString();
	}
}
