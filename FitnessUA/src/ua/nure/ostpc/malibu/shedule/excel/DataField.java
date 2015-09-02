package ua.nure.ostpc.malibu.shedule.excel;

import jxl.CellType;

public class DataField {
	private String columnName;
	private int number;
	private CellType cellType;
	private String value;

	public DataField() {
	}

	public DataField(String columnName, int number, CellType cellType,
			String value) {
		this.columnName = columnName;
		this.number = number;
		this.cellType = cellType;
		this.value = value;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public CellType getCellType() {
		return cellType;
	}

	public void setCellType(CellType cellType) {
		this.cellType = cellType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
