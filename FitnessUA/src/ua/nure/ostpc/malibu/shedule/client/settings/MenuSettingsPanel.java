package ua.nure.ostpc.malibu.shedule.client.settings;

import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class MenuSettingsPanel extends Composite {
	private MenuItem clubsMenuItem;
	private MenuItem employeeMenuItem;
	private MenuItem categoryMenuItem;
	private MenuItem holidayMenuItem;
	private MenuBar menuBar;

	/**
	 * @wbp.parser.entryPoint
	 */
	public MenuSettingsPanel() {
		menuBar = new MenuBar();
		
		clubsMenuItem = new MenuItem("Клубы", false, doClubSettings);
		menuBar.addItem(clubsMenuItem);

		employeeMenuItem = new MenuItem("Сотрудники", false, doEmployeeSettings);
		menuBar.addItem(employeeMenuItem);

		categoryMenuItem = new MenuItem("Категории", false, doCategorySettings);
		menuBar.addItem(categoryMenuItem);

		holidayMenuItem = new MenuItem("Выходные", false, doHolidaySettings);
		menuBar.addItem(holidayMenuItem);

		initWidget(menuBar);
	}

	private Command doClubSettings = new Command() {
		@Override
		public void execute() {
			History.newItem(AppConstants.HISTORY_CLUB_SETTINGS);
		}
	};

	private Command doEmployeeSettings = new Command() {
		@Override
		public void execute() {
			History.newItem(AppConstants.HISTORY_EMPLOYEE_SETTINGS);
		}
	};

	private Command doCategorySettings = new Command() {
		@Override
		public void execute() {
			History.newItem(AppConstants.HISTORY_EMPLOYEE_SETTINGS);
		}
	};

	private Command doHolidaySettings = new Command() {
		@Override
		public void execute() {
			History.newItem(AppConstants.HISTORY_EMPLOYEE_SETTINGS);
		}
	};
}
