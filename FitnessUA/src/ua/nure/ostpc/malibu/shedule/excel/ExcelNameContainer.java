package ua.nure.ostpc.malibu.shedule.excel;

import java.util.LinkedHashMap;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Right;

/**
 * Holder for all column titles and identifiers.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class ExcelNameContainer {
	private static Map<String, String> parameterMap = new LinkedHashMap<String, String>();
	private static Map<String, Right> roleConformityMap = new LinkedHashMap<String, Right>();

	static {
		// Employee
		parameterMap.put(ExcelConstants.EXCEL_COLUMN_LAST_NAME,
				ExcelConstants.EXCEL_FIELD_LAST_NAME);
		parameterMap.put(ExcelConstants.EXCEL_COLUMN_FIRST_NAME,
				ExcelConstants.EXCEL_FIELD_FIRST_NAME);
		parameterMap.put(ExcelConstants.EXCEL_COLUMN_SECOND_NAME,
				ExcelConstants.EXCEL_FIELD_SECOND_NAME);
		parameterMap.put(ExcelConstants.EXCEL_COLUMN_BIRTHDAY,
				ExcelConstants.EXCEL_FIELD_BIRTHDAY);
		parameterMap.put(ExcelConstants.EXCEL_COLUMN_ADDRESS,
				ExcelConstants.EXCEL_FIELD_ADDRESS);
		parameterMap.put(ExcelConstants.EXCEL_COLUMN_PASSPORT_NUMBER,
				ExcelConstants.EXCEL_FIELD_PASSPORT_NUMBER);
		parameterMap.put(ExcelConstants.EXCEL_COLUMN_ID_NUMBER,
				ExcelConstants.EXCEL_FIELD_ID_NUMBER);
		parameterMap.put(ExcelConstants.EXCEL_COLUMN_CELL_PHONE,
				ExcelConstants.EXCEL_FIELD_CELL_PHONE);
		parameterMap.put(ExcelConstants.EXCEL_COLUMN_WORK_PHONE,
				ExcelConstants.EXCEL_FIELD_WORK_PHONE);
		parameterMap.put(ExcelConstants.EXCEL_COLUMN_HOME_PHONE,
				ExcelConstants.EXCEL_FIELD_HOME_PHONE);
		parameterMap.put(ExcelConstants.EXCEL_COLUMN_EMAIL,
				ExcelConstants.EXCEL_FIELD_EMAIL);
		parameterMap.put(ExcelConstants.EXCEL_COLUMN_EDUCATION,
				ExcelConstants.EXCEL_FIELD_EDUCATION);
		parameterMap.put(ExcelConstants.EXCEL_COLUMN_NOTES,
				ExcelConstants.EXCEL_FIELD_NOTES);
		parameterMap.put(ExcelConstants.EXCEL_COLUMN_PASSPORT_ISSUED_BY,
				ExcelConstants.EXCEL_FIELD_PASSPORT_ISSUED_BY);
		parameterMap.put(ExcelConstants.EXCEL_COLUMN_MIN_DAYS,
				ExcelConstants.EXCEL_FIELD_MIN_DAYS);
		parameterMap.put(ExcelConstants.EXCEL_COLUMN_MAX_DAYS,
				ExcelConstants.EXCEL_FIELD_MAX_DAYS);

		// Role list. Each right column key refer to one right field.
		parameterMap.put(ExcelConstants.EXCEL_COLUMN_RESPONSIBLE_PERSON,
				ExcelConstants.EXCEL_FIELD_RIGHTS);
		parameterMap.put(ExcelConstants.EXCEL_COLUMN_ADMIN,
				ExcelConstants.EXCEL_FIELD_RIGHTS);
		parameterMap.put(ExcelConstants.EXCEL_COLUMN_SUBSCRIBER,
				ExcelConstants.EXCEL_FIELD_RIGHTS);

		// Role conformity
		roleConformityMap.put(ExcelConstants.EXCEL_COLUMN_RESPONSIBLE_PERSON,
				Right.RESPONSIBLE_PERSON);
		roleConformityMap.put(ExcelConstants.EXCEL_COLUMN_ADMIN, Right.ADMIN);
		roleConformityMap.put(ExcelConstants.EXCEL_COLUMN_SUBSCRIBER,
				Right.SUBSCRIBER);
	}

	public static String[] getColumnTitleArray() {
		return parameterMap.keySet().toArray(
				new String[parameterMap.keySet().size()]);
	}

	public static String getFieldName(String columnName) {
		return parameterMap.get(columnName);
	}

	public static Right getRight(String columnName) {
		return roleConformityMap.get(columnName);
	}
}
