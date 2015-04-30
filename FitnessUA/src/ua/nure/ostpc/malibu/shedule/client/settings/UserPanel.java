package ua.nure.ostpc.malibu.shedule.client.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class UserPanel extends VerticalPanel {
	private long employeeId;
	private Button editButton;
	private Map<String, ErrorLabel> errorLabelMap = new LinkedHashMap<String, ErrorLabel>();

	public UserPanel() {
	}

	public UserPanel(long employeeId) {
		this.employeeId = employeeId;
	}

	protected long getEmployeeId() {
		return employeeId;
	}

	protected void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

	protected Button getEditButton() {
		return editButton;
	}

	protected Map<String, ErrorLabel> getErrorLabelMap() {
		return errorLabelMap;
	}

	protected void initFlexTable(ArrayList<Label> labels,
			ArrayList<Widget> paramControls, ArrayList<ErrorLabel> errorLabels) {
		FlexTable flexTable = new FlexTable();
		flexTable.setBorderWidth(0);
		for (int i = 0; i < labels.size(); i++) {
			flexTable.insertRow(i);
			flexTable.insertCell(i, 0);
			flexTable.setWidget(i, 0, labels.get(i));
			flexTable.insertCell(i, 1);
			flexTable.setWidget(i, 1, paramControls.get(i));
			flexTable.insertCell(i, 2);
			flexTable.setWidget(i, 2, errorLabels.get(i));
		}
		add(flexTable);
	}

	protected void initEditButton() {
		editButton = new Button("Изменить");
		add(editButton);
	}

	protected void setErrors(Map<String, String> errorMap, ErrorLabel errorLabel) {
		if (errorMap != null) {
			Iterator<Entry<String, ErrorLabel>> it = errorLabelMap.entrySet()
					.iterator();
			while (it.hasNext()) {
				Entry<String, ErrorLabel> entry = it.next();
				String key = entry.getKey();
				ErrorLabel errLabel = entry.getValue();
				String errorMessage = errorMap.get(key);
				errorMessage = errorMessage != null ? errorMessage : "";
				errLabel.setText(errorMessage);
			}
			errorLabel.setText("Не все поля заполнены корректно!");
		}
	}

	protected void clearErrorLabelMap() {
		Iterator<Entry<String, ErrorLabel>> it = errorLabelMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<String, ErrorLabel> entry = it.next();
			ErrorLabel errLabel = entry.getValue();
			errLabel.setText("");
		}
	}
}
