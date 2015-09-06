package ua.nure.ostpc.malibu.shedule.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jxl.LabelCell;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.UserDAO;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.ExcelEmployee;
import ua.nure.ostpc.malibu.shedule.entity.Right;
import ua.nure.ostpc.malibu.shedule.excel.ExcelConstants;
import ua.nure.ostpc.malibu.shedule.excel.ExcelEmployeeBuilder;
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

	public void exportToExcel() {
		List<ExcelEmployee> excelEmployeeList = new ArrayList<ExcelEmployee>();
		List<Employee> employeeList = employeeDAO
				.getAllNotDeletedScheduleEmployees();
		for (Employee employee : employeeList) {
			List<Right> rightList = userDAO.getUserRightsByEmployeeId(employee
					.getEmployeeId());
			ExcelEmployee excelEmployee = new ExcelEmployee(employee, rightList);
			excelEmployeeList.add(excelEmployee);
		}
		try {
			toExcel(excelEmployeeList);
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
		}
	}

	private void toExcel(List<ExcelEmployee> excelEmployeeList)
			throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
		WritableSheet sheet = workbook.createSheet("Schedule employees - "
				+ new Date(), 0);
		try {
			setHeaders(sheet);
			int rowNumber = 1;
			for (ExcelEmployee employee : excelEmployeeList) {
				setRowInfo(sheet, employee, ++rowNumber);
			}

		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private void setHeaders(WritableSheet sheet) throws RowsExceededException,
			WriteException {
		String[] headerNames = ExcelNameContainer.getColumnTitleArray();
		Label label = new Label(0, 0, "№");
		sheet.addCell(label);
		int counter = 0;
		for (String header : headerNames) {
			Label l = new Label(++counter, 0, header);
			sheet.addCell(l);
		}
	}

	private String getColumnName(WritableSheet sheet, int column) {
		LabelCell cell = (LabelCell) sheet.getCell(column, 0);
		return cell.getString();
	}

	private String getEmpLoyeeValue(String columnName, ExcelEmployee xlsEmployee)
			throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		if (ExcelNameContainer.getFieldName(columnName).equals(
				ExcelConstants.EXCEL_FIELD_RIGHTS)) {
			Right right = ExcelNameContainer.getRight(columnName);
			if (xlsEmployee.getRights().contains(right)) {
				return "1";
			} else {
				return "0";
			}
		} else {
			Employee emp = xlsEmployee.getEmployee();
			Field field = emp.getClass().getDeclaredField(
					ExcelNameContainer.getFieldName(columnName));
			field.setAccessible(true);
			Object value = field.get(emp);
			return String.valueOf(value);
		}
	}

	private void setRowInfo(WritableSheet sheet, ExcelEmployee employee,
			int rowNumber) throws RowsExceededException, WriteException,
			NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException {
		Label label = new Label(0, rowNumber, String.valueOf(rowNumber));
		sheet.addCell(label);
		for (int i = 1; i <= ExcelNameContainer.getColumnTitleArray().length; i++) {
			String value = getEmpLoyeeValue(getColumnName(sheet, i), employee);
			Label lab = new Label(i, rowNumber, value);
			sheet.addCell(lab);
		}

	}

	public static void main(String[] args) {
		ExcelEmployeeService service = new ExcelEmployeeService(null, null);
		service.importFromExcel();
	}

	public ExcelEmployeeInsertResult importFromExcel() {
		ExcelEmployeeInsertResult insertResult;
		String[] columnArray = ExcelNameContainer.getColumnTitleArray();
		XlsReader reader = new XlsReader("employees.xls");
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
			log.error("Cannot export from excel file: " + e.getMessage());
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
