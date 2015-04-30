package ua.nure.ostpc.malibu.shedule.client.settings;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.smartgwt.client.util.SC;

import ua.nure.ostpc.malibu.shedule.client.AppState;
import ua.nure.ostpc.malibu.shedule.entity.Employee;

public class EditEmployeeForm extends ProfilePanel {
	private ErrorLabel errorLabel = new ErrorLabel();

	public interface EmployeeUpdater {
		public void updateEmployee();
	}

	private static Set<EmployeeUpdater> updaterSet = new HashSet<EmployeeUpdater>();

	public EditEmployeeForm() {
		add(errorLabel);
		getEditButton().setText("Добавить");
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
						getEditButton().setText("Изменить");
					} else {
						updateFullEmployeeProfile(paramMap, errorLabel);
					}
					for (EmployeeUpdater updater : updaterSet) {
						try {
							updater.updateEmployee();
						} catch (Exception caught) {
							SC.say(caught.getMessage());
						}
					}
				}
			}
		});
	}

	public static boolean registerUpdater(EmployeeUpdater updater) {
		if (updater != null)
			return updaterSet.add(updater);
		return false;
	}

	public static boolean unregisterUpdater(EmployeeUpdater updater) {
		if (updater != null)
			return updaterSet.remove(updater);
		return false;
	}
}
