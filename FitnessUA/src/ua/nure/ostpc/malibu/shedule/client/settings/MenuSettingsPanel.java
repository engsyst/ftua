package ua.nure.ostpc.malibu.shedule.client.settings;

import ua.nure.ostpc.malibu.shedule.parameter.AppConstants;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.Widget;

public class MenuSettingsPanel extends SimplePanel {
	private static ClubSettingsPanel clubSettings;
	private static EmployeeSettingsPanel employeeSettings;
	private static CategorySettingsPanel categorySettings;
	private static HolidaySettingsPanel holidaySettings;
	private static Grid panel = new Grid(2, 1);
	private static TabBar menuBar;
	private static Widget current;

	/**
	 * @wbp.parser.entryPoint
	 */
	public MenuSettingsPanel(int tabIndex) {
		if (menuBar == null) {
			menuBar = new TabBar();

			menuBar.addTab(AppConstants.CLUBS_MENU_ITEM);
			menuBar.addTab(AppConstants.EMPLOYEE_MENU_ITEM);
			menuBar.addTab(AppConstants.CATEGORY_MENU_ITEM);
			menuBar.addTab(AppConstants.HOLIDAY_MENU_ITEM);
			menuBar.addSelectionHandler(selectionhandler);
		}
			panel.setWidget(0, 0, menuBar);
			setWidget(panel);
		menuBar.selectTab(tabIndex);
	}

	protected void doHolidaySettings() {
		if (holidaySettings == null) {
			holidaySettings = new HolidaySettingsPanel();
		}
		panel.setWidget(1, 0, current = holidaySettings);
	}

	protected void doCategorySettings() {
		if (categorySettings == null) {
			categorySettings = new CategorySettingsPanel();
		}
		panel.setWidget(1, 0, current = categorySettings);
	}

	protected void doEmployeeSettings() {
		if (employeeSettings == null) {
			employeeSettings = new EmployeeSettingsPanel();
		}
		panel.setWidget(1, 0, current = employeeSettings);
	}

	protected void doClubSettings() {
		if (clubSettings == null) {
			clubSettings = new ClubSettingsPanel();
		}
		panel.setWidget(1, 0, current = clubSettings);
	}

	private SelectionHandler<Integer> selectionhandler = new SelectionHandler<Integer>() {

		@Override
		public void onSelection(SelectionEvent<Integer> event) {
			History.newItem(
					AppConstants.HISTORY_SETTINGS + "-"
							+ event.getSelectedItem(), false);
			if (current != null && current.isAttached()) {
				current.removeFromParent();
			}

			switch (event.getSelectedItem()) {
			case 1: {
				doEmployeeSettings();
				break;
			}
			case 2: {
				doCategorySettings();
				break;
			}
			case 3: {
				doHolidaySettings();
				break;
			}
			default: {
				doClubSettings();
				break;
			}
			}
		}
	};

}
