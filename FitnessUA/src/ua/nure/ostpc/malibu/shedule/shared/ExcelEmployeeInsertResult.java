package ua.nure.ostpc.malibu.shedule.shared;

import java.io.Serializable;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Result of {@code ExcelEmployee} list insertion.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class ExcelEmployeeInsertResult implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;

	private boolean insertResult;
	private int rowNumber;
	private Map<String, String> errorMap;

	public ExcelEmployeeInsertResult() {
	}

	public ExcelEmployeeInsertResult(boolean insertResult, int rowNumber,
			Map<String, String> errorMap) {
		this(insertResult, errorMap);
		this.rowNumber = rowNumber;
	}

	public ExcelEmployeeInsertResult(boolean insertResult,
			Map<String, String> errorMap) {
		this(insertResult);
		this.errorMap = errorMap;
	}

	public ExcelEmployeeInsertResult(boolean insertResult) {
		this.insertResult = insertResult;
	}

	public boolean isInsertResult() {
		return insertResult;
	}

	public void setInsertResult(boolean insertResult) {
		this.insertResult = insertResult;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	public Map<String, String> getErrorMap() {
		return errorMap;
	}

	public void setErrorMap(Map<String, String> errorMap) {
		this.errorMap = errorMap;
	}

	@Override
	public String toString() {
		return "ExcelEmployeeInsertResult [insertResult=" + insertResult
				+ ", rowNumber=" + rowNumber + ", errorMap=" + errorMap + "]";
	}
}
