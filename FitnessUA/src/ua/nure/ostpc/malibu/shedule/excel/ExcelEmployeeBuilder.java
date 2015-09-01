package ua.nure.ostpc.malibu.shedule.excel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.ExcelEmployee;
import ua.nure.ostpc.malibu.shedule.entity.Role;

public class ExcelEmployeeBuilder<T extends ExcelEmployee> implements
		Builder<T> {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd.MM.yyyy");

	@SuppressWarnings("unchecked")
	@Override
	public T createItem(Set<DataField> fields) {
		Employee employee = new Employee();
		List<Role> roleList = new ArrayList<Role>();
		ExcelEmployee excelEmployee = new ExcelEmployee(employee, roleList);
		
		Method[] employeeMethods = Employee.class.getMethods();
		Method[] excelEmployeeMethods = ExcelEmployee.class.getMethods();
		try {
			for (DataField dataField : fields) {
				if (!invokeOnEmployee(employee, dataField, employeeMethods)) {

				}
			}
		} catch (Exception e) {
			throw new IllegalStateException("Error during item creation: "
					+ e.getMessage());
		}
		return (T) excelEmployee;
	}

	private boolean invokeOnEmployee(Employee employee, DataField dataField,
			Method[] employeeMethods) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchFieldException, SecurityException, ParseException {
		Object parameter = null;
		Class<Employee> clazz = Employee.class;
		for (Method method : employeeMethods) {
			XlsSetter annotation = method.getAnnotation(XlsSetter.class);
			if (annotation != null
					&& annotation.fieldName().equals(dataField.getName())) {
				String fieldName = method.getAnnotation(XlsSetter.class)
						.fieldName();
				Field field = clazz.getDeclaredField(fieldName);
				if (field.getAnnotation(XlsField.class) != null) {
					Class<?> parameterType = method.getParameterTypes()[0];
					if (parameterType == String.class) {
						parameter = dataField.getValue();
					} else {
						if (parameterType == Integer.class) {
							parameter = Integer.valueOf(dataField.getValue());
						} else {
							if (parameterType == Date.class) {
								parameter = dateFormat.parse(dataField
										.getValue());
							}
						}
					}
					method.invoke(employee, parameter);
					return true;
				}
			}
		}
		return false;
	}

	private void invokeOnRoleList(List<Role> roleList, DataField dataField) {
		
	}
}
