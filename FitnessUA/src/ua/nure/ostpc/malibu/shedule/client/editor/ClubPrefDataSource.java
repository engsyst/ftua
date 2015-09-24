package ua.nure.ostpc.malibu.shedule.client.editor;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Data source for club preference select item.
 * 
 * @author Volodymyr_Semerkov
 * 
 */
public class ClubPrefDataSource extends DataSource {
	private static ClubPrefDataSource instance = null;
	private static LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();

	public static ClubPrefDataSource getInstance() {
		if (instance == null) {
			instance = new ClubPrefDataSource();
		}
		return instance;
	}

	private ClubPrefDataSource() {
		DataSourceTextField idTextField = new DataSourceTextField(
				AppConstants.DATA_SOURCE_CLUB_PREF_ID);
		idTextField.setPrimaryKey(true);
		DataSourceTextField nameTextField = new DataSourceTextField(
				AppConstants.DATA_SOURCE_CLUB_PREF_NAME);
		setFields(idTextField, nameTextField);

		setClientOnly(true);

		Iterator<Entry<String, String>> it = valueMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			ListGridRecord record = new ListGridRecord();
			record.setAttribute(AppConstants.DATA_SOURCE_CLUB_PREF_ID,
					entry.getKey());
			record.setAttribute(AppConstants.DATA_SOURCE_CLUB_PREF_NAME,
					entry.getValue());
			addData(record);
		}
	}

	public static LinkedHashMap<String, String> getValueMap() {
		return valueMap;
	}

	/**
	 * This method sets new value map for data source and sets {@code instance}
	 * to null. Data source will be initialized to these new values after method
	 * {@link #getInstanse()} calling.
	 * 
	 * @param valueMap
	 *            - new value map.
	 */
	public static void setValueMap(LinkedHashMap<String, String> valueMap) {
		ClubPrefDataSource.valueMap = valueMap;
		instance = null;
	}
}
