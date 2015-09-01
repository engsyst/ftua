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

public class ExcelEmployeeBuilder<T extends ExcelEmployee> implements
		Builder<T> {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd.MM.yyyy");

	@SuppressWarnings("unchecked")
	@Override
	public T createItem(Set<DataField> fields) {
		Employee employee = new Employee();
		List<Right> rightList = new ArrayList<Right>();
		Method[] employeeMethods = Employee.class.getMethods();
		//try {
			for (DataField dataField : fields) {
				String fieldName = ExcelNameContainer.getFieldName(dataField
						.getColumnName());
				if (fieldName.equals(ExcelConstants.EXCEL_FIELD_RIGHTS)) {
					invokeOnRightList(fieldName, rightList, dataField);
				} else {
					try {
						invokeOnEmployee(fieldName, employee, dataField,
								employeeMethods);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		/*} catch (Exception e) {
			throw new IllegalStateException("Error during item creating: "
					+ e.getMessage());
		}*/
		ExcelEmployee excelEmployee = new ExcelEmployee(employee, rightList);
		return (T) excelEmployee;
	}

	private void invokeOnEmployee(String fieldName, Employee employee,
			DataField dataField, Method[] employeeMethods)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException, SecurityException,
			ParseException {
		Object parameter = null;
		Class<Employee> clazz = Employee.class;
		for (Method method : employeeMethods) {
			XlsSetter annotation = method.getAnnotation(XlsSetter.class);
			if (annotation != null && annotation.fieldName().equals(fieldName)) {
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
