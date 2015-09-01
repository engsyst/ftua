package ua.nure.ostpc.malibu.shedule.excel;

import jxl.CellType;

public class DataField {
	private String name;
	private Integer number;
	private CellType cellType;
	private String value;

	public DataField() {
	}

	public DataField(String name, Integer number, CellType cellType,
			String value) {
		this.name = name;
		this.number = number;
		this.cellType = cellType;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
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
