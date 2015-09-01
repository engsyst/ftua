package ua.nure.ostpc.malibu.shedule.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.UserDAO;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.ExcelEmployee;
import ua.nure.ostpc.malibu.shedule.entity.Role;
import ua.nure.ostpc.malibu.shedule.excel.ExcelConstants;
import ua.nure.ostpc.malibu.shedule.excel.ExcelEmployeeBuilder;
import ua.nure.ostpc.malibu.shedule.excel.ExcelNameContainer;
import ua.nure.ostpc.malibu.shedule.excel.XlsField;
import ua.nure.ostpc.malibu.shedule.excel.XlsReader;

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

	public void importToExcel() {
		List<ExcelEmployee> excelEmployeeList = new ArrayList<ExcelEmployee>();
		List<Employee> employeeList = employeeDAO
				.getAllNotDeletedScheduleEmployees();
		for (Employee employee : employeeList) {
			List<Role> roleList = userDAO.getUserRolesByEmployeeId(employee
					.getEmployeeId());
			ExcelEmployee excelEmployee = new ExcelEmployee(employee, roleList);
			excelEmployeeList.add(excelEmployee);
		}
		try {
			toExcel(excelEmployeeList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void toExcel(List<ExcelEmployee> excelEmployeeList)
			throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
		WritableSheet sheet = workbook.createSheet("Schedule employees - "
				+ new Date(), 0);
	}

	public static void main(String[] args) throws BiffException, IOException {
		List<Field> fieldList = new ArrayList<Field>();
		fieldList.addAll(Arrays.asList(Employee.class.getDeclaredFields()));
		fieldList
				.addAll(Arrays.asList(ExcelEmployee.class.getDeclaredFields()));

		String[] columnArray = ExcelNameContainer.getColumnTitleArray();

		XlsReader reader = new XlsReader("employees.xls");
		List<ExcelEmployee> excelEmployeeList = reader.read(columnArray,
				new ExcelEmployeeBuilder<ExcelEmployee>());
	}
}
