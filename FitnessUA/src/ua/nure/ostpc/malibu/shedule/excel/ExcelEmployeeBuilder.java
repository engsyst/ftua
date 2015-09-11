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
import ua.nure.ostpc.malibu.shedule.entity.Right;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

public class ExcelEmployeeBuilder<T extends ExcelEmployee> implements
		Builder<T> {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			AppConstants.PATTERN_dd_MM_yyyy);

	@SuppressWarnings("unchecked")
	@Override
	public T createItem(Set<DataField> fields) {
		Employee employee = new Employee();
		List<Right> rightList = new ArrayList<Right>();
		Method[] employeeMethods = Employee.class.getMethods();
		try {
			for (DataField dataField : fields) {
				String fieldName = ExcelNameContainer.getFieldName(dataField
						.getColumnName());
				if (fieldName.equals(ExcelConstants.EXCEL_FIELD_RIGHTS)) {
					invokeOnRightList(fieldName, rightList, dataField);
				} else {
					invokeOnEmployee(fieldName, employee, dataField,
							employeeMethods);
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException("Error during item creating: "
					+ e.getMessage());
		}
		ExcelEmployee excelEmployee = new ExcelEmployee(employee, rightList);
		return (T) excelEmployee;
	}

	private void invokeOnEmployee(String fieldName, Employee employee,
			DataField dataField, Method[] employeeMethods)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException, SecurityException,
			ParseException {
		Class<?> clazz = employee.getClass();
		for (Method method : employeeMethods) {
			XlsSetter annotation = method.getAnnotation(XlsSetter.class);
			if (annotation != null && annotation.fieldName().equals(fieldName)) {
				Field field = clazz.getDeclaredField(fieldName);
				if (field.getAnnotation(XlsField.class) != null) {
					Class<?> parameterType = method.getParameterTypes()[0];
					Object parameter = null;
					if (parameterType == String.class) {
						parameter = dataField.getValue();
					} else {
						if (parameterType == Integer.class
								|| parameterType == int.class) {
							parameter = Integer.valueOf(dataField.getValue());
						} else {
							if (parameterType == Date.class) {
								parameter = dateFormat.parse(dataField
										.getValue());
							}
						}
					}
					method.invoke(employee, parameter);
				}
			}
		}
	}

	private void invokeOnRightList(String fieldName, List<Right> rightList,
			DataField dataField) {
		if (dataField.getValue().equals("1")) {
			Right right = ExcelNameContainer
					.getRight(dataField.getColumnName());
			rightList.add(right);
		}
	}
}
