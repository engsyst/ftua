package ua.nure.ostpc.malibu.shedule.client.settings;

import java.util.Map;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class EditEmployeeForm extends ProfilePanel {
	private ErrorLabel errorLabel = new ErrorLabel();

	public EditEmployeeForm() {
		add(errorLabel);
		getEditButton().setText("Добавить");
	}

	public EditEmployeeForm(Employee employee) {
		super(employee);
		add(errorLabel);
	}

	@Override
	protected void initPanel() {
//		HTML helpPanel = new HTML(AppConstants.TEXT__HTML_HELP_EMP_PANEL);
//		helpPanel.setTitle(AppConstants.TEXT__HELP_EMP_PANEL);
//		helpPanel.setStyleName("helpPanel");
//		add(helpPanel);
		super.initPanel();
	}

	@Override
	protected void addHandlers() {
		getEditButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Map<String, String> paramMap = getFullEmployeeParamMap();
				Map<String, String> errorMap = AppState.clientSideValidator
						.validateFullEmployeeProfile(paramMap, AppConstants.PATTERN_dd_MM_yyyy);
				if (errorMap != null && errorMap.size() != 0) {
					setErrors(errorMap, errorLabel);
				} else {
					getEditButton().setEnabled(false);
					if (getEmployeeId() == 0) {
						insertFullEmployeeProfile(paramMap, errorLabel);
						getEditButton().setText("Изменить");
					} else {
						updateFullEmployeeProfile(paramMap, errorLabel);
					}
				}
			}
		});
	}
}
