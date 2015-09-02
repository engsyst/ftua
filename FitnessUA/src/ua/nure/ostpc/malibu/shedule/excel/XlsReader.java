package ua.nure.ostpc.malibu.shedule.excel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class XlsReader {
	/**
	 * File path for excel file
	 */
	private String filePath;

	/**
	 * Index of sheet to read operation. Used if sheetName == null
	 */
	private int sheetIndex = 0;

	/**
	 * Name of sheet to read operation.
	 */
	private String sheetName;
	private Workbook workbook;

	/**
	 * Set of user header of columns. Must be unique.
	 */
	private TreeSet<DataField> headers;
	private Comparator<DataField> numberComparator = new Comparator<DataField>() {
		@Override
		public int compare(DataField o1, DataField o2) {
			return compare(o1.getNumber(), o2.getNumber());
		}

		private int compare(int x, int y) {
			return (x < y) ? -1 : ((x == y) ? 0 : 1);
		}
	};

	/**
	 * @param filePath
	 *            Name of file to read; Any operation will do at this file. If
	 *            need read another file create new instance.
	 */
	public XlsReader(String filePath) {
		if (filePath == null)
			throw new IllegalArgumentException("File path: " + filePath);
		this.filePath = filePath;
	}

	public String getSheetName() {
		return sheetName;
	}

	/**
	 * Sets name of sheet to read. If you need read sheet with some name, you
	 * must call this method before read operation. Set this field to null if
	 * you need read using index.
	 * 
	 * @param sheetName
	 */
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public int getSheetIndex() {
		return sheetIndex;
	}

	/**
	 * Sets index of sheet to read. If you need read sheet with index != 0 set
	 * this field before read operation. This field used if sheetName != null.
	 * 
	 * @param sheetIndex
	 */
	public void setSheetIndex(int sheetIndex) {
		this.sheetIndex = sheetIndex;
	}

	/**
	 * <p>
	 * Read sheet with name in {@link XlsReader#sheetName}. If
	 * {@link XlsReader#sheetName} == null read sheet with index in
	 * {@link XlsReader#sheetIndex}
	 * </p>
	 * 
	 * @param columnArray
	 * @param builder
	 * @return
	 * @throws BiffException
	 * @throws IOException
	 */
	public <T> List<T> read(String[] columnArray, Builder<T> builder)
			throws BiffException, IOException {
		workbook = Workbook.getWorkbook(new File(filePath));
		Sheet sheet = null;
		if (sheetName == null) {
			sheet = workbook.getSheet(sheetIndex);
		} else {
			sheet = workbook.getSheet(sheetName);
		}
		readHeaders(sheet, columnArray);
		int rowCount = sheet.getRows();
		ArrayList<T> res = new ArrayList<T>(rowCount);
		for (int i = 1; i < rowCount
				&& sheet.getRow(i).length >= headers.size(); i++) {
			T t = readRow(sheet.getRow(i), builder);
			res.add(t);
		}
		return res;
	}

	private void readHeaders(Sheet sheet, String[] columnArray) {
		Cell[] rowCells = sheet.getRow(0);
		if (rowCells.length < columnArray.length) {
			throw new IllegalStateException(
					"Unexpected data length. Cells count: " + rowCells.length
							+ " Expected count: " + columnArray.length
							+ toStringRow(rowCells)
							+ Arrays.toString(columnArray));
		}
		headers = new TreeSet<DataField>(numberComparator);
		for (int i = 0; i < rowCells.length; i++) {
			String rowContent = rowCells[i].getContents().trim();
			for (int j = 0; j < columnArray.length; j++) {
				if (!"".equals(rowContent) && rowContent.equals(columnArray[j])) {
					DataField dataField = new DataField();
					dataField.setColumnName(rowContent);
					dataField.setNumber(i);
					headers.add(dataField);
					break;
				}
			}
		}
		if (headers.size() < columnArray.length) {
			throw new IllegalStateException(
					"Unexpected data length. Founded count: " + headers.size()
							+ " Needed count: " + columnArray.length);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T readRow(Cell[] rowCells, Builder<T> builder) {
		TreeSet<DataField> fields = (TreeSet<DataField>) headers.clone();
		for (DataField dataField : fields) {
			dataField.setValue(rowCells[dataField.getNumber()].getContents()
					.trim());
			dataField.setCellType(rowCells[dataField.getNumber()].getType());
		}
		T t = builder.createItem(fields);
		return t;
	}

	private String toStringRow(Cell[] row) {
		StringBuilder builder = new StringBuilder();
		builder.append("\nRow: ");
		if (row.length != 0) {
			builder.append(row[0].getRow());
		}
		builder.append(" [");
		for (int i = 0; i < row.length; i++) {
			builder.append(row[i].getContents());
			builder.append(", ");
		}
		builder.append("]");
		return builder.toString();
	}
}
