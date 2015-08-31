package ua.nure.ostpc.malibu.shedule.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;
import ua.nure.ostpc.malibu.shedule.dao.UserDAO;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.ExcelEmployee;
import ua.nure.ostpc.malibu.shedule.entity.Role;

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

	private void toExcel(List<ExcelEmployee> excelEmployeeList) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
		WritableSheet sheet = workbook.createSheet("Schedule employees - "
				+ new Date(), 0);
	}
}
