package ua.nure.ostpc.malibu.shedule.client.settings;

import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

public class EditEmployeeForm extends ProfilePanel {
	private ErrorLabel errorLabel = new ErrorLabel();

	public EditEmployeeForm() {
		add(errorLabel);
	}

	public EditEmployeeForm(Employee employee) {
		super(employee);
		add(errorLabel);
	}

	@Override
	protected void addHandlers() {
		getEditButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Map<String, String> paramMap = getFullEmployeeParamMap();
				Map<String, String> errorMap = AppState.clientSideValidator
						.validateFullEmployeeProfile(paramMap, getDatePattern());
				if (errorMap != null && errorMap.size() != 0) {
					setErrors(errorMap, errorLabel);
				} else {
					getEditButton().setEnabled(false);
					if (getEmployeeId() == 0) {
						insertFullEmployeeProfile(paramMap, errorLabel);
					} else {
						updateFullEmployeeProfile(paramMap, errorLabel);
					}
				}
			}
		});
	}
}
