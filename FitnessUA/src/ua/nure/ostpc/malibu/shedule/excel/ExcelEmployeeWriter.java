package ua.nure.ostpc.malibu.shedule.excel;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jxl.LabelCell;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.PageOrientation;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.ExcelEmployee;
import ua.nure.ostpc.malibu.shedule.entity.Right;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;
import ua.nure.ostpc.malibu.shedule.service.ExcelEmployeeService;

public class ExcelEmployeeWriter {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			AppConstants.PATTERN_dd_MM_yyyy);

	private WritableCellFormat titleTextFormat;
	private WritableCellFormat cellTextFormat;

	public byte[] write(List<ExcelEmployee> excelEmployeeList) {
		try {
			setCellFormats();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
			WritableSheet sheet = workbook.createSheet(
					ExcelEmployeeService.makeNameForExport(), 0);
			sheet.setPageSetup(PageOrientation.LANDSCAPE);
			setHeaders(sheet);
			int rowNumber = 1;
			for (ExcelEmployee employee : excelEmployeeList) {
				setRowInfo(sheet, employee, rowNumber++);
			}
			workbook.write();
			workbook.close();
			byte[] res = outputStream.toByteArray();
			return res;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private void setCellFormats() throws WriteException {
		titleTextFormat = new WritableCellFormat();
		titleTextFormat.setAlignment(Alignment.CENTRE);
		titleTextFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
		titleTextFormat.setBorder(Border.ALL, BorderLineStyle.DOUBLE);
		titleTextFormat.setWrap(true);

		cellTextFormat = new WritableCellFormat();
		cellTextFormat.setAlignment(Alignment.LEFT);
		cellTextFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
		cellTextFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
		cellTextFormat.setWrap(true);
	}

	private void setHeaders(WritableSheet sheet) throws RowsExceededException,
			WriteException {
		String[] headerNames = ExcelNameContainer.getColumnTitleArray();
		Label label = new Label(0, 0, "№", titleTextFormat);
		sheet.addCell(label);
		int counter = 0;
		for (String header : headerNames) {
			Label l = new Label(++counter, 0, header, titleTextFormat);
			sheet.addCell(l);
		}
	}

	private void setRowInfo(WritableSheet sheet, ExcelEmployee employee,
			int rowNumber) throws RowsExceededException, WriteException,
			NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException {
		Label numberLabel = new Label(0, rowNumber, String.valueOf(rowNumber),
				cellTextFormat);
		sheet.addCell(numberLabel);
		for (int i = 1; i <= ExcelNameContainer.getColumnTitleArray().length; i++) {
			String value = getEmployeeValue(getColumnName(sheet, i), employee);
			Label label = new Label(i, rowNumber, value, cellTextFormat);
			sheet.addCell(label);
		}

	}

	private String getEmployeeValue(String columnName,
			ExcelEmployee excelEmployee) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		if (ExcelNameContainer.getFieldName(columnName).equals(
				ExcelConstants.EXCEL_FIELD_RIGHTS)) {
			Right right = ExcelNameContainer.getRight(columnName);
			if (excelEmployee.getRights().contains(right)) {
				return "1";
			} else {
				return "0";
			}
		} else {
			Employee employee = excelEmployee.getEmployee();
			Field field = employee.getClass().getDeclaredField(
					ExcelNameContainer.getFieldName(columnName));
			field.setAccessible(true);
			Object value = field.get(employee);
			if (value instanceof Date) {
				return dateFormat.format(value);
			}
			return String.valueOf(value);
		}
	}

	private String getColumnName(WritableSheet sheet, int column) {
		LabelCell cell = (LabelCell) sheet.getCell(column, 0);
		return cell.getString();
	}
}
